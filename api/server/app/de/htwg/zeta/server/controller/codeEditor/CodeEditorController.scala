package de.htwg.zeta.server.controller.codeEditor

import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorRef
import akka.actor.Props
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.model.codeEditor.CodeDocWsActor
import de.htwg.zeta.server.model.codeEditor.CodeDocManagerContainer
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result
import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.controller.codeEditor.CodeEditorController.ClassMethodType
import de.htwg.zeta.server.controller.codeEditor.CodeEditorController.MainMethodType
import de.htwg.zeta.server.controller.codeEditor.CodeEditorController.MethodEntityType
import de.htwg.zeta.server.controller.codeEditor.CodeEditorController.ReferenceMethodType
import de.htwg.zeta.server.controller.restApi.modelUiFormat.EdgeFormat
import de.htwg.zeta.server.controller.restApi.modelUiFormat.ModelEntityFormat
import de.htwg.zeta.server.controller.restApi.modelUiFormat.ModelFormat
import de.htwg.zeta.server.controller.restApi.modelUiFormat.ModelUiFormat
import de.htwg.zeta.server.controller.restApi.modelUiFormat.NodeFormat
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

import de.htwg.zeta.persistence.Persistence
import play.api.libs.json.JsResult
import play.api.libs.json.Json
import play.api.libs.json.Reads


class CodeEditorController @Inject()(codeDocManager: CodeDocManagerContainer) extends Controller {

  def codeEditor(metaModelId: UUID, dslType: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.metamodel.MetaModelCodeEditor(Some(request.identity), metaModelId, dslType))
  }


  def codeSocket(metaModelId: UUID, dslType: String)(securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): Props = {
    CodeDocWsActor.props(out, codeDocManager.manager, metaModelId, dslType)
  }

  def methodClassCodeEditor(metaModelId: UUID, methodName: String, className: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    Persistence.restrictedAccessRepository(request.identity.id).metaModelEntity.read(metaModelId).map{ metaModelEntity =>
      val code = metaModelEntity.metaModel.classMap(className).methodMap(methodName).code
      Ok(views.html.methodCodeEditor.MethodCodeEditor(request.identity, metaModelId, methodName, "class", code, className))
    }
  }

  def methodReferenceCodeEditor(metaModelId: UUID, methodName: String, referenceName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.methodCodeEditor.MethodCodeEditor(request.identity, metaModelId, methodName, "reference", "", referenceName))
  }

  def methodMainCodeEditor(metaModelId: UUID, methodName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.methodCodeEditor.MethodCodeEditor(request.identity, metaModelId, methodName, "main", "", ""))
  }

  private def getExistingCode(metaModelId: UUID, methodName: String, entityType: MethodEntityType)(request: SecuredRequest[ZetaEnv, AnyContent]): String = {
    val repo = restrictedAccessRepository(request.identity.id)
    for {
      metaModelEntity <- repo.metaModelEntity.read(metaModelId)
    } yield {
      println(metaModelEntity)
    }
    ""
  }
}

object CodeEditorController {

  trait MethodEntityType

  case class ReferenceMethodType(entityType: String = "reference", entityName: String) extends MethodEntityType
  case class ClassMethodType(entityType: String = "class", entityName: String) extends MethodEntityType
  case class MainMethodType(entityType: String = "main") extends MethodEntityType
}

