package de.htwg.zeta.persistence.behavior

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.FilterImage
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.GraphicalDslRelease
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.common.models.project.GraphicalDsl
import de.htwg.zeta.persistence.fixtures.AccessAuthorisationFixtures
import de.htwg.zeta.persistence.fixtures.BondedTaskFixtures
import de.htwg.zeta.persistence.fixtures.EventDrivenTaskFixtures
import de.htwg.zeta.persistence.fixtures.FilterImageTestFixtures
import de.htwg.zeta.persistence.fixtures.FilterTestFixtures
import de.htwg.zeta.persistence.fixtures.GeneratorFixtures
import de.htwg.zeta.persistence.fixtures.GeneratorImageFixtures
import de.htwg.zeta.persistence.fixtures.GraphicalDslFixtures
import de.htwg.zeta.persistence.fixtures.GraphicalDslInstanceFixtures
import de.htwg.zeta.persistence.fixtures.LogFixtures
import de.htwg.zeta.persistence.fixtures.MetaModelReleaseFixtures
import de.htwg.zeta.persistence.fixtures.SettingsFixtures
import de.htwg.zeta.persistence.fixtures.TimedTaskFixtures
import de.htwg.zeta.persistence.fixtures.UserFixtures
import de.htwg.zeta.persistence.general.AccessAuthorisationRepository
import de.htwg.zeta.persistence.general.BondedTaskRepository
import de.htwg.zeta.persistence.general.EventDrivenTaskRepository
import de.htwg.zeta.persistence.general.FileRepository
import de.htwg.zeta.persistence.general.FilterImageRepository
import de.htwg.zeta.persistence.general.FilterRepository
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import de.htwg.zeta.persistence.general.GeneratorRepository
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import de.htwg.zeta.persistence.general.GraphicalDslReleaseRepository
import de.htwg.zeta.persistence.general.GraphicalDslRepository
import de.htwg.zeta.persistence.general.LoginInfoRepository
import de.htwg.zeta.persistence.general.LogRepository
import de.htwg.zeta.persistence.general.PasswordInfoRepository
import de.htwg.zeta.persistence.general.SettingsRepository
import de.htwg.zeta.persistence.general.TimedTaskRepository
import de.htwg.zeta.persistence.general.UserRepository


