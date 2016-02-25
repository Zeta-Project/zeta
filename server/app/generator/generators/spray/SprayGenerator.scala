package generator.generators.spray

import java.nio.file.{Paths, Files}
import generator.model.diagram.Diagram
import generator.parser.Cache

/**
 * Created by julian on 07.02.16.
 */
object SprayGenerator {
  private val JOINTJS_STENCIL_FILENAME		= "stencil.js"
  private val JOINTJS_VALIDATOR_FILENAME	= "validator.js"
  private val JOINTJS_PATH 					= "jointjs-gen/diagram"
  private val JOINTJS_PATH_ROOT				= "jointjs-gen"
  private val JOINTJS_ZIP_ANT_FILENAME		= "buildZip.xml"
  private val JOINTJS_README_FILENAME		= "Readme.xml"
  private val JOINTJS_LINKHELPER_FILENAME	= "linkhelper.js"

  /**
   * This method is a long sequence of calling all templates for the code generation
   */
  def doGenerate(diagram: Diagram, location:String) {
    val DEFAULT_DIAGRAM_LOCATION = "/"+location
    val packageName = DEFAULT_DIAGRAM_LOCATION
    StencilGenerator.setPackageName(packageName)

    //val JavaGenFile java = genFileProvider.get()
    //val jointJSSprayOutputConfName = "JointJSSprayOutputConfiguration"
    //val jointJSSprayRootOutputConfName = "JointJSSprayRootOutputConfiguration"

    //java.access = fsa

    //fsa.addJointJSOutputConfiguration(jointJSSprayOutputConfName)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+JOINTJS_STENCIL_FILENAME), StencilGenerator.generate(diagram).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+JOINTJS_VALIDATOR_FILENAME), ValidatorGenerator.generate(diagram).getBytes)
    //Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+JOINTJS_README_FILENAME), ReadmeGenerator.generate(diagram).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+JOINTJS_LINKHELPER_FILENAME), LinkhelperGenerator.generate(diagram).getBytes)

    //fsa.addJointJSOutputRootConfiguration(jointJSSprayRootOutputConfName)
    Files.write(Paths.get(JOINTJS_ZIP_ANT_FILENAME), BuildZipGenerator.generate.getBytes)
  }

/*Following methods are no longer needed i guess.


  def addJointJSOutputRootConfiguration(IFileSystemAccess fsa, String name){
    fsa.addOutputConfiguration(name, JOINTJS_PATH_ROOT)
  }

  def addJointJSOutputConfiguration(IFileSystemAccess fsa, String name){
    fsa.addOutputConfiguration(name, JOINTJS_PATH)
  }

  def addOutputConfiguration(IFileSystemAccess fsa, String name, String path){
    if(fsa instanceof AbstractFileSystemAccess) {
      val aFsa = fsa as AbstractFileSystemAccess
      if(!aFsa.outputConfigurations.containsKey(name)) {
        val outputConfigurations = <String, OutputConfiguration> newHashMap
          outputConfigurations.putAll(aFsa.outputConfigurations)
        val imageOutputConfiguration = new OutputConfiguration(name)
        imageOutputConfiguration.outputDirectory = path
        imageOutputConfiguration.createOutputDirectory = true
        imageOutputConfiguration.overrideExistingResources = true
        imageOutputConfiguration.setDerivedProperty = true
        outputConfigurations.put(name, imageOutputConfiguration)
        aFsa.setOutputConfigurations(outputConfigurations)
      }
    }
  }
}*/

}
