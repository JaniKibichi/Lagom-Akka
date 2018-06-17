package com.github.janikibichi.learnakka.lagomscala.impl

import com.github.janikibichi.learnakka.lagomscala.api.LagomscalaService
import com.lightbend.lagom.scaladsl.api.{ServiceCall, ServiceLocator}
import scala.concurrent.{Await, Future}

class LagomscalaServiceImpl extends LagomscalaService {
  override def toUppercase = ServiceCall{
    x =>  Future.successful(x.toUpperCase)
  }
  override def toLowercase = ServiceCall{
    x =>  Future.successful(x.toLowerCase)
  }
  override def isEmpty(str:String) = ServiceCall{
    _ =>  Future.successful(str.isEmpty)
  }
  override def areEqual(str1:String, str2:String) = ServiceCall{
    _ =>  Future.successful(str1 == str2)
  }
}
