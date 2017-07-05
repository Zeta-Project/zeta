package controller

import java.util.UUID

import de.htwg.zeta.shared.CodeEditorMessage.DocAdded
import de.htwg.zeta.shared.CodeEditorMessage.DocDeleted
import de.htwg.zeta.shared.CodeEditorMessage.DocLoaded
import de.htwg.zeta.shared.CodeEditorMessage.DocNotFound
import de.htwg.zeta.shared.CodeEditorMessage.TextOperation

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.JSON
import org.scalajs.dom.console
import org.scalajs.jquery
import org.scalajs.jquery.JQueryAjaxSettings
import org.scalajs.jquery.JQueryXHR
import scalot.ApplyResult
import scalot.Client
import view.CodeEditorView

case class CodeEditorController(dslType: String, metaModelId: UUID) {

  val autoSave = true

  val view = new CodeEditorView(controller = this, metaModelId = metaModelId, dslType = dslType, autoSave = autoSave)
  val ws = new WebSocketConnection(controller = this, metaModelId = metaModelId, dslType = dslType)
  var document: Client = null

  def docLoadedMessage(msg: DocLoaded): js.Dynamic = {
    console.log(s"docLoadedMessage(${msg.toString})")
    document = Client(str = msg.str, revision = msg.revision, title = msg.title, docType = msg.docType, id = msg.id.toString)
    view.displayDoc(document)
  }

  def docNotFoundMessage(msg: DocNotFound): js.Dynamic = {
    console.log(s"docNotFoundMessage(${msg.toString})")
    addDocument(msg.metaModelId.toString, msg.dslType)
    view.displayDoc(document)
  }

  def addDocument(title: String, docType: String): Unit = {
    console.log(s"addDocument(${title.toString}, ${docType.toString})")
    document = Client(str = "", revision = 0, title = title, docType = docType, id = UUID.randomUUID.toString)
    ws.sendMessage(
      DocAdded(str = "", revision = 0, docType = docType, title = title, id = UUID.fromString(document.id), dslType = dslType, metaModelId = metaModelId)
    )
  }

  def deleteDocument(id: UUID): Unit = {
    console.log(s"deleteDocument(${id.toString})")
    console.log(s"deleteDocument(${id.toString})")
    ws.sendMessage(DocDeleted(id, dslType))
  }

  /**
   * Saves the code via the REST API in the database.
   * Before we can access the REST API, we have to get an oAuth access token.
   * The function authorized() checks, if there is an access token already and if it is still valid.
   * authorized() takes a function, here fnSave(), that takes the valid token and some information about it as parameter.
   * This function fnSave() is a callback function which will be called inside authorized().
   */
  def saveCode(): js.Dynamic = {
    console.log("saveCode()")
    jquery.jQuery.ajax(literal(
      `type` = "PUT",
      url = s"/rest/v1/meta-models/$metaModelId/$dslType",
      contentType = "application/json; charset=utf-8",
      dataType = "json",
      data = JSON.stringify(js.Dictionary(
        "code" -> document.str
      )),
      success = { (data: js.Dynamic, textStatus: String, jqXHR: JQueryXHR) =>
      },
      error = { (jqXHR: JQueryXHR, textStatus: String, errorThrown: String) =>
        console.log(s"Cannot save: $errorThrown")
      }
    ).asInstanceOf[JQueryAjaxSettings])
  }

  /** Apply changes to the corresponding doc */
  def operationFromRemote(op: TextOperation): Unit = {
    console.log(s"operationFromRemote(${op.toString})")
    val res = document.applyRemote(op.op)
    res.apply match {
      case Some(apply) if op.docId == view.selectedId =>
        view.updateView(apply)
      case _ =>
    }
    res.send match {
      case Some(send) =>
        ws.sendMessage(TextOperation(send, op.docId))
      case _ =>
    }
  }

  def operationFromLocal(op: scalot.Operation, docId: UUID): Any = {
    console.log(s"operationFromLocal(${op.toString}, ${docId.toString})")
    document.applyLocal(op) match {
      case ApplyResult(Some(send), _) =>
        ws.sendMessage(TextOperation(send, docId))
        if (autoSave) {
          saveCode()
        }
      case _ =>
        if (autoSave) {
          saveCode()
        }
    }

  }
}
