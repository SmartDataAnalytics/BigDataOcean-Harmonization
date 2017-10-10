$(document).on("click", ".add-button", function(e) {
    bootbox.prompt({ 
		size: "small",
		title: "Insert the Dataset URI", 
		callback: function(result){ 
		  	//result = String containing user input if OK clicked or null if Cancel clicked  
		}
	})
});

var resourceTypes = new Bloodhound({
  datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
  queryTokenizer: Bloodhound.tokenizers.whitespace,
  prefetch: 'static/json/resourceType.json'
});
resourceTypes.initialize();

var elt = $('#Type');
elt.tagsinput({
  itemValue: 'value',
  itemText: 'text',
  typeaheadjs: {
    name: 'resourceType',
    displayKey: 'text',
    source: resourceTypes.ttAdapter()
  }
});

elt.tagsinput('add', { "value": "http://inspire.ec.europa.eu/metadata-codelist/ResourceType/series" , "text": "Series" });
elt.tagsinput('add', { "value": "http://inspire.ec.europa.eu/metadata-codelist/ResourceType/dataset" , "text": "Dataset"});

// HACK: overrule hardcoded display inline-block of typeahead.js
$(".twitter-typeahead").css('display', 'inline');


$('.bootstrap-tagsinput > > input').tokenfield({
  autocomplete: {
    source: ['red','blue','green','yellow','violet','brown','purple','black','white'],
    delay: 100
  },
  showAutocompleteOnFocus: true
})