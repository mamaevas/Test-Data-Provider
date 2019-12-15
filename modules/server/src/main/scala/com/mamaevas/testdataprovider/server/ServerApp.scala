package com.mamaevas.testdataprovider.server

import java.util.concurrent.{Executors, TimeUnit}

import cats.effect.{ExitCode, IO, IOApp, Resource, SyncIO}
import cats.implicits._
import com.mamaevas.testdataprovider.server.config.{ServerConfig, SwaggerConfig}
import com.mamaevas.testdataprovider.server.routes.Routes
import com.typesafe.config.{Config, ConfigFactory}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}

import scala.concurrent.ExecutionContext

object ServerApp extends IOApp.WithContext {
  val config: Config                                    = ConfigFactory.load()
  val classLoader: ClassLoader                          = getClass.getClassLoader
  val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] =
    Resource
      .make(SyncIO(Executors.newFixedThreadPool(8)))(
        pool =>
          SyncIO {
            pool.shutdown()
            pool.awaitTermination(10, TimeUnit.SECONDS)
            ()
          }
      )
      .map(ExecutionContext.fromExecutorService)

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(ServerConfig.config.port, ServerConfig.config.host)
      .withHttpApp(Router("/" -> (new Routes().routes.reduceLeft(_ <+> _) <+> SwaggerConfig.swaggerRoutes)).orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
