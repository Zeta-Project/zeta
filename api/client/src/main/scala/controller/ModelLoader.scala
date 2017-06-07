package controller

import java.util.UUID

import org.scalajs.dom.console
import org.scalajs.jquery
import org.scalajs.jquery.JQueryAjaxSettings
import org.scalajs.jquery.JQueryXHR
import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.annotation.JSExport
import scalajs.js.{Array => JsArray}
import scala.scalajs.js.JSConverters.array2JSRichGenTrav

/**
 * remarks: annotation above does not exist prior to scala.js 0.6.5, so we can't use it (currently in project: 0.6.4)
 * but: will be required in scala.js 1.0.0
 * see: https://www.scala-js.org/doc/interoperability/facade-types.html
 */
trait MetaElementData extends js.Object {
  val mType: String = js.native
  val name: String = js.native
}

trait MetaModelData extends js.Object {
  val name: String = js.native
  val elements: scalajs.js.Array[MetaElementData] = js.native
}

case class MetaModel(
    id: String,
    name: String,
    mClasses: scalajs.js.Array[MetaElementData],
    mReferences: scalajs.js.Array[MetaElementData])

@JSExport
class MetaModelLoader(metaModelId: String, andThen: MetaModel => Unit) {

  @JSExport
  def fetch(): Unit = {

  }

  private def loadMetaModel(): Unit = {
    jquery.jQuery.ajax(literal(
      `type` = "GET",
      url = s"/metamodels/$metaModelId/definition",
      contentType = "application/json; charset=utf-8",
      dataType = "json",
      success = { (data: js.Dynamic, textStatus: String, jqXHR: JQueryXHR) =>
      val m = data.asInstanceOf[MetaModelData]
      val out = MetaModel(
        metaModelId,
        m.name,
        m.elements.filter(_.mType == "mClass"),
        m.elements.filter(_.mType == "mReference")
      )
      andThen(out)
    },
      error = { (jqXHR: JQueryXHR, textStatus: String, errorThrown: String) =>
      console.log(s"Cannot load meta model: $errorThrown")
    }
    ).asInstanceOf[JQueryAjaxSettings])
  }
}

case class ModelLoader(modelId: UUID) {

  def mClasses: JsArray[String] = {
    /*
     * TODO: We need a REST API method which returns the mClass names for a given meta model.
     * For authorized access use the AccessToken object.
     * See CodeEditorController.saveCode() for an usage example.
     */

    val res = jquery.jQuery.ajax(literal(
      url = s"/metamodels/$modelId/definition/mclasses",
      `type` = "GET",
      async = false,
      contentType = "application/json; charset=utf-8",
      dataType = "json"
    ).asInstanceOf[JQueryAjaxSettings])
    res.selectDynamic("responseText").toString.split(", ").toJSArray
    Array[String]("TestMClass1", "TestMClass2", "TestMClass3").toJSArray
  }

  def mRefs: JsArray[String] = {
    /*
     * TODO: We need a REST API method which returns the mRef names for a given meta model.
     * For authorized access use the AccessToken object.
     * See CodeEditorController.saveCode() for an usage example.
     */

    val res = jquery.jQuery.ajax(literal(
      url = s"/metamodels/$modelId/definition/mreferences",
      `type` = "GET",
      async = false,
      contentType = "application/json; charset=utf-8",
      dataType = "json"
    ).asInstanceOf[JQueryAjaxSettings])
    res.selectDynamic("responseText").toString.split(", ").toJSArray
    Array[String]("TestMRef1", "TestMRef2", "TestMRef3").toJSArray
  }
}
