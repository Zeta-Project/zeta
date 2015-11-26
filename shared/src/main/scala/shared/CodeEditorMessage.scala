package shared

import scalot.Operation

sealed trait CodeEditorMessage

object CodeEditorMessage {

  case class TextOperation(op: Operation, docId: String) extends CodeEditorMessage

  case class DocAdded(str: String, revision: Int, docType: String, title: String, id: String, dslType: String, metaModelUuid: String) extends CodeEditorMessage

  case class DocLoaded(str: String, revision: Int, docType: String, title: String, id: String, dslType: String, metaModelUuid: String) extends CodeEditorMessage

  case class DocNotFound(dslType: String, metaModelUuid: String) extends CodeEditorMessage

  case class DocDeleted(id: String, dslType: String) extends CodeEditorMessage

}