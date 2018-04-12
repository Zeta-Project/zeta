package de.htwg.zeta.parser

import scala.collection.immutable.ListMap

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.concept.elements.Method

//noinspection ScalaStyle
object ConceptCreatorHelper {

  private val baseClassRealization: MReference = cReference("BaseClassRealization", "Klasse", "InterfaceKlasse")
  private val realizationReference: MReference = cReference("Realization", "InterfaceKlasse", "AbstractKlasse")
  private val inheritanceReference: MReference = cReference("Inheritance", "Klasse", "AbstractKlasse")
  val exampleConcept = new Concept(
    classes = List(
      cClass("AbstractKlasse", List(cAttribute("text11"), cAttribute("text21"), cAttribute("text31"))),
      cClass("InterfaceKlasse", List(cAttribute("text113"), cAttribute("text213"), cAttribute("text313"))),
      cClass("Klasse", List(cAttribute("text1"), cAttribute("text2"), cAttribute("text3")),
        List(cMethod("methodUnit", AttributeType.UnitType), cMethod("methodString", AttributeType.StringType)),
        List(baseClassRealization.name, inheritanceReference.name)
      )
    ),
    references = List(
      baseClassRealization,
      realizationReference,
      inheritanceReference
    ),
    enums = List(),
    attributes = List(
      cAttribute("text11"),
      cAttribute("text21"),
      cAttribute("text31"),
      cAttribute("text113"),
      cAttribute("text213"),
      cAttribute("text313"),
      cAttribute("text1"),
      cAttribute("text2"),
      cAttribute("text3")
    ),
    methods = List(),
    uiState = ""
  )


  def cReference(name: String, source: String, target: String): MReference = new MReference(
    name = name,
    description = "",
    sourceDeletionDeletesTarget = true,
    targetDeletionDeletesSource = true,
    sourceClassName = source,
    targetClassName = target,
    sourceLowerBounds = 0,
    sourceUpperBounds = -1,
    targetLowerBounds = 0,
    targetUpperBounds = -1,
    attributes = List(),
    methods = List()
  )

  def cClass(name: String, attributes: List[MAttribute]): MClass = cClass(name, attributes, List(), List())

  def cClass(
      name: String,
      attributes: List[MAttribute],
      methods: List[Method],
      outputReferenceNames: List[String]): MClass = new MClass(
    name = name,
    description = "",
    abstractness = true,
    superTypeNames = List(),
    inputReferenceNames = List(),
    outputReferenceNames = outputReferenceNames,
    attributes = attributes,
    methods = methods
  )

  def cAttribute(name: String): MAttribute = new MAttribute(
    name = name,
    globalUnique = true,
    localUnique = true,
    typ = AttributeType.StringType,
    default = AttributeValue.StringValue(""),
    constant = false,
    singleAssignment = false,
    expression = "",
    ordered = true,
    transient = false
  )

  def cMethod(name: String, returnType: AttributeType): Method = new Method(
    name = name,
    parameters = ListMap(),
    description = "default method",
    returnType = returnType,
    code = "nice code"
  )

}
