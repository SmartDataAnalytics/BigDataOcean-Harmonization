function deleteButton(){	
	ident = document.getElementById("identifier").value
	$.confirm({
		title: 'Alert!',
		content: 'Deleting this dataset might take a little time. Do you want to delete it?',
		type: 'red',
		useBootstrap: true,
		typeAnimated: true,
		buttons: {
			delete: {
				text: 'Delete',
				btnClass: 'btn-red',
				action: function(){
					return window.location.href="/delete/"+ident
				}
			},
			cancel: function(){

			}
		}
	});
	
}