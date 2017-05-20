package controllers

import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import de.htwg.zeta.server.authentication.BasicAction
import de.htwg.zeta.server.authentication.UnAuthenticatedAction
import de.htwg.zeta.server.authentication.AuthenticatedAction
import de.htwg.zeta.server.authentication.BasicWebSocket
import de.htwg.zeta.server.authentication.AuthenticatedWebSocket
import de.htwg.zeta.server.authentication.UnAuthenticatedWebSocket
import de.htwg.zeta.server.controller.ActivateAccountController
import de.htwg.zeta.server.controller.ApplicationController
import de.htwg.zeta.server.controller.BackendController
import de.htwg.zeta.server.controller.ChangePasswordController
import de.htwg.zeta.server.controller.CodeEditorController
import de.htwg.zeta.server.controller.DynamicFileController
import de.htwg.zeta.server.controller.ForgotPasswordController
import de.htwg.zeta.server.controller.GeneratorController
import de.htwg.zeta.server.controller.MetaModelController
import de.htwg.zeta.server.controller.ModelController
import de.htwg.zeta.server.controller.ResetPasswordController
import de.htwg.zeta.server.controller.SignInController
import de.htwg.zeta.server.controller.SignUpController
import de.htwg.zeta.server.controller.SocialAuthController
import de.htwg.zeta.server.controller.restApi.MetaModelRestApi
import de.htwg.zeta.server.controller.restApi.ModelRestApi
import de.htwg.zeta.server.controller.webpage.WebpageController
import de.htwg.zeta.server.util.auth.ZetaEnv
import de.htwg.zeta.server.util.auth.WithProvider
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import play.api.mvc.WebSocket
import play.api.mvc.Action
import play.api.mvc.AnyContent

/**
 * All routes are managed in this class
 */
