package generator.model.shapecontainer.shape.geometrics

import generator.parser.CommonParserMethods

class Point(val x:Int, val y:Int, val curveBefore:Option[Int]=None, val curveAfter:Option[Int]=None)

object PointParser extends CommonParserMethods{
  def pointAttribute:Parser[Option[(String, Int)]] = ("(x|y|curveBefore|curveAfter)".r <~ "\\s*=\\s*".r) ~ ("[+-]?\\d+".r <~ ",?".r) ^^ {
    case varname ~ arg => Some((varname, arg.toInt))
    case _ => None
  }
  def point:Parser[Option[Point]] = "point\\s*\\(".r ~> pointAttribute ~ pointAttribute ~ (pointAttribute?) ~ (pointAttribute?) <~ ")" ^^ {
    case x ~ y ~ curveB ~ curveA => {
      var cB:Option[Int] = None
      var cA:Option[Int] = None
      if(curveB isDefined) {
        cB = Some(curveB.get.get._2)
        cA = Some(curveA.get.get._2)
      }
      Some(new Point(x.get._2, y.get._2, cB, cA))
    }
    case _ => None
  }

  def apply(line:String) = parse(line)
  def parse(line:String):Option[Point] ={
    val ret = parse(point, line).get
    if(ret isDefined)
      ret
    else
      None
  }

}
