/**
 * 
 */
var AminoAcidLib = {

	aminoacid: new Array(),
	codons: new Array(),
	
	/**
	 * Returns the amino acid specified by the parameter codon. The codon is a
	 * three character sequence of a nucleotide sequence.
	 */
	get: function (codon) {
		//console.log("get(" + codon + ") from this.aminoacid=" + this.aminoacid + ", this.codons=" + this.codons);
		var index = this.getIndexForCodon(codon);
		//console.log(" --> index=" + index);
		if (index >= 0 && index < this.codons.length) {
			// handle parameter as index number
			return this.aminoacid[this.codons[index]];
		}
		else {
			;//console.error("codon not found for " + codon);
			return undefined;
		}
	},
	
	getIndexForCodon: (function () {
		var index = -1,
			map = new Array();
	
		map['a'] = map['A'] = 0;
		map['c'] = map['C'] = 1;
		map['g'] = map['G'] = 2;
		map['t'] = map['T'] = 
		map['u'] = map['U'] = 3;
		
		return function (codon) {
			if (isNaN(codon) && codon.length == 3) {
				index = (map[codon.charAt(0)] << 4)
				      | (map[codon.charAt(1)] << 2)
				      |  map[codon.charAt(2)];
				return index;
			}
			else
				return -1;
		};
	})(),
	
	/**
	 * Loads the file from the specified URL. The file should follow the correct
	 * XML schema for the amino acids library XML file type based on the
	 * AminoAcidLibXMLSchema.xsd file.
	 */
	load: function (fileName, completedFn, errorFn, statusFn) {
		completedFn = completedFn || this.loadData;
		var xmlHttp = {},
			codon,
			i = 0, nodes = [], node = {},
			map = new Array(),
			self = this;
		
		map['a'] = map['A'] = 0;
		map['c'] = map['C'] = 1;
		map['g'] = map['G'] = 2;
		map['u'] = map['U'] = 3;
		
		if (window.XMLHttpRequest)
			xmlHttp = new XMLHttpRequest();
		else
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		
		xmlHttp.onreadystatechange = function () {
			var doc, n;
			if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
				doc = xmlHttp.responseXML.documentElement;
				nodes = doc.getElementsByTagName('aminoacid');
				for (i = 0; i < nodes.length; i++) {
					n = nodes[i].getElementsByTagName('*');
					for (node = 0; node < n.length; node++) {
						if (n[node]) {
							if (n[node].nodeName == "codon") {
								codon = n[node].firstChild.nodeValue;
								self.codons[self.getIndexForCodon(codon)] = i;
							}
							else {
								if (self.aminoacid[i] === undefined)
									self.aminoacid[i] = {};
								self.aminoacid[i][n[node].nodeName] = n[node].firstChild.nodeValue;
								console.log("aminoacid[" + i + "][" + n[node].nodeName + "] = " + n[node].firstChild.nodeValue);
							}
						}
					}
				}
			}
			//TODO else if (error) { if (errorFn) errorFn(this); }
		};
		var protocol = "http://",
		    host = "localhost",
		    port = ":" + 8080,
		    service = "/dnaa/services/xml/" + fileName;
		xmlHttp.open('GET', protocol + host + port + service, true);
		xmlHttp.send();
	},
	
	/**
	 * Inner object for drawing the nucleotide sequence as an UI element.
	 */
	ui: {
		
		fill: (function () {
			var f = new Array(6);
			f['start'] = f['Start'] = f['START'] = '#66BB66';
			f['stop']  = f['Stop']  = f['STOP']  = '#664444';
			
			return function (amino) { return amino.flag ? f[amino.flag] : '#AAAAAA'; };
		})(),
		
		stroke: (function () {
			var l = new Array(6);
			l['start'] = l['Start'] = l['START'] = '#002200';
			l['stop']  = l['Stop']  = l['STOP']  = '#220000';
			
			return function (amino) { return amino.flag ? l[amino.flag] : '#666666'; };
		})(),
		
		/**
		 * Returns the color for the text of the specified character.
		 */
		text: (function () {
			var c = new Array(6);
			c['start'] = c['Start'] = c['START'] = '#DDFFDD';
			c['stop']  = c['Stop']  = c['STOP']  = '#FFDDDD';
			
			return function (amino) { return amino.flag ? c[amino.flag] : '#DDDDDD'; };
		})(),

	},
	
};
