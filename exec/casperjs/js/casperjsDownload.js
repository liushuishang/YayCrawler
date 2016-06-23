var utils = require('utils');
var casper = require('casper').create({
    clientScripts:  [
        //'jquery.min.js',      // These two scripts will be injected in remote
        //'underscore-min.js'   // DOM on every request
    ],
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
        },500: function () {
            console.log(500);
        },200: function () {
            console.log(200);
        },403: function () {
            console.log(403);
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
        //casper.echo(this.getPageContent());
        //this.exit();
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
        //console.log('timeout');
    },
    logLevel: "info",              // Only "info" level messages will be logged
    verbose: false                  // log messages will be printed out to the console
});
var address = casper.cli.get(0);//获得命令行第二个参数 接下来会用到
var userAgent = decodeURIComponent(decodeURIComponent(casper.cli.get(1)));
var domain = casper.cli.get(2);
var useGzip = casper.cli.get(3);
var retryTimes = casper.cli.get(4);
var cookie = decodeURIComponent(decodeURIComponent(casper.cli.get(5)));
if (userAgent != null) {
    casper.userAgent(userAgent);
}
if (cookie != null) {
    cookie.split(";").forEach(function(pair){
        pair = pair.split("=");
        phantom.addCookie({
            'name': pair[0],
            'value': pair[1],
            'domain': domain,
        });
    });
}
casper.on('remote.message', function (msg) {
    this.log(msg, 'info');
});

casper.start(address);

casper.then(function(){
    this.wait(10000, function () {
        this.echo(this.getPageContent());
        this.exit();
    });
}).then(function(){
    this.exit();
});

casper.run();