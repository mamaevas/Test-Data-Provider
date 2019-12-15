package com.mamaevas.testdataprovider.server.routes

import cats.effect.{ContextShift, IO}
import cats.syntax.all._
import com.mamaevas.testdataprovider.server.endpoints.Endpoints
import com.mamaevas.testdataprovider.server.model.message.Message
import com.mamaevas.testdataprovider.server.services.TaskService
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s._

import scala.collection.immutable

class Routes(implicit cs: ContextShift[IO]) {

  val routes: immutable.Seq[HttpRoutes[IO]] = List(
    health(),
    runTask()
  )

  def health(): HttpRoutes[IO] = Endpoints.healthEndpoint.toRoutes { _ =>
    IO(Message("Running").asRight[String])
  }

  def runTask(): HttpRoutes[IO] = Endpoints.taskEndpoint.toRoutes(TaskService.task)
}
