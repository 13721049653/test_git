//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.ezplatform.bpm.engine.service;

import com.alibaba.fastjson.JSON;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.drools.core.util.StringUtils;
import org.ezplatform.bpm.admin.entity.BpmCategory;
import org.ezplatform.bpm.admin.service.BpmCategoryService;
import org.ezplatform.bpm.admin.service.BpmProcessDefineService;
import org.ezplatform.util.GlobalConstant;
import org.ezplatform.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BpmStatisticsApiService {
    @Autowired
    public BpmProcessDefineService bpmProcessDefineService;
    @Autowired
    public BpmEngineService workflowService;
    @Autowired
    private BpmCategoryService bpmCategoryService;
    @Autowired
    private BpmCacheService bpmCacheService;
    private static final Logger LOGGER = LoggerFactory.getLogger(BpmApiService.class);

    public BpmStatisticsApiService() {
    }

    public List getParticipantStatistics(String corpId, String processCode, String activityId, String searchData) {

        System.out.println("333333333333333333333333333333333");
        List list = new ArrayList();
        Date beginDate = null;
        Date endDate = null;
        String status = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(searchData != null) {
            Map searchDataMap = JsonUtils.readValue(searchData);
            status = (String)searchDataMap.get("status");
            String beginDataStr = (String)searchDataMap.get("createTimeBegin");
            if(!StringUtils.isEmpty(beginDataStr)) {
                try {
                    beginDate = sdf.parse(beginDataStr);
                } catch (ParseException var19) {
                    var19.printStackTrace();
                }
            }

            String endDataStr = (String)searchDataMap.get("createTimeEnd");
            if(!StringUtils.isEmpty(endDataStr)) {
                try {
                    endDate = sdf.parse(endDataStr);
                    endDate.setHours(23);
                    endDate.setMinutes(59);
                    endDate.setSeconds(59);
                } catch (ParseException var18) {
                    var18.printStackTrace();
                }
            }
        }

        String sql;
        label96: {
            sql = " select t1.NAME_ as NAME ,t.ASSIGNEE_ as USERID,t1.KEY_  as CODE,t.TASK_DEF_KEY_ as ACTID,t.NAME_ as ACTNAME, \t\tcount(*) as NUM , AVG( TIMESTAMPDIFF(MINUTE ,t.START_TIME_,t.END_TIME_)) \t\t AS HOUR from act_hi_taskinst t,act_re_procdef t1,act_hi_procinst t3   LEFT OUTER JOIN act_hi_varinst VAR ON VAR.PROC_INST_ID_ =  t3.ID_  AND VAR.NAME_ = 'ext_indraft'  \t\twhere  VAR.TEXT_ IS  NULL and   t3.DELETE_REASON_ IS NULL and t.PROC_INST_ID_=t3.ID_ and t.PROC_DEF_ID_ = t1.ID_  and t.ASSIGNEE_ is not null and  t.TASK_DEF_KEY_=:activityId and t1.KEY_=:processCode ";
            GlobalConstant.getInstance();
            if(!GlobalConstant.isOracle()) {
                GlobalConstant.getInstance();
                if(!GlobalConstant.isABase()) {
                    GlobalConstant.getInstance();
                    if(!GlobalConstant.isKingbase()) {
                        GlobalConstant.getInstance();
                        if(GlobalConstant.isDM()) {
                            sql = " select max(t1.NAME_) as NAME ,max(t.ASSIGNEE_) as USERID,max(t1.KEY_)  as CODE,max(t.TASK_DEF_KEY_) as ACTID,max(t.NAME_) as ACTNAME, \t\tcount(*) as NUM , AVG(  ( t.END_TIME_ -t.START_TIME_ ) *24 ) \t\t AS HOUR from act_hi_taskinst t,act_re_procdef t1,act_hi_procinst t3   LEFT OUTER JOIN act_hi_varinst VAR ON VAR.PROC_INST_ID_ =  t3.ID_  AND VAR.NAME_ = 'ext_indraft'  \t\twhere  VAR.TEXT_ IS  NULL and   t3.DELETE_REASON_ IS NULL \t\tand t.PROC_INST_ID_=t3.ID_ and t.PROC_DEF_ID_ = t1.ID_  and t.ASSIGNEE_ is not null and  t.TASK_DEF_KEY_=:activityId and t1.KEY_=:processCode ";
                        } else {
                            GlobalConstant.getInstance();
                            if(GlobalConstant.isSQLServer()) {
                                sql = " select max(t1.NAME_) as NAME ,max(t.ASSIGNEE_) as USERID,max(t1.KEY_)  as CODE,max(t.TASK_DEF_KEY_) as ACTID,max(t.NAME_) as ACTNAME, \t\tcount(*) as NUM , AVG( DATEDIFF(MINUTE ,t.START_TIME_,t.END_TIME_)) \t\t AS HOUR from act_hi_taskinst t,act_re_procdef t1,act_hi_procinst t3   LEFT OUTER JOIN act_hi_varinst VAR ON VAR.PROC_INST_ID_ =  t3.ID_  AND VAR.NAME_ = 'ext_indraft'  \t\twhere  VAR.TEXT_ IS  NULL and   t3.DELETE_REASON_ IS NULL and t.PROC_INST_ID_=t3.ID_ and t.PROC_DEF_ID_ = t1.ID_  and t.ASSIGNEE_ is not null and  t.TASK_DEF_KEY_=:activityId and t1.KEY_=:processCode  ";
                            }
                        }
                        break label96;
                    }
                }
            }

            sql = " select max(t1.NAME_) as NAME ,max(t.ASSIGNEE_) as USERID,max(t1.KEY_)  as CODE,max(t.TASK_DEF_KEY_) as ACTID,max(t.NAME_) as ACTNAME, \t\tcount(*) as NUM , AVG(  EXTRACT(SECOND FROM( t.END_TIME_ -t.START_TIME_ )) /60 ) \t\t AS HOUR from act_hi_taskinst t,act_re_procdef t1,act_hi_procinst t3   LEFT OUTER JOIN act_hi_varinst VAR ON VAR.PROC_INST_ID_ =  t3.ID_  AND VAR.NAME_ = 'ext_indraft'  \t\twhere  VAR.TEXT_ IS  NULL and   t3.DELETE_REASON_ IS NULL \t\tand t.PROC_INST_ID_=t3.ID_ and t.PROC_DEF_ID_ = t1.ID_  and t.ASSIGNEE_ is not null and  t.TASK_DEF_KEY_=:activityId and t1.KEY_=:processCode ";
        }

        if(beginDate != null) {
            sql = sql + " and t3.START_TIME_  >=  :beginDate  ";
        }

        if(endDate != null) {
            sql = sql + " and t3.START_TIME_ < :endDate ";
        }

        if(!StringUtils.isEmpty(status)) {
            sql = sql + " and t3.EXT_PROCESSSTATUS = '" + status + "'";
        }

        sql = sql + "  GROUP BY t.ASSIGNEE_ ";
        Map map = new HashMap();
        if(processCode != null) {
            map.put("processCode", processCode);
        }

        if(activityId != null) {
            map.put("activityId", activityId);
        }

        if(beginDate != null) {
            map.put("beginDate", beginDate);
        }

        if(endDate != null) {
            map.put("endDate", endDate);
        }

        List<Map> result = this.workflowService.findByListNativeQuery(sql, " ", map);

        for(int i = 0; i < result.size(); ++i) {
            BpmStatisticsApiService.StatisticsData data = new BpmStatisticsApiService.StatisticsData();
            String hour = ((Map)result.get(i)).get("HOUR") == null?"0.00":String.format("%.2f", new Object[]{Float.valueOf(Math.abs(Float.parseFloat(((Map)result.get(i)).get("HOUR").toString())))});
            String day = null;
            if(!StringUtils.isEmpty(hour)) {
                float hourInt = Float.parseFloat(hour) / 60.0F;
                day = String.format("%.2f", new Object[]{Float.valueOf(Math.abs(hourInt / 24.0F))});
            }

            data.setProcessName((String)((Map)result.get(i)).get("NAME"));
            data.setProcessCode((String)((Map)result.get(i)).get("CODE"));
            data.setActivityName((String)((Map)result.get(i)).get("ACTNAME"));
            data.setParticipant((String)((Map)result.get(i)).get("USERID"));
            if(!StringUtils.isEmpty((String)((Map)result.get(i)).get("USERID"))) {
                String username = this.bpmCacheService.findUserName((String)((Map)result.get(i)).get("USERID"), corpId);
                if(username == null) {
                    continue;
                }

                data.setParticipantName(username);
            }

            data.setActivityId((String)((Map)result.get(i)).get("ACTID"));
            data.setInstancesNum(((Map)result.get(i)).get("NUM") == null?"0":((Map)result.get(i)).get("NUM").toString());
            data.setAverageDay(day);
            data.setAverageHour(hour);
            list.add(data);
        }

        return list;
    }

    private void clearMapData(Map data) {
        data.remove("formData");
        data.remove("allEditIds");
        data.remove("comment");
        data.remove("editType");
        data.remove("selectedActivity");
        data.remove("dataId");
    }

    public List getActivityStatistics(String processCode, String searchData, String corpId) {
        System.out.println("2222222222222222222222222222222222222222222222222");
        List list = new ArrayList();
        Date beginDate = null;
        Date endDate = null;
        String status = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(searchData != null) {
            Map searchDataMap = JsonUtils.readValue(searchData);
            status = (String)searchDataMap.get("status");
            String beginDataStr = (String)searchDataMap.get("createTimeBegin");
            if(!StringUtils.isEmpty(beginDataStr)) {
                try {
                    beginDate = sdf.parse(beginDataStr);
                } catch (ParseException var18) {
                    var18.printStackTrace();
                }
            }

            String endDataStr = (String)searchDataMap.get("createTimeEnd");
            if(!StringUtils.isEmpty(endDataStr)) {
                try {
                    endDate = sdf.parse(endDataStr);
                    endDate.setHours(23);
                    endDate.setMinutes(59);
                    endDate.setSeconds(59);
                } catch (ParseException var17) {
                    var17.printStackTrace();
                }
            }
        }

        String sql;
        label86: {
            sql = "SELECT t2.name  as NAME,t2.code  as CODE,t2.ACTID  as ACTID,t2.ACTNAME  AS ACTNAME,COUNT(*) AS num_,AVG( TIMESTAMPDIFF(MINUTE ,t2.START_TIME_,t2.END_TIME_)) AS hour_ FROM (SELECT t1.NAME_ AS NAME ,t1.KEY_ AS CODE,t.ACT_ID_ AS ACTID,t.ACT_NAME_ AS ACTNAME ,t.START_TIME_,t.END_TIME_  FROM act_hi_actinst t,act_re_procdef t1,act_hi_procinst t3   WHERE \tt.ASSIGNEE_ is not null and  t.PROC_INST_ID_=t3.ID_ and t.PROC_DEF_ID_ = t1.ID_   AND t1.KEY_=:processCode ";
            GlobalConstant.getInstance();
            if(!GlobalConstant.isOracle()) {
                GlobalConstant.getInstance();
                if(!GlobalConstant.isABase()) {
                    GlobalConstant.getInstance();
                    if(!GlobalConstant.isKingbase()) {
                        GlobalConstant.getInstance();
                        if(GlobalConstant.isDM()) {
                            sql = "SELECT max(t2.name)  as NAME,max(t2.code)  as CODE,max(t2.ACTID)  as ACTID,max(t2.ACTNAME)  AS ACTNAME,COUNT(*) AS num_,AVG( ( t2.END_TIME_ -t2.START_TIME_ )*24 ) AS hour_ FROM (SELECT t1.NAME_ AS NAME ,t1.KEY_ AS CODE,t.ACT_ID_ AS ACTID,t.ACT_NAME_ AS ACTNAME ,t.START_TIME_,t.END_TIME_  FROM act_hi_actinst t,act_re_procdef t1,act_hi_procinst t3   WHERE \tt.ASSIGNEE_ is not null and t.PROC_INST_ID_=t3.ID_ and t.PROC_DEF_ID_ = t1.ID_   AND t1.KEY_=:processCode ";
                        } else {
                            GlobalConstant.getInstance();
                            if(GlobalConstant.isSQLServer()) {
                                sql = "SELECT max(t2.name)  as NAME,max(t2.code)  as CODE,max(t2.ACTID)  as ACTID,max(t2.ACTNAME)  AS ACTNAME,COUNT(*) AS num_,AVG( DATEDIFF(MINUTE ,t2.START_TIME_,t2.END_TIME_)) AS hour_ FROM (SELECT t1.NAME_ AS NAME ,t1.KEY_ AS CODE,t.ACT_ID_ AS ACTID,t.ACT_NAME_ AS ACTNAME ,t.START_TIME_,t.END_TIME_  FROM act_hi_actinst t,act_re_procdef t1,act_hi_procinst t3   WHERE \tt.ASSIGNEE_ is not null and  t.PROC_INST_ID_=t3.ID_ and t.PROC_DEF_ID_ = t1.ID_   AND t1.KEY_=:processCode ";
                            }
                        }
                        break label86;
                    }
                }
            }

            sql = "SELECT max(t2.name)  as NAME,max(t2.code)  as CODE,max(t2.ACTID)  as ACTID,max(t2.ACTNAME)  AS ACTNAME,COUNT(*) AS num_,AVG(  EXTRACT(DAY FROM( t2.END_TIME_ -t2.START_TIME_ )) * 24 +  EXTRACT(HOUR FROM( t2.END_TIME_ -t2.START_TIME_ ))  +  EXTRACT(MINUTE FROM( t2.END_TIME_ -t2.START_TIME_ )) / 60 ) AS hour_ FROM (SELECT t1.NAME_ AS NAME ,t1.KEY_ AS CODE,t.ACT_ID_ AS ACTID,t.ACT_NAME_ AS ACTNAME ,t.START_TIME_,t.END_TIME_  FROM act_hi_actinst t,act_re_procdef t1,act_hi_procinst t3   WHERE \tt.ASSIGNEE_ is not null and t.PROC_INST_ID_=t3.ID_ and t.PROC_DEF_ID_ = t1.ID_   AND t1.KEY_=:processCode ";
        }

        if(beginDate != null) {
            sql = sql + " and t3.START_TIME_ >= :beginDate ";
        }

        if(endDate != null) {
            sql = sql + " and t3.START_TIME_ < :endDate ";
        }

        if("1".equals(status)) {
            sql = sql + " and t3.END_TIME_ is null ";
        }

        if("2".equals(status)) {
            sql = sql + " and t3.END_TIME_ is not null ";
        }

        sql = sql + "  AND t.DELETE_REASON_ IS NULL   and not EXISTS (select  ID_  from  act_ru_variable vv  where vv.name_='ext_indraft' and  vv.PROC_INST_ID_=t.PROC_INST_ID_  )    ) t2  GROUP BY t2.ACTID ";
        Map map = new HashMap();
        if(processCode != null) {
            map.put("processCode", processCode);
        }

        if(beginDate != null) {
            map.put("beginDate", beginDate);
        }

        if(endDate != null) {
            map.put("endDate", endDate);
        }

        System.out.println("sqlsqlsqlsql  actttt=============>>>"+sql);

        List<Map> result = this.workflowService.findByListNativeQuery(sql, "", map);

        for(int i = 0; i < result.size(); ++i) {
            BpmStatisticsApiService.StatisticsData data = new BpmStatisticsApiService.StatisticsData();
            String hour = ((Map)result.get(i)).get("hour_") == null?"0.00":String.format("%.2f", new Object[]{Float.valueOf(Math.abs(Float.parseFloat(((Map)result.get(i)).get("hour_").toString())))});
            String day = null;
            if(!StringUtils.isEmpty(hour)) {
                float hourInt = Float.parseFloat(hour) / 60.0F;
                day = String.format("%.2f", new Object[]{Float.valueOf(Math.abs(hourInt / 24.0F))});
            }

            data.setProcessName((String)((Map)result.get(i)).get("NAME"));
            data.setProcessCode((String)((Map)result.get(i)).get("CODE"));
            data.setActivityName((String)((Map)result.get(i)).get("ACTNAME"));
            data.setActivityId((String)((Map)result.get(i)).get("ACTID"));
            data.setInstancesNum(((Map)result.get(i)).get("num_") == null?"0":((Map)result.get(i)).get("num_").toString());
            data.setAverageDay(day);
            data.setAverageHour(hour);
            list.add(data);
        }

        return list;
    }

    public List getProcessStatistics(String module, String categoryCode, String searchData, String corpId) {
        System.out.println("11111111111111111111111111111111111111111");
        List list = new ArrayList();
        Date beginDate = null;
        Date endDate = null;
        String status = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(searchData != null) {
            Map searchDataMap = JsonUtils.readValue(searchData);
            status = (String)searchDataMap.get("status");
            String beginDataStr = (String)searchDataMap.get("createTimeBegin");
            if(!StringUtils.isEmpty(beginDataStr)) {
                try {
                    beginDate = sdf.parse(beginDataStr);
                } catch (ParseException var20) {
                    var20.printStackTrace();
                }
            }

            String endDataStr = (String)searchDataMap.get("createTimeEnd");
            if(!StringUtils.isEmpty(endDataStr)) {
                try {
                    endDate = sdf.parse(endDataStr);
                    endDate.setHours(23);
                    endDate.setMinutes(59);
                    endDate.setSeconds(59);
                } catch (ParseException var19) {
                    var19.printStackTrace();
                }
            }
        }

        String sql = "select t1.NAME_ as NAME ,count(*) AS num_,t1.KEY_ AS CODE,AVG( TIMESTAMPDIFF(MINUTE ,t.START_TIME_,t.END_TIME_)) AS hour_ from act_hi_procinst t,act_re_procdef t1 where t.PROC_DEF_ID_ = t1.ID_  and t.DELETE_REASON_ is null  and not EXISTS (select ID_ from act_hi_varinst t3 where  t3. PROC_INST_ID_ = t.ID_ and  t3. NAME_ = 'ext_indraft') ";
        GlobalConstant.getInstance();
        if(GlobalConstant.isDM()) {
            sql = "select max(t1.NAME_) as NAME ,count(*) AS num_,max(t1.KEY_) AS CODE,AVG(   (t.END_TIME_ -t.START_TIME_)*24  \t ) AS hour_ from act_hi_procinst t,act_re_procdef t1 where t.PROC_DEF_ID_ = t1.ID_   and t.DELETE_REASON_ is null  and not EXISTS (select ID_ from act_hi_varinst t3 where  t3. PROC_INST_ID_ = t.ID_ and  t3. NAME_ = 'ext_indraft') ";
        } else {
            label140: {
                GlobalConstant.getInstance();
                if(!GlobalConstant.isOracle()) {
                    GlobalConstant.getInstance();
                    if(!GlobalConstant.isABase()) {
                        GlobalConstant.getInstance();
                        if(!GlobalConstant.isKingbase()) {
                            GlobalConstant.getInstance();
                            if(GlobalConstant.isSQLServer()) {
                                sql = "select max(t1.NAME_) as NAME ,count(*) AS num_,max(t1.KEY_) AS CODE,AVG( DATEDIFF(MINUTE ,t.START_TIME_,t.END_TIME_)) AS hour_ from act_hi_procinst t,act_re_procdef t1 where t.PROC_DEF_ID_ = t1.ID_  and t.DELETE_REASON_ is null  and not EXISTS (select ID_ from act_hi_varinst t3 where  t3. PROC_INST_ID_ = t.ID_ and  t3. NAME_ = 'ext_indraft') ";
                            }
                            break label140;
                        }
                    }
                }

                sql = "select max(t1.NAME_) as NAME ,count(*) AS num_,max(t1.KEY_) AS CODE,AVG(  EXTRACT(DAY FROM( t.END_TIME_ -t.START_TIME_ )) *24 +  EXTRACT(HOUR FROM( t.END_TIME_ -t.START_TIME_ ))  +  EXTRACT(MINUTE FROM( t.END_TIME_ -t.START_TIME_ )) /60 ) AS hour_ from act_hi_procinst t,act_re_procdef t1 where t.PROC_DEF_ID_ = t1.ID_   and t.DELETE_REASON_ is null  and not EXISTS (select ID_ from act_hi_varinst t3 where  t3. PROC_INST_ID_ = t.ID_ and  t3. NAME_ = 'ext_indraft') ";
            }
        }

        List result;
        if(module != null && !"".equals(module)) {
            String[] moduleArray = module.split(",");
            result = this.bpmCategoryService.getBpmCategoryListByModule(corpId, moduleArray);
            List<String> categoryCodeList = new ArrayList();

            for(int i = 0; i < result.size(); ++i) {
                if(((BpmCategory)result.get(i)).getCode() != null) {
                    categoryCodeList.add(((BpmCategory)result.get(i)).getCode());
                }
            }

            if(categoryCodeList.size() == 1) {
                if(categoryCodeList.get(0) != null) {
                    sql = sql + " AND t1.CATEGORY_ =  '" + (String)categoryCodeList.get(0) + "'  ";
                }
            } else if(categoryCodeList.size() == 0) {
                categoryCodeList.add("xxxxxxxxxx");
                sql = sql + " AND t1.CATEGORY_ = 'xxxxxxxx' ";
            } else {
                String cl = "";

                for(int i = 0; i < categoryCodeList.size(); ++i) {
                    if(i == 0) {
                        cl = "'" + (String)categoryCodeList.get(i) + "'";
                    } else {
                        cl = cl + ",'" + (String)categoryCodeList.get(i) + "'";
                    }
                }

                sql = sql + " AND t1.CATEGORY_ IN (" + cl + ") ";
            }
        }

        if(categoryCode != null) {
            sql = sql + " and t1.CATEGORY_=:categoryCode ";
        }

        if(beginDate != null) {
            sql = sql + " and t.START_TIME_ >= :beginDate ";
        }

        if(endDate != null) {
            sql = sql + " and t.START_TIME_ < :endDate ";
        }

        if("1".equals(status)) {
            sql = sql + " and t.END_TIME_ is null ";
        }

        if("2".equals(status)) {
            sql = sql + " and t.END_TIME_ is not null ";
        }

        sql = sql + " group by t1.KEY_";
        Map map = new HashMap();
        if(categoryCode != null) {
            map.put("categoryCode", categoryCode);
        }

        if(beginDate != null) {
            map.put("beginDate", beginDate);
        }

        if(endDate != null) {
            map.put("endDate", endDate);
        }

        LOGGER.debug("sqlsqlsqlsqlsqlsql:" + sql);
        result = this.workflowService.findByListNativeQuery(sql, "", map);
        LOGGER.debug("JSON:" + JSON.toJSONString(result));
        System.out.println("JSON:" + JSON.toJSONString(result));

        for(int i = 0; i < result.size(); ++i) {
            BpmStatisticsApiService.StatisticsData data = new BpmStatisticsApiService.StatisticsData();
            String hour = ((Map)result.get(i)).get("hour_") == null?"0.00":String.format("%.2f", new Object[]{Float.valueOf(Math.abs(Float.parseFloat(((Map)result.get(i)).get("hour_").toString())))});
            String day = null;
            if(!StringUtils.isEmpty(hour)) {
                float hourInt = Float.parseFloat(hour) / 60.0F;
                day = String.format("%.2f", new Object[]{Float.valueOf(Math.abs(hourInt / 24.0F))});
            }

            data.setProcessName((String)((Map)result.get(i)).get("NAME"));
            data.setProcessCode((String)((Map)result.get(i)).get("CODE"));
            if(StringUtils.isEmpty((String)((Map)result.get(i)).get("CODE"))) {
                data.setProcessCode((String)((Map)result.get(i)).get("code"));
            }

            try {
                data.setProcessName(this.bpmProcessDefineService.findByCodeAndCorpId(corpId, data.getProcessCode()).getName());
            } catch (Exception var18) {
                var18.printStackTrace();
            }

            data.setInstancesNum(((Map)result.get(i)).get("num_") == null?"0":((Map)result.get(i)).get("num_").toString());
            System.out.println("---------------------------------------------");
            System.out.println("day==>>"+day);
            System.out.println("hour==>>"+hour);
            System.out.println("---------------------------------------------");
            data.setAverageDay(day);
            data.setAverageHour(hour);
            list.add(data);
        }

        return list;
    }

    public List getDepartmentStatistics(String module, String searchData, String corpId) {
        List list = new ArrayList();
        Date beginDate = null;
        Date endDate = null;
        String status = null;
        String orgName = null;
        String orgName2 = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(searchData != null) {
            Map searchDataMap = JsonUtils.readValue(searchData);
            status = (String)searchDataMap.get("status");
            String beginDataStr = (String)searchDataMap.get("createTimeBegin");
            if(!StringUtils.isEmpty(beginDataStr)) {
                try {
                    beginDate = sdf.parse(beginDataStr);
                } catch (ParseException var22) {
                    var22.printStackTrace();
                }
            }

            String endDataStr = (String)searchDataMap.get("createTimeEnd");
            if(!StringUtils.isEmpty(endDataStr)) {
                try {
                    endDate = sdf.parse(endDataStr);
                    endDate.setHours(23);
                    endDate.setMinutes(59);
                    endDate.setSeconds(59);
                } catch (ParseException var21) {
                    var21.printStackTrace();
                }
            }

            orgName = (String)searchDataMap.get("orgName");
            orgName2 = (String)searchDataMap.get("parentOrgName");
        }

        String[] moduleArray = module.split(",");
        List<BpmCategory> mlist = this.bpmCategoryService.getBpmCategoryListByModule(corpId, moduleArray);
        List<String> categoryCodeList = new ArrayList();

        for(int i = 0; i < mlist.size(); ++i) {
            if(((BpmCategory)mlist.get(i)).getCode() != null) {
                categoryCodeList.add(((BpmCategory)mlist.get(i)).getCode());
            }
        }

        String sql;
        label127: {
            sql = " SELECT \tt.ASSIGNEE_ AS USERID,\tt3.org_name AS ORGNAME, t4.org_name as PORGNAME,\tcount(*) AS TASKCOUNT,\tAVG(\t\tTIMESTAMPDIFF(\t\t\tMINUTE,\t\t\tt.START_TIME_,\t\t\tt.END_TIME_\t\t)\t) AS TASKTIME FROM  act_re_procdef t5,    act_hi_procinst p,   sys_user t2     inner join  sys_org t3 on t3.id = t2.org_id    left join  sys_org t4  on  t4.id = t3.parent_id     inner join  act_hi_taskinst t on  t.ASSIGNEE_ = t2.id  LEFT OUTER JOIN act_hi_varinst VAR ON VAR.PROC_INST_ID_ = t.ID_ AND VAR.NAME_ = 'ext_indraft'  WHERE    t.TASK_DEF_KEY_ NOT LIKE 'ezflow_initiator_%'  AND t.PROC_DEF_ID_ = t5.ID_  AND VAR.TEXT_ IS NULL  AND t.PROC_INST_ID_ = p.ID_  AND t.ASSIGNEE_ IS NOT NULL  AND t.DELETE_REASON_ IS NULL  AND p.DELETE_REASON_ IS NULL  ";
            GlobalConstant.getInstance();
            if(!GlobalConstant.isOracle()) {
                GlobalConstant.getInstance();
                if(!GlobalConstant.isDM()) {
                    GlobalConstant.getInstance();
                    if(!GlobalConstant.isABase()) {
                        GlobalConstant.getInstance();
                        if(!GlobalConstant.isKingbase()) {
                            break label127;
                        }
                    }
                }
            }

            StringBuilder var10000 = new StringBuilder("SELECT    max(t.ASSIGNEE_) AS USERID,    max(t3.org_name) AS ORGNAME,    max(t4.org_name) AS PORGNAME,    count (*) AS TASKCOUNT,  ");
            GlobalConstant.getInstance();
            sql = var10000.append(GlobalConstant.isDM()?" AVG ((t.END_TIME_- t.START_TIME_)*24 ) as TASKTIME ":" AVG(  EXTRACT(SECOND FROM( t.END_TIME_ -t.START_TIME_ )) /60 ) as TASKTIME  ").append("FROM  ").append("  act_re_procdef t5,  ").append("  act_hi_procinst p,  ").append(" sys_user t2  ").append("   inner join  sys_org t3 on t3.id = t2.org_id  ").append("  left join  sys_org t4  on  t4.id = t3.parent_id   ").append("  inner join  act_hi_taskinst t on  t.ASSIGNEE_ = t2.id  ").append("LEFT OUTER JOIN act_hi_varinst VAR ON VAR.PROC_INST_ID_ = t.ID_ AND VAR.NAME_ = 'ext_indraft'  ").append("WHERE  ").append("  t.TASK_DEF_KEY_ NOT LIKE 'ezflow_initiator_%'  ").append("AND t.PROC_DEF_ID_ = t5.ID_  ").append("AND VAR.TEXT_ IS NULL  ").append("AND t.PROC_INST_ID_ = p.ID_  ").append("AND t.ASSIGNEE_ IS NOT NULL  ").append("AND t.DELETE_REASON_ IS NULL  ").append("AND p.DELETE_REASON_ IS NULL  ").toString();
            var10000 = new StringBuilder("SELECT \t \tmax( t3.org_name ) AS ORGNAME, \tmax( t4.org_name ) AS PORGNAME, \tcount( * ) AS TASKCOUNT, ");
            GlobalConstant.getInstance();
            sql = var10000.append(GlobalConstant.isDM()?" AVG ((t.END_TIME_- t.START_TIME_)*24 ) as TASKTIME ":" AVG(  EXTRACT(SECOND FROM( t.END_TIME_ -t.START_TIME_ )) /60 ) as TASKTIME  ").append("FROM ").append("\tact_re_procdef t5, ").append("\tact_hi_procinst p, ").append("\tsys_org t3  ").append("\tLEFT JOIN sys_org t4 ON t4.id = t3.parent_id ").append("\tINNER JOIN act_hi_taskinst t ON t.CREATEDORG = t3.id LEFT OUTER ").append("\tJOIN act_hi_varinst VAR ON VAR.PROC_INST_ID_ = t.ID_  ").append("\tAND VAR.NAME_ = 'ext_indraft'  ").append("WHERE ").append("\tt.TASK_DEF_KEY_ NOT LIKE 'ezflow_initiator_%'  ").append("\tAND t.PROC_DEF_ID_ = t5.ID_  ").append("\tAND VAR.TEXT_ IS NULL  ").append("\tAND t.PROC_INST_ID_ = p.ID_  ").append("\tAND t.ASSIGNEE_ IS NOT NULL  ").append("\tAND t.DELETE_REASON_ IS NULL  ").append("\tAND p.DELETE_REASON_ IS NULL").toString();
        }

        if(beginDate != null) {
            sql = sql + " and t.START_TIME_  >=  :beginDate  ";
        }

        if(endDate != null) {
            sql = sql + " and t.END_TIME_ < :endDate ";
        }

        if(categoryCodeList.size() == 0) {
            categoryCodeList.add("xxxxxxxxxx");
            sql = sql + " AND t5.CATEGORY_ = 'xxxxxxxx' ";
        } else {
            String cl = "";

            for(int i = 0; i < categoryCodeList.size(); ++i) {
                if(i == 0) {
                    cl = "'" + (String)categoryCodeList.get(i) + "'";
                } else {
                    cl = cl + ",'" + (String)categoryCodeList.get(i) + "'";
                }
            }

            sql = sql + " AND t5.CATEGORY_ IN (" + cl + ") ";
        }

        if(orgName2 != null) {
            sql = sql + " and t4.org_name like :orgName2 ";
        }

        if(orgName != null) {
            sql = sql + " and t3.org_name like :orgName ";
        }

        sql = sql + "  GROUP BY t3.id ";
        Map map = new HashMap();
        System.out.println("beginDate====>>>beginDate==============>>>" + beginDate);
        if(beginDate != null) {
            map.put("beginDate", beginDate);
        }

        if(endDate != null) {
            map.put("endDate", endDate);
        }

        if(!StringUtils.isEmpty(orgName)) {
            map.put("orgName", "%" + orgName + "%");
        }

        if(!StringUtils.isEmpty(orgName2)) {
            map.put("orgName2", "%" + orgName2 + "%");
        }

        System.out.println("sqlsqlsqlsql============>>>>"+sql);
        List<Map> result = this.workflowService.findByListNativeQuery(sql, " ", map);

        for(int i = 0; i < result.size(); ++i) {
            Map m = (Map)result.get(i);
            Number o = (Number)m.get("TASKTIME");
            if(o != null) {
                m.put("TASKTIME", Float.valueOf(Math.abs(o.floatValue())));
            } else {
                m.put("TASKTIME", Integer.valueOf(0));
            }

            list.add(result.get(i));
        }

        return list;
    }

    public List getPersonnelStatistics(String module, String searchData, String corpId) {
        List list = new ArrayList();
        Date beginDate = null;
        Date endDate = null;
        String status = null;
        String orgName = null;
        String userName = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(searchData != null) {
            Map searchDataMap = JsonUtils.readValue(searchData);
            status = (String)searchDataMap.get("status");
            String beginDataStr = (String)searchDataMap.get("createTimeBegin");
            if(!StringUtils.isEmpty(beginDataStr)) {
                try {
                    beginDate = sdf.parse(beginDataStr);
                } catch (ParseException var21) {
                    var21.printStackTrace();
                }
            }

            String endDataStr = (String)searchDataMap.get("createTimeEnd");
            if(!StringUtils.isEmpty(endDataStr)) {
                try {
                    endDate = sdf.parse(endDataStr);
                    endDate.setHours(23);
                    endDate.setMinutes(59);
                    endDate.setSeconds(59);
                } catch (ParseException var20) {
                    var20.printStackTrace();
                }
            }

            orgName = (String)searchDataMap.get("orgName");
            userName = (String)searchDataMap.get("userName");
        }

        String[] moduleArray = module.split(",");
        List<BpmCategory> mlist = this.bpmCategoryService.getBpmCategoryListByModule(corpId, moduleArray);
        List<String> categoryCodeList = new ArrayList();

        for(int i = 0; i < mlist.size(); ++i) {
            if(((BpmCategory)mlist.get(i)).getCode() != null) {
                categoryCodeList.add(((BpmCategory)mlist.get(i)).getCode());
            }
        }

        String sql;
        label122: {
            sql = " SELECT \tmax(t.ASSIGNEE_) AS USERID, \tmax(t2.user_name) AS USERNAME, \tmax(t3.org_name) AS ORGNAME, \tcount(*) AS TASKCOUNT, \tAVG( \t\tTIMESTAMPDIFF( \t\t\tMINUTE, \t\t\tt.START_TIME_, \t\t\tt.END_TIME_ \t\t) \t) AS TASKTIME  FROM  sys_user t2,  sys_org t3,  act_re_procdef t5 , act_hi_procinst p , \tact_hi_taskinst t LEFT OUTER JOIN act_hi_varinst VAR ON VAR.PROC_INST_ID_ = t.ID_ AND VAR.NAME_ = 'ext_indraft' LEFT OUTER JOIN act_hi_varinst VAR2 ON VAR2.PROC_INST_ID_ = t.ID_ AND VAR2.NAME_ = 'ext_isstart' WHERE t.TASK_DEF_KEY_ not like 'ezflow_initiator_%' and  t.PROC_DEF_ID_ = t5.ID_\tand p.ID_ = t.PROC_INST_ID_ and p.BUSINESS_KEY_ IS NOT NULL and VAR.TEXT_ IS NULL and VAR2.TEXT_ IS NULL AND t.ASSIGNEE_ = t2.id AND t3.id = t2.org_id AND t.ASSIGNEE_ IS NOT NULL ";
            GlobalConstant.getInstance();
            if(!GlobalConstant.isOracle()) {
                GlobalConstant.getInstance();
                if(!GlobalConstant.isDM()) {
                    GlobalConstant.getInstance();
                    if(!GlobalConstant.isABase()) {
                        GlobalConstant.getInstance();
                        if(!GlobalConstant.isKingbase()) {
                            break label122;
                        }
                    }
                }
            }

            StringBuilder var10000 = new StringBuilder(" SELECT \tmax(t.ASSIGNEE_) AS USERID, \tmax(t2.user_name) AS USERNAME, \tmax(t3.org_name) AS ORGNAME, \tcount(*) AS TASKCOUNT, ");
            GlobalConstant.getInstance();
            sql = var10000.append(GlobalConstant.isDM()?" AVG ((t.END_TIME_- t.START_TIME_)*24 ) as TASKTIME ":" AVG(  EXTRACT(SECOND FROM( t.END_TIME_ -t.START_TIME_ )) /60 ) as TASKTIME  ").append(" FROM ").append(" sys_user t2, ").append(" sys_org t3, ").append("act_re_procdef t5, act_hi_procinst p , act_hi_taskinst t ").append("LEFT OUTER JOIN act_hi_varinst VAR ON VAR.PROC_INST_ID_ = t.ID_ ").append("AND VAR.NAME_ = 'ext_indraft' ").append("LEFT OUTER JOIN act_hi_varinst VAR2 ON VAR2.PROC_INST_ID_ = t.ID_ ").append("AND VAR2.NAME_ = 'ext_isstart' ").append("WHERE ").append(" t.TASK_DEF_KEY_ not like 'ezflow_initiator_%' and t.PROC_DEF_ID_ = t5.ID_ and p.ID_ = t.PROC_INST_ID_ and p.BUSINESS_KEY_ IS NOT NULL and\tVAR.TEXT_ IS NULL and VAR2.TEXT_ IS NULL  ").append("AND t.ASSIGNEE_ = t2.id ").append("AND t3.id = t2.org_id ").append("AND t.ASSIGNEE_ IS NOT NULL ").append("AND t.DELETE_REASON_ IS NULL ").toString();
        }

        if(beginDate != null) {
            sql = sql + " and t.START_TIME_  >=  :beginDate  ";
        }

        if(endDate != null) {
            sql = sql + " and t.END_TIME_ < :endDate ";
        }

        if(categoryCodeList.size() == 0) {
            categoryCodeList.add("xxxxxxxxxx");
            sql = sql + " AND t5.CATEGORY_ = 'xxxxxxxx' ";
        } else {
            String cl = "";

            for(int i = 0; i < categoryCodeList.size(); ++i) {
                if(i == 0) {
                    cl = "'" + (String)categoryCodeList.get(i) + "'";
                } else {
                    cl = cl + ",'" + (String)categoryCodeList.get(i) + "'";
                }
            }

            sql = sql + " AND t5.CATEGORY_ IN (" + cl + ") ";
        }

        if(!StringUtils.isEmpty(orgName)) {
            sql = sql + " and t3.org_name like :orgName ";
        }

        if(!StringUtils.isEmpty(userName)) {
            sql = sql + " and t2.user_name like :userName ";
        }

        sql = sql + "  GROUP BY t.ASSIGNEE_ ";
        Map map = new HashMap();
        if(beginDate != null) {
            map.put("beginDate", beginDate);
        }

        if(endDate != null) {
            map.put("endDate", endDate);
        }

        if(!StringUtils.isEmpty(userName)) {
            map.put("userName", "%" + userName + "%");
        }

        if(!StringUtils.isEmpty(orgName)) {
            map.put("orgName", "%" + orgName + "%");
        }

        System.out.println("sql==========>>>==========>>>person======>>>"+sql);
        List<Map> result = this.workflowService.findByListNativeQuery(sql, " ", map);

        for(int i = 0; i < result.size(); ++i) {
            Map m = (Map)result.get(i);
            Number o = (Number)m.get("TASKTIME");
            if(o != null) {
                m.put("TASKTIME", Float.valueOf(Math.abs(o.floatValue())));
            } else {
                m.put("TASKTIME", Integer.valueOf(0));
            }

            list.add(result.get(i));
        }

        return list;
    }

    class StatisticsData {
        private String processCode;
        private String processName;
        private String activityId;
        private String activityName;
        private String participant;
        private String participantName;
        private String status;
        private String instancesNum;
        private String averageHour;
        private String averageDay;

        StatisticsData() {
        }

        public String getParticipant() {
            return this.participant;
        }

        public void setParticipant(String participant) {
            this.participant = participant;
        }

        public String getProcessCode() {
            return this.processCode;
        }

        public void setProcessCode(String processCode) {
            this.processCode = processCode;
        }

        public String getProcessName() {
            return this.processName;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }

        public String getActivityId() {
            return this.activityId;
        }

        public void setActivityId(String activityId) {
            this.activityId = activityId;
        }

        public String getActivityName() {
            return this.activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getParticipantName() {
            return this.participantName;
        }

        public void setParticipantName(String participantName) {
            this.participantName = participantName;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInstancesNum() {
            return this.instancesNum;
        }

        public void setInstancesNum(String instancesNum) {
            this.instancesNum = instancesNum;
        }

        public String getAverageHour() {
            return this.averageHour;
        }

        public void setAverageHour(String averageHour) {
            this.averageHour = averageHour;
        }

        public String getAverageDay() {
            return this.averageDay;
        }

        public void setAverageDay(String averageDay) {
            this.averageDay = averageDay;
        }
    }
}
