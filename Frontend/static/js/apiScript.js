$( function() {
    $( "#accordion" ).accordion({
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

});