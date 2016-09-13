name := """play-reactive-couchbase"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

routesGenerator := InjectedRoutesGenerator

resolvers += "ReactiveCouchbase Releases" at "https://raw.github.com/ReactiveCouchbase/repository/master/releases/"

libraryDependencies ++= Seq(
  "org.reactivecouchbase" %% "reactivecouchbase-core" % "0.3",
  "org.reactivecouchbase" %% "reactivecouchbase-play" % "0.3",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test" withSources(),
  "org.scalaz" %% "scalaz-core" % "7.2.6",
  "org.mockito" % "mockito-core" % "1.10.19"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalacOptions in ThisBuild ++= Seq("-feature", "-language:postfixOps")
