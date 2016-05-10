package yaycrawler.common.utils;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
        String domain = removeProtocol(url);
        int i = StringUtils.indexOf(domain, "/", 1);
        if (i > 0) {
            domain = StringUtils.substring(domain, 0, i);
        }
        return domain;
    }

    public static String removeProtocol(String url) {
        return patternForProtocal.matcher(url).replaceAll("");
    }


    public static Request createRequest(String url, String method, Map<String, Object> paramsMap) {
        if (StringUtils.isBlank(url)) return null;

        Request request = new Request(url);
        String requestMethod = method.toUpperCase();
        request.setMethod(requestMethod);
        if (HttpConstant.Method.POST.equals(requestMethod))
            request.putExtra("nameValuePair", paramsMap);
        else if (paramsMap != null && paramsMap.size() > 0) {
            StringBuilder urlBuilder = new StringBuilder(url);
            for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                try {
                    urlBuilder.append(String.format("%s%s=%s",  urlBuilder.indexOf("?") > 0 ? "&" : "?", entry.getKey(), URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            request.setUrl(urlBuilder.toString());
        }
        return request;
    }

}
