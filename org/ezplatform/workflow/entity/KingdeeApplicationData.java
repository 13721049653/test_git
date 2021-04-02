package org.ezplatform.workflow.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * @author WEI
 *ERP推送 data 数据结构
 */
public class KingdeeApplicationData {
	@JSONField(name="NeedUpDateFields")
	private String [] NeedUpDateFields;
	@JSONField(name="IsDeleteEntry")
	private String IsDeleteEntry;
	@JSONField(name="IsVerifyBaseDataField")
	private String IsVerifyBaseDataField;
	@JSONField(name="IsEntryBatchFill")
	private String IsEntryBatchFill;
	@JSONField(name="ValidateFlag")
	private String ValidateFlag;
	@JSONField(name="NumberSearch")
	private String NumberSearch;
	@JSONField(name="IsAutoSubmitAndAudit")
	private boolean IsAutoSubmitAndAudit;
	@JSONField(name="Model")
	private JSONObject Model;
	private String tableName;
	
	private String recordId;
	
	public KingdeeApplicationData(String isDeleteEntry, String isVerifyBaseDataField, String isEntryBatchFill,
			String validateFlag, String numberSearch, JSONObject model,String tableName,String recordId) {
		super();
		IsDeleteEntry = isDeleteEntry;
		IsVerifyBaseDataField = isVerifyBaseDataField;
		IsEntryBatchFill = isEntryBatchFill;
		ValidateFlag = validateFlag;
		NumberSearch = numberSearch;
		Model = model;
		IsAutoSubmitAndAudit=false;
		this.tableName=tableName;
		this.recordId=recordId;
	}
	
	public KingdeeApplicationData(String isDeleteEntry, String isVerifyBaseDataField, String isEntryBatchFill,
			String validateFlag, String numberSearch, JSONObject model) {
		super();
		IsDeleteEntry = isDeleteEntry;
		IsVerifyBaseDataField = isVerifyBaseDataField;
		IsEntryBatchFill = isEntryBatchFill;
		ValidateFlag = validateFlag;
		NumberSearch = numberSearch;
		Model = model;
		IsAutoSubmitAndAudit=false;
	}

	
	
	public KingdeeApplicationData(String [] needUpDateFields, String isDeleteEntry, String isVerifyBaseDataField, String isEntryBatchFill,
			String validateFlag, String numberSearch, JSONObject model,String tableName,String recordId) {
		super();
		NeedUpDateFields = needUpDateFields;
		IsDeleteEntry = isDeleteEntry;
		IsVerifyBaseDataField = isVerifyBaseDataField;
		IsEntryBatchFill = isEntryBatchFill;
		ValidateFlag = validateFlag;
		NumberSearch = numberSearch;
		IsAutoSubmitAndAudit=false;
		Model = model;
		this.tableName=tableName;
		this.recordId=recordId;
	}

	public String getIsDeleteEntry() {
		return IsDeleteEntry;
	}

	public void setIsDeleteEntry(String isDeleteEntry) {
		IsDeleteEntry = isDeleteEntry;
	}

	public String getIsVerifyBaseDataField() {
		return IsVerifyBaseDataField;
	}

	public void setIsVerifyBaseDataField(String isVerifyBaseDataField) {
		IsVerifyBaseDataField = isVerifyBaseDataField;
	}

	public String getIsEntryBatchFill() {
		return IsEntryBatchFill;
	}

	public void setIsEntryBatchFill(String isEntryBatchFill) {
		IsEntryBatchFill = isEntryBatchFill;
	}

	public String getValidateFlag() {
		return ValidateFlag;
	}

	public void setValidateFlag(String validateFlag) {
		ValidateFlag = validateFlag;
	}

	public String getNumberSearch() {
		return NumberSearch;
	}

	public void setNumberSearch(String numberSearch) {
		NumberSearch = numberSearch;
	}

	public JSONObject getModel() {
		return Model;
	}

	public void setModel(JSONObject model) {
		Model = model;
	}

	public boolean isIsAutoSubmitAndAudit() {
		return IsAutoSubmitAndAudit;
	}

	public void setIsAutoSubmitAndAudit(boolean isAutoSubmitAndAudit) {
		IsAutoSubmitAndAudit = isAutoSubmitAndAudit;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String [] getNeedUpDateFields() {
		return NeedUpDateFields;
	}

	public void setNeedUpDateFields(String [] needUpDateFields) {
		NeedUpDateFields = needUpDateFields;
	}
	
	
	
	
	
	
	
}
