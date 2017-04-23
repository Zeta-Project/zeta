package generator.model.style

import generator.model.ClassHierarchy
import generator.model.style.color.Color
import generator.model.style.color.ColorConstant
import generator.model.style.color.ColorOrGradient
import generator.model.style.color.ColorWithTransparency
import generator.model.style.color.RGBColor
import generator.model.style.color.Transparent
import generator.model.style.gradient.GradientColorArea
import generator.model.style.gradient.Gradient
import generator.model.style.gradient.GradientAlignment
import generator.parser.Cache
import generator.parser.CommonParserMethods
import parser.IDtoStyle

case class Style private (
    name: String = "noName",
    description: Option[String] = None,
    transparency: Option[Double] = None,
    background_color: Option[ColorOrGradient] = None,
    line_color: Option[ColorWithTransparency] = None,
    line_style: Option[LineStyle] = None,
    line_width: Option[Int] = None,
    font_color: Option[Color] = None,
    font_name: Option[String] = None,
    font_size: Option[Int] = None,
    font_bold: Option[Boolean] = None,
    font_italic: Option[Boolean] = None,
    gradient_orientation: Option[GradientAlignment] = None,
    selected_highlighting: Option[ColorOrGradient] = None,
    multiselected_highlighting: Option[ColorOrGradient] = None,
    allowed_highlighting: Option[ColorOrGradient] = None,
    unallowed_highlighting: Option[ColorOrGradient] = None,

    parents: List[Style] = List()
) extends StyleContainerElement {
  override def toString = name
}

/**
 * StyleParser
 * either parses a complete style or just generates an anonymous Style out of only a list of attributes
 */
object Style extends CommonParserMethods {
  val validStyleAttributes = List("description", "transparency", "background-color", "line-color", "line-style", "line-width",
    "font-color", "font-name", "font-size", "font-bold", "font-italic", "gradient-orientation", "gradient-area-color",
    "gradient-area-offset", "allowed", "unallowed", "selected", "multiselected", "highlighting")

  lazy val validColor = ("(" + ColorConstant.knownColors.keySet.map(i => if (i != ColorConstant.knownColors.keySet.last) i + "|").mkString + ")").r

  private def highlighting_selected = "selected" ~ "=" ~> color ^^ { i => ("selected", i) }
  private def highlighting_multiselected = "multiselected" ~ "=" ~> color ^^ { i => ("multiselected", i) }
  private def highlighting_allowed = "allowed" ~ "=" ~> color ^^ { i => ("allowed", i) }
  private def highlighting_unallowed = "unallowed" ~ "=" ~> color ^^ { i => ("unallowed", i) }
  private def highlightingAttribute = highlighting_selected | highlighting_multiselected | highlighting_allowed | highlighting_unallowed <~ ",?".r
  private def highlighting = "(" ~> rep1(highlightingAttribute) <~ ")"

  private val attributeMapper = Map[String, (Style, String) => Style](
    "description" -> ((style, value) => style.copy(description = Some(value))),
    "transparency" -> ((style, value) => style.copy(transparency = ifValid(value.toDouble))),
    "background-color" -> ((style, value) => style.copy(background_color = Some(parse(colorOrGradient, value).get))),
    "line-color" -> ((style, value) => style.copy(line_color = Some(parse(colorWithTransparency, value).get))),
    "line-style" -> ((style, value) => style.copy(line_style = LineStyle.getIfValid(value))),
    "line-width" -> ((style, value) => style.copy(line_width = ifValid(value.toInt))),
    "font-color" -> ((style, value) => style.copy(font_color = Some(parse(color, value).get))),
    "font-name" -> ((style, value) => style.copy(font_name = Some(value))),
    "font-size" -> ((style, value) => style.copy(font_size = ifValid(value.toInt))),
    "font-bold" -> ((style, value) => style.copy(font_bold = Some(matchBoolean(value)))),
    "font-italic" -> ((style, value) => style.copy(font_italic = Some(matchBoolean(value)))),
    "gradient-orientation" -> ((style, value) => style.copy(gradient_orientation = GradientAlignment.ifValid(value))),
    "highlighting" -> ((initialStyle, value) => processHighlighting(initialStyle, value))
  )

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
    if (parentStyles.length == 1) {
      parentStyles.head
    } else if (parentStyles.isEmpty) {
      None
    } else {
      val childName = "(child_of -> " + parentStyles.map(p => p.get.name + { if (p != parentStyles.last) " & " else "" }).mkString + ")"
      Some(Style(childName, Some(parentStyles.toList.map(i => i.get.name)), List[(String, String)](), cache))
    }
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

