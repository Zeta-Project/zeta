package experimental.generated

object Consumer {

  case class Attributes(
      name: String
  )

}

case class Consumer(id: String, source: Transition, target: Place, attributes: Consumer.Attributes, petriNet: PetriNet) {

}