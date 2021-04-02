package org.ezplatform.wuziShen.web.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.ezplatform.core.common.WebUser;
import org.ezplatform.core.web.view.OperationResult;
import org.ezplatform.workflow.service.SystemFlowService;
import org.ezplatform.workflow.web.client.HttpClientProperties;
import org.ezplatform.wuziShen.web.service.ERPwaitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 2020-11-25.
 */

@RestController
@RequestMapping({"/api/ERPwait/info"})
public class ERPwaitController {

    protected Logger logger = LoggerFactory.getLogger(ERPwaitController.class);

    @Resource(name = "systemFlowService")
    private SystemFlowService systemFlowService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Autowired
    ERPwaitService eRPwaitService;
    @Autowired
    HttpClientProperties httpClientProperties;

    /**
     * 获取erp代办数量
     * @param request
     * @return
     */
    @RequestMapping(value={"/getErpNum"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getWuziDetail(HttpServletRequest request){
        String returnJsonArr=  "";
        WebUser webUser=org.ezplatform.core.web.util.WebUtils.getCurrentUser();
        String userId = request.getParameter("userId")+"";
        //获取库存

        returnJsonArr = eRPwaitService.loadERPNum(userId);

        return OperationResult.buildSuccessResult(returnJsonArr);
    }


    @RequestMapping(value={"/queryUserDealNum"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult queryUserDealNum(HttpServletRequest request){
        Map<String,Object> mapResult = new HashMap<>();
        try{
            String userId = request.getParameter("userId")+"";


            //获取userid
            //获取usercode
            String userNameTemp = "";
            String sql = "select a.FUSERID , b.LOGIN_NAME from xfsmzjk.T_KDXF_USER@toerp a inner join sys_user b on a.fuseraccount = b.login_name where b.id='"+userId+"' ";
            System.out.println("erpsql===>>>>"+sql);
            Map<String,Object> mapCode = null;
            if(jdbcTemplate.queryForList(sql + "").size()>0){
                mapCode = jdbcTemplate.queryForList(sql + "").get(0);
                if(mapCode!=null && mapCode.size()!=0){
                    userId = mapCode.get("FUSERID")+"";
                    userNameTemp = mapCode.get("LOGIN_NAME")+"";
                }
            }else{
                userId = "";
            }

            JSONObject queryJson=new JSONObject(true);
            queryJson.put("FormId","WF_AssignmentBill");
            queryJson.put("FieldKeys", "FASSIGNID,FTitle,FCONTENT,FCREATETIME,FTmpId.FProcessType");
            queryJson.put("FilterString", "FSTATUS = 0 and FReceiverId ='"+userId +"' ");
            queryJson.put("OrderString", "");
            queryJson.put("TopRowCount",0);
            queryJson.put("StartRow", 0);
            queryJson.put("Limit", 0);
            JSONArray zcbmMap = systemFlowService.queryFormInfo(queryJson);
            System.out.println("待办总数 "+zcbmMap.size());

            System.out.println("userNameTemp:====>>>>ERP=====>>>>"+userNameTemp);
            //获取ud
            String url = eRPwaitService.createUD(userNameTemp) + "";

            if("".equalsIgnoreCase(userId)){
                mapResult.put("erpNum","0");
            }else{
                mapResult.put("erpNum",zcbmMap.size()+"");
            }
            mapResult.put("ud",url);

        }catch(Exception e){
            mapResult.put("erpNum","0");
            mapResult.put("ud","");
            //e.printStackTrace();
        }
        return OperationResult.buildSuccessResult("成功", mapResult);
    }
    
    
    /**
     * erp登出
     * @param request
     * @return
     */
    @RequestMapping(value={"/ErplogOut"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult ErplogOut(HttpServletRequest request){
        String userId=request.getParameter("userId")==null?"":request.getParameter("userId");
        eRPwaitService.loginOut(userId);
        return OperationResult.buildSuccessResult("success");
    }


}
