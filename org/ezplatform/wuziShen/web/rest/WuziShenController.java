package org.ezplatform.wuziShen.web.rest;

import com.alibaba.fastjson.JSONArray;
import net.sf.json.JSONObject;
import org.ezplatform.core.common.WebUser;
import org.ezplatform.core.web.view.OperationResult;
import org.ezplatform.wuziShen.web.service.WuziShenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by apple on 2020-11-10.
 */
@RestController
@RequestMapping({"/api/wuzishen/opera"})
public class WuziShenController {

    protected Logger logger = LoggerFactory.getLogger(WuziShenController.class);

    @Autowired
    WuziShenService wuziShenService;

    /**
     * 获取物资详细数据
     * @param request
     * @return
     */
    @RequestMapping(value={"/getDetail"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getWuziDetail(HttpServletRequest request){
        String formData=request.getParameter("formData")==null?"":request.getParameter("formData").toString();
        String wuziName = request.getParameter("wuziName")+"";

        WebUser webUser=org.ezplatform.core.web.util.WebUtils.getCurrentUser();
        //String userId = webUser.getUserId();
        String userId = request.getParameter("userid")+"";
        //获取库存
        JSONArray returnJsonArr= wuziShenService.dealWuziData(formData,userId,wuziName);
        System.out.println("returnJsonArr.toString();==11111111111111111111111111111111===>>>>"+returnJsonArr.toString());

        return OperationResult.buildSuccessResult(returnJsonArr);
    }

    /**
     * 校验物资详细数据
     * @param request
     * @return
     */
    @RequestMapping(value={"/checkDetail"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getWuziCheckData(HttpServletRequest request){
        String formData=request.getParameter("formData")==null?"":request.getParameter("formData").toString();
        String wuziName = request.getParameter("wuziName")+"";

        WebUser webUser=org.ezplatform.core.web.util.WebUtils.getCurrentUser();
        //String userId = webUser.getUserId();
        String userId = request.getParameter("userid")+"";

        if(!"".equals(wuziName)){
            String[] str = wuziName.split(",");
            for(int i=0;i<str.length;i++){
                if(!"".equals(str[i])){
                    //获取库存
                    JSONArray returnJsonArr= wuziShenService.dealWuziData(formData,userId,str[i]);
                    System.out.print("jsonArr:=====>>.jinxh+222222222222222222:=====>>>>"+returnJsonArr);
                }
            }
        }

        return OperationResult.buildSuccessResult("success");
    }


    /**
     * 处理退回库存业务
     * @param request
     * @return
     */
    @RequestMapping(value={"/pushErpKuncunData"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult pushErpKuncunData(HttpServletRequest request){

        String processInstanceId = request.getParameter("processInstanceId")+"";
        String processDefinitionId = request.getParameter("processDefinitionId");

        System.out.println("processInstanceId====>>>>"+processInstanceId);
        System.out.println("processDefinitionId====>>>>"+processDefinitionId);
        wuziShenService.pushErpKuncunData(processInstanceId,processDefinitionId);
        return OperationResult.buildSuccessResult("success");
    }

}
