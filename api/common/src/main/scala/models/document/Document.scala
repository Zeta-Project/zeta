package models.document

import java.util.UUID

import com.mohiva.play.silhouette.api.util.PasswordInfo
import julienrf.json.derived
import models.Entity
import models.modelDefinitions.helper.HLink
import models.modelDefinitions.metaModel.Dsl
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.Model
import models.worker.Job
import models.worker.RunBondedTask
import models.worker.RunEventDrivenTask
import models.worker.RunTimedTask
import org.joda.time.DateTime
import org.joda.time.Minutes
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads


case class TimedTask(
    id: UUID,
    name: String,
    generatorId: UUID,
    filterId: UUID,
    interval: Int,
    start: String
) extends Entity {

  def delay: Int = {
    val first = DateTime.parse(start)
    val now = new DateTime()
    val diff = Minutes.minutesBetween(now, first).getMinutes
    if (diff > 0) diff else 1
  }

}

case class EventDrivenTask(
    id: UUID = UUID.randomUUID,
    name: String,
    generatorId: UUID,
    filterId: UUID,
    event: String
) extends Entity

case class BondedTask(
    id: UUID = UUID.randomUUID,
    name: String,
    generatorId: UUID,
    filterId: UUID,
    menu: String,
    item: String
) extends Entity

case class Generator(
    id: UUID = UUID.randomUUID,
    name: String,
    imageId: UUID
) extends Entity

case class Filter(
    id: UUID = UUID.randomUUID,
    name: String,
    description: String,
    instanceIds: List[UUID]
) extends Entity

case class GeneratorImage(
    id: UUID = UUID.randomUUID,
    name: String,
    dockerImage: String
) extends Entity

case class FilterImage(
    id: UUID = UUID.randomUUID,
    name: String,
    dockerImage: String
) extends Entity


case class Settings(
    id: UUID = UUID.randomUUID,
    owner: UUID,
    jobSettings: JobSettings
) extends Entity

case class MetaModelEntity(
    id: UUID = UUID.randomUUID,
    rev: String,
    name: String,
    metaModel: MetaModel,
    dsl: Dsl = Dsl(),
    links: Option[Seq[HLink]] = None
) extends Entity

case class MetaModelRelease(
    id: UUID = UUID.randomUUID,
    name: String,
    metaModel: MetaModel,
    dsl: Dsl,
    version: String
) extends Entity

case class ModelEntity(
    id: UUID = UUID.randomUUID,
    model: Model,
    metaModelId: UUID,
    links: Option[Seq[HLink]] = None
) extends Entity

case class Log(
    id: UUID = UUID.randomUUID,
    task: String,
    log: String,
    status: Int,
    date: String
) extends Entity


object Log {

  def apply(job: Job, log: String, status: Int): Log = {
    val now = new DateTime().toDateTimeISO.toString
    val prefix = "Log"
    val task = job match {
      case job: RunEventDrivenTask =>
        prefix + job.taskId.toString + " - " + now
      case job: RunTimedTask =>
        prefix + job.taskId.toString + " - " + now
      case job: RunBondedTask =>
        prefix + job.taskId.toString + " - " + now
      case _ => throw new IllegalArgumentException(s"Creating Log(..) Object failed. Job type '${job.getClass.getName}' cannot be persisted.")
    }
    Log(UUID.randomUUID, task, log, status, now)
  }

}

object Document {
  implicit val formatPasswordInfo: OFormat[PasswordInfo] = derived.oformat
  implicit val readJobSettings: Reads[JobSettings] = Json.reads[JobSettings]
  implicit val metaModelFormat: OFormat[MetaModelEntity] = Json.format[MetaModelEntity]
  // implicit val modelFormat: OFormat[ModelEntity] = Json.format[ModelEntity]
}
