package controller

import java.util.{Date, UUID}

import controller.AccessToken.TokenInformation
import org.scalajs.jquery._
import scalot._
import shared.CodeEditorMessage._
import view.CodeEditorView

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.JSON

case class CodeEditorController(dslType: String, metaModelUuid: String) {

  val autoSave = true


  val view = new CodeEditorView(controller = this, metaModelUuid = metaModelUuid, dslType = dslType, autoSave = autoSave)
  val ws = new WebSocketConnection(controller = this, metaModelUuid = metaModelUuid, dslType = dslType)
  val clientId = UUID.randomUUID().toString
  var document: Client = null

  def docLoadedMessage(msg: DocLoaded) = {
    println("Got DocLoadedMessage")
    document = new Client(str = msg.str, revision = msg.revision, title = msg.title, docType = msg.docType, id = msg.id)
    view.displayDoc(document)
  }

  def docNotFoundMessage(msg: DocNotFound) = {
    println("Got DocNotFoundMessage")
    addDocument(msg.metaModelUuid, msg.dslType)
    view.displayDoc(document)
  }

  def addDocument(title: String, docType: String) = {
    document = new Client(str = "", revision = 0, title = title, docType = docType)
    ws.sendMessage(
      DocAdded(str = "", revision = 0, docType = docType, title = title, id = document.id, dslType = dslType, metaModelUuid = metaModelUuid)
    )
  }

  def deleteDocument(id: String) = {
    ws.sendMessage(DocDeleted(id, dslType))
  }

  /*
   * Saves the code via the REST API in the database.
   * Before we can access the REST API, we have to get an oAuth access token.
   * The function authorized() checks, if there is an access token already and if it is still valid.
   * authorized() takes a function, here fnSave(), that takes the valid token and some information about it as parameter.
   * This function fnSave() is a callback function which will be called inside authorized().
   */
  def saveCode() = {

    def fnSave(tokenInformation: TokenInformation): Unit = {

      if (tokenInformation.error.isDefined) {
        return
      }

      jQuery.ajax(literal(
        `type` = "PUT",
        url = s"/metamodels/$metaModelUuid/$dslType",
        contentType = "application/json; charset=utf-8",
        dataType = "json",
        data = JSON.stringify(js.Dictionary(
          "code" -> document.str
        )),
        headers = literal(
          Authorization = s"Bearer ${tokenInformation.token}"
        ),
        success = { (data: js.Dynamic, textStatus: String, jqXHR: JQueryXHR) =>
        },
        error = { (jqXHR: JQueryXHR, textStatus: String, errorThrown: String) =>
          if (!tokenInformation.refreshed) {
            AccessToken.authorized(fnSave, forceRefresh = true)
          } else {
            println(s"Cannot save: $errorThrown")
          }
        }
      ).asInstanceOf[JQueryAjaxSettings])

    }

    AccessToken.authorized(fnSave, forceRefresh = false)

  }

  /** Apply changes to the corresponding doc */
  def operationFromRemote(op: TextOperation) = {
    val res = document.applyRemote(op.op)
    res.apply match {
      case Some(apply) if op.docId == view.selectedId =>
        view.updateView(apply)
      case _ =>
    }
    res.send match {
      case Some(send) => ws.sendMessage(TextOperation(send, op.docId))
      case _ =>
    }
  }

  def operationFromLocal(op: scalot.Operation, docId: String) = {
    document.applyLocal(op) match {
      case ApplyResult(Some(send), _) =>
        ws.sendMessage(TextOperation(send, docId))
        if (autoSave) {
          saveCode()
        }
      case _ =>
    }
  }
}