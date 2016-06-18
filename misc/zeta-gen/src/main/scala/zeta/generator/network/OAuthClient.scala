package zeta.generator.network

import com.ning.http.client.AsyncHttpClientConfig
import com.typesafe.config.ConfigFactory
import play.api.libs.json.JsValue

import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClient}
import play.api.libs.ws.{DefaultWSClientConfig, WSResponse}
import zeta.generator.domain.metaModel.MetaModel
import zeta.generator.domain.model.{Model, ModelShortInfo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class OAuthClient {

  val conf = ConfigFactory.load()
  lazy val userName = conf.getString("oauth.user.name")
  lazy val userPass = conf.getString("oauth.user.password")
  lazy val clientId = conf.getString("oauth.client.id")
  lazy val clientSecret = conf.getString("oauth.client.secret")
  lazy val tokenUrl = conf.getString("oauth.tokenUrl")
  lazy val entryUrl = conf.getString("rest.entryPointUri")
  lazy val modelSelfLink = conf.getString("rest.links.model_self")
  lazy val modelMetaLink = conf.getString("rest.links.model_metamodel")

  def getModelOverview: Future[Seq[ModelShortInfo]] = {
    query[Seq[ModelShortInfo]] { client =>
      for {
        accessToken <- tokenRequest(client)
        info <- overview(client, accessToken)
      } yield info
    }
  }

  private def overview(client: NingWSClient, accessToken: AccessToken): Future[Seq[ModelShortInfo]] = {
    client.url(entryUrl).withHeaders(
      "Authorization" -> s"Bearer ${accessToken.value}"
    ).get().map { response =>
      (response.json).validate[Seq[ModelShortInfo]].get
    }
  }

  def getModel(id: String): Future[Option[Model]] = {
    query[Option[Model]] { client =>
      for {
        accessToken <- tokenRequest(client)
        info <- overview(client, accessToken).map(_.find(_.id == id))
        metaModelUri = extractLink(info, modelMetaLink)
        modelUri = extractLink(info, modelSelfLink)
        metaModelJs <- loadJsValue(metaModelUri, client, accessToken)
        modelJs <- loadJsValue(modelUri, client, accessToken)
      } yield constructModel(metaModelJs, modelJs)
    }
  }

  private def constructModel(metaOpt: Option[JsValue], modelOpt: Option[JsValue]): Option[Model] = {
    (metaOpt, modelOpt) match {
      case (Some(metaJs), Some(modelJs)) => {
        val metaModel = metaJs.validate[MetaModel].get
        val model = modelJs.validate[Model](Model.reads(metaModel)).get
        Some(model)
      }
      case _ => None
    }
  }

  private def extractLink(info: Option[ModelShortInfo], rel: String) = {
    info.flatMap(_.links.find(_.rel == rel).map(_.href))
  }

  private def loadJsValue(uri: Option[String], client: NingWSClient, token: AccessToken): Future[Option[JsValue]] = {
    uri match {
      case Some(s) =>  {
        client.url(s).withHeaders("Authorization" -> s"Bearer ${token.value}")
        .get().map { response => Some(response.json) }
      }
      case None => Future.successful(None)
    }
  }

  private def query[T](call: (NingWSClient) => Future[T]): Future[T] = {
    val config = new NingAsyncHttpClientConfigBuilder(DefaultWSClientConfig()).build
    val builder = new AsyncHttpClientConfig.Builder(config)
    val client = new NingWSClient(builder.build)
    call(client).map { f =>
      client.close()
      f
    }
  }

  private def tokenRequest(client: NingWSClient): Future[AccessToken] = {
    client.url(tokenUrl).post(Map(
      "client_id" -> Seq(clientId),
      "username" -> Seq(userName),
      "password" -> Seq(userPass),
      "grant_type" -> Seq("password"),
      "client_secret" -> Seq(clientSecret)
    )).map { response => (response.json).validate[AccessToken].get }
  }

}
