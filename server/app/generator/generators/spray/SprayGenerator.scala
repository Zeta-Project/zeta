package generator.generators.spray

import java.nio.file.{Paths, Files}
import generator.generators.{ProjectPropertiesMock, Resource}

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
  def doGenerate(resource:Resource) {

    if (!resource.getPath.endsWith(ProjectPropertiesMock.SPRAY_FILE_EXTENSION)) {
      println("Spray generator is NOT producing JointJS code for model " + resource.getPath)
      return
    }

    println("Spray generator is producing JointJS code for model " + resource.getPath)
    ProjectPropertiesMock.setModelUri(resource.getURI)
    //TODO super.doGenerate(resource, fsa) what does JvmModelGenerator (extended by SprayGenerator.xtext) do?
    val packageName = resource.getPath.getParent.toString
    StencilGenerator.setPackageName(packageName)

    //val JavaGenFile java = genFileProvider.get()
    //val jointJSSprayOutputConfName = "JointJSSprayOutputConfiguration"
    //val jointJSSprayRootOutputConfName = "JointJSSprayRootOutputConfiguration"

    //java.access = fsa

    //fsa.addJointJSOutputConfiguration(jointJSSprayOutputConfName)
    val diagram = resource.cache.diagrams.head
    Files.write(Paths.get(JOINTJS_STENCIL_FILENAME), StencilGenerator.generate(diagram._2).getBytes)
    Files.write(Paths.get(JOINTJS_VALIDATOR_FILENAME), ValidatorGenerator.generate(diagram._2).getBytes)
    Files.write(Paths.get(JOINTJS_README_FILENAME), ReadmeGenerator.generate(diagram._2).getBytes)
    Files.write(Paths.get(JOINTJS_LINKHELPER_FILENAME), LinkhelperGenerator.generate(diagram._2).getBytes)

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
