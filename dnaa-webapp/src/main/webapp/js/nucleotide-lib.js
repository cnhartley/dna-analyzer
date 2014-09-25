
if (Constants === undefined)
	var Constants = {};

Constants.SEQUENCE_LOADED     = 1;
Constants.SEQUENCE_IS_LOADING = -1;
Constants.SEQUENCE_NOT_LOADED = -2;

/*
var nucleotideDNA2RNA_Map = new Array(8);
nucleotideDNA2RNA_Map["a"] = nucleotideDNA2RNA_Map["A"] = "A";
nucleotideDNA2RNA_Map["c"] = nucleotideDNA2RNA_Map["C"] = "C";
nucleotideDNA2RNA_Map["g"] = nucleotideDNA2RNA_Map["G"] = "G";
nucleotideDNA2RNA_Map["t"] = nucleotideDNA2RNA_Map["T"] = "U";

var nucleotideRNA2DNA_Map = new Array(8);
nucleotideRNA2DNA_Map["a"] = nucleotideRNA2DNA_Map["A"] = "A";
nucleotideRNA2DNA_Map["c"] = nucleotideRNA2DNA_Map["C"] = "C";
nucleotideRNA2DNA_Map["g"] = nucleotideRNA2DNA_Map["G"] = "G";
nucleotideRNA2DNA_Map["u"] = nucleotideRNA2DNA_Map["U"] = "T";
*/

/**
 * Constructor for a new instance of the NucleotideSequence with the specified
 * information.
 */
