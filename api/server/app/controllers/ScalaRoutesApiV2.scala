package controllers

import java.util.UUID
import javax.inject.Inject

import de.htwg.zeta.server.routing.RouteController
import de.htwg.zeta.server.routing.RouteControllerContainer
import de.htwg.zeta.server.routing.WebController
import de.htwg.zeta.server.routing.WebControllerContainer
import play.api.mvc.Action
import play.api.mvc.AnyContent

// scalastyle:off number.of.methods

/**
 * All routes are managed in this class
 */
class ScalaRoutesApiV2 @Inject()(
    protected val routeCont: RouteControllerContainer,
    protected val webCont: WebControllerContainer
) extends RouteController with WebController {

  def getMetaModelShape(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApiV2.getShape(metaModelId) _)

  def getMetaModelStyle(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApiV2.getStyle(metaModelId) _)

  def getMetaModelDiagram(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApiV2.getDiagram(metaModelId) _)

}
