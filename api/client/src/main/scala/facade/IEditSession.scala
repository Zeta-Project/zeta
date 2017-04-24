package facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

trait IEditSession extends js.Object {
  var selection: Selection = js.native
  var bgTokenizer: BackgroundTokenizer = js.native
  var doc: Document = js.native
  def on(event: String, fn: js.Function1[js.Any, Any]): js.Dynamic = js.native
  def findMatchingBracket(position: Position): js.Dynamic = js.native
  def addFold(text: String, range: Range): js.Dynamic = js.native
  def getFoldAt(row: Double, column: Double): js.Dynamic = js.native
  def removeFold(arg: js.Any): js.Dynamic = js.native
  def expandFold(arg: js.Any): js.Dynamic = js.native
  def unfold(arg1: js.Any, arg2: Boolean): js.Dynamic = js.native
  def screenToDocumentColumn(row: Double, column: Double): js.Dynamic = js.native
  def getFoldDisplayLine(foldLine: js.Any, docRow: Double, docColumn: Double): js.Dynamic = js.native
  def getFoldsInRange(range: Range): js.Dynamic = js.native
  def highlight(text: String): js.Dynamic = js.native
  def setDocument(doc: Document): js.Dynamic = js.native
  def getDocument(): Document = js.native
  @JSName("$resetRowCache")
  def `$resetRowCache`(row: Double): js.Dynamic = js.native
  def setValue(text: String): js.Dynamic = js.native
  def setMode(mode: String): js.Dynamic = js.native
  def getValue(): String = js.native
  def getSelection(): Selection = js.native
  def getState(row: Double): String = js.native
  def getTokens(row: Double): js.Array[TokenInfo] = js.native
  def getTokenAt(row: Double, column: Double): TokenInfo = js.native
  def setUndoManager(undoManager: UndoManager): js.Dynamic = js.native
  def getUndoManager(): UndoManager = js.native
  def getTabString(): String = js.native
  def setUseSoftTabs(useSoftTabs: Boolean): js.Dynamic = js.native
  def getUseSoftTabs(): Boolean = js.native
  def setTabSize(tabSize: Double): js.Dynamic = js.native
  def getTabSize(): Double = js.native
  def isTabStop(position: js.Any): Boolean = js.native
  def setOverwrite(overwrite: Boolean): js.Dynamic = js.native
  def getOverwrite(): Boolean = js.native
  def toggleOverwrite(): js.Dynamic = js.native
  def addGutterDecoration(row: Double, className: String): js.Dynamic = js.native
  def removeGutterDecoration(row: Double, className: String): js.Dynamic = js.native
  def getBreakpoints(): js.Array[Double] = js.native
  def setBreakpoints(rows: js.Array[js.Any]): js.Dynamic = js.native
  def clearBreakpoints(): js.Dynamic = js.native
  def setBreakpoint(row: Double, className: String): js.Dynamic = js.native
  def clearBreakpoint(row: Double): js.Dynamic = js.native
  def addMarker(range: Range, clazz: String, `type`: js.Function, inFront: Boolean): js.Dynamic = js.native
  def addDynamicMarker(marker: js.Any, inFront: Boolean): js.Dynamic = js.native
  def removeMarker(markerId: Double): js.Dynamic = js.native
  def getMarkers(inFront: Boolean): js.Array[js.Any] = js.native
  def setAnnotations(annotations: js.Array[Annotation]): js.Dynamic = js.native
  def getAnnotations(): js.Dynamic = js.native
  def clearAnnotations(): js.Dynamic = js.native
  @JSName("$detectNewLine")
  def `$detectNewLine`(text: String): js.Dynamic = js.native
  def getWordRange(row: Double, column: Double): Range = js.native
  def getAWordRange(row: Double, column: Double): js.Dynamic = js.native
  def setNewLineMode(newLineMode: String): js.Dynamic = js.native
  def getNewLineMode(): String = js.native
  def setUseWorker(useWorker: Boolean): js.Dynamic = js.native
  def getUseWorker(): Boolean = js.native
  def onReloadTokenizer(): js.Dynamic = js.native
  @JSName("$mode")
  def `$mode`(mode: TextMode): js.Dynamic = js.native
  def getMode(): TextMode = js.native
  def setScrollTop(scrollTop: Double): js.Dynamic = js.native
  def getScrollTop(): Double = js.native
  def setScrollLeft(): js.Dynamic = js.native
  def getScrollLeft(): Double = js.native
  def getScreenWidth(): Double = js.native
  def getLine(row: Double): String = js.native
  def getLines(firstRow: Double, lastRow: Double): js.Array[String] = js.native
  def getLength(): Double = js.native
  def getTextRange(range: Range): String = js.native
  def insert(position: Position, text: String): js.Dynamic = js.native
  def remove(range: Range): js.Dynamic = js.native
  def undoChanges(deltas: js.Array[js.Any], dontSelect: Boolean): Range = js.native
  def redoChanges(deltas: js.Array[js.Any], dontSelect: Boolean): Range = js.native
  def setUndoSelect(enable: Boolean): js.Dynamic = js.native
  def replace(range: Range, text: String): js.Dynamic = js.native
  def moveText(fromRange: Range, toPosition: js.Any): Range = js.native
  def indentRows(startRow: Double, endRow: Double, indentString: String): js.Dynamic = js.native
  def outdentRows(range: Range): js.Dynamic = js.native
  def moveLinesUp(firstRow: Double, lastRow: Double): Double = js.native
  def moveLinesDown(firstRow: Double, lastRow: Double): Double = js.native
  def duplicateLines(firstRow: Double, lastRow: Double): Double = js.native
  def setUseWrapMode(useWrapMode: Boolean): js.Dynamic = js.native
  def getUseWrapMode(): Boolean = js.native
  def setWrapLimitRange(min: Double, max: Double): js.Dynamic = js.native
  def adjustWrapLimit(desiredLimit: Double): Boolean = js.native
  def getWrapLimit(): Double = js.native
  def getWrapLimitRange(): js.Dynamic = js.native
  @JSName("$getDisplayTokens")
  def `$getDisplayTokens`(str: String, offset: Double): js.Dynamic = js.native
  @JSName("$getStringScreenWidth")
  def `$getStringScreenWidth`(str: String, maxScreenColumn: Double, screenColumn: Double): js.Array[Double] = js.native
  def getRowLength(row: Double): Double = js.native
  def getScreenLastRowColumn(screenRow: Double): Double = js.native
  def getDocumentLastRowColumn(docRow: Double, docColumn: Double): Double = js.native
  def getDocumentLastRowColumnPosition(docRow: Double, docColumn: Double): Double = js.native
  def getRowSplitData(): String = js.native
  def getScreenTabSize(screenColumn: Double): Double = js.native
  def screenToDocumentPosition(screenRow: Double, screenColumn: Double): js.Dynamic = js.native
  def documentToScreenPosition(docRow: Double, docColumn: Double): js.Dynamic = js.native
  def documentToScreenColumn(row: Double, docColumn: Double): Double = js.native
  def documentToScreenRow(docRow: Double, docColumn: Double): js.Dynamic = js.native
  def getScreenLength(): Double = js.native
}

