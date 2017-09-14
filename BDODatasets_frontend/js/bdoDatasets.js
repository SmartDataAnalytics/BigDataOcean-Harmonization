/*!
 * @author: Ana C Trillos
 * @author: Jaime M Trillos
 */

function cancelButton() {

}

function submitUriButton(form) {
    var x = form.uri.value;
    var uri = encodeURI(x);
    console.log("this is " + uri);
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

function isNumberKey(evt) {
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}
