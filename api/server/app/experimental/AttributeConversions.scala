package experimental

import scala.language.implicitConversions

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt


trait AttributeConversions {

  implicit def toMInt(n: Int): MInt = MInt(n)

  implicit def fromMInt(n: MInt): Int = n.value

}
