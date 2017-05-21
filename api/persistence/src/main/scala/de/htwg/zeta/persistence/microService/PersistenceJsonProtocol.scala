package de.htwg.zeta.persistence.microService

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.User
import models.document.PasswordInfoEntity
import models.document.UserEntity
import models.document.Log
import models.document.JobSettings
import models.document.EventDrivenTask
import models.document.BondedTask
import models.document.Generator
import models.document.Filter
import models.document.GeneratorImage
import models.document.FilterImage
import models.document.Settings
import models.document.DockerSettings
import spray.json.DefaultJsonProtocol
import spray.json.JsString
import spray.json.JsValue
import spray.json.RootJsonFormat
import spray.json.deserializationError

/**
 * Provides implicit conversion for using the Spray-Json library.
 */
object PersistenceJsonProtocol extends DefaultJsonProtocol {


  /** Spray-Json conversion protocol for [[models.document.EventDrivenTask]] */
  implicit val eventDrivenTaskFormat: RootJsonFormat[EventDrivenTask] = jsonFormat6(EventDrivenTask.apply)

  /** Spray-Json conversion protocol for [[models.document.BondedTask]] */
  implicit val bondedTaskFormat: RootJsonFormat[BondedTask] = jsonFormat7(BondedTask.apply)

  /** Spray-Json conversion protocol for [[models.document.Generator]] */
  implicit val generatorFormat: RootJsonFormat[Generator] = jsonFormat4(Generator.apply)

  /** Spray-Json conversion protocol for [[models.document.Filter]] */
  implicit val filterFormat: RootJsonFormat[Filter] = jsonFormat5(Filter.apply)

  /** Spray-Json conversion protocol for [[models.document.GeneratorImage]] */
  implicit val generatorImageFormat: RootJsonFormat[GeneratorImage] = jsonFormat4(GeneratorImage.apply)

  /** Spray-Json conversion protocol for [[models.document.FilterImage]] */
  implicit val filterImageFormat: RootJsonFormat[FilterImage] = jsonFormat4(FilterImage.apply)

  /** Spray-Json conversion protocol for [[models.document.Settings]] */
  implicit val settingsFormat: RootJsonFormat[Settings] = {
    implicit val dockerSettingsFormat: RootJsonFormat[DockerSettings] = jsonFormat2(DockerSettings.apply)
    implicit val jobSettingsInfoFormat: RootJsonFormat[JobSettings] = jsonFormat3(JobSettings.apply)
    jsonFormat4(Settings.apply)
  }

  /** Spray-Json conversion protocol for [[models.document.MetaModelEntity]] */
  // TODO: implicit val metaModelEntityFormat: RootJsonFormat[MetaModelEntity] = jsonFormat6(MetaModelEntity.apply)

  /** Spray-Json conversion protocol for [[models.document.MetaModelRelease]] */
  // TODO: implicit val metaModelReleaseFormat: RootJsonFormat[MetaModelRelease] = jsonFormat6(MetaModelRelease.apply)

  /** Spray-Json conversion protocol for [[models.document.ModelEntity]] */
  // TODO: implicit val modelEntityFormat: RootJsonFormat[ModelEntity] = jsonFormat5(ModelEntity.apply)

  /** Spray-Json conversion protocol for [[models.document.Log]] */
  implicit val logFormat: RootJsonFormat[Log] = jsonFormat5(Log.apply)

  /** Spray-Json conversion protocol for [[models.document.UserEntity]] */
  implicit val passwordInfoEntityFormat: RootJsonFormat[PasswordInfoEntity] = {
    implicit val passwordInfo: RootJsonFormat[PasswordInfo] = jsonFormat3(PasswordInfo)
    jsonFormat3(PasswordInfoEntity.apply)
  }

  /** Spray-Json conversion protocol for [[models.document.UserEntity]] */
  implicit val userEntityFormat: RootJsonFormat[UserEntity] = {
    implicit object UuidJsonFormat extends RootJsonFormat[UUID] {

      /** Convert a UUID to a String.
       *
       * @param x UUID
       * @return String
       */
      def write(x: UUID): JsValue = {
        JsString(x.toString)
      }

      /** Convert a String to a UUID.
       *
       * @param value String
       * @return UUID
       */
      def read(value: JsValue): UUID = {
        value match {
          case JsString(x) => UUID.fromString(x)
          case x: Any => deserializationError(s"Expected UUID as JsString, but got $x")
        }
      }

    }
    implicit val loginInfoFormat: RootJsonFormat[LoginInfo] = jsonFormat2(LoginInfo)
    implicit val userFormat: RootJsonFormat[User] = jsonFormat8(User.apply)
    jsonFormat3(UserEntity.apply)
  }

}
