package shared

/**
 * A simple Scala Implementation of Operational Transformation for Plain Text
 * inspired by https://github.com/Operational-Transformation/ot.js
 *
 */
object PlainOT {

  /** Represents a sequence of operations valid for any string with length -> baseLength */
  case class Operation(ops: Seq[Component] = Seq[Component]()) {
    lazy val baseLength = ops.filterNot(_.isInstanceOf[InsComp]).map(_.length).sum

    lazy val targetLength: Int = ops.map(_.length).sum - ops.filter(_.isInstanceOf[DelComp]).map(_.length).sum

    def skip(count: Int): Operation = copy(ops = ops.lastOption match {
      case Some(x: SkipComp) => ops.updated(ops.length - 1, SkipComp(x.length + count))
      case _ => ops :+ SkipComp(count)
    })

    def skip(comp: SkipComp): Operation = skip(comp.length)

    def insert(str: String): Operation = copy(ops = ops.lastOption match {
      case Some(x: InsComp) => ops.updated(ops.length - 1, InsComp(x.str + str))
      case _ => ops :+ InsComp(str)
    })

    def insert(comp: InsComp): Operation = insert(comp.str)

    def delete(count: Int): Operation = copy(ops = ops.lastOption match {
      case Some(x: DelComp) => ops.updated(ops.length - 1, DelComp(x.length + count))
      case _ => ops :+ DelComp(count)
    })

    def delete(comp: DelComp): Operation = delete(comp.length)

    override def toString: String = {
      for (op <- ops) yield {
        s" [${op.getClass.getSimpleName} ${op.length}]"
      }
    }.mkString(" -> ")

    /**
     * Helper for apply, does the actual applying in a recursive manner in O(n)
     */
    @scala.annotation.tailrec
    private def applyRec(c: Int = 0, t: Seq[Component] = ops, str: String): Option[String] = t.headOption match {

      case Some(ins: InsComp) =>
        val (start, end) = str.splitAt(c)
        applyRec(c + ins.length, t.drop(1), start + ins.str + end)

      case Some(skip: SkipComp) => applyRec(c + skip.length, t.drop(1), str)

      case Some(del: DelComp) =>
        applyRec(c, t.drop(1), str.patch(c, "", del.length))

      case None => Some(str)

      case _ => None
    }

    /**
     * Applies the operation to an input String
     * It is important to notice that not every input String is valid for a given
     * operation. There are two requirements for the input string
     * 1. The baseLength of the operation must match the length of the input string
     * 2. The characters of a delete component must match the characters in the input
     * string at the given point
     * @param str the string on which the input should be applied
     * @return the result of the input transformation
     */
    def apply(str: String): Option[String] = str.length == baseLength match {
      case true => applyRec(str = str)
      case _ => None
    }

    /**
     * Combine the current Operation with op
     * Note: op must be the operation that directly follows this operation
     */
    def compose(op: Operation): Operation = ???

    /** Computes the invert operation for input string str
      * such that:
      * str == this.invert(str).apply(this.apply(str))
      * For implementing undo
      */
    def invert(str: String): Operation = Operation(ops.map {
      case x: InsComp => DelComp(x.length)
      case x: SkipComp => x
      case x: DelComp => InsComp(str.substring(Operation(ops.takeWhile(_ != x).drop(1)).baseLength, x.length))
    })
  }


