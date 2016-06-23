/**
 * Created by ucs_guoguibiao on 6/20 0020.
 */
var utils = require('utils');
var casper = require('casper').create({
    //clientScripts: ["jquery-2.1.3.min.js"],
    pageSettings: {
        javascriptEnabled: true,
        XSSAuditingEnabled: true,
        loadImages: true,        // The WebPage instance used by Casper will
        loadPlugins: false,         // use these settings
        userAgent: "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36"
    },
    waitTimeout: 10000,
    exitOnError: false,
    httpStatusHandlers: {
        404: function () {
            console.log(404);
        }
    },
    onAlert: function (msg) {
        console.log(msg);
    },
    onError: function (self, m) {
        console.log("FATAL:" + m);
        self.exit();
    },
    onDie: function () {
        console.log('dieing');
    },
    onLoadError: function (casper, url) {
        console.log(url + ' can\'t be loaded');
    },
    onPageInitialized: function () {

    },
    onResourceReceived: function () {
        //console.log(arguments[1]['url'] + ' Received');
    },
    onResourceRequested: function () {
        //console.log(arguments[1]['url'] + ' requested');
    },
    onStepComplete: function () {
        //console.log('onStepComplete');
    },
    onStepTimeout: function () {
        console.log('timeout');
    },
    logLevel: "debug",              // Only "info" level messages will be logged
    verbose: false                  // log messages will be printed out to the console
});
casper.on('remote.message', function (msg) {
    this.log(msg, 'info');
});


var address = casper.cli.get(0);//获得命令行第二个参数 接下来会用到
var methodName = casper.cli.get(1);
var param = decodeURIComponent(decodeURIComponent(casper.cli.get(2)));
var checkExist;
if(methodName == "xpath") {
    checkExist = {
        type: 'xpath',
        path: param
    }
} else if(methodName == "css"){
    checkExist = param;
}
var domain = casper.cli.get(3);
var cookie = decodeURIComponent(casper.cli.get(4));
if (cookie != null) {
    cookie.split(";").forEach(function(pair){
        pair = pair.split("=");
        phantom.addCookie({
            'name': pair[0],
            'value': pair[1],
            'domain': domain,
        });
    });
    //cookies = cookie.split(";");
    //for (i = 0; i < cookies.length; i++) {
    //    cookieVal = cookies[i].split("=");
    //    phantom.addCookie(
    //        {
    //            "domain": domain,
    //            "name": cookieVal[0],
    //            "path": "/",
    //            "value": cookieVal[1]
    //        });
    //}
}
casper.on('http.status.404', function(resource) {
    this.echo('Hey, this one is 404: ' + resource.url, 'warning');
});
casper.start(address);
casper.then(function(){
    this.wait(10000, function () {
        this.echo(this.getPageContent());
        this.exit();
    });
}).then(function(){this.exit();});
//casper.then( function() {
//    this.waitForSelector(checkExist, function() {                  //等到exist选择器匹配的元素出现时再执行回调函数
//        this.captureSelector('twitter.png', 'html');                 //成功时调用的函数,给整个页面截图
//        this.echo(this.getPageContent());
//    }, function() {
//        this.die('页面解析失败').exit();             //失败时调用的函数,输出一个消息,并退出
//    }, 1000);                                                        //超时时间,两秒钟后指定的选择器还没出现,就算失败
//});

casper.run();