package zeta.generator

import zeta.generator.controller._
import zeta.generator.view.View

object Main {
  def main(args : Array[String]) {
    val controller = Controller()
    val view = View(controller)
    view.handleArgs(args)
  }
}
