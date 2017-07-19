package experimental.generated

sealed trait State

object State {

  object Resting extends State

  object Producing extends State

  object Fired extends State

  object Consuming extends State

}
