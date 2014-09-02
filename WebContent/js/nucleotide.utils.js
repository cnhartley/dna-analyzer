/**
 * 
 */
var d = 1.25;

var nucleotideFill = new Array();
nucleotideFill["a"] = nucleotideFill["A"] = '#00BB00';
nucleotideFill["c"] = nucleotideFill["C"] = '#BB0000';
nucleotideFill["g"] = nucleotideFill["G"] = '#BB9966';
nucleotideFill["t"] = nucleotideFill["T"] = '#0000BB';
nucleotideFill["u"] = nucleotideFill["U"] = '#000099';

var nucleotideLine = new Array();
nucleotideLine["a"] = nucleotideLine["A"] = '#009900';
nucleotideLine["c"] = nucleotideLine["C"] = '#990000';
nucleotideLine["g"] = nucleotideLine["G"] = '#996633';
nucleotideLine["t"] = nucleotideLine["T"] = '#000099';
nucleotideLine["u"] = nucleotideLine["U"] = '#000066';

var nucleotidePath = new Array();
nucleotidePath["a"] = nucleotidePath["A"] = function(x, y, s) { return [x,y, x,y+s,   x+s/2,y+d*s, x+s,y+s,     x+s,y,          x,y]; };
nucleotidePath["c"] = nucleotidePath["C"] = function(x, y, s) { return [x,y, x,y+d*s, x+s/4,y+d*s, x+s/4,y+s,   x+0.75*s,y+s,   x+0.75*s,y+d*s, x+s,y+d*s, x+s,y, x,y]; };
nucleotidePath["g"] = nucleotidePath["G"] = function(x, y, s) { return [x,y, x,y+s,   x+s/4,y+s,   x+s/4,y+d*s, x+0.75*s,y+d*s, x+0.75*s,y+s,   x+s,y+s,   x+s,y, x,y]; };
nucleotidePath["t"] = nucleotidePath["T"] = 
nucleotidePath["u"] = nucleotidePath["U"] = function(x, y, s) { return [x,y, x,y+d*s, x+s/2,y+s,   x+s,y+d*s,   x+s,y,          x,y]; };

var blankPath = function(x, y, s) { return [x,y, x,y+s, x+s,y+s, x+s,y, x,y]; };

var nucleotideDNA2RNA_Map = new Array();
nucleotideDNA2RNA_Map["a"] = nucleotideDNA2RNA_Map["A"] = "A";
nucleotideDNA2RNA_Map["c"] = nucleotideDNA2RNA_Map["C"] = "C";
nucleotideDNA2RNA_Map["g"] = nucleotideDNA2RNA_Map["G"] = "G";
nucleotideDNA2RNA_Map["t"] = nucleotideDNA2RNA_Map["T"] = "U";

var nucleotideRNA2DNA_Map = new Array();
nucleotideRNA2DNA_Map["a"] = nucleotideRNA2DNA_Map["A"] = "A";
nucleotideRNA2DNA_Map["c"] = nucleotideRNA2DNA_Map["C"] = "C";
nucleotideRNA2DNA_Map["g"] = nucleotideRNA2DNA_Map["G"] = "G";
nucleotideRNA2DNA_Map["u"] = nucleotideRNA2DNA_Map["U"] = "T";


/**
 * Constructor for a new instance of the NucleotideSequence with the specified
 * information.
 * 
 * @param id  the Number unique identifier in the database.
 * @param name  the organism's name that the sequence belongs to.
 * @param blocksize  the Number length of each memory block containing the 
 *                   portions of the full nucleotide sequence.
 * @param blockcount  the Number of memory block required to hold the full 
 *                    nucleotide sequence.
 */
function NucleotideSequence(id, name, blocksize, blockcount) {
	this.id = id;
	this.name = name;
	this.length = 0;
	this.isDNA = true;
	this.blocksize = blocksize;
	this.blockcount = blockcount;
	this.blockbuffer = [];
	this.blockStartIndex = 0;
	this.blockEndIndex = 0;
	this.createdOn = new Date();
	this.createdBy = "";
	this.lastEditOn = new Date();
	this.lastEditBy = "";
	
	this.nucleotideMap = this.isDNA ?
			['A','C','G','T'] : ['A','C','G','U'];
	
	this.nucleotideComplementMap = this.isDNA ?
			['T','G','C','A'] : ['U','G','C','A'];
};

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
NucleotideSequence.prototype.nucleotideAt = function (index) {
	return nucleotideMap[get(index)];
};

/**
 * 
 * @param index 
 * @returns  
 */
NucleotideSequence.prototype.complementNucleotideAt = function (index) {
	return nucleotideComplementMap[get(index)];
};

/**
 * 
 * @param nucleotide
 * @returns a single character representing the nucleotide at the specified 
 *          index within this instance.
 */
NucleotideSequence.prototype.complementFor = function (nucleotide) {
	return nucleotideComplementMap[nucleotide];
};

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
NucleotideSequence.prototype.get = function (index) {
	if (this.isCachedIndex(index))
		this.loadBlockContaining(index);
	
	//TODO not correct yet...
	return this.blockbuffer[(index - this.blockStartIndex) % this.blockSize];
};

/**
 * Returns a boolean value if the specified |index| is contained in the cached
 * blocks for the sequence.
 * 
 * @param index 
 * 
 * @returns {Boolean}
 */
NucleotideSequence.prototype.isCachedIndex = function (index) {
	return index >= this.blockStartIndex && index <= this.blockEndIndex;
};

/**
 * Loads the cached memory blocks that include the specified |index| containing
 * and surrounding the main sequence block.
 * 
 * @param index
 */
NucleotideSequence.prototype.loadBlockContaining = function (index) {
	//TODO get the block from the database containing the specified index...
	this.blockbuffer = [];
};

/**
 * Clears the cached memory blocks for this instance of the nucleotide sequences
 * and resets the block indices. This should be followed by a new get(index) 
 * call to build the memory block around that index.
 */
NucleotideSequence.prototype.clear = function () {
	this.blockbuffer = [];
	this.blockEndIndex = 0;
	this.blockStartIndex = 0;
};


///////////////////////////////////////////////////////////////////////////////


/**
 * Returns the complement sequence to the sequence specified.
 * 
 * @param originalSeq	the original sequence to get the complement for.
 * 
 * @returns {String}
 */
function getComplementSequence(originalSeq) {
	var compSeq = "";
	for (var i = 0; i < originalSeq.length; i++) {
		compSeq = nucleotideDNA2RNA_Map[originalSeq[i]] || originalSeq[i];
	}
	return compSeq;
}




var charSize = 16; // number of bits in a character

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
    	for (var b = str.charCodeAt(c++); len > 0 && bit >= 0; len--, bit -= bitShift)
    		bytes.push(mapping[(b >> bit) & 3]);
    }
    console.log("    =>[" + bytes.join("") + "]");
    return bytes.join("");
}
