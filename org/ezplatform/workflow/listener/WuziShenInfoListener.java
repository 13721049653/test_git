package org.ezplatform.workflow.listener;

import org.ezplatform.workflow.service.SystemFlowService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by apple on 2020-11-9.
 */
public class WuziShenInfoListener  implements ApplicationContextAware {

    private static final long serialVersionUID = -5140234938739863473L;
    protected Logger logger = LoggerFactory.getLogger(WuziShenInfoListener.class);

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
        }

        String processDefinitionId = delegateTask.getProcessDefinitionId();
        //String extBussinesskey = execution.getExtBussinesskey();
        String processInstanceId = delegateTask.getProcessInstanceId();
        systemFlowService.dealERPActivity(processInstanceId, processDefinitionId,false);
        return null;


    }
}
