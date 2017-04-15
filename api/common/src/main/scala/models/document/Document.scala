package models.document

import java.util.Objects

import com.mohiva.play.silhouette.api.util.PasswordInfo
import julienrf.json.derived
import models.User
import models.modelDefinitions.helper.HLink
import models.modelDefinitions.metaModel.Dsl
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.Model
import models.worker._
import org.joda.time._
import play.api.libs.json._

sealed trait Document {
  val _id: String
  val _rev: String

  def id() = _id

  def isUpdated(): Boolean = !_rev.startsWith("1-")

  override def equals(that: Any): Boolean =
    that match {
      case that: Document => this.isInstanceOf[Document] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode(_id)
}

sealed trait Task extends Document
sealed trait Image extends Document {
  def dockerImage: String
}

sealed trait Entity extends Document

case class TimedTask(_id: String, _rev: String, name: String, generator: String, filter: String, interval: Int, start: String) extends Task {
  def delay: Int = {
    val first = DateTime.parse(start)
    val now = new DateTime()
    val diff = Minutes.minutesBetween(now, first).getMinutes
    if (diff > 0) diff else 1
  }
}
case class EventDrivenTask(_id: String, _rev: String, name: String, generator: String, filter: String, event: String) extends Task
case class BondedTask(_id: String, _rev: String, name: String, generator: String, filter: String, menu: String, item: String) extends Task
case class Generator(_id: String, _rev: String, name: String, image: String) extends Document
case class Filter(_id: String, _rev: String, name: String, description: String, instances: List[String]) extends Document
case class GeneratorImage(_id: String, _rev: String, name: String, dockerImage: String) extends Image
case class FilterImage(_id: String, _rev: String, name: String, dockerImage: String) extends Image
case class Settings(_id: String, _rev: String, owner: String, jobSettings: JobSettings) extends Document
case class MetaModelEntity(_id: String, _rev: String, name: String, metaModel: MetaModel, dsl: Dsl, links: Option[Seq[HLink]] = None) extends Entity
case class MetaModelRelease(_id: String, _rev: String, name: String, metaModel: MetaModel, dsl: Dsl, version: String) extends Document
case class ModelEntity(_id: String, _rev: String, model: Model, metaModelId: String, links: Option[Seq[HLink]] = None) extends Entity
case class Log(_id: String, _rev: String, log: String, status: Int, date: String) extends Document
case class PasswordInfoEntity(_id: String, _rev: String, passwordInfo: PasswordInfo) extends Entity
case class UserEntity(_id: String, _rev: String, user: User) extends Entity

object Settings {
  def apply(owner: String): Settings = {
    val id = s"Settings-${owner}"
    Settings(id, null, owner, JobSettings.default())
  }
}

/**
 * Job settings for a single user
 *
 * @param maxRunning The maximum number of running jobs
 * @param maxPending The maximum number of pending jobs
 */
case class JobSettings(maxRunning: Int, maxPending: Int, docker: DockerSettings)

case class DockerSettings(cpuShares: Long = 0, cpuQuota: Long = 0)

object DockerSettings {
  implicit lazy val formatDockerSettings: OFormat[DockerSettings] = derived.oformat
}

/**
 * Setup default job settings
 */
object JobSettings {
  implicit lazy val formatJobSettings: OFormat[JobSettings] = derived.oformat

  def default(): JobSettings = JobSettings(10, 100, DockerSettings())
}

case class ContainerSettings()

private case object Helper {
  def random(): String = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16)
}

object UserEntity {
  def apply(owner: String, user: User): UserEntity = {
    val id = s"UserEntity-${owner}"
    UserEntity(id, null, user)
  }
}

object PasswordInfoEntity {
  implicit lazy val formatPasswordInfo: OFormat[PasswordInfo] = derived.oformat

  def apply(owner: String, info: PasswordInfo): PasswordInfoEntity = {
    val id = s"PasswordInfoEntity-${owner}"
    PasswordInfoEntity(id, null, info)
  }
}

object Generator {
  def apply(owner: String, name: String, image: GeneratorImage): Generator = {
    val id = s"Generator-${owner}-${Helper.random()}"
    Generator(id, null, name, image.id())
  }
}

object MetaModelRelease {
  def apply(from: MetaModelEntity, version: String): MetaModelRelease = {
    val id = s"${from._id.replace("MetaModel", "MetaModelRelease")}-${version}"
    val name = s"${from.name} ${version}"
    val meta = from.metaModel
    MetaModelRelease(id, null, name, meta, from.dsl, version)
  }
}

object MetaModelEntity {
  def apply(owner: String, metaModel: MetaModel): MetaModelEntity = {
    val id = s"MetaModelEntity-${owner}-${Helper.random()}"
    MetaModelEntity(id, null, metaModel.name, metaModel, Dsl(), None)
  }
}

object ModelEntity {
  def apply(owner: String, model: Model, release: MetaModelEntity): ModelEntity = {
    val id = s"ModelEntity-${owner}-${Helper.random()}"
    val entity = ModelEntity(id, null, model, release.id, None)
    entity
  }
}

object Log {
  def apply(job: Job, log: String, status: Int): Log = {
    val now = new DateTime().toDateTimeISO.toString
    val prefix = "Log"
    val id = job match {
      case job: RunEventDrivenTask =>
        prefix + job.task.split("-").tail.mkString("-", "-", "-") + now
      case job: RunTimedTask =>
        prefix + job.task.split("-").tail.mkString("-", "-", "-") + now
      case job: RunBondedTask =>
        prefix + job.task.split("-").tail.mkString("-", "-", "-") + now
      case _ => throw new IllegalArgumentException(s"Creating Log(..) Object failed. Job type '${job.getClass.getName}' cannot be persisted.")
    }
    Log(id, null, log, status, now)
  }
}

object Document {
  implicit lazy val formatPasswordInfo: OFormat[PasswordInfo] = derived.oformat
  implicit lazy val readJobSettings = Json.reads[JobSettings]
  implicit lazy val formatDocument: OFormat[Document] = derived.flat.oformat((__ \ "type").format[String])

  def update(doc: Document, rev: String): Document = doc match {
    case t: Task => updateTask(rev, t)
    case i: Image => updateImage(rev, i)
    case e: Entity => updateEntity(rev, e)
    case d: Generator => d.copy(_rev = rev)
    case d: Filter => d.copy(_rev = rev)
    case d: Settings => d.copy(_rev = rev)
    case d: MetaModelRelease => d.copy(_rev = rev)
    case d: Log => d.copy(_rev = rev)
  }

  private def updateEntity(rev: String, e: Entity) = {
    e match {
      case d: MetaModelEntity => d.copy(_rev = rev)
      case d: ModelEntity => d.copy(_rev = rev)
      case d: PasswordInfoEntity => d.copy(_rev = rev)
      case d: UserEntity => d.copy(_rev = rev)
    }
  }

  private def updateImage(rev: String, i: Image) = {
    i match {
      case d: GeneratorImage => d.copy(_rev = rev)
      case d: FilterImage => d.copy(_rev = rev)
    }
  }

  private def updateTask(rev: String, t: Task) = {
    t match {
      case d: TimedTask => d.copy(_rev = rev)
      case d: EventDrivenTask => d.copy(_rev = rev)
      case d: BondedTask => d.copy(_rev = rev)
    }
  }
}

// Document Change types
sealed trait Change
case object Created extends Change
case object Updated extends Change
case object Deleted extends Change
//
case class Changed(doc: Document, change: Change)
