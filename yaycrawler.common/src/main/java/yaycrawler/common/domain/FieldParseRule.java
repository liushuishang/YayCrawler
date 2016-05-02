package yaycrawler.common.domain;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by yuananyun on 2016/5/1.
 */
@Entity
@Table(name="conf_field_rule")
public class FieldParseRule implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;

    private String regionId;

    @NotNull
    @Column(name="fieldName")
    private String fieldName;
    @NotNull
    @Column(name="rule")
    private String rule;
    @Column(name="valueType")
    private String valueType;

    public FieldParseRule() {
        valueType = "string";
    }

    public FieldParseRule(String fieldName, String rule) {
        this();
        this.fieldName = fieldName;
        this.rule = rule;

    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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
}
