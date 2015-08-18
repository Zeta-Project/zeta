package facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

/**
 * Scalajs Facade for AlertifyJs
 */
package object Alertify extends js.GlobalScope {
  var alertify: AlertifyTrait = js.native
}

trait Dialog extends js.Object {
  def set(args: js.Dynamic) = js.native
}

trait AlertifyTrait extends js.Object {
  def alert(message: String): Dialog

  def confirm(message: String, fn: js.Function1[Boolean, _]): Dialog

  /** Prompt String Value */
  def prompt(message: String,
             value: js.Any,
             onOk: js.Function2[_, js.Any, _],
             onCancel: js.Function0[_]): Dialog = js.native

  /** Prompt String Value */
  def prompt(message: String,
             value: js.Any,
             onOk: js.Function2[_, js.Any, _]): Dialog = js.native


  def log(message: String, kind: String, time: Int): Dialog = js.native

  def success(message: String): Dialog = js.native

  def success(message: String, wait: Int): Dialog = js.native

  def warning(message: String): Dialog = js.native

  def warning(message: String, wait: Int): Dialog = js.native

  def notify(message: String): Dialog = js.native

  def notify(message: String, wait: Int): Dialog = js.native

  def error(message: String): Dialog = js.native

  def error(message: String, wait: Int): Dialog = js.native
}