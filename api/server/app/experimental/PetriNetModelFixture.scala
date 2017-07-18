package experimental

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges


object PetriNetModelFixture {

  private val metaModel: MetaModel = PetriNetMetaModelFixture.metaModel

  private val sPlace = "Place"
  private val sTransition = "Transition"
  private val sConsumer = "Consumer"
  private val sProducer = "Producer"


  val transition1: Node = Node(
    name = "transition1",
    clazz = metaModel.classMap(sTransition),
    outputs = List(ToEdges(
      reference = metaModel.referenceMap(sConsumer),
      edgeNames = List()
    )),
    inputs = null,
    attributes = Map.empty
  )

  val transition2: Node = Node(
    name = "transition2",
    clazz = metaModel.classMap(sTransition),
    outputs = null,
    inputs = null,
    attributes = Map.empty
  )

  val place1: Node = Node(
    name = "place1",
    clazz = metaModel.classMap(sPlace),
    outputs = null,
    inputs = null,
    attributes = Map("tokens" -> List(MInt(1)))
  )

  val place2: Node = Node(
    name = "place2",
    clazz = metaModel.classMap(sPlace),
    outputs = null,
    inputs = null,
    attributes = Map("tokens" -> List(MInt(0)))
  )

  val producer1: Edge = Edge(
    name = "producer1",
    reference = metaModel.referenceMap(sProducer),
    source = null,
    target = null,
    attributes = Map.empty
  )

  val producer2: Edge = Edge(
    name = "producer2",
    reference = metaModel.referenceMap(sProducer),
    source = null,
    target = null,
    attributes = Map.empty
  )

  val consumer1: Edge = Edge(
    name = "consumer1",
    reference = metaModel.referenceMap(sConsumer),
    source = null,
    target = null,
    attributes = Map.empty
  )

  val consumer2: Edge = Edge(
    name = "consumer2",
    reference = metaModel.referenceMap(sConsumer),
    source = null,
    target = null,
    attributes = Map.empty
  )


  val model: Model = Model(
    name = "SimplePetriNet",
    metaModelId = UUID.randomUUID(),
    nodes = List(place1, place2, transition1, transition2),
    edges = List(producer1, producer2, consumer1, consumer2),
    uiState = "uiState"
  )

}
