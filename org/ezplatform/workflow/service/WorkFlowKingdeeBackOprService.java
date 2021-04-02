package org.ezplatform.workflow.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.ezplatform.workflow.service.workflowEnum.workflowEnum;
import org.ezplatform.workflow.web.client.HttpClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 2021-3-10.
 */

@Service(value="workFlowKingdeeBackOprService")
public class WorkFlowKingdeeBackOprService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate httpClientTemplate;

    @Autowired
    private SystemFlowService systemFlowService;

    @Autowired
    private HttpClientProperties httpClientProperties;

    /**
     * 财务流程退回
     * @param processCode
     * @return
     */
    public ResponseEntity<String> backCaiwu_bx(String processCode , String formid , String erpdjbh){

        ResponseEntity<String> result = null;
        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        String deleteUrl = 	httpClientProperties.getKingUrl() + "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Delete.common.kdsvc";;

        //三元报销
        if("rlxzlbx".equals(processCode)||"cglbx".equals(processCode)||"ygbx".equals(processCode)) {
            //获取erpbh
            if(StringUtils.isNotBlank(erpdjbh)){
                if(org.apache.commons.lang3.StringUtils.isNotEmpty(erpdjbh)) {
                    //删除临时数据
                    JSONArray arr=new JSONArray();
                    arr.add(erpdjbh);
                    JSONObject json=new JSONObject(true);
                    json.put("Numbers", arr);
                    Map<String, Object> applicationDataMap = new HashMap<String, Object>();
                    applicationDataMap.put("formid", formid);
                    applicationDataMap.put("data",json);
                    String jsonString = JSONObject.toJSONString(applicationDataMap);
                    HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);
                    result = httpClientTemplate.postForEntity(deleteUrl, formEntity, String.class);
                }
            }

        }
        return result;
    }


    /**
     * 获取erp单据编号
     * @param businessKey
     * @param processCode
     * @return
     */
    public String getErpDjbh(String businessKey , String processCode){
        if("cglbx".equals(processCode)){
            processCode = "dgcglbx";
        }

        String erpdjbh = "";
        String sql = " select erpdjbh from  "+ processCode +" where id = '"+ businessKey +"' ";
        List list = jdbcTemplate.queryForList(sql);
        if(list!=null && list.size()>0){
            Map<String,Object> map = (Map<String,Object>)list.get(0);
            erpdjbh = map.get("erpdjbh")+"";
        }
        return erpdjbh;
    }





}
