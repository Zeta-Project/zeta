package experimental.generated

object Transition {

  case class Attributes(
      name: String
  )

}

case class Transition(id: String, attributes: Transition.Attributes, petriNet: PetriNet) {

  lazy val incomingProducer: List[Producer] = petriNet.producerList.filter(_.target == this)

  lazy val outgoingConsumer: List[Consumer] = petriNet.consumerList.filter(_.source == this)

  def canFire(): Boolean = {
    incomingProducer.forall(_.source.attributes.tokens > 0)
  }

  def doFire(): Unit = {
    incomingProducer.foreach(_.source.attributes.tokens -= 1)
    outgoingConsumer.foreach(_.target.attributes.tokens += 1)
  }

}