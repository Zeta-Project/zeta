package de.htwg.zeta.server.routing

import java.util.UUID

import controllers.Assets
import de.htwg.zeta.server.routing.authentication.AbstractAction
import de.htwg.zeta.server.routing.authentication.AbstractWebSocket
import de.htwg.zeta.server.routing.authentication.AuthenticatedAction
import de.htwg.zeta.server.routing.authentication.AuthenticatedWebSocket
import de.htwg.zeta.server.routing.authentication.BasicAction
import de.htwg.zeta.server.routing.authentication.BasicWebSocket
import de.htwg.zeta.server.routing.authentication.UnAuthenticatedAction
import de.htwg.zeta.server.routing.authentication.UnAuthenticatedWebSocket
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent, BodyParsers, Controller, WebSocket}

/**
 */
trait RouteController extends Controller {

  protected val routeCont: RouteControllerContainer

  protected object AuthenticatedGet extends AuthenticatedAction(routeCont.abstractActionDependencies)

  protected object AuthenticatedPost extends AuthenticatedAction(routeCont.abstractActionDependencies)

  protected object AuthenticatedPut extends AuthenticatedAction(routeCont.abstractActionDependencies)

  protected object AuthenticatedDelete extends AuthenticatedAction(routeCont.abstractActionDependencies)

  protected object AuthenticatedSocket extends AuthenticatedWebSocket(routeCont.abstractWebSocketDependencies)


  protected object UnAuthenticatedGet extends UnAuthenticatedAction(routeCont.abstractActionDependencies)

  protected object UnAuthenticatedPost extends UnAuthenticatedAction(routeCont.abstractActionDependencies)

  protected object UnAuthenticatedSocket extends UnAuthenticatedWebSocket(routeCont.abstractWebSocketDependencies)


  protected object BasicGet extends BasicAction(routeCont.abstractActionDependencies)

  protected object BasicPost extends BasicAction(routeCont.abstractActionDependencies)

  protected object BasicSocket extends BasicWebSocket(routeCont.abstractWebSocketDependencies)

}

class RouteControllerContainer @Inject() private(
    val abstractWebSocketDependencies: AbstractWebSocket.Dependencies,
    val abstractActionDependencies: AbstractAction.Dependencies)

class ScalaRoutesApi @Inject()(
                                  protected val routeCont: RouteControllerContainer,
                                  protected val webCont: WebControllerContainer
                                ) extends RouteController with WebController {

  def triggerParse(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApiV2.triggerParse(metaModelId) _)

  def getMetaModelShape(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApiV2.getShape(metaModelId) _)

  def getMetaModelStyle(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApiV2.getStyle(metaModelId) _)

  def getMetaModelDiagram(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApiV2.getDiagram(metaModelId) _)
}

