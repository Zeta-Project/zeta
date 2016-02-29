package controller

import java.util.{Date, UUID}

import org.scalajs.jquery._
import scalot._
import shared.CodeEditorMessage._
import view.CodeEditorView

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.JSON

case class CodeEditorController(dslType: String, metaModelUuid: String) {

  case class AccessToken(token: String, expires: Int, timestampLoaded: Long)

  case class TokenInformation(token: String, refreshed: Boolean, error: Option[String])

  val autoSave = true

  var accessToken: AccessToken = null

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
            authorized(fnSave, forceRefresh = true)
          } else {
            println(s"Cannot save: $errorThrown")
          }
        }
      ).asInstanceOf[JQueryAjaxSettings])

    }

    authorized(fnSave, forceRefresh = false)

  }

  def authorized(fnThen: TokenInformation => Unit, forceRefresh: Boolean) = {

    var refresh = forceRefresh

    if (accessToken == null) {
      refresh = true
    } else {
      val currentTime = new Date().getTime
      val difference = (currentTime - accessToken.timestampLoaded) / 1000
      if (difference > accessToken.expires - 10) { // refresh if token expires in less than 10 seconds
        refresh = true
      }
    }

    if (refresh) {
      refreshAccessToken(fnThen)
    } else {
      fnThen(TokenInformation(accessToken.token, refreshed = false, None))
    }
  }

  def refreshAccessToken(fnThen: TokenInformation => Unit) = {
    jQuery.ajax(literal(
      `type` = "POST",
      url = "/oauth/accessTokenLocal",
      data = literal(
        client_id = "modigen-browser-app1",
        grant_type = "implicit"
      ),
      success = { (data: js.Dynamic, textStatus: String, jqXHR: JQueryXHR) =>
        accessToken = AccessToken(data.access_token.asInstanceOf[String], data.expires_in.asInstanceOf[Int], new Date().getTime)
        fnThen(TokenInformation(accessToken.token, refreshed = true, None))
      },
      error = { (jqXHR: JQueryXHR, textStatus: String, errorThrown: String) =>
        accessToken = null
        fnThen(TokenInformation("", refreshed = true, Some(errorThrown)))
      }
    ).asInstanceOf[JQueryAjaxSettings]
    )
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