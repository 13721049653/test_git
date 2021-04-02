package org.ezplatform.caiwu.web.rest;

import com.alibaba.fastjson.JSONArray;
import org.ezplatform.caiwu.service.CaiwuService;
import org.ezplatform.core.web.view.OperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by jinx on 2020-12-8.
 * 处理财务相关
 */
@RestController
@RequestMapping({"/api/caiwu/jine"})
public class CaiwuController {

    protected Logger logger = LoggerFactory.getLogger(CaiwuController.class);

    @Autowired
    CaiwuService caiwuService;

    /**
     * 获取个人欠款金额
     * @param request
     * @return
     */
    @RequestMapping(value={"/getQkjine"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getQK_money(HttpServletRequest request){
        String userId = request.getParameter("userId")+"";
        String fnumber = request.getParameter("fnumber")+"";
        String jklx = request.getParameter("jklx")+"";
        JSONArray jArr = caiwuService.getQK_money(jklx , fnumber);
        return OperationResult.buildSuccessResult(jArr);
    }

    /**
     * 读取生成编码串
     */
    @RequestMapping(value={"/getCGbm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getCG_bm(HttpServletRequest request){

        String tableCode1 = "cghtlcbm";
        String tableCode2 = "cghtsplcbh";

        Map<String,Object> map = caiwuService.getCG_bm(tableCode1 , tableCode2);
        return OperationResult.buildSuccessResult(map);
    }

    @RequestMapping(value={"/get_mcbybm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult get_mcbybm(HttpServletRequest request){

        String bm = request.getParameter("bm")+"";
        String map = caiwuService.get_mcbybm(bm);
        return OperationResult.buildSuccessResult(map);
    }


    /**
     *
     * @return
     */
    @RequestMapping(value={"/get_hasRight_goType"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult get_hasRight_goType(HttpServletRequest request){
        boolean flag  = true;
        String userId = request.getParameter("userId")+"";
        String allChuxing = request.getParameter("allChuxing");

        flag = caiwuService.get_hasRight_goType(userId,allChuxing);
        return OperationResult.buildSuccessResult(flag);
    }


    /**
     *
     * @return
     */
    @RequestMapping(value={"/getQkCurperson"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getQkCurperson(HttpServletRequest request){
        String returnVal  = "";
        String userId = request.getParameter("userId")+"";
        returnVal = caiwuService.getQkCurperson(userId);
        return OperationResult.buildSuccessResult(returnVal);
    }

}
