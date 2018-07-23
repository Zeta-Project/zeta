package de.htwg.zeta.server.controller.generatorControlForwader

import java.util.UUID

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.frontend.DeveloperRequest
import de.htwg.zeta.common.models.frontend.DeveloperResponse
import de.htwg.zeta.common.models.frontend.GeneratorRequest
import de.htwg.zeta.common.models.frontend.GeneratorResponse
import de.htwg.zeta.common.models.frontend.UserRequest
import de.htwg.zeta.common.models.frontend.UserResponse
import de.htwg.zeta.generatorControl.actors.frontend.DeveloperFrontend
import de.htwg.zeta.generatorControl.actors.frontend.GeneratorFrontend
import de.htwg.zeta.generatorControl.actors.frontend.UserFrontend
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.WebSocket.MessageFlowTransformer


/**
 * BackendController.
 *
 * @param system              ActorSystem
 * @param mat                 Materializer
 * @param backendRemoteClient the remote client for the backend
 * @param silhouette          Silhouette
 */
class GeneratorControlController @Inject()(
    system: ActorSystem,
    mat: Materializer,
    backendRemoteClient: GeneratorControlRemoteClient,
    silhouette: Silhouette[ZetaEnv],
    modelEntityRepo: GraphicalDslInstanceRepository
) extends Controller with Logging {

  private val developerMsg: MessageFlowTransformer[DeveloperRequest, DeveloperResponse] =
    MessageFlowTransformer.jsonMessageFlowTransformer[DeveloperRequest, DeveloperResponse]

  private val userMsg: MessageFlowTransformer[UserRequest, UserResponse] =
    MessageFlowTransformer.jsonMessageFlowTransformer[UserRequest, UserResponse]

  private val generatorMsg: MessageFlowTransformer[GeneratorRequest, GeneratorResponse] =
    MessageFlowTransformer.jsonMessageFlowTransformer[GeneratorRequest, GeneratorResponse]

  /**
   * Connect as a developer
   *
   * @return WebSocket
   */
  def developer()(out: ActorRef, request: SecuredRequest[ZetaEnv, AnyContent]): (Props, MessageFlowTransformer[DeveloperRequest, DeveloperResponse]) = {
    val register = GeneratorControlRegisterFactory((ident, ref) => DeveloperFrontend.CreateDeveloperFrontend(ident, ref, request.identity.id))
    (GeneratorControlForwarder.props(backendRemoteClient.developerFrontendService, out, register), developerMsg)
  }


  /** Connect from a model editor.
   *
   * @param modelId The modelId
   * @param request The request
   * @return (Future[(ActorRef) => Props], MessageFlowTransformer[UserRequest, UserResponse])
   */
  def user(modelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): (Future[(ActorRef) => Props], MessageFlowTransformer[UserRequest, UserResponse]) = {
    val futureProps = modelEntityRepo.read(modelId).map(_ => {
      val register = GeneratorControlRegisterFactory((ident, ref) => UserFrontend.CreateUserFrontend(ident, ref, request.identity.id, modelId))
      (out: ActorRef) => GeneratorControlForwarder.props(backendRemoteClient.userFrontendService, out, register)
    })
    (futureProps, userMsg)
  }


  /**
   * Connect from a generator
   *
   * @param workId The id of the work object where the generator is executed in
   * @return WebSocket
   */
  def generator(workId: UUID)
    (out: ActorRef, request: SecuredRequest[ZetaEnv, AnyContent]): (Props, MessageFlowTransformer[GeneratorRequest, GeneratorResponse]) = {

    // Extract the user from the request and connect to the endpoint of that user
    val register = GeneratorControlRegisterFactory((ident, ref) => GeneratorFrontend.CreateGeneratorFrontend(ident, ref, request.identity.id, workId))

    (GeneratorControlForwarder.props(backendRemoteClient.generatorFrontendService, out, register), generatorMsg)
  }
}


