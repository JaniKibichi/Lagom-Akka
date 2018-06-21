package com.github.janikibichi.learnakka.akka.impl

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import com.github.janikibichi.learnakka.akka.api.CalculatorService
import com.github.janikibichi.learnakka.akka.impl.CalculatorActor.{Multiply, Sum}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class CalculatorServiceImpl(system: ActorSystem)(implicit val ec: ExecutionContext) extends CalculatorService {
  implicit val timeout = Timeout(2 seconds)

  override def add(one: Int, other: Int) = ServiceCall { _ => val calculatorActor = system.actorOf( Props[CalculatorActor])
      (calculatorActor ? Sum(one, other)).mapTo[Int]
  }

  override def multiply(one: Int, other: Int) = ServiceCall { _ => val calculatorActor = system.actorOf(Props[CalculatorActor])
      (calculatorActor ? Multiply(one, other)).mapTo[Int]
  }
}
