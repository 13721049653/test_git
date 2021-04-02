package org.ezplatform.workflow.listener;

import java.util.Map;

import javax.annotation.Resource;

import org.ezplatform.workflow.service.SystemFlowService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description: 调用业务系统接口的监听器 weixy
 */
@Component
public class BusinessCallListener implements ApplicationContextAware {
	private static final long serialVersionUID = -5140234938739863473L;
	protected Logger logger = LoggerFactory.getLogger(BusinessCallListener.class);

	private static ApplicationContext applicationContext;
	
	@Resource(name = "systemFlowService")
	private SystemFlowService systemFlowService;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public String dealERPActivity(DelegateExecution execution) {
		// initKingConfig();
		if (systemFlowService == null) {
			this.systemFlowService = (SystemFlowService) applicationContext.getBean("systemFlowService");
			//this.httpClientTemplate = (RestTemplate) applicationContext.getBean("restTemplate");
		}

		String processDefinitionId = execution.getProcessDefinitionId();
		//String extBussinesskey = execution.getExtBussinesskey();
		String processInstanceId = execution.getProcessInstanceId();
		systemFlowService.dealERPActivity(processInstanceId, processDefinitionId,true);
		return null;
	}

	/**
	 * 处理金蝶退回业务
	 * 
	 * @param paramMap
	 * @return
	 */
	public String backKingdeeService(Map<String, String> paramMap) {

		if (systemFlowService == null) {
			this.systemFlowService = (SystemFlowService) applicationContext.getBean("systemFlowService");
			//this.httpClientTemplate = (RestTemplate) applicationContext.getBean("restTemplate");
		}
		systemFlowService.backKingdeeService(paramMap);
		return "";
	}

	public String dealERPActivity(DelegateTask delegateTask) {
		

		// initKingConfig();
		if (systemFlowService == null) {
			this.systemFlowService = (SystemFlowService) applicationContext.getBean("systemFlowService");
			//this.httpClientTemplate = (RestTemplate) applicationContext.getBean("restTemplate");
		}

		String processDefinitionId = delegateTask.getProcessDefinitionId();
		//String extBussinesskey = execution.getExtBussinesskey();
		String processInstanceId = delegateTask.getProcessInstanceId();
		systemFlowService.dealERPActivity(processInstanceId, processDefinitionId,false);
		return null;
	
		
	}

