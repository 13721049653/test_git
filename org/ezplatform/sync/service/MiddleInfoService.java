package org.ezplatform.sync.service;

import java.util.List;

import org.ezplatform.sync.entity.OrgInfo;
import org.ezplatform.sync.entity.StationInfo;
import org.ezplatform.sync.entity.UserInfo;
import org.springframework.stereotype.Service;


public interface MiddleInfoService {
	
	public List<UserInfo> getAllUserInfoList();
	
	public List<OrgInfo> getAllOrgInfoList();
	
	public String addUser(UserInfo userInfo); 
	
	public String modifyUser(UserInfo userInfo);
	
	public String addOrg(OrgInfo orgInfo); 
	
	public String modifyOrg(OrgInfo orgInfo);
	
	public List<StationInfo> getAllStationInfoList();
	
	public void syncOrgAndUser();
	
	public void syncDataToBase();
	
	public void uploadFile() ;
	
}
