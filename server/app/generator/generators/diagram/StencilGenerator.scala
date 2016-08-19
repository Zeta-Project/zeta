package generator.generators.diagram

import generator.model.diagram.Diagram
import generator.model.diagram.node.Node
import generator.model.shapecontainer.shape.Shape

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by julian on 07.02.16.
 */
object StencilGenerator {
  var packageName = ""

  def generate( diagram:Diagram)=
    s"""
    $generateHeader
    ${generateStencilGroups(diagram)}
    ${generateShapes(diagram)}
    ${generateGroupsToStencilMapping(getNodeToPaletteMapping(diagram))}
    ${generateDocumentReadyFunction(diagram)}
    """

  def generateHeader =
    """
    /*
    * This is a generated stencil file for JointJS
    */
    """


  def generateStencilGroups(diagram:Diagram) = {
    var i = 1
    val groupSet = getNodeToPaletteMapping(diagram).keySet
    var groups = List[String]()
    for(groupName <- groupSet) {
      groups ::= getVarName(groupName) + s""": {index: $i, label: '$groupName' }"""
      i+=1
    }
    "Stencil.groups = {"+groups.mkString(",")+"};"
  }


  def generateShapes( diagram:Diagram)={

    {for (node <- diagram.nodes) yield {s"""
        var ${getVarName(node.name)} = new joint.shapes.$packageName.${getClassName(getShapeName(node))}({
      ${if (node.onCreate.isDefined && node.onCreate.get.askFor.isDefined) {
          s"""mcoreAttributes: [
                 {
                    mcore: '${node.onCreate.get.askFor.get.name}',
                    cellPath: ['attrs', '.label', 'text']
                  }
                ],"""
        } else {
          ""
        }
      }
      nodeName: '${node.name}',
      mClass: '${node.mcoreElement.name}',
      mClassAttributeInfo: [${node.mcoreElement.attributes.map(attr =>
        s"""
           { name: '${attr.name}', type: '${attr.`type`}'}
         """
        ).mkString(",")}]
    });
    """
      }}.mkString
  }


  def generateGroupsToStencilMapping(mapping:mutable.HashMap[String,ListBuffer[Node]])={
    s"""
    Stencil.shapes = {
      ${{for(((key, value), i) <- mapping.zipWithIndex) yield
      s"""${generateShapesToGroupMapping(key, value, i == mapping.size)}
       """}.mkString(",")}
    };
    """
  }

  def generateShapesToGroupMapping( group:String, nodes:ListBuffer[Node] , isLast: Boolean) = {
    s"""
    ${getVarName(group)}: [
      ${{for(node <- nodes) yield
        s"""${getVarName(node.name) + {if(node != nodes.last)","else ""}}
         """}.mkString}
      ]
    """
  }

  def generateDocumentReadyFunction( diagram:Diagram) ={
    """
    $(document).ready(function() {"""+s"""
      ${{for(node <- diagram.nodes) yield
      s"""
      ${getVarName(node.name)}.attr(getShapeStyle("${getClassName(getShapeName(node))}"));

      ${{for((key, value) <- node.shape.get.vals) yield
      s"""${getVarName(node.name)}.attr({'.${value.id}':{text: '${key}'}});"""
      }.mkString}"""}.mkString}
      ${if(diagram.style isDefined) {
      s"""
          var style = document.createElement('style');
          style.id = 'highlighting-style';
          style.type = 'text/css';
          style.innerHTML = getDiagramHighlighting("${diagram.style.get.name}");
          document.getElementsByTagName('head')[0].appendChild(style);"""
        } else ""
      }
    });
    """
  }

  def setPackageName(packageName:String) {this.packageName = packageName}


  private def getNodeToPaletteMapping(diagram:Diagram):mutable.HashMap [String,ListBuffer[Node]] = {
    var mapping = new mutable.HashMap [String,ListBuffer[Node]]
    for(node <- diagram.nodes){
      val paletteName = node.palette.getOrElse("")
      if(mapping.contains(paletteName)){
        mapping(paletteName) += node
      }else{
        mapping += (paletteName -> (ListBuffer[Node]() += node))
      }
    }
   mapping
  }

  private def getVarName( name:String)= {
    val ret = name.replaceAll("\\W", "")
    ret.substring(0, 1).toLowerCase + ret.substring(1)
  }

  private def getClassName( name:String)= name.replaceAll("\\W", "")

  private def getShapeName( node:Node)={
    val diaShape = node.shape
    if(diaShape isDefined){
      diaShape.get.referencedShape.name
    }else{
      throw new NoSuchElementException("No Shape defined for node "+node.name)
    }
  }
}

