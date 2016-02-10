package generators

import java.net.URI
import java.nio.file.{Path, Paths}

import parser.Cache

/**
 * Created by julian on 07.02.16.
 */
//TODO this is a mock for the Resource (needed by xtext) what exactly te rsource is in here i dont know yet.
class Resource_Mock(file:String,implicit override val cache: Cache) extends Resource{
  override val path = Paths.get(file)

  override def getPath:Path = path
  override def getURI:URI = path.toUri

  override def toString = path.toString
}
