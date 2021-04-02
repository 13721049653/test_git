package org.ezplatform.model.formmgr.runtime.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

public class SubTblDataDto implements Serializable
{

    @JSONField(ordinal=1)
    private String subTblName;

    @JSONField(ordinal=2)
    private List<List<KeyValueDto>> subTblData;

    public String getSubTblName()
    {
        return this.subTblName;
    }

    public void setSubTblName(String subTblName)
    {
        this.subTblName = subTblName;
    }

    public List<List<KeyValueDto>> getSubTblData()
    {
        return this.subTblData;
    }

    public void setSubTblData(List<List<KeyValueDto>> subTblData)
    {
        this.subTblData = subTblData;
    }
}