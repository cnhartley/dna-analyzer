/**
 * 
 */
function Viewer(canvas) {
	this.canvas = canvas; // the DOM element
	
	this.initialize();
}

Viewer.prototype.initialize = function () {
	// Register all evens to listen for on the canvas DOM element...
	this.canvas.addEventListener("resize", this.resizeCanvas, false);
	this.canvas.addEventListener("onmousedown", this.onMouseDown, false);
	this.canvas.addEventListener("onmousemove", this.onMouseMove, false);
	this.canvas.addEventListener("onmouseout", this.onMouseOut, false);
	this.canvas.addEventListener("onmouseover", this.onMouseOver, false);
	this.canvas.addEventListener("onmouseup", this.onMouseUp, false);
	this.canvas.addEventListener("onmousewheel", this.onMouseWheel, false);
	this.canvas.addEventListener("ondrag", this.onDrag, false);
	this.canvas.addEventListener("ondragend", this.onDragEnd, false);
	this.canvas.addEventListener("ondragenter", this.onDragEnter, false);
	this.canvas.addEventListener("ondragleave", this.onDragLeave, false);
	this.canvas.addEventListener("ondragover", this.onDragOver, false);
	this.canvas.addEventListener("ondragstart", this.onDragStart, false);
	this.canvas.addEventListener("onclick", this.onClick, false);
};

Viewer.prototype.repaint = function () {
	
};
