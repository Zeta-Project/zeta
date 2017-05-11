package generator.model.shapecontainer.shape.geometrics

import generator.model.shapecontainer.shape.geometrics.Alignment.VAlign
import generator.model.shapecontainer.shape.geometrics.Alignment.HAlign
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.CommonParserMethods
import generator.parser.IDtoStyle

/**
 * Created by julian on 03.11.15.
 * representation of a description
 */
sealed class Description private (
    override val id: String,
    val style: Option[Style],
    val hAlign: Option[HAlign],
    val vAlign: Option[VAlign])
  extends TextBody

/**
 * Description
 */
object Description extends CommonParserMethods {

  /**
   * @param attrs Attributes
   * @param parentStyle Style instance
   * @param cache Cache instance
   * @return Description instance
   */
  def parse(attrs: (String, String), parentStyle: Option[Style], cache: Cache): Option[Description] = {
    // mapping
    var hali: Option[HAlign] = None
    var vali: Option[VAlign] = None
    var styl: Option[Style] = Style.generateChildStyle(cache, parentStyle, IDtoStyle(attrs._1)(cache))
    var id: String = ""

    val attributes = attrs._2.split("\n")
    attributes.foreach { x =>
      if (x.matches("align\\s*\\((horizontal=)?(center|left|right),\\s*(vertical=)?(top|middle|bottom)\\)")) {
        hali = Alignment.parseHAlign("(center|right|left)".r.findFirstIn(x).get)
        vali = Alignment.parseVAlign("(top|middle|bottom)".r.findFirstIn(x).get)
      } else if (x.matches("id.*")) {
        id = parse(idAsString, x).get
      } else if (cache.styleHierarchy.contains(x)) {
        styl = Style.generateChildStyle(cache, styl, IDtoStyle(x)(cache))
      }
    }

    if (id != "") {
      Some(new Description(id, styl, hali, vali))
    } else {
      None
    }
  }
}
