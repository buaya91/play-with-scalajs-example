package client

import org.scalajs.dom.Event
import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@js.native
@JSName("jQuery")
object JQueryStatic extends js.Object {
  def apply(selector: js.Any): JQuery = js.native
}

@js.native
trait JQuery extends js.Object {}
