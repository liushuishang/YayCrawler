package yaycrawler.monitor.captcha;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.spider.downloader.PhantomJsMockDonwnloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 极验验证码识别
 * Created by ucs_yuananyun on 2016/6/14.
 */
public class GeetestCaptchaIdentification {
    private static PhantomJsMockDonwnloader downloader = new PhantomJsMockDonwnloader();
    private static Pattern param_pattern = Pattern.compile("background-image: url\\((.*?)\\); background-position: (.*?)px (.*?)px;");

    private static Logger logger = LoggerFactory.getLogger(GeetestCaptchaIdentification.class);

//    public boolean process(String pageUrl) {
//        if (StringUtils.isBlank(pageUrl)) return false;
//        Page page = downloader.download(new Request(pageUrl), new Task() {
//            @Override
//            public String getUUID() {
//                return UUID.randomUUID().toString();
//            }
//
//            @Override
//            public Site getSite() {
//                return Site.me();
//            }
//        });
//        if (page == null) return false;
//
//        Html context = page.getHtml();
//        String id = context.regex("id=\"geetest_(.*?)\"").get();
//        String imageSubfix = "jpg";
//
//        List<String> fullbgSmallSrcList = context.css(".gt_cut_fullbg_slice", "style").all();
//        List<String> fullbgImageSrcList = new LinkedList<>();
//        List<Point> fullbgImagePointList = new LinkedList<>();
//        for (String fullbgStr : fullbgSmallSrcList) {
//            Matcher matcher = param_pattern.matcher(fullbgStr);
//            if (matcher.find()) {
//                fullbgImageSrcList.add(matcher.group(1));
//                fullbgImagePointList.add(new Point(Integer.parseInt(matcher.group(2).trim()), Integer.parseInt(matcher.group(3).trim())));
//            }
//        }
//
//        List<String> bgImageSrcList = new LinkedList<>();
//        List<Point> bgImagePointList = new LinkedList<>();
//        List<String> bgSmallSrcList = context.css(".gt_cut_bg_slice", "style").all();
//        for (String bgStr : bgSmallSrcList) {
//            Matcher matcher = param_pattern.matcher(bgStr);
//            if (matcher.find()) {
//                bgImageSrcList.add(matcher.group(1));
//                bgImagePointList.add(new Point(Integer.parseInt(matcher.group(2).trim()), Integer.parseInt(matcher.group(3).trim())));
//            }
//        }
//        String tmpFolder = System.getProperty("java.io.tmpdir");
//        if (fullbgImageSrcList.size() > 0 && bgImageSrcList.size() > 0) {
//            //下载合并图片
//            String fullbgSavePath = String.format("%s%s.%s", tmpFolder, id + "_fullbg", imageSubfix);
//            if (!yaycrawler.common.utils.ImageUtils.combineImages(fullbgImageSrcList, fullbgImagePointList, 26, 10, 58, fullbgSavePath, imageSubfix))
//                return false;
//            String bgSavePath = String.format("%s%s.%s", tmpFolder, id + "_bg", imageSubfix);
//            if (!yaycrawler.common.utils.ImageUtils.combineImages(bgImageSrcList, bgImagePointList, 26, 10, 58, bgSavePath, imageSubfix))
//                return false;
//
//            //计算出拖动小图块的区域坐标
//            Rectangle rectangle = ImageUtils.findDiffRectangeOfTwoImage(fullbgSavePath, bgSavePath);
//            //移动小图标
//            moveDomToTargetPoint(pageUrl, ".gt_slice", (int) rectangle.getX(), (int) rectangle.getY());
//        } else return false;
//
//        return true;
//    }

    /**
     * 验证码自动识别
     * @param pageUrl 包含验证码的节目
     * @param deltaResolveAddress 能够解析验证码移动位移的服务地址
     * @return
     */
    private boolean process(String pageUrl,String deltaResolveAddress) {
        if (pageUrl == null ) return false;
        Process p = null;
        logger.info("路径 {}" + JSON.toJSONString(this.getClass().getResource("/")));
        String path = this.getClass().getResource("/").getPath();
        path = path.substring(1, path.lastIndexOf("/") + 1);
        String progam = "";
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if (StringUtils.startsWithIgnoreCase(os, "win")) {
            progam = path + "phantomjs/window/phantomjs";
        } else {
            progam = path + "phantomjs/linux/phantomjs";
        }
        ProcessBuilder processBuilder = new ProcessBuilder(progam, "geetestCaptchaIdentification.js",
                pageUrl,deltaResolveAddress,"--debug=true","--ignore-ssl-errors=yes ","--web-security=no" );
//        ProcessBuilder processBuilder = new ProcessBuilder(progam, "phantomDownload.js", pageUrl,deltaResolveAddress);
        processBuilder.directory(new File(path + "phantomjs/js"));
        try {
            p = processBuilder.start();
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sbf = new StringBuffer();
            String tmp = "";
            while ((tmp = br.readLine()) != null) {
                sbf.append(tmp);
            }
            System.out.println(sbf.toString());

            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        GeetestCaptchaIdentification geetestCaptchaIdentification = new GeetestCaptchaIdentification();
        geetestCaptchaIdentification.process("http://user.geetest.com/login?url=http:%2F%2Faccount.geetest.com%2Freport","http://localhost:8086/worker/resolveGeetestSlicePosition");


    }

}
