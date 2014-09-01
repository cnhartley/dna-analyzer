
/**
 * 
 */
final var serviceServerURL = "ws://localhost:8080";
final var seqGrabberServiceURL = serviceServerURL
                                  + "/dnaa/services/sequence-grabber-service/";
var wsSeqGrabber = new WebSocket(seqGrabberServiceURL, 'xml');

wsSeqGrabber.onopen = function () {
	console.log("connected to web service");
};
wsSeqGrabber.onerror = function (error) {
	console.log("error occurred on web service: " + error);
};
wsSeqGrabber.onmessage = function (result) {
	console.log("web service result: [" + result.data + "]");
};

function sendBlockRequest(request) {
	wsSeqGrabber.send(request);
}

function sequenceGrabber_resultHandler(elementId, results) {
	if (document == undefined) {
		if (window == undefined) {
			console.error("document and window are underfined in the browser!");
		}
		else {
			window.alert("document is undefined in the browser!");
		}
	}
	
	Element element = document.getElementById("" + elementId);
	if (element != null) {
		element.innerHTML = results;
	}
}

; // EOF
