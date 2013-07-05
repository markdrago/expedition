import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "expedition"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalacOptions += "-feature"
  )

}
