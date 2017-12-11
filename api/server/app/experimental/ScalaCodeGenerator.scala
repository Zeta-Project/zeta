package experimental


import java.util.UUID

import scala.collection.immutable.ListMap
import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance

// scalastyle:off indentation multiple.string.literals
object ScalaCodeGenerator {

  private implicit class CamelCase(s: String) {
    def toCamelCase: String = s.substring(0, 1).toLowerCase + s.substring(1)
  }

  def generate(metaModel: Concept, modelEntity: GraphicalDslInstance): Seq[File] = {

    val enums = metaModel.enums.map(enum => generateEnum(enum, modelEntity.id))

    val classes = metaModel.classes.map(clazz => generateClass(metaModel, modelEntity, clazz.name, modelEntity.id))

    val references = metaModel.references.map(reference => generateReference(metaModel, modelEntity, reference.name, modelEntity.id))

    val main = generateMain(metaModel, modelEntity, modelEntity.id)

    enums ++ classes ++ references ++ List(main)

  }

  private def generateEnum(enum: MEnum, fileId: UUID): File = {
    val symbols = enum.values.map(symbol => s"  object ${symbol.valueName} extends ${enum.name}\n").mkString("\n")
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

  private def generateClass(metaModel: Concept, model: GraphicalDslInstance, name: String, fileId: UUID): File = {
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
          |case class $name(id: String, attributes: $name.Attributes, ${model.name.toCamelCase}: ${model.name.capitalize}) {
          |
          |""".stripMargin +
        metaModel.references.filter(_.targetClassName == name).map(reference => generateIncomingEdge(model, metaModel, reference.name)).mkString +
        metaModel.references.filter(_.sourceClassName == name).map(reference => generateOutgoingEdge(model, metaModel, reference.name)).mkString +
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


  private def generateIncomingEdge(model: GraphicalDslInstance, metaModel: Concept, name: String): String = {
    s"  lazy val incoming${name.capitalize}: List[${name.capitalize}] = ${model.name.toCamelCase}.${name.toCamelCase}List.filter(_.target == this)\n\n"
  }

  private def generateOutgoingEdge(model: GraphicalDslInstance, metaModel: Concept, name: String): String = {
    s"  lazy val outgoing${name.capitalize}: List[${name.capitalize}] = ${model.name.toCamelCase}.${name.toCamelCase}List.filter(_.source == this)\n\n"
  }


  private def generateMethod(method: Method): String = {
    s"""|  def ${method.name}(${generateParameters(method.parameters)}): ${method.returnType.asString} = {
        |${method.code.lines.map("    " + _).mkString("\n")}
        |  }
        |""".stripMargin
  }

  private def generateParameters(p: ListMap[String, AttributeType]): String = {
    p.map((generateParameter _).tupled).mkString(", ")
  }

  private def generateParameter(name: String, typ: AttributeType): String = {
    s"$name: ${typ.asString}"
  }

  private def generateReference(metaModel: Concept, model: GraphicalDslInstance, name: String, fileId: UUID): File = {
    val reference = metaModel.referenceMap(name)

    val typeName = name.capitalize
    val source = reference.sourceClassName.capitalize
    val target = reference.targetClassName.capitalize
    val mainInstance = model.name.toCamelCase
    val mainType = model.name.capitalize

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

  private def generateMain(metaModel: Concept, model: GraphicalDslInstance, fileId: UUID): File = {

    val content = {
      s"""object ${model.name.capitalize} {
        |
        |${generateAttributes(metaModel.attributes)}
        |
        |}
        |
        |class ${model.name.capitalize} {
        |
        |""".stripMargin +
        metaModel.classes.map(clazz => generateClassInstance(clazz, model)).mkString("\n\n") + "\n\n" +
        metaModel.references.map(reference => generateReferenceInstance(reference, model)).mkString("\n\n") + "\n\n" +
        s"  val attributes = ${model.name.capitalize}.${generateAttributeInstance(metaModel.attributes, model.attributeValues)}\n\n" +
        metaModel.methods.map(generateMethod).mkString +
        "\n}\n"

    }

    File(fileId, s"${model.name}.scala", content)
  }


  private def generateClassInstance(clazz: MClass, model: GraphicalDslInstance): String = {
    val nodes = model.nodes.filter(_.className == clazz.name)

    val instance = clazz.name.toCamelCase
    val typ = clazz.name.capitalize

    nodes.map { node =>
      s"""  private val $instance${nodes.indexOf(node)} = $typ("${node.name}", $typ.${generateAttributeInstance(clazz.attributes, node.attributeValues)},
        |this)""".stripMargin
    }.mkString("\n") + "\n\n" +
      s"  val ${clazz.name.toCamelCase}List: List[${clazz.name.capitalize}] = List(\n" +
      model.nodes.filter(_.className == clazz.name).map { node =>
        s"    $instance${nodes.indexOf(node)}"
      }.mkString(",\n") +
      "\n  )\n\n" + generateMap(clazz.name)
  }

  private def generateReferenceInstance(reference: MReference, model: GraphicalDslInstance): String = {
    val edges = model.edges.filter(_.referenceName == reference.name)

    val instance = reference.name.toCamelCase
    val typ = reference.name.capitalize

    edges.map { edge =>
      val sourceNode = model.nodes.find(_.name == edge.sourceNodeName).get
      val targetNode = model.nodes.find(_.name == edge.targetNodeName).get
      val source = sourceNode.className.toCamelCase + model.nodes.filter(_.className == edge.sourceNodeName).indexOf(sourceNode)
      val target = targetNode.className.toCamelCase + model.nodes.filter(_.className == edge.targetNodeName).indexOf(targetNode)
      val attributes = s"$typ.${generateAttributeInstance(reference.attributes, edge.attributeValues)}"
      s"""  private val $instance${edges.indexOf(edge)} = $typ("${edge.name}", $source, $target, $attributes, this)""".stripMargin
    }.mkString("\n") + "\n\n" +
      s"  val ${reference.name.toCamelCase}List: List[${reference.name.capitalize}] = List(\n" +
      model.edges.filter(_.referenceName == reference.name).map { edge =>
        s"    $instance${edges.indexOf(edge)}"
      }.mkString(",\n") +
      "\n  )\n\n" + generateMap(reference.name)

  }

  private def generateAttributeInstance(metaAttributes: Seq[MAttribute], attributes: Map[String, AttributeValue]): String = {
    s"Attributes(" + metaAttributes.map(a => generateAttributeValue(attributes(a.name))).mkString(", ") + ")"
  }

  private def generateAttributeValue(value: AttributeValue): String = {
    value match {
      case StringValue(s) => "\"" + s + "\""
      case BoolValue(b) => b.toString
      case IntValue(i) => i.toString
      case DoubleValue(d) => d.toString
      case EnumValue(enumName, name) => s"${enumName.capitalize}.${name.capitalize}"
    }
  }

  private def generateMap(name: String): String = {
    s"  val ${name.toCamelCase}Map: Map[String, ${name.capitalize}] = ${name.toCamelCase}List.map(${
      name
        .toCamelCase
    } => (${name.toCamelCase}.id, ${name.toCamelCase})).toMap\n"
  }

}
