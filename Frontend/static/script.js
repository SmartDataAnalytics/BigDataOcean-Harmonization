jQuery(document).ready(function($) {
  var resourceTypes = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/resourceType.json'
  });
  resourceTypes.initialize();

  var resourceSubjects = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/subject.json'
  });
  resourceSubjects.initialize(); 

  var resourceFormats = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/encodingFormat.json'
  });
  resourceFormats.initialize();

  var resourceLanguages = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/language.json'
  });
  resourceLanguages.initialize();

  var resourceKeywords = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/keywords.json'
  });
  resourceLanguages.initialize();

  $('#tokenfield_type').tokenfield({
    typeahead: [null, {
      source: resourceTypes.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  });

  $('.token-variables-field').tokenfield();

  $('#tokenfield_subject').tokenfield({
    typeahead: [null, {
      source: resourceSubjects.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  }); 

  $('#tokenfield_format').tokenfield({
    typeahead: [null, {
      source: resourceFormats.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  }); 

  $('#tokenfield_language').tokenfield({
    typeahead: [null, {
      source: resourceLanguages.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  }); 

  $('#tokenfield_keywords').tokenfield({
    typeahead: [null, {
      source: resourceKeywords.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  });

});