package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.AccessAuthorisation


object AccessAuthorisationFixtures {

  val entity1 = AccessAuthorisation(
    id = UUID.randomUUID,
    authorizedEntityAccess = Map(
      "col1" -> Set(UUID.randomUUID, UUID.randomUUID, UUID.randomUUID),
      "col2" -> Set(UUID.randomUUID, UUID.randomUUID),
      "col3" -> Set.empty
    ),
    authorizedFileAccess = Map.empty
  )

  val entity2 = AccessAuthorisation(
    id = UUID.randomUUID,
    authorizedEntityAccess = Map(
      "col4" -> Set(UUID.randomUUID),
      "col5" -> Set(UUID.randomUUID, UUID.randomUUID, UUID.randomUUID),
      "col6" -> Set.empty
    ),
    authorizedFileAccess = Map(
      UUID.randomUUID -> Set("file1.txt", "file2.txt", "file3.txt"),
      UUID.randomUUID -> Set("file4.txt"),
      UUID.randomUUID -> Set("file5.txt", "file6.txt")
    )
  )

  val entity2Updated: AccessAuthorisation = entity2.grantEntityAccess("col7", UUID.randomUUID)

  val entity3 = AccessAuthorisation(
    id = UUID.randomUUID,
    authorizedEntityAccess = Map.empty,
    authorizedFileAccess = Map(
      UUID.randomUUID -> Set("file7.txt", "file8.txt")
    )
  )

}
