package facade

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName


@JSName("AceAjax.Editor")
class Editor protected () extends js.Object {
  def this(renderer: VirtualRenderer, session: IEditSession) = this()
  def addEventListener(ev: String, callback: js.Function): js.Dynamic = js.native
  var inMultiSelectMode: Boolean = js.native
  def selectMoreLines(n: Double): js.Dynamic = js.native
  def onTextInput(text: String): js.Dynamic = js.native
  def onCommandKey(e: js.Any, hashId: js.Any, keyCode: js.Any): js.Dynamic = js.native
  var commands: CommandManager = js.native
  var session: IEditSession = js.native
  var selection: Selection = js.native
  var renderer: VirtualRenderer = js.native
  var keyBinding: KeyBinding = js.native
  var container: HTMLElement = js.native
  var $blockScrolling: js.Any = js.native
  def onSelectionChange(e: js.Any): js.Dynamic = js.native
  def onChangeMode(e: js.Any = Nil): js.Dynamic = js.native
  def execCommand(command: String, args: js.Any = Nil): js.Dynamic = js.native
  def setKeyboardHandler(keyboardHandler: String): js.Dynamic = js.native
  def getKeyboardHandler(): String = js.native
  def setSession(session: IEditSession): js.Dynamic = js.native
  def getSession(): IEditSession = js.native
  def setValue(`val`: String, cursorPos: Double = 0.0): String = js.native
  def getValue(): String = js.native
  def getSelection(): Selection = js.native
  def resize(force: Boolean = true): js.Dynamic = js.native
  def setTheme(theme: String): js.Dynamic = js.native
  def getTheme(): String = js.native
  def setStyle(style: String): js.Dynamic = js.native
  def unsetStyle(): js.Dynamic = js.native
  def setFontSize(size: String): js.Dynamic = js.native
  def focus(): js.Dynamic = js.native
  def isFocused(): js.Dynamic = js.native
  def blur(): js.Dynamic = js.native
  def onFocus(): js.Dynamic = js.native
  def onBlur(): js.Dynamic = js.native
  def onDocumentChange(e: js.Any): js.Dynamic = js.native
  def onCursorChange(): js.Dynamic = js.native
  def getCopyText(): String = js.native
  def onCopy(): js.Dynamic = js.native
  def onCut(): js.Dynamic = js.native
  def onPaste(text: String): js.Dynamic = js.native
  def insert(text: String): js.Dynamic = js.native
  def setOverwrite(overwrite: Boolean): js.Dynamic = js.native
  def getOverwrite(): Boolean = js.native
  def toggleOverwrite(): js.Dynamic = js.native
  def setScrollSpeed(speed: Double): js.Dynamic = js.native
  def getScrollSpeed(): Double = js.native
  def setDragDelay(dragDelay: Double): js.Dynamic = js.native
  def getDragDelay(): Double = js.native
  def setSelectionStyle(style: String): js.Dynamic = js.native
  def getSelectionStyle(): String = js.native
  def setHighlightActiveLine(shouldHighlight: Boolean): js.Dynamic = js.native
  def getHighlightActiveLine(): js.Dynamic = js.native
  def setHighlightSelectedWord(shouldHighlight: Boolean): js.Dynamic = js.native
  def getHighlightSelectedWord(): Boolean = js.native
  def setShowInvisibles(showInvisibles: Boolean): js.Dynamic = js.native
  def getShowInvisibles(): Boolean = js.native
  def setShowPrintMargin(showPrintMargin: Boolean): js.Dynamic = js.native
  def getShowPrintMargin(): Boolean = js.native
  def setPrintMarginColumn(showPrintMargin: Double): js.Dynamic = js.native
  def getPrintMarginColumn(): Double = js.native
  def setReadOnly(readOnly: Boolean): js.Dynamic = js.native
  def getReadOnly(): Boolean = js.native
  def setBehavioursEnabled(enabled: Boolean): js.Dynamic = js.native
  def getBehavioursEnabled(): Boolean = js.native
  def setWrapBehavioursEnabled(enabled: Boolean): js.Dynamic = js.native
  def getWrapBehavioursEnabled(): js.Dynamic = js.native
  def setShowFoldWidgets(show: Boolean): js.Dynamic = js.native
  def getShowFoldWidgets(): js.Dynamic = js.native
  def remove(dir: String): js.Dynamic = js.native
  def removeWordRight(): js.Dynamic = js.native
  def removeWordLeft(): js.Dynamic = js.native
  def removeToLineStart(): js.Dynamic = js.native
  def removeToLineEnd(): js.Dynamic = js.native
  def splitLine(): js.Dynamic = js.native
  def transposeLetters(): js.Dynamic = js.native
  def toLowerCase(): js.Dynamic = js.native
  def toUpperCase(): js.Dynamic = js.native
  def indent(): js.Dynamic = js.native
  def blockIndent(): js.Dynamic = js.native
  def blockOutdent(arg: String = new String()): js.Dynamic = js.native
  def toggleCommentLines(): js.Dynamic = js.native
  def getNumberAt(): Double = js.native
  def modifyNumber(amount: Double): js.Dynamic = js.native
  def removeLines(): js.Dynamic = js.native
  def moveLinesDown(): Double = js.native
  def moveLinesUp(): Double = js.native
  def moveText(fromRange: Range, toPosition: js.Any): Range = js.native
  def copyLinesUp(): Double = js.native
  def copyLinesDown(): Double = js.native
  def getFirstVisibleRow(): Double = js.native
  def getLastVisibleRow(): Double = js.native
  def isRowVisible(row: Double): Boolean = js.native
  def isRowFullyVisible(row: Double): Boolean = js.native
  def selectPageDown(): js.Dynamic = js.native
  def selectPageUp(): js.Dynamic = js.native
  def gotoPageDown(): js.Dynamic = js.native
  def gotoPageUp(): js.Dynamic = js.native
  def scrollPageDown(): js.Dynamic = js.native
  def scrollPageUp(): js.Dynamic = js.native
  def scrollToRow(): js.Dynamic = js.native
  def scrollToLine(line: Double, center: Boolean, animate: Boolean, callback: js.Function): js.Dynamic = js.native
  def centerSelection(): js.Dynamic = js.native
  def getCursorPosition(): Position = js.native
  def getCursorPositionScreen(): Double = js.native
  def getSelectionRange(): Range = js.native
  def selectAll(): js.Dynamic = js.native
  def clearSelection(): js.Dynamic = js.native
  def moveCursorTo(row: Double, column: Double = 0.0, animate: Boolean = true): js.Dynamic = js.native
  def moveCursorToPosition(position: Position): js.Dynamic = js.native
  def jumpToMatching(): js.Dynamic = js.native
  def gotoLine(lineNumber: Double, column: Double = 0.0, animate: Boolean = true): js.Dynamic = js.native
  def navigateTo(row: Double, column: Double): js.Dynamic = js.native
  def navigateUp(times: Double = 0.0): js.Dynamic = js.native
  def navigateDown(times: Double = 0.0): js.Dynamic = js.native
  def navigateLeft(times: Double = 0.0): js.Dynamic = js.native
  def navigateRight(times: Double): js.Dynamic = js.native
  def navigateLineStart(): js.Dynamic = js.native
  def navigateLineEnd(): js.Dynamic = js.native
  def navigateFileEnd(): js.Dynamic = js.native
  def navigateFileStart(): js.Dynamic = js.native
  def navigateWordRight(): js.Dynamic = js.native
  def navigateWordLeft(): js.Dynamic = js.native
  def replace(replacement: String, options: js.Any = 0.0): js.Dynamic = js.native
  def replaceAll(replacement: String, options: js.Any = 0.0): js.Dynamic = js.native
  def getLastSearchOptions(): js.Dynamic = js.native
  def find(needle: String, options: js.Any = Nil, animate: Boolean = true): js.Dynamic = js.native
  def findNext(options: js.Any = Nil, animate: Boolean = true): js.Dynamic = js.native
  def findPrevious(options: js.Any = Nil, animate: Boolean = true): js.Dynamic = js.native
  def undo(): js.Dynamic = js.native
  def redo(): js.Dynamic = js.native
  def destroy(): js.Dynamic = js.native
  def setOptions(options: js.Any): js.Dynamic = js.native
}

