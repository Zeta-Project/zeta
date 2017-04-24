package controllers

import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import generator.generators.diagram.DiagramGenerator
import generator.generators.shape.ShapeGenerator
import generator.generators.style.StyleGenerator
import generator.generators.vr.diagram.VrDiagramGenerator
import generator.generators.vr.shape.VrShapeGenerator
import generator.model.diagram.Diagram
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.SprayParser
import models.document.MetaModelEntity
import models.document.Repository
import models.file.File
import models.modelDefinitions.metaModel.Dsl
import models.modelDefinitions.metaModel.Shape
import models.modelDefinitions.metaModel.{Diagram => DslDiagram}
import models.modelDefinitions.metaModel.{Style => DslStyle}
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import utils.auth.DefaultEnv
import utils.auth.RepositoryFactory

import scala.annotation.tailrec

//import scala.concurrent.ExecutionContext.Implicits.global

class GeneratorController @Inject()(implicit repositoryFactory: RepositoryFactory, silhouette: Silhouette[DefaultEnv]) extends Controller {

  private def repository[A](request: SecuredRequest[DefaultEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  def generate(metaModelUuid: String): Action[AnyContent] = silhouette.SecuredAction.async(req => {

    repository(req).get[MetaModelEntity](metaModelUuid)
      .map(createGenerators(_) match {
             case None => Ok("Generation successful")
             case Some(error) => BadRequest(error)
           }).recover {
                        case _: Exception => NotFound("Metamodel with id: " + metaModelUuid + " was not found")
                      }
  }

  )

  private type ErrorMessage = String

  def createGenerators(metaModel: MetaModelEntity): Option[ErrorMessage] = {
    val metaModelUuid = metaModel._id
    val generatorOutputLocation: String = System.getenv("PWD") + "/server/model_specific/" + metaModelUuid + "/"
    val vrGeneratorOutputLocation = System.getenv("PWD") + "/server/model_specific/vr/" + metaModelUuid + "/"

    Files.createDirectories(Paths.get(generatorOutputLocation))
    Files.createDirectories(Paths.get(vrGeneratorOutputLocation))

    val hierarchyContainer = Cache()
    val parser = new SprayParser(hierarchyContainer, metaModel)

    def tryParse[E, R](get: Dsl => Option[E], parse: E => List[R], name: String): Either[List[R], ErrorMessage] = {
      get(metaModel.dsl) match {
        case None => Right(s"$name not available")
        case Some(e) =>
          try {
            Left(parse(e))
          } catch {
            case _: Throwable => Right(s"$name failed parsing")
          }
      }
    }

    tryParse[DslStyle, Style](_.style, (s: DslStyle) => parser.parseStyle(s.code), "Style") match {
      case Left(_) =>
      case Right(err) => return Some(err)
    }

    tryParse[Shape, AnyRef](_.shape, s => parser.parseShape(s.code), "Shape") match {
      case Left(_) =>
      case Right(err) => return Some(err)
    }

    val diagramName = "Diagram"
    val diagram = tryParse[DslDiagram, Option[Diagram]](_.diagram, s => parser.parseDiagram(s.code), diagramName) match {
      case Right(err) => return Some(err)
      case Left(Some(dia) :: _) => dia
      case Left(_) => return Some(s"No $diagramName available")
    }

    def tryRecoverSingle(block: () => File, onFailure: String): () => Either[List[File], ErrorMessage] =
      tryRecover(() => List(block()), onFailure)


    def tryRecover(block: () => List[File], onFailure: String): () => Either[List[File], ErrorMessage] = {
      def execute() = {
        try {
          Left(block())
        } catch {
          case _: Throwable => Right(s"failed on: $onFailure")
        }
      }

      () => execute()
    }

    val styles = hierarchyContainer.styleHierarchy.nodeView.values.map(_.data).toList

    val generators = List(
      tryRecoverSingle(() => StyleGenerator.doGenerateFile(styles, generatorOutputLocation), "StyleGenerator"),
      tryRecover(() => ShapeGenerator.doGenerateFile(hierarchyContainer, generatorOutputLocation, diagram.nodes), "ShapeGenerator"),
      tryRecover(() => DiagramGenerator.doGenerateFile(diagram, generatorOutputLocation), "DiagramGenerator"),

      // Generate files for the VR - Editor
      tryRecover(() => VrShapeGenerator.doGenerateFile(hierarchyContainer, vrGeneratorOutputLocation, diagram.nodes), "VrShapeGenerator"),
      tryRecover(() => VrDiagramGenerator.doGenerateFiles(diagram, vrGeneratorOutputLocation), "VrDiagramGenerator")
    )

    @tailrec
    def generate(generators: List[() => Either[List[File], ErrorMessage]], carry: List[File] = Nil): Either[List[File], ErrorMessage] =
      generators match {
        case Nil => Left(carry)
        case head :: tail => head() match {
          case r: Right[_, _] => r
          case Left(list) => generate(tail, carry ::: list)
        }
      }


    val res = generate(generators)
    res match {
      case Left(files) =>
        files.foreach(f => Files.write(Paths.get(f.name), f.content.getBytes))
        None
      case Right(error) =>
        Some(error)
    }


  }


}


