package controllers

import javax.inject.Inject

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.cluster.sharding.ClusterSharding
import akka.stream.Materializer

import actors.developer.Mediator
import actors.frontend.DeveloperFrontend
import actors.frontend.GeneratorFrontend
import actors.frontend.UserFrontend

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette

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

import play.api.libs.streams.ActorFlow
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.WebSocket
import play.api.mvc.WebSocket.MessageFlowTransformer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import de.htwg.zeta.server.utils.auth.ZetaEnv
import de.htwg.zeta.server.utils.auth.RepositoryFactory

class BackendController @Inject() (
    implicit system: ActorSystem,
    mat: Materializer,
    repositoryFactory: RepositoryFactory,
    silhouette: Silhouette[ZetaEnv],
    session: Session)
  extends Controller {

  implicit val developerMsg = MessageFlowTransformer.jsonMessageFlowTransformer[DeveloperRequest, DeveloperResponse]
  implicit val userMsg = MessageFlowTransformer.jsonMessageFlowTransformer[UserRequest, UserResponse]
  implicit val generatorMsg = MessageFlowTransformer.jsonMessageFlowTransformer[GeneratorRequest, GeneratorResponse]

  def repository[A]()(implicit request: SecuredRequest[ZetaEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  ClusterSharding(system).startProxy(
    typeName = Mediator.shardRegionName,
    role = Some(Mediator.locatedOnNode),
    extractEntityId = Mediator.extractEntityId,
    extractShardId = Mediator.extractShardId
  )

  val backend: ActorRef = ClusterSharding(system).shardRegion(Mediator.shardRegionName)

  /**
   * Connect as a developer
   */
  def developer = WebSocket.acceptOrResult[DeveloperRequest, DeveloperResponse] { request =>
    implicit val req = Request(request, AnyContentAsEmpty)
    silhouette.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(r, Some(user)) => Right(ActorFlow.actorRef(out => DeveloperFrontend.props(out, backend, User.getUserId(user))))
      case HandlerResult(r, None) => Left(r)
    }
  }

  /**
   * Connect from a model editor
   *
   * @param model The id of the model editor
   */
  def user(model: String) = WebSocket.acceptOrResult[UserRequest, UserResponse] { request =>
    implicit val req = Request(request, AnyContentAsEmpty)
    silhouette.SecuredRequestHandler { implicit securedRequest =>
      val p = Promise[HandlerResult[User]]
      // Access to the model?
      repository.get[ModelEntity](model).map { entity =>
        p.success(HandlerResult(Ok, Some(securedRequest.identity)))
      }.recover {
        case e: Exception => p.success(HandlerResult(Forbidden("Unknown model"), None))
      }
      p.future
    }.map {
      case HandlerResult(r, Some(user)) => Right(ActorFlow.actorRef(out => UserFrontend.props(out, backend, User.getUserId(user), model)))
      case HandlerResult(r, None) => Left(r)
    }
  }

  /**
   * Connect from a generator
   *
   * @param id The id of the work object where the generator is executed in
   */
  def generator(id: String) = WebSocket.acceptOrResult[GeneratorRequest, GeneratorResponse] { request =>
    // Extract the user from the request and connect to the endpoint of that user
    session.getUser(request).map { user =>
      Right(ActorFlow.actorRef(out => GeneratorFrontend.props(out, backend, user, id)))
    }.recover {
      case e: Exception => Left(Forbidden(e.getMessage))
    }
  }
}
