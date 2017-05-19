package de.htwg.zeta.server.generator.model.style.color

trait ColorOrGradient {
  def getRGBValue: String
  def createOpacityValue: String = """1.0"""
}
