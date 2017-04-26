package facade

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js

import js.annotation.JSName

package object ace extends js.GlobalScope {
  var ace: Ace = js.native
}

trait TokenInfo extends js.Object {
  var value: String = js.native
}

trait Position extends js.Object {
  var row: Int = js.native
  var column: Int = js.native
}

trait Ace extends js.Object {
  def require(moduleName: String): js.Dynamic = js.native
  def edit(el: String): Editor = js.native
  def createEditSession(text: js.Any, mode: js.Any): IEditSession = js.native
}

@JSName("AceAjax.PlaceHolder")
class PlaceHolder protected () extends js.Object {
  def this(session: Document, length: Double, pos: Double, others: String, mainClass: String, othersClass: String) = this()
  def this(session: IEditSession, length: Double, pos: Position, positions: js.Array[Position]) = this()
  def on(event: String, fn: js.Function1[js.Any, Any]): js.Dynamic = js.native
  def setup(): js.Dynamic = js.native
  def showOtherMarkers(): js.Dynamic = js.native
  def hideOtherMarkers(): js.Dynamic = js.native
  def onUpdate(): js.Dynamic = js.native
  def onCursorChange(): js.Dynamic = js.native
  def detach(): js.Dynamic = js.native
  def cancel(): js.Dynamic = js.native
}

@JSName("AceAjax.PlaceHolder")
object PlaceHolder extends js.Object

@JSName("AceAjax.RenderLoop")
class RenderLoop extends js.Object

@JSName("AceAjax.RenderLoop")
object RenderLoop extends js.Object

@JSName("AceAjax.ScrollBar")
class ScrollBar protected () extends js.Object {
  def this(parent: HTMLElement) = this()
  def onScroll(e: js.Any): js.Dynamic = js.native
  def getWidth(): Double = js.native
  def setHeight(height: Double): js.Dynamic = js.native
  def setInnerHeight(height: Double): js.Dynamic = js.native
  def setScrollTop(scrollTop: Double): js.Dynamic = js.native
}

@JSName("AceAjax.ScrollBar")
object ScrollBar extends js.Object

@JSName("AceAjax.Search")
class Search extends js.Object {
  def set(options: js.Any): Search = js.native
  def getOptions(): js.Dynamic = js.native
  def setOptions(An: js.Any): js.Dynamic = js.native
  def find(session: IEditSession): Range = js.native
  def findAll(session: IEditSession): js.Array[Range] = js.native
  def replace(input: String, replacement: String): String = js.native
}

@JSName("AceAjax.Search")
object Search extends js.Object

@JSName("AceAjax.Split")
class Split extends js.Object {
  def getSplits(): Double = js.native
  def getEditor(idx: Double): js.Dynamic = js.native
  def getCurrentEditor(): Editor = js.native
  def focus(): js.Dynamic = js.native
  def blur(): js.Dynamic = js.native
  def setTheme(theme: String): js.Dynamic = js.native
  def setKeyboardHandler(keybinding: String): js.Dynamic = js.native
  def forEach(callback: js.Function, scope: String): js.Dynamic = js.native
  def setFontSize(size: Double): js.Dynamic = js.native
  def setSession(session: IEditSession, idx: Double): js.Dynamic = js.native
  def getOrientation(): Double = js.native
  def setOrientation(orientation: Double): js.Dynamic = js.native
  def resize(): js.Dynamic = js.native
}

@JSName("AceAjax.Split")
object Split extends js.Object

@JSName("AceAjax.TokenIterator")
class TokenIterator protected () extends js.Object {
  def this(session: IEditSession, initialRow: Double, initialColumn: Double) = this()
  def stepBackward(): js.Array[String] = js.native
  def stepForward(): String = js.native
  def getCurrentToken(): TokenInfo = js.native
  def getCurrentTokenRow(): Double = js.native
  def getCurrentTokenColumn(): Double = js.native
}

@JSName("AceAjax.TokenIterator")
object TokenIterator extends js.Object

@JSName("AceAjax.Tokenizer")
class Tokenizer protected () extends js.Object {
  def this(rules: js.Any, flag: String) = this()
  def getLineTokens(): js.Dynamic = js.native
}

@JSName("AceAjax.Tokenizer")
object Tokenizer extends js.Object
