package yaycrawler.common.model;

/**
 * Created by ucs_yuananyun on 2016/6/23.
 */
public class PhantomCookie {

    private String domain;
    private String expires;
    private Long expiry;
    private Boolean httponly;
    private String name;
    private String path;
    private Boolean secure;
    private String value;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public Boolean getHttponly() {
        return httponly;
    }

    public void setHttponly(Boolean httponly) {
        this.httponly = httponly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
