package de.htwg.zeta.server.routing

import javax.inject.Inject

import com.google.inject.Provider
import controllers.WebJarAssets
import de.htwg.zeta.server.controller.ActivateAccountController
import de.htwg.zeta.server.controller.ApplicationController
import de.htwg.zeta.server.controller.ChangePasswordController
import de.htwg.zeta.server.controller.DynamicFileController
import de.htwg.zeta.server.controller.ForgotPasswordController
import de.htwg.zeta.server.controller.GeneratorController
import de.htwg.zeta.server.controller.MetaModelController
import de.htwg.zeta.server.controller.ModelController
import de.htwg.zeta.server.controller.ResetPasswordController
import de.htwg.zeta.server.controller.SignInController
import de.htwg.zeta.server.controller.SignUpController
import de.htwg.zeta.server.controller.WebAppController
import de.htwg.zeta.server.controller.codeEditor.CodeEditorController
import de.htwg.zeta.server.controller.generatorControlForwader.GeneratorControlController
import de.htwg.zeta.server.controller.restApi.FilterRestApi
import de.htwg.zeta.server.controller.restApi.GeneratorImageRestApi
import de.htwg.zeta.server.controller.restApi.GeneratorRestApi
import de.htwg.zeta.server.controller.restApi.MetaModelReleaseRestApi
import de.htwg.zeta.server.controller.restApi.MetaModelRestApi
import de.htwg.zeta.server.controller.restApi.ModelRestApi
import de.htwg.zeta.server.controller.webpage.WebpageController

/**
 */
class WebControllerContainer @Inject() private(
    val backendController: Provider[GeneratorControlController],
    val applicationController: Provider[ApplicationController],
    val signUpController: Provider[SignUpController],
    val signInController: Provider[SignInController],
    val forgotPasswordController: Provider[ForgotPasswordController],
    val resetPasswordController: Provider[ResetPasswordController],
    val changePasswordController: Provider[ChangePasswordController],
    val activateAccountController: Provider[ActivateAccountController],
    val webpageController: Provider[WebpageController],
    val metaModelController: Provider[MetaModelController],
    val modelController: Provider[ModelController],
    val generatorController: Provider[GeneratorController],
    val codeEditorController: Provider[CodeEditorController],
    val webJarAssets: Provider[WebJarAssets],
    val dynamicFileController: Provider[DynamicFileController],
    val metaModelRestApi: Provider[MetaModelRestApi],
    val modelRestApi: Provider[ModelRestApi],
    val webApp: Provider[WebAppController],
    val generatorImageRestApi: Provider[GeneratorImageRestApi],
    val generatorRestApi: Provider[GeneratorRestApi],
    val filterRestApi: Provider[FilterRestApi],
    val metaModelReleaseRestApi: Provider[MetaModelReleaseRestApi])
