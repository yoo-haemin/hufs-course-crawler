
package org.scalatest

import scala.util.{Success, Try, Failure}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

private[scalatest] trait AsyncOutcome {
  def onComplete(f: Try[Outcome] => Unit)
  def toStatus: Status
  // SKIP-SCALATESTJS-START
  def toOutcome: Outcome // may block
  // SKIP-SCALATESTJS-END
  def toInternalFutureOutcome: Future[Outcome]
  def toFutureOutcome: FutureOutcome
}

private[scalatest] case class PastOutcome(past: Outcome) extends AsyncOutcome {

  def onComplete(f: Try[Outcome] => Unit): Unit = {
    f(new Success(past))
  }
  def toStatus: Status =
    past match {
      case _: Failed => FailedStatus
      case _ => SucceededStatus
    }
  // SKIP-SCALATESTJS-START
  def toOutcome: Outcome = past
  // SKIP-SCALATESTJS-END
  def toInternalFutureOutcome: Future[Outcome] = Future.successful(past)
  def toFutureOutcome: FutureOutcome = FutureOutcome { Future.successful(past) }
}

private[scalatest] case class InternalFutureOutcome(future: Future[Outcome])(implicit ctx: ExecutionContext) extends AsyncOutcome {

  private final val queue = new ConcurrentLinkedQueue[Try[Outcome] => Unit]
  private final val status = new ScalaTestStatefulStatus

  future.onComplete {
    case Success(result) =>
      for (f <- queue.iterator)
        f(Success(result))
      status.setCompleted()

    case Failure(ex) =>
      for (f <- queue.iterator)
        f(Failure(ex))
      status.setFailedWith(ex)
      status.setCompleted()
  } /* fills in ctx here */

  def onComplete(f: Try[Outcome] => Unit): Unit = {
    var executeLocally = false
    synchronized {
      if (!future.isCompleted)
        queue.add(f)
      else
        executeLocally = true
    }
    if (executeLocally) {
      future.value.get match {
        case Success(result) => f(new Success(result))
        case Failure(ex) => f(new Failure(ex))
      }
    }
  }
  def toStatus: Status = status
  // SKIP-SCALATESTJS-START
  def toOutcome: Outcome = Await.result(future, Duration.Inf)
  // SKIP-SCALATESTJS-END
  def toInternalFutureOutcome: Future[Outcome] = future
  def toFutureOutcome: FutureOutcome = FutureOutcome { future }
}
