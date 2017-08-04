package fun.lambda.coursecrawler

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import play.api.libs.json._

import com.typesafe.config.ConfigFactory

//import scala.io.Source

object MainCrawler extends App {
  //Base Data
  val mainUrl = """http://webs.hufs.ac.kr:8989/src08/jsp/lecture/LECTURE2020L.jsp"""
  val browser = JsoupBrowser()
  val config = ConfigFactory.load()

  //Year and Semester Configurations
  val startYear = config.getInt("start.year")
  val startSemester = config.getInt("start.semester")
  val endYear = config.getInt("end.year")
  val endSemester = config.getInt("end.semester")

  //Hardcoded POST parameter keys
  val yearParam = "ag_ledg_year"
  val semesterParam = "ag_ledg_sessn"
  val affiliationParam = "ag_org_sect"
  val majorParam = "ag_crs_strct_cd"
  val liberalArtsParam = "ag_compt_fld_cd"
  val paramNameList = yearParam :: semesterParam :: affiliationParam :: majorParam :: liberalArtsParam :: Nil

  // Years and Semesters from config file
  val semesterParams = for {
    yr <- startYear to endYear
    sem <- (1 to 4)
    if (yr > startYear && yr < endYear) ||
    (yr == startYear && sem >= startSemester) ||
    (yr == endYear && sem <= endSemester)
  } yield Map(yearParam -> yr.toString, semesterParam -> sem.toString)

  println(semesterParams)


  //Extract parameters for individual major/liberal-arts from semester pages
  def paramTemplate(doc: Document)(paramName: String): List[Param] =
    (doc >> elementList(s"select[name=$paramName]")) flatMap { wholeElem =>
      (wholeElem >> elementList("option"))
        .map { elem => Param(
                elem >> attr("value")("option"),
                (elem >> text("option")).filterNot(_ == '\u00A0'))
        }
    }

  //Create Full parameter list from given years and semesters
  def semesterBody(getSemesterDocument: Map[String, String] => Document) = for {
    yearSemesterParam <- semesterParams
    semesterDoc = getSemesterDocument(yearSemesterParam)
    fullParam = paramNameList.map(paramName => paramName -> paramTemplate(semesterDoc)(paramName))

    major = fullParam.find(_._1 == majorParam).get._2
    la = fullParam.find(_._1 == liberalArtsParam).get._2
  } yield (yearSemesterParam, Map(Major -> major, LiberalArts -> la))

  for {
    (yearSemester, deptMap) <- semesterBody(browser.post(mainUrl, _))
    (deptType, params) <- deptMap
    param <- params
  } {
    import java.io.{ PrintWriter, File }

    val yr = yearSemester(yearParam).toInt
    val sem = Semester.fromInt(yearSemester(semesterParam).toInt)

    val pw = new PrintWriter(new File(s"assets/$yr-${Semester.toInt(sem)}-${param.value}.json" ))
    pw.write(
      Json.prettyPrint(
        DepartmentCrawler.toJson(
          year      = yr,
          semester  = sem,
          deptType  = deptType,
          deptParam = param)
      )
    )
    pw.close
  }
}
