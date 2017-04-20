package fun.lambda.coursecrawler

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

trait CourseDivision
case class Major(name1: String, name2: String, code: String, courses: Traversable[Course]) extends CourseDivision

object Major {
  implicit val majorReads: Reads[Major] = (
    (__ \ "name1").read[String] and
      (__ \ "name2").read[String] and
      (__ \ "code").read[String] and
      (__ \ "courses").read[Traversable[Course]]
  )(Major.apply _)

  implicit val majorWrites: Writes[Major] = (
    (JsPath \ "name1").write[String] and
      (JsPath \ "name2").write[String] and
      (JsPath \ "code").write[String] and
      (JsPath \ "courses").write[Traversable[Course]]
  )(unlift(Major.unapply))
}

case class LiberalArts(name1: String, code: String, courses: Traversable[Course]) extends CourseDivision

object LiberalArts {
  implicit val majorReads: Reads[LiberalArts] = (
    (__ \ "name").read[String] and
      (__ \ "code").read[String] and
      (__ \ "courses").read[Traversable[Course]]
  )(LiberalArts.apply _)

  implicit val majorWrites: Writes[LiberalArts] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "code").write[String] and
      (JsPath \ "courses").write[Traversable[Course]]
  )(unlift(LiberalArts.unapply))
}
