package de.htwg.zeta.server.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Silhouette
import grizzled.slf4j.Logging
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.mvc.{Results => HTMLResults}
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Action
import play.api.mvc.ActionBuilder
import play.api.mvc.Result
import utils.auth.ZetaEnv


/**
 * Ordering or apply method parameters: Request, followed by everything else in alphabetical order
 *
 */
private[authentication] abstract class AbstractAction[REQ <: Request[AnyContent]](
    messagesApi: MessagesApi,
    silhouette: Silhouette[ZetaEnv]
) extends HTMLResults with Logging {


  protected[authentication] def executeChecked[R](block: () => R): R = {
    try {
      block()
    } catch {
      case t: Throwable =>
        error("Exception in action: " + t)
        throw t
    }
  }

  protected[authentication] def handleFutureRequest(
    block: (REQ) => Future[Result],
    ec: ExecutionContext)
    (request: Request[AnyContent]): Future[Result]

  protected[authentication] def doAction(block: (REQ) => Result): Action[AnyContent] =
    doActionFuture((r) => Future.successful(block(r)))


  protected[authentication] def doActionFuture(block: (REQ) => Future[Result]): Action[AnyContent] =
    Async.async(handleFutureRequest(block, Async.executionContext) _)


  /*
  ordering:
  REQ
  EXC
  MSG
   */

  /**
   * All of those classes have to be implicit.
   *
   */
  // scalastyle:off

  implicit class ResultFunction(val fnc: () => Result)

  def apply(block: ResultFunction): Action[AnyContent] = doAction((_) => block.fnc())

  implicit class REQ_Function(val fnc: (REQ) => Result)

  def apply(block: REQ_Function): Action[AnyContent] = doAction((r) => block.fnc(r))

  implicit class EXC_Function(val fnc: (EXC) => Result)

  def apply(block: EXC_Function): Action[AnyContent] = doAction((_) => block.fnc(EXC()))

  implicit class MSG_Function(val fnc: (MSG) => Result)

  def apply(block: MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(MSG(r)))

  implicit class REQ_EXC_Function(val fnc: (REQ, EXC) => Result)

  def apply(block: REQ_EXC_Function): Action[AnyContent] = doAction((r) => block.fnc(r, EXC()))

  implicit class REQ_MSG_Function(val fnc: (REQ, MSG) => Result)

  def apply(block: REQ_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(r, MSG(r)))

  implicit class EXC_MSG_Function(val fnc: (EXC, MSG) => Result)

  def apply(block: EXC_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(EXC(), MSG(r)))

  implicit class REQ_EXC_MSG_Function(val fnc: (REQ, EXC, MSG) => Result)

  def apply(block: REQ_EXC_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(r, EXC(), MSG(r)))


  // future

  implicit class FutureResultFunction(val fnc: () => Future[Result])

  def apply(block: FutureResultFunction): Action[AnyContent] = doActionFuture((_) => block.fnc())

  implicit class Future_REQ_Function(val fnc: (REQ) => Future[Result])

  def apply(block: Future_REQ_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r))

  implicit class Future_EXC_Function(val fnc: (EXC) => Future[Result])

  def apply(block: Future_EXC_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC()))

  implicit class Future_MSG_Function(val fnc: (MSG) => Future[Result])

  def apply(block: Future_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(MSG(r)))

  implicit class Future_REQ_EXC_Function(val fnc: (REQ, EXC) => Future[Result])

  def apply(block: Future_REQ_EXC_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, EXC()))

  implicit class Future_REQ_MSG_Function(val fnc: (REQ, MSG) => Future[Result])

  def apply(block: Future_REQ_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, MSG(r)))

  implicit class Future_EXC_MSG_Function(val fnc: (EXC, MSG) => Future[Result])

  def apply(block: Future_EXC_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC(), MSG(r)))

  implicit class Future_REQ_EXC_MSG_Function(val fnc: (REQ, EXC, MSG) => Future[Result])

  def apply(block: Future_REQ_EXC_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, EXC(), MSG(r)))

  // scalastyle:on

  protected object Async extends ActionBuilder[Request] {
    override def executionContext: ExecutionContext = super.executionContext

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = block(request)
  }

  private type EXC = ExecutionContext

  private object EXC {
    def apply(): EXC = Async.executionContext
  }

  private type MSG = Messages

  private object MSG {
    def apply(r: Request[AnyContent]): MSG = messagesApi.preferred(r)
  }

}


