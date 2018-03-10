package controllers

import java.util.UUID
import javax.inject.Inject

import de.htwg.zeta.server.routing.RouteController
import de.htwg.zeta.server.routing.RouteControllerContainer
import de.htwg.zeta.server.routing.WebController
import de.htwg.zeta.server.routing.WebControllerContainer
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.BodyParsers
import play.api.mvc.WebSocket

// scalastyle:off number.of.methods

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
  def getIndex(): Action[AnyContent] = AuthenticatedGet(ApplicationController.index _)

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

  def getPasswordChange(): Action[AnyContent] = AuthenticatedGet(ChangePasswordController.view _)

  def postPasswordChange(): Action[AnyContent] = AuthenticatedPost(ChangePasswordController.submit _)


  def getAccountEmail(email: String): Action[AnyContent] = UnAuthenticatedGet(ActivateAccountController.send(email) _) // TODO send email per API??

  def getAccountActivate(token: UUID): Action[AnyContent] = UnAuthenticatedGet(ActivateAccountController.activate(token: java.util.UUID) _)


  // ### Webpage
  def getWebpage(): Action[AnyContent] = AuthenticatedGet(WebpageController.index _)

  def getOverviewNoArgs(): Action[AnyContent] = AuthenticatedGet(WebpageController.diagramsOverviewShortInfo _)

  def getOverview(id: UUID): Action[AnyContent] = AuthenticatedGet(WebpageController.diagramsOverview(id) _)


  // # metamodel editor
  def getMetamodelEditor(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelController.metaModelEditor(metaModelId) _)

  def getMetamodelSocket(metaModelId: UUID): WebSocket = AuthenticatedSocket(MetaModelController.metaModelSocket(metaModelId) _)


  // ### model editor
  def getModelEditor(modelId: UUID): Action[AnyContent] = AuthenticatedGet(ModelController.modelEditor(modelId) _)


  // # temporary
  def getGenerate(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(GeneratorController.generate(metaModelId) _)

  /* ### MetaModel REST API
   * MMRA => MetaModelRestApi
   */

  def getMetamodelsNoArgs: Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.showForUser _)

  def postMetamodels: Action[JsValue] = AuthenticatedPost(BodyParsers.parse.json, MetaModelRestApi.insert _)

  def putMetamodels(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.update(metaModelId) _)

  def getMetamodels(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.get(metaModelId) _)

  def deleteMetamodels(metaModelId: UUID): Action[AnyContent] = AuthenticatedDelete(MetaModelRestApi.delete(metaModelId) _)

  def getMetamodelsDefinition(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMetaModelDefinition(metaModelId) _)

  def putMetamodelsDefinition(metaModelId: UUID): Action[JsValue] =
    AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.update(metaModelId) _)

  def getMetamodelsDefinitionMclassesNoArgs(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMClasses(metaModelId) _)

  def getMetamodelsDefinitionMreferencesNoArgs(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getMReferences(metaModelId) _)

  def getMetamodelsDefinitionMclasses(metaModelId: UUID, mClassName: String): Action[AnyContent] =
    AuthenticatedGet(MetaModelRestApi.getMClass(metaModelId, mClassName) _)

  def getMetamodelsDefinitionMReferences(metaModelId: UUID, mReferenceName: String): Action[AnyContent] =
    AuthenticatedGet(MetaModelRestApi.getMReference(metaModelId, mReferenceName) _)

  def getMetamodelsShape(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getShape(metaModelId) _)

  def putMetamodelsShape(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateShape(metaModelId) _)

  def getMetamodelsStyle(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getStyle(metaModelId) _)

  def putMetamodelsStyle(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateStyle(metaModelId) _)

  def getMetamodelsDiagram(metaModelId: UUID): Action[AnyContent] = AuthenticatedGet(MetaModelRestApi.getDiagram(metaModelId) _)

  def putMetamodelsDiagram(metaModelId: UUID): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, MetaModelRestApi.updateDiagram(metaModelId) _)

  def getMetamodelsValidator(metaModelId: UUID, generate: Option[Boolean]): Action[AnyContent] =
    AuthenticatedGet(MetaModelRestApi.getValidator(metaModelId, generate, get = true) _)

  def headMetaModelsValidator(metaModelId: UUID, generate: Option[Boolean]): Action[AnyContent] =
    AuthenticatedGet(MetaModelRestApi.getValidator(metaModelId, generate, get = false) _)

  def putMetaModelsClassMethod(metaModelId: UUID, className: String, methodName: String): Action[AnyContent] =
    AuthenticatedPut(MetaModelRestApi.updateClassMethodCode(metaModelId, className, methodName) _)

  def putMetaModelsReferenceMethod(metaModelId: UUID, referenceName: String, methodName: String): Action[AnyContent] =
    AuthenticatedPut(MetaModelRestApi.updateReferenceMethodCode(metaModelId, referenceName, methodName) _)

  def putMetaModelsMainMethod(metaModelId: UUID, methodName: String): Action[AnyContent] =
    AuthenticatedPut(MetaModelRestApi.updateCommonMethodCode(metaModelId, methodName) _)

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

  def getModelsValidation(modelId: UUID): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getValidation(modelId) _)

  /* ### Generator Image REST API */
  def getGeneratorImagesNoArgs: Action[AnyContent] = AuthenticatedGet(GeneratorImageRestApi.showForUser() _)

  /* ### Generator REST API */
  def getGeneratorsNoArgs: Action[AnyContent] = AuthenticatedGet(GeneratorRestApi.showForUser() _)
  def getGenerators(id: UUID): Action[AnyContent] = AuthenticatedGet(GeneratorRestApi.get(id) _)
  def deleteGenerators(id: UUID): Action[AnyContent] = AuthenticatedGet(GeneratorRestApi.delete(id) _)

  /* ### Filter REST API */
  def getFiltersNoArgs: Action[AnyContent] = AuthenticatedGet(FilterRestApi.showForUser() _)
  def getFilters(id: UUID): Action[AnyContent] = AuthenticatedGet(FilterRestApi.get(id) _)
  def deleteFilters(id: UUID): Action[AnyContent] = AuthenticatedGet(FilterRestApi.delete(id) _)
  def postFilters: Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, FilterRestApi.insert _)

  /* ### Filter REST API */
  def getMetaModelReleasesNoArgs: Action[AnyContent] = AuthenticatedGet(MetaModelReleaseRestApi.showForUser() _)

  /* ### BondedTask REST API */
  def getBondedTasksNoArgs: Action[AnyContent] = AuthenticatedGet(BondedTaskRestApi.showForUser() _)
  def deleteBondedTasks(id: UUID): Action[AnyContent] = AuthenticatedGet(BondedTaskRestApi.delete(id) _)
  def postBondedTasks: Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, BondedTaskRestApi.insert _)

  /* ### EventDrivenTask REST API */
  def getEventDrivenTasksNoArgs: Action[AnyContent] = AuthenticatedGet(EventDrivenTaskRestApi.showForUser() _)
  def deleteEventDrivenTasks(id: UUID): Action[AnyContent] = AuthenticatedGet(EventDrivenTaskRestApi.delete(id) _)
  def postEventDrivenTasks: Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, EventDrivenTaskRestApi.insert _)

  /* ### TimedTask REST API */
  def getTimedTasksNoArgs: Action[AnyContent] = AuthenticatedGet(TimedTaskRestApi.showForUser() _)
  def deleteTimedTasks(id: UUID): Action[AnyContent] = AuthenticatedGet(TimedTaskRestApi.delete(id) _)
  def postTimedTasks: Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, TimedTaskRestApi.insert _)

  /* ### File REST API */
  def getFiles(id: UUID, name: String): Action[AnyContent] = AuthenticatedGet(FileRestApi.get(id, name) _)
  def putFiles(id: UUID, name: String): Action[JsValue] = AuthenticatedPut(BodyParsers.parse.json, FileRestApi.update(id, name) _)
  /* ### DSL REST API */
  def getDslV1(id: UUID, apiType: String): Action[AnyContent] = AuthenticatedGet(DslRestApi.getV1(id, apiType) _)
  def getDslV2(id: UUID, apiType: String): Action[AnyContent] = AuthenticatedGet(DslRestApi.getV2(id, apiType) _)
  def getAllDslV1(id: UUID): Action[AnyContent] = AuthenticatedGet(DslRestApi.getTotalApiV1(id) _)


  // ### Code Editor
  def getCodeEditor(metaModelId: UUID, dslType: String): Action[AnyContent] =
    AuthenticatedGet(CodeEditorController.codeEditor(metaModelId, dslType) _)

  // # Map static resources from the /public folder to the /assets URL path
  def getAssets(file: String): Action[AnyContent] = Assets.at(path = "/public", file)

  def getWebjars(file: String): Action[AnyContent] = WebJarAssets.at(file)

  def getMode_specific(id: UUID, name: String): Action[AnyContent] = AuthenticatedGet(DynamicFileController.serveFile(id, name) _)

  def getWebApp(path: String): Action[AnyContent] = AuthenticatedGet(WebAppController.get(path) _)

  def getStaticFiles(path: String): Action[AnyContent] = WebAppController.static(path)

  def getScalaCodeViewer(modelId: UUID): Action[AnyContent] = AuthenticatedGet(ModelRestApi.getScalaCodeViewer(modelId) _)

  def getMethodClassCodeEditor(metaModelId: UUID, methodName: String, className: String) = AuthenticatedGet(CodeEditorController.methodClassCodeEditor(metaModelId, methodName, className) _)

  def getMethodReferenceCodeEditor(metaModelId: UUID, methodName: String, referenceName: String) = AuthenticatedGet(CodeEditorController.methodReferenceCodeEditor(metaModelId, methodName, referenceName) _)

  def getMethodCommonCodeEditor(metaModelId: UUID, methodName: String) = AuthenticatedGet(CodeEditorController.methodMainCodeEditor(metaModelId, methodName) _)

}
