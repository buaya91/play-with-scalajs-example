package client

import client.infrastructure.views.WelcomePrompt
import client.infrastructure.{SnakeGame, _}
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom._

import scala.scalajs.js._
import org.scalajs.dom.raw._
import shared.protocol._
import monix.execution.Scheduler.Implicits.global

object BrowserSnakeGame extends JSApp {
  var showPrompt = true

  private def onSubmitName(name: String): Unit = {
    if (name != null && !name.isEmpty) {
      DefaultWSSource.request(JoinGame(name))
      showPrompt = false
      initDom()
    }
  }

  def initDom() = {
    val popupNode = document.getElementById("popup").asInstanceOf[html.Div]
    if (showPrompt)
      ReactDOM.render(WelcomePrompt(onSubmitName), popupNode)
    else
      ReactDOM.render(<.noscript(), popupNode)
  }

  @annotation.JSExport
  override def main(): Unit = {

    val input = new KeyboardInput(document.asInstanceOf[HTMLElement])

    val game = new SnakeGame(DefaultWSSource, ClientPredictor, input)

    game.startGame()
    initDom()
  }
}
