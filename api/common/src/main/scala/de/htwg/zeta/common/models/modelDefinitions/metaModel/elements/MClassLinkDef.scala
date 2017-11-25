package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

/** MLinkDef implementation */
case class MClassLinkDef(
    className: String,
    upperBound: Int,
    lowerBound: Int,
    deleteIfLower: Boolean
) extends MBounds
