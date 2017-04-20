package fun.lambda.coursecrawler

sealed abstract class Semester(val value: Int)

case object Semester {
  final case object FirstSemester extends Semester(1)
  final case object SummerSemester extends Semester(2)
  final case object SecondSemester extends Semester(3)
  final case object WinterSemester extends Semester(4)
}
