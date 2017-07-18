package experimental

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.Parameter
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef


object PetriNetMetaModelFixture {

  private val sPlace = "Place"
  private val sTransition = "Transition"
  private val sConsumer = "Consumer"
  private val sProducer = "Producer"

  private val enum1: MEnum = MEnum("Enum1", List("Symbol1", "Symbol2"))
  private val enum2: MEnum = MEnum("Enum2", List("Symbol3", "Symbol4", "Symbol5"))

  private val nameAttribute = MAttribute(
    name = sProducer,
    globalUnique = false,
    localUnique = true,
    typ = StringType,
    default = MString(""),
    constant = true,
    singleAssignment = true,
    expression = "",
    ordered = true,
    transient = false,
    upperBound = 1,
    lowerBound = 0
  )

  private val producer: MReference = MReference(
    name = sProducer,
    description = "Produces tokens",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    source = List(MClassLinkDef(
      className = sPlace,
      upperBound = 1,
      lowerBound = 0,
      deleteIfLower = false
    )),
    target = List(MClassLinkDef(
      className = sTransition,
      upperBound = 1,
      lowerBound = 0,
      deleteIfLower = false
    )),
    attributes = List(nameAttribute),
    methods = List.empty
  )

  private val consumer: MReference = MReference(
    name = sConsumer,
    description = "Consumes tokens",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    source = List(MClassLinkDef(
      className = sTransition,
      upperBound = 1,
      lowerBound = 0,
      deleteIfLower = false
    )),
    target = List(MClassLinkDef(
      className = sPlace,
      upperBound = 1,
      lowerBound = 0,
      deleteIfLower = false
    )),
    attributes = List(nameAttribute),
    methods = List.empty
  )

  private val place: MClass = MClass(
    name = sPlace,
    description = "Holds the tokens",
    abstractness = false,
    superTypeNames = List.empty,
    inputs = List(MReferenceLinkDef(
      referenceName = sConsumer,
      upperBound = 1,
      lowerBound = 0,
      deleteIfLower = false
    )),
    outputs = List(MReferenceLinkDef(
      referenceName = sProducer,
      upperBound = 1,
      lowerBound = 0,
      deleteIfLower = false
    )),
    attributes = List(
      nameAttribute,
      MAttribute(
        name = "tokens",
        globalUnique = false,
        localUnique = true,
        typ = IntType,
        default = MInt(0),
        constant = true,
        singleAssignment = true,
        expression = "",
        ordered = true,
        transient = false,
        upperBound = 1,
        lowerBound = 0
      )
    ),
    methods = List.empty
  )

  private val transition: MClass = MClass(
    name = sTransition,
    description = "Operates the tokens",
    abstractness = false,
    superTypeNames = List.empty,
    inputs = List(MReferenceLinkDef(
      referenceName = sProducer,
      upperBound = 1,
      lowerBound = 0,
      deleteIfLower = false
    )),
    outputs = List(MReferenceLinkDef(
      referenceName = sConsumer,
      upperBound = 1,
      lowerBound = 0,
      deleteIfLower = false
    )),
    attributes = List(nameAttribute),
    methods = List(
      Method(
        name = "canFire",
        parameters = List.empty,
        description = "check if the transition can fire",
        returnType = Some(BoolType),
        code = "incomingProducer.forall(_.from.attribute.tokens > 0)"
      ),
      Method(
        name = "doFire",
        parameters = List.empty,
        description = "fire the transition",
        returnType = None,
        code =
          """|incomingProducer.foreach(_.from.attribute.tokens -= 1)
             |outgoingConsumer.foreach(_.to.attribute.tokens += 1)""".stripMargin // scalastyle:ignore
      )
    )
  )

  val metaModel: MetaModel = MetaModel(
    name = "PetriNet",
    classes = List(place, transition),
    references = List(producer, consumer),
    enums = List(enum1, enum2),
    methods = List(
      Method(
        name = "printState",
        parameters = List.empty,
        description = "print the current state",
        returnType = Some(BoolType),
        // scalastyle:off
        code =
          """|Place.list.foreach(place => println(s"${place.name}: ${place.attribute.tokens}"))
             |println()""".stripMargin
        // scalastyle:on
      ),
      Method(
        name = "transform",
        parameters = List.empty,
        description = "fire all ready transitions",
        returnType = None,
        code = "Transition.list.filter(_.canFire).foreach(_.doFire())"
      ),
      Method(
        name = "add",
        parameters = List(
          Parameter("n1", IntType),
          Parameter("n2", IntType)
        ),
        description = "add two numbers",
        returnType = Some(IntType),
        code = "n1 + n2"
      )
    ),
    uiState = "uiState"
  )

}
