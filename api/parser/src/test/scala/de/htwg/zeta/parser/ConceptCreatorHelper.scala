package de.htwg.zeta.parser

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference

//noinspection ScalaStyle
object ConceptCreatorHelper {

  val exampleConcept = new Concept(
    classes = List(
      cClass("AbstractKlasse", List(cAttribute("text11"), cAttribute("text21"), cAttribute("text31"))),
      cClass("InterfaceKlasse", List(cAttribute("text113"), cAttribute("text213"), cAttribute("text313"))),
      cClass("Klasse", List(cAttribute("text1"), cAttribute("text2"), cAttribute("text3")))
    ),
    references = List(
      cReference("BaseClassRealization", "Klasse", "InterfaceKlasse"),
      cReference("Realization", "InterfaceKlasse", "AbstractKlasse"),
      cReference("Inheritance", "Klasse", "AbstractKlasse")
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
    attributes = List(),
    methods = List()
  )

  def cClass(name: String, attributes: List[MAttribute]): MClass = new MClass(
    name = name,
    description = "",
    abstractness = true,
    superTypeNames = List(),
    inputReferenceNames = List(),
    outputReferenceNames = List(),
    attributes = attributes,
    methods = List()
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

}
