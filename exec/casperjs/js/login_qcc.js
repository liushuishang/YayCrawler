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

var pageUrl = "http://www.qichacha.com/user_login";
//casper.cli.get(0);
var deltaResolveServer = casper.cli.get(1);
var id = casper.cli.get(2);
var username = casper.cli.get(3);
var password = casper.cli.get(4);
var pageParam = null;

casper.start(pageUrl).then(function () {
    this.wait(5000, function () {
        //this.echo("等待5秒以便页面充分渲染");
    });
});
casper.then(function () {
    if (!this.exists(".gt_slider_knob")) {
        this.echo("页面中不存在极验验证码模块");
        this.exit();
    }
});
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

var deltaX = 0;
casper.then(function () {
    if (pageParam == null) {
        this.echo("收集图片参数失败!");
        this.exit();
    }
    this.echo("开始请求滑块位置");
    var result = casper.evaluate(function (url, param) {
        return JSON.parse(__utils__.sendAJAX(url, 'POST', param, false));
    }, deltaResolveServer, {"params": JSON.stringify(pageParam)});
    if (result != null && result.status == 1) {
        deltaX = result.data.deltaX;
        this.echo("滑块位置求解成功:" + JSON.stringify(result.data));
    }
    else {
        this.echo("请求滑块位置失败:" + JSON.stringify(result));
        this.exit();
    }
});
var currentTrailIndex = 0;
casper.then(function () {
    if (deltaX <= 0) {
        this.echo("滑块目标位移为0:处理失败");
        this.exit();
    }
    this.echo("开始移动滑块,目标位移为  " + deltaX);
    currentTrailIndex = this.evaluate(function (selector, deltaX) {
        var createEvent = function (eventName, ofsx, ofsy) {
            var evt = document.createEvent('MouseEvents');
            evt.initMouseEvent(eventName, true, false, null, 0, 0, 0, ofsx, ofsy, false, false, false, false, 0, null);
            return evt;
        };
        var trailArray = [
            [[-21, -20, 0], [21, 20, 0], [1, 1, 305], [1, 0, 96], [1, 1, 56], [1, 0, 15], [2, 0, 17], [4, 0, 17], [2, 0, 7], [0, 1, 9], [2, 0, 6], [1, 0, 17], [1, 0, 8], [1, 0, 7], [2, 0, 9], [2, 1, 8], [2, 0, 8], [3, 0, 7], [3, 0, 16], [2, 1, 9], [2, 1, 7], [3, 0, 9], [1, 0, 7], [3, 0, 10], [1, 0, 6], [1, 1, 12], [1, 0, 12], [1, 0, 33], [1, 0, 7], [3, 0, 16], [2, 0, 9], [2, 0, 8], [1, 0, 8], [2, 0, 8], [1, 1, 31], [2, 0, 97], [2, 1, 7], [1, 0, 17], [2, 0, 8], [1, 0, 8], [2, 0, 7], [2, 0, 9], [1, 0, 7], [3, 0, 9], [2, 0, 8], [2, 0, 8], [3, 0, 8], [3, 2, 8], [1, 0, 7], [2, 1, 9], [1, 0, 57], [1, 0, 7], [2, 0, 8], [2, 0, 8], [3, 0, 8], [2, 0, 8], [1, 0, 9], [1, 0, 7], [1, 0, 8], [2, 0, 8], [1, 0, 8], [1, 0, 16], [1, 0, 23], [1, 0, 9], [1, 0, 7], [1, 0, 16], [1, 0, 9], [1, 0, 7], [2, 0, 9], [1, 0, 7], [1, 0, 9], [1, 0, 7], [1, 0, 9], [1, 0, 184], [1, 0, 7], [1, 0, 17], [3, 1, 7], [2, 0, 8], [2, 0, 16], [2, 1, 8], [1, 0, 48], [1, 0, 8], [1, 0, 9], [1, 0, 9], [1, 0, 7], [2, 0, 8], [1, 0, 7], [1, 0, 24], [1, 0, 8], [0, 1, 16], [2, 0, 11], [1, 0, 13], [1, 0, 16], [1, 0, 10], [1, 0, 15], [3, 0, 8], [1, 1, 8], [1, 0, 7], [1, 0, 9], [1, 0, 8], [1, 0, 8], [1, 0, 15], [1, 0, 8], [1, 0, 9], [2, 0, 7], [2, 0, 9], [2, 0, 7], [1, 0, 9], [1, 0, 8], [1, 0, 8], [1, 0, 7], [2, 0, 9], [2, 0, 25], [1, 0, 14], [1, 0, 9], [1, 0, 15], [0, 1, 9], [1, 1, 15], [1, 0, 41], [1, 0, 7], [1, 0, 16], [2, 0, 24], [1, 0, 16], [1, 0, 9], [1, 0, 80], [2, 0, 17], [1, 0, 7], [1, 0, 8], [1, 1, 8], [2, 0, 8], [1, 0, 55], [2, 0, 185], [1, 0, 39], [1, 0, 40], [1, 0, 16], [1, 0, 80], [2, 0, 192], [1, 0, 64], [0, 0, 402]],
            [[-20, -20, 0], [20, 20, 0], [1, 0, 770], [1, 0, 8], [1, 0, 10], [1, 0, 6], [1, 0, 40], [1, 0, 16], [1, 0, 16], [1, 0, 8], [1, 0, 40], [1, -1, 24], [3, 0, 8], [2, -1, 15], [1, 0, 9], [1, 0, 7], [1, 0, 9], [1, 0, 64], [1, 0, 8], [2, 0, 9], [2, -1, 9], [1, 0, 6], [2, 0, 8], [2, 0, 8], [1, 0, 8], [3, 0, 8], [2, -2, 8], [1, 0, 8], [1, 0, 8], [1, 0, 16], [2, 0, 132], [2, 0, 1], [2, 0, 4], [1, 0, 7], [1, 0, 8], [1, 0, 8], [1, 0, 8], [2, 0, 8], [2, 0, 8], [2, 0, 16], [1, 0, 8], [1, 0, 17], [1, 0, 7], [1, 0, 8], [1, 0, 8], [1, 0, 8], [1, 0, 16], [2, 0, 16], [1, 0, 8], [1, 0, 16], [1, 0, 8], [1, 0, 8], [2, 0, 8], [3, 0, 8], [1, 0, 16], [1, 0, 8], [1, -1, 8], [2, 0, 8], [2, -1, 8], [4, 0, 8], [2, 0, 16], [2, 0, 8], [1, 0, 8], [2, 0, 16], [1, 0, 16], [2, 0, 8], [1, 0, 16], [2, 0, 8], [2, 0, 8], [1, -1, 8], [4, -1, 8], [1, 0, 8], [1, 0, 8], [1, 0, 8], [1, 0, 8], [1, 0, 16], [1, 0, 8], [2, 0, 9], [1, 0, 7], [1, 0, 8], [2, 0, 7], [1, 0, 9], [1, 0, 8], [2, 0, 8], [2, 0, 8], [1, 0, 8], [1, -1, 8], [2, 0, 8], [1, 0, 16], [2, 0, 8], [1, 0, 8], [2, 0, 8], [1, 0, 8], [2, 0, 16], [1, 0, 16], [2, 0, 128], [1, 0, 29], [1, 0, 2], [2, 0, 11], [1, 0, 6], [1, 0, 16], [1, 0, 40], [1, 0, 16], [1, -1, 9], [1, 0, 8], [1, 0, 15], [2, 0, 8], [1, 0, 8], [1, 0, 8], [1, 0, 24], [1, -1, 8], [2, 0, 145], [1, 0, 16], [2, 0, 24], [1, 0, 10], [2, 0, 7], [1, 0, 8], [2, 0, 8], [1, 0, 9], [1, 0, 6], [3, 0, 8], [1, 0, 8], [2, 0, 64], [1, 0, 8], [1, 0, 16], [1, 0, 16], [1, 0, 72], [2, 0, 16], [1, 0, 19], [2, 0, 13], [1, 0, 17], [1, 0, 55], [1, 0, 48], [1, 0, 32], [2, 0, 17], [1, 0, 16], [1, 0, 7], [1, 0, 32], [1, 0, 32], [2, 0, 24], [1, 0, 16], [1, 0, 1024], [1, 0, 88], [0, 0, 464]],
            [[-19, -22, 0], [19, 22, 0], [1, 0, 634], [1, 0, 232], [1, 0, 9], [1, 0, 7], [1, 0, 16], [2, 0, 33], [1, 0, 7], [1, 0, 9], [1, 1, 7], [1, 0, 63], [2, 0, 16], [2, 0, 9], [1, 0, 15], [2, 0, 16], [1, 0, 9], [1, 0, 39], [1, 0, 8], [1, 0, 16], [1, 0, 9], [1, 0, 8], [1, 0, 7], [1, 0, 16], [1, 0, 9], [1, 0, 103], [1, 0, 16], [2, 0, 16], [2, 0, 16], [1, 0, 16], [2, 0, 8], [1, 0, 56], [1, 0, 8], [1, 0, 17], [1, 0, 7], [1, 0, 17], [1, 0, 136], [1, 1, 8], [1, 0, 9], [3, 0, 23], [1, 0, 9], [1, 0, 8], [1, 0, 15], [1, 0, 21], [1, 0, 12], [1, 0, 22], [2, 0, 34], [1, 0, 7], [1, 0, 7], [1, 0, 16], [2, 0, 9], [1, 0, 7], [1, 0, 9], [2, 0, 15], [1, 0, 9], [1, 0, 15], [1, 0, 8], [1, 0, 16], [1, 0, 9], [1, 0, 7], [2, 0, 9], [2, 0, 7], [1, 0, 9], [1, 0, 7], [1, 0, 16], [1, 0, 16], [1, 0, 9], [1, 0, 15], [2, 0, 16], [2, 0, 8], [2, 0, 17], [1, 0, 8], [1, 0, 8], [1, 0, 7], [2, 0, 9], [2, 0, 7], [1, 0, 9], [1, 0, 15], [1, 0, 16], [1, 0, 16], [2, 0, 8], [1, 0, 9], [2, 0, 7], [3, 0, 9], [1, -1, 7], [1, 0, 9], [1, 0, 15], [2, 0, 16], [1, 0, 16], [1, 0, 9], [1, 0, 8], [1, 0, 15], [1, 0, 16], [1, 0, 9], [1, 0, 15], [2, 0, 16], [1, 0, 24], [2, 0, 9], [1, 0, 7], [1, 0, 9], [1, 0, 7], [1, 0, 9], [2, 0, 7], [1, 0, 9], [3, 0, 24], [3, 0, 1], [2, 0, 13], [1, 0, 1], [1, 0, 8], [3, -1, 8], [1, 0, 8], [1, 0, 16], [1, 0, 8], [1, 0, 16], [1, -1, 16], [1, 0, 16], [1, 0, 16], [1, 0, 8], [1, 0, 8], [1, 0, 24], [3, 0, 8], [1, 0, 8], [2, 0, 9], [2, 0, 9], [1, 0, 7], [1, 0, 16], [1, 0, 80], [1, 0, 16], [1, 0, 31], [1, 0, 25], [1, 0, 16], [2, 0, 15], [1, 0, 9], [1, 0, 8], [1, 0, 7], [1, 0, 17], [1, 0, 56], [1, 0, 104], [2, 0, 7], [1, 0, 145], [1, 0, 104], [1, 0, 16], [1, 0, 208], [0, 0, 176]]
        ];
        //修改为随机取一条轨迹
        var trailIndex = Math.round(Math.random() * (trailArray.length - 1));
        var deltaArray = trailArray[trailIndex];
        console.log('当前使用轨迹路径:' + (trailIndex + 1));

        var delta = deltaX - 8;//要移动的距离,减掉2是为了防止过拟合导致验证失败
        delta = delta > 200 ? 200 : delta;
        //查找要移动的对象
        var obj = document.querySelector(selector);
        var startX = obj.getBoundingClientRect().left + 20;
        var startY = obj.getBoundingClientRect().top + 18;
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
                console.log("当前滑块位置:" + obj.getBoundingClientRect().left);
                if (nowX > (startX + delta - 2)) {
                    obj.dispatchEvent(createEvent('mousemove', startX + delta, nowY));
                    obj.dispatchEvent(createEvent('mouseup', startX + delta, nowY));
                    console.log("最终滑块位置:" + obj.getBoundingClientRect().left);
                } else {
                    moveToTarget(loopRec + 1);
                }
            }, deltaArray[loopRec][2]);
        };
        obj.dispatchEvent(createEvent("mousedown", startX, startY));
        moveToTarget(2);
        return trailIndex;
    }, ".gt_slider_knob", deltaX);
}).then(function () {
    casper.waitForSelectorTextChange('.gt_info_type', function () {
        var status = this.fetchText('.gt_info_type');
        this.echo("验证结果:" + status);
        this.capture(status.replace(":","_")+ id + "_" + currentTrailIndex + '.png');
        if (status.indexOf("通过") > -1) {
            this.fillSelectors('form#user_login_normal', {
                'input[name="nameNormal"]': username,
                'input[name="pwdNormal"]':password
            }, true);
        }
    }, function () {
        this.echo("等待滑块移动超时！");
        this.exit();
    }, 10000);
}).then(function() {
    this.waitForSelector('li.nav-user', function() {
        //this.echo(this.getPageContent());
        this.echo("自动登录成功！");
        this.echo("$CookieStart");
        this.echo(JSON.stringify(phantom.cookies));
        this.echo("$CookieEnd");
        this.exit();
    },function() {
        //this.echo(this.getPageContent());
        this.echo("自动登录失败！");
        this.exit();
    },10000);
});

casper.run();