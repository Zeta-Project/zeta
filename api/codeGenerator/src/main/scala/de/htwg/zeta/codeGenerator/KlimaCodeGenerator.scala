package de.htwg.zeta.codeGenerator

import de.htwg.zeta.codeGenerator.txt.ValueTemplate
import de.htwg.zeta.codeGenerator.txt.LinkTemplate
import de.htwg.zeta.codeGenerator.txt.MapLinkTemplate
import de.htwg.zeta.codeGenerator.txt.ReferenceLinkTemplate
import de.htwg.zeta.codeGenerator.txt.EntityTemplate

/**
 * For this to compile. SBT task twirlCompileTemplates needs to be executed first
 *
 */
object KlimaCodeGenerator{

  // Model-Classes
  def generateValue(): String = ValueTemplate.toString
  def generateLink(): String = LinkTemplate.toString
  def generateMapLink(): String = MapLinkTemplate.toString
  def generateReferenceLink(): String = ReferenceLinkTemplate.toString
  def generateEntity(): String = EntityTemplate.toString

}