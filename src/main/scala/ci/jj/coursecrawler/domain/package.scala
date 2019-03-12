package ci.jj.coursecrawler

import java.time.DayOfWeek

package domain {

  final case class Course(
      openYear: Int,
      openSemester: Semester,
      openDept: String,
      openCampus: String,
      area: String,
      suggestedYear: Option[Int],
      courseNumber: String,
      subjectName: String,
      professorNamePrimary: String,
      professorNameSecondary: String,
      creditHour: Int,
      classHour: Int,
      courseTime: List[CourseTime],
      studentEnrolled: Option[Int],
      maxEnrolled: Option[Int],
      note: String,
      required: Boolean,
      online: Boolean,
      foreignLanguage: Boolean,
      teamTeaching: Boolean
  )

  final case class CourseTime(dow: DayOfWeek, time: List[Int], room: String)

  sealed abstract class Semester(val value: Int)
  object Semester {
    case object FirstSemester extends Semester(1)
    case object SummerSemester extends Semester(2)
    case object SecondSemester extends Semester(3)
    case object WinterSemester extends Semester(4)
  }

}
