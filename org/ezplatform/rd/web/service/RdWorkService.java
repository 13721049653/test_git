package org.ezplatform.rd.web.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.ezplatform.workflow.web.client.HttpClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 2020-11-28.
 */
@Service(value="rdWorkService")
public class RdWorkService {

    @Autowired
    private HttpClientProperties httpClientProperties;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 私车补贴申请获取数据建模补贴金额
     * @param formData
     * @param userId
     * @param btbz
     * @return
     */
    public String dealMoneyData(String formData, String userId, String btbz) {

       String val = "";
       String sql = "select btbz from scbtbz where zwbm = '"+btbz+"' ";
        List list = jdbcTemplate.queryForList(sql);
        if(list!=null && list.size()>0){
            Map<String , Object> map = jdbcTemplate.queryForMap(sql);
            val = map.get("btbz")+"";
        }

        return val;
    }

    /**
     * 根据orgids 获取leader
     * @param orgIds
     * @return
     */
    public String getLeaderByOrg(String orgIds){
        String returnLeadersNames = "";

        orgIds = orgIds.replace(",","','");

        //生成sql串
        String orgidsStr ="'"+orgIds+"'";

        String sql = "select distinct( b.name) as leadernames from sys_org a inner join sys_org_scope b on a.org_leaders = b.fielddatavalue where a.id in ("+orgidsStr+") and b.fielddata='org_leaders' ";
        System.out.println("sql====>>>queryOrgLeader=====>>>>>"+sql);
        List list = jdbcTemplate.queryForList(sql);
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                Map<String,Object> map = (Map<String,Object>)list.get(i);
                returnLeadersNames += (map.get("leadernames")+",");
            }
        }
        return returnLeadersNames;
    }
}
