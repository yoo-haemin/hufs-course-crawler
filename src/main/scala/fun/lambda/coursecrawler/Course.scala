package fun.lambda.coursecrawler


import java.time.DayOfWeek
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import scala.util.Try
//import scala.util.matching.Regex

case class Course(
  courseType             : String,
  courseYear             : Option[Int],
  courseNo               : String,
  courseName1            : String,
  courseName2            : Option[String],
  required               : Boolean,
  online                 : Boolean,
  foreignLanguage        : Boolean,
  teamTeaching           : Boolean,
  professorNameMain      : String,
  professorNameAdditional: Option[String],
  creditHours            : Int,
  courseHours            : Int,
  courseTime             : Map[DayOfWeek, CourseTime],
  currentlyEnrolled      : Int,
  maximumEnrolled        : Option[Int],
  note                   : Option[String])

object Course {
  implicit val courseTimeMapReads: Reads[Map[DayOfWeek, CourseTime]] = new Reads[Map[DayOfWeek, CourseTime]] {
    def reads(json: JsValue): JsResult[Map[DayOfWeek, CourseTime]] = {
      JsSuccess(Map(
        DayOfWeek.MONDAY    -> (json \ "Mon").asOpt[CourseTime],
        DayOfWeek.TUESDAY   -> (json \ "Tue").asOpt[CourseTime],
        DayOfWeek.WEDNESDAY -> (json \ "Wed").asOpt[CourseTime],
        DayOfWeek.THURSDAY  -> (json \ "Thu").asOpt[CourseTime],
        DayOfWeek.FRIDAY    -> (json \ "Fri").asOpt[CourseTime],
        DayOfWeek.SATURDAY  -> (json \ "Sat").asOpt[CourseTime],
        DayOfWeek.SUNDAY    -> (json \ "Sun").asOpt[CourseTime]
      )
        .filter(p => p._2.isDefined)
        .map { case (k, v) => k -> v.get })
    }
  }

  implicit val courseTimeMapWrites = new Writes[Map[DayOfWeek, CourseTime]] {
    def writes(m: Map[DayOfWeek,CourseTime]): JsValue = {
      (Json.obj() /: m.map { case (k, v) => k match {
        case DayOfWeek.MONDAY    => "Mon" -> Json.toJson(v)
        case DayOfWeek.TUESDAY   => "Tue" -> Json.toJson(v)
        case DayOfWeek.WEDNESDAY => "Wed" -> Json.toJson(v)
        case DayOfWeek.THURSDAY  => "Thu" -> Json.toJson(v)
        case DayOfWeek.FRIDAY    => "Fri" -> Json.toJson(v)
        case DayOfWeek.SATURDAY  => "Sat" -> Json.toJson(v)
        case DayOfWeek.SUNDAY    => "Sun" -> Json.toJson(v) }
      })((acc, p) => acc + p)
    }
  }

  val courseReads: Reads[Course] = (
    (__ \ "courseType")               .read[String] and
      (__ \ "courseYear")             .readNullable[Int] and
      (__ \ "courseNo")               .read[String] and
      (__ \ "courseName1")            .read[String] and
      (__ \ "courseName2")            .readNullable[String] and
      (__ \ "required")               .read[Boolean] and
      (__ \ "online")                 .read[Boolean] and
      (__ \ "foreignLanguage")        .read[Boolean] and
      (__ \ "teamTeaching")           .read[Boolean] and
      (__ \ "professorNameMain")      .read[String] and
      (__ \ "professorNameAdditional").readNullable[String] and
      (__ \ "creditHours")            .read[Int] and
      (__ \ "courseHours")            .read[Int] and
      (__ \ "courseTime")             .read[Map[DayOfWeek,CourseTime]] and
      (__ \ "currentlyEnrolled")      .read[Int] and
      (__ \ "maximumEnrolled")        .readNullable[Int] and
      (__ \ "note")                   .readNullable[String]
  )(Course.apply _)

  val courseWrites: Writes[Course] = (
    (__ \ "courseType")               .write[String] and
      (__ \ "courseYear")             .writeNullable[Int] and
      (__ \ "courseNo")               .write[String] and
      (__ \ "courseName1")            .write[String] and
      (__ \ "courseName2")            .writeNullable[String] and
      (__ \ "required")               .write[Boolean] and
      (__ \ "online")                 .write[Boolean] and
      (__ \ "foreignLanguage")        .write[Boolean] and
      (__ \ "teamTeaching")           .write[Boolean] and
      (__ \ "professorNameMain")      .write[String] and
      (__ \ "professorNameAdditional").writeNullable[String] and
      (__ \ "creditHours")            .write[Int] and
      (__ \ "courseHours")            .write[Int] and
      (__ \ "courseTime")             .write[Map[DayOfWeek,CourseTime]] and
      (__ \ "currentlyEnrolled")      .write[Int] and
      (__ \ "maximumEnrolled")        .writeNullable[Int] and
      (__ \ "note")                   .writeNullable[String]
  )(unlift(Course.unapply))

  implicit val courseFormat: Format[Course] = Format(courseReads, courseWrites)

