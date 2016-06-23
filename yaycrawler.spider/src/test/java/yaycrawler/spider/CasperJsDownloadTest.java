package yaycrawler.spider;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.spider.resolver.SelectorExpressionResolver;

import java.io.*;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ucs_guoguibiao on 6/20 0020.
 */
public class CasperJsDownloadTest {

    private static Logger logger = LoggerFactory.getLogger(SelectorExpressionResolver.class);
    private static Pattern INVOKE_PATTERN = Pattern.compile("(\\w+)\\((.*)\\)");

    @Test
    public void testDownload() throws UnsupportedEncodingException {

        Process p = null;//这里我的codes.js是保存在c盘下面的phantomjs目录
        String path ="E:/workspace/YayCrawler/exec/";
//        path = path.substring(1,path.lastIndexOf("/")+1);
        String progam =  path + "casperjs/bin/casperjs.exe";
        /**
         * 程序地址：
         * 执行功能的js
         * 域名
         * cookie
         */
//        Properties prop = System.getProperties();
//        String os = prop.getProperty("os.name");
//        if(StringUtils.startsWithIgnoreCase(os,"win")) {
//            progam =  path + "phantomjs/window/phantomjs";
//        } else {
//            progam =  path + "phantomjs/linux/phantomjs";
//        }
        String cookies = "gr_user_id=66513266-0f48-418d-8896-c58998768dce; PHPSESSID=2n2rhgsrp9lc8fsejit00fjdl4; SERVERID=b7e4e7feacd29b9704e39cfdfe62aefc|1466592968|1466591401; gr_session_id_9c1eb7420511f8b2=41fd218e-94e3-4d41-9cd5-b18a9ed181f7; CNZZDATA1254842228=179135660-1465949699-http%253A%252F%252Fwww.qichacha.com%252F%7C1466591249".replaceAll(" ", "%20");
        String expression = "css(\"a.dropdown-toggle:last-child\")".replaceAll(":eq\\(",":nth-child(").replaceAll(" ", "%20");
        String[] invokeArray = expression.split("\\)\\.");
        for (int i = 0; i < invokeArray.length; i++) {
            String invokeStr = invokeArray[i];
            if (!invokeStr.endsWith(")")) invokeStr += ")";

            Matcher matcher = INVOKE_PATTERN.matcher(invokeStr);
            if (matcher.find()) {
                String methodName = matcher.group(1);
                if (StringUtils.isBlank(methodName)) continue;

                String[] paramArray = null;
                String param = matcher.group(2);
                if (param != null) {
//                    param = param.replaceAll("\"([^\"]*)\"", "$1");//去掉双引号
                    paramArray = param.split("\\$\\$");
                }
                if (paramArray == null) {
                    paramArray = new String[1];
                    paramArray[0] = param;
                }
                if(StringUtils.indexOfAny(methodName.toLowerCase(),new String[]{"xpath","css"}) > -1) {
                    ProcessBuilder processBuilder = new ProcessBuilder(progam,"casperjsDownload.js","http://www.qichacha.com/search?key=%E5%B0%8F%E7%B1%B3",methodName,URLEncoder.encode(paramArray[0],"utf-8"),"www.qichacha.com",URLEncoder.encode(cookies,"utf-8"));
                    processBuilder.directory(new File(path+"phantomjs/js"));
                    try {
                        p = processBuilder.start();
                        InputStream is = p.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                        StringBuffer sbf = new StringBuffer();
                        String tmp = "";
                        while ((tmp = br.readLine()) != null) {
                            sbf.append(tmp);
                        }
                        logger.info(sbf.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                    logger.info("开始执行函数:{},参数为:{}", methodName, JSON.toJSONString(paramArray));
                }
            }
        }


    }


}
