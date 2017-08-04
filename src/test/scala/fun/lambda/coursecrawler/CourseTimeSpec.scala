package fun.lambda.coursecrawler

import java.time.DayOfWeek._
import org.scalatest._

class CourseTimeSpec extends FlatSpec with Matchers {
  import fun.lambda.coursecrawler.CourseTime._

  "fromHtmlTag function" should "be able to extract 2017-03 T05202202" in {
    fromHtmlTag("수 3 4 금 3 4  (-)<br>(Wed 3 4 Fri 3 4  (-))") shouldEqual
    Seq(
      CourseTime(WEDNESDAY, Seq(3, 4), "-"),
      CourseTime(FRIDAY, Seq(3, 4), "-")
    )
  }

  it should "be able to extract 2017-02 Y12101201" in {
    fromHtmlTag("월 1 2 3 화 1 2 3 수 1 2 3 목 1 2 3 금 1 2 3 (3401)<br>(Mon 1 2 3 Tue 1 2 3 Wed 1 2 3 Thu 1 2 3 Fri 1 2 3 (3401))") shouldEqual
    Seq(
      CourseTime(MONDAY, Seq(1, 2, 3), "3401"),
      CourseTime(TUESDAY, Seq(1, 2, 3), "3401"),
      CourseTime(WEDNESDAY, Seq(1, 2, 3), "3401"),
      CourseTime(THURSDAY, Seq(1, 2, 3), "3401"),
      CourseTime(FRIDAY, Seq(1, 2, 3), "3401")
    )
  }

  it should "be able to extract 2017-01 D02103105" in {
    fromHtmlTag("금 4 5 6 (사이버관 대강당)<br>(Fri 4 5 6 (사이버관 대강당))") shouldEqual
    Seq(
      CourseTime(FRIDAY, (4 to 6), "사이버관 대강당")
    )
  }
}
