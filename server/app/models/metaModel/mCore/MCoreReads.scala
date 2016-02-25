package models.metaModel.mCore

import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.annotation.tailrec
import scala.collection.immutable._

object MCoreReads {

  case class Dummy(name: String) extends ClassOrRef

  val empty = new {
    def mClass(name: String) = new MClass(name, false, Seq.empty, Seq.empty, Seq.empty, Seq.empty)

    def mRef(name: String) = new MReference(name, false, false, List.empty, List.empty, List.empty)
  }

//  implicit val mObjectReads = new Reads[MObject] {
//    override def reads(json: JsValue): JsResult[MObject] =
//      json.validate(
//        checkMClassReads.map(_.asInstanceOf[MObject]) orElse
//          checkMReferenceReads.map(_.asInstanceOf[MObject]) orElse
//          checkMEnumReads.map(_.asInstanceOf[MObject])
//      )
//  }

  implicit val mObjectReads = new Reads[MObject] {
    override def reads(json: JsValue): JsResult[MObject] = (json \ "mType").toOption match {
      case Some(JsString(s)) if s == "mClass" => json.validate(mClassReads.map(_.asInstanceOf[MObject]))
      case Some(JsString(s)) if s == "mReference" => json.validate(mReferenceReads.map(_.asInstanceOf[MObject]))
      case Some(JsString(s)) if s == "mEnum" => json.validate(mEnumReads.map(_.asInstanceOf[MObject]))
      case _  => JsError("Missing or unknown mType at top level, only mClass, mReference and mEnum allowed")
    }
  }

  def wireSuperTypes(newMap: => Map[String, MObject], old: Seq[MClass]): Seq[MClass] = {
    old.map(m => newMap.get(m.name).get.asInstanceOf[MClass])
  }

  def wireClassLinks(newMap: => Map[String, MObject], old: Seq[MLinkDef]): Seq[MLinkDef] = {
    old.map(m => m.copy(mType = newMap.get(m.mType.name).get.asInstanceOf[MClass]))
  }

  def wireRefLinks(newMap: => Map[String, MObject], old: Seq[MLinkDef]): Seq[MLinkDef] = {
    old.map(m => m.copy(mType = newMap.get(m.mType.name).get.asInstanceOf[MReference]))
  }

