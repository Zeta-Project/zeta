package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future

import actors.developer.Mediator
import actors.frontend.DeveloperFrontend
import actors.frontend.GeneratorFrontend
import actors.frontend.UserFrontend
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.sharding.ClusterSharding
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.util.auth.ZetaEnv
import de.htwg.zeta.server.util.auth.RepositoryFactory
import models.User
import models.document.ModelEntity
import models.document.Repository
import models.frontend.DeveloperRequest
import models.frontend.DeveloperResponse
import models.frontend.GeneratorRequest
import models.frontend.GeneratorResponse
import models.frontend.UserRequest
import models.frontend.UserResponse
import models.session.Session
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.WebSocket.MessageFlowTransformer


/**
 * BackendController.
 *
 * @param system            ActorSystem
 * @param mat               Materializer
 * @param repositoryFactory RepositoryFactory
 * @param silhouette        Silhouette
 * @param session           Session
 */
class BackendController @Inject()(
    implicit system: ActorSystem,
    mat: Materializer,
    repositoryFactory: RepositoryFactory,
    silhouette: Silhouette[ZetaEnv],
    session: Session)
  extends Controller {

  private val developerMsg: MessageFlowTransformer[DeveloperRequest, DeveloperResponse] =
    MessageFlowTransformer.jsonMessageFlowTransformer[DeveloperRequest, DeveloperResponse]

  private val userMsg: MessageFlowTransformer[UserRequest, UserResponse] =
    MessageFlowTransformer.jsonMessageFlowTransformer[UserRequest, UserResponse]

  private val generatorMsg: MessageFlowTransformer[GeneratorRequest, GeneratorResponse] =
    MessageFlowTransformer.jsonMessageFlowTransformer[GeneratorRequest, GeneratorResponse]

  /**
   * Repository.
   *
   * @param request Request
   * @tparam A A
   * @return Repository
   */
  private def repository[A](request: SecuredRequest[ZetaEnv, A]): Repository = {
    repositoryFactory.fromSession(request)
  }

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
    (DeveloperFrontend.props(out, backend, User.getUserId(request.identity)), developerMsg)
  }


  /**
   * Connect from a model editor
   *
   * @param model The id of the model editor
   * @return WebSocket
   */
  def user(model: String)(request: SecuredRequest[ZetaEnv, AnyContent]): (Future[(ActorRef) => Props], MessageFlowTransformer[UserRequest, UserResponse]) = {
    val futureProps = repository(request).get[ModelEntity](model).map(_ => userProps(User.getUserId(request.identity), model) _)(system.dispatcher)
    (futureProps, userMsg)
  }

  private def userProps(userId: String, model: String)(out: ActorRef): Props = {
    UserFrontend.props(out, backend, userId, model)
  }

  /**
   * Connect from a generator
   *
   * @param id The id of the work object where the generator is executed in
   * @return WebSocket
   */
  def generator(id: String)
    (out: ActorRef, request: SecuredRequest[ZetaEnv, AnyContent]): (Props, MessageFlowTransformer[GeneratorRequest, GeneratorResponse]) = {
    // Extract the user from the request and connect to the endpoint of that user
    (GeneratorFrontend.props(out, backend, User.getUserId(request.identity), id), generatorMsg)
  }
}
