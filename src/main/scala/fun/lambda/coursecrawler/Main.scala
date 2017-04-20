package fun.lambda.coursecrawler

object Main {
  def main(args: Array[String]): Unit = {
    /*
    val allCourses = for {
      year <- 2011 to 2016
      semester <- 1 to 4
    } yield {
      val writer = new PrintWriter(new File(s"assets/$year-0$semester.json"))
      writer.write(Json.prettyPrint(CourseAreaCrawler.toJson(year, semester).get))
      writer.close()
      ()
     }*/
  }
}
