package de.htwg.zeta.server.generator.model.style.color

case object Transparent extends Color with ColorWithTransparency {
  override def getRGBValue = """transparent"""
}
