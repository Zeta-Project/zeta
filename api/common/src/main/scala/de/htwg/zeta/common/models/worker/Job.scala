package de.htwg.zeta.common.models.worker

import java.util.Objects
import java.util.UUID

import play.api.libs.json.Json
import play.api.libs.json.Format
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult

sealed trait Job {
  /**
   * Stream the log of the container
   */
  val stream: Boolean = false

  /**
   * Persist the log of the container
   */
  val persist: Boolean = false

  /**
   * The docker image which to use
   */
  val image: String

  /**
   * The command parameters to pass to the docker container
   */
  val cmd: List[String]

  /**
   * The id of the parent work object.
   *
   * E.g. if a generator was called by another generator we pass the parent id. If the parent stop
   * it's execution we need to stop the children as well
   *
   */
  val parent: Option[String] = None
}

/**
 * A Generator which was called from a generator
 */
case class RunGeneratorFromGeneratorJob(parentId: String, key: String, generatorId: UUID, image: String, options: String) extends Job {

  override def equals(that: Any): Boolean =
    that match {
      case that: RunGeneratorFromGeneratorJob => this.isInstanceOf[RunGeneratorFromGeneratorJob] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode("RunGeneratorFromGeneratorJob-${parentId}-${secret}")

  override val cmd: List[String] = List("--key", key, "--parent", parentId, "--generator", generatorId.toString, "--options", options)

  override val parent: Option[String] = Some(parentId)
}

/**
 * This section contain all types of jobs which can be scheduled by the system or a user.
 */
case class RunFilterManually(filterId: UUID) extends Job {
  override val stream: Boolean = true

  override def equals(that: Any): Boolean =
    that match {
      case that: RunFilterManually => this.isInstanceOf[RunFilterManually] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode(s"RunFilter-${filterId}")

  override val image: String = "modigen/filter/scala:0.1"

  override val cmd: List[String] = List("--filter", filterId.toString)
}

case class RunGeneratorManually(generatorId: UUID, image: String, filterId: UUID) extends Job {
  override val stream: Boolean = true

  override def equals(that: Any): Boolean =
    that match {
      case that: RunGeneratorManually => this.isInstanceOf[RunGeneratorManually] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode(s"RunGenerator-${generatorId}-${filterId}-${image}")

  override val cmd: List[String] = List("--filter", filterId.toString, "--generator", generatorId.toString)
}

case class CreateGeneratorJob(image: String, imageId: UUID, options: String) extends Job {
  override val stream: Boolean = true

  override def equals(that: Any): Boolean =
    that match {
      case that: CreateGeneratorJob => this.isInstanceOf[CreateGeneratorJob] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode("CreateGeneratorJob" + image.hashCode)

  override val cmd: List[String] = List("--create", options, "--image", imageId.toString)
}

case class RunBondedTask(taskId: UUID, generatorId: UUID, filterId: UUID, modelId: UUID, image: String) extends Job {
  override val persist: Boolean = true

  override def equals(that: Any): Boolean =
    that match {
      case that: RunBondedTask => this.isInstanceOf[RunBondedTask] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode("RunBondedTask-${generator}-${model}")

  override val cmd: List[String] = List("--model", modelId.toString, "--filter", filterId.toString, "--generator", generatorId.toString)
}

case class RunEventDrivenTask(taskId: UUID, generatorId: UUID, filterId: UUID, modelId: UUID, image: String) extends Job {
  override val persist: Boolean = true

  override def equals(that: Any): Boolean =
    that match {
      case that: RunEventDrivenTask => this.isInstanceOf[RunEventDrivenTask] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode("RunEventDrivenTask-${generator}-${model}")

  override val cmd: List[String] = List("--model", modelId.toString, "--filter", filterId.toString, "--generator", generatorId.toString)
}

case class RunTimedTask(taskId: UUID, generatorId: UUID, filterId: UUID, image: String) extends Job {
  override val persist: Boolean = true

  override def equals(that: Any): Boolean =
    that match {
      case that: RunTimedTask => this.isInstanceOf[RunTimedTask] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode(s"RunTimedTask-$generatorId-$filterId")

  override val cmd: List[String] = List("--filter", filterId.toString, "--generator", generatorId.toString)
}

case class CreateMetaModelReleaseJob(metaModelId: String) extends Job {
  override val stream: Boolean = true

  override def equals(that: Any): Boolean =
    that match {
      case that: CreateMetaModelReleaseJob => this.isInstanceOf[CreateMetaModelReleaseJob] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode("CreateMetaModelRelease" + metaModelId.hashCode)

  override val image: String = "modigen/metamodel/release:0.1"

  override val cmd: List[String] = List("--id", metaModelId)
}

case class RerunFilterJob(filterId: UUID) extends Job {
  override val persist: Boolean = false

  override def equals(that: Any): Boolean =
    that match {
      case that: RerunFilterJob => this.isInstanceOf[RerunFilterJob] && this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode: Int = Objects.hashCode("RerunFilterJob" + filterId)

  override val image: String = "modigen/filter/scala:0.1"

  override val cmd: List[String] = List("--filter", filterId.toString)
}

object Job {

  private val literalRunGeneratorFromGeneratorJob = "RunGeneratorFromGeneratorJob"
  private val literalRunFilterManually = "RunFilterManually"
  private val literalRunGeneratorManually = "RunGeneratorManually"
  private val literalCreateGeneratorJob = "CreateGeneratorJob"
  private val literalRunBondedTask = "RunBondedTask"
  private val literalRunEventDrivenTask = "RunEventDrivenTask"
  private val literalRunTimedTask = "RunTimedTask"
  private val literalCreateMetaModelReleaseJob = "CreateMetaModelReleaseJob"
  private val literalRerunFilterJob = "RerunFilterJob"
  private val typeLiteral = "type"

  private def transform[A](format: Format[A], name: String): Format[A] = {
    Format(format, format.transform(Writes[JsValue] {
      case jso: JsObject => JsObject((typeLiteral -> JsString(name)) +: jso.fields)
      case _ => throw new IllegalArgumentException(s"trying to update JsValue and add $typeLiteral")
    }))
  }


  private val formatRunGeneratorFromGeneratorJob = transform(Json.format[RunGeneratorFromGeneratorJob], literalRunGeneratorFromGeneratorJob)
  private val formatRunFilterManually = transform(Json.format[RunFilterManually], literalRunFilterManually)
  private val formatRunGeneratorManually = transform(Json.format[RunGeneratorManually], literalRunGeneratorManually)
  private val formatCreateGeneratorJob = transform(Json.format[CreateGeneratorJob], literalCreateGeneratorJob)
  private val formatRunBondedTask = transform(Json.format[RunBondedTask], literalRunBondedTask)
  private val formatRunEventDrivenTask = transform(Json.format[RunEventDrivenTask], literalRunEventDrivenTask)
  private val formatRunTimedTask = transform(Json.format[RunTimedTask], literalRunTimedTask)
  private val formatCreateMetaModelReleaseJob = transform(Json.format[CreateMetaModelReleaseJob], literalCreateMetaModelReleaseJob)
  private val formatRerunFilterJob = transform(Json.format[RerunFilterJob], literalRerunFilterJob)


  private def findReads(typeName: String): JsValue => JsResult[Job] = typeName match {   // scalastyle:ignore cyclomatic.complexity
    case this.literalRunGeneratorFromGeneratorJob => formatRunGeneratorFromGeneratorJob.reads
    case this.literalRunFilterManually => formatRunFilterManually.reads
    case this.literalRunGeneratorManually => formatRunGeneratorManually.reads
    case this.literalCreateGeneratorJob => formatCreateGeneratorJob.reads
    case this.literalRunBondedTask => formatRunBondedTask.reads
    case this.literalRunEventDrivenTask => formatRunEventDrivenTask.reads
    case this.literalRunTimedTask => formatRunTimedTask.reads
    case this.literalCreateMetaModelReleaseJob => formatCreateMetaModelReleaseJob.reads
    case this.literalRerunFilterJob => formatRerunFilterJob.reads
    case _ => throw new IllegalArgumentException(s"trying to read Job of $typeLiteral $typeName")
  }

  private val readJob = Reads(jsv => (jsv \ typeLiteral).validate[String].flatMap(findReads(_)(jsv)))

  private val writeJob = Writes[Job] {
    case job: RunGeneratorFromGeneratorJob => formatRunGeneratorFromGeneratorJob.writes(job)
    case job: RunFilterManually => formatRunFilterManually.writes(job)
    case job: RunGeneratorManually => formatRunGeneratorManually.writes(job)
    case job: CreateGeneratorJob => formatCreateGeneratorJob.writes(job)
    case job: RunBondedTask => formatRunBondedTask.writes(job)
    case job: RunEventDrivenTask => formatRunEventDrivenTask.writes(job)
    case job: RunTimedTask => formatRunTimedTask.writes(job)
    case job: CreateMetaModelReleaseJob => formatCreateMetaModelReleaseJob.writes(job)
    case job: RerunFilterJob => formatRerunFilterJob.writes(job)
  }

  implicit val formatJob: Format[Job] = Format(readJob, writeJob)
}