  /** Companion of the Operation case class */
  object Operation {
    /** The transform function is the heart of every OT implementation
      * It's use is explained below.
      * Let client A and client B start with the same string S
      * and A produced operation opA and B produces opB at the same time.
      *
      * The transform function yields an operation pair that makes the both strings
      * identical if A applies primeB and B applies primeA
      */
    private def transformRec(ops1: Seq[Component],
                             ops2: Seq[Component],
                             res: TransformedPair = TransformedPair()): TransformedPair = {

      if (ops1.isEmpty && ops2.isEmpty)
        return res

      //require(ops1.nonEmpty, "Could not compose operations, first op is too short!")
      // require(ops2.nonEmpty, "Could not compose operations, first op is too long!")

      ops1.headOption match {
        case Some(op1: InsComp) =>
          return transformRec(
            ops1 = ops1.drop(1),
            ops2 = ops2,
            res = res.copy(res.prime1.insert(op1.str), res.prime2.skip(op1.length)))

        case Some(op1: SkipComp) =>
          ops2.headOption match {
            case Some(op2: SkipComp) =>
              if (op1.length > op2.length) {
                return transformRec(
                  ops1.updated(0, SkipComp(op1.length - op2.length)), ops2.drop(1),
                  res.copy(prime1 = res.prime1.skip(op2), res.prime2.skip(op2)))
              } else if (op1.length == op2.length) {
                return transformRec(
                  ops1.drop(1), ops2.drop(1),
                  res.copy(prime1 = res.prime1.skip(op2), res.prime2.skip(op2)))
              } else {
                return transformRec(
                  ops1.drop(1), ops2.updated(0, SkipComp(op2.length - op1.length)),
                  res.copy(prime1 = res.prime1.skip(op2), res.prime2.skip(op2)))
              }
            case Some(op2: DelComp) =>
              if (op1.length > op2.length) {
                return transformRec(
                  ops1.updated(0, SkipComp(op1.length - op2.length)),
                  ops2.drop(1),
                  res.copy(prime2 = res.prime2.delete(op2.length)))
              } else if (op1.length == op2.length) {
                return transformRec(
                  ops1.drop(1),
                  ops2.drop(1),
                  res.copy(prime2 = res.prime2.delete(op1.length)))
              } else {
                return transformRec(
                  ops1.drop(1),
                  ops2.updated(0, DelComp(op2.length - op1.length)),
                  res.copy(prime2 = res.prime2.delete(op1.length)))
              }
            case _ =>
          }

        case Some(op1: DelComp) =>
          ops2.headOption match {
            case Some(op2: DelComp) => {
              if (op1.length > op2.length) {
                return transformRec(ops1.updated(0, DelComp(op1.length - op2.length)), ops2.drop(1), res)
              } else if (op1.length == op2.length) {
                return transformRec(ops1.drop(1), ops2.drop(1), res)
              } else {
                return transformRec(ops1.drop(1), ops2.updated(0, DelComp(op2.length - op1.length)), res)
              }
            }
            case Some(op2: SkipComp) => {
              if (op1.length > op2.length) {
                return transformRec(
                  ops1.updated(0, DelComp(op1.length - op2.length)),
                  ops2.drop(1),
                  res.copy(prime1 = res.prime1.delete(op2.length)))
              } else if (op1.length == op2.length) {
                return transformRec(
                  ops1.drop(1),
                  ops2.drop(1),
                  res.copy(prime1 = res.prime1.delete(op2.length)))
              } else {
                return transformRec(
                  ops1.drop(1),
                  ops2.updated(0, SkipComp(op2.length - op1.length)),
                  res.copy(prime1 = res.prime1.delete(op1.length)))
              }
            }
            case _ =>
          }
        case _ =>
      }

      ops2.headOption match {
        case Some(op2: InsComp) =>
          return transformRec(
            ops1 = ops1,
            ops2 = ops2.drop(1),
            res = TransformedPair(prime1 = res.prime1.skip(op2.length), prime2 = res.prime2.insert(op2.str)))
        case _ =>
      }
      res
    }

    def transform(a: Operation, b: Operation): Option[TransformedPair] = {
      Some(transformRec(a.ops, b.ops))
    }

  }

  /** The Result of the almighty 'transform operation' */
  case class TransformedPair(prime1: Operation = Operation(), prime2: Operation = Operation())

  /**
   * Component Interface, every component has an explicit or implicit length
   */
  sealed trait Component {
    val length: Int
  }

  /**
   * Component for inserting plain text at the current position
   * @param str the string to insert
   */
  private case class InsComp(str: String) extends Component {
    override lazy val length: Int = str.length
  }

  /**
   * Component for deleting plain text at the current position
   * @param count the string to delete
   */
  private case class DelComp(count: Int) extends Component {
    override lazy val length: Int = count
  }

  /** Component for moving the cursor to the right
    * @param ret the number of chars the cursor should move
    */
  private case class SkipComp(ret: Int) extends Component {
    override lazy val length: Int = ret
  }

}
