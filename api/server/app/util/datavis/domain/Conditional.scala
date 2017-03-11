package util.datavis.domain

class Conditional(val condition: Condition, val assignment: Assignment)
class Condition(val x: Operand, val y: Operand, val comparison: Comparator)
class Assignment(val target: Identifier, val value: Literal)