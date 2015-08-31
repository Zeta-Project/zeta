package controller

import scala.scalajs.js.{Array, JSON}
import org.scalajs.jquery._
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js
import org.scalajs.dom


case class ModelLoader(modelId: String) {

  def mClasses: scalajs.js.Array[String] = {
    import scala.scalajs.js.JSConverters._
    val res = jQuery.ajax(literal(
      url = s"/metamodels/$modelId/mclasses",
      `type` = "GET",
      async = false).asInstanceOf[JQueryAjaxSettings]
    )
    res.selectDynamic("responseText").toString.split(", ").toJSArray
  }

  def mRefs: scalajs.js.Array[String] = {
    import scala.scalajs.js.JSConverters._
    val res = jQuery.ajax(literal(
      url = s"/metamodels/$modelId/mrefs",
      `type` = "GET",
      async = false).asInstanceOf[JQueryAjaxSettings]
    )
    res.selectDynamic("responseText").toString.split(", ").toJSArray
  }
}
