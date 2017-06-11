package de.htwg.zeta.persistence

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.Repository
import models.Entity
import models.User
import models.document.Log
import models.document.ModelEntity
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.Model
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait RepositoryBehavior extends AsyncFlatSpec with Matchers {

  /** TODO add tests for this case classes
   * case class EventDrivenTask(_id: String, _rev: String, name: String, generator: String, filter: String, event: String)
   * *
   * case class BondedTask(_id: String, _rev: String, name: String, generator: String, filter: String, menu: String, item: String)
   * *
   * case class Generator(_id: String, _rev: String, name: String, image: String)
   * *
   * case class Filter(_id: String, _rev: String, name: String, description: String, instances: List[String])
   * *
   * case class GeneratorImage(_id: String, _rev: String, name: String, dockerImage: String)
   * *
   * case class FilterImage(_id: String, _rev: String, name: String, dockerImage: String)
   * *
   * case class Settings(_id: String, _rev: String, owner: String, jobSettings: JobSettings)
   * *
   * case class MetaModelEntity(_id: String, _rev: String, name: String, metaModel: MetaModel, dsl: Dsl, links: Option[Seq[HLink]] = None)
   * *
   * case class MetaModelRelease(_id: String, _rev: String, name: String, metaModel: MetaModel, dsl: Dsl, version: String) */


  private val modelEntity1 = ModelEntity(id = UUID.randomUUID, model = Model(
    name = "modelEntity1.model.name",
    metaModel = MetaModel(
      name = "modelEntity1.model.metaModel.name",
      classes = Map.empty,
      references = Map.empty,
      enums = Map.empty,
      uiState = "modelEntity1.model.metaModel.uiState"
    ),
    nodes = Map.empty,
    edges = Map.empty,
    uiState = "modelEntity.model.uiState"

  ), metaModelId = UUID.randomUUID, links = None)
  private val modelEntity2: ModelEntity = modelEntity1.copy(id = UUID.randomUUID)
  private val modelEntity3: ModelEntity = modelEntity1.copy(id = UUID.randomUUID)
  private val modelEntity2Updated: ModelEntity = modelEntity2.copy(metaModelId = UUID.randomUUID)



  private val log1 = Log(
    id = UUID.randomUUID,
    task = "task",
    log = "logMsg1",
    status = 1,
    date = "date"
  )
  private val log2: Log = log1.copy(id =  UUID.randomUUID)
  private val log3: Log = log1.copy(id =  UUID.randomUUID)
  private val log2Updated: Log = log2.copy(status = 2)

  private val user1 = User(
    id = UUID.randomUUID,
    firstName = "firstName",
    lastName = "lastName",
    email = "test@mail.com",
    activated = false
  )
  private val user2: User = user1.copy(id =  UUID.randomUUID)
  private val user3: User = user1.copy(id =  UUID.randomUUID)
  private val user2Updated: User = user2.copy(activated = true)


  /** Behavior for a PersistenceService.
   *
   * @param service PersistenceService
   */
  def serviceBehavior(service: Repository): Unit = {

    /*
    "ModelEntity" should behave like docBehavior[ModelEntity](
      service.modelEntities, modelEntity1, modelEntity2, modelEntity3, modelEntity2Updated
    )

    "Log" should behave like docBehavior[Log](
      service.logs, log1, log2, log3, log2Updated
    ) */

    "User" should behave like docBehavior[User](
      service.users, user1, user2, user3, user2Updated
    )

  }

  private def docBehavior[T <: Entity](persistence: Persistence[T], doc1: T, doc2: T, doc3: T, doc2Updated: T): Unit = { // scalastyle:ignore
    doc1.id shouldNot be(doc2.id)
    doc1.id shouldNot be(doc3.id)
    doc2.id shouldNot be(doc3.id)
    doc2.id shouldBe doc2Updated.id
    doc2 shouldNot be(doc2Updated)

    it should "remove all already existing documents" in {
      persistence.readAllIds().flatMap { ids =>
        Future.sequence(ids.map(id => persistence.delete(id))).flatMap { _ =>
          persistence.readAllIds().flatMap { ids =>
            ids shouldBe Set.empty
          }
        }
      }
    }

    it should "create a document" in {
      persistence.create(doc1).flatMap { _ =>
        persistence.readAllIds().flatMap { ids =>
          ids shouldBe Set(doc1.id)
        }
      }
    }

    it should "create a second document" in {
      persistence.create(doc2).flatMap { _ =>
        persistence.readAllIds().flatMap { ids =>
          ids.size shouldBe 2
          ids should contain(doc1.id)
          ids should contain(doc2.id)
        }
      }
    }

    it should "create a third document" in {
      persistence.create(doc3).flatMap { _ =>
        persistence.readAllIds().flatMap { ids =>
          ids.size shouldBe 3
          ids should contain(doc1.id)
          ids should contain(doc2.id)
          ids should contain(doc3.id)
        }
      }
    }

    it should "fail the future with any Exception, when creating an already existing document" in {
      recoverToSucceededIf[Exception] {
        persistence.create(doc2)
      }
    }

    it should "read the first, second and third document" in {
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

    it should "fail the future with any Exception, when reading a non-existent document" in {
      recoverToSucceededIf[Exception] {
        persistence.read(UUID.randomUUID)
      }
    }

    it should "delete the first document" in {
      persistence.delete(doc1.id).flatMap { _ =>
        persistence.readAllIds().flatMap { ids =>
          ids.size shouldBe 2
          ids should contain(doc2.id)
          ids should contain(doc3.id)
        }
      }
    }

    it should "fail the future with any Exception, when deleting a non-existent document" in {
      recoverToSucceededIf[Exception] {
        persistence.delete(doc1.id)
      }
    }

    it should "update the second document" in {
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

    it should "fail the future with any Exception, when updating a non-existent document" in {
      recoverToSucceededIf[Exception] {
        persistence.update(doc1)
      }
    }

    it should "delete the second and third document" in {
      persistence.delete(doc2.id).flatMap { _ =>
        persistence.delete(doc3.id).flatMap { _ =>
          persistence.readAllIds().flatMap { ids =>
            ids shouldBe Set.empty
          }
        }
      }
    }

  }

}
