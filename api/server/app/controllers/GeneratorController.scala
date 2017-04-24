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
import scala.concurrent.ExecutionContext.Implicits

class GeneratorController @Inject()(implicit repositoryFactory: RepositoryFactory, silhouette: Silhouette[DefaultEnv]) extends Controller {

  private def repository[A](request: SecuredRequest[DefaultEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  def generate(metaModelUuid: String): Action[AnyContent] = silhouette.SecuredAction.async(req => {

    repository(req).get[MetaModelEntity](metaModelUuid)
      .map(createGenerators(_) match {
        case Left(_) => Ok("Generation successful")
        case Right(error) => BadRequest(error)
      })(Implicits.global)
      .recover {
        case _: Exception => NotFound("Metamodel with id: " + metaModelUuid + " was not found")
      }(Implicits.global)
  }

  )

  private type ErrorMessage = String

  def createGenerators(metaModel: MetaModelEntity): Either[List[File], ErrorMessage] = {
    val hierarchyContainer = Cache()
    parseMetaModel(metaModel, hierarchyContainer) match {
      case Right(error) => Right(error)
      case Left(dia) => createGeneratorFile(metaModel, dia, hierarchyContainer)
    }
  }


  private def parseMetaModel(metaModel: MetaModelEntity, hierarchyContainer: Cache): Either[Diagram, ErrorMessage] = {
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
      case Right(err) => Right(err)
      case Left(_) =>
        tryParse[Shape, AnyRef](_.shape, s => parser.parseShape(s.code), "Shape") match {
          case Right(err) => Right(err)
          case Left(_) =>
            val diagramName = "Diagram"
            tryParse[DslDiagram, Option[Diagram]](_.diagram, s => parser.parseDiagram(s.code), diagramName) match {
              case Right(err) => Right(err)
              case Left(Some(dia) :: _) => Left(dia)
              case Left(_) => Right(s"No $diagramName available")
            }
        }
    }
  }


  private def tryRecoverSingle(block: () => File, onFailure: String): () => Either[List[File], ErrorMessage] =
    tryRecover(() => List(block()), onFailure)


  private def tryRecover(block: () => List[File], onFailure: String): () => Either[List[File], ErrorMessage] = {
    def execute() = {
      try {
        Left(block())
      } catch {
        case _: Throwable => Right(s"failed on: $onFailure")
      }
    }

    () => execute()
  }

  @tailrec
  private def generate(generators: List[() => Either[List[File], ErrorMessage]], carry: List[File] = Nil): Either[List[File], ErrorMessage] =
    generators match {
      case Nil => Left(carry)
      case head :: tail => head() match {
        case r: Right[_, _] => r
        case Left(list) => generate(tail, carry ::: list)
      }
    }


  private def createGeneratorFile(metaModel: MetaModelEntity, diagram: Diagram, hierarchyContainer: Cache): Either[List[File], ErrorMessage] = {
    val metaModelUuid = metaModel._id
    val currentDir = System.getenv("PWD")
    val generatorOutputLocation: String = currentDir + "/server/model_specific/" + metaModelUuid + "/"
    val vrGeneratorOutputLocation = currentDir + "/server/model_specific/vr/" + metaModelUuid + "/"

    Files.createDirectories(Paths.get(generatorOutputLocation))
    Files.createDirectories(Paths.get(vrGeneratorOutputLocation))


    val styles = hierarchyContainer.styleHierarchy.nodeView.values.map(_.data).toList

    val generators = List(
      tryRecoverSingle(() => StyleGenerator.doGenerateFile(styles, generatorOutputLocation), "StyleGenerator"),
      tryRecover(() => ShapeGenerator.doGenerateFile(hierarchyContainer, generatorOutputLocation, diagram.nodes), "ShapeGenerator"),
      tryRecover(() => DiagramGenerator.doGenerateFile(diagram, generatorOutputLocation), "DiagramGenerator"),

      // Generate files for the VR - Editor
      tryRecover(() => VrShapeGenerator.doGenerateFile(hierarchyContainer, vrGeneratorOutputLocation, diagram.nodes), "VrShapeGenerator"),
      tryRecover(() => VrDiagramGenerator.doGenerateFiles(diagram, vrGeneratorOutputLocation), "VrDiagramGenerator")
    )


    val res = generate(generators)
    res match {
      case Left(files) =>
        files.foreach(f => Files.write(Paths.get(f.name), f.content.getBytes))
      case _ =>
    }

    res
  }

}


