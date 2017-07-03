package de.htwg.zeta.server.controller.restApi.modelUiFormat

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.immutable.List
import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import play.api.libs.json.JsNumber
import play.api.libs.json.JsResult
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.JsError
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Writes


class AttributeFormat private(mAttributes: Seq[MAttribute], objectName: String)
  extends Reads[Map[String, List[AttributeValue]]] with Writes[Map[String, Seq[AttributeValue]]] {

  private val attributeBoundsCheck = JsError("Attribute bounds check failed")

  // used to validate JsLookup. JsValue has flatMap. JsLookup hasn't
  private object ToJsResult extends Reads[JsValue] {
    override def reads(json: JsValue): JsResult[JsValue] = JsSuccess(json)
  }

  private def parseList[AV](jsTypeAsString: String)(pf: PartialFunction[JsValue, JsResult[AV]])(jsValues: List[JsValue]): JsResult[List[AV]] = {
    @tailrec
    def fold(res: JsResult[List[AV]], jsList: List[JsValue]): JsResult[List[AV]] = {
      (res, jsList) match {
        case (e: JsError, _) => e
        case (s @ JsSuccess(_, _), Nil) => s
        case (JsSuccess(list, _), jsv :: tail) =>
          if (pf.isDefinedAt(jsv)) {
            fold(pf(jsv).map(_ :: list), tail)
          } else {
            JsError(s"Json: $jsv is not of type $jsTypeAsString")
          }
      }
    }

    fold(JsSuccess(Nil), jsValues).map(_.reverse)
  }

  private object CheckValidInt {
    def unapply(jsn: JsNumber): Option[Int] = try {
      Some(jsn.value.toIntExact)
    } catch {
      case _: java.lang.ArithmeticException => None
    }
  }

  private def checkAttributeBounds(la: List[AttributeValue], ma: MAttribute): Boolean = {
    val laSize = la.size
    val lowerBound = laSize >= ma.lowerBound
    val upperBound = laSize <= ma.upperBound || ma.upperBound == -1
    lowerBound && upperBound
  }

  // Cyclomatic complexity is greater 10
  // scalastyle:off cyclomatic.complexity
  private def parseAttribute(json: JsValue, ma: MAttribute): JsResult[(String, List[AttributeValue])] = {
    val jsValues: JsResult[List[JsValue]] = json.\(ma.name).validate(Reads.list(ToJsResult))
    val attributeValues: JsResult[List[AttributeValue]] = ma.typ match {
      case StringType => jsValues.flatMap(parseList("JsString") { case JsString(s) => JsSuccess(MString(s)) })
      case BoolType => jsValues.flatMap(parseList("JsBoolean") { case JsBoolean(b) => JsSuccess(MBool(b)) })
      case IntType => jsValues.flatMap(parseList("JsNumber") { case CheckValidInt(i) => JsSuccess(MInt(i)) })
      case DoubleType => jsValues.flatMap(parseList("JsNumber") { case JsNumber(n) => JsSuccess(MDouble(n.toDouble)) })
      case MEnum(name, values) =>
        val set = values.toSet
        jsValues.flatMap(parseList("JsString") {
          case JsString(s) =>
            if (set.contains(s)) {
              JsSuccess(EnumSymbol(s, name))
            } else {
              JsError(s"Found element: $s isn't a valid symbol of enum $name")
            }
        })
    }
    attributeValues.flatMap(l =>
      if (checkAttributeBounds(l, ma)) {
        JsSuccess((ma.name, l))
      } else {
        attributeBoundsCheck
      }
    )
  }

  // scalastyle:on


  override def reads(json: JsValue): JsResult[Map[String, List[AttributeValue]]] = {
    @tailrec
    def fold(res: JsResult[List[(String, List[AttributeValue])]], attrList: List[MAttribute]): JsResult[List[(String, List[AttributeValue])]] = {
      (res, attrList) match {
        case (e: JsError, _) => e
        case (s @ JsSuccess(_, _), Nil) => s
        case (JsSuccess(list, _), ma :: tail) =>
          fold(parseAttribute(json, ma).map(_ :: list), tail)
      }
    }

    val jsRes: JsResult[List[(String, List[AttributeValue])]] = fold(JsSuccess(Nil), mAttributes.toList)
    val set: mutable.HashSet[String] = mutable.HashSet()
    jsRes.flatMap(list => list.find(elem => !set.add(elem._1.toLowerCase)) match {
      case Some(e) => JsError(s"object: $objectName may contain the same attribute only once. Attributes are case insensitive, duplicate is: ${e._1}")
      case None => JsSuccess(list.toMap)
    })
  }

  override def writes(o: Map[String, Seq[AttributeValue]]): JsValue = AttributeFormat.writes(o)
}

object AttributeFormat extends Writes[Map[String, Seq[AttributeValue]]] {

  def apply(mAttributes: Seq[MAttribute], objectName: String): AttributeFormat = new AttributeFormat(mAttributes, objectName)

  private def writeAttributeValue(av: AttributeValue): JsValue = av match {
    case MString(s) => JsString(s)
    case MBool(b) => JsBoolean(b)
    case MInt(i) => JsNumber(BigDecimal(i))
    case MDouble(d) => JsNumber(BigDecimal(d))
    case EnumSymbol(s, _) => JsString(s)
  }


  override def writes(o: Map[String, Seq[AttributeValue]]): JsValue = {
    val list: List[(String, JsArray)] = o.toList.map(p => (p._1, JsArray(p._2.map(writeAttributeValue))))
    JsObject(list)
  }
}