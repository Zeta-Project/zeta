package de.htwg.zeta.server.controller.restApi.format

import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.GeneratorImageOptions
import de.htwg.zeta.common.models.entity.GeneratorMetaModelReleaseProperty
import de.htwg.zeta.common.models.entity.GeneratorNameProperty
import de.htwg.zeta.common.models.entity.GeneratorOptionProperties
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object GeneratorImageFormat extends Writes[GeneratorImage] {

  override def writes(o: GeneratorImage): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "description" -> o.description,
      "dockerImage" -> o.dockerImage,
      "options" -> generateOptions(o.options)
    )
  }

  private def generateOptions(o: GeneratorImageOptions): JsObject = {
    Json.obj(
      "$schema" -> o.schema,
      "title" -> o.title,
      "type" -> o.optionType,
      "properties" -> generateOptionProperties(o.properties)
    )
  }

  private def generateOptionProperties(o: GeneratorOptionProperties): JsObject = {
    optionalNameProperty(o.name) ++ optionalMetaModelReleaseProperty(o.metaModelRelease)
  }

  private def optionalNameProperty(o: Option[GeneratorNameProperty]): JsObject = {
    o match {
      case Some(entity) => generateNameProperty(entity)
      case None => Json.obj()
    }
  }

  private def generateNameProperty(o: GeneratorNameProperty): JsObject = {
    Json.obj(
      "name" ->  Json.obj(
        "title" -> o.title,
        "type" -> o.propertyType,
        "required" -> o.required
      )
    )
  }

  private def optionalMetaModelReleaseProperty(o: Option[GeneratorMetaModelReleaseProperty]): JsObject = {
    o match {
      case Some(entity) => generateMetaModelReleaseProperty(entity)
      case None => Json.obj()
    }
  }

  private def generateMetaModelReleaseProperty(o: GeneratorMetaModelReleaseProperty): JsObject = {
    Json.obj(
      "metaModelRelease" -> Json.obj(
        "$ref" -> o.ref
      )
    )
  }
}
