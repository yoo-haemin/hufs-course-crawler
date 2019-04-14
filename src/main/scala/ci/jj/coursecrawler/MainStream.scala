package ci.jj.coursecrawler

// import cats.effect.Sync
import cats.mtl.ApplicativeAsk
import cats.syntax.functor._
import cats.syntax.flatMap._
import fs2.Stream
import ci.jj.coursecrawler.Main.AppEnv
import ci.jj.coursecrawler.http._
import ci.jj.coursecrawler.parser._
import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

import cats.effect.{ ContextShift, ConcurrentEffect }
import com.softwaremill.sttp._
import com.softwaremill.sttp.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import scala.concurrent.duration.Duration
import java.nio.ByteBuffer

import com.olegpy.meow.hierarchy._

class Blah[F[_]: ConcurrentEffect: ContextShift] {
  implicit val sttpBackend = AsyncHttpClientFs2Backend[F]()

  val stream: Stream[F, ByteBuffer] = ???

  val a = sttp
  .method(Method.GET, uri"http://blah.com")
  .response(asStream[Stream[F, java.nio.ByteBuffer]])
  .readTimeout(Duration.Inf)
  .send()

}

class MainStream[F[_]: ContextShift](
  implicit 
  F: ConcurrentEffect[F], 
  A: ApplicativeAsk[F, AppEnv]
) {
  import ci.jj.coursecrawler.config.AppConfig //YAY finally it's working!

  val run = F.delay(println(implicitly[ApplicativeAsk[F, AppConfig]]))

  private def runWith(
      httpClient: HttpClient[F],
      document: DocumentParser[F],
  )(
      blockingExecutionContext: ExecutionContext
  ): Stream[F, Unit] = 
  ???
    // new Blah[F].a.flatMap {
    // }

  private def blockingEcStream: Stream[F, ExecutionContext] =
    Stream.bracket {
      for {
        numCPU <- F.delay(Runtime.getRuntime.availableProcessors())
        pool   <- F.delay(Executors.newFixedThreadPool(numCPU))
      } yield ExecutionContext.fromExecutorService(pool)
    } {
      ec => F.delay(ec.shutdown())
    }

  def stream: Stream[F, Unit] =
    blockingEcStream.flatMap { blockingEc => 

      ???
      
      // runWith(???, ???)(blockingEc) 
    }
}



//, cs: ContextShift[F]
//  // jawn-fs2 needs to know what JSON AST you want
//  implicit val f = io.circe.jawn.CirceSupportParser.facade
//
//  /* These values are created by a Twitter developer web app.
//   * OAuth signing is an effect due to generating a nonce for each `Request`.
//   */
//  def sign(consumerKey: String, consumerSecret: String, accessToken: String, accessSecret: String)
//      (req: Request[F]): F[Request[F]] = {
//    val consumer = oauth1.Consumer(consumerKey, consumerSecret)
//    val token    = oauth1.Token(accessToken, accessSecret)
//    oauth1.signRequest(req, consumer, callback = None, verifier = None, token = Some(token))
//  }
//
//  /* Create a http client, sign the incoming `Request[F]`, stream the `Response[IO]`, and
//   * `parseJsonStream` the `Response[F]`.
//   * `sign` returns a `F`, so we need to `Stream.eval` it to use a for-comprehension.
//   */
//  def jsonStream(consumerKey: String, consumerSecret: String, accessToken: String, accessSecret: String)
//      (req: Request[F]): Stream[F, Json] =
//    for {
//      client <- BlazeClientBuilder(global).stream
//      sr  <- Stream.eval(sign(consumerKey, consumerSecret, accessToken, accessSecret)(req))
//      res <- client.stream(sr).flatMap(_.body.chunks.parseJsonStream)
//    } yield res
//
//
//  def stream(blockingEC: ExecutionContext): Stream[F, Unit] = {
//    val req = Request[F](Method.GET, Uri.uri("https://stream.twitter.com/1.1/statuses/sample.json"))
//    val s   = jsonStream("<consumerKey>", "<consumerSecret>", "<accessToken>", "<accessSecret>")(req)
//    s.map(_.spaces2).through(lines).through(utf8Encode).through(stdout(blockingEC))
//  }
