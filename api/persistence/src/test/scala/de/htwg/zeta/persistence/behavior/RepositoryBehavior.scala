package de.htwg.zeta.persistence.behavior

import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import de.htwg.zeta.persistence.entityTestCases.AccessAuthorisationTestCase
import de.htwg.zeta.persistence.entityTestCases.BondedTaskTestCase
import de.htwg.zeta.persistence.entityTestCases.LogTestCase
import de.htwg.zeta.persistence.entityTestCases.UserTestCase
import de.htwg.zeta.persistence.general.Repository
import models.entity.BondedTask
import models.entity.Log
import models.entity.User


/** PersistenceBehavior. */
trait RepositoryBehavior extends EntityPersistenceBehavior with FilePersistenceBehavior {

  /** Behavior for a PersistenceService.
   *
   * @param repository PersistenceService
   */
  def repositoryBehavior(repository: Repository): Unit = {

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

    "User" should behave like entityPersistenceBehavior[User](
      repository.users,
      UserTestCase.entity1,
      UserTestCase.entity2,
      UserTestCase.entity2Updated,
      UserTestCase.entity3
    )

    "Log" should behave like entityPersistenceBehavior[Log](
      repository.logs,
      LogTestCase.entity1,
      LogTestCase.entity2,
      LogTestCase.entity2Updated,
      LogTestCase.entity3
    )

    "Files" should behave like filePersistenceBehavior(repository)

  }

}
