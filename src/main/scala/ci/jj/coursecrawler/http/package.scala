package ci.jj.coursecrawler

//import scala.concurrent.ExecutionContext
//import java.util.concurrent._
import shapeless.tag._

import ci.jj.coursecrawler.domain._
//import org.http4s.Uri
//import cats.effect._
//import org.http4s._
//import org.http4s.client.blaze._
import org.http4s.client._
import net.ruippeixotog.scalascraper.model.Document

package http {

  trait HttpClient[F[_]] {

    def departmentList(yearSemester: YearSemester): F[DepartmentListDocument]

    def department(yearSemester: YearSemester, dept: Department): F[DepartmentDocument]

  }

}

package object http {

  private[http] type DepartmentListDocumentT
  type DepartmentListDocument = Document @@ DepartmentListDocumentT

  private[http] type DepartmentDocumentT
  type DepartmentDocument = Document @@ DepartmentDocumentT

}



//  object Blah {
//
//    val blockingEC = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
//
//    implicit val cs = IO.contextShift(blockingEC)
//    val httpClient: Client[IO] = JavaNetClientBuilder[IO](blockingEC).create
//
//    val helloJames = httpClient.expect[String]("http://localhost:8080/hello/James")
//
//
//    def hello(name: String): IO[String] = {
//      val target = Uri.uri("http://localhost:8080/hello/") / name
//      httpClient.expect[String](target)
//    }
//
//    ///////////////////////
//    val baseUri = Uri.uri("""https://webs.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp""")
//
//  }

