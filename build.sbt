
inThisBuild(List(
  organization := "fun.lambda",
  scalaVersion := "2.12.4",
  version      := "0.1.0-SNAPSHOT"
))

name := "hufs-campus-utils"

lazy val lib = (project in file("lib"))
  .settings(
    libraryDependencies ++=
      "org.scalatest"     %% "scalatest"     % "3.0.1" ::
      "org.scalaj"        %% "scalaj-http"   % "2.3.0" ::
      "com.lihaoyi"       %% "fastparse"     % "0.4.4" ::
      "net.ruippeixotog"  %% "scala-scraper" % "2.1.0" ::
      "com.typesafe.play" %% "play-json"     % "2.6.8" ::
      "com.typesafe"      %  "config"        % "1.3.1" :: Nil
  )

//publishTo := Some("Lambda Fun Repo" at "https://repo.lambda.fun/artifactory/sbt-dev-local/")
//credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
