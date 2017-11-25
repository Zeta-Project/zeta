package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MObject
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.json.Format
import play.api.libs.json.JsError
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

private[metaModelUiFormat] class MObjectFormat(enumOpts: List[Option[MEnum]]) extends Format[MObject] {

  private val enumMap: Map[String, MEnum] = enumOpts.flatMap {
    case Some(enum) => List((enum.name, enum))
    case None => Nil
  }.toMap

  private val mClassReads = new MClassUiFormat(enumMap)
  private val mRefsReads = new MReferenceUiFormat(enumMap)

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

  override def writes(o: MObject): JsValue = MObjectFormat.writes(o)
}


private[metaModelUiFormat] object MObjectFormat extends Writes[MObject] {
  override def writes(mo: MObject): JsValue = mo match {
    case mc: MClass => MClassUiFormat.writes(mc)
    case mr: MReference => MReferenceUiFormat.writes(mr)
    case me: MEnum => MEnumFormat.writes(me)
  }
}
