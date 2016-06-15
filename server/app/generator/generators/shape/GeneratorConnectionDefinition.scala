package generator.generators.shape

import generator.generators.style.StyleGenerator
import generator.model.shapecontainer.connection.shapeconnections._
import generator.model.shapecontainer.connection.{Connection, Placing}
import generator.model.shapecontainer.shape.geometrics._
import generator.model.shapecontainer.shape.geometrics.layouts.TextLayout
import generator.model.style.HasStyle

import scala.collection.mutable

/**
 * Created by julian on 19.01.16.
 * the connection generator
 */
object GeneratorConnectionDefinition {
  val placingsCache =  mutable.HashMap[String, mutable.MutableList[Placing]]()
  val labelCache =  mutable.HashMap[String, mutable.MutableList[Placing]]()

  def generate(connections: Iterable[Connection]) = {
    head +
    """function getConnectionStyle(stylename){
      var style;
      switch(stylename){
    """ +
      connections.map(c => "case '" + c.name + "':\n" + {
      if(c.style.isDefined)
        "style = getStyle('"+c.style.get.name+"');\n"
      else
        "style = {'.connection':{stroke: 'black'}};"
      } +
      generateInlineStyle(c) +
      handlePlacings(c) +
      "break;\n").mkString +
      """default:
          style = {};
          break;
      }

      return style;
    }

    function getPlacings(stylename){
      var placings;
      switch(stylename){
        """ + generateCachedPlacings + raw"""
        default:
          placings = [];
        break;
      }

      return placings;
    }

    function getLabels(stylename){
      var labels;
      switch(stylename){
        """ + generateCachedLabels + raw"""
        default:
          labels = [];
        break;
      }

      return labels;
    }
    """
  }

  protected def head()={
    raw"""
    /*
     * This is a generated ShapeFile for JointJS
     */
    """
  }

  protected def generateInlineStyle(connection:Connection)={
    if(connection.style isDefined)
    """
    //Get inline style
    var inline = {
      '.connection, .marker-target, .marker-source':{
        """ + StyleGenerator.commonAttributes(connection.style.get) + """,
        """ + StyleGenerator.fontAttributes(connection.style.get) + """
      }
    };

    //Merge with default style
    jQuery.extend(style, inline);
    """
    else ""
  }

  protected def handlePlacings(connection:Connection )={
    val placings = connection.placing
    var isTargetMarkerSet = false; //Check, whether a target marker is set, because JointJS will show an arrow if none is set
    var ret = ""
    for(p <- placings) {

      p.position_offset match {
        case 0.0 => ret +=
          raw"""
            style['.marker-source'] = {
            ${generateStyle(p.shapeCon)},
            $generateMarkerSourceCorrection
            ${generateMarker(p)}};
          """
        case 1.0 => ret += raw"""style['.marker-target'] = { ${generateMirroredMarker(p)}, ${generateStyle(p.shapeCon)} };"""; isTargetMarkerSet = true
        case _   => cachePlacing(connection.name, p)
      }
      if(!isTargetMarkerSet) {
        ret += """style['.marker-target'] = {
                    d: 'M 0 0' //override JointJS default arrow
                  };
              """
      }
    }
    ret
  }

  protected def generateStyle(geometricModel: GeometricModel): String = {
      geometricModel match {
        case hs: HasStyle =>
          if(hs.style.isDefined) {
            println(geometricModel.isInstanceOf[Ellipse])
            println(hs.style.get.background_color)
            s"""
              ${StyleGenerator.commonAttributes(hs.style.get)},
              text: {
              ${StyleGenerator.fontAttributes(hs.style.get)}
              }
            """
          } else {
            ""
          }

      }
  }

  protected def generateCachedPlacings()= {
    var placings = ""
    if (placingsCache.nonEmpty) {
      placings = placingsCache.map { case (k, v) =>
        s"""
          case "$k":
            placings = [
            ${v.map(p => generatePlacing(p)).mkString(",")}
          ];
          break;
        """
      }.mkString
      placingsCache.clear()
    }
    placings
  }

  protected def generateCachedLabels()= {
    val labels = labelCache.map { case (k, v) =>
      "case \"" + k +
        """":
         labels = [
      """ + v.map(generateLabel).mkString +
        """
    ];
    break;
    """
  }.mkString
    labelCache.clear()
    labels
  }

  protected def generateLabel(placing:Placing)={
    raw"""
    {
      position: """+placing.position_offset +raw""",
      attrs: {
        rect: {fill: 'transparent'},
        text: {dy: """ + placing.position_distance + """}
      },
      id: '""" + placing.shapeCon.asInstanceOf[CDText].textBody+raw"""'
    }
    """
  }

  protected def generatePlacing(placing:Placing )={
    """
    {
      position: """ + placing.position_offset + """,
      """ + generateRightPlacingShape(placing.shapeCon, placing.position_distance.getOrElse(1)) + raw"""
    }"""
  }


