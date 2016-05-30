package yaycrawler.dao.domain;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ucs_yuananyun on 2016/5/30.
 */
@Entity
@Table(name = "conf_url_param")
public class UrlRuleParam implements Serializable {

    public UrlRuleParam() {
    }

    public UrlRuleParam(String urlRuleId, String paramName, String expression) {
        this.urlRuleId = urlRuleId;
        this.paramName = paramName;
        this.expression = expression;
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull
    @Column(columnDefinition = "varchar(38)")
    private String urlRuleId;

    @NotNull
    @Column(columnDefinition = "varchar(100)")
    private String paramName;

    @NotNull
    @Column(columnDefinition = "varchar(500)")
    private String expression;

//    @Column(name = "createdDate", columnDefinition = "timestamp default now()")
//    private Date createdDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlRuleId() {
        return urlRuleId;
    }

    public void setUrlRuleId(String urlRuleId) {
        this.urlRuleId = urlRuleId;
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}
