package de.htwg.zeta.persistence.behavior

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.persistence.general.Repository
import models.file.File
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait FilePersistenceBehavior extends AsyncFlatSpec with Matchers {

  private val file1 = File(UUID.randomUUID, "file1", "content1")
  private val file2 = File(file1.id, "file2", "content2")
  private val file2Updated: File = file2.copy(content = "content2Updated")
  private val file3 = File(UUID.randomUUID, "file3", "content3")


  /** Behavior for a PersistenceService.
   *
   * @param repo PersistenceService
   */
  def filePersistenceBehavior(repo: Repository): Unit = { // scalastyle:ignore

    it should "remove all already existing files" in {
      repo.files.readAllIds().flatMap { entries =>
        Future.sequence(entries.flatMap { case (id, names) => names.map(name => repo.files.delete(id, name)) }).flatMap { _ =>
          repo.files.readAllIds().flatMap { keys =>
            keys shouldBe Map.empty
          }
        }
      }
    }

    it should "create a file" in {
      repo.files.create(file1).flatMap { _ =>
        repo.files.readAllIds().flatMap { keys =>
          keys shouldBe Map(file1.id -> Set(file1.name))
        }
      }
    }

    it should "create a second file" in {
      repo.files.create(file2).flatMap { _ =>
        repo.files.readAllIds().flatMap { keys =>
          keys shouldBe Map(file1.id -> Set(file1.name, file2.name))
        }
      }
    }

    it should "create a third file" in {
      repo.files.create(file3).flatMap { _ =>
        repo.files.readAllIds().flatMap { keys =>
          keys shouldBe Map(file1.id -> Set(file1.name, file2.name), file3.id -> Set(file3.name))
        }
      }
    }

    it should "fail the future with any Exception, when creating an already existing file" in {
      recoverToSucceededIf[Exception] {
        repo.files.create(file2)
      }
    }

    it should "read the first, second and third file" in {
      repo.files.read(file1.id, file1.name).flatMap { d1 =>
        repo.files.read(file2.id, file2.name).flatMap { d2 =>
          repo.files.read(file3.id, file3.name).flatMap { d3 =>
            d1 shouldBe file1
            d2 shouldBe file2
            d3 shouldBe file3
          }
        }
      }
    }

    it should "fail the future with any Exception, when reading a non-existent file" in {
      recoverToSucceededIf[Exception] {
        repo.files.read(UUID.randomUUID, "notExisting")
      }
    }

    it should "delete the first file" in {
      repo.files.delete(file1.id, file1.name).flatMap { _ =>
        repo.files.readAllIds().flatMap { keys =>
          keys shouldBe Map(file2.id -> Set(file2.name), file3.id -> Set(file3.name))
        }
      }
    }

    it should "fail the future with any Exception, when deleting a non-existent file" in {
      recoverToSucceededIf[Exception] {
        repo.files.delete(file1.id, file1.name)
      }
    }

    it should "update the second file" in {
      repo.files.update(file2Updated).flatMap { _ =>
        repo.files.read(file2.id, file2.name).flatMap { d2 =>
          repo.files.read(file3.id, file3.name).flatMap { d3 =>
            repo.files.readAllIds().flatMap { keys =>
              keys shouldBe Map(file2.id -> Set(file2.name), file3.id -> Set(file3.name))
              d2 shouldBe file2Updated
              d3 shouldBe file3
            }
          }
        }
      }
    }

    it should "fail the future with any Exception, when updating a non-existent file" in {
      recoverToSucceededIf[Exception] {
        repo.files.update(file1)
      }
    }

    it should "delete the second and third file" in {
      repo.files.delete(file2.id, file2.name).flatMap { _ =>
        repo.files.delete(file3.id, file3.name).flatMap { _ =>
          repo.files.readAllIds().flatMap { keys =>
            keys shouldBe Map.empty
          }
        }
      }
    }

    it should "it should create all files again" in {
      repo.files.create(file1).flatMap { _ =>
        repo.files.create(file2Updated).flatMap { _ =>
          repo.files.create(file3).flatMap { _ =>
            repo.files.readAllIds().flatMap { keys =>
              keys shouldBe Map(file1.id -> Set(file1.name, file2.name), file3.id -> Set(file3.name))
            }
          }
        }
      }
    }

  }


}
