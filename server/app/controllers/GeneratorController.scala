package controllers

import javax.inject.Inject

import dao.metaModel.MetaModelDaoImpl
import util.definitions.UserEnvironment
import generator.parser.{Cache, SprayParser}

import scala.concurrent.duration._
import scala.concurrent.Await

class GeneratorController @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  def generate(metaModelUuid: String) = SecuredAction { implicit request =>

    val result = Await.result(MetaModelDaoImpl.findById(metaModelUuid), 30 seconds)
    if (result.isDefined && result.get.definition.concept.elements.nonEmpty) {

      val hierarchyContainer = Cache()
      val parser = new SprayParser(hierarchyContainer, result.get.definition)

      val styles = parser.parseStyle(result.get.definition.style.get.code)
      val shapes = parser.parseAbstractShape(result.get.definition.shape.get.code)
      val diagram = parser.parseDiagram(result.get.definition.diagram.get.code)

      Ok("Generation successful")
    } else {
      BadRequest("Generation failed")
    }
  }
}
