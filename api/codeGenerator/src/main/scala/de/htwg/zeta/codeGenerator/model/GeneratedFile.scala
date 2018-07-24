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
    private def addPoint(list: List[String]) = if (list.isEmpty) "" else s"${list.mkString(".")}."
    val nameSpace: String = addPoint(nameSpaceList)
    val currentPackage: String = addPoint(packageList)
  }

  def scalaFile(name: String, content: FileStructure => Html): GeneratedFile = GeneratedFile(name, scala, content)
}
