/*global url:true*/
/*eslint no-undef: "error"*/
// Websocket connection
let connection = new WebSocket("ws://" + url + "/status");
connection.onmessage = function (e) {

	// We parse the response.
	let response = JSON.parse(e.data);

	// Switch on message type.
	switch (response.messageType) {

		// -------------------------------------------------------------------------------------------------------------
		case "importedBlockCount":
			$(document).prop("title", "b2g - " + response.messageValue.toLocaleString() + " blocks imported");
			$("#importedBlockCount").text(response.messageValue.toLocaleString());
			break;

		// -------------------------------------------------------------------------------------------------------------
		case "totalBlockCount":
			$("#totalBlockCount").text(response.messageValue.toLocaleString());
			break;

		// -------------------------------------------------------------------------------------------------------------
		case "log":
			switch ($("#logType").val()) {
				case "blocks":
					if (response.messageValue.includes("Blocks batch")) {
						$("#logs").append("<div>" + response.messageValue.replace("Blocks batch - ", "") + "</div>");
					}
					break;

				case "addresses":
					if (response.messageValue.includes("Addresses batch")) {
						$("#logs").append("<div>" + response.messageValue.replace("Addresses batch - ", "") + "</div>");
					}
					break;

				case "transactions":
					if (response.messageValue.includes("Transactions batch")) {
						$("#logs").append("<div>" + response.messageValue.replace("Transactions batch - ", "") + "</div>");
					}
					break;

				case "relations":
					if (response.messageValue.includes("Relations batch")) {
						$("#logs").append("<div>" + response.messageValue.replace("Relations batch - ", "") + "</div>");
					}
					break;
			}
			// We clean to avoid having too much logs.
			if ($("#logs").children().length > 19) {
				$("#logs").children().eq(0).remove();
			}
			break;

		// -------------------------------------------------------------------------------------------------------------
		case "error":
			$("#lastErrorMessage").text(response.messageValue);
			break;

		// -------------------------------------------------------------------------------------------------------------
		case "averageBlockImportDuration":
			$("#executionTimeStatistic").text("Average block import duration : " + response.messageValue + " secs");
			break;
	}

};