var yasqe = YASQE(document.getElementById('yasqe'), {
	sparql: {
		endpoint: 'http://212.101.173.34:3031/bdoHarmonization/sparql',
		showQueryButton: true
	},
	value: 'PREFIX dct: <http://purl.org/dc/terms/>\nPREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\nSELECT ?dataset ?identifier \nWHERE {\n?dataset dct:identifier ?identifier;\n a dcat:Dataset.\n}'
});

YASR.defaults.outputPlugins = ["error", "boolean", "rawResponse", "table"]

var yasr = YASR(document.getElementById("yasr"), {
	//this way, the URLs in the results are prettified using the defined prefixes in the query
	getUsedPrefixes: yasqe.getPrefixesFromQuery
});
 
/**
* Set some of the hooks to link YASR and YASQE
*/
yasqe.options.sparql.callbacks.success =  function(data, textStatus, xhr) {
	yasr.setResponse({response: data, contentType: xhr.getResponseHeader("Content-Type")});
};
yasqe.options.sparql.callbacks.error = function(xhr, textStatus, errorThrown) {
	var exceptionMsg = textStatus + " (#" + xhr.status + ")";
	if (errorThrown && errorThrown.length) exceptionMsg += ": " + errorThrown;
	yasr.setResponse({exception: exceptionMsg});
};
