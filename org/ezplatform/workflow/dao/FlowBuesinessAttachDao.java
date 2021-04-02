package org.ezplatform.workflow.dao;

import java.util.List;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.workflow.entity.FlowBuesinessAttach;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("flowBuesinessAttachDao")
public abstract interface FlowBuesinessAttachDao extends JpaBaseDao<FlowBuesinessAttach, String>
{

 
  @Query(" from FlowBuesinessAttach  where  fbillno is null and processInstanceId=?1  ")
  public abstract List<FlowBuesinessAttach> getFlowBuesinessAttach(String processInstanceId);
  
  @Query(" from FlowBuesinessAttach  where  fbillno is not null   ")
  public abstract List<FlowBuesinessAttach> getFlowBuesinessAttach();
  
}