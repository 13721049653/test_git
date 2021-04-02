package org.ezplatform.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.ezplatform.core.annotation.MetaData;
import org.ezplatform.core.entity.BaseNativeEntity;

@MetaData("推送失败记录")
@Entity(name="FlowBuesinessFail")
@Table(name="oa_flowBuesinessfail", uniqueConstraints={@javax.persistence.UniqueConstraint(columnNames={"id"})})
public class FlowBuesinessFail extends BaseNativeEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@MetaData("推送类型id")
  	@Column(name="flowBId_id", nullable=false, length=32)
	//0 单据数据推送 1 附件与单据管理数据
  	private String flowBId;
	
	@MetaData("erp数据包")
  	@Column(name="data", nullable=false, length=4000)
	//erp 数据
	private String data;
	
	@MetaData("erp表单Id")
  	@Column(name="formid", nullable=false, length=100)
	private String formid;
	
	@MetaData("流程实例id")
  	@Column(name="processInstanceId", nullable=false, length=100)
	private String processInstanceId;
	
	
	@MetaData("返回json串")
  	@Column(name="responseJson", nullable=false, length=100)
	private String responseJson;
	

	@MetaData("调用接口地址")
  	@Column(name="kingdeeUrl", nullable=false, length=255)
	private String kingdeeUrl;
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getFormid() {
		return formid;
	}
	public void setFormid(String formid) {
		this.formid = formid;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public String getKingdeeUrl() {
		return kingdeeUrl;
	}
	public void setKingdeeUrl(String kingdeeUrl) {
		this.kingdeeUrl = kingdeeUrl;
	}
	public String getFlowBId() {
		return flowBId;
	}
	public void setFlowBId(String flowBId) {
		this.flowBId = flowBId;
	}
	public String getResponseJson() {
		return responseJson;
	}
	public void setResponseJson(String responseJson) {
		this.responseJson = responseJson;
	}
	
	
	
	
}
