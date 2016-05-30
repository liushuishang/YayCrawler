package yaycrawler.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by yuananyun on 2016/5/1.
 */
public class UrlUtils {

    private static Pattern patternForProtocal = Pattern.compile("[\\w]+://");

    public static String getContextPath(String url) {
        if (StringUtils.isBlank(url)) return url;

        int index = url.indexOf("?");
        return index > 0 ? url.substring(0, index) : url;
    }

    public static String getDomain(String url) {
        try {
            String domain = removeProtocol(url);
            int i = StringUtils.indexOf(domain, "/", 1);
            if (i > 0) {
                domain = StringUtils.substring(domain, 0, i);
            }
            return domain;
        } catch (Exception ex) {
            return null;
        }
    }


    /**
     * 生成附件前面参数的Url
     *
     * @param url
     * @param secret
     * @return
     */
    public static String generateSignaturedUrl(String url, String secret) {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(secret)) return url;

        String nonce = CharacterUtils.getRandomString(8);
        long timestamp = System.currentTimeMillis();

        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("url", url);
        paramMap.put("timestamp", timestamp);
        paramMap.put("nonce", nonce);
        paramMap.put("secret", secret);

        String signature = SignatureUtils.signWithSHA1(paramMap);

        if (url.contains("?")) url += "&nonce=" + nonce;
        else url += "?nonce=" + nonce;
        url += "&timestamp=" + timestamp;
        url += "&signature=" + signature;

        return url;
    }


    public static String removeProtocol(String url) {
        return patternForProtocal.matcher(url).replaceAll("");
    }


}
