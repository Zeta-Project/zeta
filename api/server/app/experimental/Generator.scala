package experimental


import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.Parameter
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.Model

// scalastyle:off indentation multiple.string.literals
object Generator {

  private implicit class FirstToLower(s: String) {
    def firstToLower: String = s.substring(0, 1).toLowerCase + s.substring(1)
  }

  def generate(metaModel: MetaModel, modelEntity: ModelEntity): Seq[File] = {


    val enums = metaModel.enums.map(enum => generateEnum(enum, modelEntity.id))

    val classes = metaModel.classes.map(clazz => generateClass(metaModel, modelEntity.model, clazz.name, modelEntity.id))

    val references = metaModel.references.map(reference => generateReference(metaModel, modelEntity.model, reference.name, modelEntity.id))

    val main = generateMain(metaModel, modelEntity.model, modelEntity.id)

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
          |case class $name(id: String, attributes: $name.Attributes, ${metaModel.name.firstToLower}: ${metaModel.name.capitalize}) {
          |
          |""".stripMargin +
        metaModel.references.filter(_.target.head.className == name).map(reference => generateIncomingEdge(metaModel, reference.name)).mkString +
        metaModel.references.filter(_.source.head.className == name).map(reference => generateOutgoingEdge(metaModel, reference.name)).mkString +
        clazz.methods.map(generateMethod).mkString +
        "}\n"
    File(fileId, s"$name.scala", content)
  }

  private def generateAttributes(attributes: Seq[MAttribute]): String = {
    "  case class Attributes(\n" +
      attributes.map(a => s"    ${if (a.constant) "" else "var "}${a.name}: ${generateAttributeType(a.typ)}").mkString(",\n") +
      "\n  )"
  }


  private def generateIncomingEdge(metaModel: MetaModel, name: String): String = {
    s"  lazy val incoming${name.capitalize}: List[${name.capitalize}] = ${metaModel.name.firstToLower}.${name.firstToLower}List.filter(_.target == this)\n\n"
  }

  private def generateOutgoingEdge(metaModel: MetaModel, name: String): String = {
    s"  lazy val outgoing${name.capitalize}: List[${name.capitalize}] = ${metaModel.name.firstToLower}.${name.firstToLower}List.filter(_.source == this)\n\n"
  }


  private def generateMethod(method: Method): String = {
    s"""|  def ${method.name}(${generateParameters(method.parameters)}): ${generateReturnType(method.returnType)} = {
        |${method.code.lines.map("    " + _).mkString("\n")}
        |  }
        |
        |""".stripMargin
  }

  private def generateParameters(p: Seq[Parameter]): String = {
    p.map(generateParameter).mkString(", ")
  }

  private def generateParameter(p: Parameter): String = {
    s"${p.name}: ${generateAttributeType(p.typ)}"
  }

  private def generateReturnType(typ: Option[AttributeType]): String = {
    typ.fold("Unit")(generateAttributeType)
  }

  private def generateAttributeType(typ: AttributeType): String = {
    typ match {
      case StringType => "String"
      case BoolType => "Boolean"
      case IntType => "Int"
      case DoubleType => "Double"
      case enum: MEnum => enum.name
    }
  }

  private def generateReference(metaModel: MetaModel, model: Model, name: String, fileId: UUID): File = {
    val reference = metaModel.referenceMap(name)
    val content =
      s"""|object $name {
          |
          |${generateAttributes(reference.attributes)}
          |
          |}
          |
          |case class $name(id: String, source: ${reference.source.head.className.capitalize}, target: ${
        reference.target.head.className.capitalize
      }, attributes: $name.Attributes, ${metaModel.name.firstToLower}: ${metaModel.name.capitalize}) {
          |
          |""".stripMargin +
        reference.methods.map(generateMethod).mkString +
        "}\n"
    File(fileId, s"$name.scala", content)
  }

  private def generateMain(metaModel: MetaModel, model: Model, fileId: UUID): File = {


    val content = {
      s"""object ${metaModel.name.capitalize} {
        |
        |${generateAttributes(metaModel.attributes)}
        |
        |}
        |
        |class ${metaModel.name.capitalize} {
        |
        |""".stripMargin +
        metaModel.classes.map(clazz => generateClassInstance(clazz, model)).mkString("\n\n") + "\n\n" +
        metaModel.references.map(reference => generateReferenceInstance(reference, model)).mkString("\n\n") + "\n\n" +
        s"  val attributes = ${metaModel.name.capitalize}.${generateAttributeInstance(metaModel.attributes, model.attributes)}\n\n" +
        metaModel.methods.map(generateMethod).mkString +
        "}\n"


    }

    File(fileId, s"${metaModel.name}.scala", content)
  }


  private def generateClassInstance(clazz: MClass, model: Model): String = {
    s"  val ${clazz.name.firstToLower}List: List[${clazz.name.capitalize}] = List(\n" +
      model.nodes.filter(_.clazz.name == clazz.name).map { node =>
        s"""    ${clazz.name.capitalize}("${node.name}", ${clazz.name.capitalize}.${generateAttributeInstance(clazz.attributes, node.attributes)}, this)"""
      }.mkString(",\n") +
      "\n  )\n\n" + generateMap(clazz.name)
  }

  private def generateReferenceInstance(reference: MReference, model: Model): String = {
    s"  val ${reference.name.firstToLower}List: List[${reference.name.capitalize}] = List(\n" +
      model.edges.filter(_.reference.name == reference.name).map { edge =>
        s"""    ${reference.name.capitalize}("${edge.name}", ${edge.source.head.clazz.name.firstToLower}Map("${edge.source.head.nodeNames.head
        }"), ${edge.target.head.clazz.name.firstToLower}Map("${
          edge.target.head.nodeNames.head}"), ${reference.name.capitalize}.${generateAttributeInstance(reference.attributes, edge.attributes)
          }, this)""".stripMargin
      }.mkString(",\n") +
      "\n  )\n\n" + generateMap(reference.name)
  }

  private def generateAttributeInstance(metaAttributes: Seq[MAttribute], attributes: Map[String, Seq[AttributeValue]]): String = {
    s"Attributes(" + metaAttributes.map(a => generateAttributeValue(attributes(a.name).head)).mkString(", ") + ")"
  }

  private def generateAttributeValue(value: AttributeValue): String = {
    value match {
      case MString(s) => "\"" + s + "\""
      case MBool(b) => b.toString
      case MInt(i) => i.toString
      case MDouble(d) => d.toString
      case EnumSymbol(enumName, name) => s"${enumName.capitalize}.${name.capitalize}"
    }
  }

  private def generateMap(name: String): String = {
    s"  val ${name.firstToLower}Map: Map[String, ${name.capitalize}] = ${name.firstToLower}List.map(${name
      .firstToLower} => (${name.firstToLower}.id, ${name.firstToLower})).toMap\n"
  }

}
