package com.mamaevas.testdataprovider.repository.mongo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.effect.{ContextShift, IO, Resource}
import cats.implicits._
import com.typesafe.config.Config
import com.typesafe.scalalogging.Logger
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class MongoDatabase()

case class ImplicitTest(id: String)

object MongoDatabase {
  private val log = Logger(this.getClass)

  def apply(config: Config, classLoader: ClassLoader, uri: String, dbName: String)(
      implicit contextShift: ContextShift[IO]
  ): Resource[IO, (DefaultDB, ActorMaterializer)] =
    for {
      driver                          <- getDriver(config, classLoader)
      implicit0(as: ActorSystem)      = driver.system
      implicit0(ec: ExecutionContext) = driver.system.dispatcher
      connection                      <- getConnection(uri, driver)
      database                        <- Resource.liftF(MongoDatabase.getDatabase(dbName, connection))
    } yield (database, ActorMaterializer())

  def getDriver(config: Config, classLoader: ClassLoader): Resource[IO, MongoDriver] =
    Resource.make(IO(MongoDriver(config, classLoader))) { driver =>
      IO {
        log.info("Closing mongo driver.")
        driver.close()
      }.void.handleError { ex =>
        log.error("Cannot close mongo driver resource", ex)
      }
    }

  def getConnection(uri: String, driver: MongoDriver)(
      implicit contextShift: ContextShift[IO]
  ): Resource[IO, MongoConnection] =
    Resource.make(IO.fromEither(driver.connection(uri).toEither)) { connection =>
      IO.fromFuture(IO {
          log.info("Closing mongo connection.")
          connection.askClose()(5.seconds)
        })
        .void
        .handleError { ex =>
          log.error("Cannot close mongo connection resource.", ex)
        }
    }

  def getDatabase(dbName: String, connection: MongoConnection)(
      implicit contextShift: ContextShift[IO],
      ec: ExecutionContext
  ): IO[DefaultDB] =
    IO.fromFuture(IO(connection.database(dbName)))
}
