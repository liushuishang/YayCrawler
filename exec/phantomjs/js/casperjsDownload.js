/**
 * Created by ucs_guoguibiao on 6/20 0020.
 */
system = require('system')
var casper = require('casper').create({
    clientScripts:  [
    //    //'jquery.min.js',      // These two scripts will be injected in remote
    //    'underscore-min.js'   // DOM on every request
    ],
    remoteScripts:[
        //'http://libs.baidu.com/jquery/1.11.1/jquery.min.js',
        //'http://www.bootcss.com/p/underscore/underscore-min.js'
    ],
    pageSettings: {
        loadImages:  false,        // The WebPage instance used by Casper will
        loadPlugins: false         // use these settings
    },
    logLevel: "info",              // Only "info" level messages will be logged
    verbose: false                  // log messages will be printed out to the console
});
//require("utils").dump(casper.cli.args);
//require("utils").dump(system.args);
casper.options.waitTimeout = 20000;
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

casper.then(function() {
    this.capture('baidu-homepage.png');
});

//casper.then(function() {
//    this.fill('form[action="/s"]', { wd: 'thoughtworks' }, true);//填入form，进行搜索
//});

casper.then( function() {
    this.waitForSelector(checkExist, function() {                  //等到exist选择器匹配的元素出现时再执行回调函数
        this.captureSelector('twitter.png', 'html');                 //成功时调用的函数,给整个页面截图
        this.echo(this.getPageContent());
    }, function() {
        this.die('页面解析失败').exit();             //失败时调用的函数,输出一个消息,并退出
    }, 2000);                                                        //超时时间,两秒钟后指定的选择器还没出现,就算失败
});

casper.run();