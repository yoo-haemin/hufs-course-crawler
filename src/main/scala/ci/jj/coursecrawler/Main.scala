package ci.jj.coursecrawler

import cats.effect._
import cats.syntax.functor._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    new TWStream[IO]().run.as(ExitCode.Success)
}

import org.http4s._
import org.http4s.client.blaze._
import org.http4s.client.oauth1
import cats.effect._
import fs2.Stream
import fs2.io.stdout
import fs2.text.{lines, utf8Encode}
import io.circe.Json
import jawnfs2._
import java.util.concurrent.Executors //, ExecutorService
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global

class TWStream[F[_]](implicit F: ConcurrentEffect[F], cs: ContextShift[F]) {
  // jawn-fs2 needs to know what JSON AST you want
  implicit val f = io.circe.jawn.CirceSupportParser.facade

  /* These values are created by a Twitter developer web app.
   * OAuth signing is an effect due to generating a nonce for each `Request`.
   */
  def sign(consumerKey: String, consumerSecret: String, accessToken: String, accessSecret: String)
      (req: Request[F]): F[Request[F]] = {
    val consumer = oauth1.Consumer(consumerKey, consumerSecret)
    val token    = oauth1.Token(accessToken, accessSecret)
    oauth1.signRequest(req, consumer, callback = None, verifier = None, token = Some(token))
  }

  /* Create a http client, sign the incoming `Request[F]`, stream the `Response[IO]`, and
   * `parseJsonStream` the `Response[F]`.
   * `sign` returns a `F`, so we need to `Stream.eval` it to use a for-comprehension.
   */
  def jsonStream(consumerKey: String, consumerSecret: String, accessToken: String, accessSecret: String)
      (req: Request[F]): Stream[F, Json] =
    for {
      client <- BlazeClientBuilder(global).stream
      sr  <- Stream.eval(sign(consumerKey, consumerSecret, accessToken, accessSecret)(req))
      res <- client.stream(sr).flatMap(_.body.chunks.parseJsonStream)
    } yield res

  /* Stream the sample statuses.
   * Plug in your four Twitter API values here.
   * We map over the Circe `Json` objects to pretty-print them with `spaces2`.
   * Then we `to` them to fs2's `lines` and then to `stdout` `Sink` to print them.
   */
  def stream(blockingEC: ExecutionContext): Stream[F, Unit] = {
    val req = Request[F](Method.GET, Uri.uri("https://stream.twitter.com/1.1/statuses/sample.json"))
    val s   = jsonStream("<consumerKey>", "<consumerSecret>", "<accessToken>", "<accessSecret>")(req)
    s.map(_.spaces2).through(lines).through(utf8Encode).through(stdout(blockingEC))
  }

  /**
    * We're going to be writing to stdout, which is a blocking API.  We don't
    * want to block our main threads, so we create a separate pool.  We'll use
    * `fs2.Stream` to manage the shutdown for us.
    */
  def blockingEcStream: Stream[F, ExecutionContext] =
    Stream.bracket(F.delay(Executors.newFixedThreadPool(4)))(pool =>
      F.delay(pool.shutdown()))
        .map(ExecutionContext.fromExecutorService)

  /** Compile our stream down to an effect to make it runnable */
  def run: F[Unit] =
    blockingEcStream.flatMap { blockingEc =>
      stream(blockingEc)
    }.compile.drain
}

