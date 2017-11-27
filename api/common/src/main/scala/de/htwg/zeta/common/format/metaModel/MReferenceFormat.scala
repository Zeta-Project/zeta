package de.htwg.zeta.common.format.metaModel

import scala.collection.immutable.Seq

import de.htwg.zeta.common.format.metaModel.MReferenceFormat.sAttributes
import de.htwg.zeta.common.format.metaModel.MReferenceFormat.sDescription
import de.htwg.zeta.common.format.metaModel.MReferenceFormat.sMethods
import de.htwg.zeta.common.format.metaModel.MReferenceFormat.sName
import de.htwg.zeta.common.format.metaModel.MReferenceFormat.sSource
import de.htwg.zeta.common.format.metaModel.MReferenceFormat.sSourceDeletionDeletesTarget
import de.htwg.zeta.common.format.metaModel.MReferenceFormat.sTarget
import de.htwg.zeta.common.format.metaModel.MReferenceFormat.sTargetDeletionDeletesSource
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes

object MReferenceFormat extends OWrites[MReference] {

  val sName = "name"
  val sDescription = "description"
  val sSourceDeletionDeletesTarget = "sourceDeletionDeletesTarget"
  val sTargetDeletionDeletesSource = "targetDeletionDeletesSource"
  val sSource = "source"
  val sTarget = "target"
  val sAttributes = "attributes"
  val sMethods = "methods"

  override def writes(reference: MReference): JsObject = Json.obj(
    sName -> reference.name,
    sDescription -> reference.description,
    sSourceDeletionDeletesTarget -> reference.sourceDeletionDeletesTarget,
    sTargetDeletionDeletesSource -> reference.targetDeletionDeletesSource,
    sSource -> Writes.seq(MClassLinkDefFormat).writes(reference.source),
    sTarget -> Writes.seq(MClassLinkDefFormat).writes(reference.target),
    sAttributes -> Writes.seq(MAttributeFormat).writes(reference.attributes),
    sMethods -> Writes.seq(MethodFormat).writes(reference.methods)
  )

}

class MReferenceFormat(enums: Seq[MEnum]) extends Reads[MReference] {

  override def reads(json: JsValue): JsResult[MReference] = {
    for {
      name <- (json \ sName).validate[String]
      description <- (json \ sDescription).validate[String]
      sourceDeletionDeletesTarget <- (json \ sSourceDeletionDeletesTarget).validate[Boolean]
      targetDeletionDeletesSource <- (json \ sTargetDeletionDeletesSource).validate[Boolean]
      source <- (json \ sSource).validate(Reads.list(MClassLinkDefFormat))
      target <- (json \ sTarget).validate(Reads.list(MClassLinkDefFormat))
      attributes <- (json \ sAttributes).validate(Reads.list(new MAttributeFormat(enums)))
      methods <- (json \ sMethods).validate(Reads.list(new MethodFormat(enums)))
    } yield {
      MReference(
        name = name,
        description = description,
        sourceDeletionDeletesTarget = sourceDeletionDeletesTarget,
        targetDeletionDeletesSource = targetDeletionDeletesSource,
        source = source,
        target = target,
        attributes = attributes,
        methods = methods
      )
    }
  }

}
