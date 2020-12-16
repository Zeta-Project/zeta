package de.htwg.zeta.server.routing

import de.htwg.zeta.server.controller.ActivateAccountController
import de.htwg.zeta.server.controller.ApplicationController
import de.htwg.zeta.server.controller.ChangePasswordController
import de.htwg.zeta.server.controller.DynamicFileController
import de.htwg.zeta.server.controller.ForgotPasswordController
import de.htwg.zeta.server.controller.MetaModelController
import de.htwg.zeta.server.controller.ModelController
import de.htwg.zeta.server.controller.ResetPasswordController
import de.htwg.zeta.server.controller.SignInController
import de.htwg.zeta.server.controller.SignUpController
import de.htwg.zeta.server.controller.WebSocketController
import de.htwg.zeta.server.controller.codeEditor.CodeEditorController
import de.htwg.zeta.server.controller.generatorControlForwader.GeneratorControlController
import de.htwg.zeta.server.controller.restApi.BondedTaskRestApi
import de.htwg.zeta.server.controller.restApi.DslRestApi
import de.htwg.zeta.server.controller.restApi.EventDrivenTaskRestApi
import de.htwg.zeta.server.controller.restApi.FileRestApi
import de.htwg.zeta.server.controller.restApi.FilterRestApi
import de.htwg.zeta.server.controller.restApi.GeneratorImageRestApi
import de.htwg.zeta.server.controller.restApi.GeneratorRestApi
import de.htwg.zeta.server.controller.restApi.GraphicalDslRestApi
import de.htwg.zeta.server.controller.restApi.MetaModelReleaseRestApi
import de.htwg.zeta.server.controller.restApi.ModelRestApi
import de.htwg.zeta.server.controller.restApi.TimedTaskRestApi
import de.htwg.zeta.server.controller.restApi.v2
import de.htwg.zeta.server.controller.webpage.WebpageController
import org.webjars.play.WebJarAssets

/**
 */
trait WebController {

  protected val webCont: WebControllerContainer

  protected lazy val backendController: GeneratorControlController = webCont.backendController.get()
  protected lazy val applicationController: ApplicationController = webCont.applicationController.get()
  protected lazy val signUpController: SignUpController = webCont.signUpController.get()
  protected lazy val signInController: SignInController = webCont.signInController.get()
  protected lazy val forgotPasswordController: ForgotPasswordController = webCont.forgotPasswordController.get()
  protected lazy val resetPasswordController: ResetPasswordController = webCont.resetPasswordController.get()
  protected lazy val changePasswordController: ChangePasswordController = webCont.changePasswordController.get()
  protected lazy val activateAccountController: ActivateAccountController = webCont.activateAccountController.get()
  protected lazy val webpageController: WebpageController = webCont.webpageController.get()
  protected lazy val metaModelController: MetaModelController = webCont.metaModelController.get()
  protected lazy val modelController: ModelController = webCont.modelController.get()
  protected lazy val codeEditorController: CodeEditorController = webCont.codeEditorController.get()
  protected lazy val webJarAssets: WebJarAssets = webCont.webJarAssets.get()
  protected lazy val dynamicFileController: DynamicFileController = webCont.dynamicFileController.get()
  protected lazy val metaModelRestApi: GraphicalDslRestApi = webCont.metaModelRestApi.get()
  protected lazy val metaModelRestApiV2: v2.GraphicalDslRestApi = webCont.metaModelRestApiV2.get()
  protected lazy val modelRestApi: ModelRestApi = webCont.modelRestApi.get()
  protected lazy val generatorImageRestApi: GeneratorImageRestApi = webCont.generatorImageRestApi.get()
  protected lazy val generatorRestApi: GeneratorRestApi = webCont.generatorRestApi.get()
  protected lazy val filterRestApi: FilterRestApi = webCont.filterRestApi.get()
  protected lazy val dslRestApi: DslRestApi = webCont.dslRestApi.get()
  protected lazy val metaModelReleaseRestApi: MetaModelReleaseRestApi = webCont.metaModelReleaseRestApi.get()
  protected lazy val bondedTaskRestApi: BondedTaskRestApi = webCont.bondedTaskRestApi.get()
  protected lazy val eventDrivenTaskRestApi: EventDrivenTaskRestApi = webCont.eventDrivenTaskRestApi.get()
  protected lazy val timedTaskRestApi: TimedTaskRestApi = webCont.timedTaskRestApi.get()
  protected lazy val fileRestApi: FileRestApi = webCont.fileRestApi.get()
  protected lazy val webSocket: WebSocketController = webCont.webSocket.get()
}
