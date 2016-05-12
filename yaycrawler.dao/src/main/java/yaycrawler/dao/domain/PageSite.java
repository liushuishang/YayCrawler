package yaycrawler.dao.domain;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by yuananyun on 2016/5/2.
 */
@Entity
@Table(name = "conf_page_site")
public class PageSite implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;
    @NotNull
    @Column(name = "domain", nullable = false, length = 50)
    private String domain;
    @Column(name = "userAgent", columnDefinition = "varchar(255) ")
    private String userAgent;
    @Column(name = "defaultCookies", columnDefinition = "varchar(1000)")
    private String defaultCookies;
    @Column(name = "charset", columnDefinition = "varchar(10) default 'utf-8'")
    private String charset;
    @Column(name = "sleepTime", insertable = false, updatable = false)
    private Long sleepTime;
    @Column(name = "retryTimes", columnDefinition = "int default 3")
    private int retryTimes;
    @Column(name = "cycleRetryTimes", columnDefinition = "int default 1")
    private int cycleRetryTimes;
    @Column(name = "sleepTime", columnDefinition = "long default 10000")
    private Long timeOut;
    @Column(name = "headers", columnDefinition = "varchar(1000)")
    private String headers;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "siteId", insertable = false, updatable = false)
    private List<SiteCookie> cookieList;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDefaultCookies() {
        return defaultCookies;
    }

    public void setDefaultCookies(String defaultCookies) {
        this.defaultCookies = defaultCookies;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public void setCycleRetryTimes(int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
    }

    public Long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public List<SiteCookie> getCookieList() {
        return cookieList;
    }

    public void setCookieList(List<SiteCookie> cookieList) {
        this.cookieList = cookieList;
    }
}
