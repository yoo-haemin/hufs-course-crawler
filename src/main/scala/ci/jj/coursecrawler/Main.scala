package ci.jj.coursecrawler

import cats.syntax.applicative._
import ci.jj.coursecrawler.config.AppConfig
import ci.jj.coursecrawler.domain.YearSemester
import ci.jj.coursecrawler.domain.Semester
import scalaz.zio.{ TaskR, UIO, ZIO, Runtime }
import scalaz.zio.interop.catz._
import scalaz.zio.interop.catz.mtl._

import java.time.Year


object Main extends CatsApp {

  case class AppEnv(environment: Environment, appConfig: AppConfig)

  type MainEffect[A] = TaskR[AppEnv, A]

  val config: UIO[AppConfig] = 
    AppConfig(
      YearSemester(Year.of(2019), Semester.FirstSemester)
    ).pure[UIO]

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {
    config.flatMap { conf =>
      implicit val rt: Runtime[AppEnv] = new Runtime[AppEnv] {
        override val Environment = AppEnv(runtime.Environment, conf)
        override val Platform = runtime.Platform
      }

      new MainStream[MainEffect]
          .run
          // .stream
          // .compile
          // .drain
          .provideSome { AppEnv(_: Environment, conf) }
          .fold(_ => 1, _ => 0)
    }

  }
}