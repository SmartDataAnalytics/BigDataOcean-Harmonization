//List of variables extracted from canonicalModelMongo.json
var options = {
  url: "../static/json/canonicalModelMongo.json",

  getValue: "canonicalName",

  template: {
    type: "description",
    fields: {
      description: "name"
    }
  },

  list: {
    maxNumberOfElements: 10,
    match: {
      enabled: true
    },
    sort: {
      enabled: true
    }
  }  
};

//Enable easyautocomplete (List) to field json_variable-#
size = jQuery('#tbl_posts >tbody >tr').length;
for (i = 1; i <= size; i++) { 
    $("#json_variable-"+i).easyAutocomplete(options);
}

//List of tableStorage extracted from JWT GET request and saved in storageTable.json
var optionsStorage = {
  url: "../static/json/storageTable.json",

  getValue: "tableName",

  list: {
    maxNumberOfElements: 10,
    match: {
      enabled: true
    },
    sort: {
      enabled: true
    }
  }
};

$("#storageTable").easyAutocomplete(optionsStorage);

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

  var resourceGeoLoc = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: '../static/json/marineregions.json'
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

  // avoid free text in easy autocomplete field (BDO variables)
  $('#json_variable-'+size).on("blur", function() {

    var $input = $('#json_variable-'+size),
        value = $input.val(),
        list = $input.getItems(),
        foundMatch = false;
    for (var i = 0, length = list.length; i < length; i += 1) {

      if (list[i].label === value) {
        foundMatch = true;
        break;
      }
    }

    if (!foundMatch || list.length === 0) {
      $input.val("").trigger("change");//or other message
    }

  });

  element.find('.sn').html(size);
  $('#json_variable-'+size).prop('required',true);
  $('#parser_variable').prop('required',true);
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

function requireFields(){
  for (var i = 1; i <=size; i++) {
    var jsonvariable = $("#json_variable-"+i).val();
    if (jsonvariable === ""){
      $.alert({
        title: 'Alert!',
        content: 'Please choose a variable in the Variable Field.',
        type: 'red',
        typeAnimated: true,
        useBootstrap: true,
      });
      return false;
    }
  }
  var parser_variable = $("#parser_variable").val();
  var title = $("#title").val();
  var description = $("#description").val();
  var tokenfield_language = $("#tokenfield_language").val();
  var publisher = $("#publisher").val();
  var storageTable = $("#storageTable").val();
  var issued_date = $("#issued_date").val();
  var modified_date = $("#modified_date").val();
  var time_reso = $("#time_reso").val();
  var tokenfield_subject = $("#tokenfield_subject").val();
  var tokenfield_keywords = $("#tokenfield_keywords").val();
  if(tokenfield_subject === "" || tokenfield_keywords === "" || parser_variable === ""
     || title === "" || description === "" || tokenfield_language === ""
      || publisher === "" || storageTable === "" || issued_date === ""
       || modified_date === "" || time_reso === ""){
    $.alert({
        title: 'Alert!',
        content: 'Please fill the mandatory fields.',
        type: 'red',
        typeAnimated: true,
        useBootstrap: true,
      });
      return false;
  }

  elem = document.getElementById( "div_profileinfo" );
  vis = elem.style
  if(vis.display === "block"){
    var nameProfile = $("#nameProfile").val();
    if (nameProfile === ""){
      $.alert({
        title: 'Alert!',
        content: 'Please choose the name of the profile.',
        type: 'red',
        typeAnimated: true,
        useBootstrap: true,
      });
      return false;
    }
  }

}

// avoid free text in easy autocomplete field (BDO variables)
$('#json_variable-1').on("blur", function() {

  var $input = $('#json_variable-1'),
      value = $input.val(),
      list = $input.getItems(),
      foundMatch = false;
  for (var i = 0, length = list.length; i < length; i += 1) {
    
    if (list[i].label === value) {
      foundMatch = true;
      break;
    }
  }

  if (!foundMatch || list.length === 0) {
    $input.val("").trigger("change");//or other message
  }

});

//Add variables to the table.
$.each(response, function(i, item) {
  var variable = response[i].split(" -- ");
  var content = jQuery('#sample_table tr'),
  size = jQuery('#tbl_posts >tbody >tr').length + 1,
  element = null,    
  element = content.clone();
  element.attr('id', 'rec-'+size);
  element.find('#parser_variable').attr('value', variable[0]);
  element.find('#json_variable').attr('id', 'json_variable-'+size);
  element.find('#json_variable-'+size).attr('value', variable[2]);
  element.find('#unit_variable').attr('value', variable[1]);
  element.find('.delete-record').attr('data-id', size);
  element.appendTo('#tbl_posts_body');
  $('#json_variable-'+size).easyAutocomplete(options);
  // avoid free text in easy autocomplete field (BDO variables)
  $('#json_variable-'+size).on("blur", function() {

    var $input = $('#json_variable-'+size),
        value = $input.val(),
        list = $input.getItems(),
        foundMatch = false;
    for (var i = 0, length = list.length; i < length; i += 1) {

      if (list[i].label === value) {
        foundMatch = true;
        break;
      }
    }

    if (!foundMatch || list.length === 0) {
      $input.val("").trigger("change");//or other message
    }

  });

  element.find('.sn').html(size);
  $('#json_variable-'+size).prop('required',true);
  $('#parser_variable').prop('required',true);
});

//When the user click on add a new container is shown
function toggleLayer( whichLayer ){
    var elem, vis;
    if( document.getElementById ) // this is the way the standards work
        elem = document.getElementById( whichLayer );
    else if( document.all ) // this is the way old msie versions work
        elem = document.all[whichLayer];
    else if( document.layers ) // this is the way nn4 works
        elem = document.layers[whichLayer];
    vis = elem.style;
    // if the style.display value is blank we try to figure it out here
    if(vis.display===''&&elem.offsetWidth!==undefined&&elem.offsetHeight!==undefined)
        vis.display = (elem.offsetWidth!==0&&elem.offsetHeight!==0)?'block':'none';
    vis.display = (vis.display===''||vis.display==='block')?'none':'block';
}



