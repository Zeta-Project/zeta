import joint from 'jointjs';

const V = joint.V;
const sanitizeText = V.sanitizeText;

V.prototype.text = function(content, opt) {
  console.log(content)

  // Replace all spaces with the Unicode No-break space (http://www.fileformat.info/info/unicode/char/a0/index.htm).
  // IE would otherwise collapse all spaces into one.
  content = sanitizeText(content);
  opt = opt || {};
  var lines = content.split(',');
  var i = 0;
  var tspan;

  // `alignment-baseline` does not work in Firefox.
  // Setting `dominant-baseline` on the `<text>` element doesn't work in IE9.
  // In order to have the 0,0 coordinate of the `<text>` element (or the first `<tspan>`)
  // in the top left corner we translate the `<text>` element by `0.8em`.
  // See `http://www.w3.org/Graphics/SVG/WG/wiki/How_to_determine_dominant_baseline`.
  // See also `http://apike.ca/prog_svg_text_style.html`.
  var y = this.attr('y');
  if (!y) {
    this.attr('y', '0.8em');
  }

  // An empty text gets rendered into the DOM in webkit-based browsers.
  // In order to unify this behaviour across all browsers
  // we rather hide the text element when it's empty.
  this.attr('display', content ? null : 'none');

  // Preserve spaces. In other words, we do not want consecutive spaces to get collapsed to one.
  this.node.setAttributeNS('http://www.w3.org/XML/1998/namespace', 'xml:space', 'preserve');

  // Easy way to erase all `<tspan>` children;
  this.node.textContent = '';

  var textNode = this.node;

  if (opt.textPath) {

    // Wrap the text in the SVG <textPath> element that points
    // to a path defined by `opt.textPath` inside the internal `<defs>` element.
    var defs = this.find('defs');
    if (defs.length === 0) {
      defs = createElement('defs');
      this.append(defs);
    }

    // If `opt.textPath` is a plain string, consider it to be directly the
    // SVG path data for the text to go along (this is a shortcut).
    // Otherwise if it is an object and contains the `d` property, then this is our path.
    var d = Object(opt.textPath) === opt.textPath ? opt.textPath.d : opt.textPath;
    if (d) {
      var path = createElement('path', { d: d });
      defs.append(path);
    }

    var textPath = createElement('textPath');
    // Set attributes on the `<textPath>`. The most important one
    // is the `xlink:href` that points to our newly created `<path/>` element in `<defs/>`.
    // Note that we also allow the following construct:
    // `t.text('my text', { textPath: { 'xlink:href': '#my-other-path' } })`.
    // In other words, one can completely skip the auto-creation of the path
    // and use any other arbitrary path that is in the document.
    if (!opt.textPath['xlink:href'] && path) {
      textPath.attr('xlink:href', '#' + path.node.id);
    }

    if (Object(opt.textPath) === opt.textPath) {
      textPath.attr(opt.textPath);
    }
    this.append(textPath);
    // Now all the `<tspan>`s will be inside the `<textPath>`.
    textNode = textPath.node;
  }

  var offset = 0;

  for (var i = 0; i < lines.length; i++) {

    var line = lines[i];
    // Shift all the <tspan> but first by one line (`1em`)
    var lineHeight = opt.lineHeight || '1em';
    if (opt.lineHeight === 'auto') {
      lineHeight = '1.5em';
    }
    var vLine = V('tspan', { dy: (i == 0 ? '0em' : lineHeight), x: this.attr('x') || 0 });
    vLine.addClass('v-line');

    if (line) {

      if (opt.annotations) {

        // Get the line height based on the biggest font size in the annotations for this line.
        var maxFontSize = 0;

        // Find the *compacted* annotations for this line.
        var lineAnnotations = V.annotateString(lines[i], isArray(opt.annotations) ? opt.annotations : [opt.annotations], { offset: -offset, includeAnnotationIndices: opt.includeAnnotationIndices });
        for (var j = 0; j < lineAnnotations.length; j++) {

          var annotation = lineAnnotations[j];
          if (isObject(annotation)) {

            var fontSize = parseInt(annotation.attrs['font-size'], 10);
            if (fontSize && fontSize > maxFontSize) {
              maxFontSize = fontSize;
            }

            tspan = V('tspan', annotation.attrs);
            if (opt.includeAnnotationIndices) {
              // If `opt.includeAnnotationIndices` is `true`,
              // set the list of indices of all the applied annotations
              // in the `annotations` attribute. This list is a comma
              // separated list of indices.
              tspan.attr('annotations', annotation.annotations);
            }
            if (annotation.attrs['class']) {
              tspan.addClass(annotation.attrs['class']);
            }
            tspan.node.textContent = annotation.t;

          } else {

            tspan = document.createTextNode(annotation || ' ');

          }
          vLine.append(tspan);
        }

        if (opt.lineHeight === 'auto' && maxFontSize && i !== 0) {

          vLine.attr('dy', (maxFontSize * 1.2) + 'px');
        }

      } else {

        vLine.node.textContent = line;
      }

    } else {

      // Make sure the textContent is never empty. If it is, add a dummy
      // character and make it invisible, making the following lines correctly
      // relatively positioned. `dy=1em` won't work with empty lines otherwise.
      vLine.addClass('v-empty-line');
      vLine.node.style.opacity = 0;
      vLine.node.textContent = '-';
    }

    V(textNode).append(vLine);

    offset += line.length + 1;      // + 1 = newline character.
  }

  return this;
};