# Creating Microservices with Lagom
Lagom is build on Akka and Play Frameworks making it an opinionated scala framework. It uses these mature frameworks as the basis for processing incoming API calls. Lagom already comes bundled with Kafka and Cassandra.
- Create a scaffolding Lagom App:
````
sbt new lagom/lagom-scala.g8
````
- While in the project run:
````
sbt runAll
````
- Test the API services:
````
curl -v -X http://127.0.0.1:9000/api/hello/graham

curl -v -H "Content-Type: application/json" -X POST -d '{"message":"Hello Lagom!"}' http://127.0.0.1:9000/api/hello/graham

curl -v -X GET http://127.0.0.1:9000/api/healthcheck
````
<br><br>
- Branch out to explore the service locator
````
git checkout -b service_locator master
````
- Add the line to build.sbt:
````
lagomUnmanagedServices in ThisBuild := Map("login" -> "http://127.0.0.1:8888")
````
- Run the App:
````
sbt runAll
````
<br><br>
- Branch out to explore the service descriptor
````
git checkout -b service_descriptor service_locator
````
- Update LagomscalaServiceImpl.scala and LagomscalaService.scala
- Run the services:
````
sbt runAll
````
- Test the new endpoints
````
curl -X POST -H "Content-Type: text/plain" --data "UPPERCASE" http://127.0.0.1:9000/toLowercase
curl -X POST -H "Content-Type: text/plain" --data "lowercase" http://127.0.0.1:9000/toUppercase
curl -X GET http://127.0.0.1:9000/isEmpty/notEmpty
curl -X GET http://127.0.0.1:9000/areEqual/something/another/somethingElse
````
<br><br>
- Branch out to explore implementing lagom services
````
git checkout -b implement_lagom_services service_descriptor
````
- Define new module in the build.sbt at the end of file:
````
lazy val `token-api` = (project in file("token-api"))
.settings(
  libraryDependencies ++= Seq(
   lagomScaladslApi
  )
)

lazy val `token-impl` = (project in file("token-impl"))
.enablePlugins(LagomScala)
.settings(
 libraryDependencies ++= Seq(
  lagomScaladslPersistenceCassandra,
  lagomScaladslTestKit,
  macwire,
  scalaTest
 )
)
.settings(lagomForkedTestSettings: _*)
.dependsOn(`token-api`)
````
- Update the project aggregate
````
lazy val `lagomscala` = (project in file("."))
  .aggregate(`lagomscala-api`, `lagomscala-impl`,
   `lagomscala-stream-api`, `lagomscala-stream-impl`,
   `token-api`,`token-impl`)

````
- Run sbt compile
````
sbt compile
````
- Create new package in token-api folder
````
mkdir -p src/main/scala/com/github/janikibichi/learnakka/token/api
````
- Create file:<b>com.github.janikibichi.learnakka.token.api.Messages.scala</b>
- Create file:<b>com.github.janikibichi.learnakka.token.api.TokenService.scala</b>
- Create new package in token-impl folder
````
mkdir -p src/main/scala/com/github/janikibichi/learnakka/token/impl
````
- Create file:<b>com.github.janikibichi.learnakka.token.impl.TokenServiceImpl.scala</b>
- Create file: <b>com.github.janikibichi.learnakka.token.impl.TokenLoader.scala</b>
- Specify where the service loader is in the application.conf, in the token-impl directory:
````
mkdir -p src/main/resources
touch src/main/resources/application.conf
````
<br><br>
- Branch out to explore consuming services:
````
git checkout -b consuming_services implement_lagom_services
````
- Define two new modules in the build.sbt file
````
lazy val `consumer-api` =(project in file("consumer-api"))
.settings(
 libraryDependencies ++= Seq(
  lagomScaladslApi
 )
)

lazy val `consumer-impl`=(project in file("consumer-impl"))
.enablePlugins(LagomScala)
.settings(
 libraryDependencies ++= Seq(
  lagomScaladslPersistenceCassandra,
  lagomScaladslTestKit,
  macwire,
  scalaTest
 )
)
.settings(lagomForkedTestSettings: _*)
.dependsOn(`consumer-api`,`token-api`)

