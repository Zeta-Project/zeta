package experimental.generated

object Place {

  case class Attributes(
      name: String,
      var tokens: Int
  )

}

case class Place(id: String, attributes: Place.Attributes, petriNet: PetriNet) {

  lazy val incomingConsumer: List[Consumer] = petriNet.consumerList.filter(_.target == this)

  lazy val outgoingProducer: List[Producer] = petriNet.producerList.filter(_.source == this)

}
