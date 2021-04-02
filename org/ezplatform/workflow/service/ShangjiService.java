package org.ezplatform.workflow.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.core.service.BaseService;
import org.ezplatform.util.StringUtils;
import org.ezplatform.workflow.dao.FlowBuesinessFailDao;
import org.ezplatform.workflow.entity.FlowBuesinessAttach;
import org.ezplatform.workflow.entity.FlowBuesinessFail;
import org.ezplatform.workflow.entity.KingdeeApplicationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 财务类报销流程
 * @author WEI
 *
 */
@Service(value="shangjiService")
public class ShangjiService extends BaseService<FlowBuesinessFail, String>{
	@Autowired
    @Qualifier("flowBuesinessFailDao")
	private FlowBuesinessFailDao flowBuesinessFailDao;
	
	
	@Autowired
	private SystemFlowService systemFlowService;
	
	
	@Override
	protected JpaBaseDao<FlowBuesinessFail, String> getEntityDao() {
		// TODO Auto-generated method stub
		return this.flowBuesinessFailDao;
	}
	
	
	
	public JSONObject getShangjiLixiang(JSONObject jsonData) {
		// 获取科目列表
		JSONArray kmArr=jsonData.getJSONArray("F_KDXF_SJLXEntity");
		JSONArray resultArr= new JSONArray();
		if(kmArr!=null&&kmArr.size()>0) {
			for(int i=0,size=kmArr.size();i<size;i++) {
				JSONObject json=kmArr.getJSONObject(i);
				JSONObject jsonnew= new JSONObject();
				JSONObject F_KDXF_FYLB1=json.getJSONObject("F_KDXF_FYLB1");
				String F_KDXF_YWCBFY =json.getString("F_KDXF_YWCBFY");
				jsonnew.put("F_KDXF_FYLB", F_KDXF_FYLB1);
				jsonnew.put("F_KDXF_YSJE", F_KDXF_YWCBFY);
				resultArr.add(jsonnew);
			}
		}
		
		jsonData.put("F_KDXF_FYYSEntity", JSONObject.parse(resultArr.toString()));
		return jsonData;
	}
	

}
