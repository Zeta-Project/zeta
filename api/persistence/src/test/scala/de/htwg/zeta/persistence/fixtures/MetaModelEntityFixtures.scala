package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import scala.collection.immutable.Seq
import scala.collection.immutable.SortedMap

import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Diagram
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Shape
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Style
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
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference


object MetaModelEntityFixtures {

  val dsl1 = Dsl(
    diagram = Some(Diagram("diagramCode1")),
    shape = Some(Shape("shapeCode1")),
    style = None
  )

  val dsl2 = Dsl(
    diagram = None,
    shape = None,
    style = Some(Style("styleCode1"))
  )

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
        parameters = SortedMap(
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
        parameters = SortedMap(
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

  val metaModel1 = MetaModel(
    name = "metaModel1",
    classes = Seq(class1, class2),
    references = Seq(reference1, reference2),
    enums = Seq(enum1, enum2),
    attributes = Seq.empty,
    uiState = "uiState1",
    methods = Seq.empty
  )

  val metaModel2: MetaModel = MetaModel(
    name = "metaModel2",
    classes = Seq(class1),
    references = Seq(reference1),
    enums = Seq(enum1),
    attributes = Seq(stringAttribute, enumAttribute),
    uiState = "uiState2",
    methods = Seq.empty
  )

  val entity1 = MetaModelEntity(
    id = UUID.randomUUID,
    metaModel = metaModel1,
    dsl = dsl1,
    validator = Some("validator1")
  )

  val entity2 = MetaModelEntity(
    id = UUID.randomUUID,
    metaModel = metaModel2,
    dsl = dsl2,
    validator = Some("validator2")
  )

  val entity2Updated: MetaModelEntity = entity2.copy(dsl = dsl1)

  val entity3 = MetaModelEntity(
    id = UUID.randomUUID,
    metaModel = metaModel1,
    dsl = dsl2,
    validator = Some("validator3")
  )

}
