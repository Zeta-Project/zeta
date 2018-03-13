package de.htwg.zeta.server.controller.codeEditor

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.general.GdslProjectRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result


class CodeEditorController @Inject()(
    metaModelEntityRepo: AccessRestrictedGdslProjectRepository,
    fullAccessMetaModelEntityRepo: GdslProjectRepository
) extends Controller {

  def codeEditor(metaModelId: UUID, dslType: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.metamodel.MetaModelCodeEditor(Some(request.identity.user), metaModelId, dslType))
  }

  def methodClassCodeEditor(metaModelId: UUID, methodName: String, className: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId).map { metaModelEntity =>
      val code = metaModelEntity.concept.classMap(className).methodMap(methodName).code
      Ok(views.html.methodCodeEditor.MethodCodeEditor(request.identity.user, metaModelId, methodName, "class", code, className))
    }
  }

  def methodReferenceCodeEditor(metaModelId: UUID, methodName: String, referenceName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId).map { metaModelEntity =>
      val code = metaModelEntity.concept.referenceMap(referenceName).methodMap(methodName).code
      Ok(views.html.methodCodeEditor.MethodCodeEditor(request.identity.user, metaModelId, methodName, "reference", code, referenceName))
    }
  }

  def methodMainCodeEditor(metaModelId: UUID, methodName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId).map { metaModelEntity =>
      val code = metaModelEntity.concept.methodMap(methodName).code
      Ok(views.html.methodCodeEditor.MethodCodeEditor(request.identity.user, metaModelId, methodName, "common", code, ""))
    }
  }

}
