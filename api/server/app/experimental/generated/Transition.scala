package experimental.generated

object Transition {

  case class Attributes(
      name: String,
      var fired: Boolean
  )

}

case class Transition(id: String, attributes: Transition.Attributes, petriNet: PetriNet) {

  lazy val incomingProducer: List[Producer] = petriNet.producerList.filter(_.target == this)

  lazy val outgoingConsumer: List[Consumer] = petriNet.consumerList.filter(_.source == this)

  def canFire(): Boolean = {
    incomingProducer.forall(_.source.attributes.tokens > 0)
  }

  def produce(): Unit = {
    incomingProducer.foreach(_.source.attributes.tokens -= 1)
    attributes.fired = true
  }

  def consume(): Unit = {
    outgoingConsumer.foreach(_.target.attributes.tokens += 1)
    attributes.fired = false
  }

}