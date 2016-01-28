package generator.model.style

import generator.model.ClassHierarchy
import generator.util.{Cache, CommonParserMethodes}

class Style(val name: String = "noName",
            val description: Option[String] = None,
            val transparency: Option[Double] = None,
            val background_color: Option[ColorOrGradient] = None,
            val line_color: Option[Color] = None,
            val line_style: Option[LineStyle] = None,
            val line_width: Option[Int] = None,
            val font_color: Option[ColorOrGradient] = None,
            val font_name: Option[String] = None,
            val font_size: Option[Int] = None,
            val font_bold: Option[Boolean] = None,
            val font_italic: Option[Boolean] = None,
            val gradient_orientation: Option[GradientAlignment] = None,
            val gradient_area_color: Option[ColorOrGradient] = None,
            val gradient_area_offset: Option[Double] = None,
            val selected_highlighting: Option[ColorOrGradient] = None,
            val multiselected_highlighting: Option[ColorOrGradient] = None,
            val allowed_highlighting: Option[ColorOrGradient] = None,
            val unallowed_highlighting: Option[ColorOrGradient] = None,

            val childOf: List[Style] = List()) {
  val key: Long = hashCode
  override def toString = name
}


/**
 * StyleParser
 * either parses a complete style or just generates an anonymous Style out of only a list of attributes*/
object Style extends CommonParserMethodes {
  val validStyleAttributes = List("description", "transparency", "background-color", "line-color", "line-style", "line-width",
    "font-color", "font-name", "font-size", "font-bold", "font-italic", "gradient-orientation", "gradient-area-color",
    "gradient-area-offset", "allowed", "unallowed", "selected", "multiselected", "highlighting")

  private def parseAttributes(input:String) = parse(attributes, input).get

  private def attributes = "style\\s*[\\(\\{]".r ~> rep(styleAttribute) <~ "[\\)\\}]".r ^^ {case attr:List[(String, String)] => attr}
  private def styleVariable =("""("""+Style.validStyleAttributes.map(i => if(i != validStyleAttributes.last) i+"|").mkString+""")""").r ^^ {_.toString}
  private def styleAttribute = styleVariable ~ (styleArguments <~ ",?".r)^^ {case v ~ a => (v, a)}
  private def styleArguments = styleVariable ~> ("=?\\s*".r ~> argument) ^^ {case arg => arg}

  lazy val validColor = ("("+knownColors.keySet.map(i => if(i != knownColors.keySet.last) i+"|").mkString+")").r
  private def highlighting_selected       = "selected" ~ "=" ~> validColor ^^ {i => ("selected",knownColors(i))}
  private def highlighting_multiselected  = "multiselected" ~ "=" ~> validColor ^^ {i => ("multiselected",knownColors(i))}
  private def highlighting_allowed        = "allowed" ~ "=" ~> validColor ^^ {i => ("allowed",knownColors(i))}
  private def highlighting_unallowed      = "unallowed" ~ "=" ~> validColor ^^ {i => ("unallowed",knownColors(i))}
  private def highlightingAttribute = highlighting_selected | highlighting_multiselected | highlighting_allowed | highlighting_unallowed <~ ",?".r
  private def highlighting = "(" ~> rep1(highlightingAttribute) <~ ")"

  /**
   * Methode for creating a child of type Style, by only giving parentStyles
    *
    * @param hierarchyContainer only for delegating the actual creation to an apply method of StyleParser
   * @param parents holds all the parentStyles, the returnedsstyle will inherit from
   *                if only one style is given, the given style is returned -> you need two to make an actual child
   * @return an Option including a new Style or None if no parentstyles were given
   * @note note, that attributes are inherited by the latest bound principle: Style A extends B -> B overrides attributes of A a call like:
   *       StyleParser.makeLove(someCacheInstance, B, C, D, A) -> A's attributes have highest priority!!
   */
  def makeLove(hierarchyContainer: Cache, parents:Option[Style]*):Option[Style] ={
    val parentStyles = parents.filter(_.isDefined)
    if(parentStyles.length == 1) return parentStyles.head
    else if(parentStyles.isEmpty) return None
    val childName = "(child_of -> "+parentStyles.map( p => p.get.name+{if(p != parentStyles.last)" & "else ""}).mkString+")"
    Some(Style(childName, Some(parentStyles.toList.map(i => i.get.name)), List[(String, String)](), hierarchyContainer))
  }

