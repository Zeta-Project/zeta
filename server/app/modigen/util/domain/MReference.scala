package modigen.util.domain

case class MReference(override val name:String, val attributes:List[MAttribute]) extends MObj with ObjectWithAttributes{
  var targetDeletionDeletesSource = false
  var sourceDeletionDeletesTarget = false
  var source:List[MLinkDef] = List()
  var target:List[MLinkDef] = List()

  val mappedAttributes = attributes.map(a => a.name -> a).toMap

  override def attribute(name:String) = mappedAttributes get name
}
