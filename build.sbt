
inThisBuild(List(
  organization := "fun.lambda",
  scalaVersion := "2.12.4",
  version      := "0.1.0",
  licenses     := Seq(("WTFPL"      -> new java.net.URL("http://www.wtfpl.net/txt/copying")),
                      ("Apache-2.0" -> new java.net.URL("https://www.apache.org/licenses/LICENSE-2.0")))
))

name := "hufs-campus-utils"

cancelable in Global := true

lazy val root = project.in(file(".")).aggregate(lib)

lazy val lib = (project in file("lib"))
  .settings(
    name := "hufs-course-crawler",
    libraryDependencies ++=
      "org.scalatest"              %% "scalatest"       % "3.0.1" ::
      "org.scalaj"                 %% "scalaj-http"     % "2.3.0" ::
      "com.lihaoyi"                %% "fastparse"       % "0.4.4" ::
      "net.ruippeixotog"           %% "scala-scraper"   % "2.1.0" ::
      "com.typesafe.play"          %% "play-json"       % "2.6.8" ::
      "com.typesafe"               %  "config"          % "1.3.1" ::
      "ch.qos.logback"             %  "logback-classic" % "1.2.3" ::
      "com.typesafe.scala-logging" %% "scala-logging"   % "3.8.0" :: Nil
  )


