package models

import java.util.UUID

/** A uniquely identifiable entity. */
trait Identifiable {

  /** The unique id of the entity */
  val id: UUID

}
