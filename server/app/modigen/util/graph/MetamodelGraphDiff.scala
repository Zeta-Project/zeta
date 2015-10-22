package modigen.util.graph

import java.util.UUID

import models.MetaModelData
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

class MetamodelGraphDiff(val metaModelData: MetaModelData) {
  val metaModel = Json.parse(metaModelData.data).as[JsObject]
  val graph = Json.parse(metaModelData.graph).as[JsObject]
  val additionalGraphElements = List("mEnumContainer")

  def isConsistent = diffNames.isEmpty

  def fixGraph(): MetaModelData = {

    println(metaModel)
    println(graph)

    def removeFields(): JsObject = {
      val filtered = graphCells.filterNot(cell => diffNames.contains((cell \ "name").as[String]))
      graph - "cells" +("cells", setToJsArray(filtered))
    }

    def addFields(): JsObject = {
      val cells = metaModelCells.filter(cell => diffNames.contains((cell \ "name").as[String]))
      println(cells.map(cellTemplate))

      graph
    }

    def setToJsArray(set: Set[JsValue]): JsArray = {
      set.foldLeft(JsArray())((acc, x) => acc ++ Json.arr(x))
    }

    graphNames.size - metaModelNames.size match {
      case x if x < 0 => metaModelData.copy(graph = addFields().toString())
      case x if x > 0 => metaModelData.copy(graph = removeFields().toString())
      case 0 => metaModelData
    }

  }

  def graphCells: Set[JsValue] = (graph \ "cells").as[Set[JsValue]]

  def metaModelCells: Set[JsValue] = metaModel.values.toSet

  def graphNames: Set[String] = graphCells.map(_.\("name").as[String]) -- additionalGraphElements

  def metaModelNames: Set[String] = metaModel.values.map(_.\("name").as[String]).toSet

  def diffNames: Set[String] = (graphNames &~ metaModelNames) ++ (metaModelNames &~ graphNames)

  def cellTemplate(cell: JsValue): String = {
    val name = (cell \ "name").as[String]
    val cellType = if ((cell \ "abstract").as[Boolean]) "uml.Abstract" else "uml.Class"
    val uuid = UUID.randomUUID.toString

    " "
  }

}
