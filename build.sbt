ThisBuild / scalacOptions ++= CompilerOptions.options

lazy val commonSettings = Seq(
  organization := "com.mamaevas",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.10",
  addCompilerPlugin(Dependencies.betterMonadicFor),
  libraryDependencies ++= Dependencies.common
)

lazy val server = (project in file("modules/server"))
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(JavaAppPackaging)
  .dependsOn(mongoRepository)
  .settings(commonSettings: _*)
  .settings(
    name := "test-data-provider-server",
    libraryDependencies ++= Dependencies.tapir,
    libraryDependencies ++= Dependencies.circe,
    libraryDependencies ++= Dependencies.pureConfig
  )

lazy val mongoRepository = (project in file("modules/repository/mongo"))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "test-data-provider-repository-mongo",
    libraryDependencies ++= Dependencies.pureConfig,
    libraryDependencies ++= Dependencies.circe,
    libraryDependencies ++= Dependencies.reactiveMongo,
    libraryDependencies ++= Dependencies.cats
  )

lazy val root = (project in file("."))
  .aggregate(
    server
  )
  .settings(
    name := "test-data-provider",
    skip in publish := true
  )
