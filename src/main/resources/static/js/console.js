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
			$(document).prop("title", "b2g - " + response.messageValue + " blocks imported");
			$("#importedBlockCount").text(response.messageValue);
			break;

		// -------------------------------------------------------------------------------------------------------------
		case "totalBlockCount":
			$("#totalBlockCount").text(response.messageValue);
			break;

		// -------------------------------------------------------------------------------------------------------------
		case "log":
			switch ($("#logType").val()) {
				case "blocks":
					if (response.messageValue.includes("Blocks batch")) {
						$("#logs").append("<div>" + response.messageValue + "</div>");
					}
					break;

				case "addresses":
					if (response.messageValue.includes("Addresses batch")) {
						$("#logs").append("<div>" + response.messageValue + "</div>");
					}
					break;

				case "transactions":
					if (response.messageValue.includes("Transactions batch")) {
						$("#logs").append("<div>" + response.messageValue + "</div>");
					}
					break;

				case "relations":
					if (response.messageValue.includes("Relations batch")) {
						$("#logs").append("<div>" + response.messageValue + "</div>");
					}
					break;
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

	// We clean to avoid having too much logs.
	let childrenLength = $("#logs").children().length;
	if (childrenLength > 18) {
		$("#logs").children().eq(0).remove();
	}

}