import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.2",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies ++= 
      scalaTest % Test ::
      "org.scalaj" %% "scalaj-http" % "2.3.0" ::
      "com.lihaoyi" %% "fastparse" % "0.4.3" ::
      "net.ruippeixotog" %% "scala-scraper" % "2.0.0" ::
      Nil
  )
