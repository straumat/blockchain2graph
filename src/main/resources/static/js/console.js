// Websocket connection
var connection = new WebSocket("ws://" + url + "/status");
connection.onmessage = function (e) {

	// We parse the response.
	var response = JSON.parse(e.data);

	// Imported block count.
	if (response.messageType === "importedBlockCount") {
		$("#importedBlockCount").text(response.messageValue);
	}

	// Total block count.
	if (response.messageType === "totalBlockCount") {
		$("#totalBlockCount").text(response.messageValue);
	}

	// Log message.
	if (response.messageType === "log") {
		$("#logs").append("<div>" + response.messageValue + "</div>");

		// We clean to avoid having too much logs.
		var childrenLength = $("#logs").children().length;
		if (childrenLength > 19) {
			$("#logs").children().eq(0).remove();
		}
	}

	// Error message.
	if (response.messageType === "error") {
		$("#lastErrorMessage").text(response.messageValue);
	}

};

