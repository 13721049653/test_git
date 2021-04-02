package org.ezplatform.rd.web.rest;

import com.alibaba.fastjson.JSONArray;
import org.ezplatform.core.common.WebUser;
import org.ezplatform.core.web.view.OperationResult;
import org.ezplatform.rd.web.service.RdWorkService;
import org.ezplatform.wuziShen.web.service.WuziShenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by apple on 2020-11-28.
 */
@RestController
@RequestMapping({"/api/rd/workflow"})
public class RdWorkController {

    protected Logger logger = LoggerFactory.getLogger(RdWorkController.class);

    @Autowired
    RdWorkService rdWorkService;

    /**
     * 获取物资详细数据
     * @param request
     * @return
     */
    @RequestMapping(value={"/getScbtmoney"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getSCBT_moneyDetail(HttpServletRequest request){
        String formData=request.getParameter("formData")==null?"":request.getParameter("formData").toString();
        String btbz = request.getParameter("btbz")+"";

        WebUser webUser=org.ezplatform.core.web.util.WebUtils.getCurrentUser();
        //String userId = webUser.getUserId();
        String userId = request.getParameter("userid")+"";
        //获取库存
        String msg= rdWorkService.dealMoneyData(formData,userId,btbz);
        System.out.println("returnJsonArr.toString();==scbt_money===>>>>"+msg.toString());

        return OperationResult.buildSuccessResult("成功", msg);
    }

    @RequestMapping(value={"/getLeaderByOrg"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getLeaderByOrg(HttpServletRequest request){
        String msg = "";
        String orgIds = request.getParameter("fNumbers");
        msg = rdWorkService.getLeaderByOrg(orgIds);
        System.out.println("returnLeadersNames============>>>"+msg);
        return OperationResult.buildSuccessResult("成功", msg);
    }
}
