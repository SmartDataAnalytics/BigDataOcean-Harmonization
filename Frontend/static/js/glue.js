var yasqe = YASQE(document.getElementById('yasqe'), {
	sparql: {
		endpoint: 'http://localhost:3030/bdoHarmonization/sparql',
		showQueryButton: true
	},
	value: 'SELECT ?p ?o \nWHERE {\n<http://bigdataocean.eu/bdo/MEDSEA_ANALYSIS_FORECAST_WAV_006_011> ?p ?o\n}'
});

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