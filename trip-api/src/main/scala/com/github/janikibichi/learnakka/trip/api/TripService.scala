package com.github.janikibichi.learnakka.trip

import akka.{Done, NotUsed}
import com.github.janikibichi.learnakka.trip.api.ReportLocation
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait TripService extends Service{
  def startTrip(clientId: String): ServiceCall[NotUsed, Done]
  def reportLocation(clientId: String): ServiceCall[ReportLocation, Done]
  def endTrip(clientId: String): ServiceCall[NotUsed, Done]

  override final def descriptor = {
    import com.lightbend.lagom.scaladsl.api.Service._

    named("trip").withCalls(
      pathCall("/trip/start/:id", startTrip _),
      pathCall("/trip/report/:id", reportLocation _),
      pathCall("/trip/end/:id", endTrip _)
    ).withAutoAcl(true)
  }
}
