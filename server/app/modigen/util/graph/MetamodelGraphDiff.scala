package modigen.util.graph

import models.MetaModelData
import play.api.libs.json.{JsArray, JsObject, Json}

import scala.collection.Set

class MetamodelGraphDiff(val metaModelData: MetaModelData) {
  val metaModel = Json.parse(metaModelData.data).as[JsObject]
  var graph = Json.parse(metaModelData.graph).as[JsObject]

  def fixMetaModel() = {
    fixAttributes()

    def fixAttributes() = {
      metaModel.keys.foreach { elementKey =>
        metaModelOnlyAttributes(elementKey).foreach(attribute => removeFromGraph(elementKey, attributeName(attribute)))
        graphOnlyAttributes(elementKey).foreach(attribute => addToGraph(elementKey, attribute))
        changedAttributes(elementKey).foreach(attribute => changeInGraph(elementKey, attribute))
      }

      def graphCells: Set[JsObject] = (graph \ "cells").as[Set[JsObject]]

      def graphCell(elementKey: String): Option[JsObject] = graphCells.find(cell => (cell \ "name").as[String] == elementKey)

      def metaModelCell(elementKey: String): Option[JsObject] = (metaModel \ elementKey).asOpt[JsObject]

      def metaModelAttributes(elementKey: String): Set[JsObject] = {
        metaModelCell(elementKey) match {
          case Some(cell) =>
            val mAttributes = (cell \ "mAttributes").asOpt[JsObject]
            mAttributes match {
              case Some(attributes) => attributes.values.map(_.as[JsObject])
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

      def changedAttributes(elementKey: String): Set[JsObject] = {
        var changedAttrs = Set[JsObject]()
        metaModelAttributes(elementKey).foreach { metaModelAttribute =>
          val graphAttribute = graphAttributes(elementKey).filter(attr => attributeName(attr) == attributeName(metaModelAttribute)).head
          if (metaModelAttribute != graphAttribute) {
            changedAttrs = changedAttrs + metaModelAttribute
          }
        }
        changedAttrs
      }

      def removeFromGraph(elementKey: String, attributeKey: String) = {

      }

      def addToGraph(elementKey: String, attribute: JsObject) = {

      }

      def changeInGraph(elementKey: String, attribute: JsObject) = {

      }


    }

  }

}
