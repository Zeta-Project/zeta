package generator.model.diagram.methodes

/**
 * Created by julian on 08.12.15.
 */
case class OnCreate(override val actionBlock:Option[ActionBlock] = None,
                    askFor:Option[String] = None) extends Methode{
  require(actionBlock.isDefined || askFor.isDefined)
}
