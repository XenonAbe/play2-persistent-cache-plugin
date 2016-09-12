name := "twinkle-persistent-cache"

version := "2.5.2"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

publishTo := Some(Resolver.file("twinkle-persistent-cache",file("../pages"))(Patterns(true, Resolver.mavenStyleBasePattern)))

libraryDependencies ++= Seq(
  cache
)

EclipseKeys.withSource := true

EclipseKeys.eclipseOutput := Some(".target")

sources in (Compile, doc) := Seq.empty

publishArtifact in(Compile, packageDoc) := false
