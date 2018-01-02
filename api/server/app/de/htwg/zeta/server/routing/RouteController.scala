package de.htwg.zeta.server.routing

import javax.inject.Inject

import de.htwg.zeta.server.routing.authentication.UnAuthenticatedWebSocket
import de.htwg.zeta.server.routing.authentication.UnAuthenticatedAction
import de.htwg.zeta.server.routing.authentication.AuthenticatedWebSocket
import de.htwg.zeta.server.routing.authentication.AuthenticatedAction
import de.htwg.zeta.server.routing.authentication.BasicAction
import de.htwg.zeta.server.routing.authentication.BasicWebSocket
import de.htwg.zeta.server.routing.authentication.AbstractWebSocket
import de.htwg.zeta.server.routing.authentication.AbstractAction
import play.api.mvc.Controller

/**
 */
trait RouteController extends Controller {

  protected val routeCont: RouteControllerContainer

  protected object AuthenticatedGet extends AuthenticatedAction(routeCont.abstractActionDependencies)

  protected object AuthenticatedPost extends AuthenticatedAction(routeCont.abstractActionDependencies)

  protected object AuthenticatedPut extends AuthenticatedAction(routeCont.abstractActionDependencies)

  protected object AuthenticatedDelete extends AuthenticatedAction(routeCont.abstractActionDependencies)

  protected object AuthenticatedSocket extends AuthenticatedWebSocket(routeCont.abstractWebSocketDependencies)


  protected object UnAuthenticatedGet extends UnAuthenticatedAction(routeCont.abstractActionDependencies)

  protected object UnAuthenticatedPost extends UnAuthenticatedAction(routeCont.abstractActionDependencies)

  protected object UnAuthenticatedSocket extends UnAuthenticatedWebSocket(routeCont.abstractWebSocketDependencies)


  protected object BasicGet extends BasicAction(routeCont.abstractActionDependencies)

  protected object BasicPost extends BasicAction(routeCont.abstractActionDependencies)

  protected object BasicSocket extends BasicWebSocket(routeCont.abstractWebSocketDependencies)

}

class RouteControllerContainer @Inject() private(
    val abstractWebSocketDependencies: AbstractWebSocket.Dependencies,
    val abstractActionDependencies: AbstractAction.Dependencies)
