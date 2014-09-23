/**
 * 
 */
var AminoAcidLib = {

	get: function (codon) {
		
	},
	
	load: function (url, completedFn, errorFn, statusFn) {
		var xmlHttp = {}, xx, codons = [], i = 0;
		
		if (window.XMLHttpRequest)
			xmlHttp = new XMLHttpRequest();
		else
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		
		xmlHttp.onreadystatechange = function () {
			if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
				codons = xmlHttp.responseXML.documentElement.getElementsByTagName('codon');
				for ( ; i < codons.length; i++) {
					console.log("codon=" + codons[i].firstChild.nodeValue);//getElementsByTagName('');
				}
			}
		};
		xmlHttp.open('GET', url, true);
		xmlHttp.send();
	}
};
