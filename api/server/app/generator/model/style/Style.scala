package generator.model.style

import generator.model.ClassHierarchy
import generator.model.style.color._
import generator.model.style.gradient.GradientColorArea
import generator.model.style.gradient.Gradient
import generator.model.style.gradient.GradientAlignment
import generator.parser.Cache
import generator.parser.CommonParserMethods
import parser._

sealed class Style private (
    val name: String = "noName",
    val description: Option[String] = None,
    val transparency: Option[Double] = None,
    val background_color: Option[ColorOrGradient] = None,
    val line_color: Option[ColorWithTransparency] = None,
    val line_style: Option[LineStyle] = None,
    val line_width: Option[Int] = None,
    val font_color: Option[Color] = None,
    val font_name: Option[String] = None,
    val font_size: Option[Int] = None,
    val font_bold: Option[Boolean] = None,
    val font_italic: Option[Boolean] = None,
    val gradient_orientation: Option[GradientAlignment] = None,
    val selected_highlighting: Option[ColorOrGradient] = None,
    val multiselected_highlighting: Option[ColorOrGradient] = None,
    val allowed_highlighting: Option[ColorOrGradient] = None,
    val unallowed_highlighting: Option[ColorOrGradient] = None,

    val parents: List[Style] = List()
) extends StyleContainerElement {
  override def toString = name
}

/**
 * StyleParser
 * either parses a complete style or just generates an anonymous Style out of only a list of attributes
 */
object Style extends CommonParserMethods {
  import ColorConstant.knownColors
  val validStyleAttributes = List("description", "transparency", "background-color", "line-color", "line-style", "line-width",
    "font-color", "font-name", "font-size", "font-bold", "font-italic", "gradient-orientation", "gradient-area-color",
    "gradient-area-offset", "allowed", "unallowed", "selected", "multiselected", "highlighting")

  lazy val validColor = ("(" + knownColors.keySet.map(i => if (i != knownColors.keySet.last) i + "|").mkString + ")").r

  private def highlighting_selected = "selected" ~ "=" ~> color ^^ { i => ("selected", i) }
  private def highlighting_multiselected = "multiselected" ~ "=" ~> color ^^ { i => ("multiselected", i) }
  private def highlighting_allowed = "allowed" ~ "=" ~> color ^^ { i => ("allowed", i) }
  private def highlighting_unallowed = "unallowed" ~ "=" ~> color ^^ { i => ("unallowed", i) }
  private def highlightingAttribute = highlighting_selected | highlighting_multiselected | highlighting_allowed | highlighting_unallowed <~ ",?".r
  private def highlighting = "(" ~> rep1(highlightingAttribute) <~ ")"

  /**
   * Methode for creating a child of type Style, by only giving parentStyles
   * @param cache only for delegating the actual creation to an apply method of StyleParser
   * @param parents holds all the parentStyles, the returnedsstyle will inherit from
   *                if only one style is given, the given style is returned -> you need two to make an actual child
   * @return an Option including a new Style or None if no parentstyles were given
   * @note note, that attributes are inherited by the latest bound principle: Style A extends B -> B overrides attributes of A a call like:
   *       StyleParser.generateChildStyle(someCacheInstance, B, C, D, A) -> A's attributes have highest priority!!
   */
  def generateChildStyle(cache: Cache, parents: Option[Style]*): Option[Style] = {
    val parentStyles = parents.filter(_.isDefined)
    if (parentStyles.length == 1) return parentStyles.head
    else if (parentStyles.isEmpty) return None
    val childName = "(child_of -> " + parentStyles.map(p => p.get.name + { if (p != parentStyles.last) " & " else "" }).mkString + ")"
    Some(Style(childName, Some(parentStyles.toList.map(i => i.get.name)), List[(String, String)](), cache))
  }

