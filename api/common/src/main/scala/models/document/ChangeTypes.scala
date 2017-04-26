package models.document

sealed trait Change
case object Created extends Change
case object Updated extends Change
case object Deleted extends Change

case class Changed(doc: Document, change: Change)
