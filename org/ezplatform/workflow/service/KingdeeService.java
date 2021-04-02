package org.ezplatform.workflow.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface KingdeeService {

	public boolean loginKingdee() ;
	
	public String saveForm(String jsonDataString, String opType);
	
	public JSONObject uploadFile(String fileName, String fileSaveName, String fileId, String token, boolean last, String filePath, String content_type);
	
	public JSONArray queryForm(JSONObject queryJson);
}
