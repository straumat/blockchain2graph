function configure(url) {

    /*global url:true*/
    /*eslint no-undef: "error"*/
    // Websocket connection
    let connection = new WebSocket("ws://" + url + "/status"), response;

    connection.onmessage = function (e) {
        let logs = $("#logs");

        // We parse the response.
        let responseModel = {messageType: "", messageValue: ""};
        response = jQuery.extend(responseModel, JSON.parse(e.data));

        // Switch on message type.
        switch (response.messageType) {

            // -------------------------------------------------------------------------------------------------------------
            case "importedBlockCount":
                if (response.messageValue !== -1) {
                    $(document).prop("title", "b2g - " + response.messageValue.toLocaleString() + " blocks imported");
                    $("#importedBlockCount").text(response.messageValue.toLocaleString());
                } else {
                    $("#importedBlockCount").text("-");
                }
                break;

            // -------------------------------------------------------------------------------------------------------------
            case "totalBlockCount":
                if (response.messageValue !== -1) {
                    $("#totalBlockCount").text(response.messageValue.toLocaleString());
                } else {
                    $("#totalBlockCount").text("-");
                }
                break;

            // -------------------------------------------------------------------------------------------------------------
            case "log":
                logs.append("<div>" + response.messageValue.replace("Blocks batch - ", "") + "</div>");
                // We clean to avoid having too much logs.
                if (logs.children().length > 19) {
                    logs.children().eq(0).remove();
                }
                break;

            // -------------------------------------------------------------------------------------------------------------
            case "averageBlockImportDuration":
                $("#executionTimeStatistic").text("Average block import duration : " + response.messageValue + " secs");
                break;

            // -------------------------------------------------------------------------------------------------------------
            case "error":
                $("#lastErrorMessage").text(response.messageValue);
                break;
        }

    };
}