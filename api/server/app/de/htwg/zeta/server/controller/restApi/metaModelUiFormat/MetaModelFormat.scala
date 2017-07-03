package de.htwg.zeta.server.controller.restApi.metaModelUiFormat

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MObject
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.Format
import play.api.libs.json.JsResult
import play.api.libs.json.Reads
import play.api.libs.json.JsSuccess


private[metaModelUiFormat] object MetaModelFormat extends Format[MetaModel] {

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

  private object MEnumOptReads extends Reads[Option[MEnum]] {

    override def reads(json: JsValue): JsResult[Option[MEnum]] = {
      json.\("mType").validate[String].flatMap {
        case "mEnum" => MEnumFormat.reads(json).map(Some(_))
        case _ => JsSuccess(None)
      }
    }
  }

  override def reads(json: JsValue): JsResult[MetaModel] = {
    val elems = json.\("elements")
    val mm: JsResult[MetaModel] =
      for {
        name <- json.\("name").validate[String]
        uiState <- json.\("uiState").validate[String]
        enumOpt <- elems.validate(Reads.list(MEnumOptReads))
        elements <- elems.validate(Reads.list(new MObjectFormat(enumOpt))).flatMap(checkObjectsUnique)
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

  override def writes(mm: MetaModel): JsValue = {
    val elems: Seq[MObject] = mm.classes ++ mm.references ++ mm.enums
    Json.obj(
      "name" -> mm.name,
      "elements" -> JsArray(elems.map(MObjectFormat.writes)),
      "uiState" -> mm.uiState
    )
  }
}
