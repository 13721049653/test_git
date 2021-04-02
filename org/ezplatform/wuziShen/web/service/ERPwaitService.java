package org.ezplatform.wuziShen.web.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.ezplatform.workflow.web.client.HttpClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

/**
 * Created by apple on 2020-11-25.
 */

@Service(value="eRPwaitService")
public class ERPwaitService {

    @Autowired
    private RestTemplate httpClientTemplate;

    @Autowired
    private HttpClientProperties httpClientProperties;

    @Resource
    private JdbcTemplate jdbcTemplate;

    //erp 代办接口地址

    private String approveMethod = "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.ExecuteBillQuery.common.kdsvc";

    public String loadERPNum(String userIdInfo){
        String returnResult = "";
        try{
            ResponseEntity<String> result = null;

            String userId = userIdInfo+"";
            String usercode = "";
            //获取usercode
            String sql = "select a.FUSERID from xfsmzjk.T_KDXF_USER@toerp a inner join sys_user b on a.fuseraccount = b.login_name where b.id='"+userId+"' ";
            System.out.println("erpsql===>>>>"+sql);
            Map<String,Object> mapCode = null;
            if(jdbcTemplate.queryForList(sql + "").size()>0){
                mapCode = jdbcTemplate.queryForList(sql + "").get(0);
                if(mapCode!=null && mapCode.size()!=0){
                    usercode = mapCode.get("FUSERID")+"";
                }
            }


            if(!"".equals(usercode)){
                //设置请求头
                HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());

                Map<String, Object> applicationDataMap = new HashMap<String, Object>();
                applicationDataMap.put("FormId", "WF_AssignmentBill");
                applicationDataMap.put("TopRowCount","10");
                applicationDataMap.put("Limit","0");
                applicationDataMap.put("StartRow","0");
                applicationDataMap.put("FilterString","FSTATUS = 0 and FReceiverId ='"+usercode + "'");
                applicationDataMap.put("OrderString","FCreateTime DESC");
                applicationDataMap.put("FieldKeys","FASSIGNID,FTitle,FCONTENT,FCREATETIME,FTmpId.FProcessType");

                String jsonString = JSONObject.toJSONString(applicationDataMap);

                System.out.println(jsonString);
                HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);
                System.out.println(formEntity);

                System.out.println(httpClientProperties.getKingUrl()+"" + approveMethod);

                httpClientTemplate = new RestTemplate();
                result = httpClientTemplate.postForEntity(httpClientProperties.getKingUrl()+"" + approveMethod, formEntity, String.class);

                returnResult = result.getBody().toString();
            }
        }catch(Exception e){
            returnResult = "0";
            //e.printStackTrace();
        }
        return returnResult;
    }

    /**
     * erp 用户登出
     * @param useraccount
     * @return
     */
    public String loginOut(String userId) {
    	
    	//根据用户id获取账号
    	 String useraccount="";
         //获取useraccount
         String sql = "select login_name from   sys_user where id='"+userId+"' ";
         Map<String,Object> mapCode = null;
         if(jdbcTemplate.queryForList(sql + "").size()>0){
             mapCode = jdbcTemplate.queryForList(sql + "").get(0);
             if(mapCode!=null && mapCode.size()!=0){
            	 useraccount = mapCode.get("login_name")+"";
             }
         }


    	 String outUrl=httpClientProperties.getKingUrl()+"Kingdee.BOS.ServiceFacade.ServicesStub.User.UserService.LogoutByOtherSystem.common.kdsvc";
         String acctID = httpClientProperties.getAcctID();
         String dbId =httpClientProperties.getAcctID();
         String appId = httpClientProperties.getAppId();
         String appSecret = httpClientProperties.getAppSecret();
         String timestamp = System.currentTimeMillis()/1000+"";
         // String [] arr = new String[]{dbId ,userName , appId, appSecret , timestamp };

         String sign = getSha1(dbId ,useraccount , appId, appSecret ,timestamp);
         System.out.println("sign=======>"+sign);


         JSONObject data=new JSONObject(true);
         data.put("AcctID", acctID);
         data.put("AppId", appId);
         data.put("SignedData", sign);
         data.put("Timestamp", timestamp);
         data.put("Username", useraccount);
         /*data.put("appid", appId);
         data.put("dbid", dbId);
         data.put("signeddata", sign);
         data.put("timestamp", timestamp);
         data.put("username", useraccount);
         System.out.println(data.toJSONString());*/
         Map<String,Object> paramsMap=new HashMap<String,Object>();
         paramsMap.put("ap0", data);
         
		//设置请求头
		/*HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
    	HttpEntity<String> formEntity = new HttpEntity<String>(paramsMap.toString(), headers);*/
    	ResponseEntity<String> result = httpClientTemplate.postForEntity(outUrl, paramsMap, String.class);

    	//公式登陆
        String urlGS = "http://time.kxdigit.com/api/logouts?domainAccount="+useraccount;
        //useraccount
        ResponseEntity<String> result2 = httpClientTemplate.getForEntity(urlGS,String.class);
        System.out.println("result2============>>>" + result2);
    	
    	return null;
    }


    /**
     * 加密UD 返回
     * @return
     */
    public String createUD(String userName){
        String returnUD = "";
        String lcId = httpClientProperties.getLcid();
        String dbId =httpClientProperties.getAcctID();
        String appId = httpClientProperties.getAppId();
        String appSecret = httpClientProperties.getAppSecret();
        String timestamp = System.currentTimeMillis()/1000+"";
        // String [] arr = new String[]{dbId ,userName , appId, appSecret , timestamp };

        String sign = getSha1(dbId ,userName , appId, appSecret ,timestamp);
        System.out.println("sign=======>"+sign);


        //String jsonString="{\"lcid\":2052,\"origintype\":\"SimPas\",\"formid\":\"WF_Worklist_Main\",\"entryrole\":\"\",\"pkid\":\"\",\"formtype\":\"list\",\"appid\":\"209675_3fdD0wDJ4rk+79Ut71Xr2/+qVsWYRNtE\",\"dbid\":\"5f3cbe648c3783\",\"otherargs\":\"{'Status':'UnCompleted'}\",\"signeddata\":\"1590e8a630b23b0eebb2f81d551b39af48446d17\",\"timestamp\":\"1606811448\",\"username\":\"WH\"}";
        String jsonString="{\"appid\":\"209675_3fdD0wDJ4rk+79Ut71Xr2/+qVsWYRNtE\",\"dbid\":\"5f3cbe648c3783\",\"lcid\":2052,\"origintype\":\"SimPas\",\"signeddata\":\"1590e8a630b23b0eebb2f81d551b39af48446d17\",\"timestamp\":\"1606811448\",\"username\":\"WH\",\"entryrole\":\"\",\"formid\":\"WF_Worklist_Main\",\"formtype\":\"list\",\"otherargs\":\"{'Status':'UnCompleted'}\",\"pkid\":\"\"}";
        JSONObject data=JSONObject.parseObject(jsonString,Feature.OrderedField);
        data.put("appid", appId);
        data.put("dbid", dbId);
        data.put("lcid", lcId);
        data.put("origintype", "SimPas");
        data.put("signeddata", sign);
        data.put("timestamp", timestamp);
        data.put("username", userName);
        data.put("entryrole", "");
        data.put("formid", "WF_AssignmentBill");
        data.put("formtype", "list");
        data.put("otherargs", "{'Status':'UnCompleted'}");
        data.put("pkid", "");
        String argJosn = data.toString();
        System.out.println("argJosn========>>>>" + argJosn);
        String argJsonBase64="";
        Base64 base64 = new Base64();

        try {
            argJsonBase64 =base64.encodeToString(argJosn.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String url=httpClientProperties.getKingUrl()+"html5/Index.aspx?ud="+ argJsonBase64;
        return url;
    }


    //SHA-1 加密
    public String getSha1(String dbid, String username, String appId, String appSecret, String timestamp) {

        String[] arr = new String[]{dbid, username, appId, appSecret, timestamp};
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        System.out.println("ccc==========="+content.toString());
        MessageDigest md = null;
        String tmpStr = null;
        try {
            //闯将 MessageDigest对象，Message Digest 通过getInstance系列静态函数来进行实例化和初始化
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha- 加密
            String s = content.toString();
            byte[] digest = md.digest(s.getBytes());
            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            tmpStr = hexstr.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        content = null;

        return tmpStr;
    }


    public static void main(String[] args){

        String argJson = "{\"appid\":\"209675_3fdD0wDJ4rk+79Ut71Xr2/+qVsWYRNtE\",\"dbid\":\"5f3cbe648c3783\",\"lcid\":\"2052\",\"origintype\":\"SimPas\",\"signeddata\":\"c80daaa52596f6561ea90ea071eafa872468934c\",\"timestamp\":\"1608279717\",\"username\":\"\",\"entryrole\":\"\",\"formid\":\"WF_Worklist_Main\",\"formtype\":\"list\",\"otherargs\":\"{'Status':'UnCompleted'}\",\"pkid\":\"\"}";
        Base64 base64 = new Base64();

        try {
            String argJsonBase64 =base64.encodeToString(argJson.getBytes());

            System.out.println("argJsonBase64=======>>>" + argJsonBase64);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
