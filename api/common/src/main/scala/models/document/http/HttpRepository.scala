package models.document.http

import java.util.concurrent.TimeUnit

import models.document.Document
import models.document.HttpAllDocsQuery
import models.document.MetaModelEntity
import models.document.ModelEntity
import models.document.Repository
import models.document.Specification
import models.modelDefinitions.model.Model
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsError
import play.api.libs.json.JsNull
import play.api.libs.json.JsObject
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.ws.WSAuthScheme
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import rx.lang.scala.Observable

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.reflect.ClassTag
import scala.reflect.classTag

object HttpRepository {
  def apply(session: String)(implicit client: WSClient): HttpRepository = new HttpRepository(session)

  def apply(auth: Auth)(implicit client: WSClient): HttpRepository = new HttpRepository(Some(auth))
}

case class Auth(username: String, password: String)

/**
 * Http Repository to query the Couchbase Sync Gateway
 */
class HttpRepository(var session: String = "", auth: Option[Auth] = None)(implicit client: WSClient) extends Repository {

  def this(auth: Option[Auth])(implicit client: WSClient) = this("", auth)

  val url = "http://database:4984/db/"

  def request(address: String): WSRequest = auth match {
    case Some(x) => client.url(address).withAuth(x.username, x.password, WSAuthScheme.BASIC)
    case None => client.url(address).withHeaders("Cookie" -> s"SyncGatewaySession=${session};")
  }

  /**
   * Create a new entity
   *
   * @param doc The Document to save
   * @return Future which resolve after create
   */
  def create[T: ClassTag](doc: Document)(implicit w: Writes[Document]): Future[T] = {
    val p = Promise[T]

    val address = s"${url}${doc.id}"

    request(address).put(Json.toJson(doc)).map { response =>
      if (response.status == 201) {
        (response.json \ "rev").toOption match {
          case Some(x) =>
            val updated = Document.update(doc, x.as[String])
            p.success(updated.asInstanceOf[T])
          case None =>
            p.failure(new Exception(s"No revision returned after creation of ${doc}"))
        }
        p.success(doc.asInstanceOf[T])
      } else {
        p.failure(new Exception(response.body))
      }
    }.recover {
      case e: Exception => p.failure(e)
    }
    p.future
  }

  /**
   * Update a document
   *
   * @param doc The Document to update
   * @return Future which resolve after update
   */
  def update[T: ClassTag](doc: Document)(implicit w: Writes[Document]): Future[T] = {
    val p = Promise[T]

    val address = s"${url}${doc.id}"

    request(address).get().map { response =>
      // preserve attachments
      val attachments = (response.json \ "_attachments").getOrElse(JsNull)
      // Document to json
      val json = Json.toJson(doc).as[JsObject] + ("_attachments" -> attachments)

      request(address).put(json).map { response =>
        if (response.status == 201) {
          (response.json \ "rev").toOption match {
            case Some(x) =>
              val updated = Document.update(doc, x.as[String])
              p.success(updated.asInstanceOf[T])
            case None =>
              p.failure(new Exception(s"No revision returned after creation of ${doc}"))
          }
        } else {
          p.failure(new Exception(response.body))
        }
      }.recover {
        case e: Exception => p.failure(e)
      }
    }.recover {
      case e: Exception => p.failure(e)
    }
    p.future
  }

  /**
   * Remove an entity
   *
   * @param id The id of the document to remove
   * @return Future which resolve after remove
   */
  def delete(id: ID): Future[Unit] = {
    val p = Promise[Unit]

    val address = s"${url}${id}"

    request(address).get().map { response =>
      // Document to json
      val json = response.json.as[JsObject] + ("_deleted" -> JsBoolean(true))

      request(address).put(json).map { response =>
        if (response.status == 201) {
          p.success()
        } else {
          p.failure(new Exception(response.body))
        }
      }.recover {
        case e: Exception => p.failure(e)
      }
    }.recover {
      case e: Exception => p.failure(e)
    }
    p.future
  }

  def get[T: ClassTag](id: String)(implicit r: Reads[Document]): Future[T] = {
    val p = Promise[T]

    val address = s"${url}${id}"

    request(address).get().map { response =>
      if (response.status == 200) {
        val json = response.json
        json.validate[Document] match {
          case s: JsSuccess[Document] => {
            val doc = s.get
            if (classTag[T].runtimeClass.isInstance(doc)) {
              val instance = doc.asInstanceOf[T]
              if (instance.isInstanceOf[ModelEntity]) {
                val model: ModelEntity = instance.asInstanceOf[ModelEntity]
                get[MetaModelEntity](model.metaModelId).map { release =>
                  Model.readAndMergeWithMetaModel((json \ "model").get, release.metaModel) match {
                    case JsSuccess(value, path) => p.success(model.copy(model = value).asInstanceOf[T])
                    case JsError(errors) => p.failure(new Exception(s"Failed parsing of MetaModel in Model on GET ${id}"))
                  }
                }.recover { case e: Exception => p.failure(e) }
              } else {
                p.success(instance)
              }
            } else {
              p failure (new Exception(s"Document has not expected type on GET ${id}"))
            }
          }
          case e: JsError => p failure (new Exception(s"Unable to parse json on GET ${id}"))
        }
      } else {
        p.failure(new Exception(response.body))
      }
    }.recover {
      case e: Exception =>
        p.failure(e)
    }
    p.future
  }

  // Document from the query which contain the document id
  case class Doc(id: String)
  implicit lazy val readChanges: Reads[Doc] = Json.reads[Doc]
  // List of documents from the query
  case class Documents(rows: List[Doc])
  implicit lazy val readDocuments: Reads[Documents] = Json.reads[Documents]
  /**
   * Query for documents which match the specification
   *
   * @param specification The specification for the query
   * @return Observable to return all entities
   */
  private def allDocs[T: ClassTag](specification: HttpAllDocsQuery): Observable[T] = {
    Observable(subscriber => {
      val address = s"${url}_all_docs"

      request(address)
        .withQueryString("startkey" -> specification.startkey)
        .withQueryString("endkey" -> specification.endkey)
        //.withQueryString("limit" -> specification.httpOptions.limit)
        .get().map { response =>
          if (response.status == 200) {
            val documents: Documents = response.json.as[Documents]

            for {document <- documents.rows} {
              if (!subscriber.isUnsubscribed) {
                val result = get[T](document.id).map { result =>
                  subscriber.onNext(result)
                }.recover {
                  case e: Exception => subscriber.onError(e)
                }
                Await.result(result, Duration(100, TimeUnit.MILLISECONDS))
              }
            }

            if (!subscriber.isUnsubscribed) {
              subscriber.onCompleted()
            }
          } else {
            subscriber.onError(new Exception(response.body))
          }
        }.recover {
          case e: Exception =>
            subscriber.onError(e)
        }
    })
  }

  /**
   * Query documents which match the specification
   *
   * @param specification The specification for the query
   * @return Observable to return all entities
   */
  def query[T: ClassTag](specification: Specification): Observable[T] = specification.http match {
    case query: HttpAllDocsQuery => allDocs[T](query)
  }
}
