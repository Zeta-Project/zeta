package experimental

// scalastyle:off

/** MClass.description */
object Place {

  /** A list containing all instances of Place. */
  val list: List[Place] =
    Place("Place1", Attributes(0)) ::
    Place("Place2", Attributes(2)) ::
    Nil

  /** A map containing all instances of Place, mapped to their name. */
  val map: Map[String, Place] = list.map(place => (place.name, place)).toMap

  /** The attributes of Place. */
  case class Attributes(
      var tokens: Int
  )

}

case class Place(name: String, attribute: Place.Attributes) {

  /** A list containing all outgoing Producer from this Place. */
  lazy val outgoingProducer: List[Producer] = Producer.list.filter(_.from == this)

  /** A list containing all incoming Consumer to this Place. */
  lazy val incomingConsumer: List[Consumer] = Consumer.list.filter(_.to == this)

}

/** The generated companion-object of Place. */
object Transition {

  val list: List[Transition] =
    Transition("Transition1") ::
    Transition("Transition2") ::
    Nil

  val map: Map[String, Transition] = list.map(transition => (transition.name, transition)).toMap

}

case class Transition(name: String) {

  lazy val incomingProducer: List[Producer] = Producer.list.filter(_.to == this)

  lazy val outgoingConsumer: List[Consumer] = Consumer.list.filter(_.from == this)


  def canFire: Boolean = {
    incomingProducer.forall(_.from.attribute.tokens > 0)
  }

  def doFire(): Unit = {
    incomingProducer.foreach(_.from.attribute.tokens -= 1)
    outgoingConsumer.foreach(_.to.attribute.tokens += 1)
  }

}

/** MReference.description */
object Producer {

  val list: List[Producer] =
    Producer("Producer1", Place.map("Place1"), Transition.map("Transition1")) ::
      Producer("Producer2", Place.map("Place2"), Transition.map("Transition2")) ::
      Nil

  val map: Map[String, Producer] = list.map(producer => (producer.name, producer)).toMap

}

case class Producer(name: String, from: Place, to: Transition)


object Consumer {

  val list: List[Consumer] =
    Consumer("Consumer1", Transition.map("Transition1"), Place.map("Place2")) ::
    Consumer("Consumer2", Transition.map("Transition2"), Place.map("Place1")) ::
    Nil

  val map: Map[String, Consumer] = list.map(consumer => (consumer.name, consumer)).toMap

}

case class Consumer(name: String, from: Transition, to: Place)


object Petri {

  def printState(): Unit = {
    Place.list.foreach(place => println(s"${place.name}: ${place.attribute.tokens}"))
    println()
  }

  def transform(): Unit = {
    Transition.list.filter(_.canFire).foreach(_.doFire())
  }

}


object Main extends App {

  Petri.printState()

  Petri.transform()

  Petri.printState()

  Petri.transform()

  Petri.printState()

  Petri.transform()

  Petri.printState()

  Petri.transform()

}
