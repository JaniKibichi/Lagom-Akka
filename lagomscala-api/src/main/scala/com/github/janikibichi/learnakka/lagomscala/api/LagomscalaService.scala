package com.github.janikibichi.learnakka.lagomscala.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object LagomscalaService  {
  val TOPIC_NAME = "greetings"
}

trait LagomscalaService extends Service{
  def toUppercase: ServiceCall[String,String]
  def toLowercase: ServiceCall[String,String]
  def isEmpty(str: String): ServiceCall[NotUsed,Boolean]
  def areEqual(str1:String, str2:String): ServiceCall[NotUsed,Boolean]

  override final def descriptor ={
    import Service._

    named("stringutils").withCalls(
      call(toUppercase),
      namedCall("toLowercase",toLowercase),
      pathCall("/isEmpty/:str", isEmpty _),
      restCall(Method.GET, "/areEqual/:one/another/:other", areEqual _)
    ).withAutoAcl(true)
  }
}

/**
  * The greeting message class.
  */
case class GreetingMessage(message: String)

object GreetingMessage {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[GreetingMessage] = Json.format[GreetingMessage]
}



/**
  * The greeting message class used by the topic stream.
  * Different than [[GreetingMessage]], this message includes the name (id).
  */
case class GreetingMessageChanged(name: String, message: String)

object GreetingMessageChanged {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[GreetingMessageChanged] = Json.format[GreetingMessageChanged]
}
