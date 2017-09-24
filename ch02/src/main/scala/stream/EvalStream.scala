package stream

import cats.Eval

object EvalStream extends App {
  implicit val evalStream: StreamOps[Eval] = new StreamOps[Eval] {
    override def delay[T](exp: => StreamCell[Eval, T]): Stream[Eval, T] =
      Stream(Eval.later(exp))

    override def force[T](stream: Stream[Eval, T]): StreamCell[Eval, T] =
      stream.suspend.value
  }

  val s: Stream[Eval, Int] = Stream.fromList((0 until 10).toList)
  val e: Stream[Eval, Int] = delay(Nil)

  assert(e.take(2).toString == "Stream()",      "empty.take(n) should be Nil")
  assert(s.take(0).toString == "Stream()",      "nonEmpty.take(0) should be Nil")
  assert(s.take(2).toString == "Stream(0, 1)",  "nonEmpty.take(n) when n>0, should not be Nil")

  val s2: Stream[Eval, Int] = Stream.fromList((10 until 20).toList)
  assert((s ++ s2).toString == s"Stream(${(0 until 20).mkString(", ")})", "concatenate stream")
  assert((s ++ e).toString == s.toString)
  assert((e ++ s).toString == s.toString)
}
