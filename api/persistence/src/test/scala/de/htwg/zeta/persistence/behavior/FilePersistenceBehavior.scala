package de.htwg.zeta.persistence.behavior

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.persistence.general.Repository
import models.entity.File
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait FilePersistenceBehavior extends AsyncFlatSpec with Matchers {

  private val file1 = File(UUID.randomUUID, "file1", "content1")
  private val file2 = File(file1.id, "file2", "content2")
  private val file2Updated: File = file2.copy(content = "content2Updated")
  private val file3 = File(UUID.randomUUID, "file3", "content3")

  def filePersistenceBehavior(repo: Repository): Unit = { // scalastyle:ignore

    it should "remove all already existing files" in {
      for {
        existingKeys <- repo.files.readAllKeys()
        _ <- Future.sequence(existingKeys.flatMap { case (id, names) => names.map(name => repo.files.delete(id, name)) })
        keys <- repo.files.readAllKeys()
      } yield {
        keys shouldBe Map.empty
      }
    }

    it should "create a file" in {
      for {
        _ <- repo.files.create(file1)
        keys <- repo.files.readAllKeys()
      } yield {
        keys shouldBe Map(file1.id -> Set(file1.name))
      }
    }

    it should "create a second file" in {
      for {
        _ <- repo.files.create(file2)
        keys <- repo.files.readAllKeys()
      } yield {
        keys shouldBe Map(file1.id -> Set(file1.name, file2.name))
      }
    }

    it should "create a third file" in {
      for {
        _ <- repo.files.create(file3)
        keys <- repo.files.readAllKeys()
      } yield {
        keys shouldBe Map(file1.id -> Set(file1.name, file2.name), file3.id -> Set(file3.name))
      }
    }

    it should "fail the future with any Exception, when creating an already existing file" in {
      recoverToSucceededIf[Exception] {
        repo.files.create(file2)
      }
    }

    it should "read the first, second and third file" in {
      for {
        f1 <- repo.files.read(file1.id, file1.name)
        f2 <- repo.files.read(file2.id, file2.name)
        f3 <- repo.files.read(file3.id, file3.name)
      } yield {
        f1 shouldBe file1
        f2 shouldBe file2
        f3 shouldBe file3
      }
    }

    it should "fail the future with any Exception, when reading a non-existent file" in {
      recoverToSucceededIf[Exception] {
        repo.files.read(UUID.randomUUID, "notExisting")
      }
    }

    it should "delete the first file" in {
      for {
        _ <- repo.files.delete(file1.id, file1.name)
        keys <- repo.files.readAllKeys()
      } yield {
        keys shouldBe Map(file2.id -> Set(file2.name), file3.id -> Set(file3.name))
      }
    }

    it should "fail the future with any Exception, when deleting a non-existent file" in {
      recoverToSucceededIf[Exception] {
        repo.files.delete(file1.id, file1.name)
      }
    }

    it should "update the second file" in {
      for {
        _ <- repo.files.update(file2Updated)
        f2 <- repo.files.read(file2.id, file2.name)
        f3 <- repo.files.read(file3.id, file3.name)
        keys <- repo.files.readAllKeys()
      } yield {
        keys shouldBe Map(file2.id -> Set(file2.name), file3.id -> Set(file3.name))
        f2 shouldBe file2Updated
        f3 shouldBe file3
      }
    }

    it should "fail the future with any Exception, when updating a non-existent file" in {
      recoverToSucceededIf[Exception] {
        repo.files.update(file1)
      }
    }

    it should "delete the second and third file" in {
      for {
        _ <- repo.files.delete(file2.id, file2.name)
        _ <- repo.files.delete(file3.id, file3.name)
        keys <- repo.files.readAllKeys()
      } yield {
        keys shouldBe Map.empty
      }
    }

    it should "create all files again" in {
      for {
        _ <- repo.files.create(file1)
        _ <- repo.files.create(file2Updated)
        _ <- repo.files.create(file3)
        keys <- repo.files.readAllKeys()
      } yield {
        keys shouldBe Map(file1.id -> Set(file1.name, file2.name), file3.id -> Set(file3.name))
      }
    }

  }


}
