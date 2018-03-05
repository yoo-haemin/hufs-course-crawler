package fun.lambda.coursecrawler

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import play.api.libs.json._

import com.typesafe.config.ConfigFactory

object MainCrawler extends App {
  //Base Data
  val mainUrl = """http://webs.hufs.ac.kr:8989/src08/jsp/lecture/LECTURE2020L.jsp"""
  val browser = JsoupBrowser()
  val config = ConfigFactory.load()

  //Year and Semester Configurations
  val startYear: Year = config.getInt("start.year")
  val startSemester = config.getInt("start.semester")
  val endYear: Year = config.getInt("end.year")
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
  } yield Map(yearParam -> yr, semesterParam -> sem)

  //Extract parameters for individual major/liberal-arts from semester pages
  def paramTemplate(doc: Document)(paramName: String): List[Department] =
    (doc >> elementList(s"select[name=$paramName]")) flatMap { wholeElem =>
      (wholeElem >> elementList("option"))
        .map { elem =>
          Department(
            elem >> attr("value")("option"),
            (elem >> text("option")).filterNot(_ == '\u00A0') //Remove nbsp (\u00A0)
          )
        }
    }

  //Create Full parameter list from given years and semesters
  def semesterBody(getSemesterDocument: Map[String, String] => Document) = for {
    yearSemesterParam <- semesterParams
    semesterDoc = getSemesterDocument(
      yearSemesterParam.map { case (param, value) => param -> value.toString }
    )

    //Parameters with everything
    fullParam = paramNameList.map(paramName => paramName -> paramTemplate(semesterDoc)(paramName))

    major = fullParam.find(_._1 == majorParam).get._2
    la = fullParam.find(_._1 == liberalArtsParam).get._2
  } yield (yearSemesterParam, Map(Major -> major, LiberalArts -> la))

  val departments = for {
    (yearSemester, deptMap) <- semesterBody(browser.post(mainUrl, _))
    year = yearSemester(yearParam)
    semester = Semester.fromInt(yearSemester(semesterParam))
    (deptType, departments) <- deptMap
    department <- departments
  } yield {
    (year, semester, department,
     DepartmentCrawler.toCourseList(
       year = year,
       semester = semester,
       deptType = deptType,
       deptParam = department
     ))
  }

  import java.io.{ PrintWriter, File }

  if (new File(s"json").isDirectory()) {

  }


  if (config.getBoolean("perSemester")) {
    departments
      .groupBy { case (yr, sem, _, _) => yr -> sem }
      .foreach { case ((yr, sem), depts) =>
        val pw = new PrintWriter(new File(s"json/$yr-${Semester.toInt(sem)}.json" ))
        pw.write(
          Json.prettyPrint(
            Json.toJson(
              (Seq.empty[Course] /: depts) { case (acc, (_, _, department, courseList)) => acc ++ courseList }
            )
          )
        )
        pw.close()
      }

  } else {
    departments.foreach { case (yr, sem, department, courseList) =>
      val pw = new PrintWriter(new File(s"json/$yr-${Semester.toInt(sem)}-${department.value}.json" ))
      pw.write(
        Json.prettyPrint(Json.toJson(courseList))
      )
      pw.close()
    }
  }
}