  /**
   * for generating simple examples of style
   */
  def apply(n: String) = new Style(name = n)
  /**
   * @param name the name of the ne Style instance
   * @param parents the style instance's names from which the new Style will inherit information
   * @param attributes List of Tuples of Strings -> List[(String, String)] consist of tuple._1 = attribute's name and tuple._2 the according value
   * @param hierarchyContainer is a Diagram which contains the styleHierarchy which gives information about inheritance
   */
  def apply(name: String, parents: Option[List[String]], attributes: List[(String, String)], hierarchyContainer: Cache) = {
    parse(name, parents, attributes, hierarchyContainer)
  }
  def parse(name: String, parents: Option[List[String]], attributes: List[(String, String)], hierarchyContainer: Cache): Style = {

    implicit val cache = hierarchyContainer
    val extendedStyle = parents.getOrElse(List[String]()).foldLeft(List[Style]())((styles, s_name) =>
      if (cache.styleHierarchy.contains(s_name.trim)) s_name.trim :: styles else styles)

    /*mapping and defaults*/
    /*fill the "mapping and defaults" with extended information or with None values if necessary*/
    /**
     * relevant is a help-methode, which shortens the actual call to mostRelevant of ClassHierarchy by ensuring the collection-parameter
     * relevant speaks for the hierarchical context -> "A extends B, C" -> C is most relevant
     */
    def relevant[T](f: Style => Option[T]) = ClassHierarchy.mostRelevant(extendedStyle) { f }

    var description = relevant { _.description }
    var transparency = relevant { _.transparency }
    var background_color = relevant { _.background_color }
    var line_color = relevant { _.line_color }
    var line_style = relevant { _.line_style }
    var line_width = relevant { _.line_width }
    var font_color = relevant { _.font_color }
    var font_name = relevant { _.font_name }
    var font_size = relevant { _.font_size }
    var font_bold = relevant { _.font_bold }
    var font_italic = relevant { _.font_italic }
    var gradient_orientation = relevant { _.gradient_orientation }
    var selected_highlighting = relevant { _.selected_highlighting }
    var multiselected_highlighting = relevant { _.multiselected_highlighting }
    var allowed_highlighting = relevant { _.allowed_highlighting }
    var unallowed_highlighting = relevant { _.unallowed_highlighting }

    def ifValid[T](f: => T): Option[T] = {
      var ret: Option[T] = None
      try {
        ret = Some(f)
        ret
      } finally {
        ret
      }
    }

    if (attributes.nonEmpty) {
      attributes.foreach {
        case ("description", x) => description = Some(x)
        case ("transparency", x: String) => transparency = ifValid(x.toDouble)
        case ("background-color", x) => background_color = Some(parse(colorOrGradient, x).get)
        case ("line-color", x) => line_color = Some(parse(colorWithTransparency, x).get)
        case ("line-style", x) => line_style = LineStyle.getIfValid(x)
        case ("line-width", x: String) => line_width = ifValid(x.toInt)
        case ("font-color", x) => font_color = Some(parse(color, x).get)
        case ("font-name", x) => font_name = Some(x)
        case ("font-size", x: String) => font_size = ifValid(x.toInt)
        case ("font-bold", x) => font_bold = Some(matchBoolean(x))
        case ("font-italic", x) => font_italic = Some(matchBoolean(x))
        case ("gradient-orientation", x) => gradient_orientation = GradientAlignment.ifValid(x)
        case ("highlighting", rest) => parse(highlighting, rest).get.foreach {
          case ("allowed", h) => allowed_highlighting = Some(h)
          case ("unallowed", h) => unallowed_highlighting = Some(h)
          case ("selected", h) => selected_highlighting = Some(h)
          case ("multiselected", h) => multiselected_highlighting = Some(h)
        }
      }
    }

    /*create the instance of the actual new Style*/
    val newStyle = new Style(
      name,
      description,
      transparency,
      background_color,
      line_color,
      line_style,
      line_width,
      font_color,
      font_name,
      font_size,
      font_bold,
      font_italic,
      gradient_orientation,
      selected_highlighting,
      multiselected_highlighting,
      allowed_highlighting,
      unallowed_highlighting,
      extendedStyle
    )

    /*include new style instance in stylehierarchie*/
    if (extendedStyle.nonEmpty) {
      extendedStyle.reverse.foreach(elem => cache.styleHierarchy(elem.name, newStyle))
    } else {
      cache.styleHierarchy.newBaseClass(newStyle)
    }

    /*return the new Style*/
    newStyle
  }

  /**
   * states a problem to standardoutput
   * @param attribute is the attribute, that was not matchable
   * @param name is the name of the class
   * @param className is the type (e.G. Style, Shape ...)
   */
  def messageIgnored(attribute: String, name: String, className: String) = println("Styleparsing: attribute -> " +
    attribute + " in " + className + " '" + name + "' was ignored")

  /*neccessary for parsing*/
  val colors = Set(
    "white",
    "light-light-gray",
    "light-gray",
    "gray",
    "black",
    "red",
    "light-orange",
    "orange",
    "dark-orange",
    "yellow",
    "green",
    "light-green",
    "dark-green",
    "cyan",
    "light-blue",
    "blue",
    "dark-blue",
    "transparent"
  )

  def colorOrGradient: Parser[ColorOrGradient] = (color | transparent | gradient) ^^ {
    case g: Gradient => g
    case Transparent => Transparent
    case color: ColorOrGradient => color
  }

  def color: Parser[Color] = (RGB | colorConstant) ^^ {
    case (r: Int, g: Int, b: Int) => RGBColor((r, g, b))
    case constant: String => knownColors(constant)
  }
  private def RGB: Parser[(Int, Int, Int)] =
    "=" ~ "RGB" ~ "(" ~ "red" ~ "=" ~> (argument_int <~ ",") ~
      ("green" ~ "=" ~> argument_int <~ ",") ~
      ("blue" ~ "=" ~> argument_int <~ ")") ^^ {
        case red ~ blue ~ green => (red, blue, green)
      }
  private def colorConstant: Parser[String] = {
    ("(" + colors.map(c => c + { if (c != colors.last) "|" else "" }).mkString + ")").r ^^ { _.toString }
  }

  def transparent: Parser[ColorOrGradient] = "transparent".r ^^ { case t => Transparent }

  def gradient =
    ("=" ~ "gradient" ~> ident <~ "{") ~
      (("description" ~ "=" ~> argument_string)?) ~
      (rep(area) <~ "}") ^^ {
        case id ~ des ~ areas => Gradient(id, des, areas)
      }
  def area: Parser[GradientColorArea] =
    ("area" ~ "(" ~ "color" ~ "=" ~> color <~ ",") ~
      ("offset" ~ "=" ~> argument_double <~ ")") ^^ {
        case c ~ d => GradientColorArea(c, d)
      }

  def colorWithTransparency: Parser[ColorWithTransparency] = {
    (color | transparent) ^^ { _.asInstanceOf[ColorWithTransparency] }
  }
}
