package de.htwg.zeta.server.util.domain

case class MClass(override val name: String, superTypes: List[MClass], attributes: List[MAttribute], isAbstract: Boolean = false,
    var inputs: List[MLinkDef] = List(), var outputs: List[MLinkDef] = List()) extends MObj with ObjectWithAttributes {

  val mappedAttributes = allAttributes.map(a => a.name -> a).toMap

  override def attribute(name: String) = mappedAttributes get name

  def allAttributes: List[MAttribute] = fullAttributeSet.toList
  def fullAttributeSet: Set[MAttribute] = superTypeAttributes.foldLeft(attributeSet)((attrs, next) => attrs ++ next)
  def superTypeAttributes: List[Set[MAttribute]] = for {st <- superTypes} yield st.fullAttributeSet
  def attributeSet: Set[MAttribute] = attributes.toSet

}
