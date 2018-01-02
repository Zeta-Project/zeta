package de.htwg.zeta.server.routing.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.mvc.{Results => HTMLResults}
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Action
import play.api.mvc.ActionBuilder
import play.api.mvc.Result
import play.api.mvc.BodyParser
import play.api.mvc.BodyParsers


/**
 * Ordering or apply method parameters: Request, followed by everything else in alphabetical order
 *
 */
// scalastyle:off number.of.methods
private[authentication] trait AbstractAction[REQ[_]] extends HTMLResults with Logging {

  private[authentication] val dependencies: AbstractAction.Dependencies
  private[authentication] val executionContext: ExecutionContext = dependencies.executionContext
  private[authentication] val messagesApi: MessagesApi = dependencies.messagesApi

  protected[authentication] def executeChecked[R](block: () => R): R = {
    try {
      block()
    } catch {
      case t: Throwable =>
        error("Exception in action: " + t)
        throw t
    }
  }

  protected[authentication] def handleFutureRequest[C](
    block: (REQ[C]) => Future[Result],
    ec: ExecutionContext)
    (request: Request[C]): Future[Result]


  protected[authentication] def reqToRequest[C](r: REQ[C]): Request[C]

  protected[authentication] def doAction(block: (REQ[AnyContent]) => Result): Action[AnyContent] = {
    doActionFuture((r) => Future.successful(block(r)))
  }

  protected[authentication] def doActionFuture(block: (REQ[AnyContent]) => Future[Result]): Action[AnyContent] = {
    Async.async(handleFutureRequest[AnyContent](block, Async.executionContext) _)
  }


  protected[authentication] def doAction[A](bodyParser: BodyParser[A])(block: (REQ[A]) => Result): Action[A] = {
    doActionFuture[A](bodyParser)((r: REQ[A]) => Future.successful(block(r)))
  }

  protected[authentication] def doActionFuture[A](bodyParser: BodyParser[A])(block: (REQ[A]) => Future[Result]): Action[A] = {
    Async.async(bodyParser)(handleFutureRequest[A](block, executionContext))
  }


  protected object Async extends ActionBuilder[Request, AnyContent] {
    override def executionContext: ExecutionContext =  AbstractAction.this.executionContext

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = block(request)

    override def parser: BodyParser[AnyContent] = dependencies.bodyParser
  }


  private object REQ {
    def apply[C](r: REQ[C]): REQ[C] = r
  }

  private type EXC[_] = ExecutionContext

  private object EXC {
    def apply(r: REQ[_]): EXC[_] = Async.executionContext
  }

  private type MSG[_] = Messages

  private object MSG {
    def apply[C](r: REQ[C]): MSG[_] = {
      messagesApi.preferred(reqToRequest(r))
    }
  }


  // scalastyle:off

  // dummy methods. Used to compile the application during creation of apply methods
  //  def apply(any: Any): Action[AnyContent] = null
  //
  //  def apply[C](bodyParser: BodyParser[C])(any: Any): Action[C] = null

  implicit class ResultFunction[C](val fnc: () => Result)

  implicit class Future_ResultFunction[C](val fnc: () => Future[Result])

  def apply(block: ResultFunction[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc())

  def apply(block: Future_ResultFunction[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc())

  def apply[C](bodyParser: BodyParser[C], block: ResultFunction[C]): Action[C] = doAction(bodyParser)((r) => block.fnc())

  def apply[C](bodyParser: BodyParser[C], block: Future_ResultFunction[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc())



  implicit class REQ_Function[C](val fnc: (REQ[C]) => Result)

  implicit class Future_REQ_Function[C](val fnc: (REQ[C]) => Future[Result])

  def apply(block: REQ_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(REQ(r)))

  def apply(block: Future_REQ_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: REQ_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_REQ_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(REQ(r)))



  implicit class EXC_REQ_Function[C](val fnc: (EXC[C], REQ[C]) => Result)

  implicit class Future_EXC_REQ_Function[C](val fnc: (EXC[C], REQ[C]) => Future[Result])

  def apply(block: EXC_REQ_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(EXC(r), REQ(r)))

  def apply(block: Future_EXC_REQ_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC(r), REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: EXC_REQ_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(EXC(r), REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_EXC_REQ_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(EXC(r), REQ(r)))



  implicit class MSG_EXC_REQ_Function[C](val fnc: (MSG[C], EXC[C], REQ[C]) => Result)

  implicit class Future_MSG_EXC_REQ_Function[C](val fnc: (MSG[C], EXC[C], REQ[C]) => Future[Result])

  def apply(block: MSG_EXC_REQ_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(MSG(r), EXC(r), REQ(r)))

  def apply(block: Future_MSG_EXC_REQ_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(MSG(r), EXC(r), REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: MSG_EXC_REQ_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(MSG(r), EXC(r), REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_MSG_EXC_REQ_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(MSG(r), EXC(r), REQ(r)))



  implicit class MSG_REQ_Function[C](val fnc: (MSG[C], REQ[C]) => Result)

  implicit class Future_MSG_REQ_Function[C](val fnc: (MSG[C], REQ[C]) => Future[Result])

  def apply(block: MSG_REQ_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(MSG(r), REQ(r)))

  def apply(block: Future_MSG_REQ_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(MSG(r), REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: MSG_REQ_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(MSG(r), REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_MSG_REQ_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(MSG(r), REQ(r)))



  implicit class EXC_MSG_REQ_Function[C](val fnc: (EXC[C], MSG[C], REQ[C]) => Result)

  implicit class Future_EXC_MSG_REQ_Function[C](val fnc: (EXC[C], MSG[C], REQ[C]) => Future[Result])

  def apply(block: EXC_MSG_REQ_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(EXC(r), MSG(r), REQ(r)))

  def apply(block: Future_EXC_MSG_REQ_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC(r), MSG(r), REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: EXC_MSG_REQ_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(EXC(r), MSG(r), REQ(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_EXC_MSG_REQ_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(EXC(r), MSG(r), REQ(r)))



  implicit class EXC_Function[C](val fnc: (EXC[C]) => Result)

  implicit class Future_EXC_Function[C](val fnc: (EXC[C]) => Future[Result])

  def apply(block: EXC_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(EXC(r)))

  def apply(block: Future_EXC_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: EXC_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_EXC_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(EXC(r)))



  implicit class REQ_EXC_Function[C](val fnc: (REQ[C], EXC[C]) => Result)

  implicit class Future_REQ_EXC_Function[C](val fnc: (REQ[C], EXC[C]) => Future[Result])

  def apply(block: REQ_EXC_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(REQ(r), EXC(r)))

  def apply(block: Future_REQ_EXC_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(REQ(r), EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: REQ_EXC_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(REQ(r), EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_REQ_EXC_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(REQ(r), EXC(r)))



  implicit class MSG_REQ_EXC_Function[C](val fnc: (MSG[C], REQ[C], EXC[C]) => Result)

  implicit class Future_MSG_REQ_EXC_Function[C](val fnc: (MSG[C], REQ[C], EXC[C]) => Future[Result])

  def apply(block: MSG_REQ_EXC_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(MSG(r), REQ(r), EXC(r)))

  def apply(block: Future_MSG_REQ_EXC_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(MSG(r), REQ(r), EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: MSG_REQ_EXC_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(MSG(r), REQ(r), EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_MSG_REQ_EXC_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(MSG(r), REQ(r), EXC(r)))



  implicit class MSG_EXC_Function[C](val fnc: (MSG[C], EXC[C]) => Result)

  implicit class Future_MSG_EXC_Function[C](val fnc: (MSG[C], EXC[C]) => Future[Result])

  def apply(block: MSG_EXC_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(MSG(r), EXC(r)))

  def apply(block: Future_MSG_EXC_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(MSG(r), EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: MSG_EXC_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(MSG(r), EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_MSG_EXC_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(MSG(r), EXC(r)))



  implicit class REQ_MSG_EXC_Function[C](val fnc: (REQ[C], MSG[C], EXC[C]) => Result)

  implicit class Future_REQ_MSG_EXC_Function[C](val fnc: (REQ[C], MSG[C], EXC[C]) => Future[Result])

  def apply(block: REQ_MSG_EXC_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(REQ(r), MSG(r), EXC(r)))

  def apply(block: Future_REQ_MSG_EXC_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(REQ(r), MSG(r), EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: REQ_MSG_EXC_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(REQ(r), MSG(r), EXC(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_REQ_MSG_EXC_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(REQ(r), MSG(r), EXC(r)))



  implicit class MSG_Function[C](val fnc: (MSG[C]) => Result)

  implicit class Future_MSG_Function[C](val fnc: (MSG[C]) => Future[Result])

  def apply(block: MSG_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(MSG(r)))

  def apply(block: Future_MSG_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: MSG_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_MSG_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(MSG(r)))



  implicit class REQ_MSG_Function[C](val fnc: (REQ[C], MSG[C]) => Result)

  implicit class Future_REQ_MSG_Function[C](val fnc: (REQ[C], MSG[C]) => Future[Result])

  def apply(block: REQ_MSG_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(REQ(r), MSG(r)))

  def apply(block: Future_REQ_MSG_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(REQ(r), MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: REQ_MSG_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(REQ(r), MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_REQ_MSG_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(REQ(r), MSG(r)))



  implicit class EXC_REQ_MSG_Function[C](val fnc: (EXC[C], REQ[C], MSG[C]) => Result)

  implicit class Future_EXC_REQ_MSG_Function[C](val fnc: (EXC[C], REQ[C], MSG[C]) => Future[Result])

  def apply(block: EXC_REQ_MSG_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(EXC(r), REQ(r), MSG(r)))

  def apply(block: Future_EXC_REQ_MSG_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC(r), REQ(r), MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: EXC_REQ_MSG_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(EXC(r), REQ(r), MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_EXC_REQ_MSG_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(EXC(r), REQ(r), MSG(r)))



  implicit class EXC_MSG_Function[C](val fnc: (EXC[C], MSG[C]) => Result)

  implicit class Future_EXC_MSG_Function[C](val fnc: (EXC[C], MSG[C]) => Future[Result])

  def apply(block: EXC_MSG_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(EXC(r), MSG(r)))

  def apply(block: Future_EXC_MSG_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC(r), MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: EXC_MSG_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(EXC(r), MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_EXC_MSG_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(EXC(r), MSG(r)))



  implicit class REQ_EXC_MSG_Function[C](val fnc: (REQ[C], EXC[C], MSG[C]) => Result)

  implicit class Future_REQ_EXC_MSG_Function[C](val fnc: (REQ[C], EXC[C], MSG[C]) => Future[Result])

  def apply(block: REQ_EXC_MSG_Function[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc(REQ(r), EXC(r), MSG(r)))

  def apply(block: Future_REQ_EXC_MSG_Function[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc(REQ(r), EXC(r), MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: REQ_EXC_MSG_Function[C]): Action[C] = doAction(bodyParser)((r) => block.fnc(REQ(r), EXC(r), MSG(r)))

  def apply[C](bodyParser: BodyParser[C], block: Future_REQ_EXC_MSG_Function[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc(REQ(r), EXC(r), MSG(r)))

  // scalastyle:on
}

object AbstractAction{

  private[routing] class Dependencies @Inject()(
      val messagesApi: MessagesApi,
      val executionContext: ExecutionContext,
      val silhouette: Silhouette[ZetaEnv],
      val bodyParser: BodyParsers.Default
  )
}

/**
 * This will create multiple methods methods with parameters that only differ in generic types,
 * because Scala functions are just syntax sugar for generic Functions.
 * This means Scala cannot overload generic methods relying on generic functions.
 * To fix this there has to be an implicit class wrapping every function.
 *
 */
object GenerateApplyMethods extends App {


  def genFunction(args: List[String]): List[String] = {
    val name = {
      val ret = args.mkString("_")
      if (ret.isEmpty) {
        "ResultFunction"
      } else {
        ret + "_Function"
      }
    }

    val calls = args.map(_ + "(r)").mkString(", ")
    val argString = args.map(_ + "[C]").mkString(", ")

    val ret = List(
      s"implicit class $name[C](val fnc: ($argString) => Result)",
      s"implicit class Future_$name[C](val fnc: ($argString) => Future[Result])",
      s"def apply(block: $name[AnyContent]): Action[AnyContent] = doAction((r) => block.fnc($calls))",
      s"def apply(block: Future_$name[AnyContent]): Action[AnyContent] = doActionFuture((r) => block.fnc($calls))",
      s"def apply[C](bodyParser: BodyParser[C], block: $name[C]): Action[C] = doAction(bodyParser)((r) => block.fnc($calls))",
      s"def apply[C](bodyParser: BodyParser[C], block: Future_$name[C]): Action[C] = doActionFuture(bodyParser)((r) => block.fnc($calls))"
    )
    "\n" :: ret.map(_ + "\n")
  }

  def genPossibilities(args: List[String]): List[List[String]] = {
    def rek(pos: List[String], exclude: List[String]): List[List[String]] = {
      exclude :: pos.filterNot(exclude.contains).flatMap(i => rek(pos, i :: exclude))
    }

    rek(args, Nil)
  }
  // this is generating thus i need to print => ignore stylecheck
  genPossibilities(List("REQ", "EXC", "MSG")).flatMap(genFunction).foreach(println) //scalastyle:ignore
}
