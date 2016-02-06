package generator.generators.shape

import generator.generators.style.StyleGenerator
import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics.Alignment.{CENTER, RIGHT, LEFT, HAlign}
import generator.model.shapecontainer.shape.geometrics._
import generator.model.style.HasStyle
import scala.collection.mutable.HashMap

/**
 * Created by julian on 19.01.16.
 * shape and inlinestyle generator kindof useless since inlinestyles are now automatically merged with the actual styles....
 */


object GeneratorShapeAndInlineStyle {
  
      def shapeStyleHead = """
        function getShapeStyle(elementName) {

        var style;

        switch(elementName) {
      """

      def generateShapeStyle( shape:Shape, packageName: String, LastElement: Boolean,  attrs:HashMap[String, HashMap[GeometricModel, String]])={
        if(shape != null) {
          val att = attrs(shape.name)
          """
          case """"+shape.name+"""":
          """+
          shape.shapes.get.map{s => getRightShape(s, att(s)) + "\n"}
          """break;

          """
        }
      }


  private def getRightShape(g:GeometricModel, shapeClass:String)= g match{
    case l:Line => getShape(l, shapeClass)
    case rr:RoundedRectangle => getShape(rr, shapeClass)
    case r:Rectangle => getShape(r, shapeClass)
    case e:Ellipse => getShape(e, shapeClass)
    case t:Text => getShape(t, shapeClass)
    case pl:PolyLine => getShape(pl, shapeClass)
    case p:Polygon => getShape(p, shapeClass)
  }


      protected def getShape(shape:Line, shapeClass: String)={
        var ret = """
        """
        if(shape.style.isDefined){
          ret += """style = {'line."""+shapeClass+"""':{} };"""
          ret += """style['line."""+shapeClass+"""'] = getStyle('"""+shape.style.get.name+"""').line;"""
        }
        ret += """style['line."""+shapeClass+"""'] = {
          """+getInlineStyle(shape)+"""
        };
        """
        ret
      }

      protected def getShape( shape:Ellipse, shapeClass: String)={
        var ret = """
        """
        if(shape.style.isDefined) {
          ret += """style = {'ellipse.""" + shapeClass + """':{} };"""
          ret += """style['ellipse.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').line;"""
        }
        ret += """style['ellipse."""+shapeClass+"""'] = {
           """+getInlineStyle(shape)+"""
        };
        """
        ret
      }

      protected def getShape( shape:Rectangle, shapeClass: String)= {
        var ret = """
                  """
        if (shape.style.isDefined) {
          ret += """style = {'rect.""" + shapeClass + """':{} };"""
          ret += """style['rect.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').line;"""
        }
        ret += """style['rect.""" + shapeClass + """'] = {
          """+getInlineStyle(shape)+"""
        };
        """
        ret
      }

       protected def getShape( shape:RoundedRectangle, shapeClass: String)= {
         var ret = """
                   """
         if (shape.style.isDefined) {
           ret += """style = {'rect.""" + shapeClass + """':{} };"""
           ret += """style['rect.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').line;"""
         }
         ret += """style['rect.""" + shapeClass + """'] = {
            """+getInlineStyle(shape)+"""
             };
        """
         ret
       }


        protected def getShape( shape:Text, shapeClass: String)= {
          var ret = """
                    """
          if (shape.style.isDefined) {
            ret += """style = {'text.""" + shapeClass + """':{} };"""
            ret += """style['text.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').line;"""
          }
          ret += """style['text.""" + shapeClass + """'] = {
         """+getInlineStyle(shape)+"""
         ref-x: """+getRefXValue(shape.hAlign.getOrElse(CENTER))+"""
             };
         """
          ret
        }

      protected def getShape( shape:PolyLine, shapeClass: String)= {
        var ret = """
                  """
        if (shape.style.isDefined) {
          ret += """style = {'polyline.""" + shapeClass + """':{} };"""
          ret += """style['polyline.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').line;"""
        }
        ret += """style['polyline.""" + shapeClass + """'] = {
         """ + getInlineStyle(shape) + raw"""
             };
         """
        ret
      }

      protected def getShape( shape:Polygon, shapeClass: String)={
        var ret = """
                  """
        if (shape.style.isDefined) {
          ret += """style = {'polygon.""" + shapeClass + """':{} };"""
          ret += """style['polygon.""" + shapeClass + """'] = getStyle('""" + shape.style.get.name + """').line;"""
        }
        ret += """style['polygon.""" + shapeClass + """'] = {
         """ + getInlineStyle(shape) + raw"""
             };
         """
        ret
      }

      def shapeStyleFooter=
        """
        default:
          style = {};
        break;

      }
      return style;
    }

    """


  protected def getInlineStyle(shape:GeometricModel)={
    val style = shape match {
      case h:HasStyle if h.style isDefined => h.style
      case _ => None
    }
    var ret = """
    """

    if(style isDefined){
      ret += StyleGenerator.commonAttributes(style.get)
      if(shape.isInstanceOf[Text]){
        ret += StyleGenerator.fontAttributes(style.get)
      }
    }
    //TODO ??? «IF layout.eContainer.eClass == TextLayout»
    ret
  }

  protected def getRefXValue(alignment:HAlign)={
     alignment match{
      case LEFT =>  0.0
      case RIGHT => 1.0
      case CENTER => 0.5
      case _ => 0.0
    }
  }
}
