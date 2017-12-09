package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import scala.collection.immutable.ListMap
import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.GraphicalDsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.UnitType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method


object GraphicalDslFixtures {

  val stringAttribute = MAttribute(
    name = "stringAttribute",
    globalUnique = true,
    localUnique = false,
    typ = StringType,
    default = StringValue("stringValue"),
    constant = false,
    singleAssignment = true,
    expression = "stringExpression",
    ordered = false,
    transient = true
  )

  val boolAttribute = MAttribute(
    name = "boolAttribute",
    globalUnique = false,
    localUnique = false,
    typ = BoolType,
    default = BoolValue(false),
    constant = false,
    singleAssignment = true,
    expression = "boolExpression",
    ordered = true,
    transient = false
  )

  val doubleAttribute = MAttribute(
    name = "doubleAttribute",
    globalUnique = false,
    localUnique = true,
    typ = DoubleType,
    default = DoubleValue(-1.5),
    constant = true,
    singleAssignment = true,
    expression = "doubleExpression",
    ordered = true,
    transient = false
  )

  val intAttribute = MAttribute(
    name = "intAttribute",
    globalUnique = true,
    localUnique = false,
    typ = IntType,
    default = IntValue(2),
    constant = true,
    singleAssignment = false,
    expression = "intExpression",
    ordered = true,
    transient = false
  )

  val enum1 = MEnum("enum1", Seq("enumValue1", "enumValue2"))
  val enum2 = MEnum("enum2", Seq.empty)

  val enumAttribute = MAttribute(
    name = "enumAttribute",
    globalUnique = false,
    localUnique = true,
    typ = enum1.typ,
    default = enum1.values.head,
    constant = false,
    singleAssignment = true,
    expression = "enumExpression",
    ordered = true,
    transient = true
  )

  val referenceName1 = "referenceName1"
  val referenceName2 = "referenceName2"

  val className1 = "className1"
  val className2 = "className2"

  val class1 = MClass(
    name = "class1",
    description = "descriptionClass1",
    abstractness = true,
    superTypeNames = Seq(className1, className2),
    inputReferenceNames = Seq(referenceName1, referenceName2),
    outputReferenceNames = Seq.empty,
    attributes = Seq(intAttribute, doubleAttribute, enumAttribute),
    methods = Seq(
      Method(
        name = "f1",
        parameters = ListMap(
          "p1" -> StringType,
          "p2" -> DoubleType
        ),
        description = "description1",
        returnType = IntType,
        code = "code1"
      )
    )
  )

  val class2 = MClass(
    name = "class2",
    description = "descriptionClass2",
    abstractness = false,
    superTypeNames = Seq(className2),
    inputReferenceNames = Seq.empty,
    outputReferenceNames = Seq(referenceName1, referenceName2),
    attributes = Seq(stringAttribute, boolAttribute),
    methods = Seq.empty
  )

  val reference1 = MReference(
    name = referenceName1,
    description = "descriptionReference1",
    sourceDeletionDeletesTarget = true,
    targetDeletionDeletesSource = true,
    sourceClassName = className1,
    targetClassName = className2,
    attributes = Seq(enumAttribute, stringAttribute),
    methods = Seq(
      Method(
        name = "f2",
        parameters = ListMap(
          "p3" -> StringType,
          "p4" -> DoubleType
        ),
        description = "description2",
        returnType = UnitType,
        code = "code2"
      )
    )
  )

  val reference2 = MReference(
    name = referenceName2,
    description = "descriptionReference2",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    sourceClassName = className2,
    targetClassName = className1,
    attributes = Seq(intAttribute, doubleAttribute),
    methods = Seq.empty
  )

  val concept1 = Concept(
    classes = Seq(class1, class2),
    references = Seq(reference1, reference2),
    enums = Seq(enum1, enum2),
    attributes = Seq.empty,
    uiState = "uiState1",
    methods = Seq.empty
  )

  val concept2: Concept = Concept(
    classes = Seq(class1),
    references = Seq(reference1),
    enums = Seq(enum1),
    attributes = Seq(stringAttribute, enumAttribute),
    uiState = "uiState2",
    methods = Seq.empty
  )

  val entity1 = GraphicalDsl(
    id = UUID.randomUUID,
    name = "name1",
    concept = concept1,
    diagram = "diagram1",
    shape = "shape1",
    style = "style1",
    validator = Some("validator1")
  )

  val entity2 = GraphicalDsl(
    id = UUID.randomUUID,
    name = "name2",
    concept = concept2,
    diagram = "diagram2",
    shape = "shape2",
    style = "style2",
    validator = Some("validator2")
  )

  val entity2Updated: GraphicalDsl = entity2.copy(diagram = "diagramUpdated")

  val entity3 = GraphicalDsl(
    id = UUID.randomUUID,
    name = "name3",
    concept = concept2,
    diagram = "diagram3",
    shape = "shape3",
    style = "style3",
    validator = None
  )

}
