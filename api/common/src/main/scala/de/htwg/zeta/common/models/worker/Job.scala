package de.htwg.zeta.common.models.worker

import java.util.Objects
import java.util.UUID

import julienrf.json.derived
import play.api.libs.json.__
import play.api.libs.json.OFormat

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
   * it's execution we need to stop the childs as well
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
  implicit val formatJob: OFormat[Job] = derived.flat.oformat((__ \ "type").format[String])
}