@JSName("AceAjax.Editor")
object Editor extends js.Object

@JSName("AceAjax.VirtualRenderer")
class VirtualRenderer protected () extends js.Object {
  def this(container: HTMLElement, theme: String = new String()) = this()
  var scroller: js.Any = js.native
  var characterWidth: Double = js.native
  var lineHeight: Double = js.native
  def screenToTextCoordinates(left: Double, top: Double): js.Dynamic = js.native
  def setSession(session: IEditSession): js.Dynamic = js.native
  def updateLines(firstRow: Double, lastRow: Double): js.Dynamic = js.native
  def updateText(): js.Dynamic = js.native
  def updateFull(force: Boolean): js.Dynamic = js.native
  def updateFontSize(): js.Dynamic = js.native
  def onResize(force: Boolean, gutterWidth: Double, width: Double, height: Double): js.Dynamic = js.native
  def adjustWrapLimit(): js.Dynamic = js.native
  def setAnimatedScroll(shouldAnimate: Boolean): js.Dynamic = js.native
  def getAnimatedScroll(): Boolean = js.native
  def setShowInvisibles(showInvisibles: Boolean): js.Dynamic = js.native
  def getShowInvisibles(): Boolean = js.native
  def setShowPrintMargin(showPrintMargin: Boolean): js.Dynamic = js.native
  def getShowPrintMargin(): Boolean = js.native
  def setPrintMarginColumn(showPrintMargin: Boolean): js.Dynamic = js.native
  def getPrintMarginColumn(): Boolean = js.native
  def getShowGutter(): Boolean = js.native
  def setShowGutter(show: Boolean): js.Dynamic = js.native
  def getContainerElement(): HTMLElement = js.native
  def getMouseEventTarget(): HTMLElement = js.native
  def getTextAreaContainer(): HTMLElement = js.native
  def getFirstVisibleRow(): Double = js.native
  def getFirstFullyVisibleRow(): Double = js.native
  def getLastFullyVisibleRow(): Double = js.native
  def getLastVisibleRow(): Double = js.native
  def setPadding(padding: Double): js.Dynamic = js.native
  def getHScrollBarAlwaysVisible(): Boolean = js.native
  def setHScrollBarAlwaysVisible(alwaysVisible: Boolean): js.Dynamic = js.native
  def updateFrontMarkers(): js.Dynamic = js.native
  def updateBackMarkers(): js.Dynamic = js.native
  def addGutterDecoration(): js.Dynamic = js.native
  def removeGutterDecoration(): js.Dynamic = js.native
  def updateBreakpoints(): js.Dynamic = js.native
  def setAnnotations(annotations: js.Array[js.Any]): js.Dynamic = js.native
  def updateCursor(): js.Dynamic = js.native
  def hideCursor(): js.Dynamic = js.native
  def showCursor(): js.Dynamic = js.native
  def scrollCursorIntoView(): js.Dynamic = js.native
  def getScrollTop(): Double = js.native
  def getScrollLeft(): Double = js.native
  def getScrollTopRow(): Double = js.native
  def getScrollBottomRow(): Double = js.native
  def scrollToRow(row: Double): js.Dynamic = js.native
  def scrollToLine(line: Double, center: Boolean, animate: Boolean, callback: js.Function): js.Dynamic = js.native
  def scrollToY(scrollTop: Double): Double = js.native
  def scrollToX(scrollLeft: Double): Double = js.native
  def scrollBy(deltaX: Double, deltaY: Double): js.Dynamic = js.native
  def isScrollableBy(deltaX: Double, deltaY: Double): Boolean = js.native
  def textToScreenCoordinates(row: Double, column: Double): js.Dynamic = js.native
  def visualizeFocus(): js.Dynamic = js.native
  def visualizeBlur(): js.Dynamic = js.native
  def showComposition(position: Double): js.Dynamic = js.native
  def setCompositionText(text: String): js.Dynamic = js.native
  def hideComposition(): js.Dynamic = js.native
  def setTheme(theme: String): js.Dynamic = js.native
  def getTheme(): String = js.native
  def setStyle(style: String): js.Dynamic = js.native
  def unsetStyle(style: String): js.Dynamic = js.native
  def destroy(): js.Dynamic = js.native
}

