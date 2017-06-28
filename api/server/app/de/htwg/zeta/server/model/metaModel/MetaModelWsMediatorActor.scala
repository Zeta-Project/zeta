package de.htwg.zeta.server.model.metaModel

import akka.actor.Props
import de.htwg.zeta.server.actor.AbstractMediatorActor

class MetaModelWsMediatorActor extends AbstractMediatorActor {
  override protected def childReceive: Receive = {
    case MetaModelWsMediatorActor.Publish(dslType, msg) => publish(dslType, msg)
  }
}

object MetaModelWsMediatorActor {

  private[metaModel] def props(): Props = Props(new MetaModelWsMediatorActor())

  private[metaModel] case class Publish(dslType: String, msg: Any)

}
