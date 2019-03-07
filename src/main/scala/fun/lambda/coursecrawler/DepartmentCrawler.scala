package fun.lambda.coursecrawler

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import play.api.libs.json._

case class Department(value: String, text: String)
sealed abstract class DepartmentType
object Major extends DepartmentType
object LiberalArts extends DepartmentType

//Receives a detailed list of parameters and creates JSON
object DepartmentCrawler {
  private val url = """http://webs.hufs.ac.kr:8989/src08/jsp/lecture/LECTURE2020L.jsp"""
  private val browser = JsoupBrowser()

  def toCourseList(year: Int, semester: Semester, deptType: DepartmentType, deptParam: Department) = {
    val postParam = Map(
      "tab_lang" -> "K",
      "type" -> "",
      "ag_ledg_year" -> year.toString,
      "ag_ledg_sessn" -> Semester.toInt(semester).toString,
      "ag_org_sect" -> "A",
      "campus_sect" -> "H1",
      ) ++ (
      deptType match {
        case Major => Map("gubun" -> "1", "ag_crs_strct_cd" -> deptParam.value)
        case LiberalArts => Map("gubun" -> "2", "ag_compt_fld_cd" -> deptParam.value)
      })

    val body = browser.post(url, postParam)

    val courseList = (body >> element("#premier1") >> elementList("tr")).tail.map { course =>
      (course >> elementList("td")).map { elem =>
        elem >> text("td") match {
          case s if s.length > 0 => s
          case s if (elem >> elementList("img")).length > 0 => "true"
          case _ => ""
        }
      }
    }

    courseList.map(xs =>
      Course.fromList(openYear = year,
                      openDept = deptParam.text,
                      openSemester = semester,
                      openCampus = "Seoul",
                      text = xs))
  }

  def toJson(year: Int, semester: Semester, deptType: DepartmentType, deptParam: Department) =
    Json.toJson(toCourseList(year, semester, deptType, deptParam))
}
