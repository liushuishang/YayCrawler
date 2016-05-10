package yaycrawler.common.domain;

import com.alibaba.fastjson.JSON;
import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * 表示一个待抓取的页面信息
 * Created by ucs_yuananyun on 2016/5/10.
 */
@Entity
@Table(name = "conf_page_info", uniqueConstraints = {@UniqueConstraint(columnNames = {"pageUrl"})})
public class PageInfo {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull
    @Column(name = "pageUrl")
    private String pageUrl;

    @NotNull
    @Column(name = "method", columnDefinition = "varchar(10) default 'GET' ")
    private String method;

    @Column(name = "paramsJson", columnDefinition = "varchar(500)")
    private String paramsJson;


    @Column(name = "createdDate", columnDefinition = "timestamp default now()")
    private Date createdDate;

    @Transient
    private Map<String, Object> paramsMap;

    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "pageId", insertable = false, updatable = false)
    private List<PageParseRegion> pageParseRegionList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParamsJson() {
        return paramsJson;
    }

    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
//        paramsMap = JSON.parseObject(paramsJson, Map.class);
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
        paramsJson = JSON.toJSONString(paramsMap);
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public List<PageParseRegion> getPageParseRegionList() {
        return pageParseRegionList;
    }

    public void setPageParseRegionList(List<PageParseRegion> pageParseRegionList) {
        this.pageParseRegionList = pageParseRegionList;
    }
}
