package com.mamaevas.testdataprovider.server.model.message

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Message(msg: String)

object Message {
  implicit val fooDecoder: Decoder[Message] = deriveDecoder
  implicit val fooEncoder: Encoder[Message] = deriveEncoder
}
