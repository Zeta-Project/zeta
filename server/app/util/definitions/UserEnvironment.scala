package util.definitions

import models.{MongoDbUserService, SecureSocialUser}
import plugins.{SecuresocialMailTemplates, SecuresocialViews}
import securesocial.controllers.{MailTemplates, ViewTemplates}
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.UsernamePasswordProvider

import scala.collection.immutable.ListMap

class UserEnvironment extends RuntimeEnvironment.Default {
  override type U = SecureSocialUser
  override implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
  override lazy val userService = new MongoDbUserService()
  override lazy val viewTemplates: ViewTemplates = new SecuresocialViews(this)
  override lazy val mailTemplates: MailTemplates = new SecuresocialMailTemplates(this)
  override lazy val providers = ListMap(
    include(new UsernamePasswordProvider(userService, avatarService, viewTemplates, passwordHashers))
  )
}
