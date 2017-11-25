import AceGrammar from 'string-replace-loader?search=_ace.require&replace=_ace.acequire!string-replace-loader?search=require\.specified&replace=false&flags=g!ace-grammar/build/ace_grammar'

function simpleToken(tokens = [], autocomplete = false, combine = "\\b") {
    return {
        type: "simple",
        tokens,
        autocomplete,
        combine
    };
}

function commentToken(tokens = []) {
    return {
        type: "comment",
        tokens,
    };
}

function blockToken(tokens = []) {
    return {
        type: "block",
        tokens,
    };
}

function escapedBlockToken(tokens = [], escape = "\\") {
    return {
        type: "escaped-block",
        tokens,
        escape,
    };
}

const TOKEN = Object.freeze({
    BUILTIN: "builtin",
    COMMENT: "comment",
    DECORATOR: "decorator",
    HEREDOC: "heredoc",
    IDENTIFIER: "identifier",
    KEYWORD: "keyword",
    NUMBER: "number",
    OPERATOR: "operator",
    STRING: "string",
})

const styleGrammar = {
    RegExpID: "RegExp::",

    // defines the mapping of tokens to editor styles
    Style: {
        [TOKEN.BUILTIN]: "constant.support",
        [TOKEN.COMMENT]: "comment",
        [TOKEN.DECORATOR]: "constant.support",
        [TOKEN.IDENTIFIER]: "identifier",
        [TOKEN.KEYWORD]: "keyword",
        [TOKEN.NUMBER]: "constant.numeric",
        [TOKEN.OPERATOR]: "operator",
        [TOKEN.STRING]: "string",
        [TOKEN.HEREDOC]: "string",
    },

    // defines the mapping of token patterns and token configuration to an associated tokenID
    Lex: {
        [TOKEN.COMMENT]: commentToken([
            ["#", null],
        ]),
        [TOKEN.HEREDOC]: blockToken([
            ["'''"],
            ["\"\"\""],
            ["RegExp::/([rubRUB]|(ur)|(br)|(UR)|(BR))?('{3}|\"{3})/", 6]
        ]),
        [TOKEN.IDENTIFIER]: simpleToken("RegExp::/[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.NUMBER]: simpleToken([
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
        ]),
        [TOKEN.STRING]: escapedBlockToken([
            // start, end of string (can be the matched regex group ie. 1 )
            ["RegExp::/(['\"])/", 1],
            ["RegExp::/([rubRUB]|(ur)|(br)|(UR)|(BR))?(['\"])/", 6]
        ]),
        [TOKEN.OPERATOR]: simpleToken([
            "(", ")", "[", "]", "{", "}", ",", ":", "`", "=", ";", ".",
            "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=",
            ">>=", "<<=", "//=", "**=", "@"
        ], false, true),
        [TOKEN.DECORATOR]: simpleToken("RegExp::/@[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.KEYWORD]: simpleToken([
            "style", "extends", "transparency", "background-color",
            "gradient-orientation", "line-color", "line-width"
        ], true),
        [TOKEN.BUILTIN]: simpleToken([
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
        ], true),
    },

    //defines what to parse and in what order
    Parser: [
        TOKEN.COMMENT,
        TOKEN.HEREDOC,
        TOKEN.NUMBER,
        TOKEN.STRING,
        TOKEN.DECORATOR,
        TOKEN.OPERATOR,
        "delimiter",
        TOKEN.KEYWORD,
        TOKEN.BUILTIN,
        TOKEN.IDENTIFIER
    ]
};

const testGrammar = {
    RegExpID: "RegExp::",

    // defines the mapping of tokens to editor styles
    Style: {
        [TOKEN.DECORATOR]: "constant.support",
        [TOKEN.COMMENT]: "comment",
        [TOKEN.KEYWORD]: "keyword",
        [TOKEN.BUILTIN]: "constant.support",
        [TOKEN.OPERATOR]: "operator",
        [TOKEN.IDENTIFIER]: "identifier",
        [TOKEN.NUMBER]: "constant.numeric",
        [TOKEN.STRING]: "string",
        [TOKEN.HEREDOC]: "string"
    },

    // defines the mapping of token patterns and token configuration to an associated tokenID
    Lex: {
        [TOKEN.COMMENT]: commentToken([["#", null]]),
        [TOKEN.HEREDOC]: blockToken([
            ["'''"],
            ["\"\"\""],
            ["RegExp::/([rubRUB]|(ur)|(br)|(UR)|(BR))?('{3}|\"{3})/", 6],
        ]),
        [TOKEN.IDENTIFIER]: simpleToken("RegExp::/[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.NUMBER]: simpleToken([
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
            "RegExp::/0(?![\\dx])/",
        ]),
        [TOKEN.STRING]: escapedBlockToken([
            // start, end of string (can be the matched regex group ie. 1 )
            ["RegExp::/(['\"])/", 1],
            ["RegExp::/([rubRUB]|(ur)|(br)|(UR)|(BR))?(['\"])/", 6]
        ]),
        [TOKEN.OPERATOR]: simpleToken([
            "(", ")", "[", "]", "{", "}", ",", ":", "`", "=", ";", ".",
            "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=",
            ">>=", "<<=", "//=", "**=", "@",
        ], false, true),
        [TOKEN.DECORATOR]: simpleToken("RegExp::/@[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.KEYWORD]: simpleToken([
            "assert", "break", "class", "continue",
            "def", "del", "elif", "else", "except", "finally",
            "for", "from", "global", "if", "import",
            "lambda", "pass", "raise", "return",
            "try", "while", "with", "yield", "as"
        ], true),
        [TOKEN.BUILTIN]: simpleToken([
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
        ], true),
    },

    //defines what to parse and in what order
    Parser: [
        TOKEN.COMMENT,
        TOKEN.HEREDOC,
        TOKEN.NUMBER,
        TOKEN.STRING,
        TOKEN.DECORATOR,
        TOKEN.OPERATOR,
        "delimiter",
        TOKEN.KEYWORD,
        TOKEN.BUILTIN,
        TOKEN.IDENTIFIER,
    ]
}

const styleLanguage = AceGrammar.getMode(styleGrammar);
const testLanguage = AceGrammar.getMode(testGrammar);
export { styleLanguage, testLanguage }