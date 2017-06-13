package de.htwg.zeta.persistence.behavior

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.persistence.general.EntityPersistence
import models.entity.Entity
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait EntityPersistenceBehavior extends AsyncFlatSpec with Matchers {

  def entityPersistenceBehavior[T <: Entity](persistence: EntityPersistence[T], doc1: T, doc2: T, doc2Updated: T, doc3: T): Unit = { // scalastyle:ignore
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
