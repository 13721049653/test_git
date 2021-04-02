package org.ezplatform.wuziShen.web.rest;

import org.ezplatform.core.common.WebUser;
import org.ezplatform.core.web.view.OperationResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by apple on 2020-11-25.
 */

@RestController
@RequestMapping({"/api/systemsearch/info"})
public class SystemSearchController {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取是否存在
     * @param request
     * @return
     */
    @RequestMapping(value={"/getHasCheck"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public OperationResult getWuziDetail(HttpServletRequest request){
        String returnJsonArr=  "";
        WebUser webUser=org.ezplatform.core.web.util.WebUtils.getCurrentUser();
        String userId = request.getParameter("userId")+"";

        String paramsQuest = request.getParameter("requestParam")+"";
        //获取
        String whereSql = " where LOGIN_NAME like '%"+paramsQuest+"%' or  USER_NAME like '%"+paramsQuest+"%'  or PHONE = '"+paramsQuest+"' ";
        String sql = "select * from sys_user  "+whereSql;
        returnJsonArr = jdbcTemplate.queryForList(sql).size()+"";

        return OperationResult.buildSuccessResult(returnJsonArr);
    }


}
