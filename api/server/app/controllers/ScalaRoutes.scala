package controllers

import java.util.UUID
import javax.inject.Inject

import de.htwg.zeta.server.routing.WebController
import de.htwg.zeta.server.routing.WebControllerContainer
import de.htwg.zeta.server.routing.RouteController
import de.htwg.zeta.server.routing.RouteControllerContainer
import de.htwg.zeta.server.routing.authentication.RouteControllerContainer
import play.api.libs.json.JsValue
import play.api.mvc.WebSocket
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.BodyParsers

/**
 * All routes are managed in this class
 */
class ScalaRoutes @Inject()(
    protected val routeCont: RouteControllerContainer,
    protected val webCont: WebControllerContainer
) extends RouteController with WebController {


  def backendDeveloper: WebSocket = AuthenticatedSocket(BackendController.developer() _)

  def backendGenerator(id: String): WebSocket = AuthenticatedSocket(BackendController.generator(id) _)

  def backendUser(model: String): WebSocket = AuthenticatedSocket(BackendController.user(model) _)


  // # Home page
  def appIndex(): Action[AnyContent] = AuthenticatedGet(ApplicationController.index _)

  def user(): Action[AnyContent] = AuthenticatedGet(ApplicationController.user _)

  def signOut: Action[AnyContent] = AuthenticatedGet(ApplicationController.signOut _)

  def authenticate(provider: String): Action[AnyContent] = UnAuthenticatedGet(SocialAuthController.authenticate(provider) _)

  def signUpView(): Action[AnyContent] = UnAuthenticatedGet(SignUpController.view _)

  def signUp(): Action[AnyContent] = UnAuthenticatedPost(SignUpController.submit _)

  def signInView(): Action[AnyContent] = UnAuthenticatedGet(SignInController.view _)

  def signIn(): Action[AnyContent] = UnAuthenticatedPost(SignInController.submit _)

  def forgotPasswordView(): Action[AnyContent] = UnAuthenticatedGet(ForgotPasswordController.view _)

  def forgotPassword(): Action[AnyContent] = UnAuthenticatedPost(ForgotPasswordController.submit _)

  def resetPasswordView(token: UUID): Action[AnyContent] = UnAuthenticatedGet(ResetPasswordController.view(token: java.util.UUID) _)

  def resetPassword(token: UUID): Action[AnyContent] = UnAuthenticatedPost(ResetPasswordController.submit(token: java.util.UUID) _)

  def changePasswordView(): Action[AnyContent] = AuthenticatedWithProviderGet(ChangePasswordController.view _)

  def changePassword(): Action[AnyContent] = AuthenticatedWithProviderPost(ChangePasswordController.submit _)

  def sendActivateAccount(email: String): Action[AnyContent] = UnAuthenticatedGet(ActivateAccountController.send(email) _) // TODO send email per API??

  def activateAccount(token: UUID): Action[AnyContent] = UnAuthenticatedGet(ActivateAccountController.activate(token: java.util.UUID) _)


  // ### Webpage
  def webpageIndex(): Action[AnyContent] = AuthenticatedGet(WebpageController.index _)

  def diagramsOverviewShortInfo(): Action[AnyContent] = AuthenticatedGet(WebpageController.diagramsOverviewShortInfo _)

  def diagramsOverview(uuid: String): Action[AnyContent] = AuthenticatedGet(WebpageController.diagramsOverview(uuid) _)


  // # metamodel editor
  def metaModelEditor(metaModelUuid: String): Action[AnyContent] = AuthenticatedGet(MetaModelController.metaModelEditor(metaModelUuid) _)

  def metaModelSocket(metaModelUuid: String): WebSocket = AuthenticatedSocket(MetaModelController.metaModelSocket(metaModelUuid) _)


  // ### model editor
  def modelEditor(metaModelUuid: String, modelUuid: String): Action[AnyContent] = AuthenticatedGet(ModelController.modelEditor(metaModelUuid, modelUuid) _)

  def modelSocket(instanceId: String, graphType: String): WebSocket = AuthenticatedSocket(ModelController.modelSocket(instanceId, graphType) _)

  def modelValidator(): Action[AnyContent] = AuthenticatedGet(ModelController.modelValidator _)


  // ### vr
  def vrModelEditor(metaModelUuid: String, modelUuid: String): Action[AnyContent] = AuthenticatedGet(ModelController.vrModelEditor(metaModelUuid, modelUuid) _)


  // # temporary
  def generate(metaModelUuid: String): Action[AnyContent] = AuthenticatedGet(GeneratorController.generate(metaModelUuid) _)

  /* ### MetaModel REST API
   * MMRA => MetaModelRestApi
   */

  def MMRAshowForUser: Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.showForUser _)

  def MMRAinsert: Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, MetaModelRestApi.insert _)

  def MMRAupdate(metaModelId: String): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.update(metaModelId) _)

  def MMRAget(metaModelId: String): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.get(metaModelId) _)

  def MMRAdelete(metaModelId: String): Action[AnyContent] = AuthenticatedDelete(MetaModelRestApi.delete(metaModelId) _)

  def MMRAgetMetaModelDefinition(metaModelId: String): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMetaModelDefinition(metaModelId) _)

  def MMRAupdateMetaModelDefinition(metaModelId: String): Action[JsValue] =
    AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateMetaModelDefinition(metaModelId) _)

  def MMRAgetMClasses(metaModelId: String): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMClasses(metaModelId) _)

  def MMRAgetMReferences(metaModelId: String): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMReferences(metaModelId) _)

  def MMRAgetMClass(metaModelId: String, mClassName: String): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMClass(metaModelId, mClassName) _)

  def MMRAgetMReference(metaModelId: String, mReferenceName: String): Action[AnyContent] =
    AuthenticatedGet(MetaModelRestApi.getMReference(metaModelId, mReferenceName) _)

  def MMRAgetShape(metaModelId: String): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getShape(metaModelId) _)

  def MMRAupdateShape(metaModelId: String): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateShape(metaModelId) _)

  def MMRAgetStyle(metaModelId: String): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getStyle(metaModelId) _)

  def MMRAupdateStyle(metaModelId: String): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateStyle(metaModelId) _)

  def MMRAgetDiagram(metaModelId: String): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getDiagram(metaModelId) _)

  def MMRAupdateDiagram(metaModelId: String): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateDiagram(metaModelId) _)


  /* ### Model REST API
   * MRA => ModelRestApi
   */

  def MRAshowForUser: Action[AnyContent] = AuthenticatedGet(ModelRestApi.showForUser() _)

  def MRAinsert: Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, ModelRestApi.insert() _)

  def MRAupdate(modelId: String): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, ModelRestApi.update(modelId) _)

  def MRAget(modelId: String): Action[AnyContent] = AuthenticatedGet(ModelRestApi.get(modelId) _)

  def MRAgetModelDefinition(modelId: String): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getModelDefinition(modelId) _)

  def MRAupdateModel(modelId: String): Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, ModelRestApi.updateModel(modelId) _)

  def MRAgetNodes(modelId: String): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getNodes(modelId) _)

  def MRAgetNode(modelId: String, nodeName: String): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getNode(modelId, nodeName) _)

  def MRAgetEdges(modelId: String): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getEdges(modelId) _)

  def MRAgetEdge(modelId: String, edgeName: String): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getEdge(modelId, edgeName) _)

  def MRAdelete(modelId: String): Action[AnyContent] = AuthenticatedDelete(ModelRestApi.delete(modelId) _)


  // ### Code Editor
  def codeEditor(metaModelUuid: String, dslType: String): Action[AnyContent] = AuthenticatedGet(CodeEditorController.codeEditor(metaModelUuid, dslType) _)

  def codeEditorSocket(metaModelUuid: String, dslType: String): WebSocket = AuthenticatedSocket(CodeEditorController.codeSocket(metaModelUuid, dslType) _)


  // # Map static resources from the /public folder to the /assets URL path
  def assetsAt(file: String): Action[AnyContent] = Assets.at(path = "/public", file)

  def webJarAssetsAt(file: String): Action[AnyContent] = WebJarAssets.at(file)

  def serveDynamicFile(file: String): Action[AnyContent] = AuthenticatedGet(DynamicFileController.serveFile(file) _)

}



