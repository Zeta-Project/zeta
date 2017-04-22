import generator.Error
import generator.Result
import generator.Success
import generator.Transformer

import models.document.Filter
import models.document.Generator
import models.document.GeneratorImage
import models.document.ModelEntity
import models.document.{Repository => Documents}
import models.file.File
import models.file.{Repository => Files}
import models.remote.Remote

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends Template[CreateOptions, String] {
  override def createTransformer(options: CreateOptions, image: String)(implicit documents: Documents, files: Files, remote: Remote): Future[Result] = {
    for {
      image <- documents.get[GeneratorImage](image)
      generator <- documents.create[Generator](Generator(user, options.name, image))
      created <- files.create(generator, createFile(Settings.generatorFile))
    } yield Success()
  }

  def createFile(name: String): File = {
    val content =
      """
        |class MyTransformer() extends Transformer {
        | def transform(entity: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote) : Future[Transformer] = {
        |   println(s"User : ${entity.id}")
        |   entity.model.elements.values.foreach { element => element match {
        |     case node: Node => println(node.`type`.name)
        |     case edge: Edge => println(edge.`type`.name)
        |   }}
        |   Future.successful(this)
        | }
        |
        | def exit()(implicit documents: Documents, files: Files, remote: Remote) : Future[Result] = {
        |   val result = Success("The generator finished")
        |   Future.successful(result)
        | }
        |}
        |
      """.stripMargin
    File(name, content)
  }

  def compiledGenerator(file: File) = {
    val content = s"""
      import scala.concurrent.Future
      import models.modelDefinitions.model.elements.{Node, Edge}
      import models.document.ModelEntity
      import models.document.{Repository => Documents}
      import models.file.{Repository => Files}
      import models.remote.Remote
      import generator._

      ${file.content}

      new MyTransformer()
      """
    compile[Transformer](content)
  }

  /**
   * Initialize the generator
   *
   * @param file The file which was loaded for the generator
   * @param documents Access to the Documents repository
   * @param files Access to the Files repository
   * @return A Generator
   */
  override def getTransformer(file: File, filter: Filter)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @param documents Access to the Documents repository
   * @param files     Access to the Files repository
   * @return A Generator
   */
  override def getTransformer(file: File, model: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Initialize the generator
   *
   * @return A Generator
   */
  override def runGeneratorWithOptions(options: String)(implicit documents: Documents, files: Files, remote: Remote): Future[Result] = {
    Future.successful(Error(s"Call a generator from a generator is not supported in this example"))
  }
}
