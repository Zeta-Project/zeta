package de.htwg.zeta.common.models.document

import models.entity.Entity

sealed trait Change
case object Created extends Change
case object Updated extends Change
case object Deleted extends Change

case class Changed(doc: Entity, change: Change)
