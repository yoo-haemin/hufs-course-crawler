
inThisBuild(List(
  organization  := "jj.ci",
  scalaVersion  := "2.12.8",
  version       := "0.2.0",
  licenses      := Seq("Apache-2.0" -> new java.net.URL("https://www.apache.org/licenses/LICENSE-2.0")),
  scalacOptions ++= Vector(
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-Xlint",
    "-Xfatal-warnings",
    "-Yno-adapted-args",
    "-Ywarn-value-discard",
    "-Ywarn-unused-import",
    "-Ypartial-unification"
  )
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
  D.shapeless,
  D.http4sClient,
  D.http4sCirce,
  D.circeGeneric,
  D.circeGenericExtras,
)

lazy val D = new {
  def fs2(module: String) = "co.fs2" %% s"fs2-$module" % V.fs2
  def circe(module: String) = "io.circe" %% s"circe-$module" % V.circe
  def http4s(module: String) = "org.http4s" %% s"http4s-$module" % V.http4s

  object V {
    val catsEffect = "1.2.0"
    val fs2        = "1.0.3"
    val shapeless  = "2.3.3"

    val circe      = "0.11.1"
    val http4s     = "0.20.0-M6"
  }

  val catsEffect = "org.typelevel" %% "cats-effect" % "1.2.0"

  val fs2Core    = fs2("core")
  val fs2Io      = fs2("io")

  val shapeless  = "com.chuusai" %% "shapeless" % V.shapeless

  val http4sClient       = http4s("blaze-client")
  val http4sCirce        = http4s("circe")
  val circeGeneric       = circe("generic")
  val circeGenericExtras = circe("generic-extras")


  val pureconfig = ???
  val refined = ???
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
