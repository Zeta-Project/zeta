package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.AccessAuthorisation


object AccessAuthorisationFixtures {

  val entity1 = AccessAuthorisation(
    id = UUID.randomUUID,
    authorizedAccess = Map(
      "col1" -> Set(UUID.randomUUID, UUID.randomUUID, UUID.randomUUID),
      "col2" -> Set(UUID.randomUUID, UUID.randomUUID),
      "col3" -> Set.empty
    )
  )

  val entity2 = AccessAuthorisation(
    id = UUID.randomUUID,
    authorizedAccess = Map(
      "col4" -> Set(UUID.randomUUID),
      "col5" -> Set(UUID.randomUUID, UUID.randomUUID, UUID.randomUUID),
      "col6" -> Set.empty
    )
  )

  val entity2Updated: AccessAuthorisation = entity2.grantAccess("col7", UUID.randomUUID)

  val entity3 = AccessAuthorisation(
    id = UUID.randomUUID,
    authorizedAccess = Map.empty
  )

}
