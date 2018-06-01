package de.htwg.zeta.codeGenerator.model

/**
 * a Value in an Entity: fix, in, out
 *
 * @param name      name of the Value
 * @param valueType type of the Value
 */
case class Value(
    name: String,
    valueType: String
)
