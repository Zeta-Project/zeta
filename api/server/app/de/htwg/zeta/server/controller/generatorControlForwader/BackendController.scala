package de.htwg.zeta.server.controller.generatorControlForwader

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.cluster.sharding.ClusterSharding
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.frontend.DeveloperRequest
import de.htwg.zeta.common.models.frontend.DeveloperResponse
import de.htwg.zeta.common.models.frontend.GeneratorRequest
import de.htwg.zeta.common.models.frontend.GeneratorResponse
import de.htwg.zeta.common.models.frontend.UserRequest
import de.htwg.zeta.common.models.frontend.UserResponse
import de.htwg.zeta.generatorControl.actors.developer.Mediator
import de.htwg.zeta.generatorControl.actors.frontend.DeveloperFrontend
import de.htwg.zeta.generatorControl.actors.frontend.GeneratorFrontend
import de.htwg.zeta.generatorControl.actors.frontend.UserFrontend
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.WebSocket.MessageFlowTransformer


/**
 * BackendController.
 *
 * @param system            ActorSystem
 * @param mat               Materializer
 * @param silhouette        Silhouette
 */
class BackendController @Inject()(
    implicit system: ActorSystem,
    mat: Materializer,
    silhouette: Silhouette[ZetaEnv])
  extends Controller {

  private val developerMsg: MessageFlowTransformer[DeveloperRequest, DeveloperResponse] =
    MessageFlowTransformer.jsonMessageFlowTransformer[DeveloperRequest, DeveloperResponse]

  private val userMsg: MessageFlowTransformer[UserRequest, UserResponse] =
    MessageFlowTransformer.jsonMessageFlowTransformer[UserRequest, UserResponse]

  private val generatorMsg: MessageFlowTransformer[GeneratorRequest, GeneratorResponse] =
    MessageFlowTransformer.jsonMessageFlowTransformer[GeneratorRequest, GeneratorResponse]


  ClusterSharding(system).startProxy(
    typeName = Mediator.shardRegionName,
    role = Some(Mediator.locatedOnNode),
    extractEntityId = Mediator.extractEntityId,
    extractShardId = Mediator.extractShardId
  )

  private val backend: ActorRef = ClusterSharding(system).shardRegion(Mediator.shardRegionName)

  /**
   * Connect as a developer
   *
   * @return WebSocket
   */
  def developer()(out: ActorRef, request: SecuredRequest[ZetaEnv, AnyContent]): (Props, MessageFlowTransformer[DeveloperRequest, DeveloperResponse]) = {
    (DeveloperFrontend.props(out, backend, request.identity.id), developerMsg)
  }


  /** Connect from a model editor.
   *
   * @param modelId The modelId
   * @param request The request
   * @return (Future[(ActorRef) => Props], MessageFlowTransformer[UserRequest, UserResponse])
   */
  def user(modelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): (Future[(ActorRef) => Props], MessageFlowTransformer[UserRequest, UserResponse]) = {
    val futureProps = restrictedAccessRepository(request.identity.id).modelEntity.read(modelId).map(_ => userProps(request.identity.id, modelId) _)
    system.dispatcher
    (futureProps, userMsg)
  }

  private def userProps(userId: UUID, modelId: UUID)(out: ActorRef): Props = {
    UserFrontend.props(out, backend, userId, modelId)
  }

  /**
   * Connect from a generator
   *
   * @param id The id of the work object where the generator is executed in
   * @return WebSocket
   */
  def generator(id: UUID)
    (out: ActorRef, request: SecuredRequest[ZetaEnv, AnyContent]): (Props, MessageFlowTransformer[GeneratorRequest, GeneratorResponse]) = {
    // Extract the user from the request and connect to the endpoint of that user
    (GeneratorFrontend.props(out, backend, request.identity.id, id), generatorMsg)
  }
}
