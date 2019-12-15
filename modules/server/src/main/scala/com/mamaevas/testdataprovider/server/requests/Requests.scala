package com.mamaevas.testdataprovider.server.requests

import cats.implicits._
import com.mamaevas.testdataprovider.server.ServerApp
import sttp.client._
import sttp.tapir._
import sttp.tapir.client.sttp._

object Requests {
  implicit val backend: SttpBackend[Identity, Nothing, NothingT] = ServerApp.backend

  def setParentId(
      url: String,
      id: String,
      parentId: String,
      accessToken: String
  ): Identity[Response[Either[Unit, String]]] =
    endpoint.put
      .in(query[Option[String]]("id"))
      .in(query[Option[String]]("parentId"))
      .in(header("Cookie", s"access-token=$accessToken"))
      .out(stringBody)
      .toSttpRequestUnsafe(uri"$url")
      .apply((id.some, parentId.some))
      .send()

  def createDocument(url: String, payload: String, accessToken: String): Identity[Response[Either[Unit, String]]] =
    endpoint.put
      .in(query[Option[String]]("parentId"))
      .in(stringBody)
      .in(header("Cookie", s"access-token=$accessToken"))
      .in(header("Content-Type", "application/json"))
      .out(stringBody)
      .toSttpRequestUnsafe(uri"$url")
      .apply((None, payload))
      .send()
}
