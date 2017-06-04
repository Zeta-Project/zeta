package models.document

import models.Identifiable

sealed trait Change
case object Created extends Change
case object Updated extends Change
case object Deleted extends Change

case class Changed(doc: Identifiable, change: Change)
