package yaycrawler.dao.domain;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

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
    @Column(name = "sleepTime", columnDefinition = "int default 500")
    private Integer sleepTime;
    @Column(name = "retryTimes", columnDefinition = "int default 3")
    private int retryTimes;
    @Column(name = "cycleRetryTimes", columnDefinition = "int default 1")
    private int cycleRetryTimes;
    @Column(name = "timeOut", columnDefinition = "int default 10000")
    private Integer timeOut;
    @Column(name = "headers", columnDefinition = "varchar(1000)")
    private String headers;

    /**
     * 是否需要登录判断表达式
     */
    @Column(name = "loginJudgeExpression", columnDefinition = "varchar(200)")
    private String loginJudgeExpression;
    /**
     * 验证码页面判断表达式
     */
    @Column(name = "captchaJudgeExpression", columnDefinition = "varchar(200)")
    private String captchaJudgeExpression;

    /**
     * 自动登录Js脚本文件名称
     */
    @Column(name = "loginJsFileName", columnDefinition = "varchar(200)")
    private String loginJsFileName;
    /**
     * 验证码识别Js脚本文件名称
     */
    @Column(name = "captchaJsFileName", columnDefinition = "varchar(200)")
    private String captchaJsFileName;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "siteId", insertable = false, updatable = false)
    private Set<SiteCookie> cookieList;


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


    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }


    public String getCaptchaJsFileName() {
        return captchaJsFileName;
    }

    public void setCaptchaJsFileName(String captchaJsFileName) {
        this.captchaJsFileName = captchaJsFileName;
    }

    public String getLoginJudgeExpression() {
        return loginJudgeExpression;
    }

    public void setLoginJudgeExpression(String loginJudgeExpression) {
        this.loginJudgeExpression = loginJudgeExpression;
    }

    public String getCaptchaJudgeExpression() {
        return captchaJudgeExpression;
    }

    public void setCaptchaJudgeExpression(String captchaJudgeExpression) {
        this.captchaJudgeExpression = captchaJudgeExpression;
    }

    public String getLoginJsFileName() {
        return loginJsFileName;
    }

    public void setLoginJsFileName(String loginJsFileName) {
        this.loginJsFileName = loginJsFileName;
    }

    public Set<SiteCookie> getCookieList() {
        return cookieList;
    }

    public void setCookieList(Set<SiteCookie> cookieList) {
        this.cookieList = cookieList;
    }

    public Integer getSleepTime() {
        return sleepTime==null?0:sleepTime;
    }

    public void setSleepTime(Integer sleepTime) {
        this.sleepTime = sleepTime;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }
}
