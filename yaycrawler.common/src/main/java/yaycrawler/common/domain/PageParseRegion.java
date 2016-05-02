package yaycrawler.common.domain;


import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuananyun on 2016/5/1.
 */
@Entity
@Table(name = "conf_page_region")
public class PageParseRegion implements Serializable {


    private String id;
    private String name;
    private String pageUrl;
    /**
     * 区域的css选择表达式
     */
    private String CSSSelector;
    private List<FieldParseRule> fieldParseRules;
    private List<UrlParseRule> urlParseRules;

    @Column(name = "createdDate",columnDefinition = "timestamp default now()")
    private Date createdDate;

    public PageParseRegion() {
        fieldParseRules = new LinkedList<>();
        urlParseRules = new LinkedList<>();
    }

    public PageParseRegion(String name, String pageUrl, String CSSSelector) {
        this();
        this.name = name;
        this.pageUrl = pageUrl;
        this.CSSSelector = CSSSelector;
    }

    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "regionId", insertable = false, updatable = false)
    public List<FieldParseRule> getFieldParseRules() {
        return fieldParseRules;
    }

    public void setFieldParseRules(List<FieldParseRule> fieldParseRules) {
        this.fieldParseRules = fieldParseRules;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "regionId", insertable = false, updatable = false)
    public List<UrlParseRule> getUrlParseRules() {
        return urlParseRules;
    }

    public void setUrlParseRules(List<UrlParseRule> urlParseRules) {
        this.urlParseRules = urlParseRules;
    }

    @NotNull
    @Column(name = "pageUrl")
    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    @NotNull
    @Column(name = "cssSelector")
    public String getCSSSelector() {
        return CSSSelector;
    }

    public void setCSSSelector(String CSSSelector) {
        this.CSSSelector = CSSSelector;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
