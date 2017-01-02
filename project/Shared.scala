import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Shared {
  object Versions {
    lazy val monixV = "2.1.2"
    lazy val boopickleV = "1.2.4"
  }

  val libDeps = Def.setting(
    Seq(
      "me.chrons"      %%% "boopickle" % Versions.boopickleV,
      "io.monix"       %%% "monix"     % Versions.monixV
    )
  )
}
