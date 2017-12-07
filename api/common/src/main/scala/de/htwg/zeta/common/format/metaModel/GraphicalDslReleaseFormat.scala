package de.htwg.zeta.common.format.metaModel

import java.util.UUID

import de.htwg.zeta.common.models.entity.GraphicalDslRelease
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class GraphicalDslReleaseFormat(
    metaModelFormat: MetaModelFormat,
    dslFormat: DslFormat,
    sId: String = "id",
    sName: String = "name",
    sMetaModel: String = "metaModel",
    sDsl: String = "dsl",
    sVersion: String = "version"
) extends OFormat[GraphicalDslRelease] {

  override def writes(release: GraphicalDslRelease): JsObject = Json.obj(
    sId -> release.id,
    sName -> release.name,
    sMetaModel -> metaModelFormat.writes(release.metaModel),
    sDsl -> dslFormat.writes(release.dsl),
    sVersion -> release.version
  )

  override def reads(json: JsValue): JsResult[GraphicalDslRelease] = {
    for {
      id <- (json \ sId).validate[UUID]
      name <- (json \ sName).validate[String]
      metaModel <- (json \ sMetaModel).validate(metaModelFormat)
      dsl <- (json \ sDsl).validate(dslFormat)
      version <- (json \ sVersion).validate[String]
    } yield {
      GraphicalDslRelease(id, name, metaModel, dsl, version)
    }
  }

}
