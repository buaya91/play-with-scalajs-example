import java.util.concurrent.Executors

import controllers.{Application, Assets, HealthController, ScriptsFacadeController}
import play.api.ApplicationLoader.Context
import play.api.{ApplicationLoader, BuiltInComponentsFromContext}
import router.Routes

import scala.concurrent.ExecutionContext

class SnakeGameLoader() extends ApplicationLoader {
  def load(context: Context) = new ApplicationComponents(context).application
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) {
  val tickEc = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
  lazy val applicationController = new Application(tickEc)(actorSystem, materializer)
  lazy val health                = new HealthController()
  lazy val assets                = new Assets(httpErrorHandler)
  lazy val scripts               = new ScriptsFacadeController()
  override lazy val router =
    new Routes(httpErrorHandler, applicationController, health, scripts, assets)
}
