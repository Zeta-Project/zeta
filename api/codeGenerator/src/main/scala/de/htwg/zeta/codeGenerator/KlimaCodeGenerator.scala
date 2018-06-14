package de.htwg.zeta.codeGenerator

import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.txt.EntityTemplate

/**
 * For this to compile. SBT task twirlCompileTemplates needs to be executed first
 *
 */
object KlimaCodeGenerator {

  private def collectAllEntities(startEntity: Entity): Map[String, Entity] = {
    val mapBuilder = Map.newBuilder[String, Entity]

    def rec(entity: Entity): Unit = {
      mapBuilder += entity.name -> entity
      entity.links.foreach { link =>
        rec(link.entity)
      }
    }

    rec(startEntity)
    mapBuilder.result()
  }

  def generateSingleEntity(entity: Entity): String = {
    EntityTemplate(entity).toString.trim
  }

  // Model-Classes
  def generateEntity(entity: Entity): String = {
    val generated = collectAllEntities(entity).map { case (_, e) =>
      generateSingleEntity(e)
    }

    generated.mkString("\n")
  }

}