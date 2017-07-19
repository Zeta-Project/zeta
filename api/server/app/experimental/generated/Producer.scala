package experimental.generated

object Producer {

  case class Attributes(
      name: String
  )

}

case class Producer(id: String, source: Place, target: Transition, attributes: Producer.Attributes, petriNet: PetriNet) {

}