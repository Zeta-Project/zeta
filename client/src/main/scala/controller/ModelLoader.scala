package controller

import scala.scalajs.js.Array


case class ModelLoader(modelId: String) {
  /** TODO:: LOAD USING METAMODEL API */
  def mClasses: scalajs.js.Array[String] = Array("NODE", "EDGE", "FOO", "BAR")
  def mRefs: scalajs.js.Array[String] = Array("Ref1", "Ref2")
}
