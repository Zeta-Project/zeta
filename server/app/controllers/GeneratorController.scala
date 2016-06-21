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



class GeneratorController @Inject()(metaModelDao: ZetaMetaModelDao, override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {


  def generate(metaModelUuid: String) = SecuredAction { implicit request =>

    val result = Await.result(metaModelDao.findById(metaModelUuid), 30 seconds)
    if (result.isDefined && result.get.metaModel.elements.nonEmpty) {
      val generatorOutputLocation = System.getenv("PWD") + "/server/public/model_specific/" + metaModelUuid + "/"
      println(generatorOutputLocation)
      Files.createDirectories(Paths.get(generatorOutputLocation))

      val hierarchyContainer = Cache()
      val parser = new SprayParser(hierarchyContainer, result.get)

      parser.parseStyle(result.get.dsl.style.get.code)
      parser.parseShape(result.get.dsl.shape.get.code)
      val diagrams = parser.parseDiagram(result.get.dsl.diagram.get.code)

      ShapeGenerator.doGenerate(hierarchyContainer, generatorOutputLocation)
      SprayGenerator.doGenerate(diagrams.head.get, generatorOutputLocation)
      StyleGenerator.doGenerate((for (style <- hierarchyContainer.styleHierarchy.nodeView) yield style._2.data).toList, generatorOutputLocation)

      Ok("Generation successful")
    } else {
      BadRequest("Generation failed")
    }
  }
}
