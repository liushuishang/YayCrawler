package yaycrawler.dao.domain;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
@Entity
@Table(name = "res_site_cookie")
public class SiteCookie {

    public SiteCookie() {
    }

    public SiteCookie(String siteId,String domain, String cookie) {
        this.siteId = siteId;
        this.domain = domain;
        this.cookie = cookie;
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull
    @Column(name = "cookie", columnDefinition = "text")
    private String cookie;

    @NotNull
    @Column(columnDefinition = "varchar(38)")
    private String siteId;

    @NotNull
    private String domain;

    @NotNull
    @Column(name = "available",insertable = false,columnDefinition = "char default '1'")
    private String  available;

    @Column(name = "createdDate", columnDefinition = "timestamp default now()")
    private Timestamp createdDate;

    @Column(name = "lastUpdatedDate", columnDefinition = "timestamp default now()")
    private Timestamp lastUpdatedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }


    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
