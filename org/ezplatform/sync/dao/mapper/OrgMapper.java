package org.ezplatform.sync.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ezplatform.sync.entity.OrgInfo;
import org.ezplatform.sync.entity.UserInfo;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

public class OrgMapper implements RowMapper<OrgInfo>{

	@Override
	public OrgInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				//从结果集里把数据得到
		//FNUMBER ,FPNUMBER,FNAME,FPNAME 
				String FNUMBER=rs.getString("FNUMBER");
				String FNAME=rs.getString("FNAME");
				String FPNUMBER=rs.getString("FPNUMBER");
				//把数据封装到对象里
				OrgInfo orgInfo=new OrgInfo();
				orgInfo.setCode(FNUMBER);
				orgInfo.setCodeName(FNUMBER);
				orgInfo.setName(FNAME);
				orgInfo.setSuperOrgCode(FPNUMBER);
				String BMFZRGH=rs.getString("BMFZRGH");
				String BMFZRNAME=rs.getString("BMFZRNAME");//部门负责人名称
				String FGLDGH=rs.getString("FGLDGH");
				String FGLDNAME=rs.getString("FGLDNAME");//分管领导
				String FGEMTNUMBER=rs.getString("FGEMTNUMBER");
				String FGEMTNAME=rs.getString("FGEMTNAME");//分管EMT
				String FGDSNUMBER=rs.getString("FGDSNUMBER");
				String FGDSNAME=rs.getString("FGDSNAME");
				orgInfo.setBmfzrgh(BMFZRGH);
				orgInfo.setBmfzrname(BMFZRNAME);//部门负责人名称
				orgInfo.setFgldgh(FGLDGH);
				orgInfo.setFgldname(FGLDNAME);//分管领导
				orgInfo.setFgemtname(FGEMTNAME);//分管EMT
				orgInfo.setFgemtnumber(FGEMTNUMBER);
				orgInfo.setFgdsname(FGDSNAME);
				orgInfo.setFgdsnumber(FGDSNUMBER);
				
				return orgInfo;

	}

}
