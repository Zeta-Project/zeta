package de.htwg.zeta.server.controller.restApi.metaModelUIJsonFormat

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef

// replacement for MLinkDef
private[metaModelUIJsonFormat] object MReferenceLinkDefFormat extends MLinkDefFormat[MReferenceLinkDef] {
  override def buildMLink(name: String, upperBound: Int, lowerBound: Int, deleteIfLower: Boolean): MReferenceLinkDef =
    MReferenceLinkDef(name, upperBound, lowerBound, deleteIfLower)

  override def destroyMLink(mb: MReferenceLinkDef): (String, Int, Int, Boolean) = {
    MReferenceLinkDef.unapply(mb).get // safe call to get
  }

}
