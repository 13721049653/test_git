package org.ezplatform.workflow.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ezplatform.workflow.service.KingdeeService;
import org.ezplatform.workflow.web.client.HttpClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service(value="kingdeeService")
public class KingdeeServiceImpl implements KingdeeService {

	private String saveUrl ;// 保存接口url
	private String loginUrl ;// 用户校验接口URL
	private String shUrl ;// 审核接口url
	private String fshUrl ;// 反审核接口url
	private String uploadurl;//附件上传接口地址
	private String deleteUrl;//删除接口
	private String path;//附件存储路径
	private String acctID ;
	private String username ;
	private String password ;
	private String lcid ;
	private String userToken;
	private String createId;
	
	
	
	 @Autowired
	 private RestTemplate httpClientTemplate;
	    
	 @Autowired
	 private HttpClientProperties httpClientProperties;
	 
	 
	@PostConstruct
    public void init(){
    	 //初始化金蝶参数
		saveUrl = httpClientProperties.getKingUrl()
				+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Save.common.kdsvc";
		loginUrl = httpClientProperties.getKingUrl()
				+ "Kingdee.BOS.WebApi.ServicesStub.AuthService.ValidateUser.common.kdsvc";
		shUrl = httpClientProperties.getKingUrl()
				+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Audit.common.kdsvc";
		fshUrl = httpClientProperties.getKingUrl()
				+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.UnAudit.common.kdsvc";
		uploadurl= httpClientProperties.getKingUrl()
				+ "FileUpLoadServices/FileService.svc/upload2attachment/";
		deleteUrl=httpClientProperties.getKingUrl()
				+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Delete.common.kdsvc";
		acctID = httpClientProperties.getAcctID();
		username = httpClientProperties.getUsername();
		password = httpClientProperties.getPassword();
		lcid = httpClientProperties.getLcid();
		path=httpClientProperties.getPath();
	
    }
	@Override
	public boolean loginKingdee() {
		boolean bResult = false;
		Map<String, Object> applicationDataMap = new HashMap<String, Object>();
		applicationDataMap.put("acctID", acctID);
		applicationDataMap.put("username", username);
		applicationDataMap.put("password", password);
		applicationDataMap.put("lcid", lcid);

		ResponseEntity<String> result = httpClientTemplate.postForEntity(loginUrl, applicationDataMap, String.class);
		String resJson = result.getBody().toString();
		JSONObject json = JSONObject.parseObject(resJson);

		int Loginresulttype = json.getIntValue("LoginResultType");
		String KDSVCSessionId = json.get("KDSVCSessionId") == null ? "" : json.get("KDSVCSessionId").toString();
		if (Loginresulttype == 1) {
			userToken= json.getJSONObject("Context").getString("UserToken");
			createId=json.getJSONObject("Context").getString("UserId");
			bResult = true;
		}
		return bResult;
	}

	@Override
	public String saveForm(String jsonDataString, String opType) {
		String url="";
		ResponseEntity<String> result = null;
		//设置请求头
		HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		if("save".equals(opType))
			url=saveUrl;
		else if("audit".equals(opType)) 
			url=shUrl;
		else if("unaudit".equals(opType)) 
			url=fshUrl;
		else if("delete".equals(opType)) 
			url=deleteUrl;	
		
		HttpEntity<String> formEntity = new HttpEntity<String>(jsonDataString, headers);
			
	    result = httpClientTemplate.postForEntity(url, formEntity, String.class);
		return result.getBody().toString();
	}
	

	@Override
	public JSONObject uploadFile(String fileName, String fileSaveName, String fileId, String token, boolean last,
			String filePath, String content_type) {
		
		String Tuploadurl=uploadurl+"?fileName="+fileName+"&fileId="+fileId+"&token="+token+"&last="+last;
		HttpHeaders headers = new HttpHeaders();
		byte[] buffer=null;
		try {
			buffer = toByteArray(filePath+File.separator+fileSaveName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
         headers.setContentLength(buffer.length);
         HttpEntity<byte[]> httpEntity = new HttpEntity<byte[]>(buffer, headers);
         String result= httpClientTemplate.postForObject(Tuploadurl, httpEntity, String.class);
         JSONObject json=JSONObject.parseObject(result);
	
         return json;
		
	}

	@Override
	public JSONArray queryForm(JSONObject queryJson ) {
		JSONArray resultArr = null;
		ResponseEntity<String> result = null;
		// 设置请求头
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		JSONObject jso = new JSONObject();
		jso.put("data", queryJson);
		HttpEntity<String> formEntity = new HttpEntity<String>(jso.toString(), headers);
		result = httpClientTemplate.postForEntity(
				httpClientProperties.getKingUrl()
						+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.ExecuteBillQuery.common.kdsvc",
				formEntity, String.class);
		if (result.getStatusCodeValue() == 200) {
			resultArr = JSONArray.parseArray(result.getBody().toString());
		}

		return resultArr;
	}

	
	
	/**
     * the traditional io way
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public  byte[] toByteArray(String filename) throws IOException {
 
        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }
 
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }
}
