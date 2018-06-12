import $ from "jquery";

export class CodeOutline {

    constructor(element, metaModelId, dslType, editor) {
        this.metaModelId = metaModelId;
        this.dslType = dslType;
        this.editor = editor;
    }

    createCodeOutline() {
        const context = this;
        context.generateOutlineElements();
        let updateOutline = CodeOutline.debounce(function () {
            context.generateOutlineElements();
        }, 250);
        this.editor.getSession().on('change', updateOutline);
    }

    generateOutlineElements() {
        switch (this.dslType) {
            case "shape":
                this.generateOutlineForDsl(["node", "edge"]);
                break;
            case "style":
                this.generateOutlineForDsl(["style"]);
                break;
            default:
                console.error("unknown dsl type for outline generation");
        }
    }

    generateOutlineForDsl(keyWords) {
        const outlineNodes = $('#outline-nodes');
        outlineNodes.empty();
        for (let i = 0; i < keyWords.length; i++) {
            const keyWord = keyWords[i],
                nodes = CodeOutline.findElementLineNumbers(this.editor, keyWord),
                elements = CodeOutline.createOutlineLinks(nodes, this.editor, keyWord);
            outlineNodes.append(elements);
        }
    }

    static createOutlineLinks(elements, editor, type) {
        let el = $("<div>").addClass("panel panel-default");
        let heading = CodeOutline.createHeadline(type);
        let body = $("<div>").addClass("panel-body");
        el.append(heading);
        el.append(body);
        let nodes = CodeOutline.createLinks(elements, editor);
        for (let i = 0; i < nodes.length; i++)
            body.append(nodes[i]);
        return el;
    }

    static createHeadline(type) {
        return $("<div>").text(CodeOutline.capitalizeFirstLetter(type + "s"))
            .addClass("outline-heading")
            .addClass("panel-heading");
    }

    static capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

    static findElementLineNumbers(editor, type) {
        let lines = editor.session.doc.getAllLines();
        let lineNumbers = [];
        for (let i = 0, l = lines.length; i < l; i++) {
            if (lines[i].indexOf(type) === 0) {
                let name = lines[i].split(" ")[1];
                if (!!name) {
                    let obj = Object.assign({typ: type, name: name, line: (i + 1)});
                    lineNumbers.push(obj);
                }
            }
        }
        return lineNumbers
    }

    static createLinks(elements, editor) {
        let links = [];
        for (let i = 0; i < elements.length; i++) {
            let obj = elements[i];
            let lineNumberEl = $("<span>").addClass("line").text(obj.line);
            let el = $("<div>")
                .attr("id", obj.line)
                .addClass(obj.name)
                .addClass(obj.typ)
                .addClass("outline-node")
                .text(obj.name)
                .bind("click", function () {
                    editor.scrollToLine(obj.line, true, true, function () {
                    });
                    editor.gotoLine(obj.line, 10, true);
                })
                .append(lineNumberEl);
            links.push(el);
        }
        return links;
    }

    // Returns a function, that, as long as it continues to be invoked, will not
    // be triggered. The function will be called after it stops being called for
    // N milliseconds. If `immediate` is passed, trigger the function on the
    // leading edge, instead of the trailing.
    static debounce(func, wait, immediate) {
        let timeout;
        return function () {
            let context = this, args = arguments;
            let later = function () {
                timeout = null;
                if (!immediate) func.apply(context, args);
            };
            let callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) func.apply(context, args);
        };
    };

}