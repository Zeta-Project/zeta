var userTags = {
	paper : null,
	uuidColorMap : {},

	tagDiv : null,
	uuidTagMap : {},
	uuidTimeoutMap : {},

	init : function(paper) {
		this.paper = paper;
		this.tagDiv = document.getElementById('user-tags');
	},

	colorFromUUID : function(uuid) {
		if (!(uuid in this.uuidColorMap)) {
			var parts = uuid.split('-')
			var ints = parts.map(function(d) {
				return parseInt(d, 16)
			});
			var code = ints[0];
			var blue = (code >> 16) & 31;
			var green = (code >> 21) & 31;
			var red = (code >> 27) & 31;
			this.uuidColorMap[uuid] = "rgb(" + (red << 3) + "," + (green << 3)
					+ "," + (blue << 3) + ")";
		}
		return this.uuidColorMap[uuid];
	},

	createTagIfNecessary : function(userID, userName) {
		if (userID in this.uuidTagMap)
			return;
		var tag = "<div class=\"tag\" " + "id=\"" + userID + "-tag" + "\">"
				+ userName + "</div>";
		this.tagDiv.innerHTML += tag;
		tag = document.getElementById(userID + "-tag");
		$(tag).css('background-color', this.colorFromUUID(userID));
		this.uuidTagMap[userID] = tag;
	},

	handleTagDrawing : function(cell, userID, userName) {
		this.createTagIfNecessary(userID, userName);

		var cellView = this.paper.findViewByModel(cell);
		var el = null;
		if (cellView) {
			el = document.getElementById(cellView.id);
		}
		if (el) {
			elOff = $(el).offset();

			$(document.getElementById(userID + "-tag")).offset({
				top : elOff.top + 10,
				left : elOff.left - 30
			});
			$(document.getElementById(userID + "-tag")).fadeIn('fast');

			var oldTimeout = this.uuidTimeoutMap[userID];
			if (oldTimeout != null) {
				window.clearTimeout(oldTimeout);
			}
			this.uuidTimeoutMap[userID] = setTimeout(function() {
				$(document.getElementById(userID + "-tag")).fadeOut('fast');
			}, 2000);
		}
		return;
	}
};