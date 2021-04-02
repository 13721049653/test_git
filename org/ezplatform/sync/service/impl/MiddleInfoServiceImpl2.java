package org.ezplatform.sync.service.impl;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.ezplatform.sync.dao.MiddleInfoDao;
import org.ezplatform.sync.entity.OrgBaseInfo;
import org.ezplatform.sync.entity.OrgInfo;
import org.ezplatform.sync.entity.RequestInfo;
import org.ezplatform.sync.entity.StationInfo;
import org.ezplatform.sync.entity.UserBaseInfo;
import org.ezplatform.sync.entity.UserInfo;
import org.ezplatform.sync.service.MiddleInfoService;
import org.ezplatform.workflow.web.client.HttpClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

@Service("middleInfoService")
public class MiddleInfoServiceImpl2 implements MiddleInfoService{

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    MiddleInfoDao middleInfoDao;

    @Autowired
    RestTemplate httpClientTemplate;
    @Autowired
    private HttpClientProperties httpClientProperties;
    @Override
    public List<UserInfo> getAllUserInfoList() {
        return middleInfoDao.getAllUserInfo();
    }

    @Override
    public List<OrgInfo> getAllOrgInfoList() {

        return middleInfoDao.getAllOrgInfo();
    }
    @Override
    public List<StationInfo> getAllStationInfoList() {

        return middleInfoDao.getAllStationInfo();
    }

    @Override
    public String addUser(UserInfo userInfo) {
        RequestInfo reqInfo= new RequestInfo();
        reqInfo.setAppname("ERP");
        reqInfo.setCmd("addUser");
        reqInfo.setPassword("");
        reqInfo.setData(JSONObject.toJSON(userInfo));
        return JSONObject.toJSONString(reqInfo);
    }

    @Override
    public String modifyUser(UserInfo userInfo) {
        RequestInfo reqInfo= new RequestInfo();
        reqInfo.setAppname("ERP");
        reqInfo.setCmd("modiUser");
        reqInfo.setPassword("");
        reqInfo.setData(JSONObject.toJSON(userInfo));
        return JSONObject.toJSONString(reqInfo);
    }

    @Override
    public String addOrg(OrgInfo orgInfo) {
        RequestInfo reqInfo= new RequestInfo();
        reqInfo.setAppname("ERP");
        reqInfo.setCmd("addOrg");
        reqInfo.setPassword("");
        reqInfo.setData(JSONObject.toJSON(orgInfo));
        return JSONObject.toJSONString(reqInfo);
    }

    @Override
    public String modifyOrg(OrgInfo orgInfo) {
        RequestInfo reqInfo= new RequestInfo();
        reqInfo.setAppname("ERP");
        reqInfo.setCmd("modiOrg");
        reqInfo.setPassword("");
        reqInfo.setData(JSONObject.toJSON(orgInfo));
        return JSONObject.toJSONString(reqInfo);
    }

