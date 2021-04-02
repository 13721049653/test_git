package org.ezplatform.travel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.ezplatform.core.annotation.MetaData;
import org.ezplatform.core.entity.BaseNativeEntity;

/**
 * 包干标准
 * @author WEI
 *
 */
@MetaData("包干补贴")
@Entity(name="TravelBaoganEntity")
@Table(name="ygbxbgbt", uniqueConstraints={@javax.persistence.UniqueConstraint(columnNames={"id"})})
public class TravelBaoganEntity extends BaseNativeEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@MetaData("城市等级")
  	@Column(name="dqfl")
  	private String dqfl;
	
	@MetaData("报销标准")
  	@Column(name="bgbz")
  	private String bgbz;

	public String getDqfl() {
		return dqfl;
	}

	public void setDqfl(String dqfl) {
		this.dqfl = dqfl;
	}

	public String getBgbz() {
		return bgbz;
	}

	public void setBgbz(String bgbz) {
		this.bgbz = bgbz;
	}

	
	
	
}
