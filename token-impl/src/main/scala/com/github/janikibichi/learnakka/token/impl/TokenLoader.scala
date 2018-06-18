package com.github.janikibichi.learnakka.token.impl

import com.github.janikibichi.learnakka.token.api.TokenService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._

class TokenLoader extends LagomApplicationLoader{
  override def load(context: LagomApplicationContext): LagomApplication =
    new TokenApplication(context){
    override def serviceLocator: ServiceLocator = NoServiceLocator
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new TokenApplication(context) with LagomDevModeComponents
  override def describeServices = List(readDescriptor[TokenService])
}

abstract class TokenApplication(context: LagomApplicationContext)
  extends LagomApplication(context) with AhcWSComponents{
  override lazy val lagomServer = LagomServer.forServices(bindService[TokenService].to(wire[TokenServiceImpl])
  )
}