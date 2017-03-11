package client

import client.domain.{AuthorityState, InputControl}
import client.infrastructure.views.{RetryData, WelcomePrompt}
import client.infrastructure._
import client.refactor._
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.vdom.prefix_<^._
import monix.execution.Ack.Continue
import org.scalajs.dom._

import scala.scalajs.js._
import org.scalajs.dom.raw._
import shared.protocol._
import monix.execution.Scheduler.Implicits.global

object BrowserSnakeGame extends JSApp {

  import GlobalData._
  private def onSubmitName(name: String): Unit = {
    if (name != null && !name.isEmpty) {
      userName = Some(name)
//      metaStatus = metaStatus.copy(joinedGame = true)
      joinedGame = true
      DefaultWSSource.request(JoinGame(name))
      updatePrompt()
    }
  }

  def updatePrompt() = {
    val popupNode = document.getElementById("popup").asInstanceOf[html.Div]
    if (!joinedGame)
      ReactDOM.render(WelcomePrompt(onSubmitName), popupNode)
    else
      ReactDOM.render(<.noscript(), popupNode)
  }

  def updateData(input: InputControl, serverData: AuthorityState) = {
    serverData.stream().subscribe { res =>
      res match {
        case st: GameState =>
          predictedState.lastOption.foreach(p => println(s"Delay: ${p._1 - st.seqNo}"))

          serverStateQueue += st.seqNo -> st

        case AssignedID(id) =>
          assignedID = Some(id)
//          metaStatus = metaStatus.copy(joinedGame = true, dead = false)
      }
      Continue
    }

    input.captureInputs().subscribe { fn =>
      predictedState.lastOption.foreach {
        case (key, _) =>
          val nextK = key + 1
          val i     = fn(nextK)
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
//      assignedID.foreach(id => {
//        if (metaStatus.joinedGame && !st.snakes.exists(_.id == id))
//          metaStatus = metaStatus.die()
//      })

      val showRetry = assignedID.exists(id => !st.snakes.exists(_.id == id))

      val retry   = RetryData(showRetry, onSubmitName, userName.getOrElse(""))

      val data    = RootData(assignedID.getOrElse(""), st, false, retry)
      ReactDOM.render(Root(data), root)
      Continue
    })

    updatePrompt()
  }
}
