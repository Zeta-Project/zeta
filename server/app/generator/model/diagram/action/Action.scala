package generator.model.diagram.action

class Action (val name:String, val label:String, val classsName:String, val methode:String)

object Action{
  def apply(name:String, label:String, className:String, methode:String) = parse(name, label, className, methode)
  def parse(name:String, label:String, className:String, methode:String) = new Action(name, label, className, methode)
}