var NucleotideSequence = {
	id: -1,
	organism: "",
	length: 0,
	isDNA: false,
	blockSize: 4096,
	blockCount: 0,
	blockBuffer: [[]],
	blockStartIndex: -1,
	blockEndIndex: -1,
	createdOn: new Date(),
	createdBy: "",
	lastEditOn: new Date(),
	lastEditBy: "",
	
	nucleotideMap: [],
	nucleotideComplementMap: [],

	/**
	 * Initialize this instance of the NucleotideSequence with the specified
	 * parameters.
	 * 
	 * @param id            the Number unique identifier in the database.
	 * @param organism      the organism's name that the sequence belongs to.
	 * @param isDNA         boolean value indicating if this sequence is DNA or RNA.
	 * @param length        total number of nucleotides this sequence contains.
	 * @param blocksize     [optional] the Number length of each memory block 
	 *                      containing the portions of the full nucleotide sequence.
	 * @param blockcount    [optional] the Number of memory block required to hold 
	 *                      the full 
	 *                      nucleotide sequence.
	 */
	init: function(id, organism, isDNA, length, blockSize, blockCount) {
		var BufferBlock = {
			id: 0,  // block id from db field [dnaa.sequence_block.index]
			length: 0,  // number of nucleotides in [this.block]
			startIndex: 0,
			endIndex: 0, // == startIndex + length
			compression: [],
			charset: 'UTF-8',
			status: Constants.SEQUENCE_NOT_LOADED, // Constants.(SEQUENCE_NOT_LOADED|SEQUENCE_IS_LOADING|SEQUENCE_LOADED)
			block: {}, // after any decompression!
		};
		
		this.id = id;
		this.organism = organism;
		this.isDNA = isDNA || true;
		this.nucleotideMap = this.isDNA ?
				['A','C','G','T'] : ['A','C','G','U'];
		this.nucleotideComplementMap = this.isDNA ?
				['T','G','C','A'] : ['U','G','C','A'];
		this.length = length;
		this.blockSize = blockSize || ((1 << 16) - 1); // 16,536 indexes
		console.log("length="+ this.length + ", blockSize=" + this.blockSize);
		this.blockCount = Math.min(5, Math.ceil(this.length / this.blockSize));//blockCount || 5;
		this.blockBuffer = new Array(this.blockCount);
		for (var b = 0; b < this.blockBuffer.length; b++)
			this.blockBuffer[b] = Object.create(BufferBlock);
	},

	/**
	 * Returns an array of single characters representing the nucleotides in the 
	 * specified range of the |index| to the |index| plus the |length|.
	 * If the starting index plus the length is greater than the length of this 
	 * nucleotide sequence then the resulting array will contain the nucleotides up
	 * to the end of the sequence.
	 * 
	 * @param index  the starting index for the resulting subset of nucleotides
	 *               for this instance of the nucleotide sequence.
	 * @param length the maximum Number of nucleotides the resulting subset will
	 *               contain of this nucleotide sequence.
	 *               
	 * @returns the resulting subset of nucleotides in the specified range.
	 */
	subset: function (index, length) {
		if (index < 0 || index >= this.length || length == 0)
			return [];
		
		var endIndex = Math.min(index + length, this.length) - 1,
		    set = [];
		
		while (index <= endIndex)
			set.push(this.nucleotideAt(index++));
		
		return set;
	},

	/**
	 * Returns the single character representation for the specified nucleotide at
	 * the given sequence index. For DNA sequences this returns one of the 
	 * ['A','C','G','T'] based on the code at the specified |index|, or for RNA this
	 * returns one of the ['A','C','G','U'] based on the code.
	 * 
	 * @param index the index within the nucleotide sequence as a single character
	 *              representation.
	 * 
	 * @returns a single character representing the nucleotide at the specified 
	 *          index within this instance.
	 */
	nucleotideAt: function (index) {
		return this.nucleotideMap[this.get(index)];
	},

	/**
	 * 
	 * @param index 
	 * @returns  
	 */
	complementNucleotideAt: function (index) {
		return this.nucleotideComplementMap[this.get(index)];
	},

	/**
	 * 
	 * @param nucleotide
	 * @returns a single character representing the nucleotide at the specified 
	 *          index within this instance.
	 */
	complementFor: (function (nucleotide) {
		var comp = new Array(10);
		comp['a'] = comp['A'] = 0;
		comp['c'] = comp['C'] = 1;
		comp['g'] = comp['G'] = 2;
		comp['t'] = comp['T'] =
		comp['u'] = comp['U'] = 3;
		
		return function (nucleotide) { return this.nucleotideComplementMap[comp[nucleotide]]; };
	})(),

	/**
	 * Returns the nucleotide code [0,1,2,3] for the specified |index| within the 
	 * cached blocks of nucleotides. The nucleotide code correspond to the 2-bit 
	 * value representing the nucleotide: [b00,b01,b10,b11] in binary.
	 * 
	 * @param index 
	 * 
	 * @returns the nucleotide code at the specified index in the range from
	 *          [0 .. 3].
	 */
	get: function (index) {
		if (index < 0 ||index >= this.length)
			throw "Index out of range: [0 to " + (this.length - 1) + "] .get(" + index + ")";
		
		if (!this.isCachedIndex(index))
			this.getBlockContaining(index);
		
		var i = Math.floor(index / (this.blockSize << 2));
		var val = -1;
		
		//TODO not correct yet...
		switch (this.blockBuffer[i].status) {
		case Constants.SEQUENCE_LOADED:
			var charIndex = (index - this.blockBuffer[i].startIndex) >> 2;
			var shift = ((index - this.blockBuffer[i].startIndex) % 4) << 1;
			var char = this.blockBuffer[i].block.charCodeAt(charIndex);
			val = (char >> (6 - shift)) & 3;
			break;
			
		case Constants.SEQUENCE_IS_LOADING:
			console.error("Still loading sequence block buffer for index " + index);
			break;
			
		case Constants.SEQUENCE_NOT_LOADED:
			console.error("Sequence block buffer has no requests to load for index " + index);
			break;
			
		default:
			console.error("Unknown status for the sequence block buffer: "
					+ this.blockBuffer[0].status);
		}
		
		return val;
	},
	
	/**
	 * Returns a boolean value if the specified |index| is contained in the cached
	 * blocks for the sequence.
	 * 
	 * @param index 
	 * 
	 * @returns {Boolean}
	 */
	isCachedIndex: function (index) {
		return index >= this.blockStartIndex && index <= this.blockEndIndex;
	},

	/**
	 * 
	 * @parma block
	 */
	loadBlock: function (block, index, json) {
		if (block.id != index)
			throw "load block index id does not match the returned block index!";
		
		var seq = json.block.substr(4);
		console.log("seq=" + seq + " --> " + unpack(json.block, ['A','C','G','T']));
		
		block.length = json.length;
		block.startIndex = 0; //TODO need to update database to hold start index as well...
		block.endIndex = block.startIndex + block.length;
		block.status = Constants.SEQUENCE_LOADED;
		block.block = seq; //json.block;
		
		dumpBlockBuffer(block);
	},
	
	/**
	 * Loads the cached memory blocks that include the specified |index| containing
	 * and surrounding the main sequence block.
	 * 
	 * @param index
	 */
	getBlockContaining: function (index) {
		var blockBufferIndex = 0; // TODO get next available block buffer index...
		var blockIndex = Math.floor(index / this.blockSize) + 1;
		var request = function (block, completedFn) {
			var protocol = "http://",
			    host = "localhost",
			    port = ":" + 8080,
			    criteria = blockIndex + "," + index,
			    service = "/dnaa/services/sequences/" + criteria,
			    params = { };
		
			console.log(" --> sequence block for: '" + criteria + "' from: " + protocol + host + port + service);
			$.ajax({
				type:		'GET',
				url:		protocol + host + port + service,
				data:		params,
				dataType:	'json',
				encode:		true,
			})
			.done(function(json) {
				console.log(" <-- sequence returned: " + json);
				completedFn(block, blockIndex, json);
			});
		};
		
		// copy fields over and update!
		/*
		 * { id: 0,  // block id from db field [dnaa.sequence_block.index]
			length: 0,  // number of nucleotides in [this.block]
			startIndex: 0,
			endIndex: 0, // == startIndex + length
			compression: [],
			charset: 'UTF-8',
			status: Constants.SEQUENCE_NOT_LOADED, // Constants.(SEQUENCE_NOT_LOADED|SEQUENCE_IS_LOADING|SEQUENCE_LOADED)
			block: {},*/
		this.blockBuffer[blockBufferIndex] = {
				id: blockIndex, // block id from db field [dnaa.sequence_block.index]
				status: Constants.SEQUENCE_IS_LOADING,
				length: this.blockSize,
				charset: 'UTF-8',
				compression: null,
				block: null,
		};
		request(this.blockBuffer[blockBufferIndex], this.loadBlock);
		
		//TODO get the block from the database containing the specified index...
		//var seq = testSequence.split("");
		this.blockCount = 1;
		this.blockStartIndex = 0;
		this.blockEndIndex = 44;
		this.blockSize = 11;
		this.length = 44;
		
		console.log("loading block containing " + index + ": block=[" + this.blockBuffer[0] + "]");
	},

	/**
	 * Clears the cached memory blocks for this instance of the nucleotide sequences
	 * and resets the block indices. This should be followed by a new get(index) 
	 * call to build the memory block around that index.
	 */
	clear: function () {
		this.blockBuffer = [[]];
		this.blockCount = 0;
		this.blockSize = 0;
		this.blockEndIndex = 0;
		this.blockStartIndex = 0;
	},

	/**
	 * Inner object for drawing the nucleotide sequence as an UI element.
	 */
	ui: {
		
		fill: (function () {
			var f = new Array(10);
			f["a"] = f["A"] = '#00BB00';
			f["c"] = f["C"] = '#BB0000';
			f["g"] = f["G"] = '#BB9966';
			f["t"] = f["T"] = '#0000BB';
			f["u"] = f["U"] = '#000099';
			
			return function (char) { return f[char] ? f[char] : '#666666'; };
		})(),
		
		stroke: (function () {
			var l = new Array(10);
			l["a"] = l["A"] = '#009900';
			l["c"] = l["C"] = '#990000';
			l["g"] = l["G"] = '#996633';
			l["t"] = l["T"] = '#000099';
			l["u"] = l["U"] = '#000066';
			
			return function (char) { return l[char] ? l[char] : '#CCCCCC'; };
		})(),
		
		shape: (function () {
			var d = 1.25,
				p = new Array(10),
				u = function(x, y, s) { return [x,y, x,y+s, x+s,y+s, x+s,y, x,y]; };
			
			p["a"] = p["A"] = function(x, y, s) { return [x,y, x,y+s,   x+s/2,y+d*s, x+s,y+s,     x+s,y,          x,y]; };
			p["c"] = p["C"] = function(x, y, s) { return [x,y, x,y+d*s, x+s/4,y+d*s, x+s/4,y+s,   x+0.75*s,y+s,   x+0.75*s,y+d*s, x+s,y+d*s, x+s,y, x,y]; };
			p["g"] = p["G"] = function(x, y, s) { return [x,y, x,y+s,   x+s/4,y+s,   x+s/4,y+d*s, x+0.75*s,y+d*s, x+0.75*s,y+s,   x+s,y+s,   x+s,y, x,y]; };
			p["t"] = p["T"] = 
			p["u"] = p["U"] = function(x, y, s) { return [x,y, x,y+d*s, x+s/2,y+s,   x+s,y+d*s,   x+s,y,          x,y]; };
	
			return function (char,x,y,s) { return p[char] ? p[char](x,y,s) : u(x,y,s); };
		})(),
		
		/**
		 * Returns the color for the text of the specified character.
		 */
		text: function (char) {
			return '#FFFFFF';
		},

	},

};


