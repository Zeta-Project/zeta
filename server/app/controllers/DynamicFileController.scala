package controllers

import java.io.File

import play.api.mvc.Action
import play.api.mvc.Results._

class DynamicFileController {
    var basePath = System.getenv("PWD") + "/server/model_specific/"
    def serveFile(filePath: String) = Action {
      try {
        Ok.sendFile(new File(basePath + filePath) )
      } catch {
        case _ => NotFound("File not found "+filePath)
      }
    }
}
