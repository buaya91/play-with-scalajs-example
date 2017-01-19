package client

import client.infrastructure.{SnakeGame, _}
import org.scalajs.dom._

import scala.scalajs.js._
import org.scalajs.dom.raw._
import shared.protocol._
import monix.execution.Scheduler.Implicits.global

object BrowserSnakeGame extends JSApp {

  private def onSubmitName(): Boolean = {
    val name = document.getElementById("username-input").asInstanceOf[HTMLInputElement].value

    if (name != null && name != "") {
      DefaultWSSource.request(JoinGame(name))
      true
    } else {
      false
    }
  }

  def initDom() = {
    JSFacade
      .JQueryStatic("#username-modal")
      .modal(Dynamic.literal(autofocus = true, onHide = () => onSubmitName()))
      .modal("setting", "closable", false)
      .modal("show")

    val nameInput = document.getElementById("username-input").asInstanceOf[HTMLInputElement]
    document.addEventListener("keydown", (ev: KeyboardEvent) => {
      if (ev.keyCode == 13) {
        JSFacade.JQueryStatic("#username-modal").modal("hide")
      }
    })
  }

  @annotation.JSExport
  override def main(): Unit = {

    val input = new KeyboardInput(document.asInstanceOf[HTMLElement])

    val game = new SnakeGame(DefaultWSSource, ClientPredictor, input)

    game.startGame()
    initDom()
  }
}
