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
import models.result.Failure
import models.result.Result
import models.result.Success
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
        case Success(_) => Ok("Generation successful")
        case Failure(error) => BadRequest(error)
      })(Implicits.global)
      .recover {
        case _: Exception => NotFound("Metamodel with id: " + metaModelUuid + " was not found")
      }(Implicits.global)
  })


  def createGenerators(metaModel: MetaModelEntity): Result[List[File]] = {
    val hierarchyContainer = Cache()
    parseMetaModel(metaModel, hierarchyContainer).flatMap(dia => createAndSaveGeneratorFiles(metaModel, dia, hierarchyContainer))
  }

  private def parseMetaModel(metaModel: MetaModelEntity, hierarchyContainer: Cache): Result[Diagram] = {
    val parser = new SprayParser(hierarchyContainer, metaModel)

    def tryParse[E, R](get: Dsl => Option[E], parse: E => List[R], name: String): Result[List[R]] = {
      get(metaModel.dsl) match {
        case None => Failure(s"$name not available")
        case Some(e) => Result(() => parse(e), s"$name failed parsing")
      }
    }

    tryParse[DslStyle, Style](_.style, (s: DslStyle) => parser.parseStyle(s.code), "Style").
      flatMap(_ => tryParse[Shape, AnyRef](_.shape, s => parser.parseShape(s.code), "Shape")).
      flatMap(_ => tryParse[DslDiagram, Option[Diagram]](_.diagram, s => parser.parseDiagram(s.code), GeneratorController.DiagramName)).
      flatMap {
        case Some(dia) :: _ => Success(dia)
        case _ => Failure(s"No ${GeneratorController.DiagramName} available")
      }
  }

  private def createAndSaveGeneratorFiles(metaModel: MetaModelEntity, diagram: Diagram, hierarchyContainer: Cache): Result[List[File]] = {
    val metaModelUuid = metaModel._id
    val currentDir = s"${System.getenv("PWD")}/server/model_specific"
    val generatorOutputLocation: String = s"$currentDir/$metaModelUuid/"
    val vrGeneratorOutputLocation = s"$currentDir/vr/$metaModelUuid/"

    createGeneratorFiles(diagram, hierarchyContainer).flatMap(gen => {
      createVrGeneratorFiles(diagram, hierarchyContainer).map(vrGen => {
        Files.createDirectories(Paths.get(generatorOutputLocation))
        Files.createDirectories(Paths.get(vrGeneratorOutputLocation))
        gen.foreach(f => Files.write(Paths.get(generatorOutputLocation + f.name), f.content.getBytes))
        vrGen.foreach(f => Files.write(Paths.get(vrGeneratorOutputLocation + f.name), f.content.getBytes))
        gen ::: vrGen
      })
    })
  }

  private def createGeneratorFiles(diagram: Diagram, hierarchyContainer: Cache): Result[List[File]] = {
    val styles = hierarchyContainer.styleHierarchy.nodeView.values.map(_.data).toList
    val generators: List[() => Result[List[File]]] = List(
      () => StyleGenerator.doGenerateResult(styles).map(List(_)),
      () => ShapeGenerator.doGenerateResult(hierarchyContainer, diagram.nodes),
      () => DiagramGenerator.doGenerateResult(diagram)
    )

    generate(generators)
  }

  private def createVrGeneratorFiles(diagram: Diagram, hierarchyContainer: Cache): Result[List[File]] = {
    val generators: List[() => Result[List[File]]] = List(
      // Generate files for the VR - Editor
      () => VrShapeGenerator.doGenerateResult(hierarchyContainer, diagram.nodes),
      () => VrDiagramGenerator.doGenerateResult(diagram)
    )

    generate(generators)
  }


  @tailrec
  private def generate(generators: List[() => Result[List[File]]], carry: List[File] = Nil): Result[List[File]] =
    generators match {
      case Nil => Success(carry)
      case head :: tail => head() match {
        case f: Failure => f
        case Success(list) => generate(tail, carry ::: list)
      }
    }
}

object GeneratorController {
  private[GeneratorController] val DiagramName = "Diagram"
}
