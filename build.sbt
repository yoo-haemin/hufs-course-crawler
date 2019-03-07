
inThisBuild(List(
  organization := "jj.ci",
  scalaVersion := "2.12.8",
  version      := "0.2.0",
  licenses     := Seq("Apache-2.0" -> new java.net.URL("https://www.apache.org/licenses/LICENSE-2.0"))
))

cancelable in Global := true

lazy val root = project.in(file("."))
  .settings(
    name := "hufs-course-crawler",
    libraryDependencies ++= (oldDep ++ newDep)
  )


lazy val newDep = Seq(
  D.catsEffect,
  D.fs2Core,
  D.fs2Io,
)

lazy val D = new {
  def fs2(module: Symbol) = "co.fs2" %% s"fs2-${module.name}" % V.fs2

  object V {
    val catsEffect = "1.2.0"
    val fs2        = "1.0.3"
  }

  val catsEffect = "org.typelevel" %% "cats-effect" % "1.2.0"

  val fs2Core    = fs2('core)
  val fs2Io      = fs2('io)

}

lazy val oldDep =
  "org.scalatest"              %% "scalatest"       % "3.0.1" % Test ::
      "org.scalaj"                 %% "scalaj-http"     % "2.3.0" ::
      "com.lihaoyi"                %% "fastparse"       % "0.4.4" ::
      "net.ruippeixotog"           %% "scala-scraper"   % "2.1.0" ::
      "com.typesafe.play"          %% "play-json"       % "2.6.8" ::
      "com.typesafe"               %  "config"          % "1.3.1" ::
      "ch.qos.logback"             %  "logback-classic" % "1.2.3" ::
      "com.typesafe.scala-logging" %% "scala-logging"   % "3.8.0" :: Nil
