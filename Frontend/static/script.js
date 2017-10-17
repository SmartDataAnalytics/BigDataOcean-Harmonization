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

  $('#tokenfield_type').tokenfield({
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

jQuery(document).ready(function($) {
  var resourceTypes = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/subject.json'
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

  $('#tokenfield_subject').tokenfield({
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

jQuery(document).ready(function($) {
  var resourceTypes = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/encodingFormat.json'
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

  $('#tokenfield_format').tokenfield({
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