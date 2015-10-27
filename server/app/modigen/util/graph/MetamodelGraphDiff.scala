package modigen.util.graph

import models.MetaModelData
import play.api.libs.json.{JsObject, Json}

import scala.collection.Set

/*
 * Die Klasse kuemmert sich um den Fall, dass Elemente ausserhalb des grafischen Metamodell-Editors zum Metamodell
 * hinzugefuegt, aus dem Metamodell entfernt, oder Attribute eines Elementes veraendert wurden.
 *
 * Die Attribute eines Elementes wurden veraendert:
 *  1. Attribut wurde hinzugefuegt => Attribut im Graph hinzufuegen
 *  2. Attribut wurde entfernt => Attribut im Graph entfernen
 *  3. Wert eines Attributes wurde veraendert => Wert des Attributes auch im Graph veraendern
 *
 * Die folgenden Faelle sind momentan noch nicht relevant, koennen dies in Zukunft aber evtl. werden:
 *
 * Ein Element wurde aus dem Metamodell entfernt:
 *  1. Element ist Verbindung => diese aus dem Graph entfernen.
 *  2. Element ist Klasse => dieses aus dem Graph entfernen, und alle Links auf Source bzw. Target mit
 *     dieser Klasse pruefen und Attribut entfernen.
 *
 * Ein Element wurde zum Metamodell hinzugefuegt:
 *  1. Element ist Verbindung => Element im Graph anlegen, Source- und Target-Elemente finden und eintragen.
 *     Problem: Verbindungstyp muss anhand der Werte von sourceDeletionDeletesTarget und targetDeletionDeletesSource
 *     herausgefunden werden. Attribute eintragen.
 *  2. Element ist Klasse => Klasse in Graph eintragen. Typ aus "abstract=true/false" erkennen. Attribute eintragen.
 *
 * Sonderfaelle:
 *  1. Einer Klasse wurde beim Attribut superTypes ein Wert hinzugefuegt oder entfernt => Link mit Typ Generalization
 *     in Graph eintragen bzw. davon entfernen.
 *
 */
class MetamodelGraphDiff(val metaModelData: MetaModelData) {
  val metaModel = Json.parse(metaModelData.data).as[JsObject]
  var graph = Json.parse(metaModelData.graph).as[JsObject]

  def fixMetaModel() = {
    fixAttributes()

    def fixAttributes() = {
      metaModel.keys.foreach { elementKey =>
        println(elementKey)
        println("Graph only: ")
        println(graphOnlyAttributes(elementKey))
        println("MetaModel only: ")
        println(metaModelOnlyAttributes(elementKey))
        println("Changed: ")
        println(changedAttributes(elementKey))
      }

      def metaModelAttributes(elementKey: String): Set[JsObject] = ((metaModel \ elementKey) \ "mAttributes").as[JsObject].values.map(_.as[JsObject])

      def graphCells: Set[JsObject] = (graph \ "cells").as[Set[JsObject]]

      def graphAttributes(elementKey: String): Set[JsObject] = (graphCells.filter(cell => (cell \ "name").as[String] == elementKey).head \ "m_attributes").as[Set[JsObject]]

      def metaModelOnlyAttributes(elementKey: String): Set[JsObject] = metaModelAttributes(elementKey).filter(attr => !graphAttributes(elementKey).exists(obj => (obj \ "name").as[String] == (attr \ "name").as[String]))

      def graphOnlyAttributes(elementKey: String): Set[JsObject] = graphAttributes(elementKey).filter(attr => !metaModelAttributes(elementKey).exists(obj => (obj \ "name").as[String] == (attr \ "name").as[String]))

      def changedAttributes(elementKey: String): Set[JsObject] = {
        // TODO
        graphAttributes(elementKey)
      }

      def removeAttribute(elementKey: String, attr: JsObject): JsObject = {
        // TODO
        graph
      }

      def addAttribute(elementKey: String, attr: JsObject): JsObject = {
        // TODO
        graph
      }


    }

  }

}
