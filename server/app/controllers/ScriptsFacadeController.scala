package controllers

import controllers.Assets.Asset
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class ScriptsFacadeController extends Controller {
  private def resourceExist(name: String) = {
    getClass.getResource(s"/public/$name") != null
  }

  def scripts = Action.async { implicit request =>

    val route = Seq("client-opt.js", "client-fastopt.js").find(resourceExist).map(Asset(_))

    route match {
      case Some(r) =>
        controllers.Assets.versioned("/public", r).apply(request)
      case None =>
        Future.successful(InternalServerError("Javascript not found"))
    }
  }
}
