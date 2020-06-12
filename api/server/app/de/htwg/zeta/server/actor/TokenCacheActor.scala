package de.htwg.zeta.server.actor

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import TransientTokenCacheActor.Create
import TransientTokenCacheActor.Delete
import TransientTokenCacheActor.Read
import de.htwg.zeta.server.model.TokenCache

@Singleton
class TokenCacheActor @Inject() (system: ActorSystem, implicit val timeout: Timeout) extends TokenCache{

  private val router: ActorRef = system.actorOf(TransientTokenCacheActor.props(), "TransientTokenCache")

  override def create(id: UUID): Future[UUID] = {
    (router ? Create(id)).flatMap {
      case Success(uid: UUID) => Future.successful(uid)
      case Failure(e) => Future.failed(e)
    }
  }

  override def read(id: UUID): Future[UUID] = {
    (router ? Read(id)).flatMap {
      case Success(uid: UUID) => Future.successful(uid)
      case Failure(e) => Future.failed(e)
    }
  }

 override def delete(id: UUID): Future[Unit] = {
    (router ? Delete(id)).flatMap {
      case Success(()) => Future.successful(())
      case Failure(e) => Future.failed(e)
    }
  }
}
