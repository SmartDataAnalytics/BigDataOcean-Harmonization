//activate accordion jquery plugin
$( function() {
    $( "#accordion" ).accordion({
      collapsible: true,
      autoHeight: false,
      active: false,
    });
    $( "#accordion1" ).accordion({
      collapsible: true,
      autoHeight: false,
      active: false,
    });
    $( "#accordion2" ).accordion({
      collapsible: true,
      autoHeight: false,
      active: false,
    });
});

//Creation of tokenfields 
jQuery(document).ready(function($) {

	var resourceVariables = new Bloodhound({
	  prefetch: 'static/json/variablesCF_BDO_tokenfield.json',
	  datumTokenizer: function(d) {
	    return Bloodhound.tokenizers.whitespace(d.value);
	  },
	  queryTokenizer: Bloodhound.tokenizers.whitespace
	});

	resourceVariables.initialize();

  $('#tokenfield_variables').tokenfield({
    typeahead: [null, {
      source: resourceVariables.ttAdapter(),
      displayKey: 'value'
    }],
    showAutocompleteOnFocus: true
  }); 

  //if the token is duplicated then it is not created
  $('#tokenfield_variables').on('tokenfield:createtoken', function (event) {
    var existingTokens = $(this).tokenfield('getTokens');
    $.each(existingTokens, function(index, token) {
      if (token.value === event.attrs.value)
        event.preventDefault();
    });
  });

  var resourceSubjects = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/subject.json'
  });
  resourceSubjects.initialize(); 

  var resourceKeywords = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/keywords.json'
  });
  resourceKeywords.initialize();

  var resourceGeoLoc = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: 'static/json/marineregions.json'
  });
  resourceGeoLoc.initialize();

  $('#tokenfield_subject').tokenfield({
    typeahead: [null, {
      source: resourceSubjects.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  }); 

  //if the token is duplicated then it is not created
  $('#tokenfield_subject').on('tokenfield:createtoken', function (event) {
    var existingTokens = $(this).tokenfield('getTokens');
    $.each(existingTokens, function(index, token) {
      if (token.value === event.attrs.value)
        event.preventDefault();
    });
  });

  //if the token has value with http then it is created
  $('#tokenfield_subject').on('tokenfield:createtoken', function (event) {
    var flag = false;
    var pattern = /http/;
    $.each(resourceSubjects, function(index, value) {
      var exists = pattern.test(event.attrs.value);
      flag = exists;
    });
    if(!flag) {
      event.preventDefault(); //prevents creation of token
    }
  });

  $('#tokenfield_keywords').tokenfield({
    typeahead: [null, {
      source: resourceKeywords.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  });

  $('#tokenfield_keywords').on('tokenfield:createtoken', function (event) {
    var existingTokens = $(this).tokenfield('getTokens');
    $.each(existingTokens, function(index, token) {
      if (token.value === event.attrs.value)
        event.preventDefault();
    });
  });

  $('#tokenfield_keywords').on('tokenfield:createtoken', function (event) {
    var flag = false;
    var pattern = /http/;
    $.each(resourceKeywords, function(index, value) {
      var exists = pattern.test(event.attrs.value);
      flag = exists;
    });
    if(!flag) {
      event.preventDefault(); //prevents creation of token
    }
  });

  $('#tokenfield_geo_loc').tokenfield({
    typeahead: [null, {
      source: resourceGeoLoc.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  });

  $('#tokenfield_geo_loc').on('tokenfield:createtoken', function (event) {
    var existingTokens = $(this).tokenfield('getTokens');
    $.each(existingTokens, function(index, token) {
      if (token.value === event.attrs.value)
        event.preventDefault();
    });
  });  

  $('#tokenfield_geo_loc').on('tokenfield:createtoken', function (event) {
    var flag = false;
    var pattern = /http/;
    $.each(resourceGeoLoc, function(index, value) {
      var exists = pattern.test(event.attrs.value);
      flag = exists;
    });
    if(!flag) {
      event.preventDefault(); //prevents creation of token
    }
  });

});