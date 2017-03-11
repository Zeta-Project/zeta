package generator.model.shapecontainer.connection.shapeconnections

import generator.model.shapecontainer.shape.geometrics.layouts.RoundedRectangleLayout
import generator.model.style.Style

/**
 * Created by julian on 20.10.15.
 */
class CDRoundedRectangle(override val style:Option[Style]=None,
                         override val curve_width:Int,
                         override val curve_height:Int,
                         override val position:Option[(Int, Int)]=None,
                         override val size_width:Int,
                         override val size_height:Int
                          ) extends ShapeConnection with RoundedRectangleLayout
