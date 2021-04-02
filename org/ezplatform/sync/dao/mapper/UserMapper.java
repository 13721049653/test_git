package org.ezplatform.sync.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ezplatform.sync.entity.UserInfo;
import org.springframework.jdbc.core.RowMapper;

public class UserMapper implements RowMapper<UserInfo>{

	@Override
	public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				//从结果集里把数据得到
		
		
				//STAFF.FNAME,STAFF.FUSERNAME,STAFF.FEMAIL,STAFF.FMOBILE,STAFF.FSFZH,
				//STAFF.FXB,STAFF.FZW,DEPT.FNUMBER AS DFNUMBER,
				//POST.FNUMBER AS PFNUMBER,POST.FNAME AS PFNAME,DEPT.FGLDGH,DEPT.BMFZRGH,
				//DEPT.FGEMTNUMBER,DEPT.FGDSNUMBER
				String FNAME=rs.getString("FNAME");
				String FUSERNAME=rs.getString("FUSERNAME");
				String FNUMBER=rs.getString("FNUMBER");
				String FEMAIL=rs.getString("FEMAIL");
				String FMOBILE=rs.getString("FMOBILE");
				String FSFZH=rs.getString("FSFZH");
				String FXB=rs.getString("FXB");
				String FZW=rs.getString("FZW");
				String DFNUMBER=rs.getString("DFNUMBER");
				String DFNAME=rs.getString("DFNAME");
				String PFNUMBER=rs.getString("PFNUMBER");
				String PFNAME=rs.getString("PFNAME");
				String FGLDGH=rs.getString("FGLDGH");
				String BMFZRGH=rs.getString("BMFZRGH");
				String FGEMTNUMBER=rs.getString("FGEMTNUMBER");
				String FGDSNUMBER=rs.getString("FGDSNUMBER");
				String fzjsjnumber=rs.getString("FZJSJNUMBER");
				String dutyCode=rs.getString("DUTYCODE");
				//把数据封装到对象里
				UserInfo user=new UserInfo();
				user.setUserName(FNAME);
				user.setAccount(FUSERNAME);
				user.setEmail(FEMAIL);
				user.setPhone(FMOBILE);
				user.setIdCard(FSFZH);
				user.setSex("1".equals(FXB)?0:2);
				user.setOrgCode(DFNUMBER);
				user.setOrgName(DFNAME);
				user.setPostsOrgCode(PFNUMBER);
				user.setExtPost(PFNAME);
				user.setSimpleCode(FNUMBER);
				user.setNickName(FUSERNAME);
				user.setOrgLeaderAccount(BMFZRGH);
				user.setInChargeLeaderAccount(FGDSNUMBER);
				user.setLeaderAccount(BMFZRGH);
				user.setFzjsjnumber(fzjsjnumber);
				user.setDutyCode(dutyCode);

				return user;

	}

}
