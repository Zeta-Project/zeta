package experimental

import scala.collection.immutable.Seq
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.Declaration
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.Implementation
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.Parameter

// scalastyle:off

object MethodGenerator extends App with AttributeConversions {


  val methods: Map[Declaration, Implementation] = Map(
    Declaration("add", List(
      Parameter("n1", IntType),
      Parameter("n2", IntType)
    )) -> Implementation(
      """|println("Test"f)
         |n1 + n2""".stripMargin,
      Some(IntType)),
    Declaration("add2", List(
      Parameter("n1", IntType),
      Parameter("n2", IntType),
      Parameter("n3", IntType)
    )) -> Implementation(
      """|n1 + n2 + n3""".stripMargin,
      Some(IntType))
  )

  def add(n1: Int, n2: Int): MInt = {
    n1 + n2
  }

  val z = add(1, MInt(2))


  val x: MInt = 4



  val generated = methods.toSeq.map(generate).mkString("\n\n") + "\nadd(2, 3)"

  println(generated)

  val toolbox = currentMirror.mkToolBox()


  println(toolbox.compile(toolbox.parse(generated)))


  private def generate(method: (Declaration, Implementation)): String = {
    s"""|  def ${method._1.name}(${generateParameters(method._1.parameters)}): ${generateReturnType(method._2.returnType)} = {
        |${method._2.code.lines.map("    " + _).mkString("\n")}
        |  }""".stripMargin
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

}
