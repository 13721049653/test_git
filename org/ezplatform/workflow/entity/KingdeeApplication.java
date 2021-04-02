package org.ezplatform.workflow.entity;

public class KingdeeApplication {
	
	
	private String formid;
	private String data;
	
	
	public KingdeeApplication(String formid, String data) {
		super();
		this.formid = formid;
		this.data = data;
	}
	public String getFormid() {
		return formid;
	}
	public void setFormid(String formid) {
		this.formid = formid;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	
}