  implicit val mObjectMapReads = new Reads[Map[String, MObject]] {

    override def reads(json: JsValue): JsResult[Map[String, MObject]] = {
      val mObjectsResult = json.validate[Seq[MObject]]
      mObjectsResult match {
        case JsSuccess(mObjects, _) => finalize(mObjects)
        case JsError(e) => JsError(e)
      }
    }

    def addEnums(nameMapping: Map[String, MObject]): Map[String, MObject] = {

      def wireEnums(attributes: Seq[MAttribute]): Seq[MAttribute] = {
        attributes.map { a => a.`type` match {
          case MEnum(name, _) => a.copy(`type` = nameMapping.get(name).get.asInstanceOf[MEnum])
          case _ => a
        }
        }
      }

      nameMapping.mapValues {
        _ match {
          case c: MClass => c.updateAttributes(wireEnums(c.attributes))
          case r: MReference => r.updateAttributes(wireEnums(r.attributes))
          case e: MEnum => e
        }
      }

    }

    def finalize(mObjects: Seq[MObject]): JsResult[Map[String, MObject]] = {
      val nameMapping = mObjects.map(mObj => mObj.name -> mObj).toMap
      if (mObjects.size != nameMapping.size) JsError("MObjects must have unique names")
      else {
        val refErrors = validateRefs(nameMapping)
        if (refErrors.isEmpty) {
          val mappingWithEnums = addEnums(nameMapping)
          JsSuccess(wire(mappingWithEnums))
        }
        else {
          JsError(s"Meta model contains invalid references: ${refErrors.mkString(", ")}")
        }
      }
    }

    def wire(mapping: Map[String, MObject]): Map[String, MObject] = {
      val builder = new {
        val finalMap: Map[String, MObject] = mapping.mapValues {
          _ match {
            case c: MClass => c.updateLinks(
              wireSuperTypes(finalMap, c.superTypes),
              wireRefLinks(finalMap, c.inputs),
              wireRefLinks(finalMap, c.outputs)
            )
            case r: MReference => r.updateLinks(
              wireClassLinks(finalMap, r.source),
              wireClassLinks(finalMap, r.target)
            )
            case e: MEnum => e
          }
        }
      }
      builder.finalMap
    }

    def validateRefs(mapping: Map[String, MObject]): List[String] = {
      val values = mapping.values.toList

      def checkSingleEnum(source: String, enum: MEnum, default: EnumSymbol): List[String] = {
        mapping.get(enum.name) match {
          case Some(MEnum(_, symbols)) => {
            if (symbols.map(_.name).contains(default.name)) Nil
            else {
              s"$source: default value '${default.name}' is not part of enum '${enum.name}'" :: Nil
            }
          }
          case _ => s"$source: enum '${enum.name}' wasn't found" :: Nil
        }
      }

      def checkEnums(source: String, attributes: Seq[MAttribute]): List[String] = {
        attributes.foldLeft(List[String]()) { (errors, attribute) =>
          (attribute.`type`, attribute.default) match {
            case (e: MEnum, s: EnumSymbol) => errors ::: checkSingleEnum(source, e, s)
            case _ => errors
          }
        }
      }

      def checkSuperTypes(source: String, superTypes: Seq[MClass]): List[String] = {
        for (
          s <- superTypes.toList;
          target = s.name
          if !mapping.contains(target) || !mapping.get(target).get.isInstanceOf[MClass]
        ) yield {
          s"invalid super type: '$source' -> '$target' ('$target' is missing or doesn't match expected type)"
        }
      }

      def checkLinkDefs[T](source: String, links: Seq[MLinkDef]): List[String] = {
        for (
          link <- links.toList;
          target = link.mType.name
          if !mapping.contains(target) || !mapping.get(target).get.isInstanceOf[T]
        ) yield {
          s"invalid MLinkDef: '$source' -> '$target' ('$target' is missing or doesn't match expected type)"
        }
      }

      def processMReference(r: MReference): List[String] = {
        checkLinkDefs[MClass](r.name, r.source) :::
          checkLinkDefs[MClass](r.name, r.target) :::
          checkEnums(r.name, r.attributes)
      }

      def processMClass(c: MClass): List[String] = {
        checkLinkDefs[MReference](c.name, c.inputs) :::
          checkLinkDefs[MReference](c.name, c.outputs) :::
          checkSuperTypes(c.name, c.superTypes) :::
          checkEnums(c.name, c.attributes)
      }

      def processMObject(mObj: MObject): List[String] = mObj match {
        case c: MClass => processMClass(c)
        case r: MReference => processMReference(r)
        case _ => Nil
      }

      @tailrec
      def walk(remaining: List[MObject], accErrors: List[String]): List[String] = {
        remaining match {
          case Nil => accErrors
          case mObj :: tail => walk(tail, accErrors ::: processMObject(mObj))
        }
      }

      walk(values, Nil)
    }


  }

  val mTypeError = ValidationError("Unknown mType at top level: only MClass, MReference and MEnum allowed")
  val boundsError = ValidationError("invalid lower and/or upper bound")
  val typeDefaultError = ValidationError("type definition and default value don't match")
  val enumSymbolError = ValidationError("Enum symbols must be unique and not empty")

  def boundsCheck(bounds: MBounds) = {
    (bounds.upperBound > bounds.lowerBound) ||
      (bounds.upperBound == bounds.lowerBound && bounds.lowerBound != 0) ||
      (bounds.upperBound == -1)
  }

  def mTypeRead(mType: String): Reads[String] = {
    (__ \ "mType").read[String].filter(mTypeError)(_ == mType)
  }

  val checkMClassReads: Reads[MClass] = mTypeRead("mClass").flatMap(_ => mClassReads)
  val checkMReferenceReads: Reads[MReference] = mTypeRead("mReference").flatMap(_ => mReferenceReads)
  val checkMEnumReads: Reads[MEnum] = mTypeRead("mEnum").flatMap(_ => mEnumReads)

