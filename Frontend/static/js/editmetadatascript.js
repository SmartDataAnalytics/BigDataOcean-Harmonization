//List of variables extracted from variablesCF_BDO.json
var options = {
  url: "../static/json/variablesCF_BDO.json",

  getValue: "text",

  list: {
    match: {
      enabled: true
    }
  }  
};

//Enable easyautocomplete (List) to field json_variable-#
size = jQuery('#tbl_posts >tbody >tr').length;
for (i = 0; i <= size; i++) { 
    $("#json_variable-"+i).easyAutocomplete(options);
}

//Creation of tokenfields 
jQuery(document).ready(function($) {
  var resourceSubjects = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: '../static/json/subject.json'
  });
  resourceSubjects.initialize(); 

  var resourceLanguages = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: '../static/json/language.json'
  });
  resourceLanguages.initialize();

  var resourceKeywords = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: '../static/json/keywords.json'
  });
  resourceKeywords.initialize();

  $('#tokenfield_subject').tokenfield({
    typeahead: [null, {
      source: resourceSubjects.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  }); 

  $('#tokenfield_subject').on('tokenfield:createtoken', function (event) {
    var existingTokens = $(this).tokenfield('getTokens');
    $.each(existingTokens, function(index, token) {
      if (token.value === event.attrs.value)
        event.preventDefault();
    });
  });

  $('#tokenfield_language').tokenfield({
    typeahead: [null, {
      source: resourceLanguages.ttAdapter(),
      displayKey: 'text'
    }],
    showAutocompleteOnFocus: true
  }); 

  $('#tokenfield_language').on('tokenfield:createtoken', function (event) {
    var existingTokens = $(this).tokenfield('getTokens');
    $.each(existingTokens, function(index, token) {
      if (token.value === event.attrs.value)
        event.preventDefault();
    });
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
  
});

//Creation of dynamic rows for the table variables
jQuery(document).delegate('a.add-record', 'click', function(e) {
  e.preventDefault();    
  var content = jQuery('#sample_table tr'),
  size = jQuery('#tbl_posts >tbody >tr').length + 1,
  element = null,    
  element = content.clone();
  element.attr('id', 'rec-'+size);
  element.find('#json_variable').attr('id', 'json_variable-'+size);
  element.find('.delete-record').attr('data-id', size);
  element.appendTo('#tbl_posts_body');
  $('#json_variable-'+size).easyAutocomplete(options);
  element.find('.sn').html(size);
});

//Deletion of rows for the table variables
jQuery(document).delegate('a.delete-record', 'click', function(e) {
     e.preventDefault();    
     var didConfirm = confirm("Are you sure You want to delete?");
     if (didConfirm == true) {
      var id = jQuery(this).attr('data-id');
      var targetDiv = jQuery(this).attr('targetDiv');
      jQuery('#rec-' + id).remove();
      
    //regnerate index number on table
    $('#tbl_posts_body tr').each(function(index) {
      //alert(index);
      $(this).find('span.sn').html(index+1);
    });
    return true;
  } else {
    return false;
  }
});

function cancelButton(){
  window.location.href="/"
}