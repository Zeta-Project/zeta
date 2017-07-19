package experimental

import java.util.UUID

import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes


object PetriNetModelFixture {

  private val metaModel: MetaModel = PetriNetMetaModelFixture.metaModel

  private val sPlace = "Place"
  private val sTransition = "Transition"
  private val sConsumer = "Consumer"
  private val sProducer = "Producer"

  private val sPlace1 = "Place1"
  private val sTransition1 = "Transition1"
  private val sConsumer1 = "Consumer1"
  private val sProducer1 = "Producer1"

  private val sPlace2 = "Place2"
  private val sTransition2 = "Transition2"
  private val sConsumer2 = "Consumer2"
  private val sProducer2 = "Producer2"

  private val sTokens = "tokens"
  private val sName = "name"


  val transition1: Node = Node(
    name = sTransition1,
    clazz = metaModel.classMap(sTransition),
    outputs = List(ToEdges(
      reference = metaModel.referenceMap(sConsumer),
      edgeNames = List(sConsumer1)
    )),
    inputs = List(ToEdges(
      reference = metaModel.referenceMap(sProducer),
      edgeNames = List(sProducer1)
    )),
    attributes = Map(
      sName -> List(MString(sTransition1))
    )
  )

  val transition2: Node = Node(
    name = sTransition2,
    clazz = metaModel.classMap(sTransition),
    outputs = List(ToEdges(
      reference = metaModel.referenceMap(sConsumer),
      edgeNames = List(sConsumer2)
    )),
    inputs = List(ToEdges(
      reference = metaModel.referenceMap(sProducer),
      edgeNames = List(sProducer2)
    )),
    attributes = Map(
      sName -> List(MString(sTransition2))
    )
  )

  val place1: Node = Node(
    name = sPlace1,
    clazz = metaModel.classMap(sPlace),
    outputs = List(ToEdges(
      reference = metaModel.referenceMap(sProducer),
      edgeNames = List(sProducer1)
    )),
    inputs = List(ToEdges(
      reference = metaModel.referenceMap(sConsumer),
      edgeNames = List(sConsumer2)
    )),
    attributes = Map(
      sName -> List(MString(sPlace1)),
      sTokens -> List(MInt(1))
    )
  )

  val place2: Node = Node(
    name = sPlace2,
    clazz = metaModel.classMap(sPlace),
    outputs = List(ToEdges(
      reference = metaModel.referenceMap(sProducer),
      edgeNames = List(sProducer2)
    )),
    inputs = List(ToEdges(
      reference = metaModel.referenceMap(sConsumer),
      edgeNames = List(sConsumer1)
    )),
    attributes = Map(
      sName -> List(MString(sPlace2)),
      sTokens -> List(MInt(0))
    )
  )

  val producer1: Edge = Edge(
    name = sProducer1,
    reference = metaModel.referenceMap(sProducer),
    source = List(ToNodes(
      clazz = metaModel.classMap(sPlace),
      nodeNames = List(sPlace1)
    )),
    target = List(ToNodes(
      clazz = metaModel.classMap(sTransition),
      nodeNames = List(sTransition1)
    )),
    attributes = Map(
      sName -> List(MString(sProducer1))
    )
  )

  val producer2: Edge = Edge(
    name = sProducer2,
    reference = metaModel.referenceMap(sProducer),
    source = List(ToNodes(
      clazz = metaModel.classMap(sPlace),
      nodeNames = List(sPlace2)
    )),
    target = List(ToNodes(
      clazz = metaModel.classMap(sTransition),
      nodeNames = List(sTransition2)
    )),
    attributes = Map(
      sName -> List(MString(sProducer2))
    )
  )

  val consumer1: Edge = Edge(
    name = sConsumer1,
    reference = metaModel.referenceMap(sConsumer),
    source = List(ToNodes(
      clazz = metaModel.classMap(sTransition),
      nodeNames = List(sTransition1)
    )),
    target = List(ToNodes(
      clazz = metaModel.classMap(sPlace),
      nodeNames = List(sPlace2)
    )),
    attributes = Map(
      sName -> List(MString(sConsumer1))
    )
  )

  val consumer2: Edge = Edge(
    name = sConsumer2,
    reference = metaModel.referenceMap(sConsumer),
    source = List(ToNodes(
      clazz = metaModel.classMap(sTransition),
      nodeNames = List(sTransition2)
    )),
    target = List(ToNodes(
      clazz = metaModel.classMap(sPlace),
      nodeNames = List(sPlace1)
    )),
    attributes = Map(
      sName -> List(MString(sConsumer2))
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
    model = model,
    metaModelId = UUID.randomUUID(),
    links = None
  )

}
