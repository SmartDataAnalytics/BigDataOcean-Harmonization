//When the user select a type a button is shown and/or a text field
function selectType() {
  var value = document.getElementById("sel1").value;
  if (value == "Copernicus Dataset") {
    document.getElementById('urifield').style.display = 'block';
    document.getElementById('addbutton').style.display = 'none';
  }else if (value == "Other"){
    document.getElementById('urifield').style.display = 'none';
    document.getElementById('addbutton').style.display = 'block';
  }else {
    document.getElementById('urifield').style.display = 'none';
    document.getElementById('addbutton').style.display = 'none';
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
  if (document.getElementById('urifield').style.display == 'block' && document.getElementById('uri').value == ""){
    alert("Please Fill the URI Field");
    return false;
  }
}