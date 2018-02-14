package fun.lambda.coursecrawler

sealed abstract class Semester(v: Int)

object Semester {
  import play.api.libs.json._

  final object FirstSemester extends Semester(1)
  final object SummerSemester extends Semester(2)
  final object SecondSemester extends Semester(3)
  final object WinterSemester extends Semester(4)

  def toInt(s: Semester): Int = s match {
    case FirstSemester  => 1
    case SummerSemester => 2
    case SecondSemester => 3
    case WinterSemester => 4
    case _ => throw new NoSuchElementException
  }

  def fromInt(i: Int): Semester = i match {
    case 1 => FirstSemester
    case 2 => SummerSemester
    case 3 => SecondSemester
    case 4 => WinterSemester
    case _ => throw new NoSuchElementException
  }

  implicit val semesterReads: Reads[Semester] = (__.read[Int].map(fromInt(_)))
  implicit val semesterWrites = new Writes[Semester] {
    def writes(semester: Semester) = JsNumber(toInt(semester))
  }
}
