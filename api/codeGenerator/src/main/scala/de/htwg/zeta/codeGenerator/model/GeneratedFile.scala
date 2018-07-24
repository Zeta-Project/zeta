package de.htwg.zeta.codeGenerator.model

import de.htwg.zeta.codeGenerator.model.GeneratedFile.FileStructure
import play.twirl.api.Html

case class GeneratedFile(
    name: String,
    fileType: String,
    content: FileStructure => Html
)

object GeneratedFile {
  val scala = "scala"

  class FileStructure(nameSpaceList: List[String], packageList: List[String]) {
    val nameSpace: String = nameSpaceList.mkString(".")
    val currentPackage: String = packageList.mkString(".")
  }

  def scalaFile(name: String, content: FileStructure => Html): GeneratedFile = GeneratedFile(name, scala, content)
}
