
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
//    "-Xfatal-warnings", //TODO remove
    "-Yno-adapted-args",
    "-Ywarn-value-discard",
//    "-Ywarn-unused-import",
    "-Ypartial-unification"
  ),
  resolvers ++= Resolver.jcenterRepo :: Resolver.sonatypeRepo("releases") :: Nil,
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.0")
))


cancelable in Global := true

lazy val root = project.in(file("."))
  .settings(
    name := "hufs-course-crawler",
    libraryDependencies ++= (oldDep ++ newDep)
  )


lazy val newDep = Seq(
  D.catsEffect,
  D.catsMTL,
  D.meowMTL,
  D.fs2Core,
  D.fs2Io,
  D.sttp,
  D.zio,
  D.zioInteropCats,
  D.shapeless,
  D.http4sClient,
  D.http4sCirce,
  D.circeGeneric,
  D.circeGenericExtras,
  D.extruderCore,
  D.extruderCatsEffect,
  D.extruderCirceYaml,
)

lazy val D = new {
  def fs2(module: String) = "co.fs2" %% s"fs2-$module" % V.fs2
  def circe(module: String) = "io.circe" %% s"circe-$module" % V.circe
  def http4s(module: String) = "org.http4s" %% s"http4s-$module" % V.http4s
  def extruder(module: String) = "io.extruder" %% s"extruder-$module" % V.extruder

  object V {
    val catsEffect = "1.2.0"
    val catsMTL    = "0.4.0"
    val meowMTL    = "0.2.0"
    val zio        = "1.0-RC3"
    val fs2        = "1.0.3"
    val sttp       = "1.5.12"
    val shapeless  = "2.3.3"

    val circe      = "0.11.1"
    val http4s     = "0.20.0-M6"
    val extruder   = "0.10.0"
  }

  val catsEffect = "org.typelevel" %% "cats-effect" % V.catsEffect
  val catsMTL    = "org.typelevel" %% "cats-mtl-core" % V.catsMTL
  val meowMTL    = "com.olegpy" %% "meow-mtl" % V.meowMTL
  val zio        = "org.scalaz" %% "scalaz-zio" % V.zio
  val zioInteropCats = "org.scalaz" %% "scalaz-zio-interop-cats" % V.zio
  val fs2Core    = fs2("core")
  val fs2Io      = fs2("io")
  val sttp       = "com.softwaremill.sttp" %% "async-http-client-backend-fs2" % V.sttp
  val shapeless  = "com.chuusai" %% "shapeless" % V.shapeless
  val http4sClient       = http4s("blaze-client")
  val http4sCirce        = http4s("circe")
  val circeGeneric       = circe("generic")
  val circeGenericExtras = circe("generic-extras")
  val extruderCore = extruder("core")
  val extruderCatsEffect = extruder("cats-effect")
  val extruderCirceYaml = extruder("circe")


}

def oldDep =
  "org.scalatest"              %% "scalatest"       % "3.0.1" % Test ::
      "org.scalaj"                 %% "scalaj-http"     % "2.3.0" ::
      "com.lihaoyi"                %% "fastparse"       % "0.4.4" ::
      "net.ruippeixotog"           %% "scala-scraper"   % "2.1.0" ::
      "com.typesafe.play"          %% "play-json"       % "2.6.8" ::
      "com.typesafe"               %  "config"          % "1.3.1" ::
      "ch.qos.logback"             %  "logback-classic" % "1.2.3" ::
      "com.typesafe.scala-logging" %% "scala-logging"   % "3.8.0" :: Nil
