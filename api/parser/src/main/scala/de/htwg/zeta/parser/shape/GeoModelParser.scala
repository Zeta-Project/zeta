package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.Attributes._
import de.htwg.zeta.parser.{UniteParsers, UnorderedParser}
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object GeoModelParser extends CommonParserMethods with UniteParsers with ShapeTokens with UnorderedParser {

  def geoModels: Parser[List[GeoModel]] = rep(geoModel)

  def geoModel: Parser[GeoModel] = ellipse | textfield | repeatingBox | line | polyline

  private def ellipse: Parser[Ellipse] = {
    val attributes = unordered(once(style), once(position), once(size))
    "ellipse" ~> leftBrace ~> attributes ~ geoModels <~ rightBrace ^^ { parseResult =>
      val attributes ~ geoModels = parseResult
      implicit val attributeList: List[Any] = attributes
      Ellipse(!![Style], !![Position], !![Size], geoModels)
    }
  }

  private def textfield: Parser[Textfield] = {
    val attributes = unordered(once(identifier), optional(multiline), once(position), once(size), optional(align))
    "textfield" ~> leftBrace ~> attributes <~ rightBrace ^^ { implicit attributes =>
      Textfield(!![Identifier], ?[Multiline].getOrElse(Multiline(false)), !![Position], !![Size],
        ?[Align].getOrElse(Align(HorizontalAlignment.middle, VerticalAlignment.middle)))
    }
  }

  private def repeatingBox: Parser[RepeatingBox] = {
    val attributes = unordered(once(editable), once(foreach))
    "repeatingBox" ~> leftBrace ~> attributes ~ rep1(geoModel) <~ rightBrace ^^ { parseResult =>
      val attributes ~ geoModels = parseResult
      implicit val attributeList: List[Any] = attributes
      RepeatingBox(!![Editable], !![For], geoModels)
    }
  }

  private def line: Parser[Line] = {
    val attributes = unordered(optional(style), exact(2, point))
    "line" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      implicit val attribtes: List[Any] = parseResult
      val from :: to :: Nil = *[Point]
      Line(?[Style], from, to)
    }
  }

  private def polyline: Parser[Polyline] = {
    val attributes = unordered(optional(style), min(2, point))
    "polyline" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      implicit val points: List[Any] = parseResult
      Polyline(?[Style], *[Point])
    }
  }

  private def style = include(AttributeParser.style)

  private def position = include(AttributeParser.position)

  private def point = include(AttributeParser.point)

  private def size = include(AttributeParser.size)

  private def align = include(AttributeParser.align)

  private def identifier = include(AttributeParser.identifier)

  private def multiline = include(AttributeParser.multiline)

  private def editable = include(AttributeParser.editable)

  private def foreach = include(AttributeParser.foreach)
}
