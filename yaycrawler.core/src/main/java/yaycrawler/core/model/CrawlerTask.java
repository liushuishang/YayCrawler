package yaycrawler.core.model;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;
import java.util.UUID;

/**
 * Created by yuananyun on 2016/4/25.
 */
public class CrawlerTask {
    private String id;
    private String url;
    private String requestMethod;
    private Map<String, Object> requestParams;

    public CrawlerTask(String url, String requestMethod, Map<String, Object> requestParams) {
        this.id = UUID.randomUUID().toString();
        this.url = url;
        this.requestMethod = requestMethod;
        this.requestParams = requestParams;
    }


    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(url);
        builder.append(requestMethod);
        builder.append(JSON.toJSONString(requestParams));
        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        return "CrawlerTask{" +
                "url='" + url + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", requestParams=" + JSON.toJSONString(requestParams) +
                '}';
    }

    public String getId() {
        return id;
    }

}
