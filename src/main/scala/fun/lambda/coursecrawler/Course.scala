package fun.lambda.coursecrawler

  case class Course(
    openYear: Int,
    openSemester: Semester,
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
    import play.api.libs.json._

    implicit def courseReads = Json.reads[Course]
    implicit def courseWrites = Json.writes[Course]
    implicit def courseFormat = Json.format[Course]

    def fromList(openYear: Int, openDept: String, openSemester: Semester, openCampus: String, text: List[String]) = {
      def strToBool(s: String): Boolean = s match {
        case "true" => true
        case _ => false
      }

      val profNameR = """(.*)\s\((.*)\)""".r

      val splitProfName: (String, String) = text(10) match {
        case profNameR(main, add) if add != "-" => main -> add
        case profNameR(main, add) if add == "-" => main -> ""
        case _ => text(10) -> ""
      }

      val no = """(\d+) \/ (\d+)""".r.findAllMatchIn(text(14)).toList.head
      val applyNo = no.group(1)
      val maxNo = no.group(2)

      val newStuff = Course(
        openYear = openYear,
        openSemester = openSemester,
        openDept = openDept,
        openCampus = openCampus,
        area = text(1),
        suggestedYear = text(2).toInt,
        courseNumber = text(3),
        subjectName = text(4),
        professorNameMain = splitProfName._1,
        professorNameAdditional = splitProfName._2,
        creditHour = text(11).toInt,
        classHour = text(12).toInt,
        time = CourseTime.fromHtmlTag(text(13)),
        applyNo = applyNo.toInt,
        maxNo = maxNo.toInt,
        note = text(15),

        required = strToBool(text(6)),
        online = strToBool(text(7)),
        foreignLanguage = strToBool(text(8)),
        teamTeaching = strToBool(text(9))
      )

      println(newStuff)
      newStuff
    }
  }
