package de.htwg.zeta.persistence.behavior

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.persistence.general.EntityPersistence
import models.entity.Entity
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait EntityPersistenceBehavior extends AsyncFlatSpec with Matchers {

  def entityPersistenceBehavior[T <: Entity](persistence: EntityPersistence[T], entity1: T, entity2: T, doc2Updated: T, entity3: T): Unit = { // scalastyle:ignore
    entity1.id shouldNot be(entity2.id)
    entity1.id shouldNot be(entity3.id)
    entity2.id shouldNot be(entity3.id)
    entity2.id shouldBe doc2Updated.id
    entity2 shouldNot be(doc2Updated)

    it should "remove all already existing entities" in {
      for {
        existingIds <- persistence.readAllIds()
        _ <- Future.sequence(existingIds.map(id => persistence.delete(id)))
        ids <- persistence.readAllIds()
      } yield {
        ids shouldBe Set.empty
      }
    }

    it should "create a entity" in {
      for {
        _ <- persistence.create(entity1)
        ids <- persistence.readAllIds()
      } yield {
        ids shouldBe Set(entity1.id)
      }
    }

    it should "create a second entity" in {
      for {
        _ <- persistence.create(entity2)
        ids <- persistence.readAllIds()
      } yield {
        ids shouldBe Set(entity1.id, entity2.id)
      }
    }

    it should "create a third entity" in {
      for {
        _ <- persistence.create(entity3)
        ids <- persistence.readAllIds()
      } yield {
        ids shouldBe Set(entity1.id, entity2.id, entity3.id)
      }
    }

    it should "fail the future with any Exception, when creating an already existing entity" in {
      recoverToSucceededIf[Exception] {
        persistence.create(entity2)
      }
    }

    it should "read the first, second and third entity" in {
      for {
        e1 <- persistence.read(entity1.id)
        e2 <- persistence.read(entity2.id)
        e3 <- persistence.read(entity3.id)
      } yield {
        e1 shouldBe entity1
        e2 shouldBe entity2
        e3 shouldBe entity3
      }
    }

    it should "fail the future with any Exception, when reading a non-existent entity" in {
      recoverToSucceededIf[Exception] {
        persistence.read(UUID.randomUUID)
      }
    }

    it should "delete the first entity" in {
      for {
        _ <- persistence.delete(entity1.id)
        ids <- persistence.readAllIds()
      } yield {
        ids shouldBe Set(entity2.id, entity3.id)
      }
    }

    it should "fail the future with any Exception, when deleting a non-existent entity" in {
      recoverToSucceededIf[Exception] {
        persistence.delete(entity1.id)
      }
    }

    it should "update the second entity" in {
      for {
        _ <- persistence.update(doc2Updated)
        e2 <- persistence.read(entity2.id)
        e3 <- persistence.read(entity3.id)
        ids <- persistence.readAllIds()
      } yield {
        ids shouldBe Set(entity2.id, entity3.id)
        e2 shouldBe doc2Updated
        e3 shouldBe entity3
      }
    }

    it should "fail the future with any Exception, when updating a non-existent entity" in {
      recoverToSucceededIf[Exception] {
        persistence.update(entity1)
      }
    }

    it should "delete the second and third entity" in {
      for {
        _ <- persistence.delete(entity2.id)
        _ <- persistence.delete(entity3.id)
        ids <- persistence.readAllIds()
      } yield {
        ids shouldBe Set.empty
      }
    }

    it should "create all entities again" in {
      for {
        _ <- persistence.create(entity1)
        _ <- persistence.create(doc2Updated)
        _ <- persistence.create(entity3)
        ids <- persistence.readAllIds()
      } yield {
        ids shouldBe Set(entity1.id, entity2.id, entity3.id)
      }
    }

  }

}