````
- Run sbt compile:
````
sbt compile
````
- Create a package in the consumer-api module
````
mkdir -p src/main/scala/com/github/janikibichi/learnakka/consumer/api
````
- Create the file:<b>com.github.janikibichi.learnakka.consumer.api.Messages.scala</b>
- Create file to define service:<b>com.github.janikibichi.learnakka.consumer.api.ConsumeService.scala</b>
- Create a package in the consumer-impl module
````
mkdir -p src/main/scala/com/github/janikibichi/learnakka/consumer/impl
````
- Create file: <b>com.github.janikibichi.learnakka.consumer.api.ConsumerServiceImpl.scala</b>
- Create the service loader:<b>com.github.janikibichi.learnakka.consumer.api.ConsumerLoader.scala</b>
- Let Lagom know where our service loader is in consumer-impl:
````
mkdir -p src/main/resources
touch src/main/resources/application.conf
````
- Update the file:
````
play.application.loader = com.github.janikibichi.learnakka.consumer.impl.ConsumerLoader
````
- Run the Application:
````
sbt runAll
````
- Test end points with CURL:
````
curl -d '{"clientId":"some-invalid-clientId","token":"some-invalid-token", "message":""}' http://127.0.0.1:9000/api/consume

curl -d '{"clientId":"123456", "clientSecret":"something-wrong"} http://127.0.0.1:9000/token/retrieve

curl -d '{"clientId":"123456", "clientSecret":"in9ne0dfka"}' http://127.0.0.1:9000/token/retrieve

curl -d '{"clientId":"123456", "token":"ee34ee95-f908-4b03-b6e5-8e0b46b2f4bf","message":""}' http://127.0.0.1:9000/api/consume
````
<br><br>
- Branch out to explore Testing services
````
git checkout -b testing_services consuming_services
````
- Create the test directory in the token-impl module, and the file TokenServiceSpec.scala:
````
mkdir -p src/test/scala
touch src/test/scala/TokenServiceSpec.scala
````
- Add test for token retrieval inside 'The token service should:' block
````
"return a token if clientId and clientSecret are correct" in{
 val retrieveTokenRequest = RetrieveTokenRequest("123456","in9ne0dfka")
 serviceClient.retrieveToken.invoke(retrieveTokenRequest).map{
  response =>
   response.successful shouldBe true
   response.token should not be 'empty'
 }
}
````
- Add a test for token validator inside 'The token service should:'
````
"validate a valid token" in{
 val retrieveTokenRequest = RetrieveTokenRequest("123456","in9ne0dfka")
 serviceClient.retrieveToken.invoke(retrieveTokenRequest).flatMap{ retrieveResponse => 
   val validateTokenRequest = ValidateTokenRequest("123456",retrieveReponse.token.get)

    serviceClient.validateToken.invoke(validateTokenRequest).map{ 
     validateResponse => validateResponse shouldBe ValidateTokenResult(true)
    }
 }
}
````
<br><br>
- Branch out to explore persistent and clustered services
````
git checkout -b persistent_clustered_services testing_services
````
- Create two new modules in the build.sbt
````
lazy val `trip-api` = (project in file("trip-api"))
.settings(
 libraryDependencies ++ = Seq(
  lagomScaladslApi
 )
)

lazy val `trip-impl` = (project in file("trip-impl"))
.enablePlugins(LagomScala)
.settings(
 libraryDependencies ++ Seq(
  lagomScaladslPersistenceCassandra,
  lagomScaladslTestKit,
  macwire,
  scalaTest
 )
)
.settings(lagomForkedTestSettings: _*)
.dependsOn(`trip-api`)