  def fromSchoolCourseBody(body: String): Array[Course] = {
    def getDayOfWeek(str: String): DayOfWeek = str match {
      case "Mon" => DayOfWeek.MONDAY
      case "Tue" => DayOfWeek.TUESDAY
      case "Wed" => DayOfWeek.WEDNESDAY
      case "Thu" => DayOfWeek.THURSDAY
      case "Fri" => DayOfWeek.FRIDAY
      case "Sat" => DayOfWeek.SATURDAY
      case "Sun" => DayOfWeek.SUNDAY
    }

    //<td>(.*)<\/td>\s*<td>(\d)?<\/td>\s*<td>([A-Z]{1,2}\d{5}[A-Za-z0-9]\d{1,2})<\/td>\s*<td align="left">\s*<!--.*-->\s*<div\s*.*\s*.\s*<font class="txt_navy">(.*)<\/font><br>\s*(?:<font class=['"]txt_gray8['"]>\((.*)\)<\/font>\s*)?<\/div>\s*<\/td>\s*<td>\s*.*\s*.*\s*<\/td>\s*(?:<td>(.*)<\/td>\s*)(?:<td>(.*)<\/td>\s*)(?:<td>(.*)<\/td>\s*)(?:<td>(.*)<\/td>\s*)<td align="left">(.*)\s*(?:<br>\s*<font class="txt_gray8">\((.*)\s*\)<\/font>)?\s*<\/td>\s*<td>(\d)<\/td>\s*<td>(\d)<\/td>\s*<td align="left">.*<br><font class="txt_gray8">\(.*?\)<\/font><\/td>\s*<td>(\d{1,})&nbsp;\/&nbsp;((?:\d{1,})|(?:없음))<\/td>\s*<td align="left">(.+)?<br>
    val regexStrings = Seq(
      //Regex part 1, capatures the following: courseType, courseYear, courseNo, courseName1, courseName2
      """<td>(.*)<\/td>\s*<td>(\d)?<\/td>\s*<td>([A-Z]{1,2}\d{5}[A-Za-z0-9]\d{1,2})<\/td>\s*<td align="left">\s*<!--.*-->\s*<div\s*.*\s*.\s*<font class="txt_navy">(.*)<\/font><br>\s*(?:<font class=['"]txt_gray8['"]>\((.*)\)<\/font>\s*)?<\/div>\s*<\/td>\s*<td>\s*.*\s*.*\s*<\/td>\s*""",

      //Regex part 2: required, online, foreignLanguage, teamteaching, professorNameMain, professornameadditional, creditHours, courseHours
      """(?:<td>(.*)<\/td>\s*)(?:<td>(.*)<\/td>\s*)(?:<td>(.*)<\/td>\s*)(?:<td>(.*)<\/td>\s*)<td align="left">(.*)\s*(?:<br>\s*<font class="txt_gray8">\((.*)\s*\)<\/font>)?\s*<\/td>\s*<td>(\d)<\/td>\s*<td>(\d)<\/td>\s*""",

      //Regex part 2: courseTime
      """<td align="left">.*<br><font class="txt_gray8">\(.*?\)<\/font><\/td>\s*""",

      //Regex part 3: rest
      """<td>(\d{1,})&nbsp;\/&nbsp;((?:\d{1,})|(?:없음))<\/td>\s*<td align="left">(.+)?<br>"""
    )

    val courseRegex = regexStrings.reduceLeft(_+_).r
    val courseList = (for {
      c <- courseRegex findAllMatchIn body
    } yield c).toSeq
    val courseListSize = (for {
      m <- """<td class="table_green">(\d){1,}<\/td>""".r findAllMatchIn body
    } yield m.subgroups).toArray.size

    //Make sure we have read all the courses
    if (courseList.size != courseListSize) {
      println(s"Total size of the body: ${courseListSize}, but regex only matched ${courseList.size}!\nMore info:")
      val regexFailBodyInfo = """<option\s*value=".*"\s*selected""".r findAllMatchIn body
      regexFailBodyInfo.foreach(println)
      assert(false)
    }

    courseList.map { courseMatchOriginal =>
      val courseMatch = courseMatchOriginal.subgroups
      val timeRegex = """(Mon|Tue|Wed|Thu|Fri|Sat|Sun)\s*(\d{1,2})\s*(\d{0,2})\s*(\d{0,2})\s*(\d{0,2})\s*(\d{0,2})\s*(\d{0,2})\s*(?:\((.*?)\))?""".r
      val timeList = timeRegex.findAllMatchIn(courseMatchOriginal.matched).map(_.subgroups.toArray).toArray
      var i = timeList.length
      while (i > 0) {
        timeList(i - 1).lastOption match {
          case Some(null) => timeList(i - 1)(timeList(i).length - 1) = timeList(i).last
          case _ => ()
        }
        i -= 1
      }

      val timeMap = (Map[DayOfWeek,CourseTime]() /: timeList) { (acc, xs) =>
        acc + (getDayOfWeek(xs(0)) -> new CourseTime(xs.tail.init, xs.tail.last))
      }

      Course(
        courseMatch(0),        //courseType
        Try(courseMatch(1).toInt).toOption,  //courseYear
        courseMatch(2),        //courseNo
        courseMatch(3),        //courseName1
        if (courseMatch(4) != null) Some(courseMatch(4)) else None, //courseName2
        if (courseMatch(5).length > 0) true else false, //required
        if (courseMatch(6).length > 0) true else false, //online
        if (courseMatch(7).length > 0) true else false, //foreign language
        if (courseMatch(8).length > 0) true else false, //team teaching
        courseMatch(9),        //professornamemain
        if (courseMatch(10) != null) Some(courseMatch(10)) else None, //professornameadditional
        courseMatch(11).toInt, //credithours
        courseMatch(12).toInt, //coursehours
        timeMap,
        courseMatch(13).toInt, //currentlyenrolled
        Try(courseMatch(14).toInt).toOption, //maximumenrolled
        if (courseMatch(15) != null) Some(courseMatch(15)) else None //note
      )
    }.toArray
  }
}
