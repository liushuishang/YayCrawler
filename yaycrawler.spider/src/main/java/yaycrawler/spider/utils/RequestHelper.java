package yaycrawler.spider.utils;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/12.
 */
public class RequestHelper {

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
                    if(StringUtils.isEmpty(entry.getKey())) {
                        urlBuilder.append(String.format("%s/%s/%s", "/", entry.getKey(), URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8")));
                    } else
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