  /**
   * @param name the name of the ne Style instance
   * @param parents the style instance's names from which the new Style will inherit information
   * @param attributes List of Tuples of Strings -> List[(String, String)] consist of tuple._1 = attribute's name and tuple._2 the according value
   * @param hierarchyContainer is a Diagram which contains the styleHierarchy which gives information about inheritance*/
  def apply(name:String, parents:Option[List[String]], attributes: List[(String, String)], hierarchyContainer: Cache) = parse(name, parents, attributes, hierarchyContainer)
  def parse(name:String, parents:Option[List[String]], attributes: List[(String, String)], hierarchyContainer: Cache):Style ={

    var extendedStyle:List[Style] = List[Style]()

    if(parents.nonEmpty)
      parents.get.foreach{parent => {
        val parentName = parent.trim
        if(hierarchyContainer.styleHierarchy.contains(parentName))
          extendedStyle = hierarchyContainer.styleHierarchy(parentName).data :: extendedStyle
        }
      }/*TODO if class was not found, to be inherited tell Logger*/
    /*mapping and defaults*/
    /*fill the "mapping and defaults" with extended information or with None values if necessary*/
    /** relevant is a help-methode, which shortens the actual call to mostRelevant of ClassHierarchy by ensuring the collection-parameter
      * relevant speaks for the hierarchical context -> "A extends B, C" -> C is most relevant */
    def relevant[T](f: Style => Option[T]) = ClassHierarchy.mostRelevant(extendedStyle) {f}

    var description: Option[String]                        = relevant { _.description }
    var transparency: Option[Double]                       = relevant { _.transparency }
    var background_color: Option[ColorOrGradient]          = relevant { _.background_color }
    var line_color: Option[Color]                          = relevant { _.line_color }
    var line_style: Option[LineStyle]                      = relevant { _.line_style }
    var line_width: Option[Int]                            = relevant { _.line_width }
    var font_color: Option[ColorOrGradient]                = relevant { _.font_color }
    var font_name: Option[String]                          = relevant { _.font_name }
    var font_size: Option[Int]                             = relevant { _.font_size }
    var font_bold: Option[Boolean]                         = relevant { _.font_bold }
    var font_italic: Option[Boolean]                       = relevant { _.font_italic }
    var gradient_orientation: Option[GradientAlignment]    = relevant { _.gradient_orientation }
    var gradient_area_color: Option[ColorOrGradient]       = relevant { _.gradient_area_color }
    var gradient_area_offset: Option[Double]               = relevant { _.gradient_area_offset }
    var selected_highlighting: Option[ColorOrGradient]     = relevant { _.selected_highlighting }
    var multiselected_highlighting: Option[ColorOrGradient]= relevant { _.multiselected_highlighting }
    var allowed_highlighting: Option[ColorOrGradient]      = relevant { _.allowed_highlighting }
    var unallowed_highlighting: Option[ColorOrGradient]    = relevant { _.unallowed_highlighting }

    def ifValid[T](f: => T):Option[T] = {
      var ret:Option[T] = None
      try { ret = Some(f)
      ret
      }finally {
        ret
      }}

    if(attributes.nonEmpty){
      attributes.foreach{
        case ("description", x) => description = Some(x)
        case ("transparency", x:String) => transparency = ifValid(x.toDouble)
        case ("background-color", x) => background_color = Some(knownColors.getOrElse(x, GRAY))
        case ("line-color", x) => line_color = Some(knownColors.getOrElse(x, WHITE))
        case ("line-style", x) => line_style= LineStyle.getIfValid(x)
        case ("line-width", x:String) => line_width= ifValid(x.toInt)
        case ("font-color", x) => font_color= Some(knownColors.getOrElse(x, BLACK))
        case ("font-name", x) => font_name= Some(x)
        case ("font-size", x:String) => font_size= ifValid(x.toInt)
        case ("font-bold", x) => font_bold = Some(matchBoolean(x))
        case ("font-italic", x) => font_italic = Some(matchBoolean(x))
        case ("gradient-orientation", x) => gradient_orientation = GradientAlignment.getIfValid(x)
        case ("gradient-area-color", x) => gradient_area_color = Some(knownColors.getOrElse(x, BLACK))
        case ("gradient-area-offset", x:String) => gradient_area_offset= ifValid(x.toDouble)
        case ("highlighting", rest) => parse(highlighting, rest).get.foreach{
          case ("allowed", h) => allowed_highlighting = Some(h)
          case ("unallowed", h) => unallowed_highlighting = Some(h)
          case ("selected", h) => selected_highlighting = Some(h)
          case ("multiselected", h) => multiselected_highlighting = Some(h)
        }
      }
    }

    /*create the instance of the actual new Style*/
    val newStyle = new Style(name, description, transparency, background_color, line_color, line_style, line_width, font_color,
      font_name, font_size, font_bold, font_italic, gradient_orientation, gradient_area_color, gradient_area_offset,
      selected_highlighting, multiselected_highlighting, allowed_highlighting, unallowed_highlighting, extendedStyle)

    /*include new style instance in stylehierarchie*/
    if (extendedStyle.nonEmpty) {
      extendedStyle.reverse.foreach(elem => hierarchyContainer.styleHierarchy(elem.name, newStyle))
    } else {
      hierarchyContainer.styleHierarchy.newBaseClass(newStyle)
    }

    /*return the new Style*/
    newStyle
  }

  val knownColors = Map(
    "white" -> WHITE,
    "light-light-gray" -> LIGHT_LIGHT_GRAY,
    "light-gray" -> LIGHT_GRAY,
    "gray" -> GRAY,
    "black" -> BLACK,
    "red" -> RED,
    "light-orange" -> LIGHT_ORANGE,
    "orange" -> ORANGE,
    "dark-orange" -> DARK_ORANGE,
    "yellow" -> YELLOW,
    "green" -> GREEN,
    "light-green" -> LIGHT_GREEN,
    "dark-green" -> DARK_GREEN,
    "cyan" -> CYAN,
    "light-blue" -> LIGHT_BLUE,
    "blue" -> BLUE,
    "dark-blue" -> DARK_BLUE,
    "transparent" -> Transparent)

  /**
   * states a problem to standardoutput
    *
    * @param attribute is the attribute, that was not matchable
   * @param name is the name of the class
   * @param className is the type (e.G. Style, Shape ...)*/
  def messageIgnored(attribute: String, name: String, className: String) = println("Styleparsing: attribute -> " +
    attribute + " in " + className + " '" + name + "' was ignored") /*TODO replace with call to Logger*/
}
