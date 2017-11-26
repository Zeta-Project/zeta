package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

/**
 * the MBounds trait
 */
trait MBounds {
  val upperBound: Int
  val lowerBound: Int

  require(
    (upperBound > lowerBound) || (upperBound == lowerBound && lowerBound != 0) || (upperBound == -1),
    "invalid lower and/or upper bound"
  )
}
