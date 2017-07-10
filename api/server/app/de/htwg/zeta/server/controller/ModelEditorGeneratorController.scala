package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.{Diagram => DslDiagram}
import de.htwg.zeta.common.models.modelDefinitions.metaModel.{Style => DslStyle}
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Shape
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.server.generator.generators.diagram.DiagramGenerator
import de.htwg.zeta.server.generator.generators.shape.ShapeGenerator
import de.htwg.zeta.server.generator.generators.style.StyleGenerator
import de.htwg.zeta.server.generator.model.diagram.Diagram
import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.server.generator.parser.SprayParser
import de.htwg.zeta.server.model.result.Failure
import de.htwg.zeta.server.model.result.Success
import de.htwg.zeta.server.model.result.Unreliable
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

class ModelEditorGeneratorController @Inject()(silhouette: Silhouette[ZetaEnv]) extends Controller {

  def generate(metaModelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    Persistence.restrictedAccessRepository(request.identity.id).metaModelEntity.read(metaModelId)
      .flatMap(createGenerators(_, request.identity.id).map {
        case Success(_) => Ok("Generation successful")
        case Failure(error) => BadRequest(error)
      })
      .recover {
        case _: Exception => NotFound(s"MetaModel with id: ${metaModelId.toString} was not found")
      }
  }

  private def createGenerators(metaModel: MetaModelEntity, userId: UUID): Future[Unreliable[List[File]]] = {
    val hierarchyContainer = Cache()
    parseMetaModel(metaModel, hierarchyContainer) match {
      case Success(dia) =>
        createAndSaveGeneratorFiles(metaModel, dia, hierarchyContainer, userId)
      case f @ Failure(_) => Future.successful(f)
    }
  }

  private def parseMetaModel(metaModel: MetaModelEntity, hierarchyContainer: Cache): Unreliable[Diagram] = {
    val parser = new SprayParser(hierarchyContainer, metaModel)

    def tryParse[E, R](get: Dsl => Option[E], parse: E => List[R], name: String): Unreliable[List[R]] = {
      get(metaModel.dsl) match {
        case None => Failure(s"$name not available")
        case Some(e) => Unreliable(() => parse(e), s"$name failed parsing")
      }
    }

    tryParse[DslStyle, Style](_.style, (s: DslStyle) => parser.parseStyle(s.code), "Style")
      .flatMap(_ => tryParse[Shape, AnyRef](_.shape, s => parser.parseShape(s.code), "Shape"))
      .flatMap(_ => tryParse[DslDiagram, Option[Diagram]](_.diagram, s => parser.parseDiagram(s.code), ModelEditorGeneratorController.diagramName))
      .flatMap {
        case Some(dia) :: _ => Success(dia)
        case _ => Failure(s"No ${ModelEditorGeneratorController.diagramName} available")
      }
  }

  private def createAndSaveGeneratorFiles(metaModel: MetaModelEntity, diagram: Diagram, hierarchyContainer: Cache, userId: UUID):
  Future[Unreliable[List[File]]] = {
    val repo = Persistence.restrictedAccessRepository(userId).file

    val allGen: Unreliable[(List[File], List[File])] = createGeneratorFiles(diagram, hierarchyContainer, metaModel.id).flatMap(gen => {
      createVrGeneratorFiles(diagram, hierarchyContainer).map(vrGen => {
        (gen, vrGen)
      })
    })

    allGen match {
      case Success((gen: List[File], vrGen: List[File])) =>
        Future.sequence((gen ++ vrGen).map(repo.createOrUpdate)).map(_ =>
          Success(gen ::: vrGen)
        )
      case f @ Failure(_) => Future.successful(f)
    }
  }

  private def createGeneratorFiles(diagram: Diagram, hierarchyContainer: Cache, metaModelId: UUID): Unreliable[List[File]] = {
    val styles = hierarchyContainer.styleHierarchy.nodeView.values.map(_.data).toList
    val generators: List[() => Unreliable[List[File]]] = List(
      () => StyleGenerator.doGenerateResult(styles, metaModelId).map(List(_)),
      () => ShapeGenerator.doGenerateResult(hierarchyContainer, diagram.nodes, metaModelId),
      () => DiagramGenerator.doGenerateResult(diagram, metaModelId)
    )

    generate(generators)
  }

  private def createVrGeneratorFiles(diagram: Diagram, hierarchyContainer: Cache): Unreliable[List[File]] = {
    val generators: List[() => Unreliable[List[File]]] = List(
      // Generate files for the VR - Editor
      // FIXME: VR generators are not working. If you want to enable them again, check the commit that introduced this message
      // Revision number: d60fbde380816a3a593a1bfdb4cdf72561977384
    )

    generate(generators)
  }


  @tailrec
  private def generate(generators: List[() => Unreliable[List[File]]], carry: List[File] = Nil): Unreliable[List[File]] = {
    generators match {
      case Nil => Success(carry)
      case head :: tail => head() match {
        case f: Failure => f
        case Success(list) => generate(tail, carry ::: list)
      }
    }
  }
}

object ModelEditorGeneratorController {

  private[ModelEditorGeneratorController] val diagramName = "Diagram"

}
