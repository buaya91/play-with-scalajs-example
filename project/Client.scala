import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Client {

  object Versions {
    lazy val jQueryV = "3.0.0"
    lazy val semanticV = "2.2.2"
    lazy val scalajsDomV = "0.9.0"
    lazy val scalaTagV = "0.6.0"
  }

  lazy val libDeps = Def.setting(
    Seq(
      "org.scala-js" %%% "scalajs-dom" % Versions.scalajsDomV,
      "com.lihaoyi" %%% "scalatags" % Versions.scalaTagV
    )
  )

  lazy val jsDeps = Def.setting(
    Seq(
      "org.webjars" % "jquery"      % Versions.jQueryV / "jquery.js" minified "jquery.min.js",
      "org.webjars" % "Semantic-UI" % Versions.semanticV / "semantic.js" minified "semantic.min.js" dependsOn "jquery.js"
    )
  )
}
