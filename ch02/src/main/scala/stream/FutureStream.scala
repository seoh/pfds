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

  val s2: Stream[Future, Int] = Stream.fromList((10 until 20).toList)
  assert((s ++ s2).toString == s"Stream(${(0 until 20).mkString(", ")})", "concatenate stream")
  assert((s ++ e).toString == s.toString)
  assert((e ++ s).toString == s.toString)

  assert(e.drop(2).toString == "Stream()",      "empty.drop(n) should be Nil")
  assert(s.drop(0).toString == s.toString,      "nonEmpty.drop(0) should be itself")
  assert(s.drop(2).toString == s"Stream(${(2 until 10).mkString(", ")})")
}
