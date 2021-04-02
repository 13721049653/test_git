package org.ezplatform.sync.dao;

import java.util.List;

import javax.annotation.Resource;

import org.ezplatform.sync.dao.mapper.OrgMapper;
import org.ezplatform.sync.dao.mapper.StationMapper;
import org.ezplatform.sync.dao.mapper.UserMapper;
import org.ezplatform.sync.entity.OrgInfo;
import org.ezplatform.sync.entity.StationInfo;
import org.ezplatform.sync.entity.UserInfo;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MiddleInfoDao {
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	public List<UserInfo> getAllUserInfo(){
		String querySQL = " SELECT STAFF.FNAME,STAFF.FUSERNAME,STAFF.FNUMBER,STAFF.FEMAIL,STAFF.FMOBILE,STAFF.FSFZH,STAFF.FXB,STAFF.FZW,STAFF.FZJSJNUMBER,STAFF.FZJSJNAME,STAFF.FZJNUMBER AS DUTYCODE,"
							+ " DEPT.FNUMBER AS DFNUMBER,DEPT.FNAME AS DFNAME,POST.FNUMBER AS PFNUMBER,POST.FNAME AS PFNAME,DEPT.FGLDGH,DEPT.BMFZRGH,DEPT.FGEMTNUMBER,DEPT.FGDSNUMBER FROM   XFSMZJK.T_KDXF_STAFF@toerp STAFF LEFT JOIN XFSMZJK.T_KDXF_POST@toerp POST ON STAFF.FZRGWNUMBER =POST.FNUMBER LEFT JOIN  XFSMZJK.T_KDXF_DEPARTMENT@toerp  DEPT ON POST.FDEPTNUMBER =DEPT.FNUMBER ";
		List<UserInfo> list = jdbcTemplate.query(querySQL, new UserMapper());
		return list;
	}
	
	public List<OrgInfo> getAllOrgInfo(){
		String querySQL = " SELECT FNUMBER ,FPNUMBER,FNAME,FPNAME,BMFZRGH,BMFZRNAME,FGLDGH,FGLDNAME,FGEMTNUMBER,FGEMTNAME,FGDSNUMBER,FGDSNAME   FROM XFSMZJK.T_KDXF_DEPARTMENT@toerp START WITH   FPNUMBER IS NULL  CONNECT BY  PRIOR FNUMBER=FPNUMBER";
		List<OrgInfo> list = jdbcTemplate.query(querySQL, new OrgMapper());
		return list;
	}
	/**
	 * 获取岗位信息
	 * @return
	 */
	public List<StationInfo> getAllStationInfo(){
		String querySQL = "  SELECT FNUMBER,FNAME,FDEPTNUMBER,FDEPTNAME FROM XFSMZJK.T_KDXF_POST@toerp ";
		List<StationInfo> list = jdbcTemplate.query(querySQL, new StationMapper());
		return list;
	}
	
}
