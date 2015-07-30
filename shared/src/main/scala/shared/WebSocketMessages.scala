package shared

sealed trait CodeEditorMessage

object WebSocketMessages{
  case class DocChanged(doc: WootDoc, ops: Seq[Operation]) extends CodeEditorMessage
  case class DocsAdded(docs: Seq[WootDoc]) extends CodeEditorMessage

  case object GetDocs extends CodeEditorMessage
}