package experimental.generated

object PetriNet {

  case class Attributes(
      var state: State
  )

}

class PetriNet {

  val placeList: List[Place] = List(
    Place("Place1", Place.Attributes("Place1", 3), this),
    Place("Place2", Place.Attributes("Place2", 0), this)
  )

  val placeMap: Map[String, Place] = placeList.map(place => (place.id, place)).toMap


  val transitionList: List[Transition] = List(
    Transition("Transition1", Transition.Attributes("Transition1", false), this),
    Transition("Transition2", Transition.Attributes("Transition2", false), this)
  )

  val transitionMap: Map[String, Transition] = transitionList.map(transition => (transition.id, transition)).toMap


  val producerList: List[Producer] = List(
    Producer("Producer1", placeMap("Place1"), transitionMap("Transition1"), Producer.Attributes("Producer1"), this),
    Producer("Producer2", placeMap("Place2"), transitionMap("Transition2"), Producer.Attributes("Producer2"), this)
  )

  val producerMap: Map[String, Producer] = producerList.map(producer => (producer.id, producer)).toMap


  val consumerList: List[Consumer] = List(
    Consumer("Consumer1", transitionMap("Transition1"), placeMap("Place2"), Consumer.Attributes("Consumer1"), this),
    Consumer("Consumer2", transitionMap("Transition2"), placeMap("Place1"), Consumer.Attributes("Consumer2"), this)
  )

  val consumerMap: Map[String, Consumer] = consumerList.map(consumer => (consumer.id, consumer)).toMap


  val attributes = PetriNet.Attributes(State.Resting)

  def transform(): Boolean = {

    attributes.state match {

      case State.Resting =>
        val shuffled = scala.util.Random.shuffle(transitionList)
        shuffled.find(_.canFire()).fold(
          false
        ) { ready =>
          ready.produce()
          attributes.state = State.Producing
          true
        }

      case State.Producing =>
        attributes.state = State.Fired
        true

      case State.Fired =>
        attributes.state = State.Consuming
        true

      case State.Consuming =>
        transitionList.filter(_.attributes.fired).foreach(_.consume())
        attributes.state = State.Resting
        true

    }
  }

}
