package util.graph

import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements._
import play.api.libs.json._

object MetamodelGraphDiff {

  def fixGraph(metaModel: MetaModel): MetaModel = {

    val elements = metaModel.elements
    if (metaModel.uiState.isEmpty) return metaModel
    var graph = Json.parse(metaModel.uiState).as[JsObject]

    def fixAttributes() = {

      elements.values.foreach { element =>
        val elementKey = element.name

        graphOnlyAttributes(elementKey).foreach(attribute => removeFromGraph(elementKey, attribute))
        metaModelOnlyAttributes(elementKey).foreach(attribute => addToGraph(elementKey, attribute))
        changedAttributes(elementKey).foreach { attribute =>
          removeFromGraph(elementKey, attribute)
          addToGraph(elementKey, attribute)
        }
      }

      def graphOnlyAttributes(elementKey: String): Set[MAttribute] = {
        val diff = metaModelAttributeNames(elementKey) -- graphAttributeNames(elementKey)
        val attributes = diff.map(attrName => graphAttributes(elementKey).filter(attr => graphAttributeName(attr) == attrName).head)
        attributes.map(toMAttribute)
      }

      def metaModelOnlyAttributes(elementKey: String): Set[MAttribute] = {
        val diff = metaModelAttributeNames(elementKey) -- graphAttributeNames(elementKey)
        diff.map(attrName => metaModelAttributes(elementKey).filter(attr => attr.name == attrName).head)
      }

      def changedAttributes(elementKey: String): Set[MAttribute] = {
        var changedAttrs = Set[MAttribute]()
        metaModelAttributes(elementKey).foreach { metaModelAttribute =>
          val graphAttribute = graphAttributes(elementKey).find(attr => graphAttributeName(attr) == metaModelAttribute.name)
          graphAttribute match {
            case Some(attribute) =>
              val graphMAttribute = toMAttribute(attribute)
              if (metaModelAttribute != graphMAttribute) {
                changedAttrs = changedAttrs + metaModelAttribute
              }
            case None => ;
          }
        }

        changedAttrs
      }

      def metaModelAttributeNames(elementKey: String): Set[String] = {
        metaModelAttributes(elementKey).map(_.name)
      }

      def graphAttributeNames(elementKey: String): Set[String] = {
        graphAttributes(elementKey).map(attribute => (attribute \ "name").as[String])
      }

      def metaModelAttributes(elementKey: String): Set[MAttribute] = {
        elements.get(elementKey) match {
          case Some(element) => element match {
            case mClass: MClass => mClass.attributes.toSet
            case mReference: MReference => mReference.attributes.toSet
            case _ => Set.empty
          }
          case None => Set.empty
        }
      }

      def graphAttributes(elementKey: String): Set[JsObject] = {
        graphCell(elementKey) match {
          case Some(cell) =>
            (cell \ "m_attributes").asOpt[JsArray] match {
              case Some(attributes) => attributes.as[Set[JsObject]]
              case None => Set.empty
            }
          case _ => Set.empty
        }
      }

      def graphCell(elementKey: String): Option[JsObject] = graphCells.find(cell => (cell \ "name").as[String] == elementKey)

      def graphCells: Set[JsObject] = (graph \ "cells").as[Set[JsObject]]

      def graphAttributeName(attribute: JsObject): String = (attribute \ "name").as[String]

      def toMAttribute(attribute: JsObject) = MAttribute(
        name = (attribute \ "name").as[String],
        globalUnique = (attribute \ "globalUnique").as[Boolean],
        localUnique = (attribute \ "localUnique").as[Boolean],
        `type` = MCoreReads.detectType((attribute \ "type").as[String]),
        default = MCoreReads.detectType((attribute \ "type").as[String]) match {
          case ScalarType.String => ScalarValue.MString((attribute \ "default").as[String])
          case ScalarType.Bool => ScalarValue.MBool((attribute \ "default").as[Boolean])
          case ScalarType.Double => ScalarValue.MDouble((attribute \ "default").as[Double])
          case ScalarType.Int => ScalarValue.MInt((attribute \ "default").as[Int])
          case MEnum(_, _) => ScalarValue.MString((attribute \ "default").as[String])
        },
        constant = (attribute \ "constant").as[Boolean],
        singleAssignment = (attribute \ "singleAssignment").as[Boolean],
        expression = (attribute \ "expression").as[String],
        ordered = (attribute \ "ordered").as[Boolean],
        transient = (attribute \ "transient").as[Boolean],
        upperBound = (attribute \ "upperBound").as[Int],
        lowerBound = (attribute \ "lowerBound").as[Int]
      )

      def toJsonAttribute(attribute: MAttribute): JsObject = {
        val `type` = JsString(attribute.`type` match {
          case ScalarType.String => "String"
          case ScalarType.Bool => "Boolean"
          case ScalarType.Int => "Integer"
          case ScalarType.Double => "Double"
          case _ => ""
        })

        val default = attribute.default match {
          case ScalarValue.MString(value) => JsString(value)
          case ScalarValue.MBool(value) => JsBoolean(value)
          case ScalarValue.MInt(value) => JsNumber(value)
          case ScalarValue.MDouble(value) => JsNumber(value)
        }

        JsObject(Seq(
          "name" -> JsString(attribute.name),
          "globalUnique" -> JsBoolean(attribute.globalUnique),
          "localUnique" -> JsBoolean(attribute.localUnique),
          "type" -> `type`,
          "default" -> default,
          "constant" -> JsBoolean(attribute.constant),
          "singleAssignment" -> JsBoolean(attribute.singleAssignment),
          "expression" -> JsString(attribute.expression),
          "ordered" -> JsBoolean(attribute.ordered),
          "transient" -> JsBoolean(attribute.transient),
          "upperBound" -> JsNumber(attribute.upperBound),
          "lowerBound" -> JsNumber(attribute.lowerBound)
        ))
      }

      def removeFromGraph(elementKey: String, attribute: MAttribute): Unit = {
        val graphAttribute = graphAttributes(elementKey).find(attr => (attr \ "name").as[String] == attribute.name).get
        val newAttributes = JsArray((graphAttributes(elementKey) - graphAttribute).toSeq)
        replaceAttributes(elementKey, newAttributes)
        println(s"Removed attribute ${attribute.name}")
      }

      def addToGraph(elementKey: String, attribute: MAttribute): Unit = {
        val newAttributes = JsArray((graphAttributes(elementKey) + toJsonAttribute(attribute)).toSeq)
        replaceAttributes(elementKey, newAttributes)
        println(s"Added attribute ${attribute.name}")
      }

      def replaceAttributes(elementKey: String, newAttributes: JsArray): Unit = {
        val newCell = JsObject((graphCell(elementKey).get.as[Map[String, JsValue]] - "m_attributes" + ("m_attributes" -> newAttributes)).toSeq)
        val newCells = JsArray((graphCells - graphCell(elementKey).get + newCell).toSeq)
        graph = JsObject((graph.as[Map[String, JsValue]] - "cells" + ("cells" -> newCells)).toSeq)
      }
    }

    fixAttributes()
    metaModel.copy(uiState = graph.toString)
  }

}
