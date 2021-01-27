package de.htwg.zeta.server.controller.codeEditor

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.format.project.MethodFormat
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.general.GdslProjectRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Result


class CodeEditorController @Inject()(
    methodFormat: MethodFormat,
    metaModelEntityRepo: AccessRestrictedGdslProjectRepository,
    fullAccessMetaModelEntityRepo: GdslProjectRepository,
    implicit val ec: ExecutionContext
) extends InjectedController {

  def methodClassCodeEditorContent(metaModelId: UUID, methodName: String, className: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId)
      .map { metaModelEntity =>
        metaModelEntity.concept.classMap(className).methodMap(methodName)
      }
      .map { methods =>
        Ok(methodFormat.writes(methods))
      }
  }

  def methodReferenceCodeEditorContent(metaModelId: UUID, methodName: String, referenceName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId)
      .map { metaModelEntity =>
        metaModelEntity.concept.referenceMap(referenceName).methodMap(methodName)
      }
      .map { methods =>
        Ok(methodFormat.writes(methods))
      }
  }

  def methodMainCodeEditorContent(metaModelId: UUID, methodName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId)
      .map { metaModelEntity => metaModelEntity.concept.methodMap(methodName) }
      .map { methods =>
        Ok(methodFormat.writes(methods))
      }
  }
}
