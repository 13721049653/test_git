package org.ezplatform.caiwu.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.sqlserver.jdbc.StringUtils;
import org.ezplatform.workflow.service.SystemFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 2020-12-8.
 */
@Service(value="caiwuService")
public class CaiwuService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SystemFlowService systemFlowService;

    /**
     * 获取欠款金额
     * @return
     */
    public JSONArray getQK_money(String jklx,String fnumber){


        String whereSql = "";

        String jklxStr = "";
        String formTable = "";
        if("4".equals(jklx)){
            //个人
            jklxStr = "BD_Empinfo";
        }else if("0".equals(jklx) || "1".equals(jklx) || "2".equals(jklx)){
            jklxStr = "BD_Customer";
        }else{
            //供应商
            jklxStr = "BD_Supplier";
        }

        if("4".equals(jklx)){
            //个人
            formTable = "xfsmzjk.t_kdxf_staff@toerp";
        }else if("0".equals(jklx) || "1".equals(jklx) || "2".equals(jklx)){
            formTable = "xfsmzjk.t_kdxf_customer@toerp";
        }else{
            //供应商
            formTable = " xfsmzjk.t_kdxf_supply@toerp";
        }

        String sqlQueryXX = "select FID from "+formTable+"  where fnumber = '"+fnumber+"' ";
        System.out.println("sqlQueryXX====>>>"+sqlQueryXX);
        List listQuery = jdbcTemplate.queryForList(sqlQueryXX);
        if(listQuery!=null && listQuery.size()>0){
            whereSql =" F_KDXF_WLDWLX ='"+jklxStr+"' and F_KDXF_Item='"+((Map<String,Object>)listQuery.get(0)).get("FID")+"' ";
        }else{
            whereSql = "1<>1";
        }


        JSONObject queryJson=new JSONObject(true);
        queryJson.put("FormId","KDXF_YSKYE");
        queryJson.put("FieldKeys", " F_KDXF_YSYE ");//query
        queryJson.put("FilterString", whereSql);//where
        queryJson.put("OrderString", "");
        queryJson.put("TopRowCount",0);
        queryJson.put("StartRow", 0);
        queryJson.put("Limit", 0);
        JSONArray map = systemFlowService.queryFormInfo(queryJson);
        System.out.println("jiekuan:===>>>"+map);

        return map;
    }


    /**
     * 获取编号
     */
    public Map<String,Object> getCG_bm(String tableCode1 , String tableCode2){
        Map<String,Object> map = new HashMap<>();
        String sql1 = "select current_num from ( select current_num from biz_code_rule_inst where 1=1 and optlock=0 and rule_id in (select id from biz_code_rule where code = '"+tableCode1+"') order by createddate desc) where rownum=1";
        String sql2 = "select current_num from ( select current_num from biz_code_rule_inst where 1=1 and optlock=0 and rule_id in (select id from biz_code_rule where code = '"+tableCode2+"') order by createddate desc) where rownum=1";
        List list1 = jdbcTemplate.queryForList(sql1);
        List list2 = jdbcTemplate.queryForList(sql2);
        if(list1.size()>0){
            map.put( tableCode1,((Map<String,Object>)list1.get(0)).get("current_num")+"" );
        }else{
            map.put( tableCode1,"" );
        }

        if(list2.size()>0){
            map.put( tableCode2,((Map<String,Object>)list2.get(0)).get("current_num")+"" );
        }else{
            map.put( tableCode2,"" );
        }

        return map;
    }

    public String get_mcbybm(String bm){
        String returnBmname = "";
        String sql = "select FNAME from  XFSMZJK.T_KDXF_UNIT@toerp where fnumber = '"+bm+"' ";
        List list = jdbcTemplate.queryForList(sql);
        if(list!=null && list.size()>0){
            Map<String,Object> map = (Map<String,Object>)list.get(0);
            returnBmname = map.get("FNAME")+"";

        }
        return returnBmname;
    }

    public boolean get_hasRight_goType(String userId,String allChuxing){
        //boolean flag = true;
        Boolean flag = null;

        //判断是否超出标准获取级别
        String curDuty="";
        String getDutySql = " select  b.duty_level from sys_user a inner join sys_duty b on a.duty_id=b.id where a.is_delete=0 and b.is_delete=0 and a.id='"+userId+"' ";
        System.out.println("getDutySql========>>>>"+getDutySql);
        List listCurDuty = jdbcTemplate.queryForList(getDutySql);
        if(listCurDuty!=null && listCurDuty.size()>0){
            Map<String,Object> mapCurDuty = (Map<String,Object>)listCurDuty.get(0);
            curDuty = mapCurDuty.get("duty_level")+"";
        }

        //shifouEMT负责人
        String getEmtSql = " select * from sys_org_scope a where a.fielddata='deputy_leaders' and scopevalue in (select id from sys_user where id ='"+userId+"')  ";
        boolean isEmt = false;
        if(jdbcTemplate.queryForList(getEmtSql)!=null && jdbcTemplate.queryForList(getEmtSql).size()>0){
            isEmt = true;
        }

        //是否一级部门领导
        String getFirstSql = " select * from sys_org_scope a inner join sys_org b on a.fielddatavalue=b.org_leaders where b.org_level=3 and  a.fielddata='org_leaders' and a.scopevalue in (select id from sys_user where id ='"+userId+"') ";
        boolean isFirst = false;
        if(jdbcTemplate.queryForList(getFirstSql)!=null && jdbcTemplate.queryForList(getFirstSql).size()>0){
            isFirst = true;
        }


        System.out.println("curDuty========>>>>"+curDuty);
        //通过现有方式比较
        if(!"".equalsIgnoreCase(curDuty)){
            String[] allChuxingStr = allChuxing.split(",");
            for(int i=0;i<allChuxingStr.length;i++){
                //每次循环重置
                flag = null;

                String tempSimpleFangshi = allChuxingStr[i]+"";
                if("".equalsIgnoreCase(tempSimpleFangshi)){
                    continue;
                }

                //5级别及以上人员 或者部门负责人
                if(Integer.parseInt(curDuty) <= 5 || isFirst){
                    String checkHasSql = "select * from ccpbz where ybbmfzrjzyxl5jys='1' and cxbz ='"+tempSimpleFangshi+"' ";
                    System.out.println(checkHasSql);
                    List hasQueryCount = jdbcTemplate.queryForList(checkHasSql);
                    if(hasQueryCount==null || hasQueryCount.size()==0){
                        flag = null;
                    }else if(hasQueryCount!=null && hasQueryCount.size()>0){
                        flag = true;
                    }

                }

                //非级别判断
                if( (Integer.parseInt(curDuty) > 5 && flag==null) && !isFirst) {
                    String checkHasSql = "select * from ccpbz where ybyg='1' and cxbz ='"+tempSimpleFangshi+"' ";
                    System.out.println(checkHasSql);
                    List hasQueryCount = jdbcTemplate.queryForList(checkHasSql);
                    if(hasQueryCount==null || hasQueryCount.size()==0){
                        flag = null;
                    }else if(hasQueryCount!=null && hasQueryCount.size()>0){
                        flag = true;
                    }

                }

                //分管EMT判断(确认是分管emt)
                if(flag==null && isEmt){
                    String checkHasSql = "select * from ccpbz where emt='1' and cxbz ='"+tempSimpleFangshi+"' ";
                    System.out.println(checkHasSql);
                    List hasQueryCount = jdbcTemplate.queryForList(checkHasSql);
                    if(hasQueryCount==null || hasQueryCount.size()==0){
                        flag = null;
                    }else if(hasQueryCount!=null && hasQueryCount.size()>0){
                        flag = true;
                    }

                }

                //一个为空，即不满足
                if(flag==null){
                    flag = false;
                    break;
                }
            }
        }else{
            flag = false;
        }


        return flag;
    }

    /**
     * 获取当前工号
     * @param userId
     * @return
     */
    public String getQkCurperson(String userId){
        String returnVal = "";
        String sql = "select a.fnumber from xfsmzjk.t_kdxf_staff@toerp a inner join sys_user b on a.fusername = b.login_name where b.id='"+userId+"'";
        List list = jdbcTemplate.queryForList(sql);
        if(list!=null && list.size()>0){
            Map<String,Object> map = (Map<String,Object>)list.get(0);
            if(map!=null){
                returnVal = map.get("fnumber")+"";
            }
        }


        return returnVal;
    }


    public  static  void main(String [] a){
            String number="0.92";
            if(!StringUtils.isNumeric(number)) {
                number="0";
            }
            System.out.println("nub===0"+number);

        }

}
