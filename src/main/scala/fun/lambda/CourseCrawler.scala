package fun.lambda


package object course_crawler {
  // import java.time.DayOfWeek
  // import play.api.libs.json._
  // import play.api.libs.json.Reads._
  // import play.api.libs.functional.syntax._


  /*
   * Beginning of Reads definitions
   */


  // implicit val courseTimeMapWrites = {
  //   def writes(m: Map[DayOfWeek, CourseTime]): JsValue =
  //     (JsObject.empty /: m)((acc, v) => {
  //       acc +
  //       ((v._1 match {
  //         case DayOfWeek.MONDAY => "Mon"
  //         case DayOfWeek.TUESDAY => "Tue"
  //         case DayOfWeek.WEDNESDAY => "Wed"
  //         case DayOfWeek.THURSDAY => "Thu"
  //         case DayOfWeek.FRIDAY => "Fri"
  //         case DayOfWeek.SATURDAY => "Sat"
  //         case DayOfWeek.SUNDAY => "Sun"
  //       }) -> courseTimeReads.reads(v._2))
  //     })
  // }

  // implicit val courseTimeMapReads: Reads[Map[DayOfWeek, CourseTime]] = new Reads[Map[DayOfWeek, CourseTime]] {
  //   def reads(json: JsValue): JsResult[Map[DayOfWeek, CourseTime]] =
  //     JsSuccess((for {
  //       mon <- (json \ "Mon").asOpt[CourseTime]
  //       tue <- (json \ "Tue").asOpt[CourseTime]
  //       wed <- (json \ "Wed").asOpt[CourseTime]
  //       thu <- (json \ "Thu").asOpt[CourseTime]
  //       fri <- (json \ "Fri").asOpt[CourseTime]
  //       sat <- (json \ "Sat").asOpt[CourseTime]
  //       sun <- (json \ "Sun").asOpt[CourseTime]
  //     } yield {
  //       Map(
  //         DayOfWeek.MONDAY -> mon,
  //         DayOfWeek.TUESDAY -> tue,
  //         DayOfWeek.WEDNESDAY -> wed,
  //         DayOfWeek.THURSDAY -> thu,
  //         DayOfWeek.FRIDAY -> fri,
  //         DayOfWeek.SATURDAY -> sat,
  //         DayOfWeek.SUNDAY -> sun
  //       )
  //     }).getOrElse(Map[DayOfWeek,CourseTime]()))
  // }

//    (__ \ "Mon" \ "time") .readNullable[SortedSet[Int]]

  // implicit val dayOfWeekReads = new Reads[DayOfWeek] {
  //   def reads(json: JsValue): DayOfWeek = json match {
  //     case JsString(s) => s match {
  //       case "Mon" => DayOfWeek.MONDAY
  //       case "Tue" => DayOfWeek.TUESDAY
  //       case "Wed" => DayOfWeek.WEDNESDAY
  //       case "Thu" => DayOfWeek.THURSDAY
  //       case "Fri" => DayOfWeek.FRIDAY
  //       case "Sat" => DayOfWeek.SATURDAY
  //       case "Sun" => DayOfWeek.SUNDAY
  //     }
  //   }
  // }



  // implicit val courseTimeMapReads: Reads[Map[DayOfWeek, CourseTime]] = (
  //   (__ \ "dayOfWeek").read[DayOfWeek] and
  //     (__ \ "time").read[CourseTime]
  // )

  // implicit val courseReads: Reads[Course] = (
  //   (__ \ "courseType")               .read[String] and
  //     (__ \ "courseYear")             .read[Int] and
  //     (__ \ "courseNo")               .read[String] and
  //     (__ \ "courseName1")            .read[String] and
  //     (__ \ "courseName2")            .readNullable[String] and
  //     (__ \ "required")               .read[Boolean] and
  //     (__ \ "online")                 .read[Boolean] and
  //     (__ \ "foreignLanguage")        .read[Boolean] and
  //     (__ \ "teamTeaching")           .read[Boolean] and
  //     (__ \ "professorNameMain")      .read[String] and
  //     (__ \ "professorNameAdditional").readNullable[String] and
  //     (__ \ "creditHours")            .read[Int] and
  //     (__ \ "courseHours")            .read[Int] and
  //     (__ \ "courseTime")             .read[Map[DayOfWeek,CourseTime]] and
  //     (__ \ "currentlyEnrolled")      .read[Int] and
  //     (__ \ "maximumEnrolled")        .read[Int] and
  //     (__ \ "note")                   .readNullable[String]
  // )(Course.apply _)




  /*


  implicit val optionReads[A]: Reads[Option[A]] = {
    def reads(opt: Option[A]) = opt match {
      case Some(v) => opt.read[A]
      case None => JsNull
    }
  }


 */
  //implicit val courseReads = Json.reads[Course]


//  implicit val courseReads = Json.reads[Course]
  /*: Reads[Course] = (
    (__ \ "courseType")               .read[String] and
      (__ \ "courseYear")             .read[Int] and
      (__ \ "courseNo")               .read[String] and
      (__ \ "courseName1")            .read[String] and
      (__ \ "courseName2")            .readNullable[String] and
      (__ \ "required")               .read[Boolean] and
      (__ \ "online")                 .read[Boolean] and
      (__ \ "foreignLanguage")        .read[Boolean] and
      (__ \ "teamTeaching")           .read[Boolean] and
      (__ \ "professorNameMain")      .read[String] and
      (__ \ "professorNameAdditional").readNullable[String] and
      (__ \ "creditHours")            .read[Int] and
      (__ \ "courseHours")            .read[Int] and
      (__ \ "courseTime")             .read[Map[DayOfWeek,CourseTime]] and
      (__ \ "currentlyEnrolled")      .read[Int] and
      (__ \ "maximumEnrolled")        .read[Int] and
      (__ \ "note")                   .readNullable[String]
  )
   */


//   implicit val courseTimeReads = Reads[CourseTime] = (

//   )

/*
 * Beginning of Writes definitions
 */
  // implicit val dayOfWeekWrites = new Writes[DayOfWeek] {
  //   def writes(dayOfWeek: DayOfWeek): JsValue = dayOfWeek match {
  //     case DayOfWeek.MONDAY => Json.parse("Mon")
  //     case DayOfWeek.TUESDAY => Json.parse("Tue")
  //     case DayOfWeek.WEDNESDAY => Json.parse("Wed")
  //     case DayOfWeek.THURSDAY => Json.parse("Thu")
  //     case DayOfWeek.FRIDAY => Json.parse("Fri")
  //     case DayOfWeek.SATURDAY => Json.parse("Sat")
  //     case DayOfWeek.SUNDAY => Json.parse("Sun")
  //     case _ => JsNull
  //   }
  // }
}
