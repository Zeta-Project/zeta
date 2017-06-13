package de.htwg.zeta.persistence.behavior

import de.htwg.zeta.persistence.entityTestCases.AccessAuthorisationTestCase
import de.htwg.zeta.persistence.entityTestCases.BondedTaskTestCase
import de.htwg.zeta.persistence.entityTestCases.EventDrivenTaskTestCase
import de.htwg.zeta.persistence.entityTestCases.FilterImageTestCase
import de.htwg.zeta.persistence.entityTestCases.FilterTestCase
import de.htwg.zeta.persistence.entityTestCases.GeneratorImageTestCase
import de.htwg.zeta.persistence.entityTestCases.GeneratorTestCase
import de.htwg.zeta.persistence.entityTestCases.LogTestCase
import de.htwg.zeta.persistence.entityTestCases.SettingsTestCase
import de.htwg.zeta.persistence.entityTestCases.TimedTaskTestCase
import de.htwg.zeta.persistence.entityTestCases.UserTestCase
import de.htwg.zeta.persistence.general.Repository
import models.entity.AccessAuthorisation
import models.entity.BondedTask
import models.entity.EventDrivenTask
import models.entity.Filter
import models.entity.FilterImage
import models.entity.Generator
import models.entity.GeneratorImage
import models.entity.Log
import models.entity.MetaModelEntity
import models.entity.MetaModelRelease
import models.entity.ModelEntity
import models.entity.Settings
import models.entity.TimedTask
import models.entity.User


/** PersistenceBehavior. */
trait RepositoryBehavior extends EntityPersistenceBehavior with FilePersistenceBehavior {

  def repositoryBehavior(repository: Repository): Unit = { // scalastyle:ignore

    "AccessAuthorisation" should behave like entityPersistenceBehavior[AccessAuthorisation](
      repository.accessAuthorisations,
      AccessAuthorisationTestCase.entity1,
      AccessAuthorisationTestCase.entity2,
      AccessAuthorisationTestCase.entity2Updated,
      AccessAuthorisationTestCase.entity3
    )

    "BondedTask" should behave like entityPersistenceBehavior[BondedTask](
      repository.bondTasks,
      BondedTaskTestCase.entity1,
      BondedTaskTestCase.entity2,
      BondedTaskTestCase.entity2Updated,
      BondedTaskTestCase.entity3
    )

    "EventDrivenTask" should behave like entityPersistenceBehavior[EventDrivenTask](
      repository.eventDrivenTasks,
      EventDrivenTaskTestCase.entity1,
      EventDrivenTaskTestCase.entity2,
      EventDrivenTaskTestCase.entity2Updated,
      EventDrivenTaskTestCase.entity3
    )

    "Files" should behave like filePersistenceBehavior(repository)

    "Filter" should behave like entityPersistenceBehavior[Filter](
      repository.filters,
      FilterTestCase.entity1,
      FilterTestCase.entity2,
      FilterTestCase.entity2Updated,
      FilterTestCase.entity3
    )

    "FilterImage" should behave like entityPersistenceBehavior[FilterImage](
      repository.filterImages,
      FilterImageTestCase.entity1,
      FilterImageTestCase.entity2,
      FilterImageTestCase.entity2Updated,
      FilterImageTestCase.entity3
    )

    "Generator" should behave like entityPersistenceBehavior[Generator](
      repository.generators,
      GeneratorTestCase.entity1,
      GeneratorTestCase.entity2,
      GeneratorTestCase.entity2Updated,
      GeneratorTestCase.entity3
    )

    "GeneratorImage" should behave like entityPersistenceBehavior[GeneratorImage](
      repository.generatorImages,
      GeneratorImageTestCase.entity1,
      GeneratorImageTestCase.entity2,
      GeneratorImageTestCase.entity2Updated,
      GeneratorImageTestCase.entity3
    )

    "Log" should behave like entityPersistenceBehavior[Log](
      repository.logs,
      LogTestCase.entity1,
      LogTestCase.entity2,
      LogTestCase.entity2Updated,
      LogTestCase.entity3
    )

    /* "MetaModelEntity" should behave like entityPersistenceBehavior[MetaModelEntity](
      repository.metaModelEntities,
      null,
      null, // TODO
      null,
      null
    ) */

    /* "MetaModelRelease" should behave like entityPersistenceBehavior[MetaModelRelease](
      repository.metaModelReleases,
      null,
      null, // TODO
      null,
      null
    ) */

    /* "ModelEntity" should behave like entityPersistenceBehavior[ModelEntity](
      repository.modelEntities,
      null,
      null, // TODO
      null,
      null
    ) */

    "Settings" should behave like entityPersistenceBehavior[Settings](
      repository.settings,
      SettingsTestCase.entity1,
      SettingsTestCase.entity2,
      SettingsTestCase.entity2Updated,
      SettingsTestCase.entity3
    )

    "TimedTask" should behave like entityPersistenceBehavior[TimedTask](
      repository.timedTasks,
      TimedTaskTestCase.entity1,
      TimedTaskTestCase.entity2,
      TimedTaskTestCase.entity2Updated,
      TimedTaskTestCase.entity3
    )

    "User" should behave like entityPersistenceBehavior[User](
      repository.users,
      UserTestCase.entity1,
      UserTestCase.entity2,
      UserTestCase.entity2Updated,
      UserTestCase.entity3
    )

  }

}
