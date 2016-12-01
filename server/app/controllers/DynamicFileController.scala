package controllers

import java.io.File
import play.api.mvc.{Action, Controller}

class DynamicFileController extends Controller {
    val basePath = System.getenv("PWD") + "/server/model_specific/"
    def serveFile(filePath: String) = Action {
      try {
        val fileStream: java.io.InputStream = new java.io.FileInputStream(basePath + filePath)
        val fileString = scala.io.Source.fromInputStream(fileStream).mkString("")
        Ok(fileString) as HTML
      } catch {
        case _ : Throwable => NotFound("File not found "+filePath)
      }
    }
}
