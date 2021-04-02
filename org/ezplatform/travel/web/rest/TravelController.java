package org.ezplatform.travel.web.rest;

import javax.servlet.http.HttpServletRequest;

import org.ezplatform.core.common.WebUser;
import org.ezplatform.core.web.view.OperationResult;
import org.ezplatform.travel.entity.ButieEntity;
import org.ezplatform.travel.service.TravelService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping({"/api/travle/travel"})
public class TravelController {
	  protected Logger logger = LoggerFactory.getLogger(TravelController.class);
	@Autowired
    TravelService2 travelService;
	
		/**
		 * 差旅补贴
		 * @param request
		 * @return
		 */
	  @RequestMapping(value={"/getTravelButie"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  @ResponseBody
	  public OperationResult getTravelButie(HttpServletRequest request){
		  String msg="";
		  String formData=request.getParameter("formData")==null?"":request.getParameter("formData").toString();
		  String userId=request.getParameter("userId")==null?"":request.getParameter("userId").toString();
		  WebUser webUser=org.ezplatform.core.web.util.WebUtils.getCurrentUser();
		 // JSONObject formDataJson =JSONObject.parseObject(formData);
		  ButieEntity butie= travelService.dealTravelData(formData,userId);

		  return OperationResult.buildSuccessResult("成功",JSONObject.toJSON(butie).toString());
	 }
	
	  
	  /**
	   * 预算管控
	   * @param request
	   * @return
	   */
	  @RequestMapping(value={"/getYsgk"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  @ResponseBody
	  public OperationResult getYsgk(HttpServletRequest request){
		  String msg="";
		  String formData=request.getParameter("formData")==null?"":request.getParameter("formData").toString();
		  String userId=request.getParameter("userId")==null?"":request.getParameter("userId").toString();
		  String operate=request.getParameter("operate")==null?"start":request.getParameter("operate").toString();
		  WebUser webUser=org.ezplatform.core.web.util.WebUtils.getCurrentUser();
		  msg= travelService.dealYsgkData(formData,userId,operate);
		 
		  return OperationResult.buildSuccessResult("成功",msg);
	 }
	  //获取信用等级
	  @RequestMapping(value={"/getXydj"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  @ResponseBody
	  public OperationResult getXydj(HttpServletRequest request){
		  String msg="";
		  String userId=request.getParameter("userId")==null?"":request.getParameter("userId").toString();
		  msg= travelService.dealXydjData(userId);
		 
		  return OperationResult.buildSuccessResult("成功",msg);
	 }
	  
}
