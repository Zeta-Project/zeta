package de.htwg.zeta.server.model.result

import grizzled.slf4j.Logging


/**
 */
sealed trait Unreliable[+T] {

  def map[R](block: T => R): Unreliable[R] = map(block, Unreliable.defaultMessage)

  def map[R](block: T => R, onFailure: String): Unreliable[R] = map(block, _ => onFailure)

  def map[R](block: T => R, onFailure: Throwable => String): Unreliable[R]

  def foreach(block: T => Unit): Unit


  def flatMap[R](block: T => Unreliable[R]): Unreliable[R] = flatMap(block, Unreliable.defaultMessage)

  def flatMap[R](block: T => Unreliable[R], onFailure: String): Unreliable[R] = flatMap(block, _ => onFailure)

  def flatMap[R](block: T => Unreliable[R], onFailure: Throwable => String): Unreliable[R]


}

object Unreliable extends Logging {

  private[Unreliable] val defaultMessage: Throwable => String = (t) => {
    val stack = t.getStackTrace
    val clazz =
      if (stack.nonEmpty) {
        s" in class: ${stack(0).getClassName} "
      } else {
        ""
      }
    val msg = {
      val msg = t.getMessage
      if (msg.nonEmpty) {
        s" with message: $msg."
      } else {
        ". There is no message."
      }
    }

    s"Failed $clazz$msg"
  }

  def apply[T](block: () => T): Unreliable[T] = apply(block, defaultMessage)

  def apply[T](block: () => T, onFailure: String): Unreliable[T] =
    apply(block, _ => onFailure)

  def apply[T](block: () => T, onFailure: Throwable => String): Unreliable[T] =
    try {
      Success[T](block())
    } catch {
      case t: Throwable =>
        val msg = onFailure(t)
        debug(s"Result failed with msg: $msg.\nStacktrace:\n", t)
        Failure(msg)
    }

}


final class Failure private(val message: String) extends Unreliable[Nothing] {
  override def map[R](block: (Nothing) => R, onFailure: (Throwable) => String): Unreliable[R] = this

  override def flatMap[R](block: (Nothing) => Unreliable[R], onFailure: (Throwable) => String): Unreliable[R] = this

  override def foreach(block: (Nothing) => Unit): Unit = {}
}

object Failure {
  def apply(message: String): Failure = new Failure(message)

  def unapply(arg: Failure): Option[String] = Some(arg.message)
}


final class Success[+T] private(val param: T) extends Unreliable[T] {
  override def map[R](block: (T) => R, onFailure: (Throwable) => String): Unreliable[R] = {
    Unreliable(() => block(param), onFailure)
  }

  override def flatMap[R](block: (T) => Unreliable[R], onFailure: (Throwable) => String): Unreliable[R] = {
    Unreliable(() => block(param), onFailure) match {
      case Success(r) => r
      case f: Failure => f
    }
  }

  override def foreach(block: (T) => Unit): Unit = {
    block(param)
  }
}

object Success {
  def apply[T](arg: T): Success[T] = new Success[T](arg)

  def unapply[T](arg: Success[T]): Option[T] = Some(arg.param)
}
