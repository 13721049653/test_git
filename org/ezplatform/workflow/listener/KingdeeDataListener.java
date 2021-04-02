package org.ezplatform.workflow.listener;
import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class KingdeeDataListener extends BusinessCallListener implements  TaskListener, ExecutionListener   {
	private static final Logger log = LoggerFactory.getLogger(KingdeeDataListener.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		 log.info("task event:" + delegateTask.getEventName() + " =======2======" + delegateTask);
		
		 String eventName = delegateTask.getEventName();
	        // ActivitiEventType.PROCESS_STARTED
	        if ("create".endsWith(eventName)) {
	            log.info("create=========");
	        } else if ("assignment".endsWith(eventName)) {
	            log.info("assignment========");
	        } else if ("complete".endsWith(eventName)) {
	            log.info("complete===========");
	           super.dealERPActivity(delegateTask);
	        } else if ("delete".endsWith(eventName)) {
	            log.info("delete=============");
	        }
	        // delegateTask 可以操作activiti引擎的一些东西
		   
	}

	

	@Override
	public void notify(DelegateExecution execution) {
		// TODO Auto-generated method stub
		 log.info("execution event:" + execution.getEventName()+ " =======2======" );
		 if("end".equals(execution.getEventName())) {
			 super.dealERPActivity(execution);
		 }
	}

	/*public static void main(String[] args) {
		List<Object> Parameters = new ArrayList<Object>();
		 //业务对象Id 
		 String formid = "SAL_OUTSTOCK";//销售出库为例
		 Parameters.add(formid);
		  //Json字串
		  String data = "{\"Creator\":\"\",\"NeedUpDateFields\":[],\"Model\":" +            "{\"FID\":\"0\",\"FStockOrgId\":{\"FNumber\":\"210\"},\"FBillTypeID\":{\"FNumber\":\"XSCKD01_SYS\"},\"FBillNo\":\"CSDGBC21002\",\"FCustomerID\":{\"FNumber\":\"CUST0073\"},\"SubHeadEntity\":{\"FExchangeRate\":6.51},\"FEntity\":[{\"FEntryID\":\"0\",\"FMATERIALID\":{\"FNumber\":\"03.001\"},\"FStockID\":{\"FNumber\":\"CK002\"},\"FRealQty\":324,\"FBaseUnitQty\":324},{\"FEntryID\":\"0\",\"FMATERIALID\":{\"FNumber\":\"03.001\"},\"FStockID\":{\"FNumber\":\"CK004\"},\"FRealQty\":220,\"FBaseUnitQty\":220}]]}}";
		  Parameters.add(data);
		  JsonConvert.SerializeObject(Parameters);
	}*/




}
