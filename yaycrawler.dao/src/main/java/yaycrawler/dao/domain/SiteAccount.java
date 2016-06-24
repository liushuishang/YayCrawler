package yaycrawler.dao.domain;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by ucs_guoguibiao on 6/24 0024.
 */
@Entity
@Table(name = "res_site_account")
public class SiteAccount {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull
    @Column(name = "userName", columnDefinition = "varchar(100)")
    private String userName;

    @NotNull
    @Column(name = "pwd", columnDefinition = "varchar(100)")
    private String pwd;

    @NotNull
    @Column(columnDefinition = "varchar(38)")
    private String siteId;

    @NotNull
    private String domain;

    @NotNull
    @Column(name = "available",insertable = false,columnDefinition = "char default '1'")
    private String  available;

    @Column(name = "createdDate", columnDefinition = "timestamp default now()")
    private Date createdDate;

    @Column(name = "lastUpdatedDate", columnDefinition = "date default now()")
    private Date lastUpdatedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
