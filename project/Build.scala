import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "expedition"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.2.0",
    "com.google.inject" % "guice" % "3.0",
    "net.codingwell" %% "scala-guice" % "3.0.2",
    jdbc,
    anorm,

    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.0" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalacOptions += "-feature"
  )

}
