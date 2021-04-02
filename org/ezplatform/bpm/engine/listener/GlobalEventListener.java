package org.ezplatform.bpm.engine.listener;

import java.util.HashMap;
import java.util.Map;
import org.flowable.engine.common.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.common.api.delegate.event.FlowableEvent;
import org.flowable.engine.common.api.delegate.event.FlowableEventListener;
import org.flowable.engine.common.api.delegate.event.FlowableEventType;

public class GlobalEventListener
  implements FlowableEventListener
{
  private Map<String, FlowableEventListener> handlers = new HashMap();

  public GlobalEventListener()
  {
    this.handlers.put("TASK_CREATED", new TaskCreateListener());
    this.handlers.put("TASK_COMPLETED", new TaskCompletedListener());
    this.handlers.put("TASK_COMPLETED", new TaskRemindListener());

    this.handlers.put("PROCESS_COMPLETED_WITH_ERROR_END_EVENT", new ProcessDeletedListener());

    this.handlers.put("PROCESS_COMPLETED", new ProcessStaticsListener());
    this.handlers.put(FlowableEngineEventType.PROCESS_STARTED.name(), new ProcessStaticsListener());
    this.handlers.put(FlowableEngineEventType.PROCESS_CANCELLED.name(), new ProcessCancleListener());
  }

  public boolean isFailOnException()
  {
    return false;
  }

  public void onEvent(FlowableEvent event)
  {
    String eventType = event.getType().name();

    FlowableEventListener handler = (FlowableEventListener)this.handlers.get(eventType);
    if (handler != null)
      handler.onEvent(event);
  }
}