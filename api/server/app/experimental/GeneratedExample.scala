package experimental

import experimental.Place.Attributes

// scalastyle:off

sealed trait State

object State {

  object Resting extends State

  object Producing extends State

  object Fired extends State

  object Consuming extends State

}


/** MClass.description */
object Place {


  /** The attributes of Place. */
  case class Attributes(
      var tokens: Int
  )

}

case class Place(name: String, attributes: Place.Attributes, petri: Petri) {

  /** A list containing all outgoing Producer from this Place. */
  lazy val outgoingProducer: List[Producer] = petri.producerList.filter(_.from == this)

  /** A list containing all incoming Consumer to this Place. */
  lazy val incomingConsumer: List[Consumer] = petri.consumerList.filter(_.to == this)

}


case class Transition(name: String, petri: Petri) {

  lazy val incomingProducer: List[Producer] = petri.producerList.filter(_.to == this)

  lazy val outgoingConsumer: List[Consumer] = petri.consumerList.filter(_.from == this)


  def canFire: Boolean = {
    incomingProducer.forall(_.from.attributes.tokens > 0)
  }

  def doFire(): Unit = {
    incomingProducer.foreach(_.from.attributes.tokens -= 1)
    outgoingConsumer.foreach(_.to.attributes.tokens += 1)
  }

}


case class Producer(name: String, from: Place, to: Transition)

case class Consumer(name: String, from: Transition, to: Place, petri: Petri)


object Petri {

  /** The attributes of Petri. */
  case class Attributes(
      var state: State
  )

}

class Petri {

  /** A list containing all instances of Place. */
  val placeList: List[Place] = List(
    Place("Place1", Attributes(0), this),
    Place("Place2", Attributes(2), this)
  )

  /** A map containing all instances of Place, mapped to their id. */
  val placeMap: Map[String, Place] = placeList.map(place => (place.name, place)).toMap


  val transitionList: List[Transition] = List(
    Transition("Transition1", this),
    Transition("Transition2", this)
  )

  val transitionMap: Map[String, Transition] = transitionList.map(transition => (transition.name, transition)).toMap


  val producerList: List[Producer] = List(
    Producer("Producer1", placeMap("Place1"), transitionMap("Transition1")),
    Producer("Producer2", placeMap("Place2"), transitionMap("Transition2"))
  )

  val producerMap: Map[String, Producer] = producerList.map(producer => (producer.name, producer)).toMap


  val consumerList: List[Consumer] = List(
    Consumer("Consumer1", transitionMap("Transition1"), placeMap("Place2"), this),
    Consumer("Consumer2", transitionMap("Transition2"), placeMap("Place1"), this)
  )

  val consumerMap: Map[String, Consumer] = consumerList.map(consumer => (consumer.name, consumer)).toMap


  val attributes = Petri.Attributes(
    state = State.Resting
  )

  def printState(): Unit = {
    placeList.foreach(place => println(s"${place.name}: ${place.attributes.tokens}"))
    println()
  }

  def transform(): Unit = {
    transitionList.filter(_.canFire).foreach(_.doFire())
  }

}


object Main extends App {

  val petri = new Petri

  petri.printState()

  petri.transform()

  petri.printState()

  petri.transform()

  petri.printState()

  petri.transform()

  petri.printState()

  petri.transform()

}
