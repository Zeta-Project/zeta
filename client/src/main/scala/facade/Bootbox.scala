package facade

import scala.scalajs.js


package object Bootbox extends js.GlobalScope {
  var bootbox: BootboxTrait = js.native
}

trait BootboxTrait extends js.Object {
  def alert(message: String): js.Any = js.native
  def alert(message: String, callback: js.Function0[_]): js.Any = js.native

  def alert(args: js.Dynamic): js.Any = js.native

  def dialog(args: js.Dynamic) : js.Any = js.native

  def confirm(message:String, callback: js.Function1[Boolean,_]) : js.Any = js.native
}
