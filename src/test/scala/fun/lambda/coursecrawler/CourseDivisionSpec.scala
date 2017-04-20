package fun.lambda.coursecrawler

import java.io.{ File, PrintWriter }
import org.scalatest._
import play.api.libs.json._

class CourseDivisionSpec extends FlatSpec with Matchers {
  val eiccPage2017Source = scala.io.Source.fromFile("assets/2017-01-EICC.html")
  val eiccPage2017 = try eiccPage2017Source.mkString finally eiccPage2017Source.close()
  val courseEicc2017 = Course.fromSchoolCourseBody(eiccPage2017)

  "Writing courses from EICC 2017 and reading from it" should "result in the same thing" in {
    val eiccMajor = Major("EICC", "EICC", "EICCCODE", courseEicc2017)
    println(eiccMajor)
    val eiccJson = Json.toJson(eiccMajor)
    val jsonFileWrite = new PrintWriter(new File("assets/2017-01-EICC.json"))
    jsonFileWrite.write(Json.prettyPrint(eiccJson))
    jsonFileWrite.close

    val jsonFileSource = io.Source.fromFile("assets/2017-01-EICC.json")
    val jsonFileString = try jsonFileSource.mkString finally jsonFileSource.close()
    val jsonFileJson: JsResult[Major] = Json.parse(jsonFileString).validate[Major]

    Major.unapply(jsonFileJson.get).get._4.size shouldEqual Major.unapply(eiccMajor).get._4.size
  }
}
