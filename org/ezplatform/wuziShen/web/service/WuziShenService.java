package org.ezplatform.wuziShen.web.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.ezplatform.workflow.service.AssetsService;
import org.ezplatform.workflow.service.SystemFlowService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by apple on 2020-11-10.
 */
@Service(value="wuziShenService")
public class WuziShenService {

    @Autowired
    private RestTemplate httpClientTemplate;

    @Autowired
    private HttpClientProperties httpClientProperties;

    @Autowired
    private SystemFlowService systemFlowService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AssetsService assetsService;

    /**
     * 获取物资库存
     * @param
     * @param
     * @return
     */
    public JSONArray dealWuziData(String formData, String userId,String wuziName) {

        //获取对应编码id
        String sqlGetBM = "select FID from xfsmzjk.T_KDXF_MATERIAL@toerp where FNUMBER = '"+wuziName+"'";
        System.out.println("sqlGetBM====>>>"+sqlGetBM);

        List listBM = jdbcTemplate.queryForList(sqlGetBM);


        String whereSql =  "";
        if(listBM.size()>0){

            Map<String,Object> maBM = (Map<String, Object>) listBM.get(0);
            String wuziBM = maBM.get("FID") + "";
            System.out.println("wuziBM====>>>"+wuziBM);
            whereSql = " FMaterialId  = ('"+wuziBM+"')  ";
        }
        JSONObject queryJson=new JSONObject(true);
        queryJson.put("FormId","STK_Inventory");
        queryJson.put("FieldKeys", " FBaseQty ");//query
        queryJson.put("FilterString", whereSql);//where
        queryJson.put("OrderString", "");
        queryJson.put("TopRowCount",0);
        queryJson.put("StartRow", 0);
        queryJson.put("Limit", 0);
        JSONArray zcbmMap = systemFlowService.queryFormInfo(queryJson);

        return zcbmMap;
    }

    public String pushErpKuncunData(String processInstanceId , String processDefinitionId){
        String msg = "success";

        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("processDefinitionId", processDefinitionId);
        paramsMap.put("processInstanceId", processInstanceId);

        /*// 根据 processInstanceId 获取 流程code 及业务表数据主键
        Map<String, String> flowInfoMap = systemFlowService.getFlowInfo(processInstanceId, processDefinitionId);
        Map<String, Map<String, String>> flowMap = SystemFlowService.getFlowMap();
        String business_key_ = flowInfoMap.get("business_key_");

        String ext_processcode = (String)flowInfoMap.get("ext_processcode");
        Map tempMap = (Map)flowMap.get(ext_processcode);
        String tableName = "";
        tableName = "fylwzsqb";


        System.out.println("business_key_====>>>>" + business_key_);
        //获取信息
        String queryParams = "*";
        Map<String,Object> mapOA = getERP_MessageFromOA(business_key_ , queryParams);

        //pushERP*/

        boolean flag = systemFlowService.backKingdeeService(paramsMap);
        System.out.println("flag:===>>>" + flag);


        return msg;

    }

    /**
     * 获取推送ERP单据信息
     */
    public Map<String,Object> getERP_MessageFromOA(String business_key_ , String queryParams){
        //select * from fylwzsqb where id = '8cf5ded32eef4af789bad483ff01b552';
        Map<String,Object> map = null;
        String whereSql = " id = '"+business_key_+"' ";
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        sql.append(" "+queryParams+" ");
        sql.append(" from fylwzsqb ");
        sql.append(" where 1=1 ");
        sql.append(" and " + whereSql);

        List list = jdbcTemplate.queryForList(sql.toString());
        if(list.size()>0){
            map = (Map<String,Object>)list.get(0);
        }
        return map;
    }

}
