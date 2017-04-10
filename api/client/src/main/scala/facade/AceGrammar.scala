package facade

import scala.scalajs.js

package object AceGrammar extends js.GlobalScope {
  val AceGrammar: AceGrammarTrait = js.native
}

trait AceGrammarTrait extends js.Object {
  def getMode(grammar: js.Any): js.Any = js.native
}
