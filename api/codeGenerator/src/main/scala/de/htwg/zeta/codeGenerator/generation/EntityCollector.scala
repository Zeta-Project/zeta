package de.htwg.zeta.codeGenerator.generation

import scala.annotation.tailrec
import scala.collection.mutable

import de.htwg.zeta.codeGenerator.model.Entity
import grizzled.slf4j.Logging

object EntityCollector extends Logging {


  @inline private def concatList[A, B](startList: List[A], addList: List[B])(toA: B => A): List[A] = {
    @tailrec def rec(list: List[A], add: List[B]): List[A] = add match {
      case Nil => list
      case head :: tail => rec(toA(head) :: list, tail)
    }
    rec(startList, addList)
  }

  def collectAllEntities(startEntities: Entity*): List[Entity] = {
    val map = mutable.Map[String, Entity]()

    @tailrec def rec(list: List[Entity]): Unit = list match {
      case Nil =>
      case entity :: tail =>
        map.get(entity.name) match {
          case Some(_) => /* already existing */ rec(tail)
          case None =>
            map(entity.name) = entity
            val listWithLinks = concatList(tail, entity.links)(_.entity)
            val updatedList = concatList(listWithLinks, entity.maps)(_.entity)
            rec(updatedList)
        }
    }

    rec(startEntities.toList)
    map.values.toList
  }


}
