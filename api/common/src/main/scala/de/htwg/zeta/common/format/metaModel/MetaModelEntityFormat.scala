package de.htwg.zeta.common.format.metaModel

import java.util.UUID

import de.htwg.zeta.common.models.entity.MetaModelEntity
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat


class MetaModelEntityFormat(
    metaModelFormat: MetaModelFormat,
    dslFormat: DslFormat,
    sId: String = "id",
    sMetaModel: String = "metaModel",
    sDsl: String = "dsl",
    sValidator: String = "validator"
) extends OFormat[MetaModelEntity] {

  override def writes(entity: MetaModelEntity): JsObject = Json.obj(
    sId -> entity.id,
    sMetaModel -> metaModelFormat.writes(entity.metaModel),
    sDsl -> dslFormat.writes(entity.dsl),
    sValidator -> entity.validator
  )

  override def reads(json: JsValue): JsResult[MetaModelEntity] = for {
    id <- (json \ sId).validate[UUID]
    metaModel <- (json \ sMetaModel).validate(metaModelFormat)
    dsl <- (json \ sDsl).validate(dslFormat)
    validator <- (json \ sValidator).validateOpt[String]
  } yield {
    MetaModelEntity(id, metaModel, dsl, validator)
  }

}
