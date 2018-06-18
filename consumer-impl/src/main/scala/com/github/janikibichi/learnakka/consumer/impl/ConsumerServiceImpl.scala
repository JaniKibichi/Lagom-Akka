package com.github.janikibichi.learnakka.consumer.impl

import com.github.janikibichi.learnakka.consumer.api.ConsumerService
import com.github.janikibichi.learnakka.token.api.{TokenService, ValidateTokenRequest}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.ExecutionContext

class ConsumerServiceImpl(tService: TokenService)(implicit ec:ExecutionContext) extends ConsumerService {
  override def consume = ServiceCall { request =>
      val validateTokenRequest = ValidateTokenRequest(request.clientId, request.token)
      tService.validateToken.invoke(validateTokenRequest).map(_.successful)
  }
}
