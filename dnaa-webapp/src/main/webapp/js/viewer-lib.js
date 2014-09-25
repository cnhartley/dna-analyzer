
var twoPI = 2.0 * Math.PI;


var mouse = {
    x: 0,
    y: 0,
    isOver: false,
    isDragging: false,
    dragFrom: { x:0, y:0 }
};

var highlighter = {
    width: 24,
    style: {
    	fillColor: '#EEEEFF',
    	lineColor: '#DDDDCC'
    }
};

/* map of [HTMLCanvasElement, Viewer] */
var viewers = new Array();

var Viewer = {
	/* DOM HTMLCanvasElement */
	canvas: {},
	
	/* Reference to the NucleotideSequence this Viewer is rendering. */
	sequence: {
		ui:						{ },
		nucleotideAt:			function (index) { return '?'; },
		complementNucleotideAt:	function (index) { return '?'; },
		complementFor:			function (index) { return '?'; },
		get:					function (index) { return NaN; },
		clear:					function () { },
		subSet:					function (startIndex, length) { return []; },
	},
	
	background: {
		init:	function () { },
		paint:	function (ctx, w, h) { },
		shift:	function (dx,dy) { },
	},
	
	init: function (canvasElementId, width, height) {
		var c = document.getElementById(canvasElementId);
	    if (c !== undefined && c != null) {
	    	width = width || window.innerWidth - c.offsetLeft * 2,
		    height = height || 360;
	    	c.setAttribute('width', width);
	        c.setAttribute('height', height);
	        
	    	$(window).on({
	    		resize:				EventHandler.onResize,
	    		orientationchange:	EventHandler.onResize
	    	});
	    	
	    	$(c).on({
	    		click:			EventHandler.onClick,
	    		touchstart:		EventHandler.onMouseDown,
	    		touchmove:		EventHandler.onMouseMove,
	    		touchleave:		EventHandler.onMouseOut,
	    		touchenter:		EventHandler.onMouseOver,
	    		touchcancel:	EventHandler.onMouseUp,
	    		touchend:		EventHandler.onMouseUp,
	    		mousedown:		EventHandler.onMouseDown,
	    		mousemove:		EventHandler.onMouseMove,
	    		mouseout:		EventHandler.onMouseOut,
	    		mouseover:		EventHandler.onMouseOver,
	    		mouseup:		EventHandler.onMouseUp,
	    		mousewheel:		EventHandler.onMouseWheel,
	    		DOMMouseScroll:	EventHandler.onMouseWheel,
	    		drag:			EventHandler.onDrag,
	    		dragend:		EventHandler.onDragEnd,
	    		dragenter:		EventHandler.onDragEnter,
	    		dragleave:		EventHandler.onDragLeave,
	    		dragover:		EventHandler.onDragOver,
	    		dragstart:		EventHandler.onDragStart
	    	});
	    	
	    	this.canvas = c;
	    	viewers[c] = this;
	    }
	    else {
	    	this.canvas = null;
	    	throw "invalid canvas id: " + canvasElementId;
	    }
	},

	repaint: function () {
		var renderer = {
			src: {},
			paint: function (ctx, w, h) {
				this.src = viewers[c];
				
				if (this.src === undefined)
					return;
				
				//TODO double buffer image while drawing...
				
				this._clear(ctx, w, h);
				this._paintBackground(ctx, w, h);
				this._paintSequences(ctx, w, h);
				this._paintHighlighter(ctx, w, h);
			},
			_clear: function (ctx, w, h) {
				ctx.clearRect(0, 0, w, h);
			},
			_paintBackground: function (ctx, w, h) {
				var bg = this.src.background;
				
				if (bg && bg.paint)
					bg.paint(ctx, w, h);
				else
					console.warn("no background to render: " + bg);
			},
			_paintSequences: function (ctx, w, h) {
				var _paintNucleotide = function (char, ctx, x, y, rotate) {
					var shape = seq.ui.shape(char, x, y, blockSize);
					var i = 0;
					// rotate by the angle
					var originX = x + blockSizeBy2;
					var originY = y + blockSizeBy2;
					var angle = rotate || 0;
					shape = PaintUtils.rotatePathAbout(shape, angle, originX, originY);
					
					ctx.beginPath();
					ctx.moveTo(shape[i], shape[i+1]);
					do {
				        i += 2;
						ctx.lineTo(shape[i], shape[i+1]);
					} while (i + 2 < shape.length);
					ctx.closePath();
					
					ctx.fillStyle = seq.ui.fill(char);
				    ctx.fill();
				    ctx.strokeStyle = seq.ui.stroke(char);
				    ctx.stroke();
				    
				    // draw text...
				    ctx.fillStyle = seq.ui.text(char);
				    ctx.textAlign = "center";
				    ctx.fillText(char, x + blockSizeBy2, y + blockSize - 2);
				};
				
				var _paintAminoAcid = function (chars, ctx, x, y) {
					//console.log("AminoAcid=" + AminoAcidLib.get(chars));
					ctx.beginPath();
					ctx.rect(x, y, blockSizeX3, blockSize);
					ctx.closePath();
					
					ctx.fillStyle = '#AAAAAA';//seq.ui.fill(char);
				    ctx.fill();
				    ctx.strokeStyle = '#666666';//seq.ui.stroke(char);
				    ctx.stroke();
				    
				    // draw text...
				    ctx.fillStyle = '#DDDDDD';//seq.ui.text(char);
				    ctx.textAlign = "center";
				    ctx.fillText(chars, x + (blockSizeX3 >> 1), y + blockSize - 2);
				};
				
				var startIndex = 0,
				    seq = this.src.sequence,
				    sub = [],
				    b = 0;
				    x = 0,
				    yd = 0, y1d = 0, y2d = 0;
			    var y1 = (h >> 1) - blockSize * 1.25;
			    var y2 = y1 + blockSize * 1.25 + 1;
				
				if (seq && seq.ui) {
					ctx.font = fontSize + "px monospaced";
				    //TODO paint the sequence...
					
					if (mouse.isDragging) {
				        x -= mouse.dragFrom.x - mouse.x;
					}

					sub = seq.subset(startIndex, Math.ceil(w / blockSizePlus1) + 3);
					if (sub != null && sub.length > 0) {
						for (var i = 0, x1 = x/* - blockSizePlus1*/; i < sub.length; i++, x1 += blockSizePlus1) {
							yd = yDisplacement(x1, w, h);
				            y1d = y1 - yd;
				            y2d = y2 + yd;
				            
				            
							//TODO mRNA of sense strand
							
							//TODO sense strand of DNA
							_paintNucleotide(sub[i], ctx, x1, y1d);
							
							//TODO anti-sense strand of DNA
							_paintNucleotide(seq.complementFor(sub[i]), ctx, x1, y2d, 180);
							
							//TODO mRNA of anti-sense strand
							
							var aa1 = sub[i] + sub[i+1] + sub[i+2],
							    aa2 = seq.complementFor(sub[i])
							        + seq.complementFor(sub[i+1])
							        + seq.complementFor(sub[i+2]);
				            
							//TODO frame 1 & 4
				            if (i % 3 == 0 && i + 2 < sub.length) {
				            	_paintAminoAcid(aa1, ctx, x1, blockSizeX2);
				            	_paintAminoAcid(aa2, ctx, x1, h - blockSizeX2);
				            }
				            
							//TODO frame 2 & 5
				            if (i % 3 == 1 && i > 0 && i + 1 < sub.length) {
				            	_paintAminoAcid(aa1, ctx, x1, blockSizeX4);
				            	_paintAminoAcid(aa2, ctx, x1, h - blockSizeX4);
				            }
				            
							//TODO frame 3 & 6
				            if (i % 3 == 2 && i > 1 && i + 2 < sub.length) {
				            	_paintAminoAcid(aa1, ctx, x1, blockSizeX6);
				            	_paintAminoAcid(aa2, ctx, x1, h - blockSizeX6);
				            }

							// Paint the indexes of each DNA strand
							b = startIndex + i;
							ctx.fillStyle = '#666666';
							if (b % 9 == 0) {
					            ctx.fillText(b + "", x1, y1d - 2);
					            ctx.fillText((seq.length - b) + "", x1, y2d + blockSize + 2 + fontSize);
					        }
						}
					}
				}
				else
					console.warn("no sequence to render: " + seq);
			},
			_paintHighlighter: function (ctx, w, h) {
				if (mouse === undefined || highlighter === undefined || !mouse.isOver)
			        return;
				
				var hw = highlighter.width;
			    var x = mouse.x - (hw >> 1);
			    
			    if (x < 0)
			        x = 0;
			    else if (x + hw > w)
			        x = w - hw;
			    
			    ctx.globalAlpha = 0.5;
			    
			    ctx.fillStyle = mouse.isDragging ? "#CCCCEE" : highlighter.style.fillColor;
			    ctx.fillRect(x, 0, hw, h);
			    
			    ctx.strokeStyle = highlighter.style.lineColor;
			    ctx.strokeRect(x, 0, hw, h);
			    
			    ctx.globalAlpha = 1.0;
			}
		};
		
		var t, c = this.canvas;
		if (c != null) {
	        t = (new Date()).getTime();
	        renderer.paint(c.getContext("2d"), c.width, c.height);
	        rightSpan.innerHTML = "repaint in " + ((new Date()).getTime() - t) + "ms";
	    }
	    else
	    	console.error("canvas could not be found using '" + c + "'!");
	},
	
	scrollBy: function (dx, dy) {
		dx = dx || 0.0;
		dy = dy || 0.0;
		
		if (dx == 0 && dy == 0)
			return;
		
		//TODO adjust the sequence by the scroll amounts...
		
		// shift the background by the scroll amounts...
		this.background.shift(dx, dy);
		this.repaint();
	},
	
	getElementAt: function (x, y) {
		var boxIndex = Math.floor(x / blockSizePlus1);
		return "boxIndex=" + boxIndex;
	},
};

