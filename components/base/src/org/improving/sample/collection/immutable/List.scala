package org.improving
package sample.collection
package immutable

import org.improving.sample.collection.mutable.ListBuffer
import org.improving.sample.collection.generic.Growable
import org.improving.sample.collection.LinearSeqOptimized
import org.improving.sample.collection.mutable.Builder
import org.improving.sample.collection.generic.CanBuildFrom
import org.improving.sample.collection.generic.GenericCompanion


@macroExtension
sealed abstract class List[+A] extends TraversableLike[A, List[A]]
//		with LinearSeq[A]
		with LinearSeqOptimized[A, org.improving.sample.collection.immutable.List[A]]
		with IterableLike[A, List[A]] {
  def companion: GenericCompanion[List] = List
  def isEmpty: Boolean
  def head: A
  def tail: org.improving.sample.collection.immutable.List[A]

  def iterator: Iterator[A] = new ListIterator[A](this)

  def ::[B >: A](x: B): List[B] =
    new immutable.::(x, this)

  def :::[B >: A](prefix: List[B]): List[B] =
    if (isEmpty) prefix
    else if (prefix.isEmpty) this
    else (new ListBuffer[B] ++= prefix).prependToList(this)

  def ++[B >: A, That](that: List[B]): That = {
    (this ::: that).asInstanceOf[That]
  }
}

case object Nil extends List[Nothing] {
  override def isEmpty = true
  override def head: Nothing = null.asInstanceOf[Nothing]
  override def tail: List[Nothing] = null

  // Removal of equals method here might lead to an infinite recursion similar to IntMap.equals.
  override def equals(that: Any) = that match {
    case that1: List[_] => that1.isEmpty
    case _ => false
  }
}

final case class ::[B](private var hd: B, private[collection] var tl: List[B]) extends List[B] {
  override def head: B = hd
  override def tail: List[B] = tl
  override def isEmpty: Boolean = false
}

class AppendToList[A] extends Function2[A, List[A], List[A]] {
  override def apply(x: A, l : List[A]): List[A] = x :: l
}

object List extends GenericCompanion[List] {
  override def empty[A]: List[A] = Nil
  override def apply[A](xs: A*): List[A] = {
    xs.foldRight[List[A]](Nil)(new AppendToList[A])
  }

  implicit def canBuildFrom[A, T]: CanBuildFrom[List[T], A, List[A]] =
    new ListCanBuildFrom[A, T]
  
  def newBuilder[A]: Builder[A, List[A]] = new ListBuilder[A]
}

class ListCanBuildFrom[A, T] extends CanBuildFrom[List[T], A, List[A]] {
  override def apply(from: List[T]) = apply
  override def apply() = new ListBuilder[A]
}

class ListBuilder[A] extends Builder[A, List[A]] {
  var lb = new ListBuffer[A]()
  override def +=(elem: A) = {
    lb ++= List(elem); this
  }
  override def clear(): Unit = lb = new ListBuffer[A]()
  override def result() = lb.prependToList(Nil)
}

class ListIterator[A](l: List[A]) extends Iterator[A] {
  var cursor = l
  override def hasNext = !l.isEmpty
  override def next = { val ret = cursor.head; cursor = cursor.tail; ret }
}
