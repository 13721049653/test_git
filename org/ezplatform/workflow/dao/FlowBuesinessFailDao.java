package org.ezplatform.workflow.dao;

import java.util.Date;
import java.util.List;
import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.workflow.entity.FlowBuesinessFail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("flowBuesinessFailDao")
public abstract interface FlowBuesinessFailDao extends JpaBaseDao<FlowBuesinessFail, String>
{

 
}