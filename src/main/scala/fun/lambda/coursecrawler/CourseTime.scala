package fun.lambda.coursecrawler

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import scala.collection.immutable.SortedSet
import scala.util.Try

case class CourseTime(time: SortedSet[Int], classroom: String) {
  def this(time: Seq[String], classroom: String) =
    this((SortedSet[Int]() /: time)((s, t) => Try(t.toInt).toOption match {
        case Some(i) => s + i
        case None => s
    }), classroom)
}

object CourseTime {
  implicit val courseTimeReads: Reads[CourseTime] = (
    (__ \ "time").read[SortedSet[Int]] and
      (__ \ "class").read[String]
  )(CourseTime.apply _)

  implicit val courseTimeWrites: Writes[CourseTime] = (
    (JsPath \ "time").write[SortedSet[Int]] and
      (JsPath \ "class").write[String]
  )(unlift(CourseTime.unapply))
}

