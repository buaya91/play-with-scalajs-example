package client.ui.components

import client.ui.{CanvasRenderer, GameRenderer}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.{html, window}
import org.scalajs.dom.raw.CanvasRenderingContext2D
import shared.protocol.GameState

object GameCanvas {
  val canvasRef = Ref[html.Canvas]("canvas")

  def setCanvasFullScreen(canvas: html.Canvas) = {
    canvas.width = (window.innerWidth * 0.8).toInt
    canvas.height = window.innerHeight.toInt
    canvas.style.height = s"${window.innerHeight}px"
    canvas.style.width = s"${window.innerWidth * 0.8}px"
  }

  type SelfID = String
  class Backend($ : BackendScope[(GameState, SelfID), Unit]) {
    var renderer: GameRenderer = _

    def onMount(refsObject: RefsObject): Callback = Callback {
      val canvas = ReactDOM.findDOMNode(refsObject(canvasRef).get).asInstanceOf[html.Canvas]
      setCanvasFullScreen(canvas)
      renderer = new CanvasRenderer {
        override val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
      }
    }

    def onComponentUpdate(refsObject: RefsObject, state: GameState, selfID: SelfID): Callback = Callback {
      renderer.render(state, selfID)
    }

    def render() = {
      <.canvas(
        ^.id := "canvas",
        ^.tabIndex := 1,
        ^.ref := canvasRef
      )
    }
  }

  val component = ReactComponentB[(GameState, String)]("GameCanvas")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.onMount(scope.refs))
    .componentWillReceiveProps(scope => {
      val ctx = scope.$
      ctx.backend.onComponentUpdate(ctx.refs, ctx.props._1, ctx.props._2)
    })
    .build

  def apply(state: GameState, id: SelfID) = component.apply((state, id))
}
