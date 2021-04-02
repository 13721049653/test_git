package org.ezplatform.sync.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class OrgBaseInfo {
  private static final long serialVersionUID = -1798070786993154676L;

  private String orgName; //组织名称
  
  private String orgSimpleName;//组织简称

  private int sortNo;//排序

  private String orgCode;//组织编码

  private String orgUnitCode;//组织机构代码

  private String orgType;//组织类型

  private int isDelete;

  private String corpStatus;

  private String parentId;//上级组织Id

  private String orgLeaders;//组织领导id

  private String userLeaders;//上级领导

  private String deputyLeaders;//分管领导

  private int isSysInit; //初始化标识 默认0



  private String orgNamePath;

  private String orgSimpleNamePath;

  private String orgIdPath;

  private int orgLevel;


  private String parentCode;

  private String superCode;//上级组织编码
  @JSONField(name="ULLoginName")
  private String ULLoginName;//上级领导登录名
  @JSONField(name="OLLoginName")
  private String OLLoginName;//组织领导登录名
  @JSONField(name="DLLoginName")
  private String DLLoginName;//分管领导登录名

  private String superName;//所属组织
  @JSONField(name="ULUserName")
  private String ULUserName;//
  @JSONField(name="OLUserName")
  private String OLUserName;
  @JSONField(name="DLUserName")
  private String DLUserName;

  private String sortNoStr;

  private String corpId;
public String getOrgName() {
	return orgName;
}

public void setOrgName(String orgName) {
	this.orgName = orgName;
}

public String getOrgSimpleName() {
	return orgSimpleName;
}

public void setOrgSimpleName(String orgSimpleName) {
	this.orgSimpleName = orgSimpleName;
}

public int getSortNo() {
	return sortNo;
}

public void setSortNo(int sortNo) {
	this.sortNo = sortNo;
}

public String getOrgCode() {
	return orgCode;
}

public void setOrgCode(String orgCode) {
	this.orgCode = orgCode;
}

public String getOrgUnitCode() {
	return orgUnitCode;
}

public void setOrgUnitCode(String orgUnitCode) {
	this.orgUnitCode = orgUnitCode;
}

public String getOrgType() {
	return orgType;
}

public void setOrgType(String orgType) {
	this.orgType = orgType;
}

public int getIsDelete() {
	return isDelete;
}

public void setIsDelete(int isDelete) {
	this.isDelete = isDelete;
}

public String getCorpStatus() {
	return corpStatus;
}

public void setCorpStatus(String corpStatus) {
	this.corpStatus = corpStatus;
}

public String getParentId() {
	return parentId;
}

public void setParentId(String parentId) {
	this.parentId = parentId;
}

public String getOrgLeaders() {
	return orgLeaders;
}

public void setOrgLeaders(String orgLeaders) {
	this.orgLeaders = orgLeaders;
}

public String getUserLeaders() {
	return userLeaders;
}

public void setUserLeaders(String userLeaders) {
	this.userLeaders = userLeaders;
}

public String getDeputyLeaders() {
	return deputyLeaders;
}

public void setDeputyLeaders(String deputyLeaders) {
	this.deputyLeaders = deputyLeaders;
}

public int getIsSysInit() {
	return isSysInit;
}

public void setIsSysInit(int isSysInit) {
	this.isSysInit = isSysInit;
}

public String getOrgNamePath() {
	return orgNamePath;
}

public void setOrgNamePath(String orgNamePath) {
	this.orgNamePath = orgNamePath;
}

public String getOrgSimpleNamePath() {
	return orgSimpleNamePath;
}

public void setOrgSimpleNamePath(String orgSimpleNamePath) {
	this.orgSimpleNamePath = orgSimpleNamePath;
}

public String getOrgIdPath() {
	return orgIdPath;
}

public void setOrgIdPath(String orgIdPath) {
	this.orgIdPath = orgIdPath;
}

public int getOrgLevel() {
	return orgLevel;
}

public void setOrgLevel(int orgLevel) {
	this.orgLevel = orgLevel;
}

public String getParentCode() {
	return parentCode;
}

public void setParentCode(String parentCode) {
	this.parentCode = parentCode;
}

public String getSuperCode() {
	return superCode;
}

public void setSuperCode(String superCode) {
	this.superCode = superCode;
}

public String getULLoginName() {
	return ULLoginName;
}

public void setULLoginName(String uLLoginName) {
	ULLoginName = uLLoginName;
}

public String getOLLoginName() {
	return OLLoginName;
}

public void setOLLoginName(String oLLoginName) {
	OLLoginName = oLLoginName;
}

public String getDLLoginName() {
	return DLLoginName;
}

public void setDLLoginName(String dLLoginName) {
	DLLoginName = dLLoginName;
}

public String getSuperName() {
	return superName;
}

public void setSuperName(String superName) {
	this.superName = superName;
}

public String getULUserName() {
	return ULUserName;
}

public void setULUserName(String uLUserName) {
	ULUserName = uLUserName;
}

public String getOLUserName() {
	return OLUserName;
}

public void setOLUserName(String oLUserName) {
	OLUserName = oLUserName;
}

public String getDLUserName() {
	return DLUserName;
}

public void setDLUserName(String dLUserName) {
	DLUserName = dLUserName;
}

public String getSortNoStr() {
	return sortNoStr;
}

public void setSortNoStr(String sortNoStr) {
	this.sortNoStr = sortNoStr;
}

public String getCorpId() {
	return corpId;
}

public void setCorpId(String corpId) {
	this.corpId = corpId;
}


  
}