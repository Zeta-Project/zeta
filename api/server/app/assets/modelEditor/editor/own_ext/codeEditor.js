/*var sideBar = {
    selected : null,

    clickedFileItem: function(sender) {
        console.log("before ",this.selected);
        if(this.selected) {
            this.selected.removeClass("file-item-highlight");
        }
        this.selected = $(sender);
        this.selected.addClass("file-item-highlight");

        console.log("\t",this.selected);
    }
}*/

 $(function() {
            $('#code-overlay').easyModal({
                top: 100,
                overlay: 0.2,
                overlayClose: true,
                closeOnEscape: true
	        });
});
/*
$( document ).ready(function() {
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/chrome");
        editor.getSession().setMode("ace/mode/javascript");

        $( "#btn-code-editor" ).click(function(e) {
               var target = $(this).attr('href');
               $(target).trigger('openModal');
               e.preventDefault();
		});
});*/