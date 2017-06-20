package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.CodeDocument
import scalot.DelComp
import scalot.InsComp
import scalot.Operation
import scalot.Server
import scalot.SkipComp


object CodeDocumentFixtures {

  val operation1 = Operation(
    ops = Seq(InsComp("ins"), DelComp(2), SkipComp(1)),
    revision = 0,
    id = UUID.randomUUID.toString
  )

  val operation2 = Operation(
    ops = Seq(DelComp(0), SkipComp(1)),
    revision = 0,
    id = UUID.randomUUID.toString
  )

  val entity1 = CodeDocument(
    id = UUID.randomUUID,
    dslType = "dslType1",
    metaModelId = UUID.randomUUID,
    serverDocument = Server(
      str = "str1",
      operations = List(operation1, operation2),
      title = "title1",
      docType = "docType1",
      id = UUID.randomUUID.toString
    )
  )

  val entity2 = CodeDocument(
    id = UUID.randomUUID,
    dslType = "dslType2",
    metaModelId = UUID.randomUUID,
    serverDocument = Server(
      str = "str2",
      operations = List(operation1),
      title = "title2",
      docType = "docType2",
      id = UUID.randomUUID.toString
    )
  )

  val entity2Updated: CodeDocument = entity2.copy(metaModelId = UUID.randomUUID)

  val entity3 = CodeDocument(
    id = UUID.randomUUID,
    dslType = "dslType3",
    metaModelId = UUID.randomUUID,
    serverDocument = Server(
      str = "str3",
      operations = List(operation2),
      title = "title3",
      docType = "docType3",
      id = UUID.randomUUID.toString
    )
  )

}
