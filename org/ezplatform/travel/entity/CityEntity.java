package org.ezplatform.travel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.ezplatform.core.annotation.MetaData;
import org.ezplatform.core.entity.BaseNativeEntity;

@MetaData("城市等级表")
@Entity(name="CityEntity")
@Table(name="csdjhf", uniqueConstraints={@javax.persistence.UniqueConstraint(columnNames={"id"})})
public class CityEntity extends BaseNativeEntity{

	@MetaData("城市名称")
  	@Column(name="MC")
  	private String name;
	
	@MetaData("父级城市")
  	@Column(name="FJ")
  	private String parentCity;
	
	@MetaData("城市级别")
  	@Column(name="CSJB")
  	private String cityLevel;
	
	@MetaData("是否偏远地区")
  	@Column(name="SFPYDQ")
  	private String sfpydq;
	
	
	@MetaData("是否艰苦地区")
  	@Column(name="SFJKDQ")
  	private String sfjkdq;
	


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getParentCity() {
		return parentCity;
	}


	public void setParentCity(String parentCity) {
		this.parentCity = parentCity;
	}


	public String getCityLevel() {
		return cityLevel;
	}


	public void setCityLevel(String cityLevel) {
		this.cityLevel = cityLevel;
	}


	public String getSfpydq() {
		return sfpydq;
	}


	public void setSfpydq(String sfpydq) {
		this.sfpydq = sfpydq;
	}


	public String getSfjkdq() {
		return sfjkdq;
	}


	public void setSfjkdq(String sfjkdq) {
		this.sfjkdq = sfjkdq;
	}
	

	
	
	
}
