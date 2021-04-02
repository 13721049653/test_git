package org.ezplatform.sync.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ezplatform.sync.entity.OrgInfo;
import org.ezplatform.sync.entity.StationInfo;
import org.ezplatform.sync.entity.UserInfo;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

public class StationMapper implements RowMapper<StationInfo>{
	/**
	 * 岗位匹配
	 */
	@Override
	public StationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				//从结果集里把数据得到
				//FNUMBER ,FPNUMBER,FNAME,FPNAME 
				//select post.fnumber,fname,fdeptnumber,fdeptname from xfsmzjk.T_KDXF_POST@toerp post 
				String fnumber=rs.getString("FNUMBER");
				String FNAME=rs.getString("FNAME");
				String fdeptnumber=rs.getString("FDEPTNUMBER");
				String fdeptname=rs.getString("FDEPTNAME");
				
				//把数据封装到对象里
				StationInfo stationinfo=new StationInfo();
				stationinfo.setCorpId("0");
				stationinfo.setOrgCode(fdeptnumber);
				stationinfo.setStationCode(fnumber);
				stationinfo.setStationName(FNAME);
				
				return stationinfo;

	}

}
