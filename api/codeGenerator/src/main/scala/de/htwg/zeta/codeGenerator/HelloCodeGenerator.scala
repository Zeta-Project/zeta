package de.htwg.zeta.codeGenerator

import net.htwg.zeta.codeGenerator.txt.HelloTwirlGenerator

/**
 * For this to compile. SBT task twirlCompileTemplates needs to be executed first
 *
 */
object HelloCodeGenerator extends App {
  println(HelloTwirlGenerator().toString)
}