package modigen.util.domain

case class MClass(override val name:String, val superTypes:List[MClass], val attributes:List[MAttribute]) extends MObj with ObjectWithAttributes{
  var isAbstract = false
  var inputs:List[MLinkDef] = List()
  var outputs:List[MLinkDef] = List()
  val mappedAttributes = allAttributes.map(a => a.name -> a).toMap

  override def attribute(name:String) = mappedAttributes get name

  def allAttributes:List[MAttribute] = fullAttributeSet.toList
  def fullAttributeSet:Set[MAttribute] = superTypeAttributes.foldLeft(attributeSet)((attrs, next) => attrs ++ next)
  def superTypeAttributes:List[Set[MAttribute]] = for(st <- superTypes) yield st.fullAttributeSet
  def attributeSet:Set[MAttribute] = attributes.toSet


}
