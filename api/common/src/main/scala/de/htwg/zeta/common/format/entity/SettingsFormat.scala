package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.document.DockerSettings
import de.htwg.zeta.common.models.document.JobSettings
import de.htwg.zeta.common.models.entity.Settings
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class SettingsFormat(
    sId: String = "id",
    sOwner: String = "owner",
    sJobSettings: String = "jobSettings",
    sMaxRunning: String = "maxRunning",
    sMaxPending: String = "maxPending",
    sDocker: String = "docker",
    sCpuShares: String = "cpuShares",
    sCpuQuota: String = "cpuQuota"
) extends OFormat[Settings] {

  override def writes(settings: Settings): JsObject = Json.obj(
    sId -> settings.id,
    sOwner -> settings.owner,
    sJobSettings -> JobSettingsFormat.writes(settings.jobSettings)
  )

  override def reads(json: JsValue): JsResult[Settings] = for {
    id <- (json \ sId).validate[UUID]
    owner <- (json \ sOwner).validate[UUID]
    jobSettings <- (json \ sJobSettings).validate(JobSettingsFormat)
  } yield {
    Settings(id, owner, jobSettings)
  }

  private object JobSettingsFormat extends OFormat[JobSettings] {

    override def writes(settings: JobSettings): JsObject = Json.obj(
      sMaxRunning -> settings.maxRunning,
      sMaxPending -> settings.maxPending,
      sDocker -> DockerSettingsFormat.writes(settings.docker)
    )

    override def reads(json: JsValue): JsResult[JobSettings] = for {
      maxRunning <- (json \ sMaxRunning).validate[Int]
      maxPending <- (json \ sMaxPending).validate[Int]
      docker <- (json \ sDocker).validate(DockerSettingsFormat)
    } yield {
      JobSettings(maxRunning, maxPending, docker)
    }

  }

  private object DockerSettingsFormat extends OFormat[DockerSettings] {

    override def writes(settings: DockerSettings): JsObject = Json.obj(
      sCpuShares -> settings.cpuShares,
      sCpuQuota -> settings.cpuQuota
    )

    override def reads(json: JsValue): JsResult[DockerSettings] = for {
      cpuShares <- (json \ sCpuShares).validate[Long]
      cpuQuota <- (json \ sCpuQuota).validate[Long]
    } yield {
      DockerSettings(cpuShares, cpuQuota)
    }

  }

}
