package fun.lambda.coursecrawler

import fastparse.all._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import java.time.DayOfWeek
//import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
//import net.ruippeixotog.scalascraper.model._

object SelectionParser extends App {

  case class Course(
    openYear: Int,
    openDept: String,
    openCampus: String,
    area: String,
    suggestedYear: Int,
    courseNumber: String,
    subjectName: String,
    professorNameMain: String,
    professorNameAdditional: String,
    creditHour: Int,
    classHour: Int,
    time: Seq[CourseTime],
    applyNo: Int,
    maxNo: Int,
    note: String,
    required: Boolean,
    online: Boolean,
    foreignLanguage: Boolean,
    teamTeaching: Boolean
  )

  object Course {
    def strToBool(s: String): Boolean = s match {
      case "true" => true
      case _ => false
    }


    def fromList(openYear: Int, openDept: String, openCampus: String, text: List[String]) = {
      val profNameR = """(.*)\s\((.*)\)""".r

      val splitProfName: (String, String) = text(10) match {
        case profNameR(main, add) => main -> add
        case _ => text(10) -> ""
      }

      //val courseTime: CourseTime = text(11)

      this(
        openYear = openYear,
        openDept = openDept,
        openCampus = openCampus,
        //(6, 인문학공통, 1, J11017201, 이슬람사상의이해(2) (Understanding of Islamic Thought (2)), true, , , , , 김동환 (Kim Dong Hwan), 3, 3, 목 1 2 3 (-) (Thu 1 2 3 (-)), 0 / 50, 중동.북아프리카지역
        area = text(1),
        suggestedYear = text(2).toInt,
        courseNumber = text(3),
        subjectName = text(4),
        professorNameMain = splitProfName._1,
        professorNameAdditional = splitProfName._2,
        creditHour = text(10).toInt,
        classHour = text(10).toInt,
        time = ???, // TODO text(11),
        applyNo = text(12).toInt,
        maxNo = text(13).toInt,
        note = text(14),

        required = strToBool(text(6)),
        online = strToBool(text(7)),
        foreignLanguage = strToBool(text(8)),
        teamTeaching = strToBool(text(9))
      )
    }
  }

  case class Param(value: String, text: String)
  sealed abstract class DeptType
  object Major extends DeptType
  object LiberalArts extends DeptType

  val url = """http://webs.hufs.ac.kr:8989/src08/jsp/lecture/LECTURE2020L.jsp"""
  val browser = JsoupBrowser()
  val doc = browser.parseFile("asset/test.html")

  val yearParam = "ag_ledg_year"
  val semesterParam = "ag_ledg_sessn"
  val affiliationParam = "ag_org_sect"
  val majorParam = "ag_crs_strct_cd"
  val liberalArtsParam = "ag_compt_fld_cd"

  val paramList = yearParam :: semesterParam :: affiliationParam :: majorParam :: liberalArtsParam :: Nil

  def paramTemplate(paramName: String) =
    (doc >> elementList(s"select[name=$paramName]")).flatMap { wholeElem =>
      (wholeElem >> elementList("option"))
        .map { elem => Param(
                elem >> attr("value")("option"),
                (elem >> text("option")).filterNot(_ == '\u00A0'))
        }
    }

  val params = paramList.map(param => param -> paramTemplate(param)).toMap

  def getList(year: Int, session: Int, dept: (DeptType, String)) = {
    //val postParam = Map(
    //  "tab_lang" -> "K",
    //  "type" -> "",
    //  "ag_ledg_year" -> year.toString,
    //  "ag_ledg_sessn" -> session.toString,
    //  "ag_org_sect" -> "A",
    //  "campus_sect" -> "H1",
    //  ) ++ (
    //  dept._1 match {
    //    case Major => Map("gubun" -> "1", "ag_crs_strct_cd" -> dept._2)
    //    case LiberalArts => Map("gubun" -> "2", "ag_compt_fld_cd" -> dept._2)
    //  })

    val body = browser.parseFile("asset/test.html") //browser.post(url, postParam)

    (body >> element("#premier1") >> elementList("tr")).tail.map { course =>
      (course >> elementList("td")).map { elem =>
        elem >> text("td") match {
          case s if s.length > 0 => s
          case s if (elem >> elementList("img")).length > 0 => "true"
          case _ => ""
        }
      }
    }
  }

  println {
    getList(2017, 3, (Major, "AAR01_H1"))
  }

  //println(params)
}

