package modigen.util

import modigen.util.domain._
import play.api.Logger
import play.api.libs.json._

class MetamodelBuilder {
  var enums:Map[String, MEnum] = Map()
  var classes:Map[String, MClass] = Map()
  var references:Map[String, MReference] = Map()
  var tmpClasses:Map[String, MClass] = Map()

  val log = Logger(this getClass() getName())

  def fromJson(json:JsObject) = {
    enums = extractMEnums(json.fieldSet.toSet).map(tuple => tuple._1 -> buildMEnum(tuple._2)).toMap
    val jsonClasses = extractMClasses(json.fieldSet.toSet).toMap
    val linearisedMClasses = lineariseMClasses(jsonClasses.map(tuple => tuple._1 -> (tuple._2 \ "superTypes").as[JsArray].value.toList))
    classes = buildMClasses(linearisedMClasses, jsonClasses)
    val jsonReferences = extractMReferences(json.fieldSet.toSet).toList
    references = jsonReferences.map(tuple => tuple._1 -> buildMReference(tuple._2)).toMap

    jsonClasses.foreach(x => addMLinkDefsToMClass(x._2))
    jsonReferences.foreach(x => addMLinkDefsToMReference(x._2))

    new Metamodel(classes, references, enums)
  }

  private def extractMEnums(objects:Set[(String, JsValue)]) = objects.filter(tuple => (tuple._2 \ "mType").as[String] == "mEnum")
  private def extractMClasses(objects:Set[(String, JsValue)]) = objects.filter(tuple => (tuple._2 \ "mType").as[String] == "mClass")
  private def extractMReferences(objects:Set[(String, JsValue)]) = objects.filter(tuple => (tuple._2 \ "mType").as[String] == "mRef")

  private def lineariseMClasses(classes:Map[String, List[JsValue]]) = {
    val classmap = classes.map(x => x._1 -> x._2.map(s => s.as[String]))
    val linearised = classmap.filter(x => x._2.isEmpty).keys.toList
    linearisation(linearised, classmap.filter(x => x._2.nonEmpty))
  }

  private def linearisation(linearised:List[String], classmap:Map[String, List[String]]):List[String] = {
    if(classmap.isEmpty)
      linearised.reverse
    else{
      val next = findNextClass(linearised, classmap.toList)
      val newClassmap = classmap - next
      val newLinearised = next::linearised
      linearisation(newLinearised, newClassmap)
    }
  }

  private def findNextClass(linearised:List[String], classmap:List[(String, List[String])]):String = {
    val head::tail = classmap
    if (classmap.isEmpty)
      throw new IllegalArgumentException("MClasses cannot be linearised.")
    else if(head._2.forall(c => linearised contains c))
      head._1
    else
      findNextClass(linearised, tail)
  }

  private def buildMEnum(json:JsValue) = {
    val name = (json \ "name").as[String]
    val values = (json \ "values").as[JsArray].value.toList

    values.head match {
      case num:JsNumber => new MEnumNumber(values.map(n => n.as[Double]), name)
      case str:JsString => new MEnumString(values.map(s => s.as[String]), name)
    }
  }

  private def buildMClasses(order:List[String], jsonMap:Map[String, JsValue]) = {
    order.map(classname => classname -> buildMClass(jsonMap.get(classname))).toMap
  }

  private def buildMClass(jsonOpt:Option[JsValue]) = {
    jsonOpt match {
      case None => throw new IllegalArgumentException("Classname not represented")
      case Some(json) =>
        val name = (json \ "name").as[String]
        val superTypes = (json \ "superTypes").as[JsArray].value.map(cls => extractSupertype(tmpClasses.get(cls.as[String]))).toList
        val attributes = (json \ "mAttributes").as[JsObject].fieldSet.map(x => buildMAttribute(x._2)).toList
        val isAbstract = getOptionalBoolean(json \ "abstract")
        val cls = MClass(name, superTypes, attributes, isAbstract)
        tmpClasses = tmpClasses + (cls.name -> cls)
        cls
    }
  }

  private def extractSupertype(opt:Option[MClass]) = opt match {
    case Some(cls) => cls
    case None => throw new IllegalArgumentException("Linearisation Error. Supertype not found.")
  }

