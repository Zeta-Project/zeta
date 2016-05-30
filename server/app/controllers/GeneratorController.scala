package controllers

import javax.inject.Inject

import dao.metaModel.MetaModelDaoImpl
import util.definitions.UserEnvironment
import generator.parser.{Cache, SprayParser}
import generator.generators.spray.SprayGenerator
import generator.generators.style.StyleGenerator
import generator.generators.shape.ShapeGenerator
import play.api.Play.current

import scala.concurrent.duration._
import scala.concurrent.Await
import java.nio.file.{Files, Paths}



class GeneratorController @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {


  def generate(metaModelUuid: String) = SecuredAction { implicit request =>

    val result = Await.result(MetaModelDaoImpl.findById(metaModelUuid), 30 seconds)
    if (result.isDefined && result.get.definition.concept.elements.nonEmpty) {
      val generatorOutputLocation = current.path.toString + "/app/assets/modelEditor/editor/model_specific/" + metaModelUuid + "/"
      Files.createDirectories(Paths.get(generatorOutputLocation))

      val hierarchyContainer = Cache()
      val parser = new SprayParser(hierarchyContainer, result.get.definition)

      parser.parseStyle(result.get.definition.style.get.code)
      parser.parseAbstractShape(result.get.definition.shape.get.code)
      val diagrams = parser.parseDiagram(result.get.definition.diagram.get.code)

      ShapeGenerator.doGenerate(hierarchyContainer, generatorOutputLocation)
      SprayGenerator.doGenerate(diagrams.head.get, generatorOutputLocation)
      StyleGenerator.doGenerate((for (style <- hierarchyContainer.styleHierarchy.nodeView) yield style._2.data).toList, generatorOutputLocation)

      Ok("Generation successful")
    } else {
      BadRequest("Generation failed")
    }
  }
}
