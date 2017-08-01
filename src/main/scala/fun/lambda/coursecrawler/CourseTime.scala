package fun.lambda.coursecrawler

import fastparse.all._
import java.time.DayOfWeek

case class CourseTime(dow: DayOfWeek, time: Seq[Int], room: String)

object CourseTime extends App {
  def fromHtmlTag(s: String) = {
    val englishRegex = """<br>\((.*)\)""".r
    val str = englishRegex.findAllMatchIn(s).toList.head.group(1)
    /*
     화 9 10 목 9 (1307)<br>(Tue 9 10 Thu 9 (1307))
     */
    println(str)


    val dayOfWeek: P[String] = P("Mon".! | "Tue".! | "Wed".! | "Thu".! | "Fri".! | "Sat".!)
    val time: P[String] = P( CharIn('0' to '9').rep.! )
    val room: P[String] = P( CharsWhile(c => c != '(' || c != ')').! )
    val ws = P(" ")

    val fullParser = P( (dayOfWeek ~ ws ~ (time ~ ws).rep ~ room.? ) ~ ws )

    println(fullParser.parse(str))
  }

  fromHtmlTag("화 9 10 목 9 (1307)<br>(Tue 9 10 Thu 9 (1307))")
}
