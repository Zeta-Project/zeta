package experimental

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.EdgeLink
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink


object PetriNetModelFixture {

  private val sPlace = "Place"
  private val sTransition = "Transition"
  private val sConsumer = "Consumer"
  private val sProducer = "Producer"

  private val sPlace1: String = "place1"
  private val sTransition1: String = "transition1"
  private val sConsumer1: String = "consumer1"
  private val sProducer1: String = "producer1"

  private val sPlace2: String = "place2"
  private val sTransition2: String = "transition2"
  private val sConsumer2: String = "consumer2"
  private val sProducer2: String = "producer2"

  private val sTokens = "tokens"
  private val sName = "name"
  private val sFired = "fired"


  val transition1: Node = Node(
    name = sTransition1,
    className = sTransition,
    outputs = List(EdgeLink(
      referenceName = sConsumer,
      edgeNames = List(sConsumer1)
    )),
    inputs = List(EdgeLink(
      referenceName = sProducer,
      edgeNames = List(sProducer1)
    )),
    attributes = Seq.empty,
    attributeValues = Map(
      sName -> List(StringValue("Transition1")),
      sFired -> List(BoolValue(false))
    ),
    methods = Seq.empty
  )

  val transition2: Node = Node(
    name = sTransition2,
    className = sTransition,
    outputs = List(EdgeLink(
      referenceName = sConsumer,
      edgeNames = List(sConsumer2)
    )),
    inputs = List(EdgeLink(
      referenceName = sProducer,
      edgeNames = List(sProducer2)
    )),
    attributes = Seq.empty,
    attributeValues = Map(
      sName -> List(StringValue("Transition2")),
      sFired -> List(BoolValue(false))
    ),
    methods = Seq.empty
  )

  val place1: Node = Node(
    name = sPlace1,
    className = sPlace,
    outputs = List(EdgeLink(
      referenceName = sProducer,
      edgeNames = List(sProducer1)
    )),
    inputs = List(EdgeLink(
      referenceName = sConsumer,
      edgeNames = List(sConsumer2)
    )),
    attributes = Seq.empty,
    attributeValues = Map(
      sName -> List(StringValue("Place1")),
      sTokens -> List(IntValue(3))
    ),
    methods = Seq.empty
  )

  val place2: Node = Node(
    name = sPlace2,
    className = sPlace,
    outputs = List(EdgeLink(
      referenceName = sProducer,
      edgeNames = List(sProducer2)
    )),
    inputs = List(EdgeLink(
      referenceName = sConsumer,
      edgeNames = List(sConsumer1)
    )),
    attributes = Seq.empty,
    attributeValues = Map(
      sName -> List(StringValue("Place2")),
      sTokens -> List(IntValue(0))
    ),
    methods = Seq.empty
  )

  val producer1: Edge = Edge(
    name = sProducer1,
    referenceName = sProducer,
    source = List(NodeLink(
      className = sPlace,
      nodeNames = List(sPlace1)
    )),
    target = List(NodeLink(
      className = sTransition,
      nodeNames = List(sTransition1)
    )),
    attributes = Seq.empty,
    attributeValues = Map(
      sName -> List(StringValue("Producer1"))
    ),
    methods = Seq.empty
  )

  val producer2: Edge = Edge(
    name = sProducer2,
    referenceName = sProducer,
    source = List(NodeLink(
      className = sPlace,
      nodeNames = List(sPlace2)
    )),
    target = List(NodeLink(
      className = sTransition,
      nodeNames = List(sTransition2)
    )),
    attributes = Seq.empty,
    attributeValues = Map(
      sName -> List(StringValue("Producer2"))
    ),
    methods = Seq.empty
  )

  val consumer1: Edge = Edge(
    name = sConsumer1,
    referenceName = sConsumer,
    source = List(NodeLink(
      className = sTransition,
      nodeNames = List(sTransition1)
    )),
    target = List(NodeLink(
      className = sPlace,
      nodeNames = List(sPlace2)
    )),
    attributes = Seq.empty,
    attributeValues = Map(
      sName -> List(StringValue("Consumer1"))
    ),
    methods = Seq.empty
  )

  val consumer2: Edge = Edge(
    name = sConsumer2,
    referenceName = sConsumer,
    source = List(NodeLink(
      className = sTransition,
      nodeNames = List(sTransition2)
    )),
    target = List(NodeLink(
      className = sPlace,
      nodeNames = List(sPlace1)
    )),
    attributes = Seq.empty,
    attributeValues = Map(
      sName -> List(StringValue("Consumer2"))
    ),
    methods = Seq.empty
  )

  val model: Model = Model(
    name = "SimplePetriNet",
    metaModelId = UUID.randomUUID(),
    nodes = List(place1, place2, transition1, transition2),
    edges = List(producer1, producer2, consumer1, consumer2),
    attributes = Seq.empty,
    attributeValues = Map(
      "state" -> List(EnumValue("State", "Resting"))
    ),
    methods = Seq.empty,
    uiState = "uiState"
  )

  val modelEntity: ModelEntity = ModelEntity(
    id = UUID.randomUUID(),
    model = model
  )

}
