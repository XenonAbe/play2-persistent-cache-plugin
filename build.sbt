//import com.typesafe.sbteclipse.core.EclipsePlugin._

name := "persistent-cache"

version := "1.0-SNAPSHOT"

scalaVersion := Common.scalaVersion

incOptions := incOptions.value.withNameHashing(true)

libraryDependencies ++= Seq(
  cache
)

PlayKeys.ebeanEnabled := false

EclipseKeys.executionEnvironment := Common.executionEnvironment

EclipseKeys.withSource := true

EclipseKeys.eclipseOutput := Some(".target")

EclipseKeys.classpathTransformerFactories := Seq(Common.addClassesManaged)

sources in (Compile, doc) := Seq.empty

publishArtifact in(Compile, packageDoc) := false
