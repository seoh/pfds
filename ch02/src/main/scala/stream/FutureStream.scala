package stream

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object FutureStream extends App {

  implicit val futureStream: StreamOps[Future] = new StreamOps[Future] {
    override def delay[T](exp: => StreamCell[Future, T]): Stream[Future, T] =
      Stream(Future(exp))

    override def force[T](stream: Stream[Future, T]): StreamCell[Future, T] =
      Await.result(stream.suspend, Duration.Inf)
  }

  val s: Stream[Future, Int] = Stream.fromList((0 until 10).toList)
  val e: Stream[Future, Int] = delay(Nil)

  assert(e.take(2).toString == "Stream()",      "empty.take(n) should be Nil")
  assert(s.take(0).toString == "Stream()",      "nonEmpty.take(0) should be Nil")
  assert(s.take(2).toString == "Stream(0, 1)",  "nonEmpty.take(n) when n>0, should not be Nil") 
}
