package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.entity.AccessAuthorisation


object AccessAuthorisationTestCase extends EntityTestCase[AccessAuthorisation] {

  override val entity1 = AccessAuthorisation(
    id = UUID.randomUUID,
    authorizedAccess = Map(
      "col1" -> Set(UUID.randomUUID, UUID.randomUUID, UUID.randomUUID),
      "col2" -> Set(UUID.randomUUID, UUID.randomUUID),
      "col3" -> Set.empty
    )
  )

  override val entity2 = AccessAuthorisation(
    id = UUID.randomUUID,
    authorizedAccess = Map(
      "col4" -> Set(UUID.randomUUID),
      "col5" -> Set(UUID.randomUUID, UUID.randomUUID, UUID.randomUUID),
      "col6" -> Set.empty
    )
  )

  override val entity2Updated: AccessAuthorisation = entity2.grantAccess("col7", UUID.randomUUID)

  override val entity3 = AccessAuthorisation(
    id = UUID.randomUUID,
    authorizedAccess = Map.empty
  )

}
