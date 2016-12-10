import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Client {

  object Versions {
    lazy val reactV = "15.3.2"
    lazy val scalaReactV = "0.11.2"
    lazy val scalaDomV = "0.9.1"
    lazy val scalaTagV = "0.6.1"
  }

  lazy val libDeps = Def.setting(
    Seq(
      "org.scala-js"                      %%% "scalajs-dom" % Versions.scalaDomV,
      "com.github.japgolly.scalajs-react" %%% "core"        % Versions.scalaReactV,
      "com.lihaoyi"                       %%% "scalatags"   % Versions.scalaTagV
    )
  )

  lazy val jsDeps = Def.setting(
    Seq(
      "org.webjars.bower" % "react" % Versions.reactV
        / "react-with-addons.js"
        minified "react-with-addons.min.js"
        commonJSName "React",
      "org.webjars.bower" % "react" % Versions.reactV
        / "react-dom.js"
        minified "react-dom.min.js"
        dependsOn "react-with-addons.js"
        commonJSName "ReactDOM",
      "org.webjars.bower" % "react" % Versions.reactV
        / "react-dom-server.js"
        minified "react-dom-server.min.js"
        dependsOn "react-dom.js"
        commonJSName "ReactDOMServer"
    )
  )
}
