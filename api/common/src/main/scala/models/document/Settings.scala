package models.document

import julienrf.json.derived
import play.api.libs.json.OFormat

/**
 * Job settings for a single user
 *
 * @param maxRunning The maximum number of running jobs
 * @param maxPending The maximum number of pending jobs
 */
case class JobSettings(maxRunning: Int, maxPending: Int, docker: DockerSettings)

case class DockerSettings(cpuShares: Long = 0, cpuQuota: Long = 0)

object DockerSettings {
  implicit lazy val formatDockerSettings: OFormat[DockerSettings] = derived.oformat
}

/**
 * Setup default job settings
 */
object JobSettings {
  implicit lazy val formatJobSettings: OFormat[JobSettings] = derived.oformat

  def default(): JobSettings = JobSettings(10, 100, DockerSettings())
}

case class ContainerSettings()
