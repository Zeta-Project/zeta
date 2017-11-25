export default {
	chatCount : 0,
	chatVisibility : 'none',
	notificationBubble : document.getElementById('notification-bubble'),
	chatToggleContainer : document.getElementById('chat-toggle-container'),

	replaceUserList : function(new_list) {
		var nodeList = document.querySelectorAll("#chat-users");
		for (var i = 0, length = nodeList.length; i < length; i++) {
			nodeList[i].innerHTML = "Online users: " + new_list;
		}
	},

	addToChatList : function(message) {
		element = "<div>" + message.userName + ": " + message.msg + "</div>";
		var chatList = document.getElementById('chat-messages')
		chatList.innerHTML += element;
		chatList.scrollTop = chatList.scrollHeight;

		if (this.chatVisibility == 'none') {
			this.chatCount += 1;
			this.setNotificationCount();
		}
	},

	keyPressed : function(e) {
		var textField = document.getElementById('chat-input-textfield');
		if (e.keyCode == 13 && textField.value !== "") {
			serverCommunication.sendChatMessage(textField.value);
			textField.value = "";
		}
	},

	setNotificationCount : function() {
		this.notificationBubble.innerHTML = this.chatCount;
		if (this.chatCount == 0) {
			this.notificationBubble.style.backgroundColor = 'grey';
		} else {
			this.notificationBubble.style.backgroundColor = 'red';
		}
	},

	toggleChatVisibility : function() {
		if (this.chatVisibility == 'none') {
			this.chatToggleContainer.style.display = this.chatVisibility = 'block';
			this.setNotificationCount(this.chatCount = 0);
		} else {
			this.chatToggleContainer.style.display = this.chatVisibility = 'none';
		}

		return false;
	}
};