package de.htwg.zeta.server.generator.model.diagram.node

import de.htwg.zeta.server.generator.model.shapecontainer.shape.Compartment
import de.htwg.zeta.server.generator.model.shapecontainer.shape.Shape
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Text
import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.server.generator.parser.ShapeSketch
import de.htwg.zeta.server.generator.parser.IDtoShapeSketch
import models.document.MetaModelEntity
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference

/**
 * Created by julian on 30.11.15.
 * diagrams shape definition
 */
class DiaShape(
    corporateStyle: Option[Style], shape: String,
    propertiesAndCompartmentsOpt: Option[List[(String, (String, String))]], cache: Cache, mc: MClass, metaModelEntity: MetaModelEntity) {
  val referencedShape: Shape = {
    val shapesketch: ShapeSketch = IDtoShapeSketch(shape)(cache)
    // Here we finally create real Shapes out of ShapeSketches!
    shapesketch.toShape(corporateStyle)
  }


  private def getElementFromCompartments[KEY, VALUE](propertiesAndCompartments: List[(String, (String, String))])
    (elementType: String, mapOptSup: Shape => Option[Map[String, VALUE]], keyLookup: String => Option[KEY]): Map[KEY, VALUE] = {

    propertiesAndCompartments.flatMap {
      case (typeName, (lookupKey, valueKey)) if typeName == elementType =>
        val entryOpt = keyLookup(lookupKey).flatMap(key => {
          val valueOpt = mapOptSup(referencedShape).flatMap(_.get(valueKey))
          valueOpt.map(value => (key, value))
        })

        entryOpt.toList

      case _ => Nil
    }.toMap
  }

  private type ElementSup[KEY, VALUE] = (String, (Shape) => Option[Map[String, VALUE]], (String) => Option[KEY]) => Map[KEY, VALUE]

  val (
    vals: Map[String, Text],
    vars: Map[MAttribute, Text],
    nests: Map[MReference, Compartment]
    ) = propertiesAndCompartmentsOpt match {
    case Some(propertiesAndCompartments: List[(String, (String, String))]) =>
      def getElement[KEY, VALUE]: ElementSup[KEY, VALUE] = getElementFromCompartments[KEY, VALUE](propertiesAndCompartments)

      (
        getVals(getElement),
        getVars(getElement),
        getNests(getElement)
      )
    case None =>
      (Map(), Map(), Map())
  }


  private def getVals(getElement: ElementSup[String, Text]): Map[String, Text] = {
    getElement("val", _.textMap, Some(_))
  }

  private def getVars(getElement: ElementSup[MAttribute, Text]): Map[MAttribute, Text] = {
    getElement("var", _.textMap, k => mc.attributes.find(_.name == k))
  }

  private def getNests(getElement: ElementSup[MReference, Compartment]): Map[MReference, Compartment] = {
    getElement("nest", _.compartmentMap, key => metaModelEntity.metaModel.elements.toStream.flatMap {
      case (name: String, ref: MReference) if name == key => List(ref)
      case _ => Nil
    }.headOption)
  }


  /**
   * ADDED: to get the name of the shape
   *
   * @return the name
   */
  def getNameOfShape: String = {
    shape
  }
}
