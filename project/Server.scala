import sbt._

object Server {
  object Versions {
    lazy val akkaV = "2.4.12"
    lazy val scalajsScriptV = "1.1.0"
    lazy val logbackV = "1.1.7"
    lazy val scalatestV = "3.0.0"
    lazy val scalacheckV = "1.13.0"
  }

  lazy val libDeps = Def.setting(
    Seq(
      "com.vmunier"       %% "scalajs-scripts"     % Versions.scalajsScriptV,
      "ch.qos.logback"    % "logback-core"         % Versions.logbackV,
      "com.typesafe.akka" %% "akka-stream-testkit" % Versions.akkaV % "test",
      "com.typesafe.akka" %% "akka-testkit"        % Versions.akkaV % "test",
      "org.scalatest"     %% "scalatest"           % Versions.scalatestV % "test",
      "org.scalacheck"    %% "scalacheck"          % Versions.scalacheckV % "test"
    )
  )
}
