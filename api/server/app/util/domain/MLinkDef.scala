package util.domain

class MLinkDef(val _type: MObj, override val upperBound: Int, override val lowerBound: Int, val deleteIfLower: Boolean = false) extends MBounds
