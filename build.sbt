import java.nio.file.{FileSystems, Files, StandardCopyOption}

name := "Scalajs-snake"

version := "0.1.0"

lazy val scalaV = "2.11.8"
lazy val akkaV = "2.4.12"
lazy val scalatestV = "3.0.0"
lazy val scalacheckV = "1.13.0"
lazy val monixV = "2.0-RC8"
lazy val microPickleV = "0.4.3"
lazy val prickleV = "1.1.12"

def commonSettings = Seq(
  scalaVersion := scalaV,
  fork in run := true,
  testOptions in Test += Tests.Argument("-oD"),
  scalacOptions ++= Seq("-feature")
)

lazy val server = (project in file("server"))
  .settings(commonSettings: _*)
  .settings(
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
    libraryDependencies ++= Seq(
      "com.vmunier"       %% "scalajs-scripts"     % "1.1.0",
      "ch.qos.logback"    % "logback-core"         % "1.1.7",
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % "test",
      "com.typesafe.akka" %% "akka-testkit"        % akkaV % "test",
      "org.scalacheck"    %% "scalacheck"          % scalacheckV % "test",
      "org.scalatest"     %% "scalatest"           % scalatestV % "test"
    )
  )
  .enablePlugins(PlayScala)
  .dependsOn(sharedJvm)

lazy val client = (project in file("client"))
  .settings(commonSettings: _*)
  .settings(
    persistLauncher := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Seq(
      "org.scala-js"  %%% "scalajs-dom" % "0.9.1",
      "io.monix"      %%% "monix"       % monixV,
      "org.scalatest" %%% "scalatest"   % scalatestV % "test"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.benhutchison" %%% "prickle"   % prickleV,
      "org.scalatest"           %%% "scalatest" % scalatestV % "test"
    )
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command
  .process("project server", _: State)) compose (onLoad in Global).value

lazy val updateGh = taskKey[Unit]("Update js and push to github pages")

updateGh in Global := {
  (fullOptJS in Compile in client).value.map(f => {
    val fullOptTarget = f.toPath
    val distFolder = FileSystems.getDefault.getPath("dist", "client-opt.js")
    Files.copy(fullOptTarget, distFolder, StandardCopyOption.REPLACE_EXISTING)

    "git stash" #&&
      "git checkout gh-pages" #&&
      "cp dist/* ./" #&&
      "git add client-launcher.js client-opt.js index.html main.css" #&&
      "git commit -m 'Update gh'" #&&
      "git push origin gh-pages" #&&
      "git checkout master" #&&
      "git stash pop" !
  })
}
