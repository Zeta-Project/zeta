package de.htwg.zeta.server.controller.restApi

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MObject
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MBounds
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import play.api.data.validation.ValidationError
import play.api.libs.json.Reads
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsResult
import play.api.libs.json.Format
import play.api.libs.json.JsNumber
import play.api.libs.json.JsString
import play.api.libs.json.Writes
import play.api.libs.json.JsBoolean

/**
 */
object MetaModelUiJsonFormat {

  val metaModelFormat: Format[MetaModel] = Format(MetaModelReads, Writes(writeJson))

  def writeJson(mm: MetaModel): JsValue = {
    null
  }

  object MetaModelReads extends Reads[MetaModel] {

    private def checkObjectsUnique(elems: List[MObject]): JsResult[List[MObject]] = {
      val set: mutable.Set[String] = mutable.HashSet()

      if (elems.map(_.name).forall(set.add)) {
        JsSuccess(elems)
      } else {
        JsError("MObjects must have unique names")
      }
    }

    private def check[M](source: String, checkType: String, contains: String => Boolean, supply: M => String)(seq: Seq[M]): List[String] = {
      for {
        elem: M <- seq.toList
        target: String = supply(elem) if !contains(target)
      } yield {
        s"invalid $checkType: '$source' -> '$target' ('$target' is missing or doesn't match expected type)"
      }
    }


    private def processMReference(refs: Map[String, MClass])(r: MReference): List[String] = {
      val higherCheck = check[MClassLinkDef](r.name, "MClassLinkDef", refs.contains, _.className) _
      higherCheck(r.source) ::: higherCheck(r.target)
    }


    private def processMClass(refs: Map[String, MReference], classes: Map[String, MClass])(c: MClass): List[String] = {
      val checkLink = check[MReferenceLinkDef](c.name, "MReferenceLinkDef", refs.contains, _.referenceName) _
      val checkSuper = check[String](c.name, "super type", classes.contains, s => s) _
      checkLink(c.inputs) ::: checkLink(c.outputs) ::: checkSuper(c.superTypeNames)
    }


    @tailrec
    private def walk[MO](process: MO => List[String])(remaining: List[MO], accErrors: List[String]): List[String] = {
      remaining match {
        case Nil => accErrors
        case mObj :: tail => walk(process)(tail, accErrors ::: process(mObj))
      }
    }

    private def validateMLinks(mm: MetaModel): JsResult[MetaModel] = {
      val classErrors = walk(processMClass(mm.referenceMap, mm.classMap))(mm.classes.toList, Nil)
      val totalErrors = walk(processMReference(mm.classMap))(mm.references.toList, classErrors)

      if (totalErrors.isEmpty) {
        JsSuccess(mm)
      } else {
        JsError(s"Meta model contains invalid references: ${totalErrors.mkString(", ")}")
      }
    }

    override def reads(json: JsValue): JsResult[MetaModel] = {
      val elems = json.\("elements")
      val mm: JsResult[MetaModel] =
        for {
          name <- json.\("name").validate[String]
          uiState <- json.\("uiState").validate[String]
          enumOpt <- elems.validate(Reads.list(MEnumOptReads))
          elements <- elems.validate(Reads.list(new MObjectReads(enumOpt))).flatMap(checkObjectsUnique)
        } yield {
          val (classes, references, enums) =
            elements.foldLeft((List[MClass](), List[MReference](), List[MEnum]()))((trip, mo) => (trip, mo) match {
              case ((cls, refs, ens), mc: MClass) => (mc :: cls, refs, ens)
              case ((cls, refs, ens), mr: MReference) => (cls, mr :: refs, ens)
              case ((cls, refs, ens), me: MEnum) => (cls, refs, me :: ens)
            })

          MetaModel(name, classes, references, enums, uiState)
        }

      mm.flatMap(validateMLinks)
    }

  }

  trait MBoundsReads[MB <: MBounds] extends Reads[MB] {
    private val boundsError = JsError(ValidationError("invalid lower and/or upper bound"))

    private def boundsCheck(bounds: MBounds): Boolean = {
      (bounds.upperBound > bounds.lowerBound) ||
        (bounds.upperBound == bounds.lowerBound && bounds.lowerBound != 0) ||
        (bounds.upperBound == -1)
    }

    override final def reads(json: JsValue): JsResult[MB] =
      readsUnchecked(json).filter(boundsError)(boundsCheck)

