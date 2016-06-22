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
        String cookies = "ip_loc=44; view_m=1; PHPSESSID=23a71d7d2581588516d5c24961154087; save_jy_login_name=13216635314; upt=3VHVlWsta6Gke6ygNJPL4foHf7tjnIVQC0xpBBWHXUhjTwyJqiONKS2HS1Rb6F4dfHFwk7pTbOjAuhaMLSdICg..; SESSION_HASH=0d83e34b2cdb57fd444c0e7e8fe737f5a223fe99; user_access=1; sl_jumper=%26cou%3D17%26omsg%3D0%26dia%3D0%26lst%3D2016-06-21; last_login_time=1466560408; pclog=%7B%22152828725%22%3A%221466560375207%7C1%7C0%22%7D; IM_CS=1; IM_ID=5; pop_1436857144=1466561575282; IM_CON=%7B%22IM_TM%22%3A1466560375464%2C%22IM_SN%22%3A5%7D; IM_S=%7B%22IM_CID%22%3A7892540%2C%22IM_SV%22%3A%22211.151.166.133%22%2C%22svc%22%3A%7B%22code%22%3A0%2C%22nps%22%3A0%2C%22unread_count%22%3A%2274%22%2C%22ocu%22%3A0%2C%22ppc%22%3A0%2C%22jpc%22%3A0%2C%22regt%22%3A%221463975828%22%2C%22using%22%3A%2251%2C%22%2C%22user_type%22%3A%2210%22%2C%22uid%22%3A152828725%7D%2C%22m%22%3A0%2C%22f%22%3A0%2C%22omc%22%3A0%7D; IZ_bind152828725=0; stadate1=151828725; myloc=11%7C1103; myage=26; PROFILE=152828725%3ATUA%3Am%3Aa1.jyimg.com%2F6b%2Fcd%2Ff146c7606dce5ebf3f9d43316d48%3A1%3A%3A1%3Af146c7606_1_avatar_p.jpg%3A1%3A1%3A50%3A10; mysex=m; myuid=151828725; myincome=50; RAW_HASH=a9EYLQnTzoVus5SuhkbQLHGeBfemuluVBIYXx1mMBz0mC6qOPvKRCsaMeCKYDR70TH3P0V-p4NiF7RGllrZ8RbI3s9oPuDLU97WVQq4NMDfEiFc.; COMMON_HASH=6bf146c7606dce5ebf3f9d43316d48cd; IM_TK=1466560384234; IM_M=%5B%5D".replaceAll(" ", "%20");
        String expression = "xpath(/html/body/div[5]/div/div[1]/div[2]/ul/li[4]/div[2]/em)".replaceAll(":eq\\(",":nth-child(").replaceAll(" ", "%20");
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
                    ProcessBuilder processBuilder = new ProcessBuilder(progam,"casperjsDownload.js","http://www.jiayuan.com/4764431",methodName,URLEncoder.encode(paramArray[0],"utf-8"),"www.jiayuan.com",URLEncoder.encode(cookies,"utf-8"));
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
