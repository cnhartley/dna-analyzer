<!DOCTYPE html>
<%@page import="java.util.HashMap"%>
<%
final String versionCode = "1.01.001a";
final String pageTitlePrefix = "DNAA";

final String paramId = request.getParameter("id");
final int sequenceId = paramId != null ? Integer.parseInt(paramId) : -1;

// TODO Database request for the sequence information based on the id parameter
//result = getDatabaseInterface().getSequenceInfoById(sequenceId);
///// TEMPORARY FOR TESTING ONLY /////
HashMap<String,String> result = new HashMap<String,String>();
result.put("id", paramId);
result.put("organism", "somesequenceone xbactirium");
result.put("length", "90324857");
result.put("isDNA", "true");
///// END TEMPORARY TESTING STUFF /////

final String sequenceOrganism = result.get("organism");
final long sequenceLength = Long.parseLong(result.get("length"));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">

<title><%=pageTitlePrefix %> - Sequence Viewer</title>

<link rel="shortcut icon" href="./img/favicon.ico" />
<link rel="apple-touch-icon image_src" href="./img/apple-touch-icon.png" />

<link rel="stylesheet" type="text/css" href="./css/defaults.css" />
<link rel="stylesheet" type="text/css" href="./css/main.css" />
<link rel="stylesheet" type="text/css" href="./css/viewer.css" />

<!-- script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script -->
<script type="text/javascript" src="./js/lib/jquery.min.js"></script>
<script type="text/javascript" src="./js/nucleotide.utils.js"></script>
<script type="text/javascript">

//// start virtual database ////
var seqNames = [
        "somesequenceone xbactirium",
        "somesequencetwo ybactirium"
];
var seqs = [];// = [ generateSequence(5), generateSequence(7) ];

function getSequenceLength(id) {
	var len = 0;
	for (var i = 0; i < seqs[id].length; i++)
		len += seqs[id][i].length;
	return len;
}
function generateSequence(blocks) {
	var seq = new Array(blocks);
	for (var i = 0; i < blocks; i++) {
		seq[i] = generateSequenceBlock();
		console.log("pre-pack[" + seq[i] + "]")
		seq[i] = pack(seq[i]);
		console.log("post-pack[" + seq[i] + "]")
		console.log(" -->unpack[" + unpack(seq[i], ['a','c','g','t']) + "]");
	}
	return seq;
}
function generateSequenceBlock() {
	var dna = ['a','A','c','C','g','G','t','T'];
	var rna = ['a','A','c','C','g','G','u','U'];
	var len = 64;
	var block = "";
	
	for (var i = 0; i < len; i++) {
		block = block + dna[Math.floor(Math.random() * dna.length)];
	}
	
	return "" + block;
}//*/

//// end virtual database ////

var canvas;
var isLoaded = false;

var seqId = -1;
var seqBlocks = [];
var seqBlockIndex = 0;

var boxSize = 12; // font size is 2 less than this value.
var d = 1.25;


function getCanvas() {
	if (typeof canvas === 'undefined')
        canvas = document.getElementById("scrollableViewer");
    
	return canvas;
}

function paintCanvas() {
	if (canvas !== undefined && isLoaded) {
		var t = (new Date()).getTime();
	    _paintCanvas(canvas.getContext("2d"), canvas.width, canvas.height);
		rightSpan.innerHTML = "repaint in " + ((new Date()).getTime() - t) + "ms";
	}
}

var mouse = function () {
	this.x = 0;
	this.y = 0;
	this.isOver = false;
	this.isDragging = false;
	this.dragFrom = { x:0, y:0 };
};
var highlighter = {
	getWidth: function () { return 24; },
	getFillColor: function () { return "#EEEEFF"; },
	getLineColor: function () { return "#DDDDCC"; }
};


var _piBy180 = Math.PI / 180;
var _boxSizeBy2 = boxSize >> 1;
var _boxSizePlus1 = boxSize + 1;

