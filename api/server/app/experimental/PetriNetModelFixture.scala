package experimental

import java.util.UUID

import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes


object PetriNetModelFixture {

  private val sPlace = "Place"
  private val sTransition = "Transition"
  private val sConsumer = "Consumer"
  private val sProducer = "Producer"

  private val sPlace1: UUID = UUID.randomUUID()
  private val sTransition1: UUID = UUID.randomUUID()
  private val sConsumer1: UUID = UUID.randomUUID()
  private val sProducer1: UUID = UUID.randomUUID()

  private val sPlace2: UUID = UUID.randomUUID()
  private val sTransition2: UUID = UUID.randomUUID()
  private val sConsumer2: UUID = UUID.randomUUID()
  private val sProducer2: UUID = UUID.randomUUID()

  private val sTokens = "tokens"
  private val sName = "name"
  private val sFired = "fired"


  val transition1: Node = Node(
    id = sTransition1,
    className = sTransition,
    outputs = List(ToEdges(
      referenceName = sConsumer,
      edgeIds = List(sConsumer1)
    )),
    inputs = List(ToEdges(
      referenceName = sProducer,
      edgeIds = List(sProducer1)
    )),
    attributes = Map(
      sName -> List(MString("Transition1")),
      sFired -> List(MBool(false))
    )
  )

  val transition2: Node = Node(
    id = sTransition2,
    className = sTransition,
    outputs = List(ToEdges(
      referenceName = sConsumer,
      edgeIds = List(sConsumer2)
    )),
    inputs = List(ToEdges(
      referenceName = sProducer,
      edgeIds = List(sProducer2)
    )),
    attributes = Map(
      sName -> List(MString("Transition2")),
      sFired -> List(MBool(false))
    )
  )

  val place1: Node = Node(
    id = sPlace1,
    className = sPlace,
    outputs = List(ToEdges(
      referenceName = sProducer,
      edgeIds = List(sProducer1)
    )),
    inputs = List(ToEdges(
      referenceName = sConsumer,
      edgeIds = List(sConsumer2)
    )),
    attributes = Map(
      sName -> List(MString("Place1")),
      sTokens -> List(MInt(3))
    )
  )

  val place2: Node = Node(
    id = sPlace2,
    className = sPlace,
    outputs = List(ToEdges(
      referenceName = sProducer,
      edgeIds = List(sProducer2)
    )),
    inputs = List(ToEdges(
      referenceName = sConsumer,
      edgeIds = List(sConsumer1)
    )),
    attributes = Map(
      sName -> List(MString("Place2")),
      sTokens -> List(MInt(0))
    )
  )

  val producer1: Edge = Edge(
    id = sProducer1,
    referenceName = sProducer,
    source = List(ToNodes(
      className = sPlace,
      nodeIds = List(sPlace1)
    )),
    target = List(ToNodes(
      className = sTransition,
      nodeIds = List(sTransition1)
    )),
    attributes = Map(
      sName -> List(MString("Producer1"))
    )
  )

  val producer2: Edge = Edge(
    id = sProducer2,
    referenceName = sProducer,
    source = List(ToNodes(
      className = sPlace,
      nodeIds = List(sPlace2)
    )),
    target = List(ToNodes(
      className = sTransition,
      nodeIds = List(sTransition2)
    )),
    attributes = Map(
      sName -> List(MString("Producer2"))
    )
  )

  val consumer1: Edge = Edge(
    id = sConsumer1,
    referenceName = sConsumer,
    source = List(ToNodes(
      className = sTransition,
      nodeIds = List(sTransition1)
    )),
    target = List(ToNodes(
      className = sPlace,
      nodeIds = List(sPlace2)
    )),
    attributes = Map(
      sName -> List(MString("Consumer1"))
    )
  )

  val consumer2: Edge = Edge(
    id = sConsumer2,
    referenceName = sConsumer,
    source = List(ToNodes(
      className = sTransition,
      nodeIds = List(sTransition2)
    )),
    target = List(ToNodes(
      className = sPlace,
      nodeIds = List(sPlace1)
    )),
    attributes = Map(
      sName -> List(MString("Consumer2"))
    )
  )

  val model: Model = Model(
    name = "SimplePetriNet",
    metaModelId = UUID.randomUUID(),
    nodes = List(place1, place2, transition1, transition2),
    edges = List(producer1, producer2, consumer1, consumer2),
    attributes = Map(
      "state" -> List(EnumSymbol("State", "Resting"))
    ),
    uiState = "uiState"
  )

  val modelEntity: ModelEntity = ModelEntity(
    id = UUID.randomUUID(),
    model = model
  )

}
