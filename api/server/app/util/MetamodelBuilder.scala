package util

import util.domain._
import play.api.Logger
import play.api.libs.json._

class MetamodelBuilder {
//  private var enums:Map[String, MEnum] = Map()
//  private var classes:Map[String, MClass] = Map()
//  private var references:Map[String, MReference] = Map()
//  private var tmpClasses:Map[String, MClass] = Map()
//
//  private val log = Logger(this getClass() getName())
//
//  /**
//   * Returns Scala representation of JSON Metamodel
//   * @param json JsObject containing MoDiGen metamodel in JSON representation
//   * @return Metamodel
//   */
  def fromJson(json:JsObject) = { new Metamodel(Map[String, MClass](), Map[String, MReference](), Map[String, MEnum]()) }
//    enums = extractMEnums(json.fieldSet.toSet).map(tuple => tuple._1 -> buildMEnum(tuple._2)).toMap
//    val jsonClasses = extractMClasses(json.fieldSet.toSet).toMap
//    val linearisedMClasses = lineariseMClasses(jsonClasses.map(tuple => tuple._1 -> (tuple._2 \ "superTypes").as[JsArray].value.toList))
//    classes = buildMClasses(linearisedMClasses, jsonClasses)
//    val jsonReferences = extractMReferences(json.fieldSet.toSet).toList
//    references = jsonReferences.map(tuple => tuple._1 -> buildMReference(tuple._2)).toMap
//
//    jsonClasses.foreach(x => addMLinkDefsToMClass(x._2))
//    jsonReferences.foreach(x => addMLinkDefsToMReference(x._2))
//
//    new Metamodel(classes, references, enums)
//  }
//
//  /**
//   * Extracts MEnums from JSON
//   * @param objects JSON objects as fieldSet
//   * @return Map[String, MEnum]
//   */
//  private def extractMEnums(objects:Set[(String, JsValue)]) = objects.filter(tuple => (tuple._2 \ "mType").as[String] == "mEnum")
//
//  /**
//   * Extracts MClasses from JSON
//   * @param objects JSON objects as fieldSet
//   * @return  Map[String, JsValue]
//   */
//  private def extractMClasses(objects:Set[(String, JsValue)]) = objects.filter(tuple => (tuple._2 \ "mType").as[String] == "mClass")
//
//  /**
//   * Extracts MReferences from JSON
//   * @param objects JSON objects as fieldSet
//   * @return Map[String, JsValue]
//   */
//  private def extractMReferences(objects:Set[(String, JsValue)]) = objects.filter(tuple => (tuple._2 \ "mType").as[String] == "mRef")
//
//  /**
//   * Determines the order in which MClasses must be created in order to preserve the type hierarchy (i.e. create supertypes before subtypes)
//   * @param classes Map of MClass names to their MClasses in JSON representation
//   * @return List[String]
//   */
//  private def lineariseMClasses(classes:Map[String, List[JsValue]]) = {
//    val classmap = classes.map(x => x._1 -> x._2.map(s => s.as[String]))
//    val linearised = classmap.filter(x => x._2.isEmpty).keys.toList //classes with no supertypes can be added right away
//    linearisation(linearised, classmap.filter(x => x._2.nonEmpty))
//  }
//
//  /**
//   * Recursive method called to determine the order in which MClasses must be created
//   * @param linearised List of classnames that were already put in order
//   * @param classmap Map of remaining classnames (key) and their supertypes (value)
//   * @return List[String]
//   */
//  private def linearisation(linearised:List[String], classmap:Map[String, List[String]]):List[String] = {
//    if(classmap.isEmpty) //recursion stop condition - no more classes left
//      linearised.reverse //Classes were in reverse order because prepending to a list is cheaper
//    else{
//      val next = findNextClass(linearised, classmap.toList)
//      val newClassmap = classmap - next
//      val newLinearised = next::linearised
//      linearisation(newLinearised, newClassmap)
//    }
//  }
//
//  /**
//   * Recursive method to find next classname to be put in orer
//   * @param linearised List of classnames that were already put in order
//   * @param classmap Map of classnames not yet checked (key) and their supertypes (value)
//   * @return String
//   * @throws IllegalArgumentException if no more classes are left to check
//   */
//  private def findNextClass(linearised:List[String], classmap:List[(String, List[String])]):String = {
//    val head::tail = classmap
//    if (classmap.isEmpty) // recursion stop condition, linerisation failed because all classnames were checked but non are eligible
//      throw new IllegalArgumentException("MClasses cannot be linearised.")
//    else if(head._2.forall(c => linearised contains c)) //recursion stop condition, all supertypes of this class are already in the order, class can be used
//      head._1
//    else
//      findNextClass(linearised, tail)
//  }
//
//  /**
//   * Create MEnum from JSON representation
//   * @param json JSON representation of an M_Enum
//   * @return MEnum
//   */
//  private def buildMEnum(json:JsValue) = {
//    val name = (json \ "name").as[String]
//    val values = (json \ "values").as[JsArray].value.toList
//
//    values.head match {
//      case num:JsNumber => new MEnumNumber(values.map(n => n.as[Double]), name)
//      case str:JsString => new MEnumString(values.map(s => s.as[String]), name)
//    }
//  }
//
//  /**
//   * Create MClasses in given order from JSON representation
//   * @param order list of classnames in the order in which MClasses should be created
//   * @param jsonMap JSON M_Classes mapped by their name
//   * @return Map[String, MClass]
//   */
//  private def buildMClasses(order:List[String], jsonMap:Map[String, JsValue]) = {
//    order.map(classname => classname -> buildMClass(jsonMap.get(classname))).toMap
//  }
//
//  /**
//   * Build MClass from JSOn representation
//   * @param jsonOpt Option, possibly containing JSON representation of M_Class
//   * @return MClass
//   * @throws IllegalArgumentException if there is no class by that name (pointing to a possible linearisation problem)
//   */
//  private def buildMClass(jsonOpt:Option[JsValue]) = {
//    jsonOpt match {
//      case None => throw new IllegalArgumentException("Classname not represented")
//      case Some(json) =>
//        val name = (json \ "name").as[String]
//        val superTypes = (json \ "superTypes").as[JsArray].value.map(cls => extractSupertype(tmpClasses.get(cls.as[String]))).toList
//        val attributes = (json \ "mAttributes").as[JsObject].fieldSet.map(x => buildMAttribute(x._2)).toList
//        val isAbstract = getOptionalBoolean(json \ "abstract")
//        val cls = MClass(name, superTypes, attributes, isAbstract)
//        tmpClasses = tmpClasses + (cls.name -> cls)
//        cls
//    }
//  }
//
//  /**
//   * Gets an MClass from an Option of MClass
//   * @param opt option, possibly containing an MClass
//   * @return MClass
//   * @throws IllegalArgumentException if MCkass is missing
//   */
//  private def extractSupertype(opt:Option[MClass]) = opt match {
//    case Some(cls) => cls
//    case None => throw new IllegalArgumentException("Linearisation Error. Supertype not found.")
//  }
//
//  /**
//   * Creates an MReference from JSON representation
//   * @param json JSON representation of an M_Reference
//   * @return MReference
//   */
//  private def buildMReference(json:JsValue) = {
//    val name = (json \ "name").as[String]
//    val attributes = (json \ "mAttributes").as[JsObject].fieldSet.map(x => buildMAttribute(x._2)).toList
//    val targetDeletesSource = getOptionalBoolean(json \ "targetDeletionDeletesSource")
//    val sourceDeletesTarget = getOptionalBoolean(json \ "sourceDeletionDeletesTarget")
//    new MReference(name, attributes, targetDeletesSource, sourceDeletesTarget)
//  }
//
//  /**
//   * Creates an MAttribute from JSON representation
//   * @param json JSON representation of an M_Attribute
//   * @return MAttribute
//   */
//  private def buildMAttribute(json:JsValue) = {
//    val name = (json \ "name").as[String]
//    val upperBound = (json \ "upperBound").as[Int]
//    val lowerBound = (json \ "lowerBound").as[Int]
//    val uniqueLocal = getOptionalBoolean(json \ "uniqueLocal")
//    val uniqueGlobal = getOptionalBoolean(json \ "uniqueGlobal")
//    val singleAssignment = getOptionalBoolean(json \ "singleAssignment")
//    val ordered = getOptionalBoolean(json \ "ordered")
//    val transient = getOptionalBoolean(json \ "transient")
//    val constant = getOptionalBoolean(json \ "constant")
//
//    (json\"type").as[String] match {
//      case "Integer" => new MAttributeNumber(name, upperBound, lowerBound, (json\"default").asOpt[Double], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
//      case "Float" => new MAttributeNumber(name, upperBound, lowerBound, (json\"default").asOpt[Double], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
//      case "String" => new MAttributeString(name, upperBound, lowerBound, (json\"default").asOpt[String], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
//      case "Boolean" => new MAttributeBoolean(name, upperBound, lowerBound, (json\"default").asOpt[Boolean], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
//      case enum:String => enums.get(enum) match {
//        case Some(x) => x match {
//          case en:MEnumNumber => new MAttributeMEnumNumber(en, name, upperBound, lowerBound, (json\"default").asOpt[Double], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
//          case es:MEnumString => new MAttributeMEnumString(es, name, upperBound, lowerBound, (json \"default").asOpt[String], uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant)
//        }
//        case None => throw new NoSuchElementException("No enum named" + enum + "contained in metamodel.")
//      }
//    }
//  }
//
//  /**
//   * Creates MLinkDefs for an M_Class and adds it to the MClass
//   * @param json JSON representation of the M_Class
//   * @throws NoSuchElementException if no MClass exists for this M_Class
//   */
//  private def addMLinkDefsToMClass(json:JsValue) = {
//    val clazz = classes.get((json\"name").as[String]) match {
//      case Some(x) => x
//      case None => throw new NoSuchElementException("Class not found.")
//    }
//
//    val inputs = (json \ "inputs").as[JsArray].value.map(buildMLinkDefsForMClass)
//    val outputs = (json \ "outputs").as[JsArray].value.map(buildMLinkDefsForMClass)
//
//    clazz.inputs = inputs.toList
//    clazz.outputs = outputs.toList
//  }
//
//  /**
//   * Creates MLinkDefs for an M_Reference and adds it to the MReference
//   * @param json JSON representation of the M_Reference
//   * @throws NoSuchElementException if no MReference exists for this M_Reference
//   */
//  private def addMLinkDefsToMReference(json:JsValue) = {
//    val ref = references.get((json\"name").as[String]) match {
//      case Some(x) => x
//      case None => throw new NoSuchElementException("Reference not found.")
//    }
//
//    val source = (json \ "source").as[JsArray].value.map(buildMLinkDefsForMReference)
//    val target = (json \ "target").as[JsArray].value.map(buildMLinkDefsForMReference)
//
//    ref.source = source.toList
//    ref.target = target.toList
//  }
//
//  /**
//   * Creates an MLinkDef from JSON representation
//   * @param json JSON representation of an M_Link_Def
//   * @return MLinkDef
//   * @throws NoSuchElementException if the referenced MReference doesn't exist
//   */
//  private def buildMLinkDefsForMClass(json:JsValue) = {
//    val _type = references.get((json \ "type").as[String]) match {
//      case Some(x) => x
//      case None => throw new NoSuchElementException("Reference not found")
//    }
//    val upperBound = (json \ "upperBound").as[Int]
//    val lowerBound = (json \ "lowerBound").as[Int]
//    val deleteIfLower = getOptionalBoolean(json \ "deleteIfLower")
//
//    new MLinkDef(_type, upperBound, lowerBound, deleteIfLower)
//  }
//
///**
// * Creates an MLinkDef from JSON representation
// * @param json JSON representation of an M_Link_Def
// * @return MLinkDef
// * @throws NoSuchElementException if the referenced MClass doesn't exist
// */
//  private def buildMLinkDefsForMReference(json:JsValue) = {
//    val _type = classes.get((json \ "type").as[String]) match {
//      case Some(x) => x
//      case None => throw new NoSuchElementException("Reference not found")
//    }
//    val upperBound = (json \ "upperBound").as[Int]
//    val lowerBound = (json \ "lowerBound").as[Int]
//    val deleteIfLower = getOptionalBoolean(json \ "deleteIfLower")
//
//    new MLinkDef(_type, upperBound, lowerBound, deleteIfLower)
//  }
//
//  /**
//   * For optional boolean attributes, this retrieves either the boolean value, or a default value of false
//   * @param value JSON Value for the attribute
//   * @return Boolean
//   */
//  private def getOptionalBoolean(value:JsValue) = value.asOpt[Boolean] match {
//    case Some(x) => x
//    case None => false
//  }
}

object MetamodelBuilder{
  def apply() = new MetamodelBuilder
}