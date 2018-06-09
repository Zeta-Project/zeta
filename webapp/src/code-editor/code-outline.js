import $ from "jquery";

export class CodeOutline {

    constructor(element, metaModelId, dslType, editor) {
        this.metaModelId = metaModelId;
        this.dslType = dslType;
        this.editor = editor;
        this.createCodeOutline();
    }

    createCodeOutline() {
        fetch(`/rest/v2/meta-models/${this.metaModelId}/${this.dslType}`, {
            method: 'GET',
            credentials: 'same-origin'
        })
            .then(() => this.generateOutlineElements())
            .catch(err => {
                console.error(err);
                alert('an unexpected error occurred');
            });
    }

    generateOutlineElements() {
        switch (this.dslType) {
            case "shape":
                let nodes = CodeOutline.findElementLineNumbers(this.editor, "node");
                let edges = CodeOutline.findElementLineNumbers(this.editor, "edge");
                this.createOutlineLinks(nodes, this.editor);
                this.createOutlineLinks(edges, this.editor);
                break;
            case "style":
                let styles = CodeOutline.findElementLineNumbers(this.editor, "style");
                this.createOutlineLinks(styles, this.editor);
                break;
            default:
                console.error("unknown dsl type for outline generation");
        }
    }

    createOutlineLinks(elements, editor) {
        let el = $("<div>").addClass("panel panel-default");
        let heading = CodeOutline.createHeadline(elements);
        let body = $("<div>").addClass("panel-body");
        el.append(heading);
        el.append(body);
        let nodes = this.createLinks(elements, editor);
        for(let i = 0; i < nodes.length; i++)
            body.append(nodes[i]);
        $('#outline-nodes').append(el);
    }

    static createHeadline(elements) {
        return $("<div>").text(CodeOutline.capitalizeFirstLetter(elements[0].typ) + "s")
            .addClass("outline-heading")
            .addClass("panel-heading");
    }

    static capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

    static findElementLineNumbers(editor, typ) {
        let lines = editor.session.doc.getAllLines();
        let lineNumbers = [];
        for (let i = 0, l = lines.length; i < l; i++) {
            if (lines[i].indexOf(typ) === 0) {
                let obj = Object.assign({typ: typ, name: lines[i].split(" ")[1], line: (i + 1)});
                lineNumbers.push(obj);
            }
        }
        return lineNumbers
    }

    createLinks(elements, editor) {
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

}