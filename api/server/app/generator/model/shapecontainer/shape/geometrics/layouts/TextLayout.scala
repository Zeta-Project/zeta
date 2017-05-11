package generator.model.shapecontainer.shape.geometrics.layouts

import scala.annotation.tailrec

import generator.model.shapecontainer.shape.geometrics.Alignment
import generator.model.shapecontainer.shape.geometrics.Alignment.VAlign
import generator.model.shapecontainer.shape.geometrics.Alignment.HAlign
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.GeoModel
import generator.parser.CommonParserMethods
import generator.parser.IDtoStyle

/**
 * representation of a textlayout and its parser
 */
trait TextLayout extends CommonLayout {
  val textBody: String
  val hAlign: Option[HAlign]
  val vAlign: Option[VAlign]
}

case class TextLayoutDefaultImpl(
    override val style: Option[Style],
    override val textBody: String,
    override val hAlign: Option[HAlign],
    override val vAlign: Option[VAlign],
    override val position: Option[(Int, Int)],
    override val size_width: Int,
    override val size_height: Int) extends TextLayout

object TextLayoutParser extends CommonParserMethods {
  def apply(geoModel: GeoModel, parentStyle: Option[Style], hierarchyContainer: Cache): Option[TextLayout] = {
    val attributes = geoModel.attributes

    // mapping
    val commonLayout = CommonLayoutParser.parse(geoModel, parentStyle, hierarchyContainer)
    commonLayout.map(cl => processAttributes(cl, attributes, hierarchyContainer))

  }

  private case class Mapping(
      hAli: Option[HAlign] = None,
      vAli: Option[VAlign] = None,
      text: Option[String] = None,
      style: Option[Style] = None
  )

  private val alignMatcher: String = "align\\s*\\((horizontal=)?(center|left|right),\\s*(vertical=)?(top|middle|bottom)\\)"

  private def parseRek(attributes: List[String], hierarchyContainer: Cache, commonLayout: CommonLayout): Mapping = {
    val defaultStyle: Option[Style] = commonLayout.style

    @tailrec
    def rek(attrList: List[String], mappings: Mapping): Mapping = {
      (attrList, mappings) match {
        case (Nil, _) | (_, Mapping(Some(_), Some(_), Some(_), Some(_))) =>
          mappings

        case (head :: tail, Mapping(None, None, _, _)) if head.matches(alignMatcher) =>
          val hAli = "(center|right|left)".r.findFirstIn(head).flatMap(Alignment.parseHAlign)
          val vAli = "(top|middle|bottom)".r.findFirstIn(head).flatMap(Alignment.parseVAlign)
          rek(tail, mappings.copy(hAli = hAli, vAli = vAli))

        case (head :: tail, Mapping(_, _, None, _)) if head.matches("(?s)textBody.*") =>
          val text = Some(parse(plainText, head).get)
          rek(tail, mappings.copy(text = text))

        case (head :: tail, Mapping(_, _, _, None)) if hierarchyContainer.styleHierarchy.contains(head) =>
          // generate anonymous style)
          val styleOpt = Style.generateChildStyle(hierarchyContainer, defaultStyle, IDtoStyle(head)(hierarchyContainer))
          rek(tail, mappings.copy(style = styleOpt))

        case (_ :: tail, _) =>
          rek(tail, mappings)
      }
    }

    val mappings = rek(attributes, Mapping())
    mappings.copy(style = mappings.style.orElse(defaultStyle))
  }

  private def processAttributes(commonLayout: CommonLayout, attributes: List[String], hierarchyContainer: Cache): TextLayoutDefaultImpl = {
    val mapping = parseRek(attributes, hierarchyContainer, commonLayout)

    TextLayoutDefaultImpl(
      mapping.style,
      mapping.text.getOrElse(""),
      mapping.hAli,
      mapping.vAli,
      commonLayout.position,
      commonLayout.size_width,
      commonLayout.size_height
    )
  }

  private def plainText: Parser[String] = {
    "textBody" ~> "(?s).*".r ^^ {
      _.toString
    }
  }
}


