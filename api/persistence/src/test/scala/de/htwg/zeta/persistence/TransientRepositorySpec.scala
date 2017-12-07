package de.htwg.zeta.persistence

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
 * PersistenceMicroServiceTest.
 */
class TransientRepositorySpec extends RepositoryBehavior {

  "TransientRepository" should behave like repositoryBehavior(
    new TransientAccessAuthorisationRepository,
    new TransientBondedTaskRepository,
    new TransientEventDrivenTaskRepository,
    new TransientFilterRepository,
    new TransientFilterImageRepository,
    new TransientGeneratorRepository,
    new TransientGeneratorImageRepository,
    new TransientLogRepository,
    new TransientGraphicalDslRepository,
    new TransientGraphicalDslReleaseRepository,
    new TransientGraphicalDslInstanceRepository,
    new TransientSettingsRepository,
    new TransientTimedTaskRepository,
    new TransientUserRepository,
    new TransientFileRepository,
    new TransientLoginInfoRepository,
    new TransientPasswordInfoRepository
  )

}
