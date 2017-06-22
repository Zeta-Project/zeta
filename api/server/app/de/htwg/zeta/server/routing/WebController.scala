package de.htwg.zeta.server.routing

import controllers.WebJarAssets
import de.htwg.zeta.server.controller.ApplicationController
import de.htwg.zeta.server.controller.SignUpController
import de.htwg.zeta.server.controller.SignInController
import de.htwg.zeta.server.controller.ForgotPasswordController
import de.htwg.zeta.server.controller.ResetPasswordController
import de.htwg.zeta.server.controller.ChangePasswordController
import de.htwg.zeta.server.controller.ActivateAccountController
import de.htwg.zeta.server.controller.MetaModelController
import de.htwg.zeta.server.controller.ModelController
import de.htwg.zeta.server.controller.GeneratorController
import de.htwg.zeta.server.controller.CodeEditorController
import de.htwg.zeta.server.controller.DynamicFileController
import de.htwg.zeta.server.controller.generatorControlForwader.GeneratorControlController
import de.htwg.zeta.server.controller.restApi.MetaModelRestApi
import de.htwg.zeta.server.controller.restApi.ModelRestApi
import de.htwg.zeta.server.controller.webpage.WebpageController

/**
 */
trait WebController {

  protected val webCont: WebControllerContainer

  protected lazy val BackendController: GeneratorControlController = webCont.backendController.get()
  protected lazy val ApplicationController: ApplicationController = webCont.applicationController.get()
  protected lazy val SignUpController: SignUpController = webCont.signUpController.get()
  protected lazy val SignInController: SignInController = webCont.signInController.get()
  protected lazy val ForgotPasswordController: ForgotPasswordController = webCont.forgotPasswordController.get()
  protected lazy val ResetPasswordController: ResetPasswordController = webCont.resetPasswordController.get()
  protected lazy val ChangePasswordController: ChangePasswordController = webCont.changePasswordController.get()
  protected lazy val ActivateAccountController: ActivateAccountController = webCont.activateAccountController.get()
  protected lazy val WebpageController: WebpageController = webCont.webpageController.get()
  protected lazy val MetaModelController: MetaModelController = webCont.metaModelController.get()
  protected lazy val ModelController: ModelController = webCont.modelController.get()
  protected lazy val GeneratorController: GeneratorController = webCont.generatorController.get()
  protected lazy val CodeEditorController: CodeEditorController = webCont.codeEditorController.get()
  protected lazy val WebJarAssets: WebJarAssets = webCont.webJarAssets.get()
  protected lazy val DynamicFileController: DynamicFileController = webCont.dynamicFileController.get()
  protected lazy val MetaModelRestApi: MetaModelRestApi = webCont.metaModelRestApi.get()
  protected lazy val ModelRestApi: ModelRestApi = webCont.modelRestApi.get()
}
