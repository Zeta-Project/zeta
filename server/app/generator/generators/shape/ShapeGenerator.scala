package generator.generators.shape

import java.io.File

/**
 * Created by julian on 19.01.16.
 * generates a complete shape
 */
object ShapeGenerator {
//  val JOINTJS_PATH =  "jointjs-gen/diagram"
//  val JOINTJS_SHAPE_FILENAME = "shape.js"
//  val JOINTJS_INSPECTOR_FILENAME = "inspector.js"
//  val JOINTJS_CONNECTION_FILENAME = "connectionstyle.js"
//  val JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME = "elementAndInlineStyle.js"
//
//  def doGenerate(resource:File) = {
//    if(resource.getName.endsWith(ProjectProperties.SHAPES_FILE_EXTENSION)) {
//    println("Shape generator is NOT producing JointJS code for model " + resource.getPath) //TODO replace with call to logger
//  }
//
//  println("Shape generator is producing JointJS code for model " + resource.getURI)
//  ProjectProperties.setModelUri(resource.getPath)
//
//  super.doGenerate(resource, fsa);
//
//  val JavaGenFile java = genFileProvider.get()
//  java.access = fsa
//
//  val attrs = generatorShapeDefinition.getAttrsInspector();
//
//  //---------------------------------------------------------------------------------------
//  // Shapes
//
//  val jointJSShapeOutputConfName = "JointJSshapeOutputConfiguration"
//  fsa.addJointJSOutputConfiguration(jointJSShapeOutputConfName)
//
//  val packageName = resource.URI.lastSegment.replace(".shape", "")
//  var jointJSShapeContent = ""
//
//  //Write Head of ShapeFile
//  jointJSShapeContent = generatorShapeDefinition.head(packageName).toString()
//
//  //Write different Shape Definitions
//  for(shapeDefinition : resource.allContents.toIterable.filter(typeof(ShapeDefinition))) {
//    jointJSShapeContent = jointJSShapeContent + java.generateJointJSShape(shapeDefinition, packageName).toString()
//  }
//
//  //Generate ShapeFile
//  fsa.generateFile(JOINTJS_SHAPE_FILENAME, jointJSShapeOutputConfName, jointJSShapeContent)
//
//  //---------------------------------------------------------------------------------------
//  //ConnectionStyle
//
//  val jointJSConnectionStyleOutputConfName = "JointJSConnectionStyleOutputConfiguration"
//  fsa.addJointJSOutputConfiguration(jointJSConnectionStyleOutputConfName)
//
//  var jointJsConnectionContent = ""
//
//  //Write connection Style file
//  jointJsConnectionContent = generatorConnectionDefinition.generate(resource.allContents.toIterable.filter(typeof(ConnectionDefinition))).toString
//
//  //Generate Connection Style file
//  fsa.generateFile(JOINTJS_CONNECTION_FILENAME, jointJSShapeOutputConfName, jointJsConnectionContent);
//
//  //---------------------------------------------------------------------------------------
//  // Inspector
//
//  val jointJSInspectorOutputConfName = "JointJSInspectorOutputConfiguration"
//  fsa.addJointJSOutputConfiguration(jointJSInspectorOutputConfName)
//
//  var jointJSInspectorContent = ""
//
//  //Write Head of Inspector File
//  jointJSInspectorContent = generatorInspectorDefinition.head().toString()
//
//  //Write different Inspector Definitions
//  var lastElement = false;
//
//  /* Comment Markus Gerhart
//   for(shapeContainerElement : resource.allContents.toIterable.filter(typeof(ShapeContainerElement))) {
//     if(shapeContainerElement == resource.allContents.toIterable.filter(typeof(ShapeContainerElement)).last){
//       lastElement = true;
//     }
//
//       jointJSInspectorContent = jointJSInspectorContent + java.generateJointJSInspector(shapeContainerElement, packageName, lastElement).toString()
//   }
//   */
//  for(shapeDefinition : resource.allContents.toIterable.filter(typeof(ShapeDefinition))) {
//    if(shapeDefinition == resource.allContents.toIterable.filter(typeof(ShapeDefinition)).last){
//      lastElement = true;
//    }
//
//    jointJSInspectorContent = jointJSInspectorContent + java.generateJointJSInspector(shapeDefinition, packageName, lastElement, attrs).toString()
//  }
//
//  //Write Footer of Inspector File
//  jointJSInspectorContent = jointJSInspectorContent + generatorInspectorDefinition.footer().toString()
//
//  //Generate InspectorFile
//  fsa.generateFile(JOINTJS_INSPECTOR_FILENAME, jointJSInspectorOutputConfName, jointJSInspectorContent)
//
//  //---------------------------------------------------------------------------------------
//  // ElementAndInlineStyle
//
//  val jointJSShapeAndInlineStyleOutputConfName = "JointJSElementAndInlineStyleOutputConfiguration"
//  fsa.addJointJSOutputConfiguration(jointJSShapeAndInlineStyleOutputConfName)
//
//  var jointJSShapeAndInlineStyleContent = ""
//
//  //Write Head of Shape Style
//  jointJSShapeAndInlineStyleContent = generatorShapeAndInlineStyle.shapeStyleHead().toString()
//
//  lastElement = false;
//
//  //Write Shape Style for different Shape Definitions
//  for(shapeDefinition : resource.allContents.toIterable.filter(typeof(ShapeDefinition))) {
//    if(shapeDefinition == resource.allContents.toIterable.filter(typeof(ShapeDefinition)).last){
//      lastElement = true;
//    }
//    jointJSShapeAndInlineStyleContent = jointJSShapeAndInlineStyleContent + java.generatorShapeStyle(shapeDefinition, packageName, lastElement, attrs).toString()
//  }
//
  //Write Footer of Shape Style
//  jointJSShapeAndInlineStyleContent = jointJSShapeAndInlineStyleContent + generatorShapeAndInlineStyle.shapeStyleFooter().toString()
//
//  //Generate ShapeFile
//  fsa.generateFile(JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME, jointJSShapeAndInlineStyleOutputConfName, jointJSShapeAndInlineStyleContent)
//
//}
//
//def generateJointJSShape(JavaGenFile java, ShapeDefinition shape, String packageName) {
//generatorShapeDefinition.generate(shape, packageName)
//}
//
//def generatorShapeStyle(JavaGenFile java, ShapeDefinition shape, String packageName, Boolean lastElement, HashMap<String, HashMap<Shape, String>> attrs) {
//generatorShapeAndInlineStyle.generateShapeStyle(shape, packageName, lastElement, attrs)
//}
//
//def generateJointJSInspector(JavaGenFile java, ShapeContainerElement shape, String packageName, Boolean lastElement, HashMap<String, HashMap<Shape, String>> attrs) {
//generatorInspectorDefinition.generate(shape, packageName, lastElement, attrs)
//}
//
//
//def private addJointJSOutputConfiguration(IFileSystemAccess fsa, String svgOutputConfName) {
//fsa.addImageOutputConfiguration(svgOutputConfName, JOINTJS_PATH)
//}
//
//def private addImageOutputConfiguration(IFileSystemAccess fsa, String outputConfName, String path) {
//if(fsa instanceof AbstractFileSystemAccess) {
//val aFsa = fsa as AbstractFileSystemAccess
//if(!aFsa.outputConfigurations.containsKey(outputConfName)) {
//val outputConfigurations = <String, OutputConfiguration> newHashMap
//outputConfigurations.putAll(aFsa.outputConfigurations)
//val imageOutputConfiguration = new OutputConfiguration(outputConfName)
//imageOutputConfiguration.outputDirectory = path
//imageOutputConfiguration.createOutputDirectory = true
//imageOutputConfiguration.overrideExistingResources = true
//imageOutputConfiguration.setDerivedProperty = true
//outputConfigurations.put(outputConfName, imageOutputConfiguration)
//aFsa.setOutputConfigurations(outputConfigurations)
//}
//}
//}

}
