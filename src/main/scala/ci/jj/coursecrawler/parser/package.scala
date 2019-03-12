package ci.jj.coursecrawler

package parser {

  import ci.jj.coursecrawler.domain.CourseTime


  trait CourseTimeParser[F[_]] {

    type CourseTimeFragment

    def parse(courseTimeFragment: CourseTimeFragment): F[CourseTime]

  }

  trait CourseParser[F[_]] {

  }

  trait DepartmentParser[F[_]] {

  }

}
