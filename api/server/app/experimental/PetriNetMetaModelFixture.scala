package experimental

import java.util.UUID

import de.htwg.zeta.common.models.entity.MetaModelEntity
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

  private val state: MEnum = MEnum("State", List("Resting", "Producing", "Fired", "Consuming"))

  private val nameAttribute = MAttribute(
    name = "name",
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
        constant = false,
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
        code = "incomingProducer.forall(_.source.attributes.tokens > 0)"
      ),
      Method(
        name = "doFire",
        parameters = List.empty,
        description = "fire the transition",
        returnType = None,
        code =
          """|incomingProducer.foreach(_.source.attributes.tokens -= 1)
             |outgoingConsumer.foreach(_.target.attributes.tokens += 1)""".stripMargin // scalastyle:ignore
      )
    )
  )

  val metaModel: MetaModel = MetaModel(
    name = "PetriNet",
    classes = List(place, transition),
    references = List(producer, consumer),
    enums = List(state),
    attributes = List(
      MAttribute(
        name = "state",
        globalUnique = false,
        localUnique = true,
        typ = state,
        default = MInt(0),
        constant = false,
        singleAssignment = true,
        expression = "",
        ordered = true,
        transient = false,
        upperBound = 1,
        lowerBound = 0
      )
    ),
    methods = List(
      Method(
        name = "printState",
        parameters = List.empty,
        description = "print the current state",
        returnType = None,
        // scalastyle:off
        code =
          """|placeList.foreach(place => println(s"${place.id}: ${place.attributes.tokens}"))
             |println()""".stripMargin
        // scalastyle:on
      ),
      Method(
        name = "transform",
        parameters = List.empty,
        description = "fire all ready transitions",
        returnType = None,
        code = "transitionList.filter(_.canFire).foreach(_.doFire())"
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

  val metaModelEntity: MetaModelEntity = MetaModelEntity(
    id = UUID.randomUUID(),
    rev = "",
    name = "",
    metaModel = metaModel
  )

}
