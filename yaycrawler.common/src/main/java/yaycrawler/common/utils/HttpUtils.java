package yaycrawler.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import yaycrawler.common.model.RestFulResult;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class HttpUtils {

    private static RestTemplate restTemplate;

    static {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(10000);
        requestFactory.setConnectTimeout(5000);
        // 添加转换器
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        messageConverters.add(new FormHttpMessageConverter());
//        messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        restTemplate = new RestTemplate(messageConverters);
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    }

    public static <T> T postForObject(String url, Object params, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//        JSONObject jsonObj = JSONObject.fromObject(params);
        HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect), headers);
        return (T) restTemplate.postForObject(url, formEntity, responseType);
    }

    public static RestFulResult postForResult(String url, Object params) {
        try {
            return postForObject(url, params, RestFulResult.class);
        } catch (Exception ex) {
            return RestFulResult.failure(ex.getMessage());
        }
    }
    public static  RestFulResult doSignedHttpExecute(String secret,String targetUrl, HttpMethod method, Object data) {
        targetUrl = UrlUtils.generateSignaturedUrl(targetUrl, secret);
        int tryCount = 3;
        RestFulResult result = null;
        while (tryCount > 0) {
            if (HttpMethod.POST == method)
                result = HttpUtils.postForResult(targetUrl, data);
            if (result!=null&&!result.hasError()) break;
            tryCount--;
        }
        return result;
    }

}

