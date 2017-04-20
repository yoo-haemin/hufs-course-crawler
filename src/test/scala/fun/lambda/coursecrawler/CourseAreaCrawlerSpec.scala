package fun.lambda.coursecrawler

import java.io.{ File, PrintWriter }
import org.scalatest._
import CourseAreaCrawler._
import play.api.libs.json.{ JsResult, Json }
import scalaj.http._

class CourseAreaCrawlerSpec extends FlatSpec with Matchers {
  val eiccPage2017Source = scala.io.Source.fromFile("assets/2017-01-EICC.html")
  val eiccPage2017 = try eiccPage2017Source.mkString finally eiccPage2017Source.close()


  "Extracting Major from 2017-1" should "be of length 68" in {
    extractSemesterMajorCode(eiccPage2017).get.size shouldEqual 68
    //println(extractMajor(eiccPage2017))
  }

  "Extracting Liberal Arts from 2017-1" should "be of length 16" in {
    extractSemesterLiberalArtsCode(eiccPage2017).get.size shouldEqual 16
  }

  val body = Http(coursePageUrl).postForm.params(courseFetchBaseParams + ("ag_ledg_year" -> "2017") + ("ag_ledg_sessn" -> "1")).asString.body
  "the HTTP connection" should "be able to get 2017-01 body" in {
    //println(body)
    body.length shouldNot equal(0)
  }

  "extractMajor" should "be able to extract major from the body just received" in {
    extractSemesterMajorCode(body).get.size shouldEqual(68)
  }

  "extractLiberalArts" should "be able to extract liberal arts from the body just received" in {
    extractSemesterLiberalArtsCode(body).get.size shouldEqual(17)
  }

  val courses201701 = CourseAreaCrawler(2017,1)
}
