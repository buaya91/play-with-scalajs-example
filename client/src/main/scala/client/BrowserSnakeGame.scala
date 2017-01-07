package client

import client.api.SnakeGame
//import client.debug.{DebugPanel, DebugRenderer, DebugSource}
import client.infrastructure._
import org.scalajs.dom._

import scala.scalajs.js._
import org.scalajs.dom.raw._
import shared.protocol._

import monix.execution.Scheduler.Implicits.global

object BrowserSnakeGame extends JSApp {

  def onSubmitName() = {
    val name = document.getElementById("username-input").asInstanceOf[HTMLInputElement].value
    DefaultWSSource.request(JoinGame(name))
    println("Joined!")
    true
  }

  def initDom() = {
    JSFacade
      .JQueryStatic("#username-modal")
      .modal(Dynamic.literal(autofocus = true, onHide = () => onSubmitName()))
      .modal("show")

    val nameInput = document.getElementById("username-input").asInstanceOf[HTMLInputElement]
    document.addEventListener("keydown", (ev: KeyboardEvent) => {
      if (ev.keyCode == 13) {
        JSFacade
          .JQueryStatic("#username-modal")
          .modal("hide")
      }
    })
  }

  def setCanvasFullScreen(canvas: html.Canvas) = {
    canvas.width = window.innerWidth.toInt
    canvas.height = window.innerHeight.toInt
    canvas.style.height = s"${window.innerHeight}px"
    canvas.style.width = s"${window.innerWidth}px"
  }

  @annotation.JSExport
  override def main(): Unit = {
    // dom related
    val canvas    = document.getElementById("canvas").asInstanceOf[html.Canvas]
    val canvasCtx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    // dependency instantiation
    val renderer = new CanvasRenderer {
      override val ctx = canvasCtx
    }

    val input = new KeyboardInput(document.asInstanceOf[HTMLElement])

    val game = new SnakeGame(DefaultWSSource, renderer, ClientPredictor, input)

    setCanvasFullScreen(canvas)

    game.startGame()
    initDom()
  }

//  @annotation.JSExport
//  def debugMain(): Unit = {
//    val serverSrc = DebugSource.src()
//
//    val canvas = document.getElementById("canvas").asInstanceOf[html.Canvas]
//
//    val ctx           = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
//    val debugRenderer = new DebugRenderer(ctx)
//
//    val gameState = serverSrc.collect {
//      case x: GameState => x
//    }
//
//    val assignedID = serverSrc.collect {
//      case x: AssignedID => x
//    }
//
//    gameState.flatMap(state => assignedID.map(a => (a.id, state))).foreach {
//      case (id, state) => debugRenderer.render(state, id)
//    }
//
//    serverSrc.subscribe()
//    addDebugPanel()
//  }
//
//  def addDebugPanel(): Unit = {
//    document.body.appendChild(DebugPanel(DebugSource.send).render)
//  }
}
