jQuery(document).ready(function($) {
  var resourceTypes = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/resourceType.json'
  });
  resourceTypes.initialize();
/*
  var elt = $('#tokenfield');
  elt.tagsinput({
    itemValue: 'value',
    itemText: 'text',
    typeaheadjs: {
      name: 'resourceType',
      displayKey: 'text',
      source: resourceTypes.ttAdapter()
    }
  });
 */   

  $('#tokenfield').tokenfield({
    typeahead: [null, {
      source: resourceTypes.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  }); 

/*

  $('#tokenfield').tokenfield({
    autocomplete: {
      source: ['red','blue','green','yellow','violet','brown','purple','black','white'],
      delay: 100
    },
    showAutocompleteOnFocus: true
  });*/
});