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
		var index = -1,
			map = new Array();
		
		map['a'] = map['A'] = 0;
		map['c'] = map['C'] = 1;
		map['g'] = map['G'] = 2;
		map['t'] = map['T'] = 
		map['u'] = map['U'] = 3;
		
		if (isNaN(codon) && codon.length == 3) {
			index = (map[codon.charAt(0)] << 4)
					| (map[codon.charAt(1)] << 2)
					| map[codon.charAt(2)];
		}
		
		if (index > 0 && index < this.codons.length) {
			// handle parameter as index number
			return this.aminoacid[this.codons[codon]];
		}
		else
			;//console.error("codon not found for " + codon);
	},
	
	/**
	 * Loads the file from the specified URL. The file should follow the correct
	 * XML schema for the amino acids library XML file type based on the
	 * AminoAcidLibXMLSchema.xsd file.
	 */
	load: function (url, completedFn, errorFn, statusFn) {
		completedFn = completedFn || this.loadData;
		var xmlHttp = {},
			codons = [], codon,
			aminos = [],
			index = 0,
			i = 0, nodes = [], node = {},
			map = new Array();
		
		map['a'] = map['A'] = 0;
		map['c'] = map['C'] = 1;
		map['g'] = map['G'] = 2;
		map['u'] = map['U'] = 3;
		
		if (window.XMLHttpRequest)
			xmlHttp = new XMLHttpRequest();
		else
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		
		xmlHttp.onreadystatechange = function () {
			if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
				var doc = xmlHttp.responseXML.documentElement;
				
				nodes = doc.getElementsByTagName('aminoacid');
				for (i = 0; i < nodes.length; i++) {
					aminos[i] = {};
					
					var n = nodes[i].getElementsByTagName('*');
					for (node = 0; node < n.length; node++) {
						if (n[node])
							if (n[node].nodeName == "codon") {
								codon = n[node].firstChild.nodeValue;
								index = (map[codon.charAt(0)] << 4)
								      | (map[codon.charAt(1)] << 2)
								      |  map[codon.charAt(2)];
								console.log("codon=" + codon + ", index=" + index);
								codons[index] = i;
							}
							else
								aminos[i][n[node].nodeName] = n[node].firstChild.nodeValue;
					}
				}
				
				if (completedFn)
					completedFn(aminos, codons);
			}
			//TODO else if (error) { if (errorFn) errorFn(this); }
		};
		xmlHttp.open('GET', url, true);
		xmlHttp.send();
	},
	
	loadData: function (aList, cList) {
		console.log("data loaded! aList=" + aList + ", cList=" + cList);
		
		for (var i = 0; i < aList.length; i++)
			this.aminoacid[i] = aList[i];
		
		for (var i = 0; i < cList.length; i++)
			this.codons[i] = cList[i];
		
		console.log("data loaded! this.aminoacid=" + this.aminoacid + ", this.codons=" + this.codons);
	},
};
