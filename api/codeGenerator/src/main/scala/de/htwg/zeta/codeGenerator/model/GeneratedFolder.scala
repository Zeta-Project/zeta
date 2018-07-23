package de.htwg.zeta.codeGenerator.model

import scala.annotation.tailrec

case class GeneratedFolder(
    name: String,
    files: List[GeneratedFile],
    children: List[GeneratedFolder]
)

object GeneratedFolder {
  private def getLastAndFlipRest[T](headT: T, tailT: List[T]): (T, List[T]) = {
    @tailrec def rec(list: List[T], buff: List[T]): (T, List[T]) = list match {
      // can never be Nil
      case head :: Nil => head -> buff
      case head :: tail => rec(tail, head :: buff)

    }
    rec(headT :: tailT, Nil)
  }

  def wrapFiles(folderName: String)(files: GeneratedFile*): GeneratedFolder = {
    GeneratedFolder(folderName, files.toList, Nil)
  }

  def wrapInFolder(firstFolder: String, restFolder: String*)(inner: GeneratedFolder*): GeneratedFolder = {
    def wrap(name: String, folder: List[GeneratedFolder]): GeneratedFolder = {
      GeneratedFolder(name, Nil, folder)
    }

    val (prefixLast, prefixFlipped) = getLastAndFlipRest(firstFolder, restFolder.toList)
    prefixFlipped.foldLeft(wrap(prefixLast, inner.toList)) { (folder, p) => wrap(p, List(folder)) }
  }
}
