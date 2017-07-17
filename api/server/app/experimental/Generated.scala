package experimental

// scalastyle:off

case class Place(name: String, var tokens: Int) {

  lazy val outgoingProducer: List[Producer] = Petri.producerList.filter(_.from == this)

  lazy val incomingConsumer: List[Consumer] = Petri.consumerList.filter(_.to == this)

}

case class Transition(name: String) {

  lazy val incomingProducer: List[Producer] = Petri.producerList.filter(_.to == this)

  lazy val outgoingConsumer: List[Consumer] = Petri.consumerList.filter(_.from == this)


  def canFire: Boolean = {
    incomingProducer.forall(_.from.tokens > 0)
  }

  def doFire(): Unit = {
    incomingProducer.foreach(_.from.tokens -= 1)
    outgoingConsumer.foreach(_.to.tokens += 1)
  }

}

case class Producer(name: String, from: Place, to: Transition)

case class Consumer(name: String, from: Transition, to: Place)


object Petri {


  val placeList: List[Place] =
    Place("Place1", 0) ::
      Place("Place2", 2) ::
      Nil

  val placeMap: Map[String, Place] = placeList.map(place => (place.name, place)).toMap


  val transitionList: List[Transition] =
    Transition("Transition1") ::
      Transition("Transition2") ::
      Nil

  val transitionMap: Map[String, Transition] = transitionList.map(transition => (transition.name, transition)).toMap


  val producerList: List[Producer] =
    Producer("Producer1", placeMap("Place1"), transitionMap("Transition1")) ::
      Producer("Producer2", placeMap("Place2"), transitionMap("Transition2")) ::
      Nil

  val producerMap: Map[String, Producer] = producerList.map(producer => (producer.name, producer)).toMap


  val consumerList: List[Consumer] =
    Consumer("Consumer1", transitionMap("Transition1"), placeMap("Place2")) ::
      Consumer("Consumer2", transitionMap("Transition2"), placeMap("Place1")) ::
      Nil

  val consumerMap: Map[String, Consumer] = consumerList.map(consumer => (consumer.name, consumer)).toMap


  def printState(): Unit = {
    placeList.foreach(place => println(s"${place.name}: ${place.tokens}"))
    println()
  }

  def transform(): Unit = {
    transitionList.filter(_.canFire).foreach(_.doFire())
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
