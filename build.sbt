import Dependencies._

name := "HUFS Course Crawler"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "fun.lambda",
      scalaVersion := "2.12.2",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies ++= 
      scalaTest % Test ::
      "org.scalaj" %% "scalaj-http" % "2.3.0" ::
      "com.lihaoyi" %% "fastparse" % "0.4.3" ::
      "net.ruippeixotog" %% "scala-scraper" % "2.0.0" ::
      "com.typesafe.play" %% "play-json" % "2.6.2" ::
      Nil
  )

publishTo := Some("Artifactory Realm" at "https://repo.lambda.fun/artifactory/sbt-dev-local/")
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"
