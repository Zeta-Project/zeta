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

object SettingsFormat extends OFormat[Settings] {

  val sId = "id"
  val sOwner = "owner"
  val sJobSettings = "jobSettings"

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

    val sMaxRunning = "maxRunning"
    val sMaxPending = "maxPending"
    val sDocker = "docker"

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

    private val sCpuShares = "cpuShares"
    private val sCpuQuota = "cpuQuota"

    override def writes(settings: DockerSettings): JsObject = Json.obj(
      sCpuShares -> settings.cpuShares,
      sCpuQuota -> settings.cpuQuota
    )

    override def reads(json: JsValue): JsResult[DockerSettings] = for {
      cpuShares <- (json \ sCpuShares).validate[Int]
      cpuQuota <- (json \ sCpuQuota).validate[Int]
    } yield {
      DockerSettings(cpuShares, cpuQuota)
    }

  }

}
