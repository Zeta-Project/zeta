package de.htwg.zeta.common.models.document

/**
 * Job settings for a single user
 *
 * @param maxRunning The maximum number of running jobs
 * @param maxPending The maximum number of pending jobs
 */
case class JobSettings(maxRunning: Int, maxPending: Int, docker: DockerSettings)

case class DockerSettings(cpuShares: Long = 0, cpuQuota: Long = 0)

/**
 * Setup default job settings
 */
object JobSettings {

  def default(): JobSettings = JobSettings(10, 100, DockerSettings())
}


