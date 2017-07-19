package experimental.generated

// scalastyle:off
object Main extends App {

  val net = new PetriNet

  var ok = true
  while(ok) {
    ok = net.transform()
    println(net.attributes.state match {
      case State.Resting => "Resting"
      case State.Producing => "Produce"
      case State.Fired => "Fired"
      case State.Consuming => "Consuming"
    })
    println(net.placeList.map(_.attributes.tokens).mkString(" "))
    Thread.sleep(1000)
  }

  println("can't fire anymore")

}
