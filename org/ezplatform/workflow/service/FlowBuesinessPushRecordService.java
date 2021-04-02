package org.ezplatform.workflow.service;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.core.service.BaseService;
import org.ezplatform.workflow.dao.FlowBuesinessPushRecordDao;
import org.ezplatform.workflow.entity.FlowBuesinessPushRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowBuesinessPushRecordService  extends BaseService<FlowBuesinessPushRecord, String>{
	
	@Autowired
	FlowBuesinessPushRecordDao flowBuesinessPushRecordDao;

	@Override
	protected JpaBaseDao<FlowBuesinessPushRecord, String> getEntityDao() {

		return this.flowBuesinessPushRecordDao;
	}

	public FlowBuesinessPushRecord getFlowBuesinessPushRecord(String processInstanceId) {

		return this.flowBuesinessPushRecordDao.getFlowBuesinessPushRecord(processInstanceId);
	}
	
	
	
}
