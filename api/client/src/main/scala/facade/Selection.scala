package facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@JSName("AceAjax.Selection")
class Selection protected () extends js.Object {
  def this(session: IEditSession) = this()
  def addEventListener(ev: String, callback: js.Function): js.Dynamic = js.native
  def moveCursorWordLeft(): js.Dynamic = js.native
  def moveCursorWordRight(): js.Dynamic = js.native
  def fromOrientedRange(range: Range): js.Dynamic = js.native
  def setSelectionRange(`match`: js.Any): js.Dynamic = js.native
  def getAllRanges(): js.Array[Range] = js.native
  def on(event: String, fn: js.Function1[js.Any, Any]): js.Dynamic = js.native
  def addRange(range: Range): js.Dynamic = js.native
  def isEmpty(): Boolean = js.native
  def isMultiLine(): Boolean = js.native
  def getCursor(): Position = js.native
  def setSelectionAnchor(row: Double, column: Double): js.Dynamic = js.native
  def getSelectionAnchor(): js.Dynamic = js.native
  def getSelectionLead(): js.Dynamic = js.native
  def shiftSelection(columns: Double): js.Dynamic = js.native
  def isBackwards(): Boolean = js.native
  def getRange(): Range = js.native
  def clearSelection(): js.Dynamic = js.native
  def selectAll(): js.Dynamic = js.native
  def setRange(range: Range, reverse: Boolean): js.Dynamic = js.native
  def selectTo(row: Double, column: Double): js.Dynamic = js.native
  def selectToPosition(pos: js.Any): js.Dynamic = js.native
  def selectUp(): js.Dynamic = js.native
  def selectDown(): js.Dynamic = js.native
  def selectRight(): js.Dynamic = js.native
  def selectLeft(): js.Dynamic = js.native
  def selectLineStart(): js.Dynamic = js.native
  def selectLineEnd(): js.Dynamic = js.native
  def selectFileEnd(): js.Dynamic = js.native
  def selectFileStart(): js.Dynamic = js.native
  def selectWordRight(): js.Dynamic = js.native
  def selectWordLeft(): js.Dynamic = js.native
  def getWordRange(): js.Dynamic = js.native
  def selectWord(): js.Dynamic = js.native
  def selectAWord(): js.Dynamic = js.native
  def selectLine(): js.Dynamic = js.native
  def moveCursorUp(): js.Dynamic = js.native
  def moveCursorDown(): js.Dynamic = js.native
  def moveCursorLeft(): js.Dynamic = js.native
  def moveCursorRight(): js.Dynamic = js.native
  def moveCursorLineStart(): js.Dynamic = js.native
  def moveCursorLineEnd(): js.Dynamic = js.native
  def moveCursorFileEnd(): js.Dynamic = js.native
  def moveCursorFileStart(): js.Dynamic = js.native
  def moveCursorLongWordRight(): js.Dynamic = js.native
  def moveCursorLongWordLeft(): js.Dynamic = js.native
  def moveCursorBy(rows: Double, chars: Double): js.Dynamic = js.native
  def moveCursorToPosition(position: js.Any): js.Dynamic = js.native
  def moveCursorTo(row: Double, column: Double, keepDesiredColumn: Boolean = true): js.Dynamic = js.native
  def moveCursorToScreen(row: Double, column: Double, keepDesiredColumn: Boolean): js.Dynamic = js.native
}

@JSName("AceAjax.Selection")
object Selection extends js.Object
