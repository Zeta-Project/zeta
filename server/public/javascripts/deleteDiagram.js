$(function() {
	$(".deleteDiagram").click(function(event) {
		event.preventDefault();
		$.delete($(this).attr('href'), function() {
			window.location.reload();
		});
	});
});