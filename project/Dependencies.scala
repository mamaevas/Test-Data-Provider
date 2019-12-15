import sbt._

object Dependencies {

  object Versions {
    val tapir            = "0.12.7"
    val circe            = "0.12.2"
    val circeBson        = "0.4.0-M1"
    val pureConfig       = "0.12.1"
    val reactiveMongo    = "0.19.3"
    val cats             = "2.0.0"
    val scalaLogging     = "3.9.2"
    val logback          = "1.2.3"
    val betterMonadicFor = "0.3.1"
  }

  lazy val tapir: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"               % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"        % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % Versions.tapir
  )

  lazy val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core"           % Versions.circe,
    "io.circe" %% "circe-generic"        % Versions.circe,
    "io.circe" %% "circe-generic-extras" % Versions.circe,
    "io.circe" %% "circe-parser"         % Versions.circe,
    "io.circe" %% "circe-bson"           % Versions.circeBson
  )

  lazy val pureConfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig" % Versions.pureConfig
  )

  lazy val logging = Seq(
    "com.typesafe.scala-logging" %% "scala-logging"  % Versions.scalaLogging,
    "ch.qos.logback"             % "logback-classic" % Versions.logback
  )

  lazy val common: Seq[ModuleID] =
    logging :+ betterMonadicFor

  lazy val reactiveMongo = Seq(
    "org.reactivemongo" %% "reactivemongo"            % Versions.reactiveMongo,
    "org.reactivemongo" %% "reactivemongo-akkastream" % Versions.reactiveMongo
  )

  lazy val cats = Seq(
    "org.typelevel" %% "cats-core"   % Versions.cats,
    "org.typelevel" %% "cats-effect" % Versions.cats
  )

  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor

}
