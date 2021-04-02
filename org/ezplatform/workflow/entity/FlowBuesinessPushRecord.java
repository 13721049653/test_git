package org.ezplatform.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.ezplatform.core.annotation.MetaData;
import org.ezplatform.core.entity.BaseNativeEntity;

@MetaData("推送失败记录")
@Entity(name="FlowBuesinessPushRecord")
@Table(name="oa_flowbuesinesspushrecord", uniqueConstraints={@javax.persistence.UniqueConstraint(columnNames={"id"})})
public class FlowBuesinessPushRecord extends BaseNativeEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@MetaData("流程实例id")
  	@Column(name="processInstanceId", nullable=false, length=100)
	private String processInstanceId;


	public String getProcessInstanceId() {
		return processInstanceId;
	}


	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	
	
	
	
}