    def readsUnchecked(json: JsValue): JsResult[MB]
  }

  trait MLinkDefReads[MB <: MBounds] extends MBoundsReads[MB] {
    override final def readsUnchecked(jsv: JsValue): JsResult[MB] = {
      for {
        refName <- jsv.\("type").validate[String]
        upperBound <- jsv.\("upperBound").validate[Int](Reads.min(-1))
        lowerBound <- jsv.\("lowerBound").validate[Int](Reads.min(0))
        deleteIfLower <- jsv.\("deleteIfLower").validate[Boolean]
      } yield {
        buildMLink(refName, upperBound, lowerBound, deleteIfLower)
      }
    }

    def buildMLink(name: String, upperBound: Int, lowerBound: Int, deleteIfLower: Boolean): MB
  }

  // replacement for MLinkDef
  object MReferenceLinkDefReads extends MLinkDefReads[MReferenceLinkDef] {
    override def buildMLink(name: String, upperBound: Int, lowerBound: Int, deleteIfLower: Boolean): MReferenceLinkDef =
      MReferenceLinkDef(name, upperBound, lowerBound, deleteIfLower)
  }

  // replacement for MLinkDef
  object MClassLinkDefReads extends MLinkDefReads[MClassLinkDef] {
    override def buildMLink(name: String, upperBound: Int, lowerBound: Int, deleteIfLower: Boolean): MClassLinkDef =
      MClassLinkDef(name, upperBound, lowerBound, deleteIfLower)
  }

  val attributeTypeWrites: Writes[AttributeType] = new Writes[AttributeType] {
    override def writes(a: AttributeType): JsValue = {
      val out = a match {
        case MEnum(name, _) => name
        case _ => a.asString
      }
      JsString(out)
    }
  }

  class MAttributeReads(val enumMap: Map[String, MEnum]) extends MBoundsReads[MAttribute] {
    private val singletonString = JsSuccess(StringType)
    private val singletonBool = JsSuccess(BoolType)
    private val singletonInt = JsSuccess(IntType)
    private val singletonDouble = JsSuccess(DoubleType)


    private val typeDefaultError = JsError("type definition and default value don't match")

    // used to validate JsLookup. JsValue has flatMap. JsLookup hasn't
    private object ToJsResult extends Reads[JsValue] {
      override def reads(json: JsValue): JsResult[JsValue] = JsSuccess(json)
    }

    private def detectType(name: String): JsResult[AttributeType] = name match {
      case "String" => singletonString
      case "Bool" => singletonBool
      case "Int" => singletonInt
      case "Double" => singletonDouble
      case _ => enumMap.get(name) match {
        case Some(enum) => JsSuccess(enum)
        case None => JsError(s"Enum with name $name should be part of the MetaModel")
      }
    }

    private object CheckValidInt {
      def unapply(jsn: JsNumber): Option[Int] = try {
        Some(jsn.value.toIntExact)
      } catch {
        case _: java.lang.ArithmeticException => None
      }
    }

    private def validateTypeDefault(typ: AttributeType, default: JsValue): JsResult[AttributeValue] = (typ, default) match {
      case (StringType, JsString(s)) => JsSuccess(AttributeValue.MString(s))
      case (BoolType, JsBoolean(b)) => JsSuccess(AttributeValue.MBool(b))
      case (DoubleType, JsNumber(n)) => JsSuccess(AttributeValue.MDouble(n.doubleValue))
      case (IntType, CheckValidInt(i)) => JsSuccess(AttributeValue.MInt(i))
      case (MEnum(enumName, values), JsString(name)) if values.contains(name) => JsSuccess(AttributeValue.EnumSymbol(name, enumName))
      case _ => typeDefaultError
    }

    override def readsUnchecked(json: JsValue): JsResult[MAttribute] = {
      for {
        name <- json.\("name").validate[String]
        globalUnique <- json.\("globalUnique").validate[Boolean]
        localUnique <- json.\("localUnique").validate[Boolean]
        typ <- json.\("type").validate[String].flatMap(detectType)
        default <- json.\("default").validate(ToJsResult).flatMap(validateTypeDefault(typ, _))
        constant <- json.\("constant").validate[Boolean]
        singleAssignment <- json.\("singleAssignment").validate[Boolean]
        expression <- json.\("expression").validate[String]
        ordered <- json.\("ordered").validate[Boolean]
        transient <- json.\("transient").validate[Boolean]
        upperBound <- json.\("upperBound").validate[Int](Reads.min(-1))
        lowerBound <- json.\("lowerBound").validate[Int](Reads.min(0))
      } yield {
        MAttribute(name, globalUnique, localUnique, typ, default, constant, singleAssignment, expression, ordered, transient, upperBound, lowerBound)
      }
    }
  }

