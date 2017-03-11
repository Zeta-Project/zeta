package controller

import scala.scalajs.js.Array
import scala.scalajs.js.Dynamic.literal

object TestLanguage {
  def langForModel(id: String) = {
    val model = ModelLoader(id)
    facade.AceGrammar.AceGrammar.getMode(
      literal(
        RegExpID = "RegExp::",
        Style = literal(
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
        ),

        Lex = literal(
          mclasses = literal(
            autocomplete = true,
            tokens = model.mClasses
          ),
          mrefs = literal(
            autocomplete = true,
            tokens = model.mRefs
          ),
          comment = literal(
            `type` = "comment",
            tokens = Array(
              Array("#", null)
            )
          ),
          heredoc = literal(
            `type` = "block",
            tokens = Array(
              Array("'''"),
              Array("\"\"\""),
              Array("RegExp::/([rubRUB]|(ur)|(br)|(UR)|(BR))?('{3}|\"{3})/", 6)
            )
          ),
          identifier = "RegExp::/[_A-Za-z][_A-Za-z0-9]*/",
          number = Array(
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
          ),
          string = literal(
            `type` = "escaped-block",
            escape = "\\",
            tokens = Array(
              // start, end of string (can be the matched regex group ie. 1 )
              Array("RegExp::/(['\"])/", 1),
              Array("RegExp::/([rubRUB]|(ur)|(br)|(UR)|(BR))?(['\"])/", 6)
            )
          ),
          operator = literal(
            combine = true,
            tokens = Array(
              "(", ")", "[", "]", "{", "}", ",", ":", "`", "=", ";", ".",
              "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=",
              ">>=", "<<=", "//=", "**=", "@"
            )
          ),
          decorator = "RegExp::/@[_A-Za-z][_A-Za-z0-9]*/",
          keyword = literal(
            autocomplete = true,
            tokens = Array(
              "assert", "break", "class", "continue",
              "def", "del", "elif", "else", "except", "finally",
              "for", "from", "global", "if", "import",
              "lambda", "pass", "raise", "return",
              "try", "while", "with", "yield", "as"
            )
          ),
          builtin = literal(
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
        ),
        Parser = Array(
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
      )
    )
  }
}