  private def generateRightPlacingShape(g:GeometricModel, distance:Int):String = g match{
    case l:Line => generatePlacingShape(l, distance)
    case e:Ellipse => generatePlacingShape(e, distance)
    case p:Polygon => generatePlacingShape(p, distance)
    case pl:PolyLine => generatePlacingShape(pl, distance)
    case r:Rectangle => generatePlacingShape(r, distance)
    case rr:RoundedRectangle => generatePlacingShape(rr, distance)
    case t:Text => generatePlacingShape(t, distance)
  }

  protected def generatePlacingShape(shape:Line ,distance:Int)={
    /*TODO getInlineStyle is ignored because inlineStyle is implicitly mixed into the sourrounding style maybe the actual style attributes should be called here*/
    """
    markup: '<line />',
    attrs:{
      x1: """+ shape.points._1.x + """,
      y1: """+ shape.points._1.y + """,
      x2: """+ shape.points._2.x + """,
      y2: """+ shape.points._2.y + s""",
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get)}
    }
    """
  }

  protected def generatePlacingShape(shape:PolyLine ,distance:Int)={
    //TODO getInlineStyle is ignored because inlineStyle is implicitly mixed into the sourrounding style
    """
    markup: '<polyline />',
    attrs:{
      """ + generateStyleCorrections + """
      points: """" + shape.points.map(point => point.x + ", " + point.y + {if(point != shape.points.last)", " else "\""}) + raw""",
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get)}
    }
    """
  }

  protected def generatePlacingShape(shape:Rectangle, distance:Int)={
    //TODO getInlineStyle is ignored because inlineStyle is implicitly mixed into the sourrounding style
    """
    markup: '<rect />',
    attrs:{
      height: """ + shape.size_height + """,
      width: """ + shape.size_width + """,
      y: """ + (distance - shape.size_height/2) + raw""",
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get)}
    }
    """
  }

  protected def generatePlacingShape(shape:RoundedRectangle ,distance:Int)={
    //TODO getInlineStyle is ignored because inlineStyle is implicitly mixed into the sourrounding style
    """
    markup: '<rect />',
    attrs:{
      height: """+shape.size_height + """,
      width: """+shape.size_width + """,
      rx: """ + shape.curve_width + """,
      ry: """ + shape.curve_height + """,
      y: """ + (distance - shape.size_height/2) + raw""",
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get)}
    }
    """
  }

  protected def generatePlacingShape(shape:Polygon , distance:Int)={
    //TODO getInlineStyle is ignored because inlineStyle is implicitly mixed into the sourrounding style
    """
    markup: '<polygon />',
    attrs:{
      points: """" + shape.points.map(point => point.x +","+ point.y + {if(point != shape.points.last)" " else "\""}).mkString + raw""",
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get)}
    }
    """
  }

  protected def generatePlacingShape(shape:Ellipse , distance:Int)={
    //TODO getInlineStyle is ignored because inlineStyle is implicitly mixed into the sourrounding style
    """
    markup: '<ellipse />',
    attrs:{
      rx: """ + shape.size_width/2 + """,
      ry: """ + shape.size_height/2 + """,
      cy: """ + distance + raw""",
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get)},
      }
    """
  }

  protected def generatePlacingShape(shape:Text, distance:Int)={
    //TODO getInlineStyle is ignored because inlineStyle is implicitly mixed into the sourrounding style
    """
    markup: '<text>«shape.body.value»</text>',
    attrs:{
      y: """ + shape.size_height/2 + """
    }
    """
  }


  protected def generateMarker(placing:Placing )={
    """
    d: '""" + generateRightSvgPathData(placing.shapeCon) + """'
    """
  }

  protected def generateMirroredMarker(placing:Placing ) = {
    /*
     PolyLine and Polygon need to be mirrored against the y-axis because target
     marker gets rotated by 180 degree
    */
    val svgPathData = placing.shapeCon match {
      case p: Polygon => generateMirroredPolygon(placing.shapeCon.asInstanceOf[Polygon])
      case pl: PolyLine => generaMirroredPolyLine(placing.shapeCon.asInstanceOf[PolyLine])
      case _ => generateRightSvgPathData(placing.shapeCon)
    }

    s"""
    d: '$svgPathData'
    """
  }

  private def generaMirroredPolyLine(shape: PolyLine) = {
    val mirroredPoints = shape.points.map(p => new Point((p.x * -1), p.y))
    val head = mirroredPoints.head
    val tail = mirroredPoints.tail
    """M """ + head.x+" "+head.y + " " + tail.map(point => "L "+point.x+" "+point.y).mkString
  }

  private def generateMirroredPolygon(shape:Polygon) = {
    val mirroredPoints = shape.points.map(p => new Point((p.x * -1) , p.y))
    val head = mirroredPoints.head
    val tail = mirroredPoints.tail
    "M "+head.x+" "+head.y+" "+ tail.map(p => "L "+p.x +" "+p.y).mkString + "z"
  }

  private def generateRightSvgPathData(g:GeometricModel):String = {
    g match {
     case l: Line => generateSvgPathData(l)
     case p: Polygon => generateSvgPathData(p)
     case pl: PolyLine =>  generateSvgPathData(pl)
     case e: Ellipse => generateSvgPathData(e)
     case r: Rectangle => generateSvgPathData(r)
     case rr: RoundedRectangle => generateSvgPathData(rr)
     case t: Text => generateSvgPathData(t)
    }
  }

  protected def generateSvgPathData(shape:Line)={
    val points = shape.points
    """M """ + points._1.x + " " + points._1.y + " L " + points._2.x + " " + points._2.y
  }

  protected def generateSvgPathData(shape:PolyLine)={
    val head = shape.points.head
    val tail = shape.points.tail
    """M """ + head.x+" "+head.y + " " + tail.map(point => "L "+point.x+" "+point.y).mkString
  }

  protected def generateSvgPathData(shape:Rectangle )={
    """M """+shape.x +" " +shape.y + "l " + shape.size_width + " 0 l 0 "+shape.size_height +" l -"+shape.size_width+" 0 z"
  }

  protected def generateSvgPathData(shape:RoundedRectangle )={
    "M "+shape.x +" "+ shape.curve_width +" "+shape.y +" "+ shape.curve_height +" l " + (shape.size_width - 2*shape.curve_width) + "l 0 a " + shape.curve_width +" "+ shape.curve_height +" 0 0 1 " +shape.curve_width + " "+shape.curve_height+"l 0 " + (shape.size_height - 2*shape.curve_height)+ " a "+shape.curve_width+" "+shape.curve_height+" 0 0 1 -" +shape.curve_width+" "+shape.curve_height+" l -"+(shape.size_width - 2*shape.curve_width) +" 0 a "+shape.curve_width+" "+shape.curve_height+" 0 0 1 -"+shape.curve_width+" -"+shape.curve_height+" l 0 -"+(shape.size_height - 2*shape.curve_height)+" a "+shape.curve_width+" "+shape.curve_height+" 0 0 1 "+shape.curve_width+" -"+shape.curve_height
  }

  protected def generateSvgPathData(shape:Polygon )={
    val head = shape.points.head
    val tail = shape.points.tail
    "M "+head.x+" "+head.y+" "+ tail.map(p => "L "+p.x +" "+p.y).mkString + "z"
  }

  protected def generateSvgPathData(shape:Ellipse )={
    val rx = shape.size_width / 2
    val ry = shape.size_height / 2
   "M "+shape.x+" "+shape.y+" a  " + rx+" "+ry+" 0 0 1 "+rx+" -"+ry+" a  "+rx+" "+ry+" 0 0 1 "+rx+" "+ry+" a  "+rx+" "+ry+" 0 0 1 -"+rx+" "+ry+" a  "+rx+" "+ry+" 0 0 1 -"+rx+" -"+ry
  }

  protected def generateSvgPathData(shape:Text )={
    """"""
  }


  private def generateRightStyleCorrection(g:Any):String = g match{
    case pl:PolyLine => generateStyleCorrections
    case s:ShapeConnection => generateStyleCorrections(s)
    case _ => ""
  }

  protected def generateStyleCorrections = {
    """
    fill: 'transparent', //JointJS uses fill attribute to fill in all markers
    """
  }

  protected def generateStyleCorrections(shape:ShapeConnection )={
    """"""
  }

  protected def generateMarkerSourceCorrection()={
    """transform: 'scale(1,1)',"""
  }

  protected def cachePlacing(connection:String , placing:Placing ){
    if(placing.shapeCon.isInstanceOf[Text]){
      writeToCache(connection, placing, labelCache)
    }else{
      writeToCache(connection, placing, placingsCache)
    }

  }

  protected def writeToCache(connection:String, placing:Placing , cache: mutable.HashMap[String, mutable.MutableList[Placing]]) {
    if(cache.contains(connection)){
      cache(connection) += placing
    }else{
      val list = mutable.MutableList[Placing]()
        list += placing
      cache.put(connection, list)
    }
  }

  /**
   * getInlineStyle is deprecated, since the style attributes are now implicitly mixed in the surrounding style instance*/
  @deprecated
  protected def getInlineStyle(shape:GeometricModel )={
    shape match {
      case hs:HasStyle if hs.style isDefined =>s"""
        ${StyleGenerator.commonAttributes(hs.style.get)}
        ${if(shape.isInstanceOf[TextLayout])StyleGenerator.fontAttributes(hs.style.get)}
        """
      case _ => ""
    }
  }
}
