package de.htwg.zeta.shared

import java.util.UUID

import scalot.Operation

sealed trait CodeEditorMessage

object CodeEditorMessage {

  case class TextOperation(op: Operation, docId: UUID) extends CodeEditorMessage

  case class DocAdded(str: String, revision: Int, docType: String, title: String, id: UUID, dslType: String, metaModelId: UUID) extends CodeEditorMessage

  case class DocLoaded(str: String, revision: Int, docType: String, title: String, id: UUID, dslType: String, metaModelId: UUID) extends CodeEditorMessage

  case class DocNotFound(dslType: String, metaModelId: UUID) extends CodeEditorMessage

  case class DocDeleted(id: UUID, dslType: String) extends CodeEditorMessage

}
