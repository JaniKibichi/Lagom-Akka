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























##### This project has been generated by the lagom/lagom-scala.g8 template. For instructions on running and testing the project, see https://www.lagomframework.com/get-started-scala.html.