class ScalaRoutes @Inject()(
    messagesApi: MessagesApi,
    silhouette: Silhouette[ZetaEnv],
    system: ActorSystem,
    mat: Materializer,
    injector: Injector // TODO don't inject Injector. Replace with multiple provider
) extends Controller {

  private object AuthenticatedGet extends AuthenticatedAction(messagesApi, silhouette)

  private object AuthenticatedPost extends AuthenticatedAction(messagesApi, silhouette)

  private object AuthenticatedSocket extends AuthenticatedWebSocket(system, silhouette, mat)

  private lazy val authorization: Option[Authorization[ZetaEnv#I, ZetaEnv#A]] = Some(WithProvider[ZetaEnv#A](CredentialsProvider.ID))

  private object AuthenticatedWithProviderGet extends AuthenticatedAction(messagesApi, silhouette, authorization)

  private object AuthenticatedWithProviderPost extends AuthenticatedAction(messagesApi, silhouette, authorization)


  private object UnAuthenticatedGet extends UnAuthenticatedAction(messagesApi, silhouette)

  private object UnAuthenticatedPost extends UnAuthenticatedAction(messagesApi, silhouette)

  private object UnAuthenticatedSocket extends UnAuthenticatedWebSocket(system, silhouette, mat)


  private object BasicGet extends BasicAction(messagesApi, silhouette)

  private object BasicPost extends BasicAction(messagesApi, silhouette)

  private object BasicSocket extends BasicWebSocket(system, silhouette, mat)


  // TODO replace injector with provider

  private lazy val BackendController: BackendController = injector.instanceOf[BackendController]
  private lazy val ApplicationController: ApplicationController = injector.instanceOf[ApplicationController]
  private lazy val SocialAuthController: SocialAuthController = injector.instanceOf[SocialAuthController]
  private lazy val SignUpController: SignUpController = injector.instanceOf[SignUpController]
  private lazy val SignInController: SignInController = injector.instanceOf[SignInController]
  private lazy val ForgotPasswordController: ForgotPasswordController = injector.instanceOf[ForgotPasswordController]
  private lazy val ResetPasswordController: ResetPasswordController = injector.instanceOf[ResetPasswordController]
  private lazy val ChangePasswordController: ChangePasswordController = injector.instanceOf[ChangePasswordController]
  private lazy val ActivateAccountController: ActivateAccountController = injector.instanceOf[ActivateAccountController]
  private lazy val WebpageController: WebpageController = injector.instanceOf[WebpageController]
  private lazy val MetaModelController: MetaModelController = injector.instanceOf[MetaModelController]
  private lazy val ModelController: ModelController = injector.instanceOf[ModelController]
  private lazy val GeneratorController: GeneratorController = injector.instanceOf[GeneratorController]
  private lazy val CodeEditorController: CodeEditorController = injector.instanceOf[CodeEditorController]
  private lazy val WebJarAssets: WebJarAssets = injector.instanceOf[WebJarAssets]
  private lazy val DynamicFileController: DynamicFileController = injector.instanceOf[DynamicFileController]


  def backendDeveloper: WebSocket = BackendController.developer

  def backendGenerator(id: String): WebSocket = BackendController.generator(id)

  def backendUser(model: String): WebSocket = BackendController.user(model)


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

  def metaModelSocket(metaModelUuid: String): WebSocket = MetaModelController.metaModelSocket(metaModelUuid)


  // ### model editor
  def modelEditor(metaModelUuid: String, modelUuid: String): Action[AnyContent] = AuthenticatedGet(ModelController.modelEditor(metaModelUuid, modelUuid) _)

  def modelSocket(instanceId: String, graphType: String): WebSocket = ModelController.modelSocket(instanceId, graphType)

  def modelValidator(): Action[AnyContent] = AuthenticatedGet(ModelController.modelValidator _)


  // ### vr
  def vrModelEditor(metaModelUuid: String, modelUuid: String): Action[AnyContent] = AuthenticatedGet(ModelController.vrModelEditor(metaModelUuid, modelUuid) _)


  // # temporary
  def generate(metaModelUuid: String): Action[AnyContent] = AuthenticatedGet(GeneratorController.generate(metaModelUuid) _)

  /* ### MetaModel REST API
   * MMRA => MetaModelRestApi
   */
  private lazy val MetaModelRestApi: MetaModelRestApi = injector.instanceOf[MetaModelRestApi]

  def MMRAshowForUser: Action[AnyContent] = MetaModelRestApi.showForUser

  def MMRAinsert: Action[JsValue] = MetaModelRestApi.insert

  def MMRAupdate(metaModelId: String): Action[JsValue] = MetaModelRestApi.update(metaModelId)

  def MMRAget(metaModelId: String): Action[AnyContent] = MetaModelRestApi.get(metaModelId)

  def MMRAdelete(metaModelId: String): Action[AnyContent] = MetaModelRestApi.delete(metaModelId)

  def MMRAgetMetaModelDefinition(metaModelId: String): Action[AnyContent] = MetaModelRestApi.getMetaModelDefinition(metaModelId)

  def MMRAupdateMetaModelDefinition(metaModelId: String): Action[JsValue] = MetaModelRestApi.updateMetaModelDefinition(metaModelId)

  def MMRAgetMClasses(metaModelId: String): Action[AnyContent] = MetaModelRestApi.getMClasses(metaModelId)

  def MMRAgetMReferences(metaModelId: String): Action[AnyContent] = MetaModelRestApi.getMReferences(metaModelId)

  def MMRAgetMClass(metaModelId: String, mClassName: String): Action[AnyContent] = MetaModelRestApi.getMClass(metaModelId, mClassName)

  def MMRAgetMReference(metaModelId: String, mReferenceName: String): Action[AnyContent] = MetaModelRestApi.getMReference(metaModelId, mReferenceName)

  def MMRAgetShape(metaModelId: String): Action[AnyContent] = MetaModelRestApi.getShape(metaModelId)

  def MMRAupdateShape(metaModelId: String): Action[JsValue] = MetaModelRestApi.updateShape(metaModelId)

  def MMRAgetStyle(metaModelId: String): Action[AnyContent] = MetaModelRestApi.getStyle(metaModelId)

  def MMRAupdateStyle(metaModelId: String): Action[JsValue] = MetaModelRestApi.updateStyle(metaModelId)

  def MMRAgetDiagram(metaModelId: String): Action[AnyContent] = MetaModelRestApi.getDiagram(metaModelId)

  def MMRAupdateDiagram(metaModelId: String): Action[JsValue] = MetaModelRestApi.updateDiagram(metaModelId)


  /* ### Model REST API
   * MRA => ModelRestApi
   */
  private lazy val ModelRestApi: ModelRestApi = injector.instanceOf[ModelRestApi]

  def MRAshowForUser: Action[AnyContent] = ModelRestApi.showForUser

  def MRAinsert: Action[JsValue] = ModelRestApi.insert

  def MRAupdate(modelId: String): Action[JsValue] = ModelRestApi.update(modelId)

  def MRAget(modelId: String): Action[AnyContent] = ModelRestApi.get(modelId)

  def MRAgetModelDefinition(modelId: String): Action[AnyContent] = ModelRestApi.getModelDefinition(modelId)

  def MRAupdateModel(modelId: String): Action[JsValue] = ModelRestApi.updateModel(modelId)

  def MRAgetNodes(modelId: String): Action[AnyContent] = ModelRestApi.getNodes(modelId)

  def MRAgetNode(modelId: String, nodeName: String): Action[AnyContent] = ModelRestApi.getNode(modelId, nodeName)

  def MRAgetEdges(modelId: String): Action[AnyContent] = ModelRestApi.getEdges(modelId)

  def MRAgetEdge(modelId: String, edgeName: String): Action[AnyContent] = ModelRestApi.getEdge(modelId, edgeName)

  def MRAdelete(modelId: String): Action[AnyContent] = ModelRestApi.delete(modelId)


  // ### Code Editor
  def codeEditor(metaModelUuid: String, dslType: String): Action[AnyContent] = CodeEditorController.codeEditor(metaModelUuid, dslType)

  def codeEditorSocket(metaModelUuid: String, dslType: String): WebSocket = CodeEditorController.codeSocket(metaModelUuid, dslType)


  // # Map static resources from the /public folder to the /assets URL path
  def assetsAt(file: String): Action[AnyContent] = Assets.at(path = "/public", file)

  def webJarAssetsAt(file: String): Action[AnyContent] = WebJarAssets.at(file)

  def serveDynamicFile(file: String): Action[AnyContent] = DynamicFileController.serveFile(file)


}
