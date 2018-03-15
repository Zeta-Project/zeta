package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes._
import de.htwg.zeta.parser.EnumParser
import de.htwg.zeta.parser.UniteParsers

object NodeAttributeParser extends CommonParserMethods with UniteParsers {

  def style: Parser[NodeStyle] = {
    ("style" ~> colon ~> ident) ^^ { parseResult =>
      val identifier = parseResult
      NodeStyle(identifier)
    }
  }

  def sizeMax: Parser[SizeMax] = {
    ("sizeMax" ~> leftParenthesis ~> "width" ~> colon ~> naturalNumber <~ comma) ~
      ("height" ~> colon ~> naturalNumber <~ rightParenthesis) ^^ { parseResult =>
      val width ~ height = parseResult
      SizeMax(width, height)
    }
  }

  def sizeMin: Parser[SizeMin] = {
    ("sizeMin" ~> leftParenthesis ~> "width" ~> colon ~> naturalNumber <~ comma) ~
      ("height" ~> colon ~> naturalNumber <~ rightParenthesis) ^^ { parseResult =>
      val width ~ height = parseResult
      SizeMin(width, height)
    }
  }

  def resizing: Parser[Resizing] = {
    ("resizing" ~> leftParenthesis ~> "horizontal" ~> colon ~> argumentBoolean <~ comma) ~
      ("vertical" ~> colon ~> argumentBoolean <~ comma) ~
      ("proportional" ~> colon ~> argumentBoolean <~ rightParenthesis) ^^ { parseResult =>
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
    ("xoffset" ~> colon ~> naturalNumber <~ comma) ~
      ("yoffset" ~> colon ~> naturalNumber) ^^ { parseResult =>
      val xOffset ~ yOffset = parseResult
      RelativeAnchor(xOffset, yOffset)
    }
  }

  def absoluteAnchor: Parser[AbsoluteAnchor] = {
    ("x" ~> colon ~> naturalNumber <~ comma) ~
      ("y" ~> colon ~> naturalNumber) ^^ { parseResult =>
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
