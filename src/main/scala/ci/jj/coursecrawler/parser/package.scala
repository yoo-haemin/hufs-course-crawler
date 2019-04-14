package ci.jj.coursecrawler

import ci.jj.coursecrawler.domain._
import ci.jj.coursecrawler.http.{ DepartmentDocument, DepartmentListDocument }

package object parser {

  type CourseTimeFragment
  type CourseFragment

}

package parser {

  trait CourseTimeParser[F[_]] {

    def parse(courseTimeFragment: CourseTimeFragment): F[CourseTime]

  }

  trait CourseParser[F[_]] {

    def parse(courseFragment: CourseFragment): F[Course]

  }

  trait DocumentParser[F[_]] {

    def departmentList(departmentListDocument: DepartmentListDocument): F[List[Department]]

    def department(department: DepartmentDocument): F[List[Course]]

  }

}
