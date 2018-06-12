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

const diagramGrammar = {
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
        [TOKEN.OPERATOR]: simpleToken([""], false, true),
        [TOKEN.DELIMITER]: simpleToken(["{", "}"], false, true),
        [TOKEN.DECORATOR]: simpleToken("RegExp::/@[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.PROPERTY]: simpleToken("RegExp::/[_A-Za-z][_A-Za-z0-9]*/"),
        [TOKEN.KEYWORD]: simpleToken(["diagram"], true),
        [TOKEN.BUILTIN]: simpleToken(["palette"], true),
    },
    Parser: defaultParser
};

const styleLanguage = AceGrammar.getMode(styleGrammar);
const shapeLanguage = AceGrammar.getMode(shapeGrammar);
const diagramLanguage = AceGrammar.getMode(diagramGrammar);
export {styleLanguage, diagramLanguage, shapeLanguage}