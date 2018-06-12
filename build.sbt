import WebKeys._

lazy val root = (project in file("."))
  .enablePlugins(SbtTwirl, SbtWeb, GitVersioning, GitBranchPrompt, DockerPlugin)
  .settings(
    scalaVersion := "2.12.6",

    JsEngineKeys.engineType := JsEngineKeys.EngineType.Node,

    (managedClasspath in Runtime) += (packageBin in Assets).value,
    packagePrefix in Assets := "public/",

    unmanagedResourceDirectories in Compile += target.value / "webpack" / "output",

    resourceDirectory in Assets := target.value / "webpack" / "output",

    pipeline := pipeline.dependsOn(webpack.toTask("")).value,

    // This avoids reloading when web assets change.  Its not meant to be a perfect solution
    // so there are likely better ways to accomplish this if desired.
    watchSources := watchSources.value.filterNot(w => w.base.getPath.contains("src/main/web") || w.base.getPath.contains("webpack/output")),

    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.2",
      "com.typesafe.akka" %% "akka-stream" % "2.5.12",
      "io.buddho.akka" %% "akka-http-twirl" % "1.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
      "com.github.pureconfig" %% "pureconfig" % "0.9.1"
    )
  )

