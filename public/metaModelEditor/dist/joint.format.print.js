/*! Rappid - the diagramming toolkit

Copyright (c) 2014 client IO

 2014-04-14 


This Source Code Form is subject to the terms of the Rappid Academic License
, v. 1.0. If a copy of the Rappid License was not distributed with this
file, You can obtain one at http://jointjs.com/license/rappid_academic_v1.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


;(function() {

    var printEvents = 'onbeforeprint' in window;

    function beforePrint(opt, data) {

        var svg = V(this.svg);

        var bbox = this.getContentBBox().moveAndExpand({
            x: -opt.padding,
            y: -opt.padding,
            width: 2 * opt.padding,
            height: 2 * opt.padding
        });

        // store original svg attributes
        data.attrs = {
            width: svg.attr('width'),
            height: svg.attr('height'),
            viewBox: svg.attr('viewBox')
        }

        // stretch the content to the size of the container
        svg.attr({
            width: '100%',
            height: '100%',
            viewBox: [bbox.x, bbox.y, bbox.width, bbox.height].join(' ')
        });

        // append the paper straight to the body
        this.$el.addClass('printarea').addClass(opt.size);

        if (opt.detachBody) {

            // store reference to the paper parent
            data.$parent = this.$el.parent();

            // detach everything from body and store it
            data.$content = $(document.body).children().detach();

            this.$el.appendTo(document.body);
        }
    }

    function afterPrint(opt, data) {

        var svg = V(this.svg);

        // Note that IE 9 in order to delete attribute requires setting null,
        // calling `svg.node.removeAttribute('viewBox')` does not work there for some reason,
        // (not even the `removeAttributeNS()` version).
        // On the other hand Firefox doesn't like setting null for viewBox and throws a warning.
        // But that's something we have to put up with.
        svg.attr(data.attrs);

        this.$el.removeClass('printarea').removeClass(opt.size)

        if (opt.detachBody) {

            // append the paper to its original parent
            this.$el.appendTo(data.$parent);

            // append the original body
            data.$content.appendTo(document.body);
        }
    }

    joint.dia.Paper.prototype.print = function(opt) {

        opt = opt || {};

        _.defaults(opt, {
            size: 'a4', // allows adding custom sizes through css
            padding: 5,
            detachBody: true // can be disabled if detaching body is not found desired.
            /*
              In that case a custom css is required to position the paper to cover the entire screen
              and to hide all elements, whose presence are not desirable in the output print page. i.e:

              @media print {

                .printarea {
                  position: absolute;
                  left: 0px;
                  top: 0px;
                }

                .stencil, .inspector, .toolbar {
                  display: none;
                }
              }

            */
        });

        // data handovered between beforePrint and afterPrint
        var data = {};

        // create local versions of before/after methods
        var localBeforePrint = _.bind(beforePrint, this, opt, data);
        var localAfterPrint = _.bind(afterPrint, this, opt, data);

        // before print

        if (printEvents) {

            // Firefox and IE

            $(window).one('beforeprint', localBeforePrint);
            $(window).one('afterprint', localAfterPrint);

        } else {

            // Chrome, Opera, Safari

            localBeforePrint();
        }

        // print

        window.print();

        // after print

        if (!printEvents) {

            // Chrome, Opera, Safari

            var onceAfterPrint = _.once(localAfterPrint);

            // although mouseover works pretty reliably
            $(document).one('mouseover', onceAfterPrint);

            // to make sure an app won't get stuck without its original body, we'll adding delayed version
            _.delay(onceAfterPrint, 1000);
        }
    };

})();