@JSName("AceAjax.BackgroundTokenizer")
class BackgroundTokenizer protected () extends js.Object {
  def this(tokenizer: Tokenizer, editor: Editor) = this()
  var states: js.Array[js.Any] = js.native
  def setTokenizer(tokenizer: Tokenizer): js.Dynamic = js.native
  def setDocument(doc: Document): js.Dynamic = js.native
  def fireUpdateEvent(firstRow: Double, lastRow: Double): js.Dynamic = js.native
  def start(startRow: Double): js.Dynamic = js.native
  def stop(): js.Dynamic = js.native
  def getTokens(row: Double): js.Array[TokenInfo] = js.native
  def getState(row: Double): String = js.native
}

@JSName("AceAjax.BackgroundTokenizer")
object BackgroundTokenizer extends js.Object

class TextMode extends js.Object {
  def this($id: js.Any) = this()
  def getTokenizer(): js.Dynamic = js.native
  def toggleCommentLines(state: js.Any, doc: js.Any, startRow: js.Any, endRow: js.Any): js.Dynamic = js.native
  def getNextLineIndent(state: js.Any, line: js.Any, tab: js.Any): String = js.native
  def checkOutdent(state: js.Any, line: js.Any, input: js.Any): Boolean = js.native
  def autoOutdent(state: js.Any, doc: js.Any, row: js.Any): js.Dynamic = js.native
  def createWorker(session: js.Any): js.Dynamic = js.native
  def createModeDelegates(mapping: js.Any): js.Dynamic = js.native
  def transformAction(state: js.Any, action: js.Any, editor: js.Any, session: js.Any, param: js.Any): js.Dynamic = js.native
}

trait Annotation extends js.Object {
  var row: Double = js.native
  var column: Double = js.native
  var text: String = js.native
  var `type`: String = js.native
}

@JSName("AceAjax.EditSession")
object EditSession extends js.Object

@JSName("AceAjax.UndoManager")
class UndoManager extends js.Object {
  def execute(options: js.Any): js.Dynamic = js.native
  def undo(dontSelect: Boolean = true): Range = js.native
  def redo(dontSelect: Boolean): js.Dynamic = js.native
  def reset(): js.Dynamic = js.native
  def hasUndo(): Boolean = js.native
  def hasRedo(): Boolean = js.native
}

@JSName("AceAjax.UndoManager")
object UndoManager extends js.Object
