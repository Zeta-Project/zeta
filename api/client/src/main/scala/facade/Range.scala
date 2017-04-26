package facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

trait IRangeList extends js.Object {
  var ranges: js.Array[Range] = js.native
  def pointIndex(pos: Position, startIndex: Double = 0.0): js.Dynamic = js.native
  def addList(ranges: js.Array[Range]): js.Dynamic = js.native
  def add(ranges: Range): js.Dynamic = js.native
  def merge(): js.Array[Range] = js.native
  def substractPoint(pos: Position): js.Dynamic = js.native
}

@JSName("AceAjax.RangeList")
object RangeList extends js.Object

@JSName("AceAjax.Range")
class Range protected () extends js.Object {
  def this(startRow: Double, startColumn: Double, endRow: Double, endColumn: Double) = this()
  var startRow: Double = js.native
  var startColumn: Double = js.native
  var endRow: Double = js.native
  var endColumn: Double = js.native
  var start: Position = js.native
  var end: Position = js.native
  def isEmpty(): Boolean = js.native
  def isEqual(range: Range): js.Dynamic = js.native
  override def toString(): String = js.native
  def contains(row: Double, column: Double): Boolean = js.native
  def compareRange(range: Range): Double = js.native
  def comparePoint(p: Range): Double = js.native
  def containsRange(range: Range): Boolean = js.native
  def intersects(range: Range): Boolean = js.native
  def isEnd(row: Double, column: Double): Boolean = js.native
  def isStart(row: Double, column: Double): Boolean = js.native
  def setStart(row: Double, column: Double): js.Dynamic = js.native
  def setEnd(row: Double, column: Double): js.Dynamic = js.native
  def inside(row: Double, column: Double): Boolean = js.native
  def insideStart(row: Double, column: Double): Boolean = js.native
  def insideEnd(row: Double, column: Double): Boolean = js.native
  def compare(row: Double, column: Double): Double = js.native
  def compareStart(row: Double, column: Double): Double = js.native
  def compareEnd(row: Double, column: Double): Double = js.native
  def compareInside(row: Double, column: Double): Double = js.native
  def clipRows(firstRow: Double, lastRow: Double): Range = js.native
  def extend(row: Double, column: Double): Range = js.native
  def isMultiLine(): Boolean = js.native
  override def clone(): Range = js.native
  def collapseRows(): Range = js.native
  def toScreenRange(session: IEditSession): Range = js.native
  def fromPoints(start: Range, end: Range): Range = js.native
}

@JSName("AceAjax.Range")
object Range extends js.Object {
  def fromPoints(pos1: Position, pos2: Position): Range = js.native
}
