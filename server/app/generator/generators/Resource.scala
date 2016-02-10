package generators

import java.net.URI
import java.nio.file.Path

import parser.Cache

/**
 * Created by julian on 07.02.16.
 */
trait Resource {
  val path:Path
  val cache:Cache

  def getPath:Path
  def getURI:URI

}
