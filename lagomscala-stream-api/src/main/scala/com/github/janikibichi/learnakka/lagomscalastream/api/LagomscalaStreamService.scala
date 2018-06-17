package com.github.janikibichi.learnakka.lagomscalastream.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

/**
  * The lagomscala stream interface.
  *
  * This describes everything that Lagom needs to know about how to serve and
  * consume the LagomscalaStream service.
  */
trait LagomscalaStreamService extends Service {

  def stream: ServiceCall[Source[String, NotUsed], Source[String, NotUsed]]

  override final def descriptor = {
    import Service._

    named("lagomscala-stream")
      .withCalls(
        namedCall("stream", stream)
      ).withAutoAcl(true)
  }
}

