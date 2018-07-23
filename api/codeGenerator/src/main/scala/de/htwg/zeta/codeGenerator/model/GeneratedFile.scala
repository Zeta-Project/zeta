package de.htwg.zeta.codeGenerator.model

case class GeneratedFile(
    name: String,
    fileType: String,
    content: String
)

object GeneratedFile {
  val scala = "scala"

  def scalaFile(name: String, content: String): GeneratedFile = GeneratedFile(name, scala, content)
}