@JSName("AceAjax.VirtualRenderer")
object VirtualRenderer extends js.Object

trait CommandManager extends js.Object {
  var byName: js.Array[EditorCommand] = js.native
  var commands: js.Array[EditorCommand] = js.native
  var platform: String = js.native
  def addCommands(commands: js.Array[EditorCommand]): js.Dynamic = js.native
  def addCommand(command: EditorCommand): js.Dynamic = js.native
  def exec(name: String, editor: Editor, args: js.Any): js.Dynamic = js.native
}

trait EditorCommand extends js.Object {
  var name: String = js.native
  var bindKey: js.Any = js.native
  var exec: js.Function = js.native
}

trait EditorChangeEvent extends js.Object {
  var start: Position = js.native
  var end: Position = js.native
  var action: String = js.native
  var lines: js.Array[js.Any] = js.native
}

@JSName("AceAjax.KeyBinding")
class KeyBinding protected () extends js.Object {
  def this(editor: Editor) = this()
  def setDefaultHandler(kb: js.Any): js.Dynamic = js.native
  def setKeyboardHandler(kb: js.Any): js.Dynamic = js.native
  def addKeyboardHandler(kb: js.Any, pos: js.Any): js.Dynamic = js.native
  def removeKeyboardHandler(kb: js.Any): Boolean = js.native
  def getKeyboardHandler(): js.Dynamic = js.native
  def onCommandKey(e: js.Any, hashId: js.Any, keyCode: js.Any): js.Dynamic = js.native
  def onTextInput(text: js.Any): js.Dynamic = js.native
}

@JSName("AceAjax.KeyBinding")
object KeyBinding extends js.Object
