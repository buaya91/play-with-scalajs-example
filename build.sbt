version := "0.1.0"

lazy val scalaV = "2.11.8"

SassKeys.assetRootURL := "/server/app/assets"

def commonSettings = Seq(
  scalaVersion := scalaV,
  fork in run := true,
  testOptions in Test += Tests.Argument("-oD"),
  scalacOptions ++= Seq("-feature", "-Ywarn-unused-import")
)

def dockerSetting = Seq(
  packageName in Docker := "scalajs-snake",
  maintainer in Docker := "Qingwei",
  packageSummary in Docker := "image for snake game",
  packageDescription := "",
  dockerBaseImage := "openjdk:8-alpine",
  dockerUpdateLatest := true,
  dockerRepository := Some("buaya91")
)

lazy val server = (project in file("server"))
  .settings(commonSettings: _*)
  .settings(
    name := "scalajs-snake",
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
    libraryDependencies ++= Server.libDeps.value
  )
  .settings(dockerSetting: _*)
  .enablePlugins(PlayScala, DockerPlugin, AshScriptPlugin)
  .dependsOn(sharedJvm)

lazy val client = (project in file("client"))
  .settings(commonSettings: _*)
  .settings(
    persistLauncher := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Client.libDeps.value,
    jsDependencies ++= Client.jsDeps.value
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Shared.libDeps.value
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs  = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
