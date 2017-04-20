import Dependencies._

name := """HUFS Course Crawler"""

version := "0.0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
		scalaTest % Test,
		"org.scalaj" %% "scalaj-http" % "2.3.0",
		"com.typesafe.play" %% "play-json" % "2.6.0-M6",
		"org.joda" % "joda-convert" % "1.8.1"
	)
