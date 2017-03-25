package client

import boopickle.Default.Unpickle
import client.ui.components.{RetryData, WelcomePrompt}
import client.input.{InputControl, KeyboardInput}
import client.refactor._
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.vdom.prefix_<^._
import monix.execution.Ack.Continue
import org.scalajs.dom._

import scala.scalajs.js._
import shared.protocol._
import shared.serializers.Serializers._
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js.typedarray._

object BrowserSnakeGame extends JSApp {

  private var latestStateToRender = GameState.init
  private val worker              = new Worker("/assets/javascript/gameUpdate.js")

  private var showInstruction = true

  import GlobalData._

  private def onSubmitName(name: String): Unit = {
    if (name != null && !name.isEmpty) {
      userName = Some(name)
      joinedGame = true
      worker.postMessage(name)
      updatePrompt()
    }
  }

  private def updatePrompt() = {
    val popupNode = document.getElementById("popup").asInstanceOf[html.Div]
    if (!joinedGame)
      ReactDOM.render(WelcomePrompt(onSubmitName), popupNode)
    else
      ReactDOM.render(<.noscript(), popupNode)
  }

  private def updateData(input: InputControl) = {
    input.captureInputs().subscribe { key =>
      if (key == 32)
        showInstruction = false
      worker.postMessage(key)
      Continue
    }
  }

  worker.onmessage = (msg: MessageEvent) => {
    val rawBytes             = TypedArrayBuffer.wrap(msg.data.asInstanceOf[ArrayBuffer])
    val deserializedResponse = Unpickle[GameResponse].fromBytes(rawBytes)

    deserializedResponse match {
      case AssignedID(id) =>
        println("Assigned ID from worker")
        assignedID = Some(id)
      case st: GameState =>
        latestStateToRender = st
    }
  }

  private def startRenderLoop(root: html.Div): Unit = {
    window.requestAnimationFrame((_: Double) => {
      val showRetry = assignedID.exists(id => !latestStateToRender.hasSnake(id))

      val retry = RetryData(showRetry, onSubmitName, userName.getOrElse(""))

      val data = RootData(assignedID.getOrElse(""), latestStateToRender, showInstruction, retry)
      ReactDOM.render(Root(data), root)
      startRenderLoop(root)
    })
  }

  @annotation.JSExport
  override def main(): Unit = {

    val input = new KeyboardInput(document.asInstanceOf[HTMLElement])
    updateData(input)

    val root = document.getElementById("root").asInstanceOf[html.Div]

    startRenderLoop(root)

    updatePrompt()
  }
}
