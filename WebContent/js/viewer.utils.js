
/**
 * 
 */
function Viewer(canvas) {
	if (canvas === undefined)
		throw "The canvas element is undefined";
	
	this.canvas = canvas; // the DOM element
	this.isLoaded = false;
	this.nucleotideSequence = {};
	
	// Constants...
	this.boxSize = 12; // font size is 2 less than this value.
	this.fontSize = this.boxSize - 2;
	this._boxSizeBy2 = this.boxSize >> 1;
	this._boxSizePlus1 = this.boxSize + 1;
	this._margin = 36;
	this._curve = 32;
	this._upper = 12;
	this._piByCurve = Math.PI / this._curve;
	this._upperBy2 = this._upper >> 1;
	this._marginPlusCurve = this._margin + this._curve;
	this._twoPI = 2.0 * Math.PI;
	
	this._bgCircles = [];

	this.lastRefreshTime = 0;
	
	// Internal objects...
	this.highlighter = {
		getWidth: function () { return 24; },
		getFillColor: function () { return "#EEEEFF"; },
		getLineColor: function () { return "#DDDDCC"; }
	};
	
	this.initialize();
}


Viewer.prototype.initialize = function () {
	// Register all evens to listen for on the canvas DOM element...
	var events = new ViewerEventHandler(this);
	window.addEventListener("resize", events.resizeCanvas, false);
	window.addEventListener("orientationchange", events.resizeCanvas, false);
	
	this.canvas.addEventListener("touchstart", events.onMouseDown, false);
	this.canvas.addEventListener("touchmove", events.onMouseMove, false);
	this.canvas.addEventListener("touchleave", events.onMouseOut, false);
	this.canvas.addEventListener("touchenter", events.onMouseOver, false);
	this.canvas.addEventListener("touchcancel", events.onMouseUp, false);
	this.canvas.addEventListener("touchend", events.onMouseUp, false);
	this.canvas.addEventListener("mousedown", events.onMouseDown, false);
	this.canvas.addEventListener("mousemove", events.onMouseMove, false);
	this.canvas.addEventListener("mouseout", events.onMouseOut, false);
	this.canvas.addEventListener("mouseover", events.onMouseOver, false);
	this.canvas.addEventListener("mouseup", events.onMouseUp, false);
	this.canvas.addEventListener("mousewheel", events.onMouseWheel, false);
	this.canvas.addEventListener("drag", events.onDrag, false);
	this.canvas.addEventListener("dragend", events.onDragEnd, false);
	this.canvas.addEventListener("dragenter", events.onDragEnter, false);
	this.canvas.addEventListener("dragleave", events.onDragLeave, false);
	this.canvas.addEventListener("dragover", events.onDragOver, false);
	this.canvas.addEventListener("dragstart", events.onDragStart, false);
	this.canvas.addEventListener("click", events.onClick, false);
	
	this.events = events;
	this.events.resizeCanvas();
	
	var numberOfCircles = 100;
	var circleRadius;
    var centerX;
    var centerY;
	for (var i = 0; i < numberOfCircles; i++) {
        circleRadius = Math.floor((Math.random() * 70) + 5);
        centerX = Math.floor(Math.random() * 4096);
        centerY = Math.floor(Math.random() * this.canvas.height);
        this._bgCircles[i] = { x: centerX, y: centerY, radius: circleRadius, };
    }
	
	this.isLoaded = true;
	this.repaint();
};


Viewer.prototype.repaint = function () {
	if (this.canvas !== undefined && this.isLoaded) {
		var t = (new Date()).getTime();
	    this._paintCanvas(this.canvas.getContext("2d"), this.canvas.width, this.canvas.height);
		//rightSpan.innerHTML = "repaint in " + ((new Date()).getTime() - t) + "ms";
	    this.lastRefreshTime = (new Date()).getTime() - t;
	}
};

Viewer.prototype._paintCanvas = function(ctx, w, h) {
	ctx.clearRect(0, 0, w, h);
	ctx.font = this.fontSize + "px Verdana,monospaced";
    
	this._paintBackground(ctx, w, h);
	this._paintSequencesAndFrame(ctx, w, h);

   	// paint the highlighter after everything else is painted.
   	this._paintMouseHighlighter(ctx, w, h);
};