/** PersistenceBehavior. */
trait RepositoryBehavior extends EntityRepositoryBehavior with FilePersistenceBehavior
  with LoginInfoRepositoryBehavior with PasswordInfoRepositoryBehavior {

  def repositoryBehavior( // scalastyle:ignore
      accessAuthorisationPersistence: AccessAuthorisationRepository, // scalastyle:ignore
      bondedTaskPersistence: BondedTaskRepository,
      eventDrivenTaskPersistence: EventDrivenTaskRepository,
      filterPersistence: FilterRepository,
      filterImagePersistence: FilterImageRepository,
      generatorPersistence: GeneratorRepository,
      generatorImagePersistence: GeneratorImageRepository,
      logPersistence: LogRepository,
      metaModelEntityPersistence: GraphicalDslRepository,
      metaModelReleasePersistence: GraphicalDslReleaseRepository,
      modelEntityPersistence: GraphicalDslInstanceRepository,
      settingsPersistence: SettingsRepository,
      timedTaskPersistence: TimedTaskRepository,
      userPersistence: UserRepository,
      filePersistence: FileRepository,
      loginInfoPersistence: LoginInfoRepository,
      passwordInfoPersistence: PasswordInfoRepository
  ): Unit = {

    "AccessAuthorisation" should behave like entityPersistenceBehavior[AccessAuthorisation](
      accessAuthorisationPersistence,
      AccessAuthorisationFixtures.entity1,
      AccessAuthorisationFixtures.entity2,
      AccessAuthorisationFixtures.entity2Updated,
      AccessAuthorisationFixtures.entity3
    )

    "BondedTask" should behave like entityPersistenceBehavior[BondedTask](
      bondedTaskPersistence,
      BondedTaskFixtures.entity1,
      BondedTaskFixtures.entity2,
      BondedTaskFixtures.entity2Updated,
      BondedTaskFixtures.entity3
    )

    "EventDrivenTask" should behave like entityPersistenceBehavior[EventDrivenTask](
      eventDrivenTaskPersistence,
      EventDrivenTaskFixtures.entity1,
      EventDrivenTaskFixtures.entity2,
      EventDrivenTaskFixtures.entity2Updated,
      EventDrivenTaskFixtures.entity3
    )

    "Filter" should behave like entityPersistenceBehavior[Filter](
      filterPersistence,
      FilterTestFixtures.entity1,
      FilterTestFixtures.entity2,
      FilterTestFixtures.entity2Updated,
      FilterTestFixtures.entity3
    )

    "FilterImage" should behave like entityPersistenceBehavior[FilterImage](
      filterImagePersistence,
      FilterImageTestFixtures.entity1,
      FilterImageTestFixtures.entity2,
      FilterImageTestFixtures.entity2Updated,
      FilterImageTestFixtures.entity3
    )

    "Generator" should behave like entityPersistenceBehavior[Generator](
      generatorPersistence,
      GeneratorFixtures.entity1,
      GeneratorFixtures.entity2,
      GeneratorFixtures.entity2Updated,
      GeneratorFixtures.entity3
    )

    "GeneratorImage" should behave like entityPersistenceBehavior[GeneratorImage](
      generatorImagePersistence,
      GeneratorImageFixtures.entity1,
      GeneratorImageFixtures.entity2,
      GeneratorImageFixtures.entity2Updated,
      GeneratorImageFixtures.entity3
    )

    "Log" should behave like entityPersistenceBehavior[Log](
      logPersistence,
      LogFixtures.entity1,
      LogFixtures.entity2,
      LogFixtures.entity2Updated,
      LogFixtures.entity3
    )

    "GraphicalDsl" should behave like entityPersistenceBehavior[GraphicalDsl](
      metaModelEntityPersistence,
      GraphicalDslFixtures.entity1,
      GraphicalDslFixtures.entity2,
      GraphicalDslFixtures.entity2Updated,
      GraphicalDslFixtures.entity3
    )

    "MetaModelRelease" should behave like entityPersistenceBehavior[GraphicalDslRelease](
      metaModelReleasePersistence,
      MetaModelReleaseFixtures.entity1,
      MetaModelReleaseFixtures.entity2,
      MetaModelReleaseFixtures.entity2Updated,
      MetaModelReleaseFixtures.entity3
    )

    "GraphicalDslInstance" should behave like entityPersistenceBehavior[GraphicalDslInstance](
      modelEntityPersistence,
      GraphicalDslInstanceFixtures.entity1,
      GraphicalDslInstanceFixtures.entity2,
      GraphicalDslInstanceFixtures.entity2Updated,
      GraphicalDslInstanceFixtures.entity3
    )

    "Settings" should behave like entityPersistenceBehavior[Settings](
      settingsPersistence,
      SettingsFixtures.entity1,
      SettingsFixtures.entity2,
      SettingsFixtures.entity2Updated,
      SettingsFixtures.entity3
    )

    "TimedTask" should behave like entityPersistenceBehavior[TimedTask](
      timedTaskPersistence,
      TimedTaskFixtures.entity1,
      TimedTaskFixtures.entity2,
      TimedTaskFixtures.entity2Updated,
      TimedTaskFixtures.entity3
    )

    "User" should behave like entityPersistenceBehavior[User](
      userPersistence,
      UserFixtures.entity1,
      UserFixtures.entity2,
      UserFixtures.entity2Updated,
      UserFixtures.entity3
    )

    "File" should behave like filePersistenceBehavior(filePersistence)

    "LoginInfo" should behave like loginInfoPersistenceBehavior(loginInfoPersistence)

    "PasswordInfo" should behave like passwordInfoPersistenceBehavior(passwordInfoPersistence)

  }

}
