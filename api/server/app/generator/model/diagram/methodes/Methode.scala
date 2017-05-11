package generator.model.diagram.methodes

import generator.model.diagram.action.ActionGroup
import generator.model.diagram.action.Action

/**
 * ???
 */
trait Methode {
  val actionBlock: ActionBlock
}

case class ActionBlock(
    action: List[Action],
    actionGroup: List[ActionGroup]
) {
  require(action.nonEmpty || actionGroup.nonEmpty)
}
