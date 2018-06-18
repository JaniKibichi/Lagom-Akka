package com.github.janikibichi.learnakka.lagomscalastream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.github.janikibichi.learnakka.lagomscalastream.api.LagomscalaStreamService
import com.github.janikibichi.learnakka.lagomscala.api.LagomscalaService

import scala.concurrent.Future

/**
  * Implementation of the LagomscalaStreamService.
  */
class LagomscalaStreamServiceImpl(lagomscalaService: LagomscalaService) extends LagomscalaStreamService {
  def stream = ServiceCall { hellos => ???
    //Future.successful(hellos.mapAsync(8)(lagomscalaService.hello(_).invoke()))
  }
}
