var utils = require('utils');
var casper = require('casper').create({
    //clientScripts: ["jquery-2.1.3.min.js"],
    pageSettings: {
        javascriptEnabled: true,
        XSSAuditingEnabled: true,
        loadImages: true,        // The WebPage instance used by Casper will
        loadPlugins: true,         // use these settings
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
        if (arguments[1]['url'].indexOf("http://api.geetest.com/ajax.php") >= 0) {
            utils.log(arguments);
        }
        //console.log(arguments[1]['url'] + ' requested');
    },
    onResourceRequested: function () {
        //console.log(arguments[1]['url'] + ' received');
    },
    onStepComplete: function () {
        //console.log('onStepComplete');
    },
    onStepTimeout: function () {
        console.log('timeout');
    },
    logLevel: "debug",              // Only "info" level messages will be logged
    verbose: true                  // log messages will be printed out to the console
});
casper.on('remote.message', function (msg) {
    this.log(msg, 'info');
});

var pageUrl = casper.cli.get(0);
var deltaResolveServer = casper.cli.get(1);
var pageParam = null;

casper.start(pageUrl);
casper.waitFor(function check() {
    return this.evaluate(function () {
        return (document.querySelectorAll('.gt_cut_bg_slice').length == 52)&&(document.querySelectorAll('.gt_cut_fullbg_slice').length == 52);
    });
}, function then() {
    this.echo("页面渲染成功!");
    this.echo(this.getPageContent());
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

var deltaX =50;
casper.then(function () {
    this.echo("开始请求滑块位置.......");
    //this.echo(JSON.stringify(pageParam));
    var result = casper.evaluate(function (url, param) {
        return JSON.parse(__utils__.sendAJAX(url, 'POST', param, false));
    }, deltaResolveServer, {"params": JSON.stringify(pageParam)});
    if (result.status == 1) deltaX = result.data.deltaX;
    else {
        this.echo("请求滑块位置失败！");
        this.exit();
    }
});
casper.then(function () {
    this.mouseEvent("click", ".gt_slider_knob","568","180");
    this.echo("开始移动滑块,目标位移为  " + deltaX);
    this.evaluate(function (selector, deltaX) {
        var createEvent = function (eventName, ofsx, ofsy) {
            var evt = document.createEvent('MouseEvents');
            evt.initMouseEvent(eventName, true, false, null, 0, 0, 0, ofsx, ofsy, false, false, false, false, 0, null);
            return evt;
        };
        var deltaArray =
            [[-26, -24, 0], [26, 24, 0], [0, 0, 4], [2, 1, 167], [0, 1, 7], [1, 0, 8], [2, 0, 9], [2, 0, 15], [2, 0, 9], [3, 0, 7], [3, 0, 9], [3, 0, 7], [2, 0, 10], [2, 0, 7], [1, 0, 8], [1, 0, 8], [1, 0, 16], [2, 0, 11], [2, 0, 6], [4, 0, 10], [3, 0, 5], [5, 0, 8], [4, 0, 8], [3, 0, 8], [3, 0, 8], [2, 0, 7], [4, -1, 9], [2, 0, 7], [3, 0, 9], [4, -1, 7], [2, 0, 9], [2, 0, 7], [1, 0, 9], [2, 0, 7], [1, 0, 9], [1, 0, 17], [1, 0, 6], [2, 0, 9], [1, 0, 7], [2, 0, 10], [1, 0, 6], [1, 0, 12], [1, 0, 5], [2, 0, 8], [3, 0, 8], [3, 0, 9], [5, 0, 7], [5, 0, 7], [3, 0, 9], [4, 0, 7], [2, 0, 9], [2, 0, 7], [1, 0, 16], [1, 0, 9], [1, 0, 15], [1, 0, 8], [1, 0, 9], [2, 0, 7], [4, 0, 9], [2, 0, 7], [3, 0, 9], [2, 0, 7], [2, 0, 9], [2, 0, 8], [2, 0, 8], [1, 0, 8], [2, -1, 8], [1, 0, 23], [2, 0, 12], [1, 0, 4], [1, 0, 9], [2, 0, 8], [2, 0, 7], [1, -1, 9], [3, -2, 8], [1, 0, 8], [1, 0, 7], [1, 0, 9], [2, 0, 15], [3, 0, 16], [3, 0, 9], [3, 0, 8], [2, 0, 7], [3, -1, 9], [2, 0, 8], [1, 0, 39], [1, 0, 16], [1, 0, 9], [1, 0, 8], [1, 0, 15], [1, 0, 9], [1, 0, 23], [1, 0, 16], [1, 0, 24], [1, 0, 40], [2, 0, 88], [1, 0, 81], [1, 0, 111], [1, 0, 67], [1, -1, 37], [2, 0, 8], [-1, 0, 1281], [-1, 0, 87], [-2, 1, 16], [1, 0, 321], [1, 0, 87], [1, 0, 49], [0, 0, 616], [0, 0, 616], [0, 0, 616], [0, 0, 616], [0, 0, 616]];

        var delta = deltaX>200?200:deltaX;//要移动的距离
        var obj = $(selector)[0];
        var startX = obj.getBoundingClientRect().left + 20;
        var startY = obj.getBoundingClientRect().top + 18;

        console.log("startX:" + startX);
        console.log("startY:" + startY);

        var nowX = startX;
        var nowY = startY;
        var moveToTarget = function (loopRec) {
            setTimeout(function () {
                nowX = nowX + deltaArray[loopRec][0];
                nowY = nowY + deltaArray[loopRec][1];
                console.log(loopRec + "次移动滑块");
                obj.dispatchEvent(createEvent('mousemove', nowX, nowY));
                console.log("当前滑块位置:" + obj.getBoundingClientRect().left);
                if (nowX > (startX + delta - 1)) {
                    obj.dispatchEvent(createEvent('mouseup', nowX, nowY));
                    console.log("滑块移动结束");
                    console.log("最终滑块位置:" + obj.getBoundingClientRect().left);
                } else {
                    moveToTarget(loopRec + 1);
                }
            }, deltaArray[loopRec][2]);
        };
        obj.dispatchEvent(createEvent("mousedown", startX, startY));
        moveToTarget(2);
    }, ".gt_slider_knob", deltaX);
}).then(function () {
    casper.waitForSelector('.gt_info_type', function () {
        this.echo("验证结果:" + this.fetchText('.gt_info_type'));
        this.echo(this.getPageContent());
    }, function () {
        this.echo("等待滑块移动超时！");
    }, 20000);
});
casper.run();