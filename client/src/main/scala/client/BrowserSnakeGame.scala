package client

import client.domain.{AuthorityState, InputControl}
import client.infrastructure.views.WelcomePrompt
import client.infrastructure._
import client.refactor.{GameLoop, GlobalData, Root, RootData}
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.vdom.prefix_<^._
import monix.execution.Ack.Continue
import org.scalajs.dom._

import scala.scalajs.js._
import org.scalajs.dom.raw._
import shared.protocol._
import monix.execution.Scheduler.Implicits.global

object BrowserSnakeGame extends JSApp {
  var showPrompt = true

  import GlobalData._
  private def onSubmitName(name: String): Unit = {
    if (name != null && !name.isEmpty) {
      userName = Some(name)
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

  def updateData(input: InputControl, serverData: AuthorityState) = {
    serverData.stream().subscribe { res =>
      res match {
        case x: GameState =>
          predictedState.lastOption.foreach(p => {
            if (x.seqNo >= p._1) {
              println(s"received: ${x.seqNo} predicted: ${p._1}")
            }
          })
          serverStateQueue += x.seqNo -> x
        case AssignedID(id) =>
          assignedID = Some(id)
      }
      Continue
    }

    input.captureInputs().subscribe { fn =>
      predictedState.lastOption.foreach {
        case (key, _) =>
          val nextK = key + 1
          val i = fn(nextK)
          serverData.request(i)
          unackInput = unackInput + (nextK -> i)
      }
      Continue
    }
  }

  @annotation.JSExport
  override def main(): Unit = {

    val input = new KeyboardInput(document.asInstanceOf[HTMLElement])

    updateData(input, DefaultWSSource)

    val state = GameLoop.start()
    val root  = document.getElementById("root").asInstanceOf[html.Div]

    state.subscribe(st => {
      val data = RootData(true, GlobalData.assignedID.getOrElse(""), st)
      ReactDOM.render(Root(data), root)
      Continue
    })

//    val game = new SnakeGame(DefaultWSSource, ClientPredictor, input)
//
//    game.startGame()

    initDom()
  }
}
