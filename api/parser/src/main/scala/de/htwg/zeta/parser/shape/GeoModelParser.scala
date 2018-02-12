package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object GeoModelParser extends CommonParserMethods with UniteParsers with ShapeTokens {

  def geoModels: Parser[List[GeoModel]] = rep(geoModel)

  private def geoModel: Parser[GeoModel] = ellipse | textfield

  private def ellipse: Parser[Ellipse] = {
    "ellipse" ~> leftBrace ~> style ~ position ~ size ~ geoModels <~ rightBrace ^^ { parseResult =>
      val style ~ position ~ size ~ children = parseResult
      Ellipse(style, position, size, children)
    }
  }

  private def textfield: Parser[Textfield] = {
    "textfield" ~> leftBrace ~> identifier ~ multiline ~ position ~ size ~ align <~ rightBrace ^^ { parseResult =>
      val identifier ~ multiline ~ position ~ size ~ align = parseResult
      Textfield(identifier, multiline, position, size, align)
    }
  }

  private def style = include(AttributeParser.style)

  private def position = include(AttributeParser.position)

  private def size = include(AttributeParser.size)

  private def align = include(AttributeParser.align)

  private def identifier = include(AttributeParser.identifier)

  private def multiline = include(AttributeParser.multiline)


}
