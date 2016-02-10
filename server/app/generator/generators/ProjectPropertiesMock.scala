package generators

import java.net.URI

import model.diagram.node.Node

/**
 * Created by julian on 07.02.16.
 */
//TODO this is a mock for PorjectProperties! Ask Markus where to access actual ProjectProperties
object ProjectPropertiesMock{
  private var modelPath:Option[URI] = None
  val SHAPES_FILE_EXTENSION = "mock.shape"
  val SPRAY_FILE_EXTENSION = "mock.spray"

  def setModelUri(uri:URI) = modelPath = Some(uri)
}

trait MclassMock {
  val name:String
  def isSuperTypeOf(possibleChildClass:MclassMock):Boolean
}
 trait MreferenceMock {
   val name:String
   def EReferenceType:MclassMock
 }
