package models.result


/**
 */
sealed trait Result[+T] {

  def map[R](block: T => R): Result[R] = map(block, Result.defaultMessage)

  def map[R](block: T => R, onFailure: String): Result[R] = map(block, _ => onFailure)

  def map[R](block: T => R, onFailure: Throwable => String): Result[R]

  def foreach(block: T => Unit): Unit


  def flatMap[R](block: T => Result[R]): Result[R] = flatMap(block, Result.defaultMessage)

  def flatMap[R](block: T => Result[R], onFailure: String): Result[R] = flatMap(block, _ => onFailure)

  def flatMap[R](block: T => Result[R], onFailure: Throwable => String): Result[R]


}

object Result {

  private[Result] val defaultMessage: Throwable => String = (t) => {
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

  def apply[T](block: () => T): Result[T] = apply(block, defaultMessage)

  def apply[T](block: () => T, onFailure: String): Result[T] =
    apply(block, _ => onFailure)

  def apply[T](block: () => T, onFailure: Throwable => String): Result[T] =
    try {
      new Success[T](block())
    } catch {
      case t: Throwable => new Failure(onFailure(t))
    }

}


final class Failure private(val message: String) extends Result[Nothing] {
  override def map[R](block: (Nothing) => R, onFailure: (Throwable) => String): Result[R] = this

  override def flatMap[R](block: (Nothing) => Result[R], onFailure: (Throwable) => String): Result[R] = this

  override def foreach(block: (Nothing) => Unit): Unit = {}
}

object Failure {
  def apply(message: String): Failure = new Failure(message)

  def unapply(arg: Failure): Option[String] = Some(arg.message)
}


final class Success[+T] private(val param: T) extends Result[T] {
  override def map[R](block: (T) => R, onFailure: (Throwable) => String): Result[R] = {
    Result(() => block(param), onFailure)
  }

  override def flatMap[R](block: (T) => Result[R], onFailure: (Throwable) => String): Result[R] = {
    Result(() => block(param), onFailure) match {
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
