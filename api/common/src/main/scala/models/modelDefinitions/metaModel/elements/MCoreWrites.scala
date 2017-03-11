package models.modelDefinitions.metaModel.elements

import models.modelDefinitions.metaModel.elements.ScalarValue.{ MString, MInt, MDouble, MBool }
import play.api.libs.json._

/**
 * Writes[T] for all MCore structures
 */

object MCoreWrites {

  implicit val mObjectWrites = new Writes[MObject] {
    def writes(o: MObject): JsValue = {
      o match {
        case m: MClass => Json.toJson(m)(mClassWrites)
        case m: MReference => Json.toJson(m)(mReferenceWrites)
        case m: MEnum => Json.toJson(m)(mEnumWrites)
        case m: MAttribute => Json.toJson(m)(mAttributeWrites)
      }
    }
  }

  implicit val mClassWrites: Writes[MClass] = new Writes[MClass] {
    def writes(c: MClass): JsValue = {
      Json.obj(
        "mType" -> "mClass",
        "name" -> c.name,
        "abstract" -> c.abstractness,
        "superTypes" -> c.superTypes.map(_.name),
        "inputs" -> c.inputs,
        "outputs" -> c.outputs,
        "attributes" -> c.attributes
      )
    }
  }

  implicit val mReferenceWrites: Writes[MReference] = new Writes[MReference] {
    def writes(r: MReference): JsValue = {
      Json.obj(
        "mType" -> "mReference",
        "name" -> r.name,
        "sourceDeletionDeletesTarget" -> r.sourceDeletionDeletesTarget,
        "targetDeletionDeletesSource" -> r.targetDeletionDeletesSource,
        "source" -> r.source,
        "target" -> r.target,
        "attributes" -> r.attributes
      )
    }
  }

  implicit val mLinkDefWrites: Writes[MLinkDef] = new Writes[MLinkDef] {
    def writes(m: MLinkDef): JsValue = {
      Json.obj(
        "type" -> m.mType.name,
        "upperBound" -> m.upperBound,
        "lowerBound" -> m.lowerBound,
        "deleteIfLower" -> m.deleteIfLower
      )
    }
  }

  implicit val mEnumWrites: Writes[MEnum] = new Writes[MEnum] {
    def writes(e: MEnum): JsValue = {
      Json.obj(
        "mType" -> "mEnum",
        "name" -> e.name,
        "symbols" -> e.values.map(_.name)
      )
    }
  }

  implicit val mAttributeWrites: Writes[MAttribute] = new Writes[MAttribute] {
    override def writes(a: MAttribute): JsValue = {
      Json.obj(
        "name" -> a.name,
        "globalUnique" -> a.globalUnique,
        "localUnique" -> a.localUnique,
        "type" -> a.`type`,
        "default" -> a.default,
        "constant" -> a.constant,
        "singleAssignment" -> a.singleAssignment,
        "expression" -> a.expression,
        "ordered" -> a.ordered,
        "transient" -> a.transient,
        "upperBound" -> a.upperBound,
        "lowerBound" -> a.lowerBound
      )
    }
  }

  implicit val attributeTypeWrites: Writes[AttributeType] = new Writes[AttributeType] {
    override def writes(a: AttributeType): JsValue = {
      val out = a match {
        case ScalarType.Bool => "Bool"
        case ScalarType.Double => "Double"
        case ScalarType.Int => "Int"
        case ScalarType.String => "String"
        case MEnum(name, _) => name
      }
      JsString(out)
    }
  }

  implicit val attributeValueWrites: Writes[AttributeValue] = new Writes[AttributeValue] {
    override def writes(a: AttributeValue): JsValue = {
      a match {
        case MBool(b) => JsBoolean(b)
        case MDouble(d) => JsNumber(d)
        case MInt(i) => JsNumber(i)
        case MString(s) => JsString(s)
        case EnumSymbol(name, _) => JsString(name)
      }
    }
  }

}
