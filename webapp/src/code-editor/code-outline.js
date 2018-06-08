import $ from "jquery";

export class codeoutline {

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
            .then(() => {
                switch (this.dslType) {
                    case "shape":
                        let nodes = this.findElementLineNumbers(this.editor, "node");
                        let edges = this.findElementLineNumbers(this.editor, "edge");
                        this.createHeadline(nodes);
                        this.createLinks(nodes, this.editor);
                        this.createHeadline(edges);
                        this.createLinks(edges, this.editor);
                        break;
                    case "style":
                        let styles = this.findElementLineNumbers(this.editor, "style");
                        this.createHeadline(styles);
                        this.createLinks(styles, this.editor);
                        break;
                    default:
                        console.log(" ");
                }
            })
            .catch(err => {
                console.error(err);
                alert('an unexpected error occurred');
            });
    }

    createHeadline(elements) {
        let el = $("<span>").text(this.capitalizeFirstLetter(elements[0].typ) + "s").addClass("outline-heading");
        $('#outline-nodes').append(el);
    }

    capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

    findElementLineNumbers(editor, typ) {
        let lines = editor.session.doc.getAllLines();
        let LineNumbers = [];
        for (let i = 0, l = lines.length; i < l; i++) {
            if (lines[i].indexOf(typ) == 0) {
                let obj = Object.assign({typ: typ, name: lines[i].split(" ")[1], line: (i + 1)});
                LineNumbers.push(obj);
            }
        }
        return LineNumbers
    }

    createLinks(elements, editor) {
        for (let i = 0; i < elements.length; i++) {
            let obj = elements[i];
            let el = $("<div>")
                .attr("id", obj.line)
                .addClass(obj.name)
                .addClass("outline-node")
                .text(obj.name)
                .bind("click", function () {
                    editor.scrollToLine(obj.line, true, true, function () {
                    });
                    editor.gotoLine(obj.line, 10, true);
                });
            $("#outline-nodes").append(el);
        }
    }


    markEditorLine(from, to, editor) {
        var ace = require('brace');
        var Range = ace.acequire('ace/range').Range;
        editor.session.addMarker(new Range(from, 0, to, 1), "lineErrorMarker", "fullLine");
    }
}