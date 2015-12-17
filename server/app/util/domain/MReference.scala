package util.domain

case class MReference(override val name:String, attributes:List[MAttribute], targetDeletionDeletesSource:Boolean = false,
                       sourceDeletionDeletesTarget:Boolean = false, var source:List[MLinkDef] = List(), var target:List[MLinkDef] = List())
  extends MObj with ObjectWithAttributes{

  val mappedAttributes = attributes.map(a => a.name -> a).toMap

  override def attribute(name:String) = mappedAttributes get name
}
