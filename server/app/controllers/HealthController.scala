package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

class HealthController extends Controller {
  def health = Action {
    Ok(Json.obj("status" -> "Ok"))
  }
}
