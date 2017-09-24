/**
  data StreamCell a = Nil
                    | Cons a (Stream a)
  type Stream a = (lazy) StreamCell a
 */
package object stream {
  
  sealed trait StreamCell[+C[_], +T]
  final case class Cons[C[_], T](head: T, tail: Stream[C, T]) extends StreamCell[C, T]
  final case object Nil extends StreamCell[Nothing, Nothing]

  trait StreamOps[C[_]] {
    def delay[T](exp: => StreamCell[C, T]): Stream[C, T]
    def force[T](stream: Stream[C, T]): StreamCell[C, T]
  }

  def delay[C[_], T](exp: => StreamCell[C, T])(implicit ops: StreamOps[C]): Stream[C, T] = {
    ops.delay(exp)
  }

  def force[C[_], T](stream: Stream[C, T])(implicit ops: StreamOps[C]): StreamCell[C, T] =
    ops.force(stream)

  case class Stream[C[_], T](suspend: C[StreamCell[C, T]])(implicit ops: StreamOps[C]) {
    
    def toList: List[T] =
      force(this) match {
        case Nil => List.empty[T]
        case Cons(h, t) => h :: t.toList
      }
      
    override def toString: String =
      s"Stream(${toList.mkString(", ")})"

    def take(n: Int): Stream[C, T] = {
      delay {
        if(n == 0) Nil
        else force(this) match {
          case Nil => Nil
          case Cons(h, t) => Cons(h, t.take(n-1))
        }
      }
    }
  }

  object Stream {

    def fromList[C[_], T](xs: List[T])(implicit ops: StreamOps[C]): Stream[C, T] = delay {
      xs match {
        case h :: t => Cons(h, Stream.fromList(t))
        case _ => Nil
      }
    }
  }
}