function _paintCanvas(ctx, w, h) {
	ctx.clearRect(0, 0, w, h);
	ctx.font = (boxSize - 2) + "px Verdana,monospaced";
    
	_paintBackground(ctx, w, h);

    var x = 0;
    var y = (h >> 1) - _boxSizeBy2;
    var currentSeq = seqBlocks[seqBlockIndex] || "";
    var seqWidth = (boxSize + 1) * currentSeq.length;
    
    if (seqBlockIndex == 0)
    	x = 0;
    else if (seqBlockIndex == seqBlocks.length - 1)
    	x = w - seqWidth;
    else
    	x = (w - seqWidth) >> 1;
    
   	// check for new drag offset...
    if (mouse !== undefined && mouse.isDragging)
    	x += mouse.x - mouse.dragFrom.x;
   	
    if (seqId > -1)
        _paintSequence(ctx, x, y, currentSeq);
    
    if (x > 0 && seqBlockIndex > 0)
    	_paintSequence(ctx, x - _boxSizePlus1 * seqBlocks[seqBlockIndex - 1].length, y, seqBlocks[seqBlockIndex - 1] || []);
    
    if (x + seqWidth < w && seqBlockIndex < seqBlocks.length - 1)
    	_paintSequence(ctx, x + seqWidth, y, seqBlocks[seqBlockIndex + 1] || [])

   	// paint the highlighter after everything else is painted.
   	_paintMouseHighlighter(ctx, w, h);
}

var twoPI = 2.0 * Math.PI;
var bgCircles = [];

function getGradientPaint(ctx, x1, y1, x2, y2, colors) {
	colors = colors || ["#CCCCCC"];
	var step = 1 / (colors.length - 1);
    var gradientPaint = ctx.createLinearGradient(x1, y1, x2, y2);
    for (var i = 0, pos = 0.0; i < colors.length; i++, pos += step)
        gradientPaint.addColorStop(pos, colors[i]);
    return gradientPaint;
}

function _paintBackground(ctx, w, h) {
	var colors = [ "#D8E0EF", "#F0F8FF", "#D8E0EF" ];
	ctx.fillStyle = getGradientPaint(ctx, 0, 0, 0, h, colors);
	ctx.fillRect(0, 0, w, h);
    
    ctx.strokeStyle = "#CCCCCC";
    ctx.fillStyle = "#DDDDDD";
    ctx.globalAlpha = 0.5;
    
    for (var i = 0; i < bgCircles.length; i++) {
    	ctx.beginPath();
        ctx.arc(bgCircles[i].x, bgCircles[i].y, bgCircles[i].radius, 0 , twoPI);
        ctx.fill();
    	ctx.stroke();
    }

    ctx.globalAlpha = 1.0;
    
    ctx.beginPath();
    ctx.moveTo(0,0);
    ctx.lineTo(w,h);
    ctx.stroke();
    
    ctx.beginPath();
    ctx.moveTo(0,h);
    ctx.lineTo(w,0);
    ctx.stroke();
}

function _paintMouseHighlighter(ctx, w, h) {
	if (mouse === undefined || highlighter === undefined || !mouse.isOver)
		return;
	
	var hw = highlighter.getWidth();
	var x = mouse.x - (hw >> 1);
    
	if (x < 0)
		x = 0;
	else if (x + hw > w)
		x = w - hw;
	
	ctx.globalAlpha = 0.5;
    
	ctx.fillStyle = mouse.isDragging ? "#CCCCEE" : highlighter.getFillColor();
	ctx.fillRect(x, 0, hw, h);
	
	ctx.strokeStyle = highlighter.getLineColor();
	ctx.strokeRect(x, 0, hw, h);
	
	ctx.globalAlpha = 1.0;
}

var margin = 36;
var curve = 32;
var upper = 12;

var _piByCurve = Math.PI / curve;
var _upperBy2 = upper >> 1;

var senseCoords = function(x) { //returns y-coordinate along the path.
	x += _boxSizeBy2;
	var width = canvas.width;
	
	if (x < margin || x > canvas.width - margin)
		y = 0;
	else if (x >= margin && x <= curve + margin)
		y = _upperBy2 * (1 - Math.cos((x - margin) * _piByCurve));
	else if (width >= x + margin && width <= x + margin + curve)
		y = _upperBy2 * (1 + Math.cos((x - width + curve + margin) * _piByCurve));
	else
		y = upper;
	
	return y;
};

