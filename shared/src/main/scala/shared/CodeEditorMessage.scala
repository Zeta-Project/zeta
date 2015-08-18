package shared

import scalot.Operation

sealed trait CodeEditorMessage

object CodeEditorMessage {
  case class TextOperation(op: Operation) extends CodeEditorMessage

  /*
   case class DocsAdded(docs: Seq[scalot.Client]) extends CodeEditorMessage
   case class DocsRemoved(docs: Seq[scalot.Client]) extends CodeEditorMessage
   case object GetDocs extends CodeEditorMessage
   */
}