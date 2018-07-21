package de.htwg.zeta.codeGenerator.generation.model

import scala.annotation.tailrec
import scala.collection.mutable

import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFolder

/**
 */
object ModelGenerator extends App {

  @inline private def concatList[A, B](startList: List[A], addList: List[B])(toA: B => A): List[A] = {
    @tailrec def rec(list: List[A], add: List[B]): List[A] = add match {
      case Nil => list
      case head :: tail => rec(toA(head) :: list, tail)
    }
    rec(startList, addList)
  }

  private def collectAllEntities(startEntities: List[Entity]): List[Entity] = {
    val map = mutable.Map[String, Entity]()

    @tailrec def rec(list: List[Entity]): Unit = list match {
      case Nil =>
      case entity :: tail =>
        map.get(entity.name) match {
          case Some(_) => // already existing
          case None =>
            map(entity.name) = entity
            val listWithLinks = concatList(tail, entity.links)(_.entity)
            val updatedList = concatList(listWithLinks, entity.maps)(_.entity)
            rec(updatedList)
        }
    }

    rec(startEntities)
    map.values.toList
  }

  def generate(anchor: Anchor): GeneratedFolder = {
    createModelPackage(anchor, collectAllEntities(List(anchor.period, anchor.team)))
  }

  private def createModelPackage(start: Anchor, comps: List[Entity]): GeneratedFolder = {
    val components = GeneratedFolder("component", comps.map(cmp => ModelEntityGenerator.generate(cmp)), Nil)
    val periodEntity = PeriodModelGenerator.generate(start)
    val teamEntity = TeamModelGenerator.generate(start)
    val gameEntity = GameModelGenerator.generate()
    GeneratedFolder("model", Nil, List(components, periodEntity, teamEntity, gameEntity))
  }
}
