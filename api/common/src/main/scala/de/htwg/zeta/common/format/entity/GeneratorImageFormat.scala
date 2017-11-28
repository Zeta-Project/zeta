package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.GeneratorImageOptions
import de.htwg.zeta.common.models.entity.GeneratorMetaModelReleaseProperty
import de.htwg.zeta.common.models.entity.GeneratorNameProperty
import de.htwg.zeta.common.models.entity.GeneratorOptionProperties
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Writes


object GeneratorImageFormat extends OFormat[GeneratorImage] {

  private val sId = "id"
  private val sName = "name"
  private val sDescription = "description"
  private val sDockerImage = "dockerImage"
  private val sOptions = "options"
  private val sTitle = "title"
  private val sType = "type"

  override def writes(image: GeneratorImage): JsObject = Json.obj(
    sId -> image.id.toString,
    sName -> image.name,
    sDescription -> image.description,
    sDockerImage -> image.dockerImage,
    sOptions -> GeneratorImageOptionsFormat.writes(image.options)
  )

  override def reads(json: JsValue): JsResult[GeneratorImage] = for {
    id <- (json \ sId).validate[UUID]
    name <- (json \ sName).validate[String]
    description <- (json \ sDescription).validate[String]
    dockerImage <- (json \ sDockerImage).validate[String]
    options <- (json \ sOptions).validate(GeneratorImageOptionsFormat)
  } yield {
    GeneratorImage(id, name, description, dockerImage, options)
  }

  object GeneratorImageOptionsFormat extends OFormat[GeneratorImageOptions] {

    private val sSchema = "schema"
    private val sProperties = "properties"

    override def writes(options: GeneratorImageOptions): JsObject = Json.obj(
      sSchema -> options.schema,
      sTitle -> options.title,
      sType -> options.optionType,
      sProperties -> GeneratorOptionPropertiesFormat.writes(options.properties)
    )

    override def reads(json: JsValue): JsResult[GeneratorImageOptions] = for {
      schema <- (json \ sSchema).validate[String]
      title <- (json \ sTitle).validate[String]
      typ <- (json \ sType).validate[String]
      properties <- (json \ sProperties).validate(GeneratorOptionPropertiesFormat)
    } yield {
      GeneratorImageOptions(schema, title, typ, properties)
    }

  }

  object GeneratorOptionPropertiesFormat extends OFormat[GeneratorOptionProperties] {
    private val sMetaModelRelease = "metaModelRelease"

    override def writes(properties: GeneratorOptionProperties): JsObject = Json.obj(
      sName -> Writes.optionWithNull(GeneratorNamePropertyFormat).writes(properties.name),
      sMetaModelRelease -> Writes.optionWithNull(GeneratorMetaModelReleasePropertyFormat).writes(properties.metaModelRelease)
    )

    override def reads(json: JsValue): JsResult[GeneratorOptionProperties] = for {
      name <- (json \ sName).validateOpt(GeneratorNamePropertyFormat)
      metaModelRelease <- (json \ sMetaModelRelease).validateOpt(GeneratorMetaModelReleasePropertyFormat)
    } yield {
      GeneratorOptionProperties(name, metaModelRelease)
    }
  }

  object GeneratorNamePropertyFormat extends OFormat[GeneratorNameProperty] {

    private val sRequired = "required"

    override def writes(property: GeneratorNameProperty): JsObject = Json.obj(
      sTitle -> property.title,
      sType -> property.propertyType,
      sRequired -> property.required
    )

    override def reads(json: JsValue): JsResult[GeneratorNameProperty] = for {
      title <- (json \ sTitle).validate[String]
      typ <- (json \ sType).validate[String]
      required <- (json \ sRequired).validate[Boolean]
    } yield {
      GeneratorNameProperty(title, typ, required)
    }

  }

  private object GeneratorMetaModelReleasePropertyFormat extends OFormat[GeneratorMetaModelReleaseProperty] {

    private val sRef = "ref"

    override def writes(property: GeneratorMetaModelReleaseProperty): JsObject = Json.obj(
      sRef -> property.ref
    )

    override def reads(json: JsValue): JsResult[GeneratorMetaModelReleaseProperty] = {
      (json \ sRef).validate[String].map(GeneratorMetaModelReleaseProperty)
    }

  }

}