````
- Add the modules to the aggregate of the project:
````
lazy val `lagomscala` = (project in file("."))
  .aggregate(
`lagomscala-api`, `lagomscala-impl`,
`lagomscala-stream-api`, `lagomscala-stream-impl`,
`token-api`,`token-impl`,`consumer-api`,
`consumer-impl`, `trip-api`,`trip-impl`
)
````
- Run sbt compile
````
sbt compile
````
- In the trip-api module, create the package:
````
mkdir -p src/main/scala/com/github/janikibichi/learnakka/trip/api
````
- Create file:<b>com.github.janikibichi.learnakka.trip.api.Messages.scala</b>
- Create file:<b>com.github.janikibichi.learnakka.trip.api.TripService.scala</b>
- In the trip-impl module, create the package:
````
mkdir -p src/main/scala/com/github/janikibichi/learnakka/trip/impl
````
- Create file:<b>com.github.janikibichi.learnakka.trip.impl.CommandEventState.scala</b>
- Create file to serialize data to/from data store: <b>com.github.janikibichi.learnakka.trip.impl.ClientSerializerRegistry.scala</b>
- Create file:<b>com.github.janikibichi.learnakka.trip.impl.ClientEntity.scala</b>
- Create file:<b>com.github.janikibichi.learnakka.trip.impl.TripServiceImpl.scala</b>
- Create the service loader:<b>com.github.janikibichi.learnakka.trip.impl.TripLoader.scala</b>
- Let Lagom know where the service loader is in trip-impl module:
````
mkdir -p src/main/resources
touch src/main/resources/application.conf
````
- Update application.conf file
````
play.application.loader = com.github.janikibichi.learnakka.trip.impl.TripLoader

````
- Run all our services
````
sbt runAll
````
- Test the persistence:
````
curl http://127.0.0.1:9000/trip/end/0

curl http://127.0.0.1:9000/trip/start/0

curl -d '{"latitude":0.1, "longitude": 0.1}' http://127.0.0.1:9000/trip/report/0

curl -d '{"latitude":0.2, "longitude": 0.2}' http://127.0.0.1:9000/trip/report/0

curl -d '{"latitude":0.3, "longitude": 0.3}' http://127.0.0.1:9000/trip/report/0

curl http://127.0.0.1:9000/trip/end/0
````
<br><br>
- Branch out to explore akka in lagom
````
git checkout -b akka_in_lagom persistent_clustered_services
````
- Define new modules in build.sbt
````
lazy val `akka-api`=(project in file("akka-api"))
.settings(
 libraryDependencies ++= Seq(
  lagomScaladslApi
 )
)

lazy val `akka-impl` = (project in file("akka-impl"))
.settings(
 libraryDependencies ++= Seq(
  lagomScaladslPersistenceCassandra,
  lagomScaladslTestKit,
  macwire,
  scalaTest
 )
)
.settings(lagomForkedTestSettings: _*)
.dependsOn(`akka-api`)
````
- Add the new modules to the aggregate of the project:
````
lazy val `lagomscala` = (project in file("."))
  .aggregate(`lagomscala-api`, `lagomscala-impl`,`lagomscala-stream-api`, `lagomscala-stream-impl`,
    `token-api`,`token-impl`,`consumer-api`,`consumer-impl`,`trip-api`,`trip-impl`,`akka-api`,`akka-impl`)

````
- Run sbt compile
````
sbt compile
````
- Create Package:
````
mkdir -p src/main/scala/com/github/janikibichi/learnakka/akka/api
````
- In the akka-api module, create file: CalculatorService.scala
````
com.github.janikibichi.learnakka.akka.api.CalculatorService.scala
````
- Create the implementation package in akka-impl
````
mkdir -p src/main/scala/com/github/janikibichi/learnakka/akka/impl
````
- In the akka-impl module, create file: CalculatorActor.scala
````
com.github.janikibichi.learnakka.akka.impl.CalculatorActor.scala
````
- Create the actual implementation:
- In the akka-impl module, create file: CalculatorServiceImpl.scala
````
com.github.janikibichi.learnakka.akka.impl.CalculatorServiceImpl.scala
````
- Create the service loader file: CalculatorLoader.scala
````
com.github.janikibichi.learnakka.akka.impl.CalculatorLoader.scala
````
- Notify Lagom where the Loader is using application.conf
````
mkdir -p src/main/resources
touch src/main/resources/application.conf
echo "play.application.loader = com.github.janikibichi.learnakka.akka.impl.CalculatorLoader" >> src/main/resources/application.conf
````
- Test out the services:
````
curl http://127.0.0.1:9000/add/3/5

curl http://127.0.0.1:9000/multiply/3/5
````




















##### This project has been generated by the lagom/lagom-scala.g8 template. For instructions on running and testing the project, see https://www.lagomframework.com/get-started-scala.html.
