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

var pageUrl = casper.cli.get(0);
var deltaResolveServer = casper.cli.get(1);
var domain=casper.cli.get(2);
var cookie= decodeURIComponent(decodeURIComponent(casper.cli.get(3)));

//casper.echo(pageUrl);
//casper.echo(deltaResolveServer);
//casper.echo(domain);
//casper.echo(cookie);

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
casper.start(pageUrl).then(function () {
    this.wait(6000, function () {
        //this.echo("等待5秒以便页面充分渲染");
    });
});
casper.then(function () {
    if (!this.exists(".gt_slider_knob")) {
        this.echo("页面中不存在极验验证码模块");
        //this.echo(this.getPageContent());
        this.exit();
    }
});
var pageParam = null;
casper.waitFor(function check() {
    return this.evaluate(function () {
        return (document.querySelectorAll('.gt_cut_bg_slice').length == 52) && (document.querySelectorAll('.gt_cut_fullbg_slice').length == 52);
    });
}, function then() {
    this.echo("页面渲染成功!");
    var styleReg = new RegExp("background-image: url\\((.*?)\\); background-position: (.*?);");
    var fullbgSrcArray = [];
    var fullbgCoordinateArray = [];
    var fullbgSliceArray = this.getElementsAttribute('.gt_cut_fullbg_slice', 'style');

    for (var i = 0; i < fullbgSliceArray.length; i++) {
        var result = styleReg.exec(fullbgSliceArray[i]);
        if (result != null) {
            fullbgSrcArray.push(result[1]);
            fullbgCoordinateArray.push(result[2]);
        } else this.echo(fullbgSliceArray[i]);
    }
    var bgSrcArray = [];
    var bgCoordinateArray = [];
    var bgSliceArray = this.getElementsAttribute('.gt_cut_bg_slice', 'style');
    for (var i = 0; i < bgSliceArray.length; i++) {
        var result = styleReg.exec(bgSliceArray[i]);
        if (result != null) {
            bgSrcArray.push(result[1]);
            bgCoordinateArray.push(result[2]);
        }
    }
    var data = {};
    data.fullbgSrcArray = fullbgSrcArray;
    data.fullbgPositionArray = fullbgCoordinateArray;
    data.bgSrcArray = bgSrcArray;
    data.bgPositionArray = bgCoordinateArray;
    data.itemWidth = 10;//每个小块的宽度（像素）
    data.itemHeight = 58;//每个小块的高度（像素）
    data.lineItemCount = 26;//拼图中每行包含的小图片个数
    pageParam = data;
}, function () {
    this.echo("等待渲染超时！");
    this.exist();
}, 10000);

var trailId=null;
var deltaX=0;
var trailArrayStr=null;
casper.then(function () {
    if (pageParam == null) {
        this.echo("收集图片参数失败!");
        //this.echo(this.getPageContent());
        this.exit();
    }
    this.echo("开始请求滑块轨迹");
    var result = casper.evaluate(function (url, param) {
        return JSON.parse(__utils__.sendAJAX(url, 'POST', param, false));
    }, deltaResolveServer, {"params": JSON.stringify(pageParam)});
    if (result != null && result.status == 1) {
        trailArrayStr = result.data.trailArray;
        deltaX=result.data.deltaX;
        trailId = result.data.trailId;
        this.echo("滑块轨迹求解成功:" + JSON.stringify(result.data));
    }
    else {
        this.echo("请求滑块轨迹失败:" + JSON.stringify(result));
        this.exit();
    }
});
casper.then(function () {
    this.echo("开始移动滑块,位移为:"+deltaX);
    this.evaluate(function (selector, trailArrayStr,deltaX) {
        var deltaArray = eval(trailArrayStr);
        var createEvent = function (eventName, ofsx, ofsy) {
            var evt = document.createEvent('MouseEvents');
            evt.initMouseEvent(eventName, true, false, null, 0, 0, 0, ofsx, ofsy, false, false, false, false, 0, null);
            return evt;
        };
        //查找要移动的对象
        var obj = document.querySelector(selector);
        var startX = Math.round(obj.getBoundingClientRect().left + 20);
        var startY = Math.round(obj.getBoundingClientRect().top + 18);
        var nowX = startX;
        var nowY = startY;
        console.log("startX:" + startX);
        console.log("startY:" + startY);
        var moveToTarget = function (loopRec) {
            setTimeout(function () {
                nowX = nowX + deltaArray[loopRec][0];
                nowY = nowY + deltaArray[loopRec][1];
                //console.log(loopRec + "次移动滑块");
                obj.dispatchEvent(createEvent('mousemove', nowX, nowY));
                console.log("当前滑块轨迹:" + obj.getBoundingClientRect().left);
                if (nowX > (startX + deltaX)) {
                    obj.dispatchEvent(createEvent('mousemove', startX + deltaX-2, nowY));
                    obj.dispatchEvent(createEvent('mouseup', startX + deltaX-2, nowY));
                    console.log("最终滑块轨迹:" + obj.getBoundingClientRect().left);
                } else {
                    moveToTarget(loopRec + 1);
                }
            }, deltaArray[loopRec][2]);
        };
        obj.dispatchEvent(createEvent("mousedown", startX, startY));
        moveToTarget(2);
    }, ".gt_slider_knob", trailArrayStr,deltaX);
}).then(function () {
    casper.waitForSelectorTextChange('.gt_info_type', function () {
        var status = this.fetchText('.gt_info_type');
        this.echo("验证结果:" + status);
        //this.capture(status.replace(":","_")+ (new Date()).getTime() + '.png');
        if (status.indexOf("通过") > -1) {
            if (this.exists('#verify')) {
                this.click("#verify");
                this.echo("点击成功");
            }
        }
    }, function () {
        this.echo("等待滑块移动超时！");
    }, 10000);
});
casper.run();