package org.ezplatform.travel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.ezplatform.core.annotation.MetaData;
import org.ezplatform.core.entity.BaseNativeEntity;

@MetaData("住宿补贴标准")
@Entity(name="TravelZhusuEntity")
@Table(name="gdqzsbz", uniqueConstraints={@javax.persistence.UniqueConstraint(columnNames={"id"})})
public class TravelZhusuEntity extends BaseNativeEntity{

	private static final long serialVersionUID = 1L;

	@MetaData("地区分类")
  	@Column(name="dqfl")
  	private String dqfl;
	
	@MetaData("一般员工住宿标准")
  	@Column(name="ybyg")
	private String ybyg;
	
	@MetaData("一级部门负责人及专业序列5级以上员工")
  	@Column(name="ybbmfzrjzyxl5jys")
	private String middleyg;
	
	@MetaData("分管EMT住宿标准")
  	@Column(name="gsemtcy")
	private String gsemtcy;

	public String getDqfl() {
		return dqfl;
	}

	public void setDqfl(String dqfl) {
		this.dqfl = dqfl;
	}

	public String getYbyg() {
		return ybyg;
	}

	public void setYbyg(String ybyg) {
		this.ybyg = ybyg;
	}

	public String getMiddleyg() {
		return middleyg;
	}

	public void setMiddleyg(String middleyg) {
		this.middleyg = middleyg;
	}

	public String getGsemtcy() {
		return gsemtcy;
	}

	public void setGsemtcy(String gsemtcy) {
		this.gsemtcy = gsemtcy;
	}
	
	
	
}
