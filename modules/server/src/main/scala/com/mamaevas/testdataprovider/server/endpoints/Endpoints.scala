package com.mamaevas.testdataprovider.server.endpoints

import com.mamaevas.testdataprovider.server.model.TaskParameters
import com.mamaevas.testdataprovider.server.model.message.Message
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, endpoint, jsonBody, stringBody, _}

object Endpoints {

  val healthEndpoint: Endpoint[Unit, String, Message, Nothing] =
    endpoint.get
      .in("health")
      .errorOut(stringBody)
      .out(jsonBody[Message])

  val taskEndpoint: Endpoint[TaskParameters, Unit, Message, Nothing] =
    endpoint.post
      .in("runTask")
      .description("Run task of reading data from mongo collection and send it to the api endpoint")
      .in(jsonBody[TaskParameters].description("Parameters of the task"))
      .out(jsonBody[Message])
      .out(statusCode(StatusCode.Accepted))
}
