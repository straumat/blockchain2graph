// Websocket connection
let connection = new WebSocket("ws://" + url + "/status");
connection.onmessage = function (e) {

	// We parse the response.
	let response = JSON.parse(e.data);

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

		//$("#logs").append("<div>" + $("#logType").val() + "</div>");

		// Blocks batch logs.
		if ($("#logType").val() == "blocks") {
			if (response.messageValue.includes("Blocks batch")) {
				$("#logs").append("<div>" + response.messageValue + "</div>");
			}
		}

		// Addresses batch logs.
		if ($("#logType").val() == "addresses") {
			if (response.messageValue.includes("Addresses batch")) {
				$("#logs").append("<div>" + response.messageValue + "</div>");
			}
		}

		// Addresses batch logs.
		if ($("#logType").val() == "transactions") {
			if (response.messageValue.includes("Transactions batch")) {
				$("#logs").append("<div>" + response.messageValue + "</div>");
			}
		}

		// Addresses batch logs.
		if ($("#logType").val() == "relations") {
			if (response.messageValue.includes("Relations batch")) {
				$("#logs").append("<div>" + response.messageValue + "</div>");
			}
		}

		// We clean to avoid having too much logs.
		let childrenLength = $("#logs").children().length;
		if (childrenLength > 18) {
			$("#logs").children().eq(0).remove();
		}
	}

	// Error message.
	if (response.messageType === "error") {
		$("#lastErrorMessage").text(response.messageValue);
	}

	// Execution time statistic.
	/*
	 if (response.messageType === "executionTimeStatistic") {
	 $("#executionTimeStatistic").text("Mean execution time : " + response.messageValue + " secs");
	 }
	 */

};