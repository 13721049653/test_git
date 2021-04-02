package org.ezplatform.model.formmgr.runtime.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

public class RuntimeDataDto implements Serializable
{

    @JSONField(ordinal=1)
    private String formId;

    @JSONField(ordinal=2)
    private String dataId;

    @JSONField(ordinal=3)
    private String mainTblName;

    @JSONField(ordinal=4)
    private List<KeyValueDto> mainTblData;

    @JSONField(ordinal=5)
    private List<SubTblDataDto> subTbl;

    @JSONField(ordinal=6)
    private List<RelatedTblDataDto> relatedTbl;

    @JSONField(ordinal=7)
    private boolean isTrans;

    @JSONField(ordinal=8)
    private String moduleCode;

    @JSONField(ordinal=9)
    private String paramJson;

    public String getFormId()
    {
        return this.formId;
    }

    public void setFormId(String formId)
    {
        this.formId = formId;
    }

    public String getDataId()
    {
        return this.dataId;
    }

    public void setDataId(String dataId)
    {
        this.dataId = dataId;
    }

    public String getMainTblName()
    {
        return this.mainTblName;
    }

    public void setMainTblName(String mainTblName)
    {
        this.mainTblName = mainTblName;
    }

    public List<KeyValueDto> getMainTblData()
    {
        return this.mainTblData;
    }

    public void setMainTblData(List<KeyValueDto> mainTblData)
    {
        this.mainTblData = mainTblData;
    }

    public List<SubTblDataDto> getSubTbl()
    {
        return this.subTbl;
    }

    public void setSubTbl(List<SubTblDataDto> subTbl)
    {
        this.subTbl = subTbl;
    }

    public List<RelatedTblDataDto> getRelatedTbl()
    {
        return this.relatedTbl;
    }

    public void setRelatedTbl(List<RelatedTblDataDto> relatedTbl)
    {
        this.relatedTbl = relatedTbl;
    }

    public boolean isTrans() {
        return this.isTrans;
    }

    public void setTrans(boolean isTrans) {
        this.isTrans = isTrans;
    }

    public String getModuleCode() {
        return this.moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getParamJson() {
        return this.paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }

    public String toString()
    {
        String strTemp = "RuntimeDataDto [formId=" + this.formId + ", dataId=" + this.dataId + ", mainTblName=" + this.mainTblName +
                ", mainTblData=" + this.mainTblData + ", subTbl=" + this.subTbl + ", relatedTbl=" + this.relatedTbl + ", isTrans=" +
                this.isTrans + ", moduleCode=" + this.moduleCode + ", paramJson=" + this.paramJson + "]";

        System.out.println("strTemp===========>>>>"+strTemp);
        return "RuntimeDataDto [formId=" + this.formId + ", dataId=" + this.dataId + ", mainTblName=" + this.mainTblName +
                ", mainTblData=" + this.mainTblData + ", subTbl=" + this.subTbl + ", relatedTbl=" + this.relatedTbl + ", isTrans=" +
                this.isTrans + ", moduleCode=" + this.moduleCode + ", paramJson=" + this.paramJson + "]";
    }
}