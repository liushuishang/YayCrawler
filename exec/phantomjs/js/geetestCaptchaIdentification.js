"use strict";

/**
 * 等待一个测试条件为真或者超时，主要用于等待一个服务端响应
 * @param testFx 自定义的测试函数，需要返回一个布尔值
 * @param onReady 定义测试函数满足时要做的事情
 * @param timeOutMillis 超时时间
 */
function waitFor(testFx, onReady, timeOutMillis) {
    var maxtimeOutMillis = timeOutMillis ? timeOutMillis : 3000,
        start = new Date().getTime(),
        condition = false,
        interval = setInterval(function () {
            if ((new Date().getTime() - start < maxtimeOutMillis) && !condition) {
                condition = (typeof(testFx) === "string" ? eval(testFx) : testFx()); //< defensive code
            } else {
                if (!condition) {
                    console.log("'waitFor()' timeout");
                    phantom.exit(1);
                } else {
                    console.log("'waitFor()' finished in " + (new Date().getTime() - start) + "ms.");
                    typeof(onReady) === "string" ? eval(onReady) : onReady();
                    clearInterval(interval);
                }
            }
        }, 250);
};

var captcha = {
    move: function (selector, deltaX) {
        var createEvent = function (eventName, ofsx, ofsy) {
            var evt = document.createEvent('MouseEvents');
            evt.initMouseEvent(eventName, true, false, null, 0, 0, 0, ofsx, ofsy, false, false, false, false, 0, null);
            return evt;
        };
        var getDeltaArray = function () {
            var a = [[-23, -23, 0], [0, 0, 0], [1, 0, 151], [4, 0, 158], [6, 0, 167], [8, 0, 175], [11, 0, 183], [13, 0, 191], [14, 0, 199], [17, 0, 207], [20, 0, 215], [23, -1, 224], [25, -1, 231], [28, -1, 241], [30, -1, 247], [32, -1, 255], [33, -2, 262], [34, -2, 271], [35, -2, 286], [36, -2, 303], [38, -3, 318], [39, -3, 342], [40, -3, 374], [41, -3, 398], [42, -3, 414], [43, -3, 438], [44, -3, 446], [45, -3, 462], [46, -3, 470], [47, -3, 478], [48, -3, 486], [50, -3, 494], [51, -3, 502], [53, -3, 518], [54, -3, 526], [57, -4, 534], [58, -4, 542], [60, -4, 551], [64, -6, 559], [65, -6, 567], [67, -6, 575], [68, -6, 583], [69, -6, 592], [70, -6, 599], [71, -6, 607], [73, -7, 614], [74, -7, 623], [75, -8, 630], [78, -8, 639], [80, -8, 646], [83, -8, 655], [86, -9, 662], [88, -9, 678], [89, -9, 694], [91, -9, 703], [92, -9, 710], [94, -9, 719], [96, -9, 726], [98, -9, 735], [100, -9, 742], [103, -9, 750], [105, -9, 759], [107, -9, 766], [109, -9, 776], [110, -9, 782], [112, -9, 790], [113, -9, 805], [115, -9, 822], [116, -9, 838], [118, -9, 846], [119, -9, 854], [121, -9, 862], [122, -9, 870], [125, -9, 878], [126, -10, 894], [129, -10, 902], [130, -10, 918], [132, -10, 927], [133, -10, 935], [135, -10, 943], [137, -10, 950], [138, -10, 958], [140, -10, 966], [141, -10, 975], [142, -10, 982], [143, -10, 998], [144, -10, 1007], [146, -10, 1022], [147, -10, 1030], [148, -10, 1038], [149, -10, 1045], [151, -10, 1054], [152, -10, 1069], [153, -10, 1078], [154, -10, 1086], [156, -10, 1101], [157, -10, 1111], [158, -10, 1141], [159, -10, 1157], [160, -10, 1173], [161, -10, 1238], [162, -10, 1246], [163, -10, 1269], [164, -10, 1293], [165, -10, 1311], [166, -10, 1398], [167, -10, 1422], [168, -10, 1446], [169, -10, 1486], [171, -10, 1550], [171, -11, 1558], [172, -11, 1574], [174, -11, 1598], [175, -11, 1798], [175, -11, 2182]];
            var deltaArray = [];
            var prevPoint = [0, 0, 0];
            for (var i = 0; i < a.length; i++) {
                var pointDelta = [];
                pointDelta[0] = a[i][0] - prevPoint[0];
                pointDelta[1] = a[i][1] - prevPoint[1];
                pointDelta[2] = a[i][2] - prevPoint[2];
                deltaArray.push(pointDelta);
                prevPoint = a[i];
            }
            return deltaArray;
        };
        var delta = deltaX;//要移动的距离
        var obj = $(selector)[0];
        var startX = obj.getBoundingClientRect().left + 20;
        var startY = obj.getBoundingClientRect().top + 18;
        var nowX = startX;
        var nowY = startY;

        var deltaArray = getDeltaArray();
        var moveToTarget = function (loopRec) {
            setTimeout(function () {
                nowX = nowX + deltaArray[loopRec][0];
                nowY = nowY + deltaArray[loopRec][1];
                obj.dispatchEvent(createEvent('mousemove', nowX, nowY));
                if (nowX > (startX + delta - 2)) {
                    obj.dispatchEvent(createEvent('mouseup', nowX, nowY));
                } else {
                    moveToTarget(loopRec + 1);
                }
            }, deltaArray[loopRec][2]);
        };
        obj.dispatchEvent(createEvent("mousedown", startX, startY));
        moveToTarget(2);
    }
};


var system = require('system');
var pageUrl = system.args[1];
var javaApiAddress = system.args[2];//java 接口的地址
var page = require('webpage').create();

//打开验证码页面开始处理
page.open(pageUrl, function (status) {
    if (status !== "success") {
        console.log("Unable to access network");
    } else {
        waitFor(function () {
            //判断验证码控件是否渲染成功
            return page.evaluate(function () {
                return $(".gt_slider_knob") != undefined;
            });
        }, function () {
            /**
             * 开始验证码解锁
             */
            // 1、请求java结果获取图块的移动位移(我们这里只需要考虑x坐标即可)
            var data = {};
            data.fullbg = "";//完整的拼图素材地址，是一张打乱内容的图片
            data.bg = "";//带凹块的拼图素材地址，是一张打乱了内容的图片
            data.itemCount = 52;//完整拼图包含小图片的个数
            data.lineItemCount = 26;//拼图中每行包含的小图片个数
            var apiPage = require('webpage').create();
            apiPage.open(javaApiAddress, 'post', data, function (status) {
                if (status !== 'success') {
                    console.log('Java接口请求失败!');
                } else {
                    console.log(apiPage.content);
                    var result = JSON.parse(apiPage.content);
                    var deltaX = result.data;//求解出来的x的位移量
                    // 2、模拟验证码拖动
                    captcha.move(".gt_slider_knob", deltaX);
                }
            });
        }, 3000);
    }
    phantom.exit(1);
});








