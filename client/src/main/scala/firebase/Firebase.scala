package firebase

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@JSName("firebase")
@js.native
object Firebase extends js.Object {
  def database(): Database = js.native
}

@js.native
trait Database extends js.Any {
  def ref(path: Option[String] = None): Reference = js.native
}

@js.native
trait Reference extends js.Any {
  def child(path: String): Reference = js.native
  def on(eventType: String, callback: js.Function1[DataSnapshot, Any]): Unit = js.native
  def once(eventType: String, callback: js.Function1[DataSnapshot, Any]): Unit = js.native
  def push(value: js.Any): js.Dynamic = js.native
  def remove(): Unit = js.native
  def set(value: js.Any): js.Dynamic = js.native
}

@js.native
trait DataSnapshot extends js.Any {
  def `val`(): js.Dynamic = js.native
}