Viewer.prototype._paintBackground = function(ctx, w, h) {
	var colors = [ "#D8E0EF", "#F0F8FF", "#D8E0EF" ];
	ctx.fillStyle = getGradientPaint(ctx, 0, 0, 0, h, colors);
	ctx.fillRect(0, 0, w, h);
    
    ctx.strokeStyle = "#CCCCCC";
    ctx.fillStyle = "#DDDDDD";
    ctx.globalAlpha = 0.5;
    
    for (var i = 0; i < this._bgCircles.length; i++) {
    	ctx.beginPath();
        ctx.arc(this._bgCircles[i].x, this._bgCircles[i].y, this._bgCircles[i].radius, 0 , this._twoPI);
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
};

Viewer.prototype._paintMouseHighlighter = function(ctx, w, h) {
	if (this.mouse === undefined || this.highlighter === undefined || !this.mouse.isOver)
		return;
	
	var hw = this.highlighter.getWidth();
	var x = this.mouse.x - (hw >> 1);
    
	if (x < 0)
		x = 0;
	else if (x + hw > w)
		x = w - hw;
	
	ctx.globalAlpha = 0.5;
    
	ctx.fillStyle = this.mouse.isDragging ? "#CCCCEE" : this.highlighter.getFillColor();
	ctx.fillRect(x, 0, hw, h);
	
	ctx.strokeStyle = this.highlighter.getLineColor();
	ctx.strokeRect(x, 0, hw, h);
	
	ctx.globalAlpha = 1.0;
};

Viewer.prototype._paintSequencesAndFrame = function(ctx, w, h) {
    var x = 0;
    var y = (h >> 1) - this._boxSizeBy2;
    var currentSeq = seqBlocks[seqBlockIndex] || "";
    var seqWidth = this._boxSizePlus1 * currentSeq.length;
    
    if (seqBlockIndex == 0)
    	x = 0;
    else if (seqBlockIndex == seqBlocks.length - 1)
    	x = w - seqWidth;
    else
    	x = (w - seqWidth) >> 1;
    
   	// check for new drag offset...
    if (this.mouse !== undefined && this.mouse.isDragging)
    	x += this.mouse.x - this.mouse.dragFrom.x;
   	
    if (seqId > -1)
        this._paintSequence(ctx, x, y, currentSeq);
    
    if (x > 0 && seqBlockIndex > 0)
    	this._paintSequence(ctx, x - this._boxSizePlus1 * seqBlocks[seqBlockIndex - 1].length, y, seqBlocks[seqBlockIndex - 1] || []);
    
    if (x + seqWidth < w && seqBlockIndex < seqBlocks.length - 1)
    	this._paintSequence(ctx, x + seqWidth, y, seqBlocks[seqBlockIndex + 1] || []);	
};

Viewer.prototype._paintSequence = function(ctx, x, y, seq) {
	var complement = new Array(10);
	complement['A'] = complement['a'] = 'T';
	complement['C'] = complement['c'] = 'G';
	complement['G'] = complement['g'] = 'C';
	complement['T'] = complement['t'] = 'A';
	var x1 = x;
	
	// block separator
    this._paintSeparator(ctx, x1, y);
	
	for (var i = 0; i < seq.length; i++) {
		this._paintNucleotide(ctx, seq[i].toUpperCase(), x1, y - this._senseCoords(x1));
		this._paintNucleotide(ctx, complement[seq[i]], x1, y + this._antisenseCoords(x1));
		x1 += this._boxSizePlus1;
	}
	
	// block separator
	this._paintSeparator(ctx, x1, y);
};

Viewer.prototype._paintNucleotide = function(ctx, nucleotide, x, y) {
	if (x <= -this.boxSize || x > this.canvas.width)
		return;
	
	this._paintObject(ctx, x, y, nucleotidePath[nucleotide] || blankPath);
	// draw shape...
    ctx.fillStyle = nucleotideFill[nucleotide] || '#666666';
    ctx.fill();
    ctx.strokeStyle = nucleotideLine[nucleotide] || '#CCCCCC';
    ctx.stroke();
    
    // draw text...
    ctx.fillStyle = '#FFFFFF';
    ctx.fillText(nucleotide, x + 2, y + this.boxSize - 2);
};

Viewer.prototype._paintObject = function(ctx, x, y, pathFn) {
	var path = pathFn(x, y, this.boxSize);
	var i = 0;
	
	// rotate by the angle
	var originX = x + this._boxSizeBy2;
	var originY = y + this._boxSizeBy2;
	var angle = 0;
	path = _rotatePathAbout(path, angle, originX, originY);
	
	ctx.beginPath();
	ctx.moveTo(path[i], path[i+1]);
	do {
        i += 2;
		ctx.lineTo(path[i], path[i+1]);
	} while (i + 2 < path.length);
};

Viewer.prototype._paintSeparator = function(ctx, x, y) {
	if (x <= -this.boxSize || x > this.canvas.width)
        return;
    
	ctx.beginPath();
    ctx.moveTo(x, y - this.boxSize);
    ctx.lineTo(x, y + this.boxSize + this.boxSize);
    ctx.strokeStyle = '#555555';
    ctx.stroke();
};

Viewer.prototype._senseCoords = function(x) { //returns y-coordinate along the path.
	x += this._boxSizeBy2;
	var width = this.canvas.width;
	var y = 0;
	
	if (x < this._margin || x > width - this._margin)
		y = 0;
	else if (x >= this._margin && x <= this._marginPlusCurve)
		y = this._upperBy2 * (1 - Math.cos((x - this._margin) * this._piByCurve));
	else if (width >= x + this._margin && width <= x + this._marginPlusCurve)
		y = this._upperBy2 * (1 + Math.cos((x - width + this._marginPlusCurve) * this._piByCurve));
	else
		y = this._upper;
	
	return y;
};

Viewer.prototype._antisenseCoords = function(x) { //returns y-coordinate along the path.
	x -= this._boxSizeBy2;
	var width = this.canvas.width;
	var y = 0;
	
	if (x < this._margin || x > width - this._margin)
		y = 0;
	else if (x >= this._margin && x <= this._marginPlusCurve)
		y = this._upperBy2 * (1 - Math.cos((x - this._margin) * this._piByCurve));
	else if (width >= x + this._margin && width <= x + this._marginPlusCurve)
		y = this._upperBy2 * (1 + Math.cos((x - width + this._marginPlusCurve) * this._piByCurve));
	else
		y = this._upper;
	
	return y;
};

/* Event handlers */

function ViewerEventHandler(viewer) {
	this.viewer = viewer;
	
	this.mouseX = 0;
	this.mouseY = 0;
	this.isOver = false;
	this.isDragging = false;
	this.dragFrom = { x:0, y:0 };
};

ViewerEventHandler.prototype.onMouseMove = function(event) {
	var offsets = this.viewer.canvas.getBoundingClientRect();
	this.mouseX = event.clientX - offsets.left;
	this.mouseY = event.clientY - offsets.top;
	this.viewer.repaint();
	//leftSpan.innerHTML = "Coordinates: (" + mouse.x + ", " + mouse.y + ")";
};

ViewerEventHandler.prototype.onMouseOut = function(event) {
	this.isOver = false;
	this.isDragging = false;
	this.viewer.repaint();
};

ViewerEventHandler.prototype.onMouseOver = function(event) {
	this.isOver = true;
};

ViewerEventHandler.prototype.onMouseDown = function(event) {
	var offsets = this.canvas.getBoundingClientRect();
    this.isDragging = true;
    this.dragFrom = { x: event.clientX - offsets.left, y: event.clientY - offsets.top, };
    this.viewer.repaint();
    this.viewer.canvas.style.cursor = "ew-resize";
};

ViewerEventHandler.prototype.onMouseUp = function(event) {
	this.isDragging = false;
	this.viewer.repaint();
	this.viewer.canvas.style.cursor = "pointer";
};

ViewerEventHandler.prototype.onMouseWheel = function(event) {
	console.log("onMouseWheel=" + event);
};

ViewerEventHandler.prototype.onDrag = function(event) {
	;//console.log("onDrag=" + event);
};

ViewerEventHandler.prototype.onDragEnd = function(event) {
	;//console.log("onDragEnd=" + event);
};

ViewerEventHandler.prototype.onDragEnter = function(event) {
	;//console.log("onDragEnter=" + event);
};

ViewerEventHandler.prototype.onDragLeave = function(event) {
	;//console.log("onDragLeave=" + event);
};

ViewerEventHandler.prototype.onDragOver = function(event) {
	;//console.log("onDragOver=" + event);
};

ViewerEventHandler.prototype.onDragStart = function(event) {
	;//console.log("onDragStart=" + event);
};

ViewerEventHandler.prototype.onClick = function(event) {
	var offsets = this.viewer.canvas.getBoundingClientRect();
	var x = event.clientX - offsets.left;
	var y = event.clientY - offsets.top;
	//TODO determine what element the mouse is over to display the appropriate info within the tooltip.
	this.viewer.setTooltip("New Title - (" + x + "," + x + ")",
			  "<em>" + this.getElementAt(x, y) + "</em>"
			+ "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
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
};

ViewerEventHandler.prototype.getElementAt = function(x, y) {
	//TODO process which frame or stand the position is at...
	var boxIndex = Math.floor(x / this.viewer._boxSizePlus1);
	return "boxIndex=" + boxIndex;
};

ViewerEventHandler.prototype.resizeCanvas = function() {
	var w = window.innerWidth - this.viewer.canvas.offsetLeft * 2;
	var h = 144;
	
	this.viewer.canvas.setAttribute("width", w);
	this.viewer.canvas.setAttribute("height", h);
	
	//document.getElementById("scrollableViewerContainer").setAttribute("height", h);
	//document.getElementById("scrollLeftButton").setAttribute("height", h);
	//document.getElementById("scrollRightButton").setAttribute("height", h);
	this.viewer.repaint();
};


var _piBy180 = Math.PI / 180;

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
};

function getGradientPaint(ctx, x1, y1, x2, y2, colors) {
	colors = colors || ["#CCCCCC"];
	var step = 1 / (colors.length - 1);
    var gradientPaint = ctx.createLinearGradient(x1, y1, x2, y2);
    for (var i = 0, pos = 0.0; i < colors.length; i++, pos += step)
        gradientPaint.addColorStop(pos, colors[i]);
    return gradientPaint;
};
