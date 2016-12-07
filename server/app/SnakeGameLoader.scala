import controllers.{Application, Assets, DebugApp}
import play.api.ApplicationLoader.Context
import play.api.{ApplicationLoader, BuiltInComponentsFromContext}
import router.Routes

class SnakeGameLoader() extends ApplicationLoader {
  def load(context: Context) = new ApplicationComponents(context).application
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) {
  lazy val applicationController = new Application()(actorSystem, materializer)
  lazy val debugApp = new DebugApp()(actorSystem, materializer)
  lazy val assets = new Assets(httpErrorHandler)
  override lazy val router = new Routes(httpErrorHandler, applicationController, debugApp, assets)
}
