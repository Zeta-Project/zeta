package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.{EnumParser, UniteParsers}
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes._
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object NodeAttributeParser extends CommonParserMethods with UniteParsers {

  def style: Parser[NodeStyle] = {
    ("style" ~> colon ~> ident) ^^ { parseResult =>
      val identifier = parseResult
      NodeStyle(identifier)
    }
  }

  def sizeMax: Parser[SizeMax] = {
    ("sizeMax" ~> leftParenthesis ~> "width" ~> colon ~> natural_number <~ comma) ~
      ("height" ~> colon ~> natural_number <~ rightParenthesis) ^^ { parseResult =>
      val width ~ height = parseResult
      SizeMax(width, height)
    }
  }

  def sizeMin: Parser[SizeMin] = {
    ("sizeMin" ~> leftParenthesis ~> "width" ~> colon ~> natural_number <~ comma) ~
      ("height" ~> colon ~> natural_number <~ rightParenthesis) ^^ { parseResult =>
      val width ~ height = parseResult
      SizeMin(width, height)
    }
  }

  def resizing: Parser[Resizing] = {
    ("resizing" ~> leftParenthesis ~> "horizontal" ~> colon ~> argument_boolean <~ comma) ~
      ("vertical" ~> colon ~> argument_boolean <~ comma) ~
      ("proportional" ~> colon ~> argument_boolean <~ rightParenthesis) ^^ { parseResult =>
      val horizontal ~ vertical ~ proportional = parseResult
      Resizing(horizontal, vertical, proportional)
    }
  }

  def anchor: Parser[Anchor] = {
    val anchor = relativeAnchor | absoluteAnchor | predefinedAnchor
    "anchor" ~> leftParenthesis ~> anchor <~ rightParenthesis ^^ { parseResult =>
      val anchor = parseResult
      anchor
    }
  }

  def relativeAnchor: Parser[RelativeAnchor] = {
    ("xoffset" ~> colon ~> natural_number <~ comma) ~
      ("yoffset" ~> colon ~> natural_number) ^^ { parseResult =>
      val xOffset ~ yOffset = parseResult
      RelativeAnchor(xOffset, yOffset)
    }
  }

  def absoluteAnchor: Parser[AbsoluteAnchor] = {
    ("x" ~> colon ~> natural_number <~ comma) ~
      ("y" ~> colon ~> natural_number) ^^ { parseResult =>
      val x ~ y = parseResult
      AbsoluteAnchor(x, y)
    }
  }

  def predefinedAnchor: Parser[PredefinedAnchor] = {
    val anchorPosition = include(EnumParser.parseEnum(AnchorPosition))
    ("predefined" ~> colon ~> anchorPosition) ^^ {
      PredefinedAnchor
    }
  }

}
