package models

import java.util.UUID

/** A uniquely identifiable entity. */
trait Entity {

  /** The unique id of the entity */
  val id: UUID

}
