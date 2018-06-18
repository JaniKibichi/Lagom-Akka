package com.github.janikibichi.learnakka.consumer.api

import play.api.libs.json.{Format, Json}

case class ConsumeRequest(clientId: String, token: String, message: String)

object ConsumeRequest{
  implicit val consumeRequestFormat: Format[ConsumeRequest] = Json.format[ConsumeRequest]
}
