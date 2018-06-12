import AceGrammar
    from 'string-replace-loader?search=_ace.require&replace=_ace.acequire!string-replace-loader?search=require\.specified&replace=false&flags=g!ace-grammar/build/ace_grammar'

function simpleToken(tokens = [], autocomplete = false, combine = "\\b") {
    return {type: "simple", tokens, autocomplete, combine};
}

function commentToken(tokens = []) {
    return {type: "comment", tokens,};
}

function blockToken(tokens = []) {
    return {type: "block", tokens,};
}

function escapedBlockToken(tokens = [], escape = "\\") {
    return {type: "escaped-block", tokens, escape,};
}

const TOKEN = Object.freeze({
    BUILTIN: "builtin",
    COMMENT: "comment",
    DECORATOR: "decorator",
    HEREDOC: "heredoc",
    IDENTIFIER: "identifier",
    KEYWORD: "keyword",
    NUMBER: "number",
    PROPERTY: "property",
    ATOM: "atom",
    OPERATOR: "operator",
    DELIMITER: "delimiter",
    STRING: "string",
});

// defines the mapping of tokens to editor styles
const defaultStyle = {
    [TOKEN.BUILTIN]: "constant.support",
    [TOKEN.COMMENT]: "comment",
    [TOKEN.DECORATOR]: "constant.support",
    [TOKEN.HEREDOC]: "string",
    [TOKEN.IDENTIFIER]: "identifier",
    [TOKEN.KEYWORD]: "keyword",
    [TOKEN.NUMBER]: "constant.numeric",
    [TOKEN.PROPERTY]: "constant.regexp",
    [TOKEN.ATOM]: "type.support",
    [TOKEN.OPERATOR]: "operator",
    [TOKEN.DELIMITER]: "operator",
    [TOKEN.STRING]: "string",
};

//defines what to parse and in what order
const defaultParser = [
    TOKEN.COMMENT,
    TOKEN.HEREDOC,
    TOKEN.NUMBER,
    TOKEN.ATOM,
    TOKEN.STRING,
    TOKEN.DECORATOR,
    TOKEN.OPERATOR,
    TOKEN.DELIMITER,
    TOKEN.KEYWORD,
    TOKEN.BUILTIN,
    TOKEN.PROPERTY,
    TOKEN.IDENTIFIER
];

const styleGrammar = {
    RegExpID: "RegExp::",
    Style: defaultStyle,
    // defines the mapping of token patterns and token configuration to an associated tokenID
    Lex: {
        [TOKEN.COMMENT]: commentToken([["//", null], ["/*", "*/"]]),
        [TOKEN.HEREDOC]: blockToken([["/**", "*/"]]),
        [TOKEN.IDENTIFIER]: simpleToken("RegExp::/[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.NUMBER]: simpleToken([
            // floats
            "RegExp::/\\d*\\.\\d+(e[\\+\\-]?\\d+)?/",
            "RegExp::/\\d+\\.\\d*/",
            "RegExp::/\\.\\d+/",
            // integers
            // hex
            "RegExp::/#[0-9a-fA-F]{6,8}/",
            // binary
            "RegExp::/0b[01]+L?/",
            // octal
            "RegExp::/0o[0-7]+L?/",
            // decimal
            "RegExp::/[1-9]\\d*(e[\\+\\-]?\\d+)?L?/",
            // just zero
            "RegExp::/0(?![\\dx])/"
        ]),
        [TOKEN.ATOM]: simpleToken(["true", "false"]),
        [TOKEN.STRING]: escapedBlockToken([["RegExp::/(['\"])/", 1]]),
        [TOKEN.OPERATOR]: simpleToken(["="], false, true),
        [TOKEN.DELIMITER]: simpleToken(["{", "}"], false, true),
        [TOKEN.DECORATOR]: simpleToken("RegExp::/@[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.PROPERTY]: simpleToken("RegExp::/[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.KEYWORD]: simpleToken(["style", "extends"], true),
        [TOKEN.BUILTIN]: simpleToken([
            "description", "line-color", "line-style", "line-width", "transparency", "background-color", "font-color",
            "font-name", "font-size", "font-bold", "font-italic", "gradient-orientation", "gradient-area-color",
            "gradient-area-offset"
        ], true),
    },
    Parser: defaultParser
};

const shapeGrammar = {
    RegExpID: "RegExp::",
    Style: defaultStyle,
    // defines the mapping of token patterns and token configuration to an associated tokenID
    Lex: {
        [TOKEN.COMMENT]: commentToken([["//", null], ["/*", "*/"]]),
        [TOKEN.HEREDOC]: blockToken([["/**", "*/"]]),
        [TOKEN.IDENTIFIER]: simpleToken("RegExp::/[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.NUMBER]: simpleToken([
            // floats
            "RegExp::/\\d*\\.\\d+(e[\\+\\-]?\\d+)?/",
            "RegExp::/\\d+\\.\\d*/",
            "RegExp::/\\.\\d+/",
            // integers
            // hex
            "RegExp::/#[0-9a-fA-F]{6,8}/",
            // binary
            "RegExp::/0b[01]+L?/",
            // octal
            "RegExp::/0o[0-7]+L?/",
            // decimal
            "RegExp::/[1-9]\\d*(e[\\+\\-]?\\d+)?L?/",
            // just zero
            "RegExp::/0(?![\\dx])/"
        ]),
        [TOKEN.ATOM]: simpleToken(["true", "false"]),
        [TOKEN.STRING]: escapedBlockToken([["RegExp::/(['\"])/", 1]]),
        [TOKEN.OPERATOR]: simpleToken([":", ","], false, true),
        [TOKEN.DELIMITER]: simpleToken(["{", "}"], false, true),
        [TOKEN.DECORATOR]: simpleToken("RegExp::/@[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.PROPERTY]: simpleToken("RegExp::/\w+\.\w+(\.\w+\.\w+)?/"),
        [TOKEN.KEYWORD]: simpleToken([
            "node", "edge", "for", "ellipse", "textfield", "repeatingBox", "line", "polyline", "polygon",
            "rectangle", "horizontalLayout", "verticalLayout", "statictext", "roundedRectangle", "placing"
        ], true),
        [TOKEN.BUILTIN]: simpleToken([
            "resizing", "style", "sizeMin", "sizeMax", "anchor", "edges", "target",
            "offset", "text", "textBody", "position", "point", "size", "align", "identifier", "multiline",
            "editable", "curve", "for", "each", "as", "x", "y", "width", "height", "horizontal", "vertical"
        ], true),
    },
    Parser: defaultParser
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
};

const styleLanguage = AceGrammar.getMode(styleGrammar);
const shapeLanguage = AceGrammar.getMode(shapeGrammar);
const testLanguage = AceGrammar.getMode(testGrammar);
export {styleLanguage, testLanguage, shapeLanguage}