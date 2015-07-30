package modigen.util.datavis.dummy

import modigen.util.domain._

object Dummy {
  private val socialSecurityNumber = new MAttributeString("socialSecurityNumber", 1, 1)
  socialSecurityNumber.uniqueGlobal = true

  private val name = new MAttributeString("name", 1, 1)

  private val person = new MClass("Person", List(), List(socialSecurityNumber, name))
  person.isAbstract = true

  private val male = new MClass("Male", List(person), List())
  private val female = new MClass("Female", List(person), List())

  private val isHusband = new MReference("isHusband", List())
  private val isWife = new MReference("isWife", List())
  private val isFather = new MReference("isFather", List())
  private val isMother = new MReference("isMother", List())

  person.inputs = List(new MLinkDef(isFather, 1, 1, false), new MLinkDef(isMother, 1, 1, false))

  male.inputs = List(new MLinkDef(isWife, 1, 1, false))
  male.outputs = List(new MLinkDef(isHusband, 1, 1, false), new MLinkDef(isFather, 1, 1, false))

  female.inputs = List(new MLinkDef(isHusband, 1, 1, false))
  female.outputs = List(new MLinkDef(isWife, 1, 1, false), new MLinkDef(isMother, 1, 1, false))

  isHusband.source = List(new MLinkDef(male, 1, 1, false))
  isHusband.target = List(new MLinkDef(female, 1, 1, false))

  isWife.source = List(new MLinkDef(female, 1, 1, false))
  isWife.target = List(new MLinkDef(male, 1, 1, false))

  isFather.source = List(new MLinkDef(male, 1, 1, false))
  isFather.target = List(new MLinkDef(person, 1, 1, false))

  isMother.source = List(new MLinkDef(female, 1, 1, false))
  isMother.target = List(new MLinkDef(person, 1, 1, false))

  private val model = new Metamodel(List(male, female, person).map(m => m.name -> m).toMap, List(isHusband, isWife, isFather, isMother).map(m => m.name -> m).toMap)

  def metamodel(instanceID:String) = model
}
