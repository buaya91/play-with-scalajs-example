package controllers

import play.api.mvc.{Action, Controller}

class Paper extends Controller {
  def paper = Action {
    Ok(views.html.test())
  }
}