class ScalaRoutes @Inject()(
                             protected val routeCont: RouteControllerContainer,
                             protected val webCont: WebControllerContainer
                           ) extends RouteController with WebController {


  def getSocketDeveloper: WebSocket = AuthenticatedSocket(backendController.developer() _)

  def getSocketGenerator(id: UUID): WebSocket = AuthenticatedSocket(backendController.generator(id) _)

  def getSocketUser(modelId: UUID): WebSocket = AuthenticatedSocket(backendController.user(modelId) _)

  def getSocketConnection: WebSocket = webSocket.socket

  // # Home page
  def getIndex(): Action[AnyContent] = AuthenticatedGet(webpageController.index _)

  def getUser(): Action[AnyContent] = AuthenticatedGet(applicationController.user _)

  def getSignout: Action[AnyContent] = AuthenticatedGet(applicationController.signOut _)

  def getSignUp(): Action[AnyContent] = UnAuthenticatedGet(signUpController.view _)

  def postSignUp(): Action[AnyContent] = UnAuthenticatedPost(signUpController.submit _)

  def getSignIn(): Action[AnyContent] = UnAuthenticatedGet(signInController.view _)

  def postSignIn(): Action[AnyContent] = UnAuthenticatedPost(signInController.submit _)

  def getPasswordForgot(): Action[AnyContent] = UnAuthenticatedGet(forgotPasswordController.view _)

  def postPasswordForgot(): Action[AnyContent] = UnAuthenticatedPost(forgotPasswordController.submit _)

  def getPasswordReset(token: UUID): Action[AnyContent] = UnAuthenticatedGet(resetPasswordController.view(token: java.util.UUID) _)

  def postPasswordReset(token: UUID): Action[AnyContent] = UnAuthenticatedPost(resetPasswordController.submit(token: java.util.UUID) _)

  def getPasswordChange(): Action[AnyContent] = AuthenticatedGet(changePasswordController.view _)

  def postPasswordChange(): Action[AnyContent] = AuthenticatedPost(changePasswordController.submit _)


  def getAccountEmail(email: String): Action[AnyContent] = UnAuthenticatedGet(activateAccountController.send(email) _) // TODO send email per API??

  def getAccountActivate(token: UUID): Action[AnyContent] = UnAuthenticatedGet(activateAccountController.activate(token: java.util.UUID) _)


  // ### Webpage
  def getWebpage(): Action[AnyContent] = AuthenticatedGet(webpageController.index _)

  def getOverviewNoArgs(): Action[AnyContent] = AuthenticatedGet(webpageController.diagramsOverviewShortInfo _)

  def getOverview(id: UUID): Action[AnyContent] = AuthenticatedGet(webpageController.diagramsOverview(id) _)


  // # metamodel editor
  def getMetamodelEditor(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelController.metaModelEditor(metaModelId) _)

  def getMetamodelSocket(metaModelId: UUID): WebSocket = AuthenticatedSocket(metaModelController.metaModelSocket(metaModelId) _)


  // ### model editor
  def getModelEditor(modelId: UUID): Action[AnyContent] = AuthenticatedGet(modelController.modelEditor(modelId) _)


  /* ### MetaModel REST API
   * MMRA => MetaModelRestApi
   */

  def getMetamodelsNoArgs: Action[AnyContent] = AuthenticatedGet(metaModelRestApi.showForUser _)

  def postMetamodels: Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, metaModelRestApi.insert _)

  def putMetamodels(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, metaModelRestApi.update(metaModelId) _)

  def getMetamodels(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApi.get(metaModelId) _)

  def deleteMetamodels(metaModelId: UUID): Action[AnyContent] = AuthenticatedDelete(metaModelRestApi.delete(metaModelId) _)

  def getMetamodelsDefinition(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApi.getMetaModelDefinition(metaModelId) _)

  def putMetamodelsDefinition(metaModelId: UUID): Action[JsValue] =
    AuthenticatedPut(BodyParsers.parse.json, metaModelRestApi.update(metaModelId) _)

  def getMetamodelsDefinitionMclassesNoArgs(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApi.getMClasses(metaModelId) _)

  def getMetamodelsDefinitionMreferencesNoArgs(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApi.getMReferences(metaModelId) _)

  def getMetamodelsDefinitionMclasses(metaModelId: UUID, mClassName: String): Action[AnyContent] =
    AuthenticatedGet(metaModelRestApi.getMClass(metaModelId, mClassName) _)

  def getMetamodelsDefinitionMReferences(metaModelId: UUID, mReferenceName: String): Action[AnyContent] =
    AuthenticatedGet(metaModelRestApi.getMReference(metaModelId, mReferenceName) _)

  def getMetamodelsShape(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApi.getShape(metaModelId) _)

  def putMetamodelsShape(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, metaModelRestApi.updateShape(metaModelId) _)

  def getMetamodelsStyle(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApi.getStyle(metaModelId) _)

  def putMetamodelsStyle(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, metaModelRestApi.updateStyle(metaModelId) _)

  def getMetamodelsDiagram(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(metaModelRestApi.getDiagram(metaModelId) _)

  def putMetamodelsDiagram(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, metaModelRestApi.updateDiagram(metaModelId) _)

  def getMetamodelsValidator(metaModelId: UUID, generate: Option[Boolean]): Action[AnyContent] =
    AuthenticatedGet(metaModelRestApi.getValidator(metaModelId, generate, get = true) _)

  def headMetaModelsValidator(metaModelId: UUID, generate: Option[Boolean]): Action[AnyContent] =
    AuthenticatedGet(metaModelRestApi.getValidator(metaModelId, generate, get = false) _)

  def putMetaModelsClassMethod(metaModelId: UUID, className: String, methodName: String): Action[AnyContent] =
    AuthenticatedPut(metaModelRestApi.updateClassMethodCode(metaModelId, className, methodName) _)

  def putMetaModelsReferenceMethod(metaModelId: UUID, referenceName: String, methodName: String): Action[AnyContent] =
    AuthenticatedPut(metaModelRestApi.updateReferenceMethodCode(metaModelId, referenceName, methodName) _)

  def putMetaModelsMainMethod(metaModelId: UUID, methodName: String): Action[AnyContent] =
    AuthenticatedPut(metaModelRestApi.updateCommonMethodCode(metaModelId, methodName) _)

  /* ### Model REST API
   * MRA => ModelRestApi
   */


  def getModelsNoArgs: Action[AnyContent] = AuthenticatedGet(modelRestApi.showForUser() _)

  def postModels: Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, modelRestApi.insert() _)

  def putModels(modelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, modelRestApi.update(modelId) _)

  def getModels(modelId: UUID): Action[AnyContent] = AuthenticatedGet(modelRestApi.get(modelId) _)

  def getModelsDefinition(modelId: UUID): Action[AnyContent] = AuthenticatedGet(modelRestApi.getModelDefinition(modelId) _)

  def putModelsDefinition(modelId: UUID): Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, modelRestApi.updateModel(modelId) _)

  def getModelsDefinitionNodesNoArgs(modelId: UUID): Action[AnyContent] = AuthenticatedGet(modelRestApi.getNodes(modelId) _)

  def getModelsDefinitionNodes(modelId: UUID, nodeName: String): Action[AnyContent] = AuthenticatedGet(modelRestApi.getNode(modelId, nodeName) _)

  def getModelDefinitionEdgesNoArgs(modelId: UUID): Action[AnyContent] = AuthenticatedGet(modelRestApi.getEdges(modelId) _)

  def getModelDefinitionEdges(modelId: UUID, edgeName: String): Action[AnyContent] = AuthenticatedGet(modelRestApi.getEdge(modelId, edgeName) _)

  def deleteModels(modelId: UUID): Action[AnyContent] = AuthenticatedDelete(modelRestApi.delete(modelId) _)

  def getModelsValidation(modelId: UUID): Action[AnyContent] = AuthenticatedGet(modelRestApi.getValidation(modelId) _)

  def downloadSourceCode(modelId: java.util.UUID): Action[AnyContent] = AuthenticatedGet(modelRestApi.downloadSourceCode(modelId) _)

  def exportProject(gdslProjectId: java.util.UUID): Action[AnyContent] = AuthenticatedGet(modelRestApi.exportProject(gdslProjectId) _)

  def importProject(): Action[AnyContent] = AuthenticatedPost(modelRestApi.importProject() _)

  def inviteToProject(id: UUID, email: String): Action[AnyContent] = AuthenticatedGet(metaModelRestApi.inviteUser(id, email) _)

  def duplicateProject(id: UUID, name: String): Action[AnyContent] = AuthenticatedGet(metaModelRestApi.duplicate(id, name) _)


  /* ### Generator Image REST API */
  def getGeneratorImagesNoArgs: Action[AnyContent] = AuthenticatedGet(generatorImageRestApi.showForUser() _)

  /* ### Generator REST API */
  def getGeneratorsNoArgs: Action[AnyContent] = AuthenticatedGet(generatorRestApi.showForUser() _)
  def getGenerators(id: UUID): Action[AnyContent] = AuthenticatedGet(generatorRestApi.get(id) _)
  def deleteGenerators(id: UUID): Action[AnyContent] = AuthenticatedGet(generatorRestApi.delete(id) _)

  /* ### Filter REST API */
  def getFiltersNoArgs: Action[AnyContent] = AuthenticatedGet(filterRestApi.showForUser() _)
  def getFilters(id: UUID): Action[AnyContent] = AuthenticatedGet(filterRestApi.get(id) _)
  def deleteFilters(id: UUID): Action[AnyContent] = AuthenticatedGet(filterRestApi.delete(id) _)
  def postFilters: Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, filterRestApi.insert _)

  /* ### Filter REST API */
  def getMetaModelReleasesNoArgs: Action[AnyContent] = AuthenticatedGet(metaModelReleaseRestApi.showForUser() _)

  /* ### BondedTask REST API */
  def getBondedTasksNoArgs: Action[AnyContent] = AuthenticatedGet(bondedTaskRestApi.showForUser() _)
  def deleteBondedTasks(id: UUID): Action[AnyContent] = AuthenticatedGet(bondedTaskRestApi.delete(id) _)
  def postBondedTasks: Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, bondedTaskRestApi.insert _)

  /* ### EventDrivenTask REST API */
  def getEventDrivenTasksNoArgs: Action[AnyContent] = AuthenticatedGet(eventDrivenTaskRestApi.showForUser() _)
  def deleteEventDrivenTasks(id: UUID): Action[AnyContent] = AuthenticatedGet(eventDrivenTaskRestApi.delete(id) _)
  def postEventDrivenTasks: Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, eventDrivenTaskRestApi.insert _)

  /* ### TimedTask REST API */
  def getTimedTasksNoArgs: Action[AnyContent] = AuthenticatedGet(timedTaskRestApi.showForUser() _)
  def deleteTimedTasks(id: UUID): Action[AnyContent] = AuthenticatedGet(timedTaskRestApi.delete(id) _)
  def postTimedTasks: Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, timedTaskRestApi.insert _)

  /* ### File REST API */
  def getFiles(id: UUID, name: String): Action[AnyContent] = AuthenticatedGet(fileRestApi.get(id, name) _)
  def putFiles(id: UUID, name: String): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, fileRestApi.update(id, name) _)

  /* ### DSL REST API */
  def getDslV1(id: UUID, apiType: String): Action[AnyContent] = AuthenticatedGet(dslRestApi.getDSL(id, apiType) _)
  def getAllDslV1(id: UUID): Action[AnyContent] = AuthenticatedGet(dslRestApi.getTotalApiV1(id) _)


  // ### Code Editor
  def getCodeEditor(metaModelId: UUID, dslType: String): Action[AnyContent] =
    AuthenticatedGet(codeEditorController.codeEditor(metaModelId, dslType) _)

  // # Map static resources from the /public folder to the /assets URL path
  def getAssets(file: String): Action[AnyContent] = Assets.at(path = "/public", file)

  def getWebjars(file: String): Action[AnyContent] = webJarAssets.at(file)

  def getMode_specific(id: UUID, name: String): Action[AnyContent] = AuthenticatedGet(dynamicFileController.serveFile(id, name) _)

  def getWebApp(path: String): Action[AnyContent] = AuthenticatedGet(webAppController.get(path) _)

  def getStaticFiles(path: String): Action[AnyContent] = webAppController.static(path)

  def getScalaCodeViewer(modelId: UUID): Action[AnyContent] = AuthenticatedGet(modelRestApi.getScalaCodeViewer(modelId) _)

  def getKlimaCodeViewer(modelId: UUID): Action[AnyContent] = AuthenticatedGet(modelRestApi.getKlimaCodeViewer(modelId) _)

  def getMethodClassCodeEditor(metaModelId: UUID, methodName: String, className: String) = AuthenticatedGet(codeEditorController.methodClassCodeEditor(metaModelId, methodName, className) _)

  def getMethodReferenceCodeEditor(metaModelId: UUID, methodName: String, referenceName: String) = AuthenticatedGet(codeEditorController.methodReferenceCodeEditor(metaModelId, methodName, referenceName) _)

  def getMethodCommonCodeEditor(metaModelId: UUID, methodName: String) = AuthenticatedGet(codeEditorController.methodMainCodeEditor(metaModelId, methodName) _)

}