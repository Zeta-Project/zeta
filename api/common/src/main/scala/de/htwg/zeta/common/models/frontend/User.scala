package de.htwg.zeta.common.models.frontend

import java.util.UUID

import play.api.libs.json.Format
import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue


/**
 * Representation of a change of the model.
 * E.g. Model Saved, Entity created etc..
 */
sealed trait ModelChanged extends UserRequest

sealed trait UserRequest extends Request {
  /**
   * The model from which the request was send
   */
  val modelId: UUID
}

case class ExecuteBondedTask(modelId: UUID, taskId: UUID) extends UserRequest

object ExecuteBondedTask {

  implicit val format: Format[ExecuteBondedTask] = new Format[ExecuteBondedTask] {

    override def writes(o: ExecuteBondedTask): JsValue = {
      Json.obj(
        "type" -> "ExecuteBondedTask",
        "model" -> o.modelId,
        "task" -> o.taskId
      )
    }

    override def reads(json: JsValue): JsResult[ExecuteBondedTask] = {
      for {
        modelId <- json.\("model").validate[UUID]
        taskId <- json.\("task").validate[UUID]
      } yield {
        ExecuteBondedTask(modelId, taskId)
      }
    }

  }

}

case class SavedModel(modelId: UUID) extends UserRequest with ModelChanged

object SavedModel {

  implicit val format: Format[SavedModel] = new Format[SavedModel] {

    override def writes(o: SavedModel): JsValue = {
      Json.obj(
        "type" -> "SavedModel",
        "model" -> o.modelId
      )
    }

    override def reads(json: JsValue): JsResult[SavedModel] = {
      for {
        modelId <- json.\("model").validate[UUID]
      } yield {
        SavedModel(modelId)
      }
    }

  }

}

object UserRequest {

  implicit val format: Format[UserRequest] = new Format[UserRequest] {

    override def writes(o: UserRequest): JsValue = {
      o match {
        case o: ExecuteBondedTask => ExecuteBondedTask.format.writes(o)
        case o: SavedModel => SavedModel.format.writes(o)
      }
    }

    override def reads(json: JsValue): JsResult[UserRequest] = {
      json.\("action").validate[String].flatMap {
        case "ExecuteBondedTask" => ExecuteBondedTask.format.reads(json)
        case "SavedModel" => SavedModel.format.reads(json)
      }
    }

  }

}

sealed trait UserResponse extends Response

case class BondedTaskNotExecutable(taskId: UUID, reason: String) extends UserResponse

object BondedTaskNotExecutable {

  implicit val format: Format[BondedTaskNotExecutable] = new Format[BondedTaskNotExecutable] {

    override def writes(o: BondedTaskNotExecutable): JsValue = {
      Json.obj(
        "type" -> "BondedTaskNotExecutable",
        "task" -> o.taskId,
        "reason" -> o.reason
      )
    }

    override def reads(json: JsValue): JsResult[BondedTaskNotExecutable] = {
      for {
        taskId <- json.\("task").validate[UUID]
        reason <- json.\("reason").validate[String]
      } yield {
        BondedTaskNotExecutable(taskId, reason)
      }
    }

  }

}

case class Entry(taskId: UUID, menu: String, item: String)

object Entry {

  implicit val format: Format[Entry] = new Format[Entry] {

    override def writes(o: Entry): JsValue = {
      Json.obj(
        "type" -> "Entry",
        "task" -> o.taskId,
        "menu" -> o.menu,
        "item" -> o.item
      )
    }

    override def reads(json: JsValue): JsResult[Entry] = {
      for {
        taskId <- json.\("task").validate[UUID]
        menu <- json.\("menu").validate[String]
        item <- json.\("item").validate[String]
      } yield {
        Entry(taskId, menu, item)
      }
    }

  }

}

case class BondedTaskList(tasks: List[Entry]) extends UserResponse

object BondedTaskList {

  implicit val format: Format[BondedTaskList] = new Format[BondedTaskList] {

    override def writes(o: BondedTaskList): JsValue = {
      Json.obj(
        "type" -> "Entry",
        "tasks" -> JsArray(o.tasks.map(Entry.format.writes))
      )
    }

    override def reads(json: JsValue): JsResult[BondedTaskList] = {
      for {
        tasks <- json.\("tasks").validate[List[Entry]]
      } yield {
        BondedTaskList(tasks)
      }
    }

  }

}

case class BondedTaskCompleted(taskId: UUID, status: Int) extends UserResponse

object BondedTaskCompleted {

  implicit val format: Format[BondedTaskCompleted] = new Format[BondedTaskCompleted] {

    override def writes(o: BondedTaskCompleted): JsValue = {
      Json.obj(
        "type" -> "BondedTaskCompleted",
        "task" -> o.taskId,
        "status" -> o.status
      )
    }

    override def reads(json: JsValue): JsResult[BondedTaskCompleted] = {
      for {
        taskId <- json.\("task").validate[UUID]
        status <- json.\("status").validate[Int]
      } yield {
        BondedTaskCompleted(taskId, status)
      }
    }

  }

}

case class BondedTaskStarted(taskId: UUID) extends UserResponse

object BondedTaskStarted {

  implicit val format: Format[BondedTaskStarted] = new Format[BondedTaskStarted] {

    override def writes(o: BondedTaskStarted): JsValue = {
      Json.obj(
        "type" -> "BondedTaskStarted",
        "task" -> o.taskId
      )
    }

    override def reads(json: JsValue): JsResult[BondedTaskStarted] = {
      for {
        taskId <- json.\("task").validate[UUID]
      } yield {
        BondedTaskStarted(taskId)
      }
    }

  }

}

object UserResponse {

  implicit val format: Format[UserResponse] = new Format[UserResponse] {

    override def writes(o: UserResponse): JsValue = {
      o match {
        case o: BondedTaskNotExecutable => BondedTaskNotExecutable.format.writes(o)
        case o: BondedTaskList => BondedTaskList.format.writes(o)
        case o: BondedTaskCompleted => BondedTaskCompleted.format.writes(o)
        case o: BondedTaskStarted => BondedTaskStarted.format.writes(o)
      }
    }

    override def reads(json: JsValue): JsResult[UserResponse] = {
      json.\("type").validate[String].flatMap {
        case "BondedTaskNotExecutable" => BondedTaskNotExecutable.format.reads(json)
        case "BondedTaskList" => BondedTaskList.format.reads(json)
        case "BondedTaskCompleted" => BondedTaskCompleted.format.reads(json)
        case "BondedTaskStarted" => BondedTaskStarted.format.reads(json)
      }
    }

  }

}
