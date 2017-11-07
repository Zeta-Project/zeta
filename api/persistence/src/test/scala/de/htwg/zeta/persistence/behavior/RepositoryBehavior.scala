package de.htwg.zeta.persistence.behavior

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.FilterImage
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.persistence.fixtures.AccessAuthorisationFixtures
import de.htwg.zeta.persistence.fixtures.BondedTaskFixtures
import de.htwg.zeta.persistence.fixtures.EventDrivenTaskFixtures
import de.htwg.zeta.persistence.fixtures.FilterImageTestFixtures
import de.htwg.zeta.persistence.fixtures.FilterTestFixtures
import de.htwg.zeta.persistence.fixtures.GeneratorFixtures
import de.htwg.zeta.persistence.fixtures.GeneratorImageFixtures
import de.htwg.zeta.persistence.fixtures.LogFixtures
import de.htwg.zeta.persistence.fixtures.MetaModelEntityFixtures
import de.htwg.zeta.persistence.fixtures.MetaModelReleaseFixtures
import de.htwg.zeta.persistence.fixtures.ModelEntityFixtures
import de.htwg.zeta.persistence.fixtures.SettingsFixtures
import de.htwg.zeta.persistence.fixtures.TimedTaskFixtures
import de.htwg.zeta.persistence.fixtures.UserFixtures
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.PasswordInfoPersistence


/** PersistenceBehavior. */
trait RepositoryBehavior extends EntityPersistenceBehavior with FilePersistenceBehavior
  with LoginInfoPersistenceBehavior with PasswordInfoPersistenceBehavior {

  def persistenceBehavior(
      accessAuthorisationPersistence: EntityPersistence[AccessAuthorisation],
      bondedTaskPersistence: EntityPersistence[BondedTask],
      eventDrivenTaskPersistence: EntityPersistence[EventDrivenTask],
      filterPersistence: EntityPersistence[Filter],
      filterImagePersistence: EntityPersistence[FilterImage],
      generatorPersistence: EntityPersistence[Generator],
      generatorImagePersistence: EntityPersistence[GeneratorImage],
      logPersistence: EntityPersistence[Log],
      metaModelEntityPersistence: EntityPersistence[MetaModelEntity],
      metaModelReleasePersistence: EntityPersistence[MetaModelRelease],
      modelEntityPersistence: EntityPersistence[ModelEntity],
      settingsPersistence: EntityPersistence[Settings],
      timedTaskPersistence: EntityPersistence[TimedTask],
      userPersistence: EntityPersistence[User],
      filePersistence: FilePersistence,
      loginInfoPersistence: LoginInfoPersistence,
      passwordInfoPersistence: PasswordInfoPersistence
  ): Unit = { // scalastyle:ignore

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

    "MetaModelEntity" should behave like entityPersistenceBehavior[MetaModelEntity](
      metaModelEntityPersistence,
      MetaModelEntityFixtures.entity1,
      MetaModelEntityFixtures.entity2,
      MetaModelEntityFixtures.entity2Updated,
      MetaModelEntityFixtures.entity3
    )

    "MetaModelRelease" should behave like entityPersistenceBehavior[MetaModelRelease](
      metaModelReleasePersistence,
      MetaModelReleaseFixtures.entity1,
      MetaModelReleaseFixtures.entity2,
      MetaModelReleaseFixtures.entity2Updated,
      MetaModelReleaseFixtures.entity3
    )

    "ModelEntity" should behave like entityPersistenceBehavior[ModelEntity](
      modelEntityPersistence,
      ModelEntityFixtures.entity1,
      ModelEntityFixtures.entity2,
      ModelEntityFixtures.entity2Updated,
      ModelEntityFixtures.entity3
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
