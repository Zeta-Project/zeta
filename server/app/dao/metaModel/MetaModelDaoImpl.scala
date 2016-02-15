package dao.metaModel

import dao.{Result, GenericMongoDao, ReactiveMongoHelper}
import models.metaModel._
import models.metaModel.mCore.{MClass, MReference}
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}

import scala.concurrent.Future


trait MetaModelDao extends GenericMongoDao[MetaModel] with ReactiveMongoHelper {
  // "Coast to Coast" (read-only)
}

object MetaModelDaoImpl extends MetaModelDao {

  // should be a def! => better for connection pooling
  def metaModels = collection("mmd_new")

  val idProjection = Json.obj("_id" -> 0)

  override def findById(id: String): Future[Option[MetaModel]] = {
    findOne(Json.obj("id" -> id))
  }

  override def findOne(query: JsObject): Future[Option[MetaModel]] = {
    metaModels.find(query).projection(idProjection).one[MetaModel]
  }

  override def find(query: JsObject): Future[Seq[MetaModel]] = {
    metaModels.find(query).cursor[MetaModel].collect[List]()
  }

  override def deleteById(id: String): Future[Result] = {
    metaModels.remove(Json.obj("id" -> id)).map {
      res => wrapWriteResult(res)
    }
  }

  override def save(entity: MetaModel): Future[Result] = {
    val modifier = Json.obj("$set" -> entity)
    metaModels.update(Json.obj("id" -> entity.id), modifier, upsert = true).map {
      res => wrapUpdateResult(res)
    }
  }


}