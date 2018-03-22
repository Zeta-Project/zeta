package de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts

import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.server.generator.parser.CommonParserMethods
import de.htwg.zeta.server.generator.parser.GeoModel

/**
 * Created by julian on 20.10.15.
 * representation of a RoundedRectangleLayout
 */
trait RoundedRectangleLayout extends CommonLayout {
  val curve_width: Int
  val curve_height: Int
}

object RoundedRectangleLayoutParser extends CommonParserMethods {

  def curve: Parser[Option[(Int, Int)]] = "[Cc]urve\\s*\\(\\s*(width=)?".r ~> argument ~ (",\\s*(height=)?".r ~> argument) <~ ")" ^^ {
    case width ~ height => Some((width.toInt, height.toInt))
    case _ => None
  }

  def apply(geoModel: GeoModel, parentStyle: Option[Style], hierarchyContainer: Cache) = parse(geoModel, parentStyle, hierarchyContainer)
  def parse(geoModel: GeoModel, parentStyle: Option[Style], hierarchyContainer: Cache): Option[RoundedRectangleLayout] = {
    val attributes = geoModel.attributes
    // mapping
    val commonLayout = CommonLayoutParser.parse(geoModel, parentStyle, hierarchyContainer)
    if (commonLayout.isEmpty) {
      None
    } else {
      attributes.find(x => x.matches("curve.+")) match {
        case Some(x) =>
          val newCurve = parse(curve, x).get
          Some(new RoundedRectangleLayout {
            override val style: Option[Style] = commonLayout.get.style
            override val curve_width: Int = newCurve.get._1
            override val curve_height: Int = newCurve.get._2
            override val position: Option[(Int, Int)] = commonLayout.get.position
            override val size_width: Int = commonLayout.get.size_width
            override val size_height: Int = commonLayout.get.size_height
          })
        case None => None
      }
    }
  }
}
