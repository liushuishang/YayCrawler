//codes.js   
system = require('system')
address = system.args[1];//获得命令行第二个参数 接下来会用到
//console.log('Loading a web page');
var page = require('webpage').create();
cookie = system.args[2];
if (cookie != null) {
    cookies = cookie.split(";");
    for (i = 0; i < cookies.length; i++) {
        console.log(cookies[i]);
        cookieVal = cookies[i].split("=");
        phantom.addCookie(
            {
                "domain": ".jiayuan.com",
                "name": cookieVal[0],
                "path": "/",
                "value": cookieVal[1]
            });
    }
}
var url = address;
page.onResourceReceived = function (response) {
    if (response.contentType == null)
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