function getRelativeCursorPosition(event) {
	var x, y;
	if (event.pageX != undefined && event.pageY != undefined) {
		x = event.pageX;
		y = event.pageY;
	}
	else {
		x = event.clientX + document.body.scrollLeft
				+ document.documentElement.scrollLeft;
		y = event.clientY + document.body.scrollTop
				+ document.documentElement.scrollTop;
	}
	
	return {
		'x': x - event.target.offsetLeft,
		'y': y - event.target.offsetTop
	};
};

var EventHandler = {
	onResize: function (event) {
		var w = 0,
			c = {},
			owns = Object.prototype.hasOwnProperty;

    	for (c in viewers) {
			if (!owns.call(viewers, c))
				continue;
			
			c = viewers[c].canvas;
			w = window.innerWidth - c.offsetLeft * 2 - 1;
			//h = c.height;
			
			c.setAttribute("width", w);
			//c.setAttribute("height", h);
			
			viewers[c].background.rebuffer = true;
			viewers[c].repaint();
		}
	},
	
	onMouseMove: function (event) {
	    var cursor = getRelativeCursorPosition(event),
	        innerHTML = "";
	    
	    mouse.x = cursor.x;
	    mouse.y = cursor.y;
	    
	    innerHTML = "Coordinates: (" + mouse.x + ", " + mouse.y + ")";
	    if (mouse.isDragging) {
	    	viewers[event.target].background.rebuffer = true;
	        innerHTML += " from (" + mouse.dragFrom.x + ", " + mouse.dragFrom.y + ")";
	    }
	    
	    viewers[event.target].repaint();
	    leftSpan.innerHTML = innerHTML;
	},
	onMouseOut: function (event) {
	    mouse.isOver = false;
	    viewers[event.target].repaint();
	},
	onMouseOver: function (event) {
	    mouse.isOver = true;
	},
	onMouseDown: function (event) {
	    mouse.isDragging = true;
	    mouse.dragFrom = getRelativeCursorPosition(event);
	    viewers[event.target].repaint();
	    event.target.style.cursor = "ew-resize";
	},
	onMouseUp: function (event) {
		var dx = mouse.x - mouse.dragFrom.x,
			dy = mouse.y - mouse.dragFrom.y;
		
	    mouse.isDragging = false;
	    event.target.style.cursor = "pointer";
	    
	    viewers[event.target].background.shift(dx, dy);
	    
	    sequenceLeftIndex -= Math.floor(dx / blockSizePlus1);
	    if (sequenceLeftIndex < 0) {
	    	viewers[event.target].scrollBy(sequenceLeftIndex * blockSizePlus1, 0);
	    	//TODO replace with exact distance to return by; -mouse.x, 0);
	    	sequenceLeftIndex = 0;
	    }
	    viewers[event.target].repaint();
	},
	onMouseWheel: function (event) {
		var dx = 0.0,
			e = window.event || event;

		e.preventDefault();
	    dx = (e.wheelDelta || e.detail) * wheelScrollFactor;
	    viewers[e.target].scrollBy(dx, 0);
	    return true;
	},
	onDrag: function (event) {
	    //console.log("onDrag=" + event);
	},
	onDragEnd: function (event) {
	    //console.log("onDragEnd=" + event);
	},
	onDragEnter: function (event) {
	    //console.log("onDragEnter=" + event);
	},
	onDragLeave: function (event) {
	    //console.log("onDragLeave=" + event);
	},
	onDragOver: function (event) {
	    //console.log("onDragOver=" + event);
	},
	onDragStart: function (event) {
		//console.log("onDragStart=" + event);
	},
	onClick: function (event) {
		var pt = getRelativeCursorPosition(event);
		event.preventDefault();
		console.log("onClick=" + event + ", window.event=" + window.event + ", pt=" + pt);
		//TODO determine what element the mouse is over to display the appropriate info within the tooltip.
		
		$('#viewerTooltipTitle').html("New Title - (" + pt.x + "," + pt.y + ")");
    	$('#viewerTooltipContent').html(
    		  "<em>" + viewers[event.target].getElementAt(pt.x, pt.y) + "</em>"
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
    	$('#viewerTooltip').fadeIn("fast");
	},
};


var sequence = "ACGTAGCGATCGCATACGCTGCGAGCAGGCGCAATATACTATACGCGCCACGCAACGTAGCGATCGCATACGCTGCGAGCAGGCGCAATATACTATACGCGCCACGCAACGTAGCGATCGCATACGCTGCGAGCAGGCGCAATATACTATACGCGCCACGCAACGTAGCGATCGCATACGCTGCGAGCAGGCGCAATATACTATACGCGCCACGCAACGTAGCGATCGCATACGCTGCGAGCAGGCGCAATATACTATACGCGCCACGCAACGTAGCGATCGCATACGCTGCGAGCAGGCGCAATATACTATACGCGCCACGCA";
var sequenceLeftIndex = 0;

var blockSize = 12;
var blockSizePlus1 = blockSize + 1.0;
var blockSizeX2 = blockSizePlus1 << 1;
var blockSizeX3 = blockSize + blockSizeX2;
var blockSizeX4 = blockSizeX2 << 1;
var blockSizeX6 = blockSizeX2 + blockSizeX4;
var blockSizeX8 = blockSizeX4 << 1;
var blockSizeBy2 = blockSize >> 1;

var fontSize = blockSize - 2;
var wheelScrollFactor = blockSize / 120.0;

var aCoeff = 1.0 / (16.0 * blockSizePlus1 * blockSizePlus1);
var cCoeff = -0.75;

function yDisplacement(x, w, h) {
    if ((x <= blockSizeX4) || (w <= x + blockSizeX4))
        return 0.0;
    else if (x < blockSizeX8) {
        x -= blockSizeX6;
        return blockSizePlus1 - aCoeff * x*x*x - cCoeff * x;
    }
    else if (w < x + blockSizeX8) {
        x += blockSizeX6 - w;
        return blockSizePlus1 + aCoeff * x*x*x + cCoeff * x;
    }
    else
        return blockSizeX2;
}


var FloatingBubblesBackground = {
    numberOfCircles: 400,
    bgCircles: [],
    gradient: { h:0, colors:[ "#D8E0EF", "#F0F8FF", "#D8E0EF" ], paint:{} },
    buffered: document.createElement('canvas'),
    rebuffer: true,
    
    init: function (canvasWith, canvasHeight) {
        var radius,
            centerX,
            centerY,
            i = 0,
            w = canvasWith || 2048, //this.canvas.width,
            h = canvasHeight || 480; //this.canvas.height;
        
        while (i < this.numberOfCircles) {
            radius = Math.floor((Math.random() * 75) + 5);
            centerX = Math.floor(Math.random() * w * 5) - w * 2;
            centerY = Math.floor(Math.random() * h * 3) - h;
            this.bgCircles[i++] = {
                    x: centerX,
                    y: centerY,
                    radius: radius,
                    diameter: radius + radius,
                    volumeRatio: radius / 80.0,
            };
        }
    },
    
    paint: function (ctx, w, h) {
    	if (this.rebuffer) {
    		this.buffered.width = w;
    		this.buffered.height = h;
    		
	        var ctxb = this.buffered.getContext('2d'),
	            c = {},
	            dx = 0, dy = 0,
	            x = 0, y = 0,
	            i = 0;
	        
	        if (this.gradient.h != h) {
	        	this.gradient.h = h;
	        	this.gradient.paint =
	        		PaintUtils.getGradientPaint(ctxb, 0, 0, 0, h, this.gradient.colors);
	        }
	        ctxb.fillStyle = this.gradient.paint;
	        ctxb.fillRect(0, 0, w, h);
	        
	        ctxb.fillStyle = "#DDDDDD";
	        ctxb.strokeStyle = "#CCCCCC";
	        ctxb.globalAlpha = 0.35;
	        
	        if (mouse.isDragging) {
	            dx = mouse.x - mouse.dragFrom.x;
	            dy = mouse.y - mouse.dragFrom.y;
	        }
	        
	        while (i < this.numberOfCircles) {
	            c = this.bgCircles[i++];
	            x = c.x + dx * c.volumeRatio;
	            y = c.y + dy * c.volumeRatio;
	            
	            if ((x + c.diameter >= 0) && (x <= w)
	                    && (y + c.diameter >= 0) && (y <= h))
	            {
	            	ctxb.beginPath();
	            	ctxb.arc(x, y, c.radius, 0, twoPI);
	            	ctxb.fill();
	            	ctxb.stroke();
	            }
	        }
	
	        ctxb.globalAlpha = 1.0;
	        PaintUtils.paintDiagnals(ctxb, w, h);
        	this.rebuffer = false;
    	}
    	
    	ctx.drawImage(this.buffered, 0, 0);
    },
    
    /**
     * Shift the background by the specified deltas (dx, dy).
     */
    shift: function (dx, dy) {
    	if (dx == 0.0 && dy == 0.0)
    		return;
    	
    	var i = 0, c = {};
    	while (i < this.bgCircles.length) {
        	c = this.bgCircles[i++];
    	    c.x += dx * c.volumeRatio;
            c.y += dy * c.volumeRatio;
        }
        
        this.rebuffer = true;
    },
    
};


var PaintUtils = {
	_piBy180: Math.PI / 180.0,

    getGradientPaint: function (ctx, x1, y1, x2, y2, colors) {
        colors = colors || ["#CCCCCC"];
        var step = colors.length > 1 ? 1.0 / (colors.length - 1) : 1.0;
        var gradientPaint = ctx.createLinearGradient(x1, y1, x2, y2);
        for (var i = 0, pos = 0.0; i < colors.length; pos += step)
            gradientPaint.addColorStop(pos, colors[i++]);
        return gradientPaint;
    },
    
    paintDiagnals: function (ctx, w, h) {
        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.lineTo(w, h);
        
        ctx.moveTo(0, h);
        ctx.lineTo(w, 0);
        ctx.closePath();
        ctx.stroke();
    },
    
    rotatePathAbout: function (path, degrees, originX, originY) {
    	var rotatedPath = new Array(path.length);
    	var rad = degrees * this._piBy180;
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
};

