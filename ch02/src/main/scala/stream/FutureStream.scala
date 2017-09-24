/**
  use `Future` for suspend
 */
package stream

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  data StreamCell a = Nil
                    | Cons a (Stream a)
  type Stream a = (lazy) StreamCell a
 */


object FutureStream extends App {

  sealed trait StreamCell[+T]
  final case class Cons[T](head: T, tail: Stream[T]) extends StreamCell[T]
  final case object Nil extends StreamCell[Nothing]

  def delay[T](exp: => StreamCell[T]): Stream[T] =
    Stream(Future(exp))

  def force[T](stream: Stream[T], duration: Duration = Duration.Inf): StreamCell[T] =
      Await.result(stream.suspend, duration)

  case class Stream[T](suspend: Future[StreamCell[T]]) {
    def toList: List[T] =
      force(this) match {
        case Nil => List.empty[T]
        case Cons(h, t) => h :: t.toList
      }

    override def toString: String =
      s"Stream(${toList.mkString(", ")})"

    def take(n: Int): Stream[T] = delay {
      if(n == 0) Nil
      else force(this) match {
        case Nil => Nil
        case Cons(h, t) => Cons(h, t.take(n-1))
      }
    }
  }

  object Stream {
    def fromList[T](xs: List[T]): Stream[T] = delay {
      xs match {
        case h :: t => Cons(h, Stream.fromList(t))
        case _ => Nil
      }
    }
  }

  val s: Stream[Int] = Stream.fromList((0 until 10).toList)
  val e: Stream[Int] = delay(Nil)

  assert(e.take(2).toString == "Stream()",      "empty.take(n) should be Nil")
  assert(s.take(0).toString == "Stream()",      "nonEmpty.take(0) should be Nil")
  assert(s.take(2).toString == "Stream(0, 1)",  "nonEmpty.take(n) when n>0, should not be Nil") 
}
