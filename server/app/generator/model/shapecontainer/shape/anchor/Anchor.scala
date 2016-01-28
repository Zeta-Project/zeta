package generator.model.shapecontainer.shape.anchor

import generator.util.CommonParserMethodes

/**
 * Created by julian on 19.10.15.
 * representation of an Anchor, or its various types
 */
object Anchor extends CommonParserMethodes{

  abstract class AnchorType

  abstract class AnchorPredefined extends AnchorType
    object Center extends AnchorPredefined
    object Corners extends AnchorPredefined

  abstract class AnchorManual(val positions:List[(Int, Int)]) extends AnchorType
    class AnchorRelativePosition(pos:List[(Int, Int)]) extends AnchorManual(pos)
    class AnchorFixPointPosition(pos:List[(Int, Int)]) extends AnchorManual(pos)

  def anchor:Parser[AnchorType] = (anchorPredefined | anchorManual) ^^ {case anch => anch}

  def anchorPredefined:Parser[AnchorPredefined] = "=?".r ~> "(center|corners)".r ^^ {
    case center:String if center == "center" => Center
    case corners:String if corners == "corners" => Corners
  }

  def anchorManual:Parser[AnchorManual] = anchorFixPointPosition | anchorRelativePosition ^^ {
    case anch:AnchorManual => anch
    case _ => println("no match");new AnchorRelativePosition(List())
  }

  def anchorRelativePosition:Parser[AnchorRelativePosition] = "\\{".r ~> rep(relativePosition) <~ "\\}".r ^^ {
    case relpos:List[Option[(Int, Int)]] => new AnchorRelativePosition(relpos.map{_.get})
    case _ =>  new AnchorRelativePosition(List())
  }
  def anchorFixPointPosition:Parser[AnchorFixPointPosition] = "\\{".r ~> rep(fixPointPosition) <~ "\\}".r ^^ {
    case relpos:List[Option[(Int, Int)]] =>new AnchorFixPointPosition(relpos.map{_.get})
    case _ => new AnchorFixPointPosition(List())
  }

  def relativePosition:Parser[Option[(Int, Int)]] = "position\\s*\\(\\s*(xoffset=)?".r ~> argument ~ ((",\\s*(yoffset=)?".r ~> argument) <~"\\)\n*".r) ^^ {
    case xarg ~ yarg => Some((xarg.toInt, yarg.toInt))
    case _ => None}

  def fixPointPosition:Parser[Option[(Int, Int)]] = "position\\s*\\(\\s*(x=)?".r ~> argument ~ ((",\\s*(y=)?".r ~> argument) <~"\\)\n*".r) ^^ {
    case xarg ~ yarg => Some((xarg.toInt, yarg.toInt))
    case _ => None}
}
