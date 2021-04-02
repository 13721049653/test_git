package org.ezplatform.bpm.engine.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ezplatform.bpm.admin.entity.BpmProcessStatistics;
import org.ezplatform.bpm.admin.service.BpmProcessOvertimeService;
import org.ezplatform.bpm.admin.service.BpmProcessStatisticsService;
import org.ezplatform.bpm.admin.service.BpmProcessTimelimitService;
import org.ezplatform.bpm.admin.util.BpmUserUtil;
import org.ezplatform.bpm.engine.cmd.UpdateProcessStatusCmd;
import org.ezplatform.bpm.engine.entity.BpmTaskMessage;
import org.ezplatform.bpm.engine.service.BpmCommentService;
import org.ezplatform.bpm.engine.service.BpmEngineService;
import org.ezplatform.bpm.engine.service.BpmReadTaskApiService;
import org.ezplatform.bpm.engine.service.BpmTaskMessageService;
import org.ezplatform.bpm.engine.util.EngineConstants;
import org.ezplatform.system.user.entity.User;
import org.ezplatform.system.user.service.UserService;
import org.ezplatform.util.context.SpringContextHolder;
import org.ezplatform.workflow.listener.BusinessCallListener;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.common.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.common.api.delegate.event.FlowableEvent;
import org.flowable.engine.common.api.delegate.event.FlowableEventListener;
import org.flowable.engine.common.impl.identity.Authentication;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.delegate.event.impl.FlowableProcessCancelledEventImpl;
import org.flowable.engine.delegate.event.impl.FlowableProcessStartedEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessCancleListener extends BusinessCallListener implements FlowableEventListener
{
  private static BpmProcessStatisticsService bpmProcessStatisticsService;

  @Autowired
  private static BpmEngineService workflowService;

  @Autowired
  private static UserService userService;

  @Autowired
  private static BpmProcessOvertimeService bpos;

  @Autowired
  private static BpmProcessTimelimitService bpls;
  static BpmTaskMessageService bpmTaskService;
  static BpmUserUtil bpmUserUtil;
  static BpmReadTaskApiService bpmReadTaskApiService;

  public void onEvent(FlowableEvent event)
  {
    if (bpmUserUtil == null) {
      bpmUserUtil = 
        (BpmUserUtil)SpringContextHolder.getBean(BpmUserUtil.class);
    }

    if (bpmProcessStatisticsService == null) {
      bpmProcessStatisticsService = (BpmProcessStatisticsService)SpringContextHolder.getBean(BpmProcessStatisticsService.class);
      bpos = 
        (BpmProcessOvertimeService)SpringContextHolder.getBean(BpmProcessOvertimeService.class);

      bpls = 
        (BpmProcessTimelimitService)SpringContextHolder.getBean(BpmProcessTimelimitService.class);

      bpmTaskService = 
        (BpmTaskMessageService)SpringContextHolder.getBean(BpmTaskMessageService.class);

      bpmReadTaskApiService = 
        (BpmReadTaskApiService)SpringContextHolder.getBean(BpmReadTaskApiService.class);
    } //处理金蝶退回业务
      Map<String,String> paramsMap=new HashMap<String,String> ();
	 

     if (event.getType().equals(FlowableEngineEventType.PROCESS_CANCELLED)) {
      if ((event instanceof FlowableProcessCancelledEventImpl))
      {
        String processInstanceId = ((FlowableProcessCancelledEventImpl)event).getProcessInstanceId();
        String processDefinitionId=((FlowableProcessCancelledEventImpl)event).getProcessDefinitionId();
        BpmProcessStatistics bpmStatistics = bpmProcessStatisticsService.getProStaByProInsId(processInstanceId);
        if (bpmStatistics != null)
        {
          bpmStatistics.setStatus("2");

          bpmStatistics.setEndTime(new Date());
          bpmProcessStatisticsService.save(bpmStatistics);
        }

        bpos.updateOverTimeByProInsId(processInstanceId, "2");

        bpls.deleteByProInsId(processInstanceId);
        paramsMap.put("processDefinitionId", processDefinitionId);
  	  	paramsMap.put("processInstanceId", processInstanceId);
      } else {
        FlowableEntityEventImpl evento = (FlowableEntityEventImpl)event;

        ExecutionEntityImpl entity = (ExecutionEntityImpl)evento.getEntity();

        String processCode = entity.getProcessDefinitionKey();

        String processInstanceId = evento.getProcessInstanceId();

        BpmProcessStatistics bpmStatistics = bpmProcessStatisticsService.getProStaByProInsId(processInstanceId);
        if (bpmStatistics != null)
        {
          bpmStatistics.setStatus("2");

          bpmStatistics.setEndTime(new Date());

          bpmProcessStatisticsService.save(bpmStatistics);
        }

        bpos.updateOverTimeByProInsId(processInstanceId, "2");

        bpls.deleteByProInsId(processInstanceId);
        paramsMap.put("processDefinitionId", processCode);
  	  	paramsMap.put("processInstanceId", processInstanceId);
      }
      
     
      super.backKingdeeService(paramsMap);
    }
  }

  public boolean isFailOnException()
  {
    return false;
  }
}