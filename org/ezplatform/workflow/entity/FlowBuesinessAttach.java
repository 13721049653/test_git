package org.ezplatform.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.ezplatform.core.annotation.MetaData;
import org.ezplatform.core.entity.BaseNativeEntity;

@MetaData("附件上传失败记录")
@Entity(name="FlowBuesinessAttach")
@Table(name="oa_flowbuesinessattach", uniqueConstraints={@javax.persistence.UniqueConstraint(columnNames={"id"})})
public class FlowBuesinessAttach extends BaseNativeEntity{

	private static final long serialVersionUID = 1L;

	
	@MetaData("附件显示名称")
  	@Column(name="fileName", nullable=false, length=4000)
	private String fileName;
	
	@MetaData("附件显示名称")
  	@Column(name="fileSaveName", nullable=false, length=100)
	private String fileSaveName;
	
	@MetaData("附件显示路径")
  	@Column(name="filePath", nullable=false, length=1000)
	private String filePath;
	
	@MetaData("附件后缀")
  	@Column(name="extType", nullable=false, length=100)
	private String extType;
	
	@MetaData("上传contentType")
  	@Column(name="content_type", nullable=false, length=100)
	private String content_type;
	
	@MetaData("ERP单据formId")
  	@Column(name="formid", nullable=false, length=100)
	private String formid;
	
	@MetaData("ERP单据编号")
  	@Column(name="fbillno", nullable=false, length=100)
	private String fbillno;
	
	@MetaData("ERP单据保存id")
  	@Column(name="finterid", nullable=false, length=100)
	private String finterid;
	
	
	@MetaData("流程实例id")
  	@Column(name="processInstanceId", nullable=false, length=100)
	private String processInstanceId;
	
	@MetaData("返回json串")
  	@Column(name="responseJson", nullable=false, length=100)
	private String responseJson;
	

	@MetaData("调用接口地址")
  	@Column(name="kingdeeUrl", nullable=false, length=255)
	private String kingdeeUrl;


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getFileSaveName() {
		return fileSaveName;
	}


	public void setFileSaveName(String fileSaveName) {
		this.fileSaveName = fileSaveName;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public String getFormid() {
		return formid;
	}


	public void setFormid(String formid) {
		this.formid = formid;
	}


	public String getFbillno() {
		return fbillno;
	}


	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}


	public String getFinterid() {
		return finterid;
	}


	public void setFinterid(String finterid) {
		this.finterid = finterid;
	}


	public String getProcessInstanceId() {
		return processInstanceId;
	}


	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}


	public String getResponseJson() {
		return responseJson;
	}


	public void setResponseJson(String responseJson) {
		this.responseJson = responseJson;
	}


	public String getKingdeeUrl() {
		return kingdeeUrl;
	}


	public void setKingdeeUrl(String kingdeeUrl) {
		this.kingdeeUrl = kingdeeUrl;
	}


	public String getContent_type() {
		return content_type;
	}


	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}


	public String getExtType() {
		return extType;
	}


	public void setExtType(String extType) {
		this.extType = extType;
	}
	
	
	
	
}
