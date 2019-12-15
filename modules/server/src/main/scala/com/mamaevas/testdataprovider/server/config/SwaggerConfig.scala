package com.mamaevas.testdataprovider.server.config

import buildinfo.BuildInfo
import cats.effect.{ContextShift, IO}
import com.mamaevas.testdataprovider.server.endpoints.Endpoints.{healthEndpoint, taskEndpoint}
import org.http4s.HttpRoutes
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

object SwaggerConfig {

  def swaggerRoutes(implicit cs: ContextShift[IO]): HttpRoutes[IO] = new SwaggerHttp4s(yaml).routes[IO]

  def yaml: String =
    endpoints
      .toOpenAPI(BuildInfo.name, BuildInfo.version)
      .toYaml

  def endpoints = List(
    healthEndpoint,
    taskEndpoint
  )
}
