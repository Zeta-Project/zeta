package testclient

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport


object TestClient extends js.JSApp {
  @JSExport
  def main(): Unit = {
    org.scalajs.dom.alert(shared.TestMessage.message)
  }
}
