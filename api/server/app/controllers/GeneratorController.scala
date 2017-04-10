package controllers

import javax.inject.Inject

import generator.parser.Cache
import generator.parser.SprayParser
import generator.generators.diagram.DiagramGenerator
import generator.generators.style.StyleGenerator
import generator.generators.shape.ShapeGenerator
import generator.generators.vr.shape.VrShapeGenerator
import java.nio.file.Files
import java.nio.file.Paths

import scala.concurrent.ExecutionContext.Implicits.global
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import generator.generators.vr.diagram.VrDiagramGenerator
import generator.model.diagram.Diagram
import models.document.MetaModelEntity
import models.document.Repository
import play.api.mvc.Controller
import utils.auth.DefaultEnv
import utils.auth.RepositoryFactory

class GeneratorController @Inject() (implicit repositoryFactory: RepositoryFactory, silhouette: Silhouette[DefaultEnv]) extends Controller {

  def repository[A]()(implicit request: SecuredRequest[DefaultEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  def generate(metaModelUuid: String) = silhouette.SecuredAction.async { implicit request =>

    repository.get[MetaModelEntity](metaModelUuid).map { result =>
      val generatorOutputLocation = System.getenv("PWD") + "/server/model_specific/" + metaModelUuid + "/"
      val vrGeneratorOutputLocation = System.getenv("PWD") + "/server/model_specific/vr/" + metaModelUuid + "/"

      Files.createDirectories(Paths.get(generatorOutputLocation))
      Files.createDirectories(Paths.get(vrGeneratorOutputLocation))

      val hierarchyContainer = Cache()
      val parser = new SprayParser(hierarchyContainer, result)
      var diagram: Option[Diagram] = None
      var error: Option[String] = None

      try {
        parser.parseStyle(result.dsl.style.get.code)
        parser.parseShape(result.dsl.shape.get.code)
        diagram = parser.parseDiagram(result.dsl.diagram.get.code).head
      } catch {
        case e: Throwable => error = Some("There occurred an error during parsing")
      }
      if (error.isEmpty) {
        try {
          StyleGenerator.doGenerate((for (style <- hierarchyContainer.styleHierarchy.nodeView) yield style._2.data).toList, generatorOutputLocation)
          ShapeGenerator.doGenerate(hierarchyContainer, generatorOutputLocation, diagram.get.nodes)
          DiagramGenerator.doGenerate(diagram.get, generatorOutputLocation)

          // Generate files for the VR - Editor
          VrShapeGenerator.doGenerate(hierarchyContainer, vrGeneratorOutputLocation, diagram.get.nodes)
          VrDiagramGenerator.doGenerate(diagram.get, vrGeneratorOutputLocation)
        } catch {
          case e: Throwable => error = Some("There occurred an error during generation");
        }
      }
      if (error.isDefined) BadRequest(error.get) else Ok("Generation successful")
    }.recover {
      case e: Exception => NotFound("Metamodel with id: " + metaModelUuid + " was not found")
    }
  }
}
