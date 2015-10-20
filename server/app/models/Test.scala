package models

import java.awt.print.Book

import play.api.libs.json.Json

/**
 * Created by mgt on 20.10.15.
 */
object Test {

  case class Test(name: String, author: String)

  implicit val bookWrites = Json.writes[Test]
  implicit val bookReads = Json.reads[Test]

}
