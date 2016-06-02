//codes.js   
system = require('system')   
address = system.args[1];
//console.log('Loading a web page');   
var page = require('webpage').create();   
var url = address;   
//console.log(url);
var tmp = [];
page.onResourceReceived = function(response) {
	if(response.contentType == null)
		console.log(JSON.stringify(response));
};
page.open(url, function (status) {   
    //Page is loaded!   
    if (status !== 'success') {   
        console.log('Unable to post!');   
    } else {
        console.log(page.content);   
    }
    phantom.exit();   
});

