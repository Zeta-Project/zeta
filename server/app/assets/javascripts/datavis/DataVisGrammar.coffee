root = exports ? this

root.getManipulationGrammar = ->
  RegExpID: "RegExp::"

  Style:
    comment: "comment"
    keyword: "keyword"
    operator: "operator"
    comparison: "operator"
    delimiter: "operator"
    identifier: "identifier"
    property: "constant.support"
    number: "constant.numeric"
    string: "string"
    boolean: "constant"
    selectorString: "constant.support"
    style: "constant"

  Lex:
    comment:
      type: "comment"
      tokens:[
        ["//", null]
        ["/*", "*/"]
      ]

    identifier : "RegExp::/[_A-Za-z$][_A-Za-z0-9$]*/"

    property : "RegExp::/[_A-Za-z$][\- _A-Za-z0-9$]*/"

    number:
      tokens: [
        "RegExp::/[0-9]*\\.[0-9]+/"
        "RegExp::/[0-9]+/"
      ]

    string:
      type: "escaped-block"
      escape: "\\"
      tokens: ["RegExp::/([\"])/", 1]

    boolean:
      tokens: [
        "true"
        "false"
      ]

    selectorString:
      type: "escaped-block"
      escape: "\\"
      tokens: ["RegExp::/(['])/", 1]

    operator:
      combine: true
      tokens: [
        "="
      ]
      state:
        "assignmentOp": "assignmentValue"

    comparison:
      combine: true
      tokens: [
        "=="
        "!="
        "<"
        ">"
        "<="
        ">="
      ]
      state:
        "comparator": "conditionOperandR"

    delimiter:
      combine:true
      tokens:[
        ":"
      ]
      state:
        "delimiter": "assignmentTarget"

    keyword:
      autocomplete: true
      tokens: ["if"]
      state:
        "initial": "conditionOperandL"

  Syntax:

    literalValue:
      type: "group"
      match: "either"
      tokens: ["boolean", "number", "string"]

    dotProperty:
      type: "group"
      match: "all"
      tokens: [".", "property"]

    dotProperties:
      type: "group"
      match: "zeroOrMore"
      tokens:["dotProperty"]

    selector:
      type: "group"
      match: "all"
      tokens: ["style", "[", "selectorString", "]"]

    literal:
      type: "n-gram"
      tokens: ["literalValue"]
      state:
        "conditionOperandL": "comparator"
        "conditionOperandR": "delimiter"
        "assignmentValue": "initial"

    mIdentifier:
      type: "n-gram"
      tokens:[
        "identifier"
        "dotProperties"
      ]
      state:
        "conditionOperandL": "comparator"
        "conditionOperandR": "delimiter"

    styleIdentifier:
      type: "n-gram"
      tokens:[
        "selector"
        "dotProperties"
      ]
      state:
        "assignmentTarget": "assignmentOp"

  Parser: [
    "comment"
    "literal"
    "keyword"
    "delimiter"
    "comparison"
    "operator"
    "styleIdentifier"
    "mIdentifier"
  ]