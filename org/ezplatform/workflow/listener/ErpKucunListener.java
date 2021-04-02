package org.ezplatform.workflow.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by apple on 2020-12-2.
 */
@Component
public class ErpKucunListener implements ApplicationContextAware {

    private static final long serialVersionUID = -5140234938739863473L;
    protected Logger logger = LoggerFactory.getLogger(ErpKucunListener.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("Erp11111111111111111111111111111");
        this.applicationContext = applicationContext;
    }

    /**
     * 处理金蝶退回业务
     *
     * @param paramMap
     * @return
     */
    public String backKingdeeService(Map<String, String> paramMap) {

        Set set = paramMap.keySet();
        Iterator it = set.iterator();
        while(it.hasNext()){
            String tempName = it.next()+"";
            System.out.println("datainfoBack===========:  "+ tempName +"========>>>" + paramMap.get(tempName+""));
        }

        return "";
    }
}
