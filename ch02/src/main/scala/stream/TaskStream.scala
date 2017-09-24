package stream

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object TaskStream extends App {
  implicit val taskStream: StreamOps[Task] = new StreamOps[Task] {
    override def delay[T](exp: => StreamCell[Task, T]): Stream[Task, T] =
      Stream(Task(exp))

    override def force[T](stream: Stream[Task, T]): StreamCell[Task, T] =
      Await.result(stream.suspend.runAsync, Duration.Inf)
  }

  val s: Stream[Task, Int] = Stream.fromList((0 until 10).toList)
  val e: Stream[Task, Int] = delay(Nil)

  assert(e.take(2).toString == "Stream()",      "empty.take(n) should be Nil")
  assert(s.take(0).toString == "Stream()",      "nonEmpty.take(0) should be Nil")
  assert(s.take(2).toString == "Stream(0, 1)",  "nonEmpty.take(n) when n>0, should not be Nil")

  val s2: Stream[Task, Int] = Stream.fromList((10 until 20).toList)
  assert((s ++ s2).toString == s"Stream(${(0 until 20).mkString(", ")})", "concatenate stream")
  assert((s ++ e).toString == s.toString)
  assert((e ++ s).toString == s.toString)

  assert(e.drop(2).toString == "Stream()",      "empty.drop(n) should be Nil")
  assert(s.drop(0).toString == s.toString,      "nonEmpty.drop(0) should be itself")
  assert(s.drop(2).toString == s"Stream(${(2 until 10).mkString(", ")})")

  assert(s.reverse.toString == s"Stream(${(0 until 10).reverse.mkString(", ")})")
  assert(e.reverse.toString == e.toString)
}
