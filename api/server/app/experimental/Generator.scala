package experimental


import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.Parameter
import de.htwg.zeta.common.models.modelDefinitions.model.Model

// scalastyle:off indentation multiple.string.literals
object Generator {

  private implicit class CamelCase(s: String) {
    def toCamelCase: String = s.substring(0, 1).toLowerCase + s.substring(1)
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

    val methods = if (clazz.methods.nonEmpty) {
      clazz.methods.map(generateMethod).mkString("\n") + "\n"
    } else {
      ""
    }

    val content =
      s"""|object $name {
          |
          |${generateAttributes(clazz.attributes)}
          |
          |}
          |
          |case class $name(id: String, attributes: $name.Attributes, ${metaModel.name.toCamelCase}: ${metaModel.name.capitalize}) {
          |
          |""".stripMargin +
        metaModel.references.filter(_.target.head.className == name).map(reference => generateIncomingEdge(metaModel, reference.name)).mkString +
        metaModel.references.filter(_.source.head.className == name).map(reference => generateOutgoingEdge(metaModel, reference.name)).mkString +
        methods +
        "}\n"
    File(fileId, s"$name.scala", content)
  }

  private def generateAttributes(attributes: Seq[MAttribute]): String = {
    if (attributes.nonEmpty) {
      "  case class Attributes(\n" +
        attributes.map(a => s"    ${if (a.constant) "" else "var "}${a.name}: ${a.typ.asString}").mkString(",\n") +
        "\n  )"
    } else {
      "  case class Attributes()"
    }
  }


  private def generateIncomingEdge(metaModel: MetaModel, name: String): String = {
    s"  lazy val incoming${name.capitalize}: List[${name.capitalize}] = ${metaModel.name.toCamelCase}.${name.toCamelCase}List.filter(_.target == this)\n\n"
  }

  private def generateOutgoingEdge(metaModel: MetaModel, name: String): String = {
    s"  lazy val outgoing${name.capitalize}: List[${name.capitalize}] = ${metaModel.name.toCamelCase}.${name.toCamelCase}List.filter(_.source == this)\n\n"
  }


  private def generateMethod(method: Method): String = {
    s"""|  def ${method.name}(${generateParameters(method.parameters)}): ${method.returnType.asString} = {
        |${method.code.lines.map("    " + _).mkString("\n")}
        |  }
        |""".stripMargin
  }

  private def generateParameters(p: Seq[Parameter]): String = {
    p.map(generateParameter).mkString(", ")
  }

  private def generateParameter(p: Parameter): String = {
    s"${p.name}: ${p.typ.asString}"
  }

  private def generateReference(metaModel: MetaModel, model: Model, name: String, fileId: UUID): File = {
    val reference = metaModel.referenceMap(name)

    val typeName = name.capitalize
    val source = reference.source.head.className.capitalize
    val target = reference.target.head.className.capitalize
    val mainInstance = metaModel.name.toCamelCase
    val mainType = metaModel.name.capitalize

    val head =
      s"""|object $typeName {
          |
          |${generateAttributes(reference.attributes)}
          |
          |}
          |
          |case class $typeName(id: String, source: $source, target: $target, attributes: $name.Attributes, $mainInstance: $mainType)""".stripMargin

    val content = if (reference.methods.nonEmpty) {
      val methods = reference.methods.map(generateMethod).mkString("\n")
      s"$head {\n\n$methods \n}\n"
    } else {
      head + "\n"
    }

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
        "\n}\n"

    }

    File(fileId, s"${metaModel.name}.scala", content)
  }


  private def generateClassInstance(clazz: MClass, model: Model): String = {
    val nodes = model.nodes.filter(_.className == clazz.name)

    val instance = clazz.name.toCamelCase
    val typ = clazz.name.capitalize

    nodes.map { node =>
      s"""  private val $instance${nodes.indexOf(node)} = $typ("${node.id}", $typ.${generateAttributeInstance(clazz.attributes, node.attributes)}, this)"""
    }.mkString("\n") + "\n\n" +
      s"  val ${clazz.name.toCamelCase}List: List[${clazz.name.capitalize}] = List(\n" +
      model.nodes.filter(_.className == clazz.name).map { node =>
        s"    $instance${nodes.indexOf(node)}"
      }.mkString(",\n") +
      "\n  )\n\n" + generateMap(clazz.name)
  }

  private def generateReferenceInstance(reference: MReference, model: Model): String = {
    val edges = model.edges.filter(_.referenceName == reference.name)

    val instance = reference.name.toCamelCase
    val typ = reference.name.capitalize

    edges.map { edge =>
      val sourceNode = model.nodes.find(_.id == edge.source.head.nodeIds.head).get
      val targetNode = model.nodes.find(_.id == edge.target.head.nodeIds.head).get
      val source = sourceNode.className.toCamelCase + model.nodes.filter(_.className == edge.source.head.className).indexOf(sourceNode)
      val target = targetNode.className.toCamelCase + model.nodes.filter(_.className == edge.target.head.className).indexOf(targetNode)
      val attributes = s"$typ.${generateAttributeInstance(reference.attributes, edge.attributes)}"
      s"""  private val $instance${edges.indexOf(edge)} = $typ("${edge.id}", $source, $target, $attributes, this)""".stripMargin
    }.mkString("\n") + "\n\n" +
      s"  val ${reference.name.toCamelCase}List: List[${reference.name.capitalize}] = List(\n" +
      model.edges.filter(_.referenceName == reference.name).map { edge =>
        s"    $instance${edges.indexOf(edge)}"
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
    s"  val ${name.toCamelCase}Map: Map[String, ${name.capitalize}] = ${name.toCamelCase}List.map(${
      name
        .toCamelCase
    } => (${name.toCamelCase}.id, ${name.toCamelCase})).toMap\n"
  }

}
