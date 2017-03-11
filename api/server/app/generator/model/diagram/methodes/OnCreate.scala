package generator.model.diagram.methodes

import models.modelDefinitions.metaModel.elements.MAttribute

/**
 * Created by julian on 08.12.15.
 */
case class OnCreate(
    override val actionBlock: Option[ActionBlock] = None,
    askFor: Option[MAttribute] = None
) extends Methode {
  require(actionBlock.isDefined || askFor.isDefined)
}
