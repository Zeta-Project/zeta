/*! Rappid - the diagramming toolkit

Copyright (c) 2014 client IO

 2014-04-14 


This Source Code Form is subject to the terms of the Rappid Academic License
, v. 1.0. If a copy of the Rappid License was not distributed with this
file, You can obtain one at http://jointjs.com/license/rappid_academic_v1.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


(function() {

    var _resize = joint.dia.Element.prototype.resize;

    joint.dia.Element.prototype.resize = resize;

    function resize(width, height, opt) {

	if (_.isUndefined(opt)) {

	    return _resize.call(this, width, height);

	}

	// Default options gives as the same behaviour as the original resize method.
	opt.direction = opt.direction || 'bottom-right';

	// Get the angle and clamp its value between 0 and 360 degrees.
	var angle = g.normalizeAngle(this.get('angle') || 0);

	var quadrant = { 'top-right': 0, 'top-left': 1, 'bottom-left': 2, 'bottom-right': 3 }[opt.direction];

	if (opt.absolute) {

	    // We are taking the element's rotation into account
	    quadrant += Math.floor((angle + 45) / 90);
	    quadrant %= 4;
	}

	// This is a rectangle in size of the unrotated element.
	var bbox = this.getBBox();

	// Pick the corner point on the element, which meant to stay on its place before and
	// after the rotation.
	var indentFixedPoint = bbox[['bottomLeft', 'corner', 'topRight', 'origin'][quadrant]]();

	// Find  an image of the previous indent point. This is the position, where is the
	// point actually located on the screen.
	var imageFixedPoint = g.point(indentFixedPoint).rotate(bbox.center(), -angle);

	// Every point on the element rotates around a circle with the centre of rotation
	// in the middle of the element while the whole element is being rotated. That means
	// that the distance from a point in the corner of the element (supposed its always rect) to
	// the center of the element doesn't change during the rotation and therefore it equals
	// to a distance on unrotated element.
	// We can find the distance as DISTANCE = (ELEMENTWIDTH/2)^2 + (ELEMENTHEIGHT/2)^2)^0.5.
	var radius = Math.sqrt(width*width + height*height) / 2;

	// Now we are looking for an angle between x-axis and the line starting at image of fixed point
	// and ending at the center of the element. We call this angle `alpha`.

	// The image of a fixed point is located in n-th quadrant. For each quadrant passed
	// going anti-clockwise we have to add 90 degrees. Note that the first quadrant has index 0.
	//
	// 3 | 2
	// --c-- Quadrant positions around the element's center `c`
	// 0 | 1
	//
	var alpha = quadrant * Math.PI/2;

	// Add an angle between the beginning of the current quadrant (line parallel with x-axis or y-axis
	// going through the center of the element) and line crossing the indent of the fixed point and the center
	// of the element. This is the angle we need but on the unrotated element.
	alpha += Math.atan(quadrant % 2 == 0 ? height/width : width/height);

	// Lastly we have to deduct the original angle the element was rotated by and that's it.
	alpha -= g.toRad(angle);

	// With this angle and distance we can easily calculate the centre of the unrotated element.
	// Note that fromPolar constructor accepts an angle in radians.
	var center = g.point.fromPolar(radius, alpha, imageFixedPoint);

	// The top left corner on the unrotated element has to be half a width on the left
	// and half a height to the top from the center. This will be the origin of rectangle
	// we were looking for.
	var origin = g.point(center).offset( width / -2, height / -2);

	// Finally resize the element and adjust the position.
	this.resize(width, height).position(origin.x, origin.y);
    };

})()