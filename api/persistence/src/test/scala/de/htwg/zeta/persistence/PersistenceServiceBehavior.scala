package de.htwg.zeta.persistence

import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.PersistenceService
import models.User
import models.document.Document
import models.document.Log
import models.document.ModelEntity
import models.document.PasswordInfoEntity
import models.document.UserEntity
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.Model
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait PersistenceServiceBehavior extends AsyncFlatSpec with Matchers {

  /** TODO add tests for this case classes
  case class EventDrivenTask(_id: String, _rev: String, name: String, generator: String, filter: String, event: String)

  case class BondedTask(_id: String, _rev: String, name: String, generator: String, filter: String, menu: String, item: String)

  case class Generator(_id: String, _rev: String, name: String, image: String)

  case class Filter(_id: String, _rev: String, name: String, description: String, instances: List[String])

  case class GeneratorImage(_id: String, _rev: String, name: String, dockerImage: String)

  case class FilterImage(_id: String, _rev: String, name: String, dockerImage: String)

  case class Settings(_id: String, _rev: String, owner: String, jobSettings: JobSettings)

  case class MetaModelEntity(_id: String, _rev: String, name: String, metaModel: MetaModel, dsl: Dsl, links: Option[Seq[HLink]] = None)

  case class MetaModelRelease(_id: String, _rev: String, name: String, metaModel: MetaModel, dsl: Dsl, version: String) */


  private val modelEntity1 = ModelEntity(
    _id = "modelEntityId1",
    _rev = "modelEntityRev",
    model = Model(
      name = "modelEntity1.model.name",
      metaModel = MetaModel(
        name = "modelEntity1.model.metaModel.name",
        elements = Map.empty,
        uiState = "modelEntity1.model.metaModel.uiState"
      ),
      elements = Map.empty,
      uiState = "modelEntity.model.uiState"

    ),
    metaModelId = "metaModelId",
    links = None
  )
  private val modelEntity2: ModelEntity = modelEntity1.copy(_id = "modelEntityId2")
  private val modelEntity3: ModelEntity = modelEntity1.copy(_id = "modelEntityId3")
  private val modelEntity2Updated: ModelEntity = modelEntity2.copy(metaModelId = "metaModelIdUpdated")

  private val log1 = Log(
    _id = "logId1",
    _rev = "logRev",
    log = "logMsg1",
    status = 1,
    date = "date"
  )
  private val log2: Log = log1.copy(_id = "logId2")
  private val log3: Log = log1.copy(_id = "logId3")
  private val log2Updated: Log = log2.copy(status = 2)

  private val pwInfoEntity1 = PasswordInfoEntity(
    _id = "pwInfoEntity1Id1",
    _rev = "pwInfoEntity1Rev",
    passwordInfo = PasswordInfo(
      hasher = "hash1",
      password = "pw1",
      salt = Some("salt1")
    )
  )
  private val pwInfoEntity2: PasswordInfoEntity = pwInfoEntity1.copy(_id = "pwInfoEntity1Id2")
  private val pwInfoEntity3: PasswordInfoEntity = pwInfoEntity1.copy(_id = "pwInfoEntity1Id3")
  private val pwInfoEntity2Updated: PasswordInfoEntity = pwInfoEntity2.copy(passwordInfo = pwInfoEntity2.passwordInfo.copy(salt = None))

  private val userEntity1 = UserEntity(
    _id = "userEntity1Id1",
    _rev = "userEntity1Rev",
    user = User(
      userID = UUID.randomUUID(),
      loginInfo = LoginInfo("provId", "provKey"),
      firstName = Some("FirstName"),
      lastName = Some("LastName"),
      fullName = None,
      email = None,
      avatarURL = Some("avatarUrl"),
      activated = true
    )
  )
  private val userEntity2: UserEntity = userEntity1.copy(_id = "userEntity1Id2")
  private val userEntity3: UserEntity = userEntity1.copy(_id = "userEntity1Rev3")
  private val userEntity2Updated: UserEntity = userEntity2.copy(user = userEntity2.user.copy(fullName = Some("FullName")))

  /** Behavior for a PersistenceService.
   *
   * @param service PersistenceService
   */
  def serviceBehavior(service: PersistenceService): Unit = {

    "ModelEntity" should behave like docBehavior[ModelEntity](
      service.modelEntity, modelEntity1, modelEntity2, modelEntity3, modelEntity2Updated
    )

    "Log" should behave like docBehavior[Log](
      service.log, log1, log2, log3, log2Updated
    )

    "PasswordInfoEntity" should behave like docBehavior[PasswordInfoEntity](
      service.passwordInfoEntity, pwInfoEntity1, pwInfoEntity2, pwInfoEntity3, pwInfoEntity2Updated
    )

    "UserEntity" should behave like docBehavior[UserEntity](
      service.userEntity, userEntity1, userEntity2, userEntity3, userEntity2Updated
    )

  }

  private def docBehavior[T <: Document](persistence: Persistence[T], doc1: T, doc2: T, doc3: T, doc2Updated: T): Unit = { // scalastyle:ignore
    doc1._id shouldNot be(doc2._id)
    doc1._id shouldNot be(doc3._id)
    doc2._id shouldNot be(doc3._id)
    doc2._id shouldBe doc2Updated._id
    doc2 shouldNot be(doc2Updated)

    it should "remove all already existing documents" in {
      persistence.readAllIds.flatMap { ids =>
        Future.sequence(ids.map(id => persistence.delete(id))).flatMap { _ =>
          persistence.readAllIds.flatMap { ids =>
            ids shouldBe Seq.empty
          }
        }
      }
    }

    it should "create a document" in {
      persistence.create(doc1).flatMap { _ =>
        persistence.readAllIds.flatMap { ids =>
          ids shouldBe Seq(doc1._id)
        }
      }
    }

    it should "create a second document" in {
      persistence.create(doc2).flatMap { _ =>
        persistence.readAllIds.flatMap { ids =>
          ids.size shouldBe 2
          ids should contain(doc1._id)
          ids should contain(doc2._id)
        }
      }
    }

    it should "create a third document" in {
      persistence.create(doc3).flatMap { _ =>
        persistence.readAllIds.flatMap { ids =>
          ids.size shouldBe 3
          ids should contain(doc1._id)
          ids should contain(doc2._id)
          ids should contain(doc3._id)
        }
      }
    }

    it should "fail the future with any Exception, when creating an already existing document" in {
      recoverToSucceededIf[Exception] {
        persistence.create(doc2)
      }
    }

    it should "read the first, second and third document" in {
      persistence.read(doc1._id).flatMap { d1 =>
        persistence.read(doc2._id).flatMap { d2 =>
          persistence.read(doc3._id).flatMap { d3 =>
            d1 shouldBe doc1
            d2 shouldBe doc2
            d3 shouldBe doc3
          }
        }
      }
    }

    it should "fail the future with any Exception, when loading a non-existent document" in {
      recoverToSucceededIf[Exception] {
        persistence.read("id4")
      }
    }

    it should "delete the first document" in {
      persistence.delete(doc1._id).flatMap { _ =>
        persistence.readAllIds.flatMap { ids =>
          ids.size shouldBe 2
          ids should contain(doc2._id)
          ids should contain(doc3._id)
        }
      }
    }

    it should "fail the future with any Exception, when deleting a non-existent document" in {
      recoverToSucceededIf[Exception] {
        persistence.delete(doc1._id)
      }
    }

    it should "update the second document" in {
      persistence.update(doc2Updated).flatMap { _ =>
        persistence.read(doc2._id).flatMap { d2 =>
          persistence.read(doc3._id).flatMap { d3 =>
            persistence.readAllIds.flatMap { ids =>
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
      persistence.delete(doc2._id).flatMap { _ =>
        persistence.delete(doc3._id).flatMap { _ =>
          persistence.readAllIds.flatMap { ids =>
            ids shouldBe Seq.empty
          }
        }
      }
    }

  }

}
