package org.ezplatform.workflow.dao;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.workflow.entity.FlowBuesinessPushRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("flowBuesinessPushRecordDao")
public abstract interface FlowBuesinessPushRecordDao extends JpaBaseDao<FlowBuesinessPushRecord, String>
{

 
  @Query(" from FlowBuesinessPushRecord  where processInstanceId=?1  ")
  public abstract FlowBuesinessPushRecord getFlowBuesinessPushRecord(String processInstanceId);
}