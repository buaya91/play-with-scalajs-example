import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Client {

  object Versions {
    lazy val reactV       = "15.3.2"
    lazy val scalajsDomV  = "0.9.0"
    lazy val scalaTagV    = "0.6.0"
    lazy val scalatestV   = "3.0.0"
    lazy val scalajsReact = "0.11.3"
  }

  lazy val libDeps = Def.setting(
    Seq(
      "com.github.japgolly.scalajs-react" %%% "core"        % Versions.scalajsReact,
      "org.scala-js"                      %%% "scalajs-dom" % Versions.scalajsDomV,
      "com.lihaoyi"                       %%% "scalatags"   % Versions.scalaTagV,
      "org.scalatest"                     %%% "scalatest"   % Versions.scalatestV % "test"
    )
  )

  lazy val jsDeps = Def.setting(
    Seq(
      RuntimeDOM,
      "org.webjars.bower" % "react" % Versions.reactV / "react.js" minified "react.min.js" commonJSName "React",
      "org.webjars.bower" % "react" % Versions.reactV / "react-dom.js" minified "react-dom.min.js" dependsOn "react.js" commonJSName "ReactDOM"
    )
  )
}
