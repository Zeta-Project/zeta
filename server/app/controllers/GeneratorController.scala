package controllers

import javax.inject.Inject

import dao.metaModel.MetaModelDaoImpl
import util.definitions.UserEnvironment
import generator.parser.{Cache, ShapeSketch, SprayParser}
import generator.generators.spray.SprayGenerator
import generator.generators.style.StyleGenerator
import generator.generators.shape.ShapeGenerator
import generator.model.style.Style
import play.api.Play.current

import scala.concurrent.duration._
import scala.concurrent.Await
import java.nio.file.{Files, Paths}

import generator.model.shapecontainer.shape.Shape

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class GeneratorController @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {


  def generate(metaModelUuid: String) = SecuredAction { implicit request =>

    val result = Await.result(MetaModelDaoImpl.findById(metaModelUuid), 30 seconds)
    if (result.isDefined && result.get.definition.concept.elements.nonEmpty) {
      val generatorOutputLocation = current.path.toString + "/app/assets/modelEditor/editor/model_specific/" + metaModelUuid + "/"
      Files.createDirectories(Paths.get(generatorOutputLocation))

      val hierarchyContainer = Cache()
      val parser = new SprayParser(hierarchyContainer, result.get.definition)

      val styles = parser.parseStyle(result.get.definition.style.get.code)
      StyleGenerator.doGenerate(styles, generatorOutputLocation)

      val shapes = parser.parseAbstractShape(result.get.definition.shape.get.code)
      ShapeGenerator.doGenerate(hierarchyContainer, generatorOutputLocation)

      val diagrams = parser.parseDiagram(result.get.definition.diagram.get.code)
      SprayGenerator.doGenerate(diagrams.head.get, generatorOutputLocation)

      Ok("Generation successful")
    } else {
      BadRequest("Generation failed")
    }
  }
}
