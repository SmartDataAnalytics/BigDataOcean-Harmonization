/*!
 * @author: Ana C Trillos
 * @author: Jaime M Trillos
 */

function cancelButton() {

}

function submitUriButton(form) {
    var uri = encodeURI(form.uri.value);
    console.log("this is " + uri);
    if (!isURL(uri)){
        alert("ERROR");
    }else {
        var command = "/home/anatrillos/Documents/BigDataOcean-Harmonization/Backend/bdodatasets/target/BDODatsets-bdodatasets/BDODatsets/bin/suggest \""+ uri +"\"";
        var exec = require('child_process').exec();
        child = exec(command,
            function (error, stdout, stderr) {
                console.log('stdout: ' + stdout);
                console.log('stderr: ' + stderr);
                if (error !== null) {
                    console.log('exec error: ' + error);
                }
            }
        )
    }
}

function isNumberKey(evt) {
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}

function isURL(str) {
  var pattern = new RegExp('^(https?:\\/\\/)?'+ // protocol
  '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.?)+[a-z]{2,}|'+ // domain name
  '((\\d{1,3}\\.){3}\\d{1,3}))'+ // OR ip (v4) address
  '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*'+ // port and path
  '(\\?[;&a-z\\d%_.~+=-]*)?'+ // query string
  '(\\#[-a-z\\d_]*)?$','i'); // fragment locator
  return pattern.test(str);
}
