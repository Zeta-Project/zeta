package facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@JSName("AceAjax.Anchor")
class Anchor protected () extends js.Object {
  def this(doc: Document, row: Double, column: Double) = this()
  def on(event: String, fn: js.Function1[js.Any, Any]): js.Dynamic = js.native
  def getPosition(): Position = js.native
  def getDocument(): Document = js.native
  def onChange(e: js.Any): js.Dynamic = js.native
  def setPosition(row: Double, column: Double, noClip: Boolean): js.Dynamic = js.native
  def detach(): js.Dynamic = js.native
}

@JSName("AceAjax.Anchor")
object Anchor extends js.Object
