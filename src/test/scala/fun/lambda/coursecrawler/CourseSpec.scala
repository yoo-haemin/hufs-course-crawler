package fun.lambda.coursecrawler

import org.scalatest._
import play.api.libs.json._
import play.api.libs.json.Reads._
import java.time.DayOfWeek
import scala.collection.immutable.SortedSet

class CourseSpec extends FlatSpec with Matchers {
  val eiccPage2017Source = scala.io.Source.fromFile("assets/2017-01-EICC.html")
  val eiccPage2017 = try eiccPage2017Source.mkString finally eiccPage2017Source.close()
  val courseEicc2017 = Course.fromSchoolCourseBody(eiccPage2017)

  "Extracting Courses from 2017-01 EICC class" should "be of length 67" in {
    courseEicc2017.size shouldEqual 67
  }

  val sampleTimeJsonSource = scala.io.Source.fromFile("assets/timeSample.json")
  val sampleTimeJsonString = try sampleTimeJsonSource.mkString finally sampleTimeJsonSource.close()
  val sampleTimeJson = Json.parse(sampleTimeJsonString)

  import Course._
  "Reading time from Monday" should "be 1, 2, 3 and \"3503\"" in {
    val json = sampleTimeJson
    (json \ "Mon").asOpt[CourseTime] shouldEqual Some(CourseTime(SortedSet(1, 2, 3), "3503"))
  }

  "Reading time from Friday" should "have nothing" in {
    val json = sampleTimeJson
    (json \ "Fri").asOpt[CourseTime] shouldEqual None
  }

  "Reading Time from sample json" should "validate" in {
    val courseResult: JsResult[Map[DayOfWeek,CourseTime]] = sampleTimeJson.validate[Map[DayOfWeek,CourseTime]]
    courseResult match {
      case s: JsSuccess[Map[DayOfWeek,CourseTime]] => assert(s.get.size > 0)
      case e: JsError => fail("Parse Errors: " + JsError.toJson(e).toString())
    }
  }

  val sampleCourseJsonSource = scala.io.Source.fromFile("assets/courseSample.json")
  val sampleCourseJsonString = try sampleCourseJsonSource.mkString finally sampleCourseJsonSource.close()
  val sampleCourseJson = Json.parse(sampleCourseJsonString)

  "Reading Courses from sample json" should "validate" in {
    val courseResult: JsResult[Course] = sampleCourseJson.validate[Course]
    courseResult match {
      case s: JsSuccess[Course] => assert(Course.unapply(s.get).get._14.size > 0)
      case e: JsError => fail(JsError.toJson(e).toString())
    }
  }

  "Writing a sample json and reading from it" should "result in the same thing" in {
    val sample = Course(
      "전공",Some(1),"D03208401","Business Law(기업법)",Some("Business Law"),true,false,true,false,"Yoon Soo Kim",Some("Jamie Kim"),
      3,3,Map(DayOfWeek.MONDAY -> CourseTime(SortedSet(1, 2, 3),"3503")),48,Some(61),Some("1전공자만 수강"))

    val sample2JsonStr = Json.prettyPrint(Json.toJson(sample))
    Json.parse(sample2JsonStr).validate[Course] shouldEqual JsSuccess(sample)
  }
}
