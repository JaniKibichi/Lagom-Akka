organization in ThisBuild := "com.github.janikibichi.learnakka"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `lagomscala` = (project in file("."))
  .aggregate(`lagomscala-api`, `lagomscala-impl`,`lagomscala-stream-api`, `lagomscala-stream-impl`,
    `token-api`,`token-impl`,`consumer-api`,`consumer-impl`)


lazy val `lagomscala-api` = (project in file("lagomscala-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagomscala-impl` = (project in file("lagomscala-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`lagomscala-api`)


lazy val `lagomscala-stream-api` = (project in file("lagomscala-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagomscala-stream-impl` = (project in file("lagomscala-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`lagomscala-stream-api`, `lagomscala-api`)


lagomUnmanagedServices in ThisBuild := Map("login" -> "http://127.0.0.1:8888")


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