package yaycrawler.common.domain;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by yuananyun on 2016/5/1.
 */
@Entity
@Table(name = "conf_url_rule")
public class UrlParseRule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String regionId;

    @NotNull
    private String rule;
    public String method;

    public UrlParseRule() {
        method = "get";
    }

    public UrlParseRule(String rule) {
        this();
        this.rule = rule;
    }


    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
