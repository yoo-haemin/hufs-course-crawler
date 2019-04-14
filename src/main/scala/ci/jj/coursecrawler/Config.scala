package ci.jj.coursecrawler

import ci.jj.coursecrawler.domain.YearSemester

object config {

  final case class AppConfig(
    yearSemester: YearSemester
  )

}
