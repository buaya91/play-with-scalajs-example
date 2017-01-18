import java.nio.file.{FileSystems, Files, StandardCopyOption}
import org.irundaia.sbt.sass._

name := "Scalajs-snake"

version := "0.1.0"

lazy val scalaV = "2.11.8"

SassKeys.assetRootURL := "/server/app/assets"

def commonSettings = Seq(
  scalaVersion := scalaV,
  fork in run := true,
  testOptions in Test += Tests.Argument("-oD"),
  scalacOptions ++= Seq("-feature", "-Ywarn-unused-import")
)

lazy val server = (project in file("server"))
  .settings(commonSettings: _*)
  .settings(
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
    libraryDependencies ++= Server.libDeps.value
  )
  .enablePlugins(PlayScala)
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

lazy val updateGh = taskKey[Unit]("Update js and push to github pages")

updateGh in Global := {
  (fullOptJS in Compile in client).value.map(f => {
    val fullOptTarget = f.toPath
    val distFolder    = FileSystems.getDefault.getPath("dist", "client-opt.js")
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
