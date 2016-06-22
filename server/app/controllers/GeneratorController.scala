package controllers

import javax.inject.Inject

import dao.metaModel.ZetaMetaModelDao
import util.definitions.UserEnvironment
import generator.parser.{Cache, SprayParser}
import generator.generators.spray.SprayGenerator
import generator.generators.style.StyleGenerator
import generator.generators.shape.ShapeGenerator
import play.api.Play.current

import scala.concurrent.duration._
import scala.concurrent.Await
import java.nio.file.{Files, Paths}

import generator.model.diagram.Diagram



class GeneratorController @Inject()(metaModelDao: ZetaMetaModelDao, override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {


  def generate(metaModelUuid: String) = SecuredAction { implicit request =>

    val result = Await.result(metaModelDao.findById(metaModelUuid), 30 seconds)
    if (result.isDefined && result.get.metaModel.elements.nonEmpty) {
      val generatorOutputLocation = System.getenv("PWD") + "/server/model_specific/" + metaModelUuid + "/"
      Files.createDirectories(Paths.get(generatorOutputLocation))

      val hierarchyContainer = Cache()
      val parser = new SprayParser(hierarchyContainer, result.get)
      var diagram: Option[Diagram] = None
      var error = ""

      try {
        parser.parseStyle(result.get.dsl.style.get.code)
        parser.parseShape(result.get.dsl.shape.get.code)
        diagram = parser.parseDiagram(result.get.dsl.diagram.get.code).head
      } catch {
        case _ :Throwable => error = "There occurred an error during parsing"
      }

      try {
        ShapeGenerator.doGenerate(hierarchyContainer, generatorOutputLocation)
        SprayGenerator.doGenerate(diagram.get, generatorOutputLocation)
        StyleGenerator.doGenerate((for (style <- hierarchyContainer.styleHierarchy.nodeView) yield style._2.data).toList, generatorOutputLocation)
      } catch {
        case _ :Throwable => error = "There occurred an error during generation"
      }

      if(error.nonEmpty) BadRequest(error) else Ok("Generation successful")

    } else {
      NotFound("Metamodel with id: "+metaModelUuid+"was not found")
    }
  }
}
