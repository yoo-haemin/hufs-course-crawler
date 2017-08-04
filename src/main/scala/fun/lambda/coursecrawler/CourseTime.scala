package fun.lambda.coursecrawler

import fastparse.all._
import fastparse.core.Parsed.{ Failure, Success }

import java.time.DayOfWeek
import scala.util.Try

case class CourseTime(dow: DayOfWeek, time: Seq[Int], room: String)

object CourseTime {
  import play.api.libs.json._
  import scala.language.implicitConversions

  implicit def dayOfWeekString(dow: DayOfWeek): String = dow match {
    case DayOfWeek.MONDAY    => "Mon"
    case DayOfWeek.TUESDAY   => "Tue"
    case DayOfWeek.WEDNESDAY => "Wed"
    case DayOfWeek.THURSDAY  => "Thu"
    case DayOfWeek.FRIDAY    => "Fri"
    case DayOfWeek.SATURDAY  => "Sat"
    case DayOfWeek.SUNDAY    => "Sun"
  }


  object DayOfWeekString {
    def fromString(s: String): DayOfWeek = {
      s match {
        case "Mon" => DayOfWeek.MONDAY
        case "Tue" => DayOfWeek.TUESDAY
        case "Wed" => DayOfWeek.WEDNESDAY
        case "Thu" => DayOfWeek.THURSDAY
        case "Fri" => DayOfWeek.FRIDAY
        case "Sat" => DayOfWeek.SATURDAY
        case "Sun" => DayOfWeek.SUNDAY
        case _ => throw new NoSuchElementException
      }
    }
  }

  implicit val dayOfWeekReads: Reads[DayOfWeek] = (__.read[String].map(DayOfWeekString.fromString(_)))
  implicit val dayOfWeekWrites = new Writes[DayOfWeek] {
    def writes(dow: DayOfWeek) = JsString(dayOfWeekString(dow))
  }

  implicit def courseTimeFormat = Json.format[CourseTime]


  def fromHtmlTag(s: String) = {
    val englishRegex = """\(([(?:Mon)|(?:Tue)|(?:Wed)|(?:Thu)|(?:Fri)|(?:Sat)|(?:Sun)].*)\)""".r
    val str = englishRegex.findAllMatchIn(s).toList.head.group(1)

    val dayOfWeek = P("Mon".! | "Tue".! | "Wed".! | "Thu".! | "Fri".! | "Sat".! | "Sun".!).map {
      DayOfWeekString.fromString
    }
    val time = P( CharIn('0' to '9').rep(1).! )
    val room = P( "(" ~/ CharsWhile(c => c != '(' && c != ')').! ~ ")" )
    val ws = P(" ")

    P( (dayOfWeek ~ ws ~ (time ~ ws).rep ~ ws.rep ~ room.? ).rep ~ End ).map { xs =>
      ((Seq.empty[CourseTime] -> "") /: xs.reverse) {
        case ((acc, lastRoom), (dow, timesString, roomOpt)) =>
          val times = timesString.map(i => Try(i.toInt).toOption).filterNot(_.isEmpty).map(_.get)
          roomOpt match {
            case Some(r) => (acc :+ CourseTime(dow, times, r)) -> r
            case None => (acc :+ CourseTime(dow, times, lastRoom)) -> lastRoom
          }
      }._1.reverse
    }.parse(str) match {
      case Success(xs, _) => xs
      case f @ Failure(_, _, _) => throw new Exception(
        s"CourseTime Parse Error: $str\n$f")
    }
  }
}