  private def parse(name: String, parents: Option[List[String]], attributes: List[(String, String)], hierarchyContainer: Cache): Style = {

    implicit val cache = hierarchyContainer
    val extendedStyle = parents.getOrElse(List[String]()).foldLeft(List[Style]())((styles, s_name) =>
      if (cache.styleHierarchy.contains(s_name.trim)) s_name.trim :: styles else styles)

    // mapping and defaults
    // fill the "mapping and defaults" with extended information or with None values if necessary
    /**
     * relevant is a help-methode, which shortens the actual call to mostRelevant of ClassHierarchy by ensuring the collection-parameter
     * relevant speaks for the hierarchical context -> "A extends B, C" -> C is most relevant
     */
    def relevant[T](f: Style => Option[T]) = ClassHierarchy.mostRelevant(extendedStyle) { f }

    val style = new Style(
      name,
      relevant { _.description },
      relevant { _.transparency },
      relevant { _.background_color },
      relevant { _.line_color },
      relevant { _.line_style },
      relevant { _.line_width },
      relevant { _.font_color },
      relevant { _.font_name },
      relevant { _.font_size },
      relevant { _.font_bold },
      relevant { _.font_italic },
      relevant { _.gradient_orientation },
      relevant { _.selected_highlighting },
      relevant { _.multiselected_highlighting },
      relevant { _.allowed_highlighting },
      relevant { _.unallowed_highlighting },
      extendedStyle
    )

    /* create the instance of the actual new Style */
    val newStyle = processAttributes(style, attributes)

    /* include new style instance in stylehierarchie */
    if (extendedStyle.nonEmpty) {
      extendedStyle.reverse.foreach(elem => cache.styleHierarchy(elem.name, newStyle))
    } else {
      cache.styleHierarchy.newBaseClass(newStyle)
    }
    newStyle
  }

  private def processAttributes(style: Style, attributes: List[(String, String)]) = {
    if (attributes.nonEmpty) {
      attributes.foldLeft(style) { (style, entry) =>
        attributeMapper.get(entry._1) match {
          case Some(func) => func(style, entry._2)
          case None => style
        }
      }
    } else {
      style
    }
  }

  private def ifValid[T](f: => T): Option[T] = {
    var ret: Option[T] = None
    try {
      ret = Some(f)
      ret
    } finally {
      ret
    }
  }

  private def processHighlighting(initialStyle: Style, value: String) = {
    parse(highlighting, value).get.foldLeft(initialStyle) { (style: Style, entry: (String, Color)) =>
      entry._1 match {
        case "allowed" => style.copy(allowed_highlighting = Some(entry._2))
        case "unallowed" => style.copy(unallowed_highlighting = Some(entry._2))
        case "selected" => style.copy(selected_highlighting = Some(entry._2))
        case "multiselected" => style.copy(multiselected_highlighting = Some(entry._2))
        case _ => style
      }
    }
  }

  /**
   * states a problem to standardoutput
   * @param attribute is the attribute, that was not matchable
   * @param name is the name of the class
   * @param className is the type (e.G. Style, Shape ...)
   */
  def messageIgnored(attribute: String, name: String, className: String) = {
    println("Styleparsing: attribute -> " + attribute + " in " + className + " '" + name + "' was ignored")
  }

  /* neccessary for parsing */
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

  private def colorOrGradient: Parser[ColorOrGradient] = (color | transparent | gradient) ^^ {
    case g: Gradient => g
    case Transparent => Transparent
    case color: ColorOrGradient => color
  }

  private def color: Parser[Color] = (rgb | colorConstant) ^^ {
    case (r: Int, g: Int, b: Int) => RGBColor((r, g, b))
    case constant: String => ColorConstant.knownColors(constant)
  }
  private def rgb: Parser[(Int, Int, Int)] = {
    "=" ~ "RGB" ~ "(" ~ "red" ~ "=" ~> (argument_int <~ ",") ~
      ("green" ~ "=" ~> argument_int <~ ",") ~
      ("blue" ~ "=" ~> argument_int <~ ")") ^^ {
        case red ~ blue ~ green => (red, blue, green)
      }
  }
  private def colorConstant: Parser[String] = {
    ("(" + colors.map(c => c + { if (c != colors.last) "|" else "" }).mkString + ")").r ^^ { _.toString }
  }

  private def transparent: Parser[ColorOrGradient] = "transparent".r ^^ (_ => Transparent)

  private def gradient =
    ("=" ~ "gradient" ~> ident <~ "{") ~
      (("description" ~ "=" ~> argument_string)?) ~
      (rep(area) <~ "}") ^^ {
        case id ~ des ~ areas => Gradient(id, des, areas)
      }
  private def area: Parser[GradientColorArea] = {
    ("area" ~ "(" ~ "color" ~ "=" ~> color <~ ",") ~
      ("offset" ~ "=" ~> argument_double <~ ")") ^^ {
        case c ~ d => GradientColorArea(c, d)
      }
  }
  private def colorWithTransparency: Parser[ColorWithTransparency] = {
    (color | transparent) ^^ { _.asInstanceOf[ColorWithTransparency] }
  }
}
