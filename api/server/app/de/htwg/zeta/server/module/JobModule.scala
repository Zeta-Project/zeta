package de.htwg.zeta.server.module

import de.htwg.zeta.server.job.AuthTokenCleaner
import de.htwg.zeta.server.job.Scheduler
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * The job module.
 */
class JobModule extends ScalaModule with AkkaGuiceSupport {

  /**
   * Configures the module.
   */
  def configure() = {
    bindActor[AuthTokenCleaner]("auth-token-cleaner")
    bind[Scheduler].asEagerSingleton()
  }
}
