package com.github.janikibichi.learnakka.trip.impl

import com.github.janikibichi.learnakka.trip.TripService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire.wire

class TripLoader extends LagomApplicationLoader{
  override def load(context: LagomApplicationContext): LagomApplication = new TripApplication(context) {
    override def serviceLocator: ServiceLocator = NoServiceLocator
  }
  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new TripApplication(context) with LagomDevModeComponents
  }
  override def describeServices = List(readDescriptor[TripService])
}

abstract class TripApplication(context: LagomApplicationContext)
  extends LagomApplication(context) with CassandraPersistenceComponents with AhcWSComponents{
  override lazy val lagomServer = LagomServer.forServices(
    bindService[TripService].to(wire[TripServiceImpl])
  )
  override lazy val jsonSerializerRegistry = ClientSerializerRegistry

  persistentEntityRegistry.register(wire[ClientEntity])
}