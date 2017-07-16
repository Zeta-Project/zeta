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
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.Parameter

// scalastyle:off

object MethodGenerator extends App with AttributeConversions {


  val methods: Seq[Method] = Seq(
    Method(
      name = "add",
      parameters = Seq(
        Parameter("n1", IntType),
        Parameter("n2", IntType)
      ),
      returnType = Some(IntType),
      code =
        """|println("Test")
           |n1 + n2""".stripMargin
    )
  )

  def add(n1: Int, n2: Int): MInt = {
    n1 + n2
  }

  val z = add(1, MInt(2))


  val x: MInt = 4


  var generated = methods.map(generate).mkString("\n\n") + "\nadd(2, 3)"

  generated = s"object Test {\n$generated\n}"

  generated = generated + "\n\nTest.add(4, 4, 5+6)"

  println(generated)

  val toolbox = currentMirror.mkToolBox()


  println(toolbox.compile(toolbox.parse(generated)))


  private def generate(method: Method): String = {
    s"""|  def ${method.name}(${generateParameters(method.parameters)}): ${generateReturnType(method.returnType)} = {
        |${method.code.lines.map("    " + _).mkString("\n")}
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
