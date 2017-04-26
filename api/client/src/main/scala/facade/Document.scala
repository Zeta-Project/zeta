package facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@JSName("AceAjax.Document")
class Document protected () extends js.Object {
  def this(text: String = new String()) = this()
  def on(event: String, fn: js.Function1[js.Any, Any]): js.Dynamic = js.native
  def setValue(text: String): js.Dynamic = js.native
  def getValue(): String = js.native
  def createAnchor(row: Double, column: Double): js.Dynamic = js.native
  def getNewLineCharacter(): String = js.native
  def setNewLineMode(newLineMode: String): js.Dynamic = js.native
  def getNewLineMode(): String = js.native
  def isNewLine(text: String): Boolean = js.native
  def getLine(row: Double): String = js.native
  def getLines(firstRow: Double, lastRow: Double): js.Array[String] = js.native
  def getAllLines(): js.Array[String] = js.native
  def getLength(): Int = js.native
  def getTextRange(range: Range): String = js.native
  def insert(position: Position, text: String): js.Dynamic = js.native
  def insertLines(row: Double, lines: js.Array[String]): js.Dynamic = js.native
  def insertNewLine(position: Position): js.Dynamic = js.native
  def insertInLine(position: js.Any, text: String): js.Dynamic = js.native
  def remove(range: Range): js.Dynamic = js.native
  def removeInLine(row: Double, startColumn: Double, endColumn: Double): js.Dynamic = js.native
  def removeLines(firstRow: Double, lastRow: Double): js.Array[String] = js.native
  def removeNewLine(row: Double): js.Dynamic = js.native
  def replace(range: Range, text: String): js.Dynamic = js.native
  def applyDeltas(deltas: js.Array[Delta]): js.Dynamic = js.native
  def revertDeltas(deltas: js.Array[Delta]): js.Dynamic = js.native
  def indexToPosition(index: Int, startRow: Int): Position = js.native
  def positionToIndex(pos: Position, startRow: Int): Int = js.native
}

@JSName("AceAjax.Document")
object Document extends js.Object

trait Delta extends js.Object {
  var action: String = js.native
  var range: Range = js.native
  var text: String = js.native
  var lines: js.Array[String] = js.native
}
