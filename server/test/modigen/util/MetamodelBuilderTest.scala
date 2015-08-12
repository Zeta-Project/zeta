package modigen.util

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json.{JsObject, Json}

import play.api.test._
import play.api.test.Helpers._

import scala.io.Source

class MetamodelBuilderTest extends Specification{
  "MetamodelBuilder" should {
    "parse JSON into Scala" in new WithApplication{
      val json = Json.parse(Source.fromFile("server/test/modigen/util/test.json").getLines.reduceLeft(_+_)).as[JsObject]

      val x = MetamodelBuilder().fromJson(json)
      x
    }
  }

}
