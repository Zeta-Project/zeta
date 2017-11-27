package de.htwg.zeta.common.format.metaModel

import java.util.UUID

import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat


object MetaModelReleaseFormat extends OFormat[MetaModelRelease] {

  private val sId = "id"
  private val sName = "name"
  private val sMetaModel = "metaModel"
  private val sDsl = "dsl"
  private val sVersion = "version"

  override def writes(release: MetaModelRelease): JsValue = Json.obj(
    sId -> release.id,
    sName -> release.name,
    sMetaModel -> MetaModelFormat.writes(release.metaModel),
    sDsl -> Dsl.dslFormat.writes(release.dsl),
    sVersion -> release.version
  )

  override def reads(json: JsValue): JsResult[MetaModelRelease] = {
    for {
      id <- (json \ sId).validate[UUID]
      name <- (json \ sName).validate[String]
      metaModel <- (json \ sMetaModel).validate(MetaModelFormat)
      dsl <- (json \ sDsl).validate[Dsl]
      version <- (json \ sVersion).validate[String]
    } yield {
      MetaModelRelease(id, name, metaModel, dsl, version)
    }
  }

}
