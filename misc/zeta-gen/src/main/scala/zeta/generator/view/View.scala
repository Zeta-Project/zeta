package zeta.generator.view

import zeta.generator.controller._
import zeta.generator.domain.model.ModelShortInfo

import scala.swing.Reactor

trait View extends Reactor

object View {
  def apply(controller: Controller) = new GeneratorView(controller)
}

class GeneratorView(controller: Controller) extends View {

  listenTo(controller)

  reactions += {
    case ShortInfoAvailable(info) => printShortInfo(info)
    case GeneratorLoadFailure => println("Error: Transformation or Model not found")
    case GeneratorSuccess => println("SUCCESS!")
    case GeneratorError(e) => println(s"the following errors occured $e")
  }

  def handleArgs(args : Array[String]) {
    val a = Array("generate", "-t", "zeta.generator.GenericReport", "-m", "504603de-7125-496a-a734-7d865be85bcc")
    parser.parse(a, Command()) match {
      case Some(cmd) => cmd match {
        case Command("generate", transformation, model) => controller.generate(transformation, model)
        case Command("transformations", _, _) => printTransformations(controller.getTransformations)
        case Command("models", _, _) => controller.getModels
        case _ => // Will never happen
      }
      case None => println("Exit.")
    }
    scala.io.StdIn.readLine()
  }

  def printTransformations(t: Seq[String]) {
    println("Found transformations:")
    t.foreach(println)
  }

  def printShortInfo(info: Seq[ModelShortInfo]) = {
    println("Models that are available on the server:")
    info.foreach(i => println(s"${i.name} (${i.id})"))
  }

  val parser = new scopt.OptionParser[Command]("zeta-gen") {
    head("zeta-gen", "0.1")
    help("help") text("prints this usage text")
    cmd("generate") action { (_, c) =>
      c.copy(mode = "generate")
    } text ("Starts generation using the provided transformation and model.") children(
      opt[String]("transformation") required() abbr ("t") action { (s, c) =>
        c.copy(transformation = s)
      } validate { s =>
        if (s.length > 0) success else failure("Name of transformation must not be empty")
      } text ("specifies name of transformation"),
      opt[String]("model") required() abbr ("m") action { (m, c) =>
        c.copy(model = m)
      } validate { s =>
        if (s.length > 0) success else failure("Name of model must not be empty")
      } text ("specifies name/id of model")
      )
    cmd("transformations") action { (_, c) =>
      c.copy(mode = "transformations")
    } text ("Lists all transformations that are available.")
    cmd("models") action { (_, c) =>
      c.copy(mode = "models")
    } text ("Lists all models that are found on the server.")
  }
}

case class Command(mode: String = "", transformation: String = "", model: String = "")


