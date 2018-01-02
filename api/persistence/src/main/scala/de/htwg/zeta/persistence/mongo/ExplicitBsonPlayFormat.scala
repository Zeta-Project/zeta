package de.htwg.zeta.persistence.mongo

import scala.util.control.NoStackTrace

import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.OFormat
import play.api.libs.json.Json
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.play.json.JsObjectReader
import reactivemongo.play.json.JsObjectWriter


private[mongo] class ExplicitBsonPlayFormat[E](format: OFormat[E]) extends BSONDocumentWriter[E] with BSONDocumentReader[E] {
  override def read(bson: BSONDocument): E = format.reads(JsObjectReader.read(bson)) match {
    case JsSuccess(value, _) => value
    case error: JsError => throw new BsonParseException(Json.prettyPrint(JsError.toJson(error)))
  }

  override def write(t: E): BSONDocument = JsObjectWriter.write(format.writes(t))
}

private[mongo] object ExplicitBsonPlayFormat {
  def apply[E](format: OFormat[E]): ExplicitBsonPlayFormat[E] = new ExplicitBsonPlayFormat(format)
}

class BsonParseException(msg: String) extends Exception(msg) with NoStackTrace

