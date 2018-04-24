package de.htwg.zeta.common.format.project

import de.htwg.zeta.common.models.project.concept.elements.MReference
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
    methodFormat: MethodFormat
) extends OFormat[MReference] {

  val sName: String = "name"
  val sDescription: String = "description"
  val sSourceDeletionDeletesTarget: String = "sourceDeletionDeletesTarget"
  val sTargetDeletionDeletesSource: String = "targetDeletionDeletesSource"
  val sSourceClassName: String = "sourceClassName"
  val sTargetClassName: String = "targetClassName"
  val sSourceLowerBounds: String = "sourceLowerBounds"
  val sSourceUpperBounds: String = "sourceUpperBounds"
  val sTargetLowerBounds: String = "targetLowerBounds"
  val sTargetUpperBounds: String = "targetUpperBounds"
  val sAttributes: String = "attributes"
  val sMethods: String = "methods"


  override def writes(reference: MReference): JsObject = Json.obj(
    sName -> reference.name,
    sDescription -> reference.description,
    sSourceDeletionDeletesTarget -> reference.sourceDeletionDeletesTarget,
    sTargetDeletionDeletesSource -> reference.targetDeletionDeletesSource,
    sSourceClassName -> reference.sourceClassName,
    sTargetClassName -> reference.targetClassName,
    sSourceLowerBounds -> reference.sourceLowerBounds,
    sSourceUpperBounds -> reference.sourceUpperBounds,
    sTargetLowerBounds -> reference.targetLowerBounds,
    sTargetUpperBounds -> reference.targetUpperBounds,
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
    sourceLowerBounds <- (json \ sSourceLowerBounds).validate[Int]
    sourceUpperBounds <- (json \ sSourceUpperBounds).validate[Int]
    targetLowerBounds <- (json \ sTargetLowerBounds).validate[Int]
    targetUpperBounds <- (json \ sTargetUpperBounds).validate[Int]
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
      sourceLowerBounds = sourceLowerBounds,
      sourceUpperBounds = sourceUpperBounds,
      targetLowerBounds = targetLowerBounds,
      targetUpperBounds = targetUpperBounds,
      attributes = attributes,
      methods = methods
    )
  }

}
