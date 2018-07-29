//When the user select a type a button is shown and/or a text field
function selectType() {
  var value = document.getElementById("sel1").value;
  if (value == "Copernicus Dataset") {
    document.getElementById('fileNetcdf').value = "";
    document.getElementById('fileCsv').value = "";
    document.getElementById('copernicusDiv').style.display = 'block';
    document.getElementById('otherDiv').style.display = 'none';
    document.getElementById('netcdfDiv').style.display = 'none';
    document.getElementById('csvDiv').style.display = 'none';
  }else if (value == "NetCDF Dataset File"){
    document.getElementById('uri').value = "";
    document.getElementById('fileCsv').value = "";
    document.getElementById('copernicusDiv').style.display = 'none';
    document.getElementById('otherDiv').style.display = 'none';
    document.getElementById('netcdfDiv').style.display = 'block';
    document.getElementById('csvDiv').style.display = 'none';
  }else if (value == "CSV Dataset File"){
    document.getElementById('uri').value = "";
    document.getElementById('fileNetcdf').value = "";
    document.getElementById('copernicusDiv').style.display = 'none';
    document.getElementById('otherDiv').style.display = 'none';
    document.getElementById('netcdfDiv').style.display = 'none';
    document.getElementById('csvDiv').style.display = 'block';
  }else if (value == "Other"){
    document.getElementById('uri').value = "";
    document.getElementById('fileNetcdf').value = "";
    document.getElementById('fileCsv').value = "";
    document.getElementById('copernicusDiv').style.display = 'none';
    document.getElementById('otherDiv').style.display = 'block';
    document.getElementById('netcdfDiv').style.display = 'none';
    document.getElementById('csvDiv').style.display = 'none';
  }else {
    document.getElementById('uri').value = "";
    document.getElementById('fileNetcdf').value = "";
    document.getElementById('fileCsv').value = "";
    document.getElementById('copernicusDiv').style.display = 'none';
    document.getElementById('otherDiv').style.display = 'none';
    document.getElementById('netcdfDiv').style.display = 'none';
    document.getElementById('csvDiv').style.display = 'none';
  }
}

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

function requireUri(){
  if (document.getElementById('copernicusDiv').style.display == 'block' && document.getElementById('uri').value == ""){
    $.alert({
        title: 'Alert!',
        content: 'Please fill the URI Field.',
        type: 'red',
        typeAnimated: true,
        useBootstrap: true,
    });
    return false;
  }
  if (document.getElementById('csvDiv').style.display == 'block' && document.getElementById('fileCsv').value == ""){
    $.alert({
        title: 'Alert!',
        content: 'Please fill the URL Field.',
        type: 'red',
        typeAnimated: true,
        useBootstrap: true,
    });
    return false;
  }
  if (document.getElementById('netcdfDiv').style.display == 'block' && document.getElementById('fileNetcdf').value == ""){
    $.alert({
        title: 'Alert!',
        content: 'Please choose a file in the File Field.',
        type: 'red',
        typeAnimated: true,
        useBootstrap: true,
    });
    return false;
  }
  if (document.getElementById('netcdfDiv').style.display == 'block' && document.getElementById('fileNetcdf').value != ""){
    return Validate();
  }
}

var _validFileExtensions = [".nc"];    
function Validate() {
    var arrInputs = document.getElementsByTagName("input");
    for (var i = 0; i < arrInputs.length; i++) {
        var oInput = arrInputs[i];
        if (oInput.type == "file") {
            var sFileName = oInput.value;
            if (sFileName.length > 0) {
                var blnValid = false;
                for (var j = 0; j < _validFileExtensions.length; j++) {
                    var sCurExtension = _validFileExtensions[j];
                    if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() == sCurExtension.toLowerCase()) {
                        blnValid = true;
                        break;
                    }
                }
                
                if (!blnValid) {
                    $.alert({
                        title: 'Alert!',
                        content: "Sorry, " + sFileName + " is invalid, allowed extensions are: " + _validFileExtensions,
                        type: 'red',
                        typeAnimated: true,
                        useBootstrap: true,
                    });
                    return false;
                }
            }
        }
    }
  
    return true;
}