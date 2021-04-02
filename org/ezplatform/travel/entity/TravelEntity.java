package org.ezplatform.travel.entity;

import java.math.BigDecimal;

public class TravelEntity {
	/**
	 * 补贴信息
	 */
	private String name; 
	private String userLevel; //用户级别
	private String starTime;//出发时间
	private String endTime;//离开时间
	private String arriveTime;//到达时间
	private float days;//出差天数
	private float jkdqDays;//艰苦地区天数
	private float  butiejine;//补贴金额
	private String cityLevel;//城市等级
	private String cityName;//达到城市
	private String chufaCity;//出发城市
	private boolean oneday;//是否为同一天
	private float zhusudays;//住宿天数
	private boolean sfzd;//是否终点
	private boolean sfqd;//是否起点
	
	
	
	
	
	public TravelEntity() {
		super();
		this.sfzd=false;
		this.sfqd=false;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserLevel() {
		return userLevel;
	}
	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}
	public String getStarTime() {
		return starTime;
	}
	public void setStarTime(String starTime) {
		this.starTime = starTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public float getDays() {
		return days;
	}
	public void setDays(float days) {
		this.days = days;
	}
	public float  getButiejine() {
		return butiejine;
	}
	public void setButiejine(float  butiejine) {
		this.butiejine = butiejine;
	}
	
	
	public float getJkdqDays() {
		return jkdqDays;
	}
	public void setJkdqDays(float jkdqDays) {
		this.jkdqDays = jkdqDays;
	}
	public String getCityLevel() {
		return cityLevel;
	}
	public void setCityLevel(String cityLevel) {
		this.cityLevel = cityLevel;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getChufaCity() {
		return chufaCity;
	}
	public void setChufaCity(String chufaCity) {
		this.chufaCity = chufaCity;
	}
	public boolean getOneday() {
		return oneday;
	}
	public void setOneday(boolean oneday) {
		this.oneday = oneday;
	}
	public float getZhusudays() {
		return zhusudays;
	}
	public void setZhusudays(float zhusudays) {
		this.zhusudays = zhusudays;
	}
	public boolean getSfzd() {
		return sfzd;
	}
	public void setSfzd(boolean sfzd) {
		this.sfzd = sfzd;
	}
	public boolean getSfqd() {
		return sfqd;
	}
	public void setSfqd(boolean sfqd) {
		this.sfqd = sfqd;
	}
	public String getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}
	
	
	
	
	
}
