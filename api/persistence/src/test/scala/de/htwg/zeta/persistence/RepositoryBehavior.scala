package de.htwg.zeta.persistence

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import de.htwg.zeta.persistence.entityTestCases.AccessAuthorisationTestCase
import de.htwg.zeta.persistence.entityTestCases.BondedTaskTestCase
import de.htwg.zeta.persistence.entityTestCases.LogTestCase
import de.htwg.zeta.persistence.entityTestCases.UserTestCase
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.Repository
import models.entity.BondedTask
import models.entity.Entity
import models.entity.Log
import models.entity.User
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait RepositoryBehavior extends AsyncFlatSpec with Matchers {

  /** Behavior for a PersistenceService.
   *
   * @param service PersistenceService
   */
  def serviceBehavior(service: Repository): Unit = {

    "AccessAuthorisation" should behave like entityBehavior[AccessAuthorisation](
      service.accessAuthorisations,
      AccessAuthorisationTestCase.entity1,
      AccessAuthorisationTestCase.entity2,
      AccessAuthorisationTestCase.entity2Updated,
      AccessAuthorisationTestCase.entity3
    )

    "BondedTask" should behave like entityBehavior[BondedTask](
      service.bondTasks,
      BondedTaskTestCase.entity1,
      BondedTaskTestCase.entity2,
      BondedTaskTestCase.entity2Updated,
      BondedTaskTestCase.entity3
    )

    "User" should behave like entityBehavior[User](
      service.users,
      UserTestCase.entity1,
      UserTestCase.entity2,
      UserTestCase.entity2Updated,
      UserTestCase.entity3
    )

    "Log" should behave like entityBehavior[Log](
      service.logs,
      LogTestCase.entity1,
      LogTestCase.entity2,
      LogTestCase.entity2Updated,
      LogTestCase.entity3
    )

  }

  private def entityBehavior[T <: Entity](persistence: Persistence[T], doc1: T, doc2: T, doc2Updated: T, doc3: T): Unit = { // scalastyle:ignore
    doc1.id shouldNot be(doc2.id)
    doc1.id shouldNot be(doc3.id)
    doc2.id shouldNot be(doc3.id)
    doc2.id shouldBe doc2Updated.id
    doc2 shouldNot be(doc2Updated)

    it should "remove all already existing entities" in {
      persistence.readAllIds().flatMap { ids =>
        Future.sequence(ids.map(id => persistence.delete(id))).flatMap { _ =>
          persistence.readAllIds().flatMap { ids =>
            ids shouldBe Set.empty
          }
        }
      }
    }

    it should "create a entity" in {
      persistence.create(doc1).flatMap { _ =>
        persistence.readAllIds().flatMap { ids =>
          ids shouldBe Set(doc1.id)
        }
      }
    }

    it should "create a second entity" in {
      persistence.create(doc2).flatMap { _ =>
        persistence.readAllIds().flatMap { ids =>
          ids.size shouldBe 2
          ids should contain(doc1.id)
          ids should contain(doc2.id)
        }
      }
    }

    it should "create a third entity" in {
      persistence.create(doc3).flatMap { _ =>
        persistence.readAllIds().flatMap { ids =>
          ids.size shouldBe 3
          ids should contain(doc1.id)
          ids should contain(doc2.id)
          ids should contain(doc3.id)
        }
      }
    }

    it should "fail the future with any Exception, when creating an already existing entity" in {
      recoverToSucceededIf[Exception] {
        persistence.create(doc2)
      }
    }

    it should "read the first, second and third entity" in {
      persistence.read(doc1.id).flatMap { d1 =>
        persistence.read(doc2.id).flatMap { d2 =>
          persistence.read(doc3.id).flatMap { d3 =>
            d1 shouldBe doc1
            d2 shouldBe doc2
            d3 shouldBe doc3
          }
        }
      }
    }

    it should "fail the future with any Exception, when reading a non-existent entity" in {
      recoverToSucceededIf[Exception] {
        persistence.read(UUID.randomUUID)
      }
    }

    it should "delete the first entity" in {
      persistence.delete(doc1.id).flatMap { _ =>
        persistence.readAllIds().flatMap { ids =>
          ids.size shouldBe 2
          ids should contain(doc2.id)
          ids should contain(doc3.id)
        }
      }
    }

    it should "fail the future with any Exception, when deleting a non-existent entity" in {
      recoverToSucceededIf[Exception] {
        persistence.delete(doc1.id)
      }
    }

    it should "update the second entity" in {
      persistence.update(doc2Updated).flatMap { _ =>
        persistence.read(doc2.id).flatMap { d2 =>
          persistence.read(doc3.id).flatMap { d3 =>
            persistence.readAllIds().flatMap { ids =>
              ids.size shouldBe 2
              d2 shouldBe doc2Updated
              d3 shouldBe doc3
            }
          }
        }
      }
    }

    it should "fail the future with any Exception, when updating a non-existent entity" in {
      recoverToSucceededIf[Exception] {
        persistence.update(doc1)
      }
    }

    it should "delete the second and third entity" in {
      persistence.delete(doc2.id).flatMap { _ =>
        persistence.delete(doc3.id).flatMap { _ =>
          persistence.readAllIds().flatMap { ids =>
            ids shouldBe Set.empty
          }
        }
      }
    }


    it should "it should create all entities again" in {
      persistence.create(doc1).flatMap { _ =>
        persistence.create(doc2Updated).flatMap { _ =>
          persistence.create(doc3).flatMap { _ =>
            persistence.readAllIds().flatMap { ids =>
              ids.size shouldBe 3
              ids should contain(doc1.id)
              ids should contain(doc2.id)
              ids should contain(doc3.id)
            }
          }
        }
      }
    }

  }

}
