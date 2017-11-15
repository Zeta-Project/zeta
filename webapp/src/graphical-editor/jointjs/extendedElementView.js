import joint from 'jointjs';

/*
 * ExtendedElementView inherits joint.dia.ElementView.
 *
 * This class expand the functionality of elementview.
 * The new functions is the possibility to select elements when they are in
 * other elements. For example is it possible to select element 2 and 3 with the
 * mouse.(SHIFT must be pressed).
 *
 * +-----------------+
 * | 1 +----+ +----+ |
 * |   | 2  | | 3  | |
 * |   +----+ +----+ |
 * +-----------------+
 *
 * @author: Maximilian Göke
 */
export default joint.dia.ElementView.extend({

  // ---------- manipulate joint.dia.ElementView Methods start
  /*
   * This Method is overriden from joint.dia.ElementView. It's a little bit
   * customized and calls the super()-Methode
   */
  pointermove: function(evt, x, y) {
      /*
       * Ignore default behaviour if SHIFT is pressed.
       * @author: Maximilian Göke
       */
      if(evt.shiftKey) return;

      // call super
      joint.dia.ElementView.prototype.pointermove.apply(this, [evt,x ,y]);
  },

  /*
   * This Method is overriden from joint.dia.ElementView. It's a little bit
   * customized and calls the super()-Methode.
   */
  pointerup: function(evt, x, y) {
      /*
       * Ignore default behaviour if SHIFT is pressed.
       * @author: Maximilian Göke
       */
      if(evt.shiftKey) return;

      // call super
      joint.dia.ElementView.prototype.pointerup.apply(this, [evt,x ,y]);
  }

  // ---------- manipulate joint.dia.ElementView Methods end
});