package org.ezplatform.workflow.web.rest;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ezplatform.core.exception.ValidationException;
import org.ezplatform.core.web.view.OperationResult;
import org.ezplatform.travel.service.TravelService2;
import org.ezplatform.util.StringUtils;
import org.ezplatform.workflow.entity.FlowBuesinessPushRecord;
import org.ezplatform.workflow.service.FlowBuesinessPushRecordService;
import org.ezplatform.workflow.service.SystemFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
@RestController
@RequestMapping({"/api/workflow/kingdee"})
public class WorkFlowKingdeeWebApiController {
	
	  @Autowired
	  private SystemFlowService systemFlowService;
	  @Autowired
	  private FlowBuesinessPushRecordService flowBuesinessPushRecordService ;
	  @Autowired 
	  private TravelService2 travelService;
	  @RequestMapping(value={"/pushERPFlow"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  @ResponseBody
	  public OperationResult pushERPFlow(HttpServletRequest request){
		String  msg="推送成功！";
		String processInstanceId=request.getParameter("processInstanceId");
		String processDefinitionId=request.getParameter("processDefinitionId");
		FlowBuesinessPushRecord po=new FlowBuesinessPushRecord();
	    if (StringUtils.isBlank(processInstanceId)) {
	      throw new ValidationException("非法参数");
	    }
	    po=flowBuesinessPushRecordService.getFlowBuesinessPushRecord(processInstanceId);
	    if(po!=null&&processInstanceId.equals(po.getProcessInstanceId())) {
	    	msg="不允许重复推送！";
	    }else {
		    boolean returnFlag=systemFlowService.dealERPActivity(processInstanceId, processDefinitionId,true);
		    if(returnFlag) {
		    	//记录推送记录
		    	po=new FlowBuesinessPushRecord();
		    	po.setProcessInstanceId(processInstanceId);
		    	flowBuesinessPushRecordService.save(po);
		    }else {
		    	msg="推送失败，请联系管理员！";
		    }
	    }
	    return OperationResult.buildSuccessResult("成功", msg);
	  }

	  
	  
	  @RequestMapping(value={"/getUserOrOrgIdByFNumber"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  @ResponseBody
	  public OperationResult getUserAndOrgId(HttpServletRequest request){
		String infoType=request.getParameter("infoType");//获取数据类型  user  org 
		String fNumber=request.getParameter("fNumber");
	    if (StringUtils.isBlank(infoType)) {
	      throw new ValidationException("非法参数");
	    }
	    Map<String, String> map= systemFlowService.getUserOrOrgIdByFNumber(fNumber, infoType);
	    String msg=JSONObject.toJSONString(map);
	    return OperationResult.buildSuccessResult("成功", msg);
	  }

	  @RequestMapping(value={"/getUserOrOrgIdByFNumberNew"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  @ResponseBody
	  public OperationResult getUserOrOrgIdByFNumberNew(HttpServletRequest request){
		String infoType=request.getParameter("infoType");//获取数据类型  user  org 
		String fNumber=request.getParameter("fNumber");
	    if (StringUtils.isBlank(infoType)) {
	      throw new ValidationException("非法参数");
	    }
	    JSONArray jsonArr= systemFlowService.getUserOrOrgIdJSON_NEW(fNumber, infoType);
	    String msg=jsonArr.toString();
	    return OperationResult.buildSuccessResult("成功", msg);
	  }
	  
	  
	  @RequestMapping(value={"/updateFormData"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  @ResponseBody
	  public OperationResult updateFormData(HttpServletRequest request){
		/*String issubtable=request.getParameter("issubtable");//是否子表  true  false
		String code=request.getParameter("code");
		String tableName=request.getParameter("tableName");*/
		String formData=request.getParameter("formData")==null?"":request.getParameter("formData").toString();
	   
		if (StringUtils.isBlank(formData)) {
	      throw new ValidationException("非法参数");
	    }
		
		
	    Map<String, String> map= systemFlowService.updateFormData(formData);
	    String msg=JSONObject.toJSONString(map);
	    return OperationResult.buildSuccessResult("成功", msg);
	    
	    
	  }
	  
	  
	  
	  
	  @RequestMapping(value={"/querySJXMYS"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  @ResponseBody
	  public OperationResult querySJXMYS(HttpServletRequest request){
		String xmbms=request.getParameter("xmbms")==null?"":request.getParameter("xmbms").toString();
	    String kmbms=request.getParameter("kmbms")==null?"":request.getParameter("kmbms").toString();
		if (StringUtils.isBlank(xmbms)) {
	      throw new ValidationException("非法参数");
	    }
		
			JSONObject queryJson=new JSONObject(true);
			queryJson.put("FormId","KDXF_SJXM");
			queryJson.put("FieldKeys", "FNumber,F_KDXF_KBXJE,F_KDXF_FYLB.fnumber");
			queryJson.put("FilterString", "FNumber in ("+xmbms+") and F_KDXF_FYLB.fnumber  in ("+kmbms+") ");
			queryJson.put("OrderString", "");
			queryJson.put("TopRowCount",0);
			queryJson.put("StartRow", 0);
			queryJson.put("Limit", 0);
			JSONArray zcbmMap = systemFlowService.queryFormInfo(queryJson);
	
			Map<String, BigDecimal> map = travelService.getFyMap(zcbmMap, false);
			
	    return OperationResult.buildSuccessResult("成功", map);
	    
	    
	  }
	  
	  /**
	   * 弹出框赋值筛选
	   * @param request
	   * @return
	   */
	  
	  @RequestMapping(value={"/querystintField"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  @ResponseBody
	  public OperationResult querystintField(HttpServletRequest request){
		String tableName=request.getParameter("tableName")==null?"":request.getParameter("tableName").toString();
	    String fieldName=request.getParameter("fieldName")==null?"":request.getParameter("fieldName").toString();
		if (StringUtils.isBlank(tableName)) {
	      throw new ValidationException("非法参数");
	    }
		String  seachname = systemFlowService.querystintField(tableName,fieldName);
	
			
	    return OperationResult.buildSuccessResult("成功", seachname);
	    
	    
	  }
	

}
