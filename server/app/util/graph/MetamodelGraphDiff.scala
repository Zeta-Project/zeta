package util.graph

import models.metaModel.MetaModelData_2
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

import scala.collection.Set

object MetamodelGraphDiff {

  /**
   * Die Funktion prueft, ob der Graph zum Metamodell noch konsistent ist. Dies kann dann notwendig sein, wenn von
   * ausserhalb, bspw. ueber die REST-Schnittstelle zum Metamodell, das Metamodell veraendert wurden
   * ohne den Graph dazu anzupassen.
   * Aenderungen sind nur an den Attributen zugelassen; die Funktion kann den Graph korrigieren, wenn Attribute aus
   * dem Metamodell entfernt, zum Metamodell hinzugefuegt oder Attribute im Metamodell veraendert wurden.
   *
   * @param metaModelData MetaModelData-Objekt, welches Graph und Data enthaelt.
   * @return Ein neues MetaModelData-Objekt mit korrigiertem Graph.
   */
  def fixMetaModel(metaModelData: MetaModelData): MetaModelData = {
    val metaModel = Json.parse(metaModelData.data).as[Seq[JsObject]]
    var graph = Json.parse(metaModelData.graph).as[JsObject]

    def fixAttributes() = {

      metaModel.foreach { element =>
        val elementKey = (element \ "name").as[String]
        graphOnlyAttributes((element \ "name").as[String]).foreach(attribute => graph = removeFromGraph(elementKey, attribute))
        metaModelOnlyAttributes(elementKey).foreach(attribute => graph = addToGraph(elementKey, attribute))
        changedAttributes(elementKey).foreach { a =>
          graph = removeFromGraph(elementKey, a._1)
          graph = addToGraph(elementKey, a._2)
        }
      }

      def removeFromGraph(elementKey: String, attribute: JsObject): JsObject = {
        val newAttributes = JsArray((graphAttributes(elementKey) - attribute).toSeq)
        val newGraph = replaceAttributes(elementKey, newAttributes)
        newGraph
      }

      def addToGraph(elementKey: String, attribute: JsObject) = {
        val newAttributes = JsArray((graphAttributes(elementKey) + attribute).toSeq)
        val newGraph = replaceAttributes(elementKey, newAttributes)
        newGraph
      }

      def replaceAttributes(elementKey: String, newAttributes: JsArray): JsObject = {
        val newCell = JsObject((graphCell(elementKey).get.as[Map[String, JsValue]] - "m_attributes" + ("m_attributes" -> newAttributes)).toSeq)
        val newCells = JsArray((graphCells - graphCell(elementKey).get + newCell).toSeq)
        JsObject((graph.as[Map[String, JsValue]] - "cells" + ("cells" -> newCells)).toSeq)
      }

      def graphCells: Set[JsObject] = (graph \ "cells").as[Set[JsObject]]

      def graphCell(elementKey: String): Option[JsObject] = graphCells.find(cell => (cell \ "name").as[String] == elementKey)

      def metaModelCell(elementKey: String): Option[JsObject] = metaModel.find(element => (element \ "name").as[String] == elementKey)

      def metaModelAttributes(elementKey: String): Set[JsObject] = {
        metaModelCell(elementKey) match {
          case Some(cell) =>
            val mAttributes = (cell \ "attributes").asOpt[JsObject]
            mAttributes match {
              case Some(attributes) => attributes.values.map(_.as[JsObject]).toSet
              case None => Set.empty
            }
          case None => Set.empty
        }
      }

      def graphAttributes(elementKey: String): Set[JsObject] = {
        graphCell(elementKey) match {
          case Some(cell) =>
            (cell \ "m_attributes").asOpt[JsArray] match {
              case Some(attribute) => attribute.as[Set[JsObject]]
              case None => Set.empty
            }
          case None => Set.empty
        }
      }

      def graphAttributeNames(elementKey: String): Set[String] = graphAttributes(elementKey).map(attributeName)

      def metaModelAttributeNames(elementKey: String): Set[String] = metaModelAttributes(elementKey).map(attributeName)

      def attributeName(attribute: JsObject): String = (attribute \ "name").as[String]

      def metaModelOnlyAttributes(elementKey: String): Set[JsObject] = {
        val diff = metaModelAttributeNames(elementKey) -- graphAttributeNames(elementKey)
        diff.map(attrName => metaModelAttributes(elementKey).filter(attr => attributeName(attr) == attrName).head)
      }

      def graphOnlyAttributes(elementKey: String): Set[JsObject] = {
        val diff = graphAttributeNames(elementKey) -- metaModelAttributeNames(elementKey)
        diff.map(attrName => graphAttributes(elementKey).filter(attr => attributeName(attr) == attrName).head)
      }

      /*
       * Das Tupel enthaelt die folgenden zwei Objekte:
       * ._1 Das Attribut-Objekt des Graphs
       * ._2 Das Attribut-Objekt des Metamodells
       */
      def changedAttributes(elementKey: String): Set[(JsObject, JsObject)] = {
        var changedAttrs = Set[(JsObject, JsObject)]()
        metaModelAttributes(elementKey).foreach { metaModelAttribute =>
          val graphAttribute = graphAttributes(elementKey).find(attr => attributeName(attr) == attributeName(metaModelAttribute))
          graphAttribute match {
            case Some(attribute) =>
              if (metaModelAttribute != attribute) {
                val attrs = (attribute, metaModelAttribute)
                changedAttrs = changedAttrs + attrs
              }
            case None => ;
          }

        }
        changedAttrs
      }

    }

    fixAttributes()
    metaModelData.copy(graph = graph.toString())

  }
}
