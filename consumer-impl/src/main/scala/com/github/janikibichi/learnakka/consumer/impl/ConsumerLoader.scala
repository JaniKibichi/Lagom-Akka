package com.github.janikibichi.learnakka.consumer.impl

import com.github.janikibichi.learnakka.consumer.api.ConsumerService
import com.github.janikibichi.learnakka.token.api.TokenService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire.wire

class ConsumerLoader extends LagomApplicationLoader{
  override def load(context: LagomApplicationContext): LagomApplication =
    new ConsumerApplication(context) {
    override def serviceLocator: ServiceLocator = NoServiceLocator
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ConsumerApplication(context) with LagomDevModeComponents
  override def describeServices = List(readDescriptor[ConsumerService])
}

abstract class ConsumerApplication(context: LagomApplicationContext)
  extends LagomApplication(context) with AhcWSComponents{
  override lazy val lagomServer = LagomServer.forServices(bindService[ConsumerService].to(wire[ConsumerServiceImpl])
  )
  lazy val tokenService = serviceClient.implement[TokenService]
}