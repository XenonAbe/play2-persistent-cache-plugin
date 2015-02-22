name := "play2-persistent-cache"

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.4"

publishTo := Some(Resolver.file("play2-persistent-cache",file("../pages"))(Patterns(true, Resolver.mavenStyleBasePattern)))

incOptions := incOptions.value.withNameHashing(true)

libraryDependencies ++= Seq(
  cache
)

PlayKeys.ebeanEnabled := false

//EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE18)

EclipseKeys.withSource := true

EclipseKeys.eclipseOutput := Some(".target")

sources in (Compile, doc) := Seq.empty

publishArtifact in(Compile, packageDoc) := false
