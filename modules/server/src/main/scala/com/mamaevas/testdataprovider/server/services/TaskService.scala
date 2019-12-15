package com.mamaevas.testdataprovider.server.services

import akka.Done
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import cats.effect.{ContextShift, IO}
import cats.syntax.all._
import com.mamaevas.testdataprovider.repository.mongo.MongoDatabase
import com.mamaevas.testdataprovider.server.ServerApp
import com.mamaevas.testdataprovider.server.model.TaskParameters
import com.mamaevas.testdataprovider.server.model.message.Message
import com.mamaevas.testdataprovider.server.requests.Requests
import com.typesafe.scalalogging.Logger
import io.circe._
import io.circe.bson._
import reactivemongo.akkastream.cursorProducer
import reactivemongo.api.DefaultDB
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.compat._
import reactivemongo.bson.BSONDocument
import sttp.client.{Identity, _}

import scala.concurrent.Future

object TaskService {
  private val log = Logger(this.getClass)

  def task(params: TaskParameters)(implicit cs: ContextShift[IO]): IO[Either[Unit, Message]] =
    for {
      _    <- IO(log.info("Task started..."))
      task <- runTaskAsync(params)
      _    <- task.join.flatMap(_ => IO(log.info("Task ended..."))).start
    } yield {
      Message("Task started").asRight[Unit]
    }

  private def runTaskAsync(params: TaskParameters)(implicit cs: ContextShift[IO]) =
    MongoDatabase(ServerApp.config, ServerApp.classLoader, params.uri, params.dbName).use {
      case (db, mat) =>
        for {
          _ <- readDataAndSend(params, db, mat)
          _ <- params.setParentUri.fold(IO(Done.done()))(_ => setParents(params, db, mat))
        } yield ()
    }.start

  private def readDataAndSend(params: TaskParameters, db: DefaultDB, mat: ActorMaterializer)(
      implicit cs: ContextShift[IO]
  ): IO[Done] =
    IO.fromFuture(
      IO {
        getSource(params, db, mat)
          .mapAsync(1) { doc =>
            Future.successful {
              val json = bsonToJson(doc).toOption
              params.bsonField.fold(json) { field =>
                json.map(_.hcursor.downField(field)).flatMap(_.focus)
              }
            }
          }
          .runWith(Sink.foreach { document =>
            document.fold(log.error("Failed parse bson as json.")) { d =>
              val payload = Printer.noSpaces.print(d)
              val request = Requests.createDocument(params.outputUri, payload, params.accessToken)
              logRequest(payload, request, ".")
            }
          })(mat)
      }
    )

  private def setParents(params: TaskParameters, db: DefaultDB, mat: ActorMaterializer)(
      implicit cs: ContextShift[IO]
  ): IO[Done] =
    IO.fromFuture(IO {
      getSource(params, db, mat)
        .mapAsync(1) { doc =>
          Future.successful {
            val json     = bsonToJson(doc).toOption
            val docId    = json.flatMap(_.hcursor.downField("_id").as[String].toOption)
            val parentId = json.flatMap(_.hcursor.downField("parent").as[String].toOption)
            (docId, parentId)
          }
        }
        .runWith(Sink.foreach {
          case (docId, parentId) =>
            docId.fold(log.error("Id of the document not found!")) { id =>
              parentId.foreach { pid =>
                params.setParentUri.foreach { parentUri =>
                  val request = Requests.setParentId(parentUri, id, pid, params.accessToken)
                  logRequest(s"id=$id pid=$pid", request, ":")
                }
              }
            }
        })(mat)
    })

  private def getSource(params: TaskParameters, db: DefaultDB, mat: ActorMaterializer) =
    db.collection[BSONCollection](params.collectionName)
      .find(BSONDocument.empty, None)
      .cursor[BSONDocument]()
      .documentSource()(mat)

  private def logRequest(payload: String, request: Identity[Response[Either[Unit, String]]], counter: String): Unit = {
    print(counter)
    if (!request.code.isSuccess) {
      log.warn(s"${request} ${System.lineSeparator()} ${payload} ${System.lineSeparator()}")
    }
  }
}
