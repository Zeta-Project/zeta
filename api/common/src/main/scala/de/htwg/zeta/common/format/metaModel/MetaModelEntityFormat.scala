package de.htwg.zeta.common.format.metaModel

import java.util.UUID

import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads


object MetaModelEntityFormat extends OFormat[MetaModelEntity] {

  private val sId = "id"
  private val sMetaModel = "metaModel"
  private val sDsl = "dsl"
  private val sValidator = "validator"

  override def writes(entity: MetaModelEntity): JsObject = Json.obj(
    sId -> entity.id,
    sMetaModel -> MetaModelFormat.writes(entity.metaModel),
    sDsl -> Dsl.dslFormat.writes(entity.dsl),
    sValidator -> entity.validator
  )

  override def reads(json: JsValue): JsResult[MetaModelEntity] = {
    for {
      id <- (json \ sId).validate[UUID]
      metaModel <- (json \ sMetaModel).validate(MetaModelFormat)
      dsl <- (json \ sDsl).validate(Dsl.dslFormat)
      validator <- (json \ sValidator).validate(Reads.optionNoError[String])
    } yield {
      MetaModelEntity(id, metaModel, dsl, validator)
    }
  }

}
