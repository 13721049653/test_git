package org.ezplatform.travel.entity;

import java.math.BigDecimal;

public class ButieEntity {

	private BigDecimal chailvbutie;//差旅补贴
	private BigDecimal zhusubutie;//住宿费补贴
	private BigDecimal jiankudiqubutie;//艰苦地区补贴
	private String sfbg;
	private String  msg; //提醒
	
	
	public ButieEntity() {
		super();
	}

	public ButieEntity(BigDecimal chailvbutie, BigDecimal zhusubutie, BigDecimal jiankudiqubutie, String msg,String sfbg) {
		super();
		this.sfbg=sfbg;
		this.chailvbutie = chailvbutie;
		this.zhusubutie = zhusubutie;
		this.jiankudiqubutie = jiankudiqubutie;
		this.msg = msg;
	}
	
	public BigDecimal getChailvbutie() {
		return chailvbutie;
	}
	public void setChailvbutie(BigDecimal chailvbutie) {
		this.chailvbutie = chailvbutie;
	}
	public BigDecimal getZhusubutie() {
		return zhusubutie;
	}
	public void setZhusubutie(BigDecimal zhusubutie) {
		this.zhusubutie = zhusubutie;
	}
	public BigDecimal getJiankudiqubutie() {
		return jiankudiqubutie;
	}
	public void setJiankudiqubutie(BigDecimal jiankudiqubutie) {
		this.jiankudiqubutie = jiankudiqubutie;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSfbg() {
		return sfbg;
	}
	public void setSfbg(String sfbg) {
		this.sfbg = sfbg;
	}
	
	
	
	
}
