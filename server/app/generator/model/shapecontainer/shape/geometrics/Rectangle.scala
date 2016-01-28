package generator.model.shapecontainer.shape.geometrics

import generator.model.shapecontainer.shape.{CompartmentParser, Compartment}
import generator.model.shapecontainer.shape.geometrics.layouts.{CommonLayoutParser, RectangleEllipseLayout, CommonLayout}
import generator.model.style.Style
import generator.util.{Cache, GeoModel}

/**
 * Created by julian on 19.10.15.
 * representation of  Rectangle
 */
class Rectangle(parent:Option[GeometricModel] = None,
                commonLayout: CommonLayout,
                val compartment:Option[Compartment],
                parentOf:List[GeometricModel] = List[GeometricModel]()
                 ) extends GeometricModel(parent) with RectangleEllipseLayout with Wrapper{

  override val style:Option[Style] = commonLayout.style
  override val position:Option[(Int, Int)] = commonLayout.position
  override val size_width: Int = commonLayout.size_width
  override val size_height: Int = commonLayout.size_height
  override var children:List[GeometricModel] = parentOf
}

object Rectangle{
  /**
   * parses a GeoModel into an actual GeometricModel, in this case a Rectangle
 *
   * @param geoModel is the sketch to parse into a GeometricModel
   * @param parent is the parent instance that wraps the new GeometricModel*/
  def apply(geoModel:GeoModel, parent:Option[GeometricModel] = None, parentStyle:Option[Style], diagram:Cache)= parse(geoModel, parent, parentStyle, diagram)
  def parse(geoModel:GeoModel, parent:Option[GeometricModel] = None, parentStyle:Option[Style], diagram:Cache): Option[Rectangle] = {
    /*mapping*/
    val commonLayout:Option[CommonLayout] = CommonLayoutParser.parse(geoModel, parentStyle, diagram)
    val compartmentInfo:Option[Compartment] = CompartmentParser.parse(geoModel.attributes)

    if(commonLayout.isEmpty)
      return None

    val ret:Rectangle = new Rectangle(parent, commonLayout.get, compartmentInfo, List())
    ret.children = for(i <- geoModel.children)yield{i.parse(Some(ret), ret.style).get}
    Some(ret)
  }
}
