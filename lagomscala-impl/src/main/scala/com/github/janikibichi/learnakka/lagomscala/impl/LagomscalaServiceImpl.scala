package com.github.janikibichi.learnakka.lagomscala.impl

import akka.Done
import com.github.janikibichi.learnakka.lagomscala.api
import com.github.janikibichi.learnakka.lagomscala.api.LagomscalaService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import scala.concurrent.Future

/**
  * Implementation of the LagomscalaService.
  */
class LagomscalaServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends LagomscalaService {
  override def healthCheck() = ServiceCall (_ => Future.successful(Done))

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the lagomscala entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomscalaEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the lagomscala entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomscalaEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(LagomscalaEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[LagomscalaEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
