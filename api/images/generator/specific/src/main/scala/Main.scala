import java.util.UUID

import de.htwg.zeta.server.generator.Error
import de.htwg.zeta.server.generator.Result
import de.htwg.zeta.server.generator.Success
import de.htwg.zeta.server.generator.Transformer
import models.entity.Filter
import models.entity.Generator
import models.entity.MetaModelEntity
import models.entity.ModelEntity
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import models.remote.Remote
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import models.entity.File

object Main extends Template[CreateOptions, String] {
  val sep = "\n"
  val sep1 = "\n "
  val sep2 = "\n  "
  val sepMethods = "\n\n"

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @return A Generator
   */
  override def getTransformer(file: File, filter: Filter)(implicit remote: Remote): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @return A Generator
   */
  override def getTransformer(file: File, model: ModelEntity)(implicit remote: Remote): Future[Transformer] = {
    compiledGenerator(file)
  }

  def matchMClassMethod(entity: MClass) = {
    s"""case "${entity.name}" => transform${entity.name}Node(node)"""
  }

  def matchMReferenceMethod(entity: MReference): String = {
    s"""case "${entity.name}" => transform${entity.name}Edge(edge)"""
  }

  def getMClassTypeMethod(entity: MClass): String = {
    s"""
      |def transform${entity.name}Node(node: Node): Node = {
      |  node
      |}
    """.stripMargin
  }

  def getMReferenceTypeMethod(entity: MReference): String = {
    s"""
      |def transform${entity.name}Edge(edge: Edge): Edge = {
      |  edge
      |}
    """.stripMargin
  }

  def methodPrototypes(mClass: Iterable[MClass], mReference: Iterable[MReference]): String = {
    s"""
      |${mClass.map(getMClassTypeMethod).mkString(sepMethods)}
      |
      |${mReference.map(getMReferenceTypeMethod).mkString(sepMethods)}
    """.stripMargin
  }

  def createFileContent(mClassList: Iterable[MClass], mReferenceList: Iterable[MReference]) = {
    s"""
      |class MyTransformer() extends Transformer {
      | def transform(entity: ModelEntity)(implicit entitys: Documents, files: Files, remote: Remote) : Future[Transformer] = {
      |   val transformed = entity.model.elements.values.map { element => element match {
      |     case node: Node => transformNode(node)
      |     case edge: Edge => transformEdge(edge)
      |   }}
      |   Future.successful(this)
      | }
      |
      | def transformNode(node: Node): Node = node.`type`.name match {
      |   ${mClassList.map(matchMClassMethod).mkString(sep2)}
      | }
      |
      | def transformEdge(edge: Edge): Edge = edge.`type`.name match {
      |   ${mReferenceList.map(matchMReferenceMethod).mkString(sep2)}
      | }
      |
      | ${methodPrototypes(mClassList, mReferenceList)}
      |
      | def exit()(implicit entitys: Documents, files: Files, remote: Remote) : Future[Result] = {
      |   val result = Success("The generator finished")
      |   Future.successful(result)
      | }
      |}
      |
    """.stripMargin
  }

  def compiledGenerator(file: File) = {
    val content = s"""
      import scala.concurrent.Future
      import models.modelDefinitions.model.elements.{Node, Edge}
      import models.entity.ModelEntity
      import models.entity.{Repository => Documents}
      import models.file.{Repository => Files}
      import models.remote.Remote
      import generator._

      ${file.content}

      new MyTransformer()
      """
    compile[Transformer](content)
  }

  /**
   * Create assets for the generator
   *
   * @param options   The Options for the creation of the generator
   * @param imageId     The id of the image for the generator
   * @return The result of the generator creation
   */
  override def createTransformer(options: CreateOptions, imageId: UUID)(implicit remote: Remote): Future[Result] = {
    for {
      image <- repository.generatorImages.read(imageId)
      metaModel <- repository.metaModelEntities.read(UUID.fromString(options.metaModelRelease))
      generator <- repository.generators.create(Generator(user, options.name, image.id))
      created <- repository.files.create(file(Settings.generatorFile, metaModel))
    } yield Success()
  }

  def file(name: String, entity: MetaModelEntity): File = {
    val mClassList = entity.metaModel.classes.values
    val mReferenceList = entity.metaModel.references.values
    val content = createFileContent(mClassList, mReferenceList)
    File(UUID.randomUUID, name, content)
  }

  /**
   * Initialize the generator
   *
   * @return A Generator
   */
  override def runGeneratorWithOptions(options: String)(implicit remote: Remote): Future[Result] = {
    Future.successful(Error(s"Call a generator from a generator is not supported in this example"))
  }
}
