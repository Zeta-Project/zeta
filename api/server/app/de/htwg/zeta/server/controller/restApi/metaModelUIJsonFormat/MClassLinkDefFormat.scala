package de.htwg.zeta.server.controller.restApi.metaModelUIJsonFormat

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef


// replacement for MLinkDef
private[metaModelUIJsonFormat] object MClassLinkDefFormat extends MLinkDefFormat[MClassLinkDef] {
  override def buildMLink(name: String, upperBound: Int, lowerBound: Int, deleteIfLower: Boolean): MClassLinkDef =
    MClassLinkDef(name, upperBound, lowerBound, deleteIfLower)

  override def destroyMLink(mb: MClassLinkDef): (String, Int, Int, Boolean) = {
    MClassLinkDef.unapply(mb).get // safe call to get
  }
}
