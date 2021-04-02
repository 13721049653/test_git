package org.ezplatform.workflow.web.rest;

import com.alibaba.fastjson.JSONArray;
import org.ezplatform.core.web.view.OperationResult;
import org.ezplatform.workflow.service.WorkFlowKingdeeBackOprService;
import org.ezplatform.workflow.service.workflowEnum.workflowEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jinxh on 2021-3-10.
 * 处理流程退回撤回等操作财务功能
 */
@RestController
@RequestMapping({"/api/workflowback/back"})
public class WorkFlowKingdeeBackOprController {

    protected Logger logger = LoggerFactory.getLogger(WorkFlowKingdeeBackOprController.class);

    @Autowired
    WorkFlowKingdeeBackOprService workFlowKingdeeBackOprService;

    /**
     * 财务退回
     * @param request
     * @return
     */
    @RequestMapping(value={"/backCaiwuflow"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getQK_money(HttpServletRequest request){

        String processCode = request.getParameter("processCode")+"";
        String erpdjbh = request.getParameter("erpdjbh")+"";
        String formid = workflowEnum.getName(processCode);

        ResponseEntity<String> result = workFlowKingdeeBackOprService.backCaiwu_bx(processCode , formid, erpdjbh);
        return OperationResult.buildSuccessResult(result);
    }


    /**
     * 获取erp单据编号
     * @param request
     * @return
     */
    @RequestMapping(value={"/getErpDjbh"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getErpDjbh(HttpServletRequest request){
        String businessKey = request.getParameter("businessKey")+"";
        String processCode = request.getParameter("processCode")+"";

        String result = workFlowKingdeeBackOprService.getErpDjbh(businessKey , processCode);
        return OperationResult.buildSuccessResult(result);
    }

}
