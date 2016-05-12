package generator.generators.shape


import java.nio.file._

import generator.model.shapecontainer.ShapeContainerElement
import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics.GeometricModel
import generator.parser.Cache

import scala.collection.mutable.HashMap

/**
  * Created by julian on 19.01.16.
  * generates a complete shape
  */
object ShapeGenerator {


  val JOINTJS_PATH = "jointjs-gen/diagram"
  val JOINTJS_SHAPE_FILENAME = "shape.js"
  val JOINTJS_INSPECTOR_FILENAME = "inspector.js"
  val JOINTJS_CONNECTION_FILENAME = "connectionstyle.js"
  val JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME = "elementAndInlineStyle.js"

  def doGenerate(cache: Cache, location: String) {
    val DEFAULT_SHAPE_LOCATION = location

    val attrs = GeneratorShapeDefinition.attrsInspector

    //---------------------------------------------------------------------------------------
    // Shapes
    val packageName = "Test"
    var jointJSShapeContent = ""

    //Write Head of ShapeFile
    jointJSShapeContent = GeneratorShapeDefinition.head(packageName)

    //Write different Shape Definitions
    for (shapeDefinition <- cache.shapeHierarchy.nodeView.values) {
      jointJSShapeContent += generateJointJSShape(shapeDefinition.data, packageName)
    }

    //Generate ShapeFile
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_SHAPE_FILENAME), jointJSShapeContent.getBytes)

    //---------------------------------------------------------------------------------------
    //ConnectionStyle
    var jointJsConnectionContent = ""

    //Write connection Style file
    jointJsConnectionContent = GeneratorConnectionDefinition.generate(cache.connections.values)

    //Generate Connection Style file
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_CONNECTION_FILENAME), jointJsConnectionContent.getBytes)

    //---------------------------------------------------------------------------------------
    // Inspector

    var jointJSInspectorContent = ""

    //Write Head of Inspector File
    jointJSInspectorContent = GeneratorInspectorDefinition.head

    //Write different Inspector Definitions
    var lastElement = false

    for (shapeDefinition <- cache.shapeHierarchy.nodeView.values) {
      if (shapeDefinition == cache.shapeHierarchy.nodeView.values.last) {
        lastElement = true
      }
      jointJSInspectorContent += generateJointJSInspector(shapeDefinition.data, packageName, lastElement, attrs)
    }

    //Write Footer of Inspector File
    jointJSInspectorContent += GeneratorInspectorDefinition.footer

    //  Generate InspectorFile
    //fsa.generateFile(JOINTJS_INSPECTOR_FILENAME, jointJSInspectorOutputConfName, jointJSInspectorContent)
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_INSPECTOR_FILENAME), jointJSInspectorContent.getBytes)

    //---------------------------------------------------------------------------------------
    // ElementAndInlineStyle

    var jointJSShapeAndInlineStyleContent = ""

    //  //Write Head of Shape Style
    jointJSShapeAndInlineStyleContent = GeneratorShapeAndInlineStyle.shapeStyleHead

    lastElement = false

    //Write Shape Style for different Shape Definitions
    for (shapeDefinition <- cache.shapeHierarchy.nodeView.values) {
      if (shapeDefinition == cache.shapeHierarchy.nodeView.values.last) {
        lastElement = true
      }
      jointJSShapeAndInlineStyleContent += generatorShapeStyle(shapeDefinition.data, packageName, lastElement, attrs)
    }

    //Write Footer of Shape Style
    jointJSShapeAndInlineStyleContent += GeneratorShapeAndInlineStyle.shapeStyleFooter

    //Generate ShapeFile
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME), jointJSShapeAndInlineStyleContent.getBytes)

  }

  def generateJointJSShape(shape: Shape, packageName: String) = {
    GeneratorShapeDefinition.generate(shape, packageName)
  }

  def generatorShapeStyle(shape: Shape, packageName: String, lastElement: Boolean, attrs: HashMap[String, HashMap[GeometricModel, String]]) {
    GeneratorShapeAndInlineStyle.generateShapeStyle(shape, packageName, lastElement, attrs)
  }

  def generateJointJSInspector(shape: ShapeContainerElement, packageName: String, lastElement: Boolean, attrs: HashMap[String, HashMap[GeometricModel, String]]) = {
    GeneratorInspectorDefinition.generate(shape, packageName, lastElement, attrs)
  }

}
