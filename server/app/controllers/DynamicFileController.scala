package controllers

import java.io.File
import play.api.mvc.{Action, Controller}

class DynamicFileController extends Controller {
    val basePath = System.getenv("PWD") + "/server/model_specific/"
    def serveFile(filePath: String) = Action {
      try {
        Ok.sendFile(new File(basePath + filePath) )
      } catch {
        case _ : Throwable => NotFound("File not found "+filePath)
      }
    }
}
