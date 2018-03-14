import {linkhelper} from '../generator/editor/LinkHelperGenerator'
import {validator} from '../generator/editor/ValidatorGenerator'
import '../generator/temporary/old/connectionstyle';

//import {getConnectionStyle,getLabels,getPlacings} from "../generator/shape/connectionDefinitionGenerator/ConnectionDefinitionGenerator";

var contextMenu = {
     DEBUG: false,

     menu: null,
     elements: null,
     visible: false,
     focusedElement: null,
     canSetLink: false,
     graph: null,
     linkID: null,
     edgeData: null,
     connectionStyleGenerator: null,
    linkhelperGenerator: null,
    validatorGenerator: null,

     init: function(graph){
        this.log("init contextMenu");
        this.graph = graph;
        this.connectionStyleGenerator = global.generatorFactory.state.connectionDefinitionGenerator;
        this.linkhelperGenerator = global.generatorFactory.state.linkhelperGenerator;
        this.validatorGenerator = global.generatorFactory.state.validatorGenerator;

        console.log("ValidatorGenerator:");
        console.log(this.validatorGenerator);
        this.createMenu(['']);
     },

     showMenu: function(x, y){
        y-=20; // center
        this.visible = true;
        this.menu.style.display = 'inline';
        this.menu.style.top = y + "px";
        this.menu.style.left = x + "px";

        this.log("showMenu on "+x + ":"+y);
     },

     hideMenu: function(){
         this.log("hideMenu");
         if(this.visible){
            this.menu.style.display = 'none';
            this.visible = false;
        }
         
         this.removeHighlighting();
     },

     // separate function that is being called from serverCommunication, because
     // jointJs overrides link.attributes.target when establishing a link
     enhanceLinkForEcore: function(link){
        if(this.edgeData != null){
            // set Link Ecore-Attributes
            link.attributes.sourceAttribute = this.edgeData.from;
            link.attributes.targetAttribute = this.edgeData.to;

            this.hideMenu();
            console.log("Link SourceAttribute: "+ link.attributes.sourceAttribute);
            console.log("Link TargetAttribute: "+ link.attributes.targetAttribute);

            // FIXED FOR PETRINET FOR NOW, USE GENERATED DATA!
            //  link.attributes.source.linkMappings = { InputArc: "inputArc", OutputArc: "outputArc"};
            //  link.attributes.target.linkMappings = { InputArc: "inputArc", OutputArc: "outputArc"};

            this.edgeData = null;
        }else{
            this.log("EdgeData is null!");
        }
     },

     clickedEl: function(el){
        this.log("Clicked Element:", el);

        // getEdgeData and remeber for enhancing Link
        this.edgeData = this.validatorGenerator.getEdgeData(this.elements[el]);
         this.edgeData = validator.getEdgeData(this.elements[el]);

        // set Link Style
        var styleName = this.edgeData.style;
        var link = this.graph.getCell(this.linkID);
        link.attributes.attrs = this.connectionStyleGenerator.getConnectionStyle(styleName);
        link.attributes.placings = this.connectionStyleGenerator.getPlacings(styleName);
        link.attributes.ecoreName = this.edgeData.type;
        link.attributes.styleSet = true;
         _.each(this.connectionStyleGenerator.getLabels(styleName), function(label, idx){
             label.attrs.text.text = linkhelper.getLabelText(this.elements[el], label.id);
             //label.attrs.text.textxx = this.linkhelperGenerator.getLabelText(this.elements[el], label.id);
             //console.log(label.attrs.text.text);
             //console.log(label.attrs.text.textxx);
             link.label(idx, label);
         }, this);
        this.hideMenu();
     },

     focusElement: function(linkID, targetEcoreName, targetElement, x, y){
        if(targetEcoreName){
           this.linkID = linkID;

           // get possible link types for sourceEcoreName -> targetEcoreName
           var sourceID = this.graph.getCell(linkID).attributes.source.id;
           var sourceEcoreName = this.graph.getCell(sourceID).attributes.ecoreName;
           this.log(sourceEcoreName + "->" + targetEcoreName);
        }else{ // element under cursor has no ecoreName => can't establish link
            return;
        }
        // build up menu for source and target element
        if(!this.focusedElement){
            //var menuList = this.validatorGenerator.getValidEdges(sourceEcoreName, targetEcoreName);
            var menuList = validator.getValidEdges(sourceEcoreName, targetEcoreName);
            this.createMenu(menuList);
            this.focusedElement = targetElement;
            this.showMenu(x,y);
            
            this.addHighlighting(menuList);
        }
     },

     lostFocus: function(newFocusedElement, x, y){
        // if the focus changed to our menu do not hide
        if($(newFocusedElement).hasClass( "ContextItem" )
            || $(newFocusedElement).hasClass( "menuItem" )
            || $(newFocusedElement).hasClass("menuTable")){
            return;
        }

        this.removeHighlighting();
        this.focusedElement=null;
        this.hideMenu();
     },
     
     addHighlighting: function(menuList) {
    	//highlighting of target element (adding class tag(->CSS)) depending on linking permissions (validator)
         var classNames = ""

         if (this.focusedElement.hasAttribute("class")) {
         	classNames = this.focusedElement.getAttribute("class");
         }
         
         if (menuList.length > 0) {
         	classNames = classNames + " linking-allowed";
         } else {
         	classNames = classNames + " linking-unallowed";
         }

         this.focusedElement.setAttribute("class", classNames); 
     },
     
     removeHighlighting: function() {
    	//removal of highlighting (remove class tag)
        if (this.focusedElement != null && this.focusedElement.hasAttribute("class")) {
        	var classNames = this.focusedElement.getAttribute("class");
         	classNames = classNames.replace("linking-allowed", "").replace("linking-unallowed", "");

         	this.focusedElement.setAttribute("class", classNames);
        }
     },

     // elements == [string title, .., ..]
     createMenu: function(elements){        
        this.log("Create ContextMenu for Linktypes");

        // remove old menu from DOM
        this.elements = null;
        if(this.menu){
            this.menu.parentNode.removeChild(this.menu);
        }

        ////////////////////////
        // Construct Menu in dom
        var menu = '<div id="contextMenu"><table class="menuTable">';
        if(elements.length > 0){ // the nodes are linkable, at least one link type exists
             this.elements = elements;
             elements.forEach(function(entry, index, array) {
                        menu += '<tr><td class="menuItem"><div id="menuItem'+index+'" class="ContextItem">'
                        menu += entry;
                        menu += '</div></td></tr>';
             });
             menu += '</table></div>';
             $("#contextMenuContainer").html(menu);
             // bind mouse handlers to elements
             elements.forEach(function(entry, index, array) {
                         var item = "#menuItem"+index;
                         $(item).mouseup(function() {
                             contextMenu.clickedEl(index);
                           //  contextMenu.hideMenu();
                         });

                         $(item).mouseenter(function() {
                            $(item).css("font-weight","bold");
                            $(item).css("background-color","#a1a1a1");
                            contextMenu.log("MouseEnter in ContextMenuItem");
                            contextMenu.canSetLink = true;
                         });
                         $(item).mouseleave(function() {
                              $(item).css("font-weight","normal");
                              $(item).css("background-color","#F0F0F0");
                         });
             });
        } els {  // nodes are not linkable
            menu += '<tr><td class="menuItem"><div class="ContextItem">';
            menu += "These elements are not linkable!";
            menu += '</div></td></tr>';
            menu += '</table></div>';
            $("#contextMenuContainer").html(menu);
            this.canSetLink = false;
        }

        this.menu = document.getElementById("contextMenu");
        $(this.menu).mouseleave(function(){
            contextMenu.canSetLink = false;
        });
        $('body').mouseup(function(){
             contextMenu.hideMenu();
        });

     },

     log : function(message) {
     	if (this.DEBUG) {
     		if (!message) {
     			message = "";
     		}
     		console.log(message);
     	}
     }
};