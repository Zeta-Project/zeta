package de.htwg.zeta.codeGenerator

import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFolder
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

  private def generateFolder(folderName: String, parentNode: Entity): GeneratedFolder = {
    GeneratedFolder(folderName,
      collectAllEntities(parentNode).toList.map { case (name, entity) =>
        model.GeneratedFile(name, "scala", generateSingleEntity(entity))
      }, Nil)
  }

  def generateAnchor(anchor: Anchor): GeneratedFolder = {
    GeneratedFolder("klima", Nil, List(
      generateFolder("team", anchor.team),
      generateFolder("period", anchor.period),
    ))
  }

  // Model-Classes
  def generateEntity(entity: Entity): String = {
    val generated = collectAllEntities(entity).map { case (_, e) =>
      generateSingleEntity(e)
    }

    generated.mkString("\n")
  }

}