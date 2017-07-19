package experimental


import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.model.Model

// scalastyle:off indentation multiple.string.literals
object Generator {


  def generate(metaModel: MetaModel, modelEntity: ModelEntity): Seq[File] = {


    val enums = metaModel.enums.map(enum => generateEnum(enum, modelEntity.id))

    val classes = metaModel.classes.map(clazz => generateClass(metaModel, modelEntity.model, clazz.name, modelEntity.id))

    val references = metaModel.references.map(reference => generateReference(metaModel, modelEntity.model, reference.name, modelEntity.id))

    val main = generateMain(metaModel, modelEntity.id)

    enums ++ classes ++ references ++ List(main)

  }

  private def generateEnum(enum: MEnum, fileId: UUID): File = {
    val symbols = enum.symbols.map(symbol => s"  object ${symbol.name} extends ${enum.name}\n").mkString("\n")
    val content =
      s"""|sealed trait ${enum.name}
          |
          |object ${enum.name} {
          |
          |$symbols
          |}
          |""".stripMargin
    File(fileId, s"${enum.name}.scala", content)
  }

  private def generateClass(metaModel: MetaModel, model: Model, name: String, fileId: UUID): File = {
    val clazz = metaModel.classMap(name)

    val content =
      s"""|object $name {
          |
          |${generateAttributes(clazz.attributes)}
          |
          |}
          |
          |case class $name(name: String, attributes: $name.Attributes) {
          |
          |}
          |""".stripMargin


    File(fileId, s"$name.scala", content)
  }

  private def generateAttributes(attributes: Seq[MAttribute]): String = {

    s"  Attributes(\n    TODO\n  )"


  }

  private def generateReference(metaModel: MetaModel, model: Model, name: String, fileId: UUID): File = {
    File(fileId, s"$name.scala", "TODO")
  }

  private def generateMain(metaModel: MetaModel, fileId: UUID): File = {
    File(fileId, s"${metaModel.name}.scala", "TODO")
  }


}


object X extends App {

  val a = Generator.generate(PetriNetMetaModelFixture.metaModel, PetriNetModelFixture.modelEntity)

  a.foreach { file =>
    println(file.name)
    println("------------------------------------------------------------------------------------")
    println(file.content)
    println("------------------------------------------------------------------------------------")
    println()
  }

}
