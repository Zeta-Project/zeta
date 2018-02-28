package de.htwg.zeta.common.model.style

case class Font(
    bold: Boolean,
    color: Color,
    italic: Boolean,
    name: String,
    size: Int,
    transparent: Boolean
)