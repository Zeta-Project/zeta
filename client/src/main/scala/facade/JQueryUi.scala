package facade

import org.scalajs.jquery.JQuery
import scala.scalajs.js

/**
 * Partial JQueryUI Facade for ScalaJS
 */
object JQueryUi {
  implicit def jquery2ui(jquery: JQuery): JQueryUi =
    jquery.asInstanceOf[JQueryUi]
}

trait JQueryUi extends JQuery {
  def button(options: js.Any): this.type = js.native

  def selectable(options: js.Any): this.type = js.native
  def sortable(options: js.Any): this.type = js.native
}