  class MClassReads(val enumMap: Map[String, MEnum]) extends Reads[MClass] {

    private val attributeListReads: Reads[List[MAttribute]] = Reads.list(new MAttributeReads(enumMap))

    override def reads(json: JsValue): JsResult[MClass] = {
      for {
        name <- json.\("name").validate[String](Reads.minLength[String](1))
        abstractness <- json.\("abstract").validate[Boolean]
        superTypes <- json.\("superTypes").validate[Seq[String]]
        inputs <- json.\("inputs").validate(Reads.list(MReferenceLinkDefReads))
        outputs <- json.\("outputs").validate(Reads.list(MReferenceLinkDefReads))
        attributes <- json.\("attributes").validate(attributeListReads)
      } yield {
        MClass(name, abstractness, superTypes, inputs, outputs, attributes)
      }
    }
  }


  class MReferenceReads(val enumMap: Map[String, MEnum]) extends Reads[MReference] {

    private val attributeListReads: Reads[List[MAttribute]] = Reads.list(new MAttributeReads(enumMap))

    override def reads(json: JsValue): JsResult[MReference] = {
      for {
        name <- json.\("name").validate[String](Reads.minLength[String](1))
        sourceDeletionDeletesTarget <- json.\("sourceDeletionDeletesTarget").validate[Boolean]
        targetDeletionDeletesSource <- json.\("targetDeletionDeletesSource").validate[Boolean]
        source <- json.\("source").validate(Reads.list(MClassLinkDefReads))
        target <- json.\("target").validate(Reads.list(MClassLinkDefReads))
        attributes <- json.\("attributes").validate(attributeListReads)
      } yield {
        MReference(name, sourceDeletionDeletesTarget, targetDeletionDeletesSource, source, target, attributes)
      }
    }
  }

  class MObjectReads(enumOpts: List[Option[MEnum]]) extends Reads[MObject] {

    private val enumMap: Map[String, MEnum] = enumOpts.flatMap {
      case Some(enum) => List((enum.name, enum))
      case None => Nil
    }.toMap

    private val mClassReads = new MClassReads(enumMap)
    private val mRefsReads = new MReferenceReads(enumMap)

    override def reads(json: JsValue): JsResult[MObject] = {
      json.\("mType").validate[String] match {
        case JsSuccess("mClass", _) => json.validate(mClassReads)
        case JsSuccess("mReference", _) => json.validate(mRefsReads)
        case JsSuccess("mEnum", _) => json.\("name").validate[String].flatMap(enumMap.get(_) match {
          case Some(enum) => JsSuccess(enum)
          // this can only happen if there is a mistake in the Reads implementation thus throws an Exception instead of returning a JsError
          case None => throw new IllegalStateException("MEnum map should contain all MEnums in this MetaModel")
        })
        case JsSuccess(_, _) => JsError("Missing or unknown mType at top level, only mClass, mReference and mEnum allowed")
        case e: JsError => e
      }
    }
  }

  object MEnumOptReads extends Reads[Option[MEnum]] {

    private val enumSymbolError = JsError("Enum symbols must be unique and not empty")

    override def reads(json: JsValue): JsResult[Option[MEnum]] = {
      json.\("mType").validate[String].flatMap {
        case "mEnum" => parseEnum(json).map(Some(_))
        case _ => JsSuccess(None)
      }
    }

    private def checkSymbols(list: List[String]): JsResult[List[String]] = {
      val size = list.size
      // check if elems in list are unique
      if (size == 0 || list.toSet.size != size) {
        enumSymbolError
      } else {
        JsSuccess(list)
      }
    }

    private def parseEnum(json: JsValue): JsResult[MEnum] = {
      for {
        name <- json.\("name").validate(Reads.minLength[String](1))
        symbols <- json.\("symbols").validate(Reads.list(Reads.minLength[String](1))).flatMap(checkSymbols)
      } yield {
        MEnum(name, symbols)
      }
    }
  }

}
