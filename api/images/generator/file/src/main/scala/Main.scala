import generator._
import models.document.{ Repository => Documents, _ }
import models.file.{ File, Repository => Files }
import models.modelDefinitions.model.elements.{ Edge, Node }
import models.remote.Remote

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Promise }

class MyTransformer() extends Transformer {
  def transform(entity: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer] = {
    println("Start example")
    val p = Promise[Transformer]

    val nodes = entity.model.elements.values.filter(_.isInstanceOf[Node]).size
    val edges = entity.model.elements.values.filter(_.isInstanceOf[Edge]).size

    val filename = "example.txt"
    val content =
      s"""
         |Number of nodes : ${nodes}
         |Number of edges : ${edges}
       """.stripMargin

    files.create(entity, File(filename, content)).map { result =>
      println(s"Successfully saved results to '${filename}' for model '${entity.model.name}' (MetaModel '${entity.model.metaModel.name}')")
      p.success(this)
    }.recover {
      case e: Exception => p.failure(e)
    }

    p.future
  }

  def exit()(implicit documents: Documents, files: Files, remote: Remote): Future[Result] = {
    val result = Success("The generator finished")
    Future.successful(result)
  }
}

object Main extends Template[CreateOptions, String] {
  override def createTransformer(options: CreateOptions, image: String)(implicit documents: Documents, files: Files, remote: Remote): Future[Result] = {
    for {
      image <- documents.get[GeneratorImage](image)
      generator <- documents.create[Generator](Generator(user, options.name, image))
      created <- files.create(generator, createFileContent)
    } yield Success()
  }

  def createFileContent(): File = {
    File(Settings.generatorFile, "This is a demo to save the results of a generator. No further configuration is required.")
  }

  def createFile(name: String): File = {
    val content =
      """
        |class MyTransformer() extends Transformer {
        |	def transform(entity: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote) : Future[Transformer] = {
        |   println(s"User : ${entity.id}")
        |   entity.model.elements.values.foreach { element => element match {
        |     case node: Node => println(node.`type`.name)
        |     case edge: Edge => println(edge.`type`.name)
        |   }}
        |		Future.successful(this)
        |	}
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
      import scala.concurrent.ExecutionContext.Implicits.global
      import scala.concurrent.{Future, Promise}
      import models.modelDefinitions.model.elements.{Node, Edge}
      import models.document.ModelEntity
      import models.document.{Repository => Documents}
      import models.file.{File, Repository => Files}
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
  override def getTransformer(file: File, filter: Filter)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer] =
    Future.successful(new MyTransformer())

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @param documents Access to the Documents repository
   * @param files     Access to the Files repository
   * @return A Generator
   */
  override def getTransformer(file: File, model: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer] =
    Future.successful(new MyTransformer())

  /**
   * Initialize the generator
   *
   * @return A Generator
   */
  override def runGeneratorWithOptions(options: String)(implicit documents: Documents, files: Files, remote: Remote): Future[Result] = {
    Future.successful(Error(s"Call a generator from a generator is not supported in this example"))
  }
}