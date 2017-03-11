package controllers

import akka.actor.{ ActorRef, ActorSystem }
import javax.inject._

import actors.developer.Mediator
import actors.frontend.{ DeveloperFrontend, GeneratorFrontend, UserFrontend }
import akka.cluster.sharding.ClusterSharding
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{ HandlerResult, Silhouette }
import models.User
import models.document.{ ModelEntity, Repository }
import models.frontend._
import models.session.Session
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import utils.auth.{ DefaultEnv, RepositoryFactory }

import scala.concurrent.{ Future, Promise }

class BackendController @Inject() (implicit system: ActorSystem, mat: Materializer, repositoryFactory: RepositoryFactory, silhouette: Silhouette[DefaultEnv], session: Session) extends Controller {
  implicit val developerMsg = MessageFlowTransformer.jsonMessageFlowTransformer[DeveloperRequest, DeveloperResponse]
  implicit val userMsg = MessageFlowTransformer.jsonMessageFlowTransformer[UserRequest, UserResponse]
  implicit val generatorMsg = MessageFlowTransformer.jsonMessageFlowTransformer[GeneratorRequest, GeneratorResponse]

  def repository[A]()(implicit request: SecuredRequest[DefaultEnv, A]): Repository =
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