///////////////////////////////////////////////////////////////////////////////


var charSize = 8; // number of bits in a character

function pack(bytes) {
    var chars = [];
    var map = new Array(10);
    map['a'] = map['A'] = 0;
    map['c'] = map['C'] = 1;
    map['g'] = map['G'] = 2;
    map['t'] = map['T'] = 
    map['u'] = map['U'] = 3;
    
    var len = bytes.length;
    chars.push((len >> 24) & 0xFF);
    chars.push((len >> 16) & 0xFF);
    chars.push((len >> 8) & 0xFF);
    chars.push(len & 0xFF);
    var b = 0;
    var bit = charSize - 2;
    for (var c = 0; c < bytes.length; c++) {
    	b |= (map[bytes.charAt(c)] << bit);
    	if (bit ==0) {
    		chars.push(b);
    		bit = charSize;
    		b = 0;
    	}
    	bit -= 2;
    }
    
    if (b != 0)
    	chars.push(b);
    	
    return String.fromCharCode.apply(null, chars);
}

function unpack(str, mapping) {
	var bitShift = 2;
    var bytes = [];
    var c = 0;
    var len = (str.charCodeAt(c++) << 24);
    len += (str.charCodeAt(c++) << 16);
    len += (str.charCodeAt(c++) << 8);
    len += str.charCodeAt(c++);
    console.log("unpack[" + str + "], length " + len);
    while (c < str.length) {
    	var bit = charSize - bitShift;
    	for (var b = str.charCodeAt(c++); len > 0 && bit >= 0; len--, bit -= bitShift) {
    		bytes.push(mapping[(b >> bit) & 3]);
    	}
    }
    console.log("    =>[" + bytes.join("") + "]");
    return bytes.join("");
}