	/**
	 * 处理业务表数据
	 * 
	 * @return
	 *//*
	public List<Object> dealTableData(String processInstanceId, String processDefinitionId) {
		List<Object> Parameters = new ArrayList<Object>();
		// 根据 processInstanceId 获取 流程code 及业务表数据主键
		Map<String, String> flowInfoMap = systemFlowService.getFlowInfo(processInstanceId, processDefinitionId);
		logger.info("flowInfoMap==============>" + flowInfoMap);
		// systemFlowService.dealFlow(flowInfoMap);
		// 流程定义Map
		Map<String, Map<String, String>> flowMap = SystemFlowService.getFlowMap();
		Map<String, List<String[]>> fieldMap = SystemFlowService.getFieldMap();
		// 获取子表Map
		Map<String, List<String[]>> fieldChildMap = SystemFlowService.getFieldChildMap();
		if (flowInfoMap != null) {
			String ext_processcode = flowInfoMap.get("ext_processcode");
			String business_key_ = flowInfoMap.get("business_key_");
			if (flowMap.get(ext_processcode) != null) {
				// 获取各个流程业务代办
				Map<String, String> tempMap = flowMap.get(ext_processcode);
				String tableName = tempMap.get("tableName");
				String className = tempMap.get("className");
				String classMethod = tempMap.get("classMethod");
				String formid = tempMap.get("formid");
				String dataTpye = tempMap.get("dataTpye");
				Parameters.add(dataTpye);
				Map<String, Object> tableData = new HashMap<String, Object>();
				if (StringUtils.isNotEmpty(tableName)) {
					// 获取业务表数据
					List<String[]> fieldList = fieldMap.get(ext_processcode);
					String searchField = "";// 主表数据查询字段
					String searchFieldTypes = "";// 主表数据字段类型
					String interFaceField = "";// 主表数据接口字段名
					String interFaceFieldChilds = "";// 主数据接口字段名子节点
					String childTables = "";// 子表名称
					String childTableInterFaceFields = "";// 子表接口字段名
					if (fieldList != null && fieldList.size() > 0) {
						for (int i = 0, size = fieldList.size(); i < size; i++) {
							String[] obj = fieldList.get(i);
							if ("table".equals(obj[1])) {
								// 子表数据
								childTables += obj[0] + ",";
								childTableInterFaceFields = obj[2] + ",";
								List<String[]> childFieldList = fieldChildMap.get(obj[0]);
								tableData.put(obj[2],
										systemFlowService.getChildTableData(obj[0], childFieldList, business_key_));

							} else {
								searchField += obj[0] + ",";
								searchFieldTypes += obj[1] + ",";
								interFaceField += obj[2] + ",";
								interFaceFieldChilds += obj[3] + ",";
							}

						}
						searchField = trimBothEndsChars(searchField, ",");
						searchFieldTypes = trimBothEndsChars(searchFieldTypes, ",");
						interFaceField = trimBothEndsChars(interFaceField, ",");
						interFaceFieldChilds = trimBothEndsChars(interFaceFieldChilds, ",");
						// 业务表数据查询sql
						String sql = "select " + searchField + " from " + tableName + " where id =:id ";
						Map<String, String> queryMap = new HashMap<String, String>();
						queryMap.put("id", business_key_);
						// 获取各个业务表数据
						Map<String, Object> tableDataTemp = systemFlowService.getTableDataMap(sql, searchField,
								interFaceField, interFaceFieldChilds, queryMap);
						tableData.putAll(tableDataTemp);
						String dataJson = JSONObject.toJSONString(tableData);
						logger.info("dataJson==============>" + dataJson);
						logger.info("formid==============>" + formid);
						if ("pushStatus".equals(dataTpye)) {
							Parameters.add(formid);
							Parameters.add(dataJson);
						} else {
							KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
									"true", JSONObject.parseObject(dataJson));
							Parameters.add(formid);
							Parameters.add(JSONObject.toJSON(appData).toString());
						}
					}
				}
			}
		}
		return Parameters;

	}

	// 添加容错信息
	// private void createWfBuesinessException(FlowBuesinessException fbe) {
	// try {
	// flowBuesinessExceptionService.insertFlowBuesinessException(fbe);
	// } catch (Exception e) {
	// logger.error("创建调用异常出错", e);
	// }
	// }

	*//**
	 * 初始化金蝶参数
	 *//*
	private void initKingConfig() {

		httpClientProperties = (HttpClientProperties) this.applicationContext.getBean("httpClientProperties");
		saveUrl = httpClientProperties.getKingUrl()
				+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Save.common.kdsvc";
		loginUrl = httpClientProperties.getKingUrl()
				+ "Kingdee.BOS.WebApi.ServicesStub.AuthService.ValidateUser.common.kdsvc";
		shUrl = httpClientProperties.getKingUrl()
				+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Audit.common.kdsvc";
		fshUrl = httpClientProperties.getKingUrl()
				+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.UnAudit.common.kdsvc";

		acctID = httpClientProperties.getAcctID();
		username = httpClientProperties.getUsername();
		password = httpClientProperties.getPassword();
		lcid = httpClientProperties.getLcid();
	}

	private boolean Login() {
		initKingConfig();
		boolean bResult = false;
		Map<String, Object> applicationDataMap = new HashMap<String, Object>();
		applicationDataMap.put("acctID", acctID);
		applicationDataMap.put("username", username);
		applicationDataMap.put("password", password);
		applicationDataMap.put("lcid", lcid);

		ResponseEntity<String> result = httpClientTemplate.postForEntity(loginUrl, applicationDataMap, String.class);
		String resJson = result.getBody().toString();
		JSONObject json = JSONObject.parseObject(resJson);

		int Loginresulttype = json.getIntValue("LoginResultType");
		String KDSVCSessionId = json.get("KDSVCSessionId") == null ? "" : json.get("KDSVCSessionId").toString();
		if (Loginresulttype == 1) {
			bResult = true;
		}
		return bResult;
	}

	*//**
	 * 去除字符串首尾两端指定的字符
	 *//*
	public String trimBothEndsChars(String srcStr, String splitter) {
		
		 * String regex = "^" + splitter + "*|" + splitter + "*$"; return
		 * srcStr.replaceAll(regex, "");
		 
		if (srcStr.endsWith(splitter)) {
			srcStr = srcStr.substring(0, srcStr.length() - 1);
		}
		return srcStr;
	}

	*//**
	 * 定时处理发送失败的任务
	 *//*
	@Scheduled(fixedDelay = 60000L)
	public void dealFailedKingdeeData() {

		// 获取失败的任务数据
		List<FlowBuesinessFail> list = flowBuesinessFailDao.findAll();
		List<FlowBuesinessFail> listsuccess = new ArrayList<FlowBuesinessFail>();
		if (list != null && list.size() > 0) {
			// 先登录ERP
			if (Login()) {
				String ids = "";// 推送成功的记录id
				for (FlowBuesinessFail po : list) {
					String jsonString = po.getData();
					String url = po.getKingdeeUrl();
					ResponseEntity<String> result = httpClientTemplate.postForEntity(url, jsonString, String.class);
					JSONObject json = JSONObject.parseObject(result.getBody().toString());
					String IsSuccess = json.getString("IsSuccess");
					if ("success".equals(IsSuccess)) {
						listsuccess.add(po);
					}

				}

				if (listsuccess.size() > 0) {
					// 删除推送成功的记录
					flowBuesinessFailDao.deleteInBatch(listsuccess);
				}
			}
		}

	}*/
}
