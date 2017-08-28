package fun.lambda.coursecrawler

import scala.util.Try
import scala.util.matching.Regex

case class Course(
  openYear: Int,
  openSemester: Semester,
  openDept: String,
  openCampus: String,
  area: String,
  suggestedYear: Option[Int],
  courseNumber: String,
  subjectName: String,
  professorNameMain: String,
  professorNameAdditional: String,
  creditHour: Int,
  classHour: Int,
  time: Seq[CourseTime],
  applyNo: Option[Int],
  maxNo: Option[Int],
  note: String,
  required: Boolean,
  online: Boolean,
  foreignLanguage: Boolean,
  teamTeaching: Boolean
)

object Course {
  import play.api.libs.json._

  implicit def courseReads = Json.reads[Course]
  implicit def courseWrites = Json.writes[Course]
  implicit def courseFormat = Json.format[Course]

  def fromList(openYear: Int, openDept: String, openSemester: Semester, openCampus: String, text: List[String]) = {
    def strToBool(s: String): Boolean = s match {
      case "true" => true
      case _ => false
    }

    val profNameR = """(.*)\s\((.*)\)""".r

    val (profNameMain, profNameAdditional) = text(10) match {
      case profNameR(main, add) if add != "-" => main -> add
      case profNameR(main, add) if add == "-" => main -> ""
      case _ => text(10) -> ""
    }

    def extractNo(r: Regex) = Try(r.findAllMatchIn(text(14)).toList.head.group(1)).toOption.map(_.toInt)
    val applyNo = extractNo("""(\d+) \/""".r)
    val maxNo = extractNo("""\/ (\d+)""".r)

    val newStuff = Course(
      openYear = openYear,
      openSemester = openSemester,
      openDept = openDept,
      openCampus = openCampus,
      area = text(1),
      suggestedYear = Try(text(2).toInt).toOption,
      courseNumber = text(3),
      subjectName = text(4),
      professorNameMain = profNameMain,
      professorNameAdditional = profNameAdditional,
      creditHour = text(11).toInt,
      classHour = text(12).toInt,
      time = CourseTime.fromHtmlTag(text(13)),
      applyNo = applyNo,
      maxNo = maxNo,
      note = text(15),

      required = strToBool(text(6)),
      online = strToBool(text(7)),
      foreignLanguage = strToBool(text(8)),
      teamTeaching = strToBool(text(9))
    )

    newStuff
  }
}
