<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">

header {
    margin: 0;
    padding: 0;
}
header h1,h2 {
    margin: 0;
    padding: 0;
}
footer {
    clear: both;
    margin: 0;
    padding: 1em 0em;
}
#viewer {
    background-color: #EEEEEE;
    border: 1px solid #CCCCCC;
}

span#leftSpan {
    font-family: monospaced;
    font-size: 0.75em;
    float:left;
}

span#rightSpan {
    font-family: monospaced;
    font-size: 0.75em;
    float:right;
}

</style>
<script type="text/javascript">

var canvas = null;
var leftSpan = null;
var rightSpan = null;

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

function onMouseMove(event) {
	var offsets = canvas.getBoundingClientRect();
	mouse.x = event.clientX - offsets.left;
	mouse.y = event.clientY - offsets.top;
	paintCanvas();
	leftSpan.innerHTML = "Coordinates: (" + mouse.x + ", " + mouse.y + ")";
}
function onMouseOut(event) {
	mouse.isOver = false;
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
	console.log("onClick=" + event);
}


function paintCanvas() {
	if (canvas !== undefined) {
		var t = (new Date()).getTime();
	    _paintCanvas(canvas.getContext("2d"), canvas.width, canvas.height);
		rightSpan.innerHTML = "repaint in " + ((new Date()).getTime() - t) + "ms";
	}
}

var sequence = "ACGTAGCGATCGCATACGCTGCGAGCAGGCGCAATATACTATACGCGCCACGCAACGTAGCGATCGCATACGCTGCGAGCAGGCGCAATATACTATACGCGCCACGCA";
var blocks = [];
var blockSize = 12;

function _paintCanvas(ctx, w, h) {
	ctx.clearRect(0, 0, w, h);

	_paintBackground(ctx, w, h);

	// paint sequences...
	//_paintLayers(ctx, w, h);
	var x = 0;
	var y1 = h >> 1;
	var y2 = y1 + blockSize + 1;
	for (var b = 0; b < sequence.length; b++, x += blockSize + 1) {
		ctx.fillRect(x, y1, blockSize, blockSize);
		ctx.strokeRect(x, y1, blockSize, blockSize);
		
		ctx.fillRect(x, y2, blockSize, blockSize);
        ctx.strokeRect(x, y2, blockSize, blockSize);
	}
	
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


function init() {
	canvas = document.getElementById("viewer");
	canvas.setAttribute("width", window.innerWidth - canvas.offsetLeft * 2);
	canvas.setAttribute("height", "360");

	var numberOfCircles = 100;
	var circleRadius;
    var centerX;
    var centerY;
	for (var i = 0; i < numberOfCircles; i++) {
        circleRadius = Math.floor((Math.random() * 75) + 5);
        centerX = Math.floor(Math.random() * canvas.width);
        centerY = Math.floor(Math.random() * canvas.height);
        bgCircles[i] = { x: centerX, y: centerY, radius: circleRadius };
    }

	leftSpan = document.getElementById("leftSpan");
	rightSpan = document.getElementById("rightSpan");
	
	for (var i = 0; i < sequence.length; i++)
		blocks[i] = sequence.charAt(i);
	
	paintCanvas();
}

</script>
</head>
<body onload="init();">

<!-- header>
    <h1>Some Title</h1>
    <h2>Some Subtitle</h2>
</header -->

<canvas id="viewer"
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

<!-- footer>Some Application &copy; 2014-2015, Some Developer</footer -->

</body>
</html>