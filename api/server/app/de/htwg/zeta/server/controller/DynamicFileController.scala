package de.htwg.zeta.server.controller

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result

// TODO replace with Database access
class DynamicFileController extends Controller {
  private val basePath: String = System.getenv("PWD") + "/server/model_specific/"

  def serveFile(filePath: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    try {
      val fileStream: java.io.InputStream = new java.io.FileInputStream(basePath + filePath)
      val fileString = scala.io.Source.fromInputStream(fileStream).mkString("")
      Ok(fileString) as JAVASCRIPT
    } catch {
      case _: Throwable => NotFound("File not found " + filePath)
    }
  }
}
