package de.htwg.zeta.common.format.model

import scala.annotation.tailrec
import scala.collection.immutable.List
import scala.collection.immutable.Seq
import scala.collection.mutable

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsError
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
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
      case StringType => jsValues.flatMap(parseList("JsString") { case JsString(s) => JsSuccess(StringValue(s)) })
      case BoolType => jsValues.flatMap(parseList("JsBoolean") { case JsBoolean(b) => JsSuccess(BoolValue(b)) })
      case IntType => jsValues.flatMap(parseList("JsNumber") { case CheckValidInt(i) => JsSuccess(IntValue(i)) })
      case DoubleType => jsValues.flatMap(parseList("JsNumber") { case JsNumber(n) => JsSuccess(DoubleValue(n.toDouble)) })
      case MEnum(name, values) =>
        val set = values.toSet
        jsValues.flatMap(parseList("JsString") {
          case JsString(s) =>
            if (set.contains(s)) {
              JsSuccess(EnumValue(s, name))
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
    case StringValue(s) => JsString(s)
    case BoolValue(b) => JsBoolean(b)
    case IntValue(i) => JsNumber(BigDecimal(i))
    case DoubleValue(d) => JsNumber(BigDecimal(d))
    case EnumValue(s, _) => JsString(s)
  }


  override def writes(o: Map[String, Seq[AttributeValue]]): JsValue = {
    val list: List[(String, JsArray)] = o.toList.map(p => (p._1, JsArray(p._2.map(writeAttributeValue))))
    JsObject(list)
  }
}
