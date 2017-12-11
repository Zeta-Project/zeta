package de.htwg.zeta.persistence

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.util.Timeout
import de.htwg.zeta.persistence.actorCache.ActorCacheAccessAuthorisationRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheBondedTaskRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheEventDrivenTaskRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheFileRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheFilterImageRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheFilterRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheGeneratorImageRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheGeneratorRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheLoginInfoRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheLogRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheGraphicalDslRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheGraphicalDslReleaseRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheGraphicalDslInstanceRepository
import de.htwg.zeta.persistence.actorCache.ActorCachePasswordInfoRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheSettingsRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheTimedTaskRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheUserRepository
import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.transient.TransientAccessAuthorisationRepository
import de.htwg.zeta.persistence.transient.TransientBondedTaskRepository
import de.htwg.zeta.persistence.transient.TransientEventDrivenTaskRepository
import de.htwg.zeta.persistence.transient.TransientFileRepository
import de.htwg.zeta.persistence.transient.TransientFilterImageRepository
import de.htwg.zeta.persistence.transient.TransientFilterRepository
import de.htwg.zeta.persistence.transient.TransientGeneratorImageRepository
import de.htwg.zeta.persistence.transient.TransientGeneratorRepository
import de.htwg.zeta.persistence.transient.TransientLoginInfoRepository
import de.htwg.zeta.persistence.transient.TransientLogRepository
import de.htwg.zeta.persistence.transient.TransientGraphicalDslRepository
import de.htwg.zeta.persistence.transient.TransientGraphicalDslReleaseRepository
import de.htwg.zeta.persistence.transient.TransientGraphicalDslInstanceRepository
import de.htwg.zeta.persistence.transient.TransientPasswordInfoRepository
import de.htwg.zeta.persistence.transient.TransientSettingsRepository
import de.htwg.zeta.persistence.transient.TransientTimedTaskRepository
import de.htwg.zeta.persistence.transient.TransientUserRepository

/**
 * ActorCacheMicroServiceTest.
 */
class ActorCacheRepositorySpec extends RepositoryBehavior {

  val system = ActorSystem()
  val cacheDuration = Duration(1, TimeUnit.MINUTES)
  val timeout: Timeout = Duration(1, TimeUnit.MINUTES)

  "ActorCacheRepository" should behave like repositoryBehavior(
    new ActorCacheAccessAuthorisationRepository(new TransientAccessAuthorisationRepository, system, 3, cacheDuration, timeout),
    new ActorCacheBondedTaskRepository(new TransientBondedTaskRepository, system, 3, cacheDuration, timeout),
    new ActorCacheEventDrivenTaskRepository(new TransientEventDrivenTaskRepository, system, 3, cacheDuration, timeout),
    new ActorCacheFilterRepository(new TransientFilterRepository, system, 3, cacheDuration, timeout),
    new ActorCacheFilterImageRepository(new TransientFilterImageRepository, system, 3, cacheDuration, timeout),
    new ActorCacheGeneratorRepository(new TransientGeneratorRepository, system, 3, cacheDuration, timeout),
    new ActorCacheGeneratorImageRepository(new TransientGeneratorImageRepository, system, 3, cacheDuration, timeout),
    new ActorCacheLogRepository(new TransientLogRepository, system, 3, cacheDuration, timeout),
    new ActorCacheGraphicalDslRepository(new TransientGraphicalDslRepository, system, 3, cacheDuration, timeout),
    new ActorCacheGraphicalDslReleaseRepository(new TransientGraphicalDslReleaseRepository, system, 3, cacheDuration, timeout),
    new ActorCacheGraphicalDslInstanceRepository(new TransientGraphicalDslInstanceRepository, system, 3, cacheDuration, timeout),
    new ActorCacheSettingsRepository(new TransientSettingsRepository, system, 3, cacheDuration, timeout),
    new ActorCacheTimedTaskRepository(new TransientTimedTaskRepository, system, 3, cacheDuration, timeout),
    new ActorCacheUserRepository(new TransientUserRepository, system, 3, cacheDuration, timeout),
    new ActorCacheFileRepository(new TransientFileRepository, system, 3, cacheDuration, timeout),
    new ActorCacheLoginInfoRepository(new TransientLoginInfoRepository, system, 3, cacheDuration, timeout),
    new ActorCachePasswordInfoRepository(new TransientPasswordInfoRepository, system, 3, cacheDuration, timeout)
  )

}
