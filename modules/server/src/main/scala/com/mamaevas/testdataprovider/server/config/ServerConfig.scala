package com.mamaevas.testdataprovider.server.config

import com.typesafe.config.ConfigFactory
import pureconfig.generic.semiauto._
import pureconfig.{ConfigReader, ConfigSource}

case class ServerConfig(
    host: String,
    port: Int
)

object ServerConfig {
  implicit val configReader: ConfigReader[ServerConfig] = deriveReader[ServerConfig]

  private val configPath: String = "server"
  private val configSource       = ConfigFactory.load()
  val config: ServerConfig       = ConfigSource.fromConfig(configSource).at(ServerConfig.configPath).loadOrThrow[ServerConfig]
}