function _paintSequence(ctx, x, y, seq) {
	var x1 = x;
	
	// block separator
    _paintSeparator(ctx, x1, y);
	
	for (var i = 0; i < seq.length; i++) {
		_paintNucleotide(ctx, seq[i].toUpperCase(), x1, y - senseCoords(x1));
		x1 += _boxSizePlus1;
	}
	
	// block separator
	_paintSeparator(ctx, x1, y);
}

function _rotatePathAbout(path, degrees, originX, originY) {
	var rotatedPath = new Array(path.length);
	var rad = degrees * _piBy180;
	var s = Math.sin(rad);
	var c = Math.cos(rad);
	var x, y;
	
	for (var i = 0; i < path.length; i += 2) {
		// translate point back to origin:
		x = path[i] - originX;
		y = path[i+1] - originY;

		// translate point back:
		rotatedPath[i] = x * c - y * s + originX;
		rotatedPath[i+1] = x * s + y * c + originY;
	}
	
	return rotatedPath;
}
function _paintObject(ctx, x, y, pathFn) {
	var path = pathFn(x, y, boxSize);
	var i = 0;
	
	// rotate by the angle
	var originX = x + _boxSizeBy2;
	var originY = y + _boxSizeBy2;
	var angle = 0;
	path = _rotatePathAbout(path, angle, originX, originY);
	
	ctx.beginPath();
	ctx.moveTo(path[i], path[i+1]);
	do {
        i += 2;
		ctx.lineTo(path[i], path[i+1]);
	} while (i + 2 < path.length);
}
function _paintNucleotide(ctx, nucleotide, x, y) {
	if (x <= -boxSize || x > getCanvas.width)
		return;
	
	_paintObject(ctx, x, y, nucleotidePath[nucleotide] || blankPath);
	// draw shape...
    ctx.fillStyle = nucleotideFill[nucleotide] || '#666666';
    ctx.fill();
    ctx.strokeStyle = nucleotideLine[nucleotide] || '#CCCCCC';
    ctx.stroke();
    
    // draw text...
    ctx.fillStyle = '#FFFFFF';
    ctx.fillText(nucleotide, x + 2, y + boxSize - 2);
}
function _paintSeparator(ctx, x, y) {
	if (x <= -boxSize || x > getCanvas.width)
        return;
    
	ctx.beginPath();
    ctx.moveTo(x, y - boxSize);
    ctx.lineTo(x, y + boxSize + boxSize);
    ctx.strokeStyle = '#555555';
    ctx.stroke();
}


function init() {
	console.log("initializing...");
	
	canvas = document.getElementById("scrollableViewer");
	leftSpan = document.getElementById("leftSpan");
	rightSpan = document.getElementById("rightSpan");
	
	seqs = [ generateSequence(5), generateSequence(7) ];

	var nucleotideSequence = new NucleotideSequence(
		"<%=result.get("id") %>",
		"<%=result.get("organism") %>",
		4096, 100);
	nucleotideSequence.get(0); // cause the nucleotide sequence to start caching
	                           // at the begining of the sequence.
	
	resizeCanvas();
	
	var numberOfCircles = 100;
	var circleRadius;
    var centerX;
    var centerY;
	for (var i = 0; i < numberOfCircles; i++) {
        circleRadius = Math.floor((Math.random() * 70) + 5);
        centerX = Math.floor(Math.random() * 4096);
        centerY = Math.floor(Math.random() * canvas.height);
        bgCircles[i] = { x: centerX, y: centerY, radius: circleRadius };
    }
	
	isLoaded = true;
	paintCanvas();
}

function onMouseMove(event) {
	var offsets = canvas.getBoundingClientRect();
	mouse.x = event.clientX - offsets.left;
	mouse.y = event.clientY - offsets.top;
	paintCanvas();
	leftSpan.innerHTML = "Coordinates: (" + mouse.x + ", " + mouse.y + ")";
}

function onMouseOut(event) {
	mouse.isOver = false;
	mouse.isDragging = false;
    paintCanvas();
}

function onMouseOver(event) {
	mouse.isOver = true;
}

