package org.ezplatform.workflow.listener;

import org.apache.commons.lang.StringUtils;
import org.flowable.engine.*;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * @Description: 基本的监听器
 * @Author: weixy
 */
public abstract class BaseListener   {

/*    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected IdentityService identityService;

    private static BaseListener baseListener;
    // 解决监听器中 Bean 获取不到问题
    @PostConstruct
    public void init() {
    	baseListener = this;
    	baseListener.runtimeService = this.runtimeService;
    	baseListener.historyService = this.historyService;
    	baseListener.repositoryService = this.repositoryService;
    	baseListener.taskService = this.taskService;
    	baseListener.identityService = this.identityService;
    }*/
	
	
    /**
     * 设置参数
     * @param parameters 参数
     * @param paramMap 传值map
     */
    public void setParams(String parameters, Map<String, Object> paramMap) {
        if (StringUtils.isNotBlank(parameters)) {
            String[] ps = parameters.split(";");
            if (ps != null && ps.length > 0) {
                for (String p : ps) {
                    String[] split = p.split(":");
                    if (split != null && split.length > 0) {
                        paramMap.put(split[0], split[1]);
                    }
                }
            }
        }
    }




}
