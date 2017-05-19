package de.htwg.zeta.server.controller

import play.api.mvc.Action
import play.api.mvc.Controller

class DynamicFileController extends Controller {
  val basePath = System.getenv("PWD") + "/server/model_specific/"

  def serveFile(filePath: String) = Action {
    try {
      val fileStream: java.io.InputStream = new java.io.FileInputStream(basePath + filePath)
      val fileString = scala.io.Source.fromInputStream(fileStream).mkString("")
      Ok(fileString) as JAVASCRIPT
    } catch {
      case _: Throwable => NotFound("File not found " + filePath)
    }
  }
}
