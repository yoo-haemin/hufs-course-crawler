package fun.lambda.coursecrawler

import play.api.libs.json.{ JsValue, Json }
import scalaj.http._

object CourseAreaCrawler {
  /**
    * 학기 정보를 받고 각각의 전공(교양영역 미포함)에 대한 정보를 내놓음
    */
  def extractSemesterMajorCode(body: String): Option[Array[Seq[String]]] = {
    val majorCourseRegex = """<option value="(A.{2,4}_H1)"\s*(?: selected="")*>\s*&nbsp;\(서울\)&nbsp;-&nbsp;(.*)&nbsp;\((.*)\)&nbsp;""".r
    val majorFullRegex = """(?:<option value="(A.{2,4}_H1)"\s*(?: selected="")*>\s*&nbsp;\(서울\)&nbsp;-&nbsp;(.*)&nbsp;\((.*)\)&nbsp;\s*<\/option>\s*)+""".r
    majorFullRegex.findFirstIn(body).map { body =>
      majorCourseRegex.findAllMatchIn(body).toVector.map { courseMatch =>
        courseMatch.subgroups
      }.toArray
    }
  }

  /**
    *
    */
  def extractSemesterLiberalArtsCode(body: String): Option[Array[Seq[String]]] = {
    val liberalArtsCourseRegex = """<option value {0,1}="(\d{2,3}[A-Z]{0,1}_H1)"\s*>\s*&nbsp;(.*)&nbsp;\s*<\/option>\s*""".r
    val liberalArtsFullRegex =  """(?:<option value {0,1}="(\d{2,3}[A-Z]{0,1}_H1)"\s*>\s*&nbsp;(.*)&nbsp;\s*<\/option>\s*)+""".r
    liberalArtsFullRegex.findFirstIn(body).map { body =>
      liberalArtsCourseRegex.findAllMatchIn(body).toVector.map { courseMatch =>
        courseMatch.subgroups
      }.toArray
    }
  }

  //HUFS course page
  val coursePageUrl = "http://webs.hufs.ac.kr:8989/src08/jsp/lecture/LECTURE2020L.jsp"

  //Base params
  val courseFetchBaseParams = Map(
    "tab_lang" -> "K",
    "type" -> "",
    "ag_org_sect" -> "A", //
    "campus_sect" -> "H1"
  )

  def apply(year: Int, semester: Int): Option[(Array[Major],Array[LiberalArts])] = {
    val params = courseFetchBaseParams ++ Map("ag_ledg_year" -> year.toString, "ag_ledg_sessn" -> semester.toString)
    val body: String = Http(coursePageUrl).postForm.params(params).asString.body
    val areaCodes = for {
      mj <- extractSemesterMajorCode(body)
      la <- extractSemesterLiberalArtsCode(body)
    } yield (mj -> la)

    areaCodes match {
      case t: Some[(Array[Seq[String]], Array[Seq[String]])] => Some((t.get._1.map { mj =>
        Major(mj(1), mj(2), mj(0),
          Course.fromSchoolCourseBody(
            Http(coursePageUrl).postForm.params(params + ("gubun" -> "1") + ("ag_crs_strct_cd" -> mj(0))).asString.body
          ))
        }.toArray) -> (t.get._2.map { la =>
        LiberalArts(la(1), la(0),
          Course.fromSchoolCourseBody(
            Http(coursePageUrl).postForm.params(params + ("gubun" -> "2") + ("ag_compt_fld_cd" -> la(0))).asString.body
          ))
        }.toArray))
      case _ => None
    }
  }

  def toJson(year: Int, semester: Int): Option[JsValue] = {
    val data = apply(year, semester)
    data.map { d =>
      Json.obj(
        "year" -> year,
        "semester" -> semester,
        "major" -> Json.toJson(d._1),
        "liberalArts" -> Json.toJson(d._2)
      )
    }
  }
}
/*
  def createMajorParams(majorParams: Map[String,String]): Map[String,String] =  majorParams ++ Map(
    "gubun" -> "1" //, //전공을 선택
//    "ag_compt_fld_cd" -> "302_H1" //교양을 아무렇게나 정함
  )

  def createLiberalArtsParams(liberalArtsParams: Map[String,String]) = liberalArtsParams ++ Map(
    "gubun" -> "2" //,
//    "ag_crs_strct_cd" -> "AAR01_H1"
  )
 */
