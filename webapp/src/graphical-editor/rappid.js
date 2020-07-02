import 'jointjs/dist/joint.css';
import 'rappid/css/joint.ui.freeTransform.css';
import 'rappid/css/joint.ui.selectionView.css';
import 'rappid/css/joint.ui.paperScroller.css';
import 'rappid/css/joint.ui.tooltip.css';
import 'rappid/css/joint.ui.halo.css';
import 'rappid/css/joint.format.print.css';

// Load Rappid (jointjs extensions)
import 'imports-loader?exports=>false&joint&_=lodash!string-replace-loader?search=require&replace="false"&flags=g!rappid/dist/joint.shapes.uml';
import 'imports-loader?joint&Backbone=backbone1.0&V=>joint.V&g=>joint.g!string-replace-loader?search=this\\["joint"\\]&replace=joint&flags=g!rappid/dist/joint.ui.halo';
import 'imports-loader?joint&g=>joint.g!rappid/dist/joint.dia.freeTransform';
import 'imports-loader?joint&Backbone=backbone1.0&g=>joint.g!string-replace-loader?search=this\\["joint"\\]&replace=joint&flags=g!rappid/dist/joint.ui.freeTransform';
import 'imports-loader?joint&Backbone=backbone1.0!string-replace-loader?search=this\\["joint"\\]&replace=joint&flags=g!rappid/dist/joint.ui.inspector';
import 'imports-loader?joint&Backbone=backbone1.0&V=>joint.V!rappid/dist/joint.ui.selectionView';
import 'imports-loader?joint!rappid/dist/joint.ui.clipboard';
import 'imports-loader?joint&Backbone=backbone1.0&V=>joint.V&g=>joint.g!string-replace-loader?search=this\\["joint"\\]&replace=joint&flags=g!rappid/dist/joint.ui.stencil';
import 'imports-loader?joint&Backbone=backbone1.0&V=>joint.V!rappid/dist/joint.ui.paperScroller';
import 'imports-loader?joint&Backbone=backbone1.0&V=>joint.V!rappid/dist/joint.ui.tooltip';
import 'imports-loader?joint&V=>joint.V!rappid/dist/joint.format.svg';
import 'imports-loader?joint!rappid/dist/joint.format.raster';
import 'imports-loader?joint&V=>joint.V!rappid/dist/joint.format.print';
import 'imports-loader?joint!rappid/dist/joint.dia.command';
import 'imports-loader?joint!rappid/dist/joint.dia.validator';
import 'imports-loader?joint!rappid/dist/joint.layout.ForceDirected';
import 'imports-loader?joint!rappid/dist/joint.layout.GridLayout';
import 'imports-loader?joint&g=>joint.g!rappid/dist/joint.layout.DirectedGraph';