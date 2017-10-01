$(document).on("click", ".add-button", function(e) {
    bootbox.prompt({ 
		size: "small",
		title: "Insert the Dataset URI", 
		callback: function(result){ 
		  	//result = String containing user input if OK clicked or null if Cancel clicked  
		}
	})
});