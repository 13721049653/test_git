package org.ezplatform.model.formmgr.runtime.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class KeyValueDto implements Serializable
{

    @JSONField(ordinal=1)
    private String key;

    @JSONField(ordinal=2)
    private Object value;

    @JSONField(ordinal=3)
    private String showValue;

    public String getKey()
    {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public Object getValue() {
        return this.value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public String getShowValue() {
        return this.showValue;
    }
    public void setShowValue(String showValue) {
        this.showValue = showValue;
    }
}