  implicit val linkDefReads: Reads[MLinkDef] = (
    (__ \ "type").read[String].map(Dummy(_)) and
      (__ \ "upperBound").read[Int](min(-1)) and
      (__ \ "lowerBound").read[Int](min(0)) and
      (__ \ "deleteIfLower").read[Boolean]
    ) (MLinkDef.apply _).filter(boundsError) {
    boundsCheck(_)
  }

  def detectType(name: String) = name match {
    case "String" => ScalarType.String
    case "Bool" => ScalarType.Bool
    case "Int" => ScalarType.Int
    case "Double" => ScalarType.Double
    case _ => MEnum(name, Seq.empty)
  }

  implicit val attributeValueReads = new Reads[AttributeValue] {
    override def reads(json: JsValue) = json match {
      case JsString(s) => JsSuccess(ScalarValue.MString(s))
      case JsBoolean(b) => JsSuccess(ScalarValue.MBool(b))
      case JsNumber(n) => JsSuccess(ScalarValue.MDouble(n.doubleValue))
      case _ => JsError("invalid attribute type")
    }
  }

  implicit val mAttributeReads: Reads[MAttribute] = (
    (__ \ "name").read[String] and
      (__ \ "globalUnique").read[Boolean] and
      (__ \ "localUnique").read[Boolean] and
      (__ \ "type").read[String].map(detectType(_)) and
      (__ \ "default").read[AttributeValue] and
      (__ \ "constant").read[Boolean] and
      (__ \ "singleAssignment").read[Boolean] and
      (__ \ "expression").read[String] and
      (__ \ "ordered").read[Boolean] and
      (__ \ "transient").read[Boolean] and
      (__ \ "upperBound").read[Int](min(-1)) and
      (__ \ "lowerBound").read[Int](min(0))
    ) (MAttribute.apply _).filter(boundsError) {
    boundsCheck(_)
  }.filter(typeDefaultError) { att =>
    (att.`type`, att.default) match {
      case (ScalarType.String, ScalarValue.MString(_)) => true
      case (ScalarType.Bool, ScalarValue.MBool(_)) => true
      case (ScalarType.Double, ScalarValue.MDouble(_)) => true
      case (ScalarType.Int, ScalarValue.MDouble(d)) if d <= Int.MaxValue => true
      case (MEnum(_, _), ScalarValue.MString(_)) => true
      case _ => false
    }
  }.map { att =>
    (att.`type`, att.default) match {
      case (m: MEnum, ScalarValue.MString(s)) => att.copy(default = EnumSymbol(s, m))
      case (ScalarType.Int, ScalarValue.MDouble(d)) => att.copy(default = ScalarValue.MInt(d.toInt))
      case _ => att
    }
  }

  implicit val mClassReads: Reads[MClass] = (
    (__ \ "name").read[String](minLength[String](1)) and
      (__ \ "abstract").read[Boolean] and
      (__ \ "superTypes").read[Seq[String]].map(_.map(empty.mClass(_))) and
      (__ \ "inputs").read[Seq[MLinkDef]] and
      (__ \ "outputs").read[Seq[MLinkDef]] and
      (__ \ "attributes").read[Seq[MAttribute]]
    ) (MClass.apply _)

  implicit val mReferenceReads: Reads[MReference] = (
    (__ \ "name").read[String](minLength[String](1)) and
      (__ \ "sourceDeletionDeletesTarget").read[Boolean] and
      (__ \ "targetDeletionDeletesSource").read[Boolean] and
      (__ \ "source").read[Seq[MLinkDef]] and
      (__ \ "target").read[Seq[MLinkDef]] and
      (__ \ "attributes").read[Seq[MAttribute]]
    ) (MReference.apply _)

  implicit val mEnumReads: Reads[MEnum] = (
    (__ \ "name").read[String](minLength[String](1)) and
      (__ \ "symbols").read[Seq[String]].map(_.map(EnumSymbol(_, MEnum("", Seq.empty))))
    ) (MEnum.apply _).filter(enumSymbolError) { enum =>
    enum.values.size > 0 && enum.values.distinct.size == enum.values.size
  }.map { enum =>
    val builder = new {
      val finalEnum: MEnum = enum.copy(values = enum.values.map(s => new EnumSymbol(s.name, finalEnum)))
    }
    builder.finalEnum
  }


}
