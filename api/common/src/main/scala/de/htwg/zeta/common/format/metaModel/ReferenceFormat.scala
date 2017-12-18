package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class ReferenceFormat(
    attributeFormat: AttributeFormat,
    methodFormat: MethodFormat,
    sName: String = "name",
    sDescription: String = "description",
    sSourceDeletionDeletesTarget: String = "sourceDeletionDeletesTarget",
    sTargetDeletionDeletesSource: String = "targetDeletionDeletesSource",
    sSourceClassName: String = "sourceClassName",
    sTargetClassName: String = "targetClassName",
    sAttributes: String = "attributes",
    sMethods: String = "methods"
) extends OFormat[MReference] {

  override def writes(reference: MReference): JsObject = Json.obj(
    sName -> reference.name,
    sDescription -> reference.description,
    sSourceDeletionDeletesTarget -> reference.sourceDeletionDeletesTarget,
    sTargetDeletionDeletesSource -> reference.targetDeletionDeletesSource,
    sSourceClassName -> reference.sourceClassName,
    sTargetClassName -> reference.targetClassName,
    sAttributes -> Writes.seq(attributeFormat).writes(reference.attributes),
    sMethods -> Writes.seq(methodFormat).writes(reference.methods)
  )

  override def reads(json: JsValue): JsResult[MReference] = for {
    name <- (json \ sName).validate[String]
    description <- (json \ sDescription).validate[String]
    sourceDeletionDeletesTarget <- (json \ sSourceDeletionDeletesTarget).validate[Boolean]
    targetDeletionDeletesSource <- (json \ sTargetDeletionDeletesSource).validate[Boolean]
    sourceClassName <- (json \ sSourceClassName).validate[String]
    targetClassName <- (json \ sTargetClassName).validate[String]
    attributes <- (json \ sAttributes).validate(Reads.list(attributeFormat))
    methods <- (json \ sMethods).validate(Reads.list(methodFormat))
  } yield {
    MReference(
      name = name,
      description = description,
      sourceDeletionDeletesTarget = sourceDeletionDeletesTarget,
      targetDeletionDeletesSource = targetDeletionDeletesSource,
      sourceClassName = sourceClassName,
      targetClassName = targetClassName,
      attributes = attributes,
      methods = methods
    )
  }

}
