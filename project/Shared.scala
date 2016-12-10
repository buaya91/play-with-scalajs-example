import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Shared {
  object Versions {
    lazy val scalaV = "2.11.8"
    lazy val scalatestV = "3.0.0"
    lazy val monixV = "2.0-RC8"
    lazy val boopickleV = "1.2.4"
  }

  val libDeps = Def.setting(
    Seq(
      "me.chrons"     %%% "boopickle" % Versions.boopickleV,
      "io.monix"      %%% "monix"     % Versions.monixV,
      "org.scalatest" %%% "scalatest" % Versions.scalatestV % "test"
    )
  )
}
