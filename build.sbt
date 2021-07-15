name := "playframework"
version := "1.0-SNAPSHOT"

enablePlugins(PlayScala, NewRelic)

scalaVersion := "3.0.2-RC1"
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-doc"           % "2.1.0",
  "org.eclipse.jgit"  % "org.eclipse.jgit"    % "5.7.0.202003110725-r",
  "com.damnhandy"     % "handy-uri-templates" % "2.1.8",
  "org.webjars"       % "jquery"              % "3.6.0",
  "org.webjars"       % "html5shiv"           % "3.7.3",
  "org.webjars"       % "prettify"            % "4-Mar-2013-1",
  "org.webjars"       % "clipboard.js"        % "2.0.6",
  guice,
  ehcache,
  filters,
  ws,
  specs2 % Test,
)

libraryDependencies := {
  libraryDependencies.value.map { x =>
    if (x.organization == "com.typesafe.play" && x.crossVersion.isInstanceOf[CrossVersion.Binary]) {
      x cross CrossVersion.for3Use2_13
    } else {
      x
    }
  }
}

routesGenerator := InjectedRoutesGenerator

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

StylusKeys.useNib in Assets := true
StylusKeys.compress in Assets := true

pipelineStages := Seq(digest, gzip)

sourceGenerators in Compile += Def.task {
  import scala.sys.process._
  val siteVersionFile = crossTarget.value / "version" / "SiteVersion.scala"
  val gitHash         = "git rev-parse HEAD".!!.trim
  if (!siteVersionFile.exists || !IO.read(siteVersionFile).contains(gitHash)) {
    IO.write(
      siteVersionFile,
      """package utils
        |
        |object SiteVersion {
        |  val hash = "%s"
        |}
      """.stripMargin.format(gitHash),
    )
  }
  Seq(siteVersionFile)
}.taskValue

managedSourceDirectories in Compile += crossTarget.value / "version"
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

newrelicVersion := "4.9.0"
newrelicAppName := "playframework.com"
