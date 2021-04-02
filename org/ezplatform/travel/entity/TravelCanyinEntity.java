package org.ezplatform.travel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.ezplatform.core.annotation.MetaData;
import org.ezplatform.core.entity.BaseNativeEntity;

/**
 * 餐饮标准
 * @author WEI
 *
 */
@MetaData("餐饮标准")
@Entity(name="TravelCanyinEntity")
@Table(name="ygbxfbgbt", uniqueConstraints={@javax.persistence.UniqueConstraint(columnNames={"id"})})
public class TravelCanyinEntity extends BaseNativeEntity{


	private static final long serialVersionUID = 1L;

	@MetaData("地区分类")
  	@Column(name="dqfl")
  	private String dqfl;
	

	@MetaData("早餐标准")
  	@Column(name="zaocanbz")
  	private String zaocanbz;
	
	@MetaData("中餐标准")
  	@Column(name="zhongcanbz")
  	private String zhongcanbz;
	
	@MetaData("晚餐标准")
  	@Column(name="wancanbz")
  	private String wancanbz;

	public String getDqfl() {
		return dqfl;
	}

	public void setDqfl(String dqfl) {
		this.dqfl = dqfl;
	}

	public String getZaocanbz() {
		return zaocanbz;
	}

	public void setZaocanbz(String zaocanbz) {
		this.zaocanbz = zaocanbz;
	}

	public String getZhongcanbz() {
		return zhongcanbz;
	}

	public void setZhongcanbz(String zhongcanbz) {
		this.zhongcanbz = zhongcanbz;
	}

	public String getWancanbz() {
		return wancanbz;
	}

	public void setWancanbz(String wancanbz) {
		this.wancanbz = wancanbz;
	}
	
	
}