    @Override
    //@Scheduled(cron = "0 0 11 * * ? ")
    @Scheduled(initialDelay = 1000L, fixedDelay = 3*3600*1000L)
    public void syncOrgAndUser() {

        String url=httpClientProperties.getIdsUrl()+"/UUMS/apiservice.do";
        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        //同步组织至IDS
        List<OrgInfo> orgList= getAllOrgInfoList();
        if(orgList!=null&&orgList.size()>0) {
            for(OrgInfo orgInfo : orgList) {
                String jsonString=addOrg(orgInfo);
                HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);

                ResponseEntity<String> result = httpClientTemplate.postForEntity(url, formEntity, String.class);
                System.out.println("同步组织=======>"+result.getBody().toString());

                //JSONObject json = JSONObject.parseObject(result.getBody().toString());
            }
            for(OrgInfo orgInfo : orgList) {
                String jsonString=modifyOrg(orgInfo);
                HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);

                ResponseEntity<String> result = httpClientTemplate.postForEntity(url, formEntity, String.class);
                System.out.println("更新组织=======>"+result.getBody().toString());
                //JSONObject json = JSONObject.parseObject(result.getBody().toString());
            }
        }


        //同步用户至IDS
        List<UserInfo> userList= getAllUserInfoList();
        if(userList!=null&&userList.size()>0) {
            for(UserInfo userInfo : userList) {
                String jsonString=addUser(userInfo);
                HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);
                //print
                //userInfo.getUserName()

                ResponseEntity<String> result = httpClientTemplate.postForEntity(url, formEntity, String.class);
                JSONObject json = JSONObject.parseObject(result.getBody().toString());
                String message=json.getString("message");
                System.out.println("message=============>"+message);
                if("nickName is exist!".equals(message)) {
                    jsonString=modifyUser(userInfo);
                    if("whir".equals(userInfo.getAccount())){
                        System.out.println("用户jsonString===========》"+jsonString);
                    }
                    formEntity = new HttpEntity<String>(jsonString, headers);
                    result = httpClientTemplate.postForEntity(url, formEntity, String.class);
                    System.out.println("用户result.getBody().toString()===========》" + result.getBody().toString());
                }else if ("name is exist!".equals(message)){
                    //userInfo.setUserName(userInfo.getUserName()+"_del");
                    //userInfo.setAccount(userInfo.getAccount()+"_del");
                    //userInfo.setNickName(userInfo.getNickName()+"_del");
                    jsonString=modifyUser(userInfo);
                    formEntity = new HttpEntity<String>(jsonString, headers);
                    System.out.println("jsonString=111==>"+jsonString);
                    result = httpClientTemplate.postForEntity(url, formEntity, String.class);

                }else{
                    System.out.println("other meg =====>>>>"+message);
                }
            }
        }

        syncSfz_sjh();
    }

    @Override
    //@Scheduled(cron = "0 0 12 * * ? ")
    @Scheduled(initialDelay = 1000L, fixedDelay = 3*3600*1000L)
    public void syncDataToBase() {
        /**
         * 同步数据至base
         */
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
        headers.setContentType(type);

        // headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        // 获取token
        String url = httpClientProperties.getBaseUrl() + "/flexbase/oauth2/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("client_id", httpClientProperties.getClient_id());
        params.add("client_secret", httpClientProperties.getClient_secret());
        params.add("grant_type", httpClientProperties.getGrant_type());
        // 返回 AccessToken 数据
        // String jsonString=JSONObject.toJSONString(params);
        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<MultiValueMap<String, String>>(params,
                headers);
        ResponseEntity<String> result = httpClientTemplate.postForEntity(url, formEntity, String.class);
        JSONObject json = JSONObject.parseObject(result.getBody().toString());
        String token = json.getString("access_token");
        // 添加请求头认证
        HttpHeaders httpHeaders = preparedHttpHeaders(token);
        // 获取岗位列表
        String saveurl = httpClientProperties.getBaseUrl() + "/flexbase/api/system/station/create";
        String updateUrl = httpClientProperties.getBaseUrl() + "/flexbase/api/system/station/update";
        String orgInfoUrl = httpClientProperties.getBaseUrl() + "/flexbase/api/workflow/kingdee/getUserOrOrgIdByFNumber";
        String addOrgUrl=httpClientProperties.getBaseUrl() + "/flexbase/api/system/org/create";
        String updateOrgUrl=httpClientProperties.getBaseUrl() + "/flexbase/api/system/org/update";
        String deleteOrgUrl=httpClientProperties.getBaseUrl() + "/flexbase/api/system/org/delete";

        String addUserUrl=httpClientProperties.getBaseUrl() + "/flexbase/api/system/user/create";
        String updateUserUrl=httpClientProperties.getBaseUrl() + "/flexbase/api/system/user/update";
        String deleteUserUrl=httpClientProperties.getBaseUrl() + "/flexbase/api/system/user/delete";

        Map<String, String> orgInfoMap = new HashMap<String, String>();
        Map<String, String> empInfoMap = new HashMap<String, String>();
        Map<String, String> stationInfoMap = new HashMap<String, String>();
        Map<String, String> dutyInfoMap = new HashMap<String, String>();

        type = MediaType.parseMediaType("application/json; charset=UTF-8");
        httpHeaders.setContentType(type);


        //同步组织信息
        List<OrgBaseInfo> orgBaseList = getBaseOrgInfo();
        if(orgBaseList!=null&&orgBaseList.size()>0) {

            MultiValueMap<String, String> temp = new LinkedMultiValueMap<String, String>();
            HttpEntity<MultiValueMap<String, String>> tempFormEntity = new HttpEntity<MultiValueMap<String, String>>(temp, httpHeaders);

            for(OrgBaseInfo orgInfo:orgBaseList) {

                // 通过组织编码获取组织id
                if (orgInfoMap.get(orgInfo.getOrgCode()) == null) {
                    ResponseEntity<String> tempResult = httpClientTemplate.exchange(orgInfoUrl+"?infoType=org&fNumber="+orgInfo.getSuperCode(),HttpMethod.GET, tempFormEntity,String.class);
                    String orgId = getJSON(tempResult).getJSONObject("data").getString("id");
                    orgInfoMap.put(orgInfo.getSuperCode(), orgId);
                    orgInfo.setParentId(orgId);
                } else {
                    orgInfo.setParentId(orgInfoMap.get(orgInfo.getSuperCode()));
                }

                //获取上级领导信息
				/*if(StringUtils.isNoneEmpty(orgInfo.getULLoginName())) {
					String uLLoginName=orgInfo.getULLoginName();
					if(empInfoMap.get(uLLoginName)!=null) {
						orgInfo.setUserLeaders(empInfoMap.get(uLLoginName));
					}else {
						ResponseEntity<String> tempResult = httpClientTemplate.exchange(orgInfoUrl+"?infoType=user&fNumber="+uLLoginName,HttpMethod.GET, tempFormEntity,String.class);
						String empId = getJSON(tempResult).getJSONObject("data").getString("id");
						empInfoMap.put(uLLoginName, empId);
						orgInfo.setUserLeaders(empId);
					}
				}*/
                //获取组织领导信息
				/*if(StringUtils.isNoneEmpty(orgInfo.getOLLoginName())) {
					String oLLoginName=orgInfo.getOLLoginName();
					if(empInfoMap.get(oLLoginName)!=null) {
						orgInfo.setOrgLeaders(empInfoMap.get(oLLoginName));
					}else {
						ResponseEntity<String> tempResult = httpClientTemplate.exchange(orgInfoUrl+"?infoType=user&fNumber="+oLLoginName,HttpMethod.GET, tempFormEntity,String.class);
						String empId = getJSON(tempResult).getJSONObject("data").getString("id");
						empInfoMap.put(oLLoginName, empId);
						orgInfo.setOrgLeaders(empId);
					}
				}*/

                //获取分管emt领导信息
				/*if(StringUtils.isNoneEmpty(orgInfo.getDLLoginName())) {
					String dLLoginName=orgInfo.getDLLoginName();
					if(empInfoMap.get(dLLoginName)!=null) {
						orgInfo.setDeputyLeaders(empInfoMap.get(dLLoginName));
					}else {
						ResponseEntity<String> tempResult = httpClientTemplate.exchange(orgInfoUrl+"?infoType=user&fNumber="+dLLoginName,HttpMethod.GET, tempFormEntity,String.class);
						String empId = getJSON(tempResult).getJSONObject("data").getString("id");
						empInfoMap.put(dLLoginName, empId);
						orgInfo.setDeputyLeaders(empId);
					}
				}*/
                HttpEntity<String> formEntity1 = new HttpEntity<String>(JSONObject.toJSONString(orgInfo), httpHeaders);

                result = httpClientTemplate.postForEntity(addOrgUrl, formEntity1, String.class);
                json = JSONObject.parseObject(result.getBody().toString());
                if (!"success".equals(json.getString("type")) && json.getString("message").indexOf("已存在") > -1) {
                    //result = httpClientTemplate.postForEntity(updateOrgUrl, formEntity1, String.class);
                    result = httpClientTemplate.exchange(updateOrgUrl,HttpMethod.PUT, formEntity1,String.class);
                    System.out.println("1111111111133333" + result.getBody().toString());
                }
            }

        }

        syncSfz_sjh();
        //jinxh 删除多余组织（组织在erp中不存在，贼修改删除）
        deleteOrgUser();



        //同步岗位信息
        List<StationInfo> stationList = getAllStationInfoList();
        if (stationList != null && stationList.size() > 0) {
            MultiValueMap<String, String> temp = new LinkedMultiValueMap<String, String>();
            HttpEntity<MultiValueMap<String, String>> tempFormEntity = new HttpEntity<MultiValueMap<String, String>>(temp, httpHeaders);
            temp.add("infoType", "org");
            for (StationInfo station : stationList) {
                // 通过组织编码获取组织id
                if (orgInfoMap.get(station.getOrgCode()) == null) {
                    temp.add("fNumber ", station.getOrgCode());
                    ResponseEntity<String> tempResult = httpClientTemplate.exchange(orgInfoUrl+"?infoType=org&fNumber="+station.getOrgCode(),HttpMethod.GET, tempFormEntity,String.class);
                    String orgId = getJSON(tempResult).getJSONObject("data").getString("id");
                    orgInfoMap.put(station.getOrgCode(), orgId);
                    station.setOrgId(orgId);
                } else {
                    station.setOrgId(orgInfoMap.get(station.getOrgCode()));
                }

                HttpEntity<String> formEntity1 = new HttpEntity<String>(JSONObject.toJSONString(station), httpHeaders);

                result = httpClientTemplate.postForEntity(saveurl, formEntity1, String.class);
                json = JSONObject.parseObject(result.getBody().toString());

                if (!"success".equals(json.getString("type")) && json.getString("message").indexOf("岗位编码已存在") > -1) {
                    //	result = httpClientTemplate.postForEntity(updateUrl, formEntity1, String.class);
                    result = httpClientTemplate.exchange(updateUrl,HttpMethod.PUT, formEntity1,String.class);
                    //System.out.println("11111111111" + result.getBody().toString());
                    //stationInfoMap.put(station.getStationCode(), json.getJSONObject("data").getString("id"));
                }else {
                    stationInfoMap.put(station.getStationCode(), json.getJSONObject("data").getString("id"));
                }
            }

        }



        //同步用户信息
        List<UserBaseInfo> userBaseList = getUserBaseList();

        List<UserBaseInfo> userBaseUpdateList=new ArrayList<UserBaseInfo>();
        if(userBaseList!=null&&userBaseList.size()>0) {

            MultiValueMap<String, String> temp = new LinkedMultiValueMap<String, String>();
            HttpEntity<MultiValueMap<String, String>> tempFormEntity = new HttpEntity<MultiValueMap<String, String>>(temp, httpHeaders);
            for(UserBaseInfo userInfo:userBaseList) {
				/*// 通过组织编码获取组织id
				if (orgInfoMap.get(userInfo.getOrgCode()) == null) {
					ResponseEntity<String> tempResult = httpClientTemplate.exchange(orgInfoUrl+"?infoType=org&fNumber="+userInfo.getOrgCode(),HttpMethod.GET, tempFormEntity,String.class);
					String orgId = getJSON(tempResult).getJSONObject("data").getString("id");
					orgInfoMap.put(userInfo.getOrgCode(), orgId);
					userInfo.setOrgId(orgId);
				} else {
					userInfo.setOrgId(orgInfoMap.get(userInfo.getOrgCode()));
				}
				*/

                //获取岗位信息
                if(StringUtils.isNoneEmpty(userInfo.getStationCode())) {
                    String stationCode=userInfo.getStationCode();
                    if(stationInfoMap.get(stationCode)!=null) {
                        userInfo.setUserLeaders(stationInfoMap.get(stationCode));
                    }else {
                        ResponseEntity<String> tempResult = httpClientTemplate.exchange(orgInfoUrl+"?infoType=station&fNumber="+stationCode,HttpMethod.GET, tempFormEntity,String.class);
                        String stationId = getJSON(tempResult).getJSONObject("data").getString("id");
                        stationInfoMap.put(stationCode, stationId);
                        userInfo.setUserLeaders(stationId);
                    }
                }


                //获取职级信息
                if(StringUtils.isNoneEmpty(userInfo.getDutyCode())) {
                    String dutyCode=userInfo.getDutyCode();
                    if(dutyInfoMap.get(dutyCode)!=null) {
                        userInfo.setDutyId(dutyInfoMap.get(dutyCode));
                    }else {
                        ResponseEntity<String> tempResult = httpClientTemplate.exchange(orgInfoUrl+"?infoType=duty&fNumber="+dutyCode,HttpMethod.GET, tempFormEntity,String.class);
                        String dutyId = getJSON(tempResult).getJSONObject("data").getString("id");
                        dutyInfoMap.put(dutyCode, dutyId);
                        userInfo.setDutyId(dutyId);
                    }
                }




                //先去除分管董事/总裁
                String fgds=userInfo.getDeputyLeaderAccount();
                //userInfo.setDeputyLeaderAccount(null);
                HttpEntity<String> formEntity1 = new HttpEntity<String>(JSONObject.toJSONString(userInfo), httpHeaders);
                result = httpClientTemplate.postForEntity(addUserUrl, formEntity1, String.class);
                json = JSONObject.parseObject(result.getBody().toString());
                //获取上级领导信息
				/*if(StringUtils.isNoneEmpty(userInfo.getLeaderAccount())) {
					String leaderAccount=userInfo.getLeaderAccount();
					if(empInfoMap.get(leaderAccount)!=null) {
						userInfo.setUserLeaders(empInfoMap.get(leaderAccount));
					}else {
						ResponseEntity<String> tempResult = httpClientTemplate.exchange(orgInfoUrl+"?infoType=user&fNumber="+leaderAccount,HttpMethod.GET, tempFormEntity,String.class);
						String empId = getJSON(tempResult).getJSONObject("data").getString("id");
						empInfoMap.put(leaderAccount, empId);
						userInfo.setUserLeaders(empId);
					}
				}*/
                userInfo.setDeputyLeaderAccount(fgds);
                userBaseUpdateList.add(userInfo);



				/*if (!"success".equals(json.getString("type")) && json.getString("message").indexOf("该用户已存在") > -1) {
					//result = httpClientTemplate.postForEntity(updateUserUrl, formEntity1, String.class);
					//result = httpClientTemplate.exchange(updateUserUrl,HttpMethod.PUT, formEntity1,String.class);
					//System.out.println("11111111111" + result.getBody().toString());
				}*/
            }


            //更新领导信息

            for(UserBaseInfo userInfo:userBaseUpdateList) {
                JSONObject josn=(JSONObject) JSONObject.toJSON(userInfo);
                josn.remove("password");
                HttpEntity<String> formEntity1 = new HttpEntity<String>(josn.toString(), httpHeaders);
                json = JSONObject.parseObject(result.getBody().toString());
                result = httpClientTemplate.exchange(updateUserUrl,HttpMethod.PUT, formEntity1,String.class);
            }

            //跟新身份证和手机号
            System.out.println("111111111111111111111");
        }

        //跟新人员组织领导信息
       /* String querySqlAll = "select a.id ids ,c.org_leaders  orgleaders from sys_user a inner join sys_org_user b on a.id = b.user_id inner join sys_org c on b.org_id= c.id";
        List listQueryAll = jdbcTemplate.queryForList(querySqlAll);
        if(listQueryAll.size()>0){
            for(int i=0;i<listQueryAll.size();i++){
                Map<String,Object> mapQuery = (Map<String,Object>)listQueryAll.get(i);
                String tempId = mapQuery.get("ids")+"";
                String tempOrgLeaders = mapQuery.get("orgleaders")+"";
                String updateSqlTemp = "update sys_user_leaders set leader_id='"+tempOrgLeaders+"' where 1=1 and leader_type='org' and user_id='"+tempId+"'";
                System.out.println("infoWriteSql:====>>>"+updateSqlTemp);
                //jdbcTemplate.execute(updateSqlTemp);
            }
        }*/

        //jinxh 删除多余组织（组织在erp中不存在，贼修改删除）
        //deleteOrgUser();

    }

    public void syncSfz_sjh(){
        String sql = "";
        List<UserInfo> userBaseUpdateList = middleInfoDao.getAllUserInfo();
        for(UserInfo userInfo:userBaseUpdateList) {
            JSONObject josn=(JSONObject) JSONObject.toJSON(userInfo);
            System.out.println(josn);
            //跟新人员信息
            String login_name = userInfo.getAccount()+"";
            String telephone =  userInfo.getPhone()+"";
            String cardId = userInfo.getIdCard()+"";

            String sqlUpdateUser =  "update sys_user set phone='"+telephone+"' ,telephone = '"+telephone+"' , card_id = '"+cardId+"' where login_name = '"+login_name+"'  ";
            System.out.println("sqlUpdateUser===>>>"+sqlUpdateUser);
            jdbcTemplate.execute(sqlUpdateUser);
        }

    }

    public void deleteOrgUser(){
        String sqlOrg = "update sys_org set is_delete=1 where  third_uid is not null or org_code in (select org_code from sys_org where org_code not in (select fnumber from XFSMZJK.T_KDXF_DEPARTMENT@toerp))";
        jdbcTemplate.execute(sqlOrg);

        //String sqlUser = "update sys_user set is_delete =1 where login_name not like '%whir%' and id in (select a.id from sys_user a inner join sys_org_user b on a.id=b.user_id inner join sys_org c on b.org_id = c.id where a.card_id not in (select fsfzh from XFSMZJK.T_KDXF_STAFF@toerp))";
        String sqlUser = "update sys_user set is_delete =1 where (login_name not like '%whir%' and login_name not like '%admin%') and id in (select a.id from sys_user a inner join sys_org_user b on a.id=b.user_id inner join sys_org c on b.org_id = c.id where (a.card_id not in (select fsfzh from XFSMZJK.T_KDXF_STAFF@toerp)  or a.card_id is null))";
        String sqlUserHF = "update sys_user set is_delete =0 where id in (select a.id from sys_user a inner join sys_org_user b on a.id=b.user_id inner join sys_org c on b.org_id = c.id where (a.card_id in (select fsfzh from XFSMZJK.T_KDXF_STAFF@toerp) ))";

        jdbcTemplate.execute(sqlUser);
        jdbcTemplate.execute(sqlUserHF);
    }

    /**
     * 同步组织信息至base
     */
    public List<OrgBaseInfo> getBaseOrgInfo() {
        List<OrgInfo> orgList= getAllOrgInfoList();
        List<OrgBaseInfo> listOrg=new ArrayList<OrgBaseInfo>();
        if(orgList!=null&&orgList.size()>0) {
            for(OrgInfo orgInfo : orgList) {
                OrgBaseInfo orgBase=new OrgBaseInfo();
                orgBase.setOrgName(orgInfo.getName());
                orgBase.setOrgSimpleName(orgInfo.getName());
                orgBase.setOrgCode(orgInfo.getCode());

                orgBase.setCorpId("0");
                orgBase.setParentCode(orgInfo.getSuperOrgCode());
                orgBase.setSuperCode(orgInfo.getSuperOrgCode());
                if(StringUtils.isEmpty(orgInfo.getSuperOrgCode())) {
                    orgBase.setOrgType("2");
                }else {
                    orgBase.setOrgType("3");
                }

                orgBase.setULLoginName(orgInfo.getFgemtnumber());
                orgBase.setULUserName(orgInfo.getFgemtname());//erp 分管领导==》 oa 上级领导

				/*
				orgBase.setOLLoginName();
				orgBase.setOLUserName();*/

                orgBase.setOLLoginName(orgInfo.getBmfzrgh());
                orgBase.setOLUserName(orgInfo.getBmfzrname());//erp 部门负责人 ==> 组织领导 oa

                orgBase.setDLLoginName(orgInfo.getFgldgh());
                orgBase.setDLUserName(orgInfo.getFgldname()); //erp 分管emt ===> 分管领导 oa

                listOrg.add(orgBase);

            }
        }
        return  listOrg;
    }


    /**
     * 获取base用户
     * @return
     */
    public List<UserBaseInfo> getUserBaseList(){
        List<UserInfo> userList=getAllUserInfoList();
        List<UserBaseInfo>  userBaseList=new ArrayList<UserBaseInfo>();
        if(userList!=null&&userList.size()>0) {
            for(UserInfo user:userList) {
                UserBaseInfo userBase=new UserBaseInfo();
                userBase.setUserName(user.getUserName());
                userBase.setLoginName(user.getAccount());
                userBase.setPassword("qwer@12345");//qwer@12345
                //userBase.setSex("1".equals(user.getAge())?"female":"male");
                userBase.setSex("0".equals((user.getSex()+"").trim())?"female":"male");
                System.out.println("sexsexsexsexsex:=====>>>"+user.getSex());

                userBase.setTelephone(user.getPhone()!=null?user.getPhone().trim():"");
                userBase.setUserCode(user.getSimpleCode());
                //userBase.setPwdEncrypt("1");
                userBase.setEmail(user.getEmail()!=null?user.getEmail().trim():"");
                userBase.setCardId(user.getIdCard());
                userBase.setLeaderAccount(user.getFzjsjnumber());
                userBase.setOrgName(user.getOrgName());
                userBase.setOrgCode(user.getOrgCode());
                userBase.setStaName(user.getExtPost());
                userBase.setStationCode(user.getPostsOrgCode());
                userBase.setDeputyLeaderAccount(user.getInChargeLeaderAccount());
                userBase.setCorpId("0");
                userBase.setDutyCode(user.getDutyCode());

                String querySQL = " SELECT bmfzrgh FROM   XFSMZJK.T_KDXF_STAFF@toerp STAFF LEFT JOIN XFSMZJK.T_KDXF_POST@toerp POST ON STAFF.FZRGWNUMBER =POST.FNUMBER LEFT JOIN  XFSMZJK.T_KDXF_DEPARTMENT@toerp  DEPT ON POST.FDEPTNUMBER =DEPT.FNUMBER where STAFF.FUSERNAME='"+user.getAccount()+"' ";
                System.out.println("querySQL:=======>>>>>"+querySQL);
                Map<String,Object> mapTemp = null;
                if( jdbcTemplate.queryForList(querySQL).size()>0){
                    mapTemp = jdbcTemplate.queryForMap(querySQL);
                }
                System.out.println("mapTemp===>>>"+mapTemp);


                if(mapTemp!=null){

                    Set setT = mapTemp.keySet();
                    Iterator it = setT.iterator();
                    while(it.hasNext()){
                        String keyT = it.next()+"";
                        System.out.print("key====>>>>"+keyT);
                        System.out.print("value====>>>>"+mapTemp.get(keyT));
                    }
                    if(mapTemp.get("bmfzrgh")!=null){

                        System.out.println("sj_fzr:==>>>"+user.getAccount()+"===>>>"+mapTemp.get("bmfzrgh"));
                        userBase.setOrgLeaderAccount( mapTemp.get("bmfzrgh")+"");

                    }else{
                        //userBase.setOrgLeaderAccount( "whir1" );
                    }

                }

                userBaseList.add(userBase);
            }
        }
        return userBaseList;
    }




    private JSONObject getJSON(ResponseEntity<String> tempResult) {
        return JSONObject.parseObject(tempResult.getBody().toString());
    }

    private HttpHeaders preparedHttpHeaders(String token) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("X-TOKEN-TYPE", "JWT");
        requestHeaders.add("Authorization", "Bearer " + token);
        return requestHeaders;
    }

    @Override
    public void uploadFile() {
        //上传附件

        // 获取token
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
        headers.setContentType(type);
        String url = httpClientProperties.getBaseUrl() + "/flexbase/oauth2/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("client_id", httpClientProperties.getClient_id());
        params.add("client_secret", httpClientProperties.getClient_secret());
        params.add("grant_type", httpClientProperties.getGrant_type());
        // 返回 AccessToken 数据
        // String jsonString=JSONObject.toJSONString(params);
        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<MultiValueMap<String, String>>(params,
                headers);
        ResponseEntity<String> result = httpClientTemplate.postForEntity(url, formEntity, String.class);
        JSONObject json = JSONObject.parseObject(result.getBody().toString());
        String token = json.getString("access_token");
        // 添加请求头认证
        HttpHeaders httpHeaders = preparedHttpHeaders(token);
        MediaType type1 = MediaType.parseMediaType("multipart/form-data");
        httpHeaders.setContentType(type1);
        FileSystemResource fileSystemResource = new FileSystemResource("D:\\JBOSS环境搭建.docx");
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", fileSystemResource);
        form.add("dir","approve");
        form.add("fileDisplayName","JBOSS环境搭建.docx");


        HttpEntity<MultiValueMap<String, Object>> formEntity1 = new HttpEntity<MultiValueMap<String, Object>>(form, httpHeaders);
        String uploadUrl="http://192.168.2.202:8085/flexbase/api/cmp/attachment/upload";
        String result1= httpClientTemplate.postForObject(uploadUrl, formEntity1, String.class);
        System.out.println(result1);


    }

}