  private def buildMReference(json:JsValue) = {
    val name = (json \ "name").as[String]
    val attributes = (json \ "mAttributes").as[JsObject].fieldSet.map(x => buildMAttribute(x._2)).toList
    val targetDeletesSource = getOptionalBoolean(json \ "targetDeletionDeletesSource")
    val sourceDeletesTarget = getOptionalBoolean(json \ "sourceDeletionDeletesTarget")
    new MReference(name, attributes, targetDeletesSource, sourceDeletesTarget)
  }

  private def buildMAttribute(json:JsValue) = {
    val name = (json \ "name").as[String]
    val upperBound = (json \ "upperBound").as[Int]
    val lowerBound = (json \ "lowerBound").as[Int]
    val uniqueLocal = getOptionalBoolean(json \ "uniqueLocal")
    val uniqueGlobal = getOptionalBoolean(json \ "uniqueGlobal")
    val singleAssignment = getOptionalBoolean(json \ "singleAssignment")
    val ordered = getOptionalBoolean(json \ "ordered")
    val transient = getOptionalBoolean(json \ "transient")
    val constant = getOptionalBoolean(json \ "constant")

    (json\"type").as[String] match {
      case "Integer" => new MAttributeNumber(name, upperBound, lowerBound, (json\"default").asOpt[Double], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
      case "Float" => new MAttributeNumber(name, upperBound, lowerBound, (json\"default").asOpt[Double], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
      case "String" => new MAttributeString(name, upperBound, lowerBound, (json\"default").asOpt[String], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
      case "Boolean" => new MAttributeBoolean(name, upperBound, lowerBound, (json\"default").asOpt[Boolean], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
      case enum:String => enums.get(enum) match {
        case Some(x) => x match {
          case en:MEnumNumber => new MAttributeMEnumNumber(en, name, upperBound, lowerBound, (json\"default").asOpt[Double], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
          case es:MEnumString => new MAttributeMEnumString(es, name, upperBound, lowerBound, (json \"default").asOpt[String], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
        }
        case None => throw new NoSuchElementException("No enum named" + enum + "contained in metamodel.")
      }
    }
  }

  private def addMLinkDefsToMClass(json:JsValue) = {
    val clazz = classes.get((json\"name").as[String]) match {
      case Some(x) => x
      case None => throw new NoSuchElementException("Class not found.")
    }

    val inputs = (json \ "inputs").as[JsArray].value.map(buildMLinkDefsForMClass)
    val outputs = (json \ "outputs").as[JsArray].value.map(buildMLinkDefsForMClass)

    clazz.inputs = inputs.toList
    clazz.outputs = outputs.toList
  }

  private def addMLinkDefsToMReference(json:JsValue) = {
    val ref = references.get((json\"name").as[String]) match {
      case Some(x) => x
      case None => throw new NoSuchElementException("Reference not found.")
    }

    val source = (json \ "source").as[JsArray].value.map(buildMLinkDefsForMReference)
    val target = (json \ "target").as[JsArray].value.map(buildMLinkDefsForMReference)

    ref.source = source.toList
    ref.target = target.toList
  }

  private def buildMLinkDefsForMClass(json:JsValue) = {
    val _type = references.get((json \ "type").as[String]) match {
      case Some(x) => x
      case None => throw new NoSuchElementException("Reference not found")
    }
    val upperBound = (json \ "upperBound").as[Int]
    val lowerBound = (json \ "lowerBound").as[Int]
    val deleteIfLower = getOptionalBoolean(json \ "deleteIfLower")

    new MLinkDef(_type, upperBound, lowerBound, deleteIfLower)
  }

  private def buildMLinkDefsForMReference(json:JsValue) = {
    val _type = classes.get((json \ "type").as[String]) match {
      case Some(x) => x
      case None => throw new NoSuchElementException("Reference not found")
    }
    val upperBound = (json \ "upperBound").as[Int]
    val lowerBound = (json \ "lowerBound").as[Int]
    val deleteIfLower = getOptionalBoolean(json \ "deleteIfLower")

    new MLinkDef(_type, upperBound, lowerBound, deleteIfLower)
  }

  private def getOptionalBoolean(value:JsValue) = value.asOpt[Boolean] match {
    case Some(x) => x
    case None => false
  }
}

object MetamodelBuilder{
  def apply() = new MetamodelBuilder
}