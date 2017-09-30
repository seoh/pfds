
package object queue {

  sealed trait Queue[T] {
    def isEmpty: Boolean
    def head: T
    def tail: Queue[T]
    def snoc(elem: T): Queue[T]
  }

  class BatchedQueue[T](front: List[T], rear: List[T]) extends Queue[T] {
    def isEmpty: Boolean = front.isEmpty && rear.isEmpty

    def head: T = (front, rear) match {
      case (Nil, _) => throw new NoSuchElementException
      case (x :: f, _) => x
    }

    def tail: Queue[T] = (front, rear) match {
      case (Nil, _) => throw new NoSuchElementException
      case (x :: f, r) => Queue(f, r)
    }

    def snoc(elem: T): Queue[T] = Queue(front, elem :: rear)
  }

  object Queue {
    def empty[T] = new BatchedQueue(List.empty[T], List.empty[T])

    def apply[T](front: List[T], rear: List[T]): Queue[T] = (front, rear) match {
      case (Nil, r) => new BatchedQueue(r.reverse, Nil)
      case _ => new BatchedQueue(front, rear)
    }
  }
}