function onMouseDown(event) {
    mouse.isDragging = true;
    var offsets = canvas.getBoundingClientRect();
    mouse.dragFrom = {x: event.clientX - offsets.left,y: event.clientY - offsets.top};
    paintCanvas();
    canvas.style.cursor = "ew-resize";
}

function onMouseUp(event) {
	mouse.isDragging = false;
    paintCanvas();
    canvas.style.cursor = "pointer";
}

function onMouseWheel(event) {
	console.log("onMouseWheel=" + event);
}

function onDrag(event) {
	//console.log("onDrag=" + event);
}

function onDragEnd(event) {
	//console.log("onDragEnd=" + event);
}

function onDragEnter(event) {
	//console.log("onDragEnter=" + event);
}

function onDragLeave(event) {
	//console.log("onDragLeave=" + event);
}

function onDragOver(event) {
	//console.log("onDragOver=" + event);
}

function onDragStart(event) {
	//console.log("onDragStart=" + event);
}

function onClick(event) {
	//TODO determine what element the mouse is over to display the appropriate info within the tooltip.
	setTooltip("New Title - (" + event.clientX + "," + event.clientY + ")",
			"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
			+ "Ut sagittis pulvinar mattis. <i>Vestibulum eget condimentum turpis.</i><ol>"
			+ "<li>Praesent posuere nulla in dolor rutrum</li><li>eget ultrices nibh laoreet</li> "
			+ "<li>Suspendisse nunc erat</li><li>mollis non tempor sodales</li><li>mattis tincidunt diam</li></ol>"
			+ "Nam at neque ut justo pharetra ultrices porta in purus. <b>Nulla facilisi.</b> "
			+ "Donec id erat eget dui congue fermentum non a libero. </p>"
			+ "<p>Pellentesque cursus metus nec eros pulvinar, quis eleifend turpis convallis. "
			+ "Mauris tristique auctor ex et rutrum. "
			+ "Curabitur justo nisl, semper eget urna nec, aliquet vehicula sem. "
			+ "In lacus lorem, fermentum nec suscipit ut, congue id enim. Cras sed lectus est.</p>"
			+ "<p><a href=''>more info...</a></p>");
}

function resizeCanvas() {
	var w = window.innerWidth - canvas.offsetLeft * 2;
	var h = 144;
	
	canvas.setAttribute("width", w);
	canvas.setAttribute("height", h);
	
	document.getElementById("scrollableViewerContainer").setAttribute("height", h);
	document.getElementById("scrollLeftButton").setAttribute("height", h);
	document.getElementById("scrollRightButton").setAttribute("height", h);
	paintCanvas();
}

function scrollViewerLeft(e) {
    if (seqBlockIndex > 0) {
    	seqBlockIndex -= 1;
    	paintCanvas();
    }
}

function scrollViewerRight(e) {
    if (seqBlockIndex < seqBlocks.length - 1) {
    	seqBlockIndex += 1;
    	paintCanvas();
    }
}



function requestSequence(id) {
	//TODO: load the correct sequence...
	if (seqId != id) {
		seqId = id;
		seqBlocks = seqs[id] || [];
		seqBlockIndex = 0;
		var info = "<table>"
				+ " <tr><td>Organism name:</td><td>" + seqNames[id] + "</td></tr>"
				+ " <tr><td>Gnome length:</td><td>" + getSequenceLength(id) + "</td></tr>"
				+ "</table>\n";
		
		document.getElementById("sequenceInfoContainer").innerHTML = info;
		console.log("loaded sequence " + (id + 1) + ":");
		for (var i = 0; i < seqs[id].length; i++) {
			var unpacked = unpack(seqs[id][i], ['a','c','g','t']);
			console.log("unpack => [" + seqs[id][i] + "] --> [" + unpacked + "]");
			seqs[id][i] = unpacked;
		}
		paintCanvas();
	}
}

function setTooltip(title, content) {
	var tooltip = document.getElementById("viewerTooltip");
	var tooltipTitle = document.getElementById("viewerTooltipTitle");
	var tooltipContent = document.getElementById("viewerTooltipContent");
	
	if (tooltip === undefined || tooltipContent === undefined)
		return;
	
	if (tooltip.style.display === "none")
		tooltip.style.display = "block";
	
	tooltipTitle.innerHTML = title;
	tooltipContent.innerHTML = content;
}

function hideTooltip() {
	var tooltip = document.getElementById("viewerTooltip");
	
	if (tooltip !== undefined)
		tooltip.style.display = "none";
}


//onload events
$(document).ready( function() {
	console.log("document loaded");
	init();
});
 
$(window).load( function() {
	console.log("window loaded");	
	//TODO: call to load the initial sequence...
	
	window.addEventListener("resize", resizeCanvas, false);
	window.addEventListener("orientationchange", resizeCanvas, false);
	
	document.getElementById("viewerTooltipCloseButton").addEventListener("click", hideTooltip, true);
	
	setTimeout(requestSequence(0), 500);
});

</script>
</head>
<body>

<header>
	<div id='versionInfo'>Version <%=versionCode %></div>
	<h1><a href='index.jsp'>DNA Analyzer</a></h1>
	<h2>Sequence Viewer</h2>
</header>

<nav>
	<ol>
		<li><a href="index.jsp" alt="Home page">Home</a></li>
		<li><a href="newSeq.jsp" alt="New nucleotide sequence to evaluate">New Sequence</a></li>
		<li><a href="lookup.jsp" alt="Lookup nucleotide sequences, amino acids, and proteins">Lookup</a></li>
		<li><a href="help.jsp" alt="Find help for this website">Help</a></li>
		
		<li><a id="link1" onclick="requestSequence(0);">Short cut [Sequence 1]</a></li>
		<li><a id="link2" onclick="requestSequence(1);">Short cut [Sequence 2]</a></li>
		
		<span>
		<li><a href="register.php" alt="Register a new user">Register</a></li> 
		<li><a href="signin.php" alt="Sign in as an existing user">Sign in</a></li>
		</span>
	</ol>
</nav>

<details>
	<summary>Sequence Information:</summary>
	<p>Some lame long winded description that probably reads like a Dicken's novel</p>
	<ul>
		<li><b>Sequence ID <%=result.get("id") %></b></li>
		<li>Organism: <em><%=result.get("organism") %></em></li>
		<li>Gnome size <%=Long.parseLong(result.get("length")) %></li>
		<li><em><%=(Boolean.parseBoolean(result.get("isDNA")) ? "DNA (Deoxyr" : "RNA (R") %>ibonucleic Acid)</em></li>
	</ul>
	<div id="sequenceInfoContainer"><em>[No sequence specified]</em></div>
</details>

<div id="scrollableViewerContainer">
	<a id="scrollLeftButton" onclick="scrollViewerLeft(event);">&ltrif;</a>
	<a id="scrollRightButton" onclick="scrollViewerRight(event);">&rtrif;</a>
    <canvas id="scrollableViewer"
	        onmousedown="onMouseDown(event)"
	        onmousemove="onMouseMove(event)"
	        onmouseout="onMouseOut(event)"
	        onmouseover="onMouseOver(event)"
	        onmouseup="onMouseUp(event)"
	        onmousewheel="onMouseWheel(event)"
	        ondrag="onDrag(event)"
	        ondragend="onDragEnd(event)"
	        ondragenter="onDragEnter(event)"
	        ondragleave="onDragLeave(event)"
	        ondragover="onDragOver(event)"
	        ondragstart="onDragStart(event)"
	        onclick="onClick(event)"></canvas>
    <div>
		<span id="leftSpan"><em>[no data]</em></span>
		<span id="rightSpan"><em>[no data]</em></span>
	</div>
</div>

<aside id="viewerTooltip">
	<span id="viewerTooltipCloseButton">&times;<!-- &cross; x X --></span>
	<h1 id="viewerTooltipTitle">Some Very Long Drawn-Out Title</h1>
	<div id="viewerTooltipContent"><em>[no information]</em></div>
</aside>

<footer>
DNA Analyzer Application &amp; Web Service &copy; 2014-2015, Christopher N. Hartley<br />
In Collaboration with California Polytechnic State University as a Senior Project
</footer>

</body>
</html>