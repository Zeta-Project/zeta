package de.htwg.zeta.generator.specific

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.GraphicalDsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.generator.template.Error
import de.htwg.zeta.generator.template.Result
import de.htwg.zeta.generator.template.Settings
import de.htwg.zeta.generator.template.Success
import de.htwg.zeta.generator.template.Template
import de.htwg.zeta.generator.template.Transformer

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
  override def getTransformer(file: File, filter: Filter): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @return A Generator
   */
  override def getTransformer(file: File, model: GraphicalDslInstance): Future[Transformer] = {
    compiledGenerator(file)
  }

  private def matchMClassMethod(entity: MClass): String = {
    s"""case "${entity.name}" => transform${entity.name}Node(node)"""
  }

  private def matchMReferenceMethod(entity: MReference): String = {
    s"""case "${entity.name}" => transform${entity.name}Edge(edge)"""
  }

  private def getMClassTypeMethod(entity: MClass): String = {
    s"""
      |def transform${entity.name}Node(node: Node): Node = {
      |  node
      |}
    """.stripMargin
  }

  private def getMReferenceTypeMethod(entity: MReference): String = {
    s"""
      |def transform${entity.name}Edge(edge: Edge): Edge = {
      |  edge
      |}
    """.stripMargin
  }

  private def methodPrototypes(mClass: Iterable[MClass], mReference: Iterable[MReference]): String = {
    s"""
      |${mClass.map(getMClassTypeMethod).mkString(sepMethods)}
      |
      |${mReference.map(getMReferenceTypeMethod).mkString(sepMethods)}
    """.stripMargin
  }

  private def createFileContent(mClassList: Iterable[MClass], mReferenceList: Iterable[MReference]): String = {
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

  private def compiledGenerator(file: File): Future[Transformer] = {
    val content = s"""
      |import scala.concurrent.Future
      |import de.htwg.zeta.common.models.modelDefinitions.model.elements.{Node, Edge}
      |import de.htwg.zeta.common.models.entity.ModelEntity
      |import de.htwg.zeta.common.models.entity.{Repository => Documents}
      |import de.htwg.zeta.common.models.file.{Repository => Files}
      |import de.htwg.zeta.common.models.remote.Remote
      |import generator._
      |
      |${file.content}
      |
      |new MyTransformer()
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
  override def createTransformer(options: CreateOptions, imageId: UUID): Future[Result] = {
    for {
      image <- generatorImagePersistence.read(imageId)
      metaModel <- metaModelEntityPersistence.read(UUID.fromString(options.metaModelRelease))
      file <- createFile(metaModel)
      _ <- createGenerator(options, image, file)
    } yield Success()
  }

  private def createFile(metaModel: GraphicalDsl): Future[File] = {
    val mClassList = metaModel.concept.classMap.values
    val mReferenceList = metaModel.concept.referenceMap.values
    val content = createFileContent(mClassList, mReferenceList)
    val entity = File(UUID.randomUUID, Settings.generatorFile, content)
    filePersistence.create(entity)
  }

  private def createGenerator(options: CreateOptions, image: GeneratorImage, file: File): Future[Generator] = {
    val entity = Generator(
      id = UUID.randomUUID(),
      name = options.name,
      imageId = image.id,
      files = Map(file.id -> file.name)
    )
    generatorPersistence.create(entity)
  }

  /**
   * Initialize the generator
   *
   * @return A Generator
   */
  override def runGeneratorWithOptions(options: String): Future[Result] = {
    Future.successful(Error(s"Call a generator from a generator is not supported in this example"))
  }
}
