package yaycrawler.common.model;

import yaycrawler.common.utils.UrlUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/12.
 */
public class CrawlerRequest implements Serializable{

    private String url;
    private String method;
    private Map data;
    private String domain;
    private Long startTime;
    private String hashCode;
    private String workerId;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public CrawlerRequest() {
    }

    public CrawlerRequest(String url, String domain, String method) {
        this.url = url;
        this.method = method;
        this.domain = domain;
    }
    public CrawlerRequest(String url, String method,Map data) {
        this.url = url;
        this.method = method;
        this.domain = UrlUtils.getDomain(url);
        this.data = data;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
