package controllers

import java.util.UUID
import javax.inject.Inject

import de.htwg.zeta.server.routing.WebController
import de.htwg.zeta.server.routing.WebControllerContainer
import de.htwg.zeta.server.routing.RouteController
import de.htwg.zeta.server.routing.RouteControllerContainer
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


  def getSocketDeveloper: WebSocket = AuthenticatedSocket(BackendController.developer() _)

  def getSocketGenerator(id: UUID): WebSocket = AuthenticatedSocket(BackendController.generator(id) _)

  def getSocketUser(modelId: UUID): WebSocket = AuthenticatedSocket(BackendController.user(modelId) _)


  // # Home page
  def getIndex(): Action[AnyContent] = {
    AuthenticatedGet(ApplicationController.index _)
  }

  def getUser(): Action[AnyContent] = AuthenticatedGet(ApplicationController.user _)

  def getSignout: Action[AnyContent] = AuthenticatedGet(ApplicationController.signOut _)

  def getSignUp(): Action[AnyContent] = UnAuthenticatedGet(SignUpController.view _)

  def postSignUp(): Action[AnyContent] = UnAuthenticatedPost(SignUpController.submit _)

  def getSignIn(): Action[AnyContent] = UnAuthenticatedGet(SignInController.view _)

  def postSignIn(): Action[AnyContent] = UnAuthenticatedPost(SignInController.submit _)

  def getPasswordForgot(): Action[AnyContent] = UnAuthenticatedGet(ForgotPasswordController.view _)

  def postPasswordForgot(): Action[AnyContent] = UnAuthenticatedPost(ForgotPasswordController.submit _)

  def getPasswordReset(token: UUID): Action[AnyContent] = UnAuthenticatedGet(ResetPasswordController.view(token: java.util.UUID) _)

  def postPasswordReset(token: UUID): Action[AnyContent] = UnAuthenticatedPost(ResetPasswordController.submit(token: java.util.UUID) _)

  def getPasswordChange(): Action[AnyContent] = AuthenticatedWithProviderGet(ChangePasswordController.view _)

  def postPasswordChange(): Action[AnyContent] = AuthenticatedWithProviderPost(ChangePasswordController.submit _)

  def getAccountEmail(email: String): Action[AnyContent] = UnAuthenticatedGet(ActivateAccountController.send(email) _) // TODO send email per API??

  def getAccountActivate(token: UUID): Action[AnyContent] = UnAuthenticatedGet(ActivateAccountController.activate(token: java.util.UUID) _)


  // ### Webpage
  def getWebpage(): Action[AnyContent] = {
    AuthenticatedGet(WebpageController.index _)
  }

  def getOverviewNoArgs(): Action[AnyContent] = AuthenticatedGet(WebpageController.diagramsOverviewShortInfo _)

  def getOverview(uuid: String): Action[AnyContent] = AuthenticatedGet(WebpageController.diagramsOverview(uuid) _)


  // # metamodel editor
  def getMetamodelEditor(metaModelId: UUID): Action[AnyContent] = {
    AuthenticatedGet(MetaModelController.metaModelEditor(metaModelId) _)
  }

  def getMetamodelSocket(metaModelId: UUID): WebSocket = AuthenticatedSocket(MetaModelController.metaModelSocket(metaModelId) _)


  // ### model editor
  def getModelEditor(modelId: UUID): Action[AnyContent] = {
    AuthenticatedGet(ModelController.modelEditor(modelId) _)
  }

  def getModelSocket(instanceId: String, graphType: String): WebSocket = AuthenticatedSocket(ModelController.modelSocket(instanceId, graphType) _)

  def getModelValidator(): Action[AnyContent] = AuthenticatedGet(ModelController.modelValidator _)


  // ### vr
  def getModelVrEditor(modelUuid: UUID): Action[AnyContent] = {
    AuthenticatedGet(ModelController.vrModelEditor(modelUuid) _)
  }


  // # temporary
  def getGenerate(metaModelUuid: UUID): Action[AnyContent] = {
    AuthenticatedGet(GeneratorController.generate(metaModelUuid) _)
  }

  /* ### MetaModel REST API
   * MMRA => MetaModelRestApi
   */

  def getMetamodelsNoArgs: Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.showForUser _)

  def postMetamodels: Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, MetaModelRestApi.insert _)

  def putMetamodels(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.update(metaModelId) _)

  def getMetamodels(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.get(metaModelId) _)

  def deleteMetamodels(metaModelId: UUID): Action[AnyContent] = AuthenticatedDelete(MetaModelRestApi.delete(metaModelId) _)

  def getMetamodelsDefinition(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMetaModelDefinition(metaModelId) _)

  def putMetamodelsDefinition(metaModelId: UUID): Action[JsValue] = {
    AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateMetaModelDefinition(metaModelId) _)
  }

  def getMetamodelsDefinitionMclassesNoArgs(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMClasses(metaModelId) _)

  def getMetamodelsDefinitionMreferencesNoArgs(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMReferences(metaModelId) _)

  def getMetamodelsDefinitionMclasses(metaModelId: UUID, mClassName: String): Action[AnyContent] = {
    AuthenticatedGet(MetaModelRestApi.getMClass(metaModelId, mClassName) _)
  }

  def getMetamodelsDefinitionMReferences(metaModelId: UUID, mReferenceName: String): Action[AnyContent] = {
    AuthenticatedGet(MetaModelRestApi.getMReference(metaModelId, mReferenceName) _)
  }

  def getMetamodelsShape(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getShape(metaModelId) _)

  def putMetamodelsShape(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateShape(metaModelId) _)

  def getMetamodelsStyle(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getStyle(metaModelId) _)

  def putMetamodelsStyle(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateStyle(metaModelId) _)

  def getMetamodelsDiagram(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getDiagram(metaModelId) _)

  def putMetamodelsDiagram(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateDiagram(metaModelId) _)


  /* ### Model REST API
   * MRA => ModelRestApi
   */


  def getModelsNoArgs: Action[AnyContent] = AuthenticatedGet(ModelRestApi.showForUser() _)

  def postModels: Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, ModelRestApi.insert() _)

  def putModels(modelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, ModelRestApi.update(modelId) _)

  def getModels(modelId: UUID): Action[AnyContent] = AuthenticatedGet(ModelRestApi.get(modelId) _)

  def getModelsDefinition(modelId: UUID): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getModelDefinition(modelId) _)

  def putModelsDefinition(modelId: UUID): Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, ModelRestApi.updateModel(modelId) _)

  def getModelsDefinitionNodesNoArgs(modelId: UUID): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getNodes(modelId) _)

  def getModelsDefinitionNodes(modelId: UUID, nodeName: String): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getNode(modelId, nodeName) _)

  def getModelDefinitionEdgesNoArgs(modelId: UUID): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getEdges(modelId) _)

  def getModelDefinitionEdges(modelId: UUID, edgeName: String): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getEdge(modelId, edgeName) _)

  def deleteModels(modelId: UUID): Action[AnyContent] = AuthenticatedDelete(ModelRestApi.delete(modelId) _)


  // ### Code Editor
  def getCodeeditorEditor(metaModelId: UUID, dslType: String): Action[AnyContent] = {
    AuthenticatedGet(CodeEditorController.codeEditor(metaModelId, dslType) _)
  }

  def getCodeeditorSocket(metaModelId: UUID, dslType: String): WebSocket = AuthenticatedSocket(CodeEditorController.codeSocket(metaModelId, dslType) _)


  // # Map static resources from the /public folder to the /assets URL path
  def getAssets(file: String): Action[AnyContent] = {
    Assets.at(path = "/public", file)
  }

  def getWebjars(file: String): Action[AnyContent] = WebJarAssets.at(file)

  def getMode_specific(file: String): Action[AnyContent] = AuthenticatedGet(DynamicFileController.serveFile(file) _)

}



