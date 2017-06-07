package controller

import java.util.UUID

import scala.scalajs.js.Array
import scala.scalajs.js.Dynamic.literal

object TestLanguage {
  def langForModel(id: UUID) = {
    val model = ModelLoader(id)
    facade.aceGrammar.AceGrammar.getMode(
      literal(
        RegExpID = "RegExp::",
        Style = createStyle(),

        Lex = literal(
          mclasses = createMClasses(model),
          mrefs = createMRefs(model),
          comment = createComment(),
          heredoc = createHeredoc(),
          identifier = "RegExp::/[_A-Za-z][_A-Za-z0-9]*/",
          number = createNumberRegex(),
          string = createString(),
          operator = createOperator(),
          decorator = "RegExp::/@[_A-Za-z][_A-Za-z0-9]*/",
          keyword = createKeyword(),
          builtin = createBuiltin()
        ),
        Parser = createParser()
      )
    )
  }

  private def createStyle() = {
    literal(
      decorator = "constant.support",
      comment = "comment",
      keyword = "keyword",
      mclasses = "comment",
      mrefs = "keyword",
      builtin = "constant.support",
      operator = "operator",
      identifier = "identifier",
      number = "constant.numeric",
      string = "string",
      heredoc = "string"
    )
  }

  private def createMClasses(model: ModelLoader) = {
    literal(
      autocomplete = true,
      tokens = model.mClasses
    )
  }

  private def createMRefs(model: ModelLoader) = {
    literal(
      autocomplete = true,
      tokens = model.mRefs
    )
  }

  private def createComment() = {
    literal(
      `type` = "comment",
      tokens = Array(
        Array("#", null)
      )
    )
  }

  private def createHeredoc() = {
    literal(
      `type` = "block",
      tokens = Array(
        Array("'''"),
        Array("\"\"\""),
        Array("RegExp::/([rubRUB]|(ur)|(br)|(UR)|(BR))?('{3}|\"{3})/", 6)
      )
    )
  }

  private def createNumberRegex() = {
    Array(
      "RegExp::/\\d*\\.\\d+(e[\\+\\-]?\\d+)?[jJ]?/",
      "RegExp::/\\d+\\.\\d*[jJ]?/",
      "RegExp::/\\.\\d+[jJ]?/",
      // integers
      // hex
      "RegExp::/0x[0-9a-fA-F]+[lL]?/",
      // binary
      "RegExp::/0b[01]+[lL]?/",
      // octal
      "RegExp::/0o[0-7]+[lL]?/",
      // decimal
      "RegExp::/[1-9]\\d*(e[\\+\\-]?\\d+)?[lL]?[jJ]?/",
      // just zero
      "RegExp::/0(?![\\dx])/"
    )
  }

  private def createString() = {
    literal(
      `type` = "escaped-block",
      escape = "\\",
      tokens = Array(
        // start, end of string (can be the matched regex group ie. 1 )
        Array("RegExp::/(['\"])/", 1),
        Array("RegExp::/([rubRUB]|(ur)|(br)|(UR)|(BR))?(['\"])/", 6)
      )
    )
  }

  private def createOperator() = {
    literal(
      combine = true,
      tokens = Array(
        "(", ")", "[", "]", "{", "}", ",", ":", "`", "=", ";", ".",
        "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=",
        ">>=", "<<=", "//=", "**=", "@"
      )
    )
  }

  private def createKeyword() = {
    literal(
      autocomplete = true,
      tokens = Array(
        "assert", "break", "class", "continue",
        "def", "del", "elif", "else", "except", "finally",
        "for", "from", "global", "if", "import",
        "lambda", "pass", "raise", "return",
        "try", "while", "with", "yield", "as"
      )
    )
  }

  private def createBuiltin() = {
    literal(
      autocomplete = true,
      tokens = Array(
        "abs", "all", "any", "bin", "bool", "bytearray", "callable", "chr",
        "classmethod", "compile", "complex", "delattr", "dict", "dir", "divmod",
        "enumerate", "eval", "filter", "float", "format", "frozenset",
        "getattr", "globals", "hasattr", "hash", "help", "hex", "id",
        "input", "int", "isinstance", "issubclass", "iter", "len",
        "list", "locals", "map", "max", "memoryview", "min", "next",
        "object", "oct", "open", "ord", "pow", "property", "range",
        "repr", "reversed", "round", "set", "setattr", "slice",
        "sorted", "staticmethod", "str", "sum", "super", "tuple",
        "type", "vars", "zip", "__import__", "NotImplemented",
        "Ellipsis", "__debug__"
      )
    )
  }

  private def createParser() = {
    Array(
      "comment",
      "heredoc",
      "mclasses",
      "mrefs",
      "number",
      "string",
      "decorator",
      "operator",
      "delimiter",
      "keyword",
      "builtin",
      "identifier"
    )
  }
}
