package client

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

object JSFacade {
  @js.native
  @JSName("jQuery")
  object JQueryStatic extends js.Object {
    def apply(selector: js.Any): JQuery = js.native
  }

  @js.native
  trait JQuery extends js.Object {
    def show(): Unit = js.native
  }

  @js.native
  trait SemanticJQuery extends JQuery {
    def accordion(params: js.Any*): SemanticJQuery = js.native
    def dropdown(params: js.Any*): SemanticJQuery = js.native
    def checkbox(): SemanticJQuery = js.native
    def modal(params: js.Any*): SemanticJQuery = js.native
    def calendar(params: js.Any*): SemanticJQuery = js.native
  }

  implicit def jq2bootstrap(jq: JQuery): SemanticJQuery = jq.asInstanceOf[SemanticJQuery]
}
