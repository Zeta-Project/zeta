package controller


case class ModelLoader(modelId: String) {

  def mClasses: scalajs.js.Array[String] = {
    import scala.scalajs.js.JSConverters._

    /*
     * TODO: We need a REST API method which returns the mClass names for a given meta model.
     * For authorized access use the AccessToken object.
     * See CodeEditorController.saveCode() for an usage example.
     */

    /*val res = jQuery.ajax(literal(
      url = s"/metamodels/$modelId/mclasses",
      `type` = "GET",
      async = false).asInstanceOf[JQueryAjaxSettings]
    )
    res.selectDynamic("responseText").toString.split(", ").toJSArray*/
    Array[String]("TestMClass1", "TestMClass2", "TestMClass3").toJSArray
  }

  def mRefs: scalajs.js.Array[String] = {
    import scala.scalajs.js.JSConverters._

    /*
     * TODO: We need a REST API method which returns the mRef names for a given meta model.
     * For authorized access use the AccessToken object.
     * See CodeEditorController.saveCode() for an usage example.
     */

    /*val res = jQuery.ajax(literal(
      url = s"/metamodels/$modelId/mrefs",
      `type` = "GET",
      async = false).asInstanceOf[JQueryAjaxSettings]
    )
    res.selectDynamic("responseText").toString.split(", ").toJSArray*/
    Array[String]("TestMRef1", "TestMRef2", "TestMRef3").toJSArray
  }
}
