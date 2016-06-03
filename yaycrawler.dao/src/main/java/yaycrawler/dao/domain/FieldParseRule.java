package yaycrawler.dao.domain;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Created by yuananyun on 2016/5/1.
 */
@Entity
@Table(name = "conf_field_rule")
public class FieldParseRule implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull
    @Column(columnDefinition = "varchar(38) not null")
    private String regionId;
    @NotNull
    @Column(name = "fieldName")
    private String fieldName;

    @Column(name = "remark", columnDefinition = "varchar(100)")
    private String remark;
    @NotNull
    @Column(name = "rule")
    private String rule;
    @Column(name = "valueType")
    private String valueType;
    @Column(name = "createdDate", columnDefinition = "timestamp default now()")
    private Date createdDate;

    public FieldParseRule() {
        valueType = "string";
    }

    public FieldParseRule(String fieldName, String rule) {
        this();
        setFieldName(fieldName);
        this.rule = rule;

    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        if (fieldName == null) fieldName = "";
        this.fieldName = fieldName.replace(".", "_");
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
