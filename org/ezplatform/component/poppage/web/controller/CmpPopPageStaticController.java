package org.ezplatform.component.poppage.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.ezplatform.component.poppage.service.CmpPopPageSataticService;
import org.ezplatform.core.common.WebUser;
import org.ezplatform.core.exception.ValidationException;
import org.ezplatform.core.pagination.PropertyFilter;
import org.ezplatform.core.service.BaseService;
import org.ezplatform.core.web.controller.BaseController;
import org.ezplatform.core.web.util.WebUtils;
import org.ezplatform.core.web.view.OperationResult;
import org.ezplatform.sdk.AccessToken;
import org.ezplatform.sdk.Client;
import org.ezplatform.util.GlobalConstant;
import org.ezplatform.util.JsonUtils;
import org.ezplatform.util.StringUtils;
import org.ezplatform.util.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/cmp/popcommpage/popuprandominfo"})
public class CmpPopPageStaticController extends BaseController
{
  private final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

  @Autowired
  private CmpPopPageSataticService cmpPopPageSataticService;

  protected BaseService getEntityService() { return null; }


  @RequestMapping(value={"/popuprandomList"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String popuprandomList(HttpServletRequest request, Model model)
  {
    model.addAttribute("popuCode", StringUtils.null2String(request.getParameter("popuCode")));
    model.addAttribute("showId", StringUtils.null2String(request.getParameter("showId")));
    model.addAttribute("showField", StringUtils.null2String(request.getParameter("showField")));
    model.addAttribute("hiddenId", StringUtils.null2String(request.getParameter("hiddenId")));
    model.addAttribute("hiddenField", StringUtils.null2String(request.getParameter("hiddenField")));
    model.addAttribute("selectType", StringUtils.null2String(request.getParameter("selectType")));
    model.addAttribute("popFunc", StringUtils.null2String(request.getParameter("popFunc")));
    model.addAttribute("queryCondition", StringUtils.null2String(request.getParameter("queryCondition")));
    model.addAttribute("stintType", StringUtils.null2String(request.getParameter("stintType")));
    model.addAttribute("stintField", StringUtils.null2String(request.getParameter("stintField")));
    model.addAttribute("stintData", StringUtils.null2String(request.getParameter("stintData")));

    return "static/cmp/poppage/popuprandom_list.html";
  }

  @RequestMapping(value={"/popuprandomTree"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String popuprandomTree(HttpServletRequest request, Model model)
  {
    model.addAttribute("popuCode", StringUtils.null2String(request.getParameter("popuCode")));
    model.addAttribute("showId", StringUtils.null2String(request.getParameter("showId")));
    model.addAttribute("showField", StringUtils.null2String(request.getParameter("showField")));
    model.addAttribute("hiddenId", StringUtils.null2String(request.getParameter("hiddenId")));
    model.addAttribute("hiddenField", StringUtils.null2String(request.getParameter("hiddenField")));
    model.addAttribute("selectType", StringUtils.null2String(request.getParameter("selectType")));
    model.addAttribute("popFunc", StringUtils.null2String(request.getParameter("popFunc")));
    model.addAttribute("queryCondition", StringUtils.null2String(request.getParameter("queryCondition")));
    model.addAttribute("stintType", StringUtils.null2String(request.getParameter("stintType")));
    model.addAttribute("stintField", StringUtils.null2String(request.getParameter("stintField")));
    model.addAttribute("stintData", StringUtils.null2String(request.getParameter("stintData")));
    model.addAttribute("treeChkboxType", StringUtils.null2String(request.getParameter("treeChkboxType")));
    model.addAttribute("temphiddenId", StringUtils.null2String(request.getParameter("temphiddenId")));

    return "static/cmp/poppage/popuprandom_tree.html";
  }

  @RequestMapping(value={"/popuprandomTabletree"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String popuprandomTabletree(HttpServletRequest request, Model model)
  {
    model.addAttribute("popuCode", StringUtils.null2String(request.getParameter("popuCode")));
    model.addAttribute("showId", StringUtils.null2String(request.getParameter("showId")));
    model.addAttribute("showField", StringUtils.null2String(request.getParameter("showField")));
    model.addAttribute("hiddenField", StringUtils.null2String(request.getParameter("hiddenField")));
    model.addAttribute("hiddenId", StringUtils.null2String(request.getParameter("hiddenId")));
    model.addAttribute("selectType", StringUtils.null2String(request.getParameter("selectType")));
    model.addAttribute("popFunc", StringUtils.null2String(request.getParameter("popFunc")));
    model.addAttribute("queryCondition", StringUtils.null2String(request.getParameter("queryCondition")));
    model.addAttribute("stintType", StringUtils.null2String(request.getParameter("stintType")));
    model.addAttribute("stintField", StringUtils.null2String(request.getParameter("stintField")));
    model.addAttribute("stintData", StringUtils.null2String(request.getParameter("stintData")));
    return "static/cmp/poppage/popuprandom_tabletree.html";
  }

  @RequestMapping(value={"/getPopupListResult"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Page getPopupListResult(HttpServletRequest request)
  {
    String corpId = WebUtils.getCurrentUser().getCorpId();

    String popuCode = request.getParameter("popuCode");
    if (!ValidateUtils.validateId(popuCode)) {
      this.LOGGER.debug("非法参数");
      return null;
    }

    Map param = new HashMap();
    param.put("corpId", corpId);
    param.put("popuCode", popuCode);

    String result = new Client().get(GlobalConstant.getApiUrl("flexbase", "/cmp/popcommpage/popuprandominfo/getPoppageData"), param, null, null, new AccessToken(
      WebUtils.getCurrentUser().getJwtToken()));
    Map scopeName = JsonUtils.readValue(result);
    String code = String.valueOf(scopeName.get("code"));
    Map popupRandom = null;
    if ("0".equals(code)) {
      popupRandom = (Map)scopeName.get("data");   
    }
    String sqlStr = "";
    String queryAlias = "t1";

    if (popupRandom != null) {
      sqlStr = (String)popupRandom.get("querySql");
      queryAlias = (String)popupRandom.get("queryAlias");
    }

    String stintType = "undefined".equals(request.getParameter("stintType")) ? "" : request.getParameter("stintType");
    String stintField = "undefined".equals(request.getParameter("stintField")) ? "" : request.getParameter("stintField");
    String stintData = "undefined".equals(request.getParameter("stintData")) ? "" : request.getParameter("stintData");

    Pageable pageable = PropertyFilter.buildPageableFromHttpRequest(request);
    List buildFilters = PropertyFilter.buildFiltersFromHttpRequest(request);

    Page page = this.cmpPopPageSataticService.getPopuppageResult(queryAlias, sqlStr, corpId, stintType, stintField, stintData, pageable, buildFilters);

    return page;
  }

  @RequestMapping(value={"/getPopupTreeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public String getPopupTreeData(HttpServletRequest request) {
    String corpId = WebUtils.getCurrentUser().getCorpId();

    String popuCode = request.getParameter("popuCode");
    if (!ValidateUtils.validateId(popuCode)) {
      this.LOGGER.debug("非法参数");
      return null;
    }

    Map param = new HashMap();
    param.put("corpId", corpId);
    param.put("popuCode", popuCode);

    String result = new Client().get(GlobalConstant.getApiUrl("flexbase", "/cmp/popcommpage/popuprandominfo/getPoppageData"), param, null, null, new AccessToken(
      WebUtils.getCurrentUser().getJwtToken()));
    Map scopeName = JsonUtils.readValue(result);
    String code = String.valueOf(scopeName.get("code"));
    Map popupRandom = null;
    if ("0".equals(code)) {
      popupRandom = (Map)scopeName.get("data");
    }

    if (popupRandom != null) {
      String sqlStr = (String)popupRandom.get("querySql");
      String queryAlias = (String)popupRandom.get("queryAlias");
      String queryReturn = (String)popupRandom.get("queryReturn");
      String stintType = "undefined".equals(request.getParameter("stintType")) ? "" : request.getParameter("stintType");
      String stintField = "undefined".equals(request.getParameter("stintField")) ? "" : request.getParameter("stintField");
      String stintData = "undefined".equals(request.getParameter("stintData")) ? "" : request.getParameter("stintData");

      String popupName = request.getParameter("popupName");
      String treeShow = request.getParameter("treeShow");
      if ((!ValidateUtils.validateParam(treeShow)) || (!ValidateUtils.validateParam(popupName))) {
        throw new ValidationException("非法参数");
      }

      String jsonData = this.cmpPopPageSataticService.getPopupTree(sqlStr, corpId, queryAlias, stintType, stintField, stintData, treeShow, popupName, queryReturn);

      return jsonData;
    }
    return "";
  }

  @RequestMapping(value={"/getPoppageData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public OperationResult getPoppageData(HttpServletRequest request)
  {
    String corpId = WebUtils.getCurrentUser().getCorpId();
    String popuCode = request.getParameter("popuCode");

    if (!ValidateUtils.validateId(popuCode)) {
      OperationResult.buildFailureResult("非法参数");
      return null;
    }
    Map param = new HashMap();
    param.put("corpId", corpId);
    param.put("popuCode", popuCode);

    String result = new Client().get(GlobalConstant.getApiUrl("flexbase", "/cmp/popcommpage/popuprandominfo/getPoppageData"), param, null, null, new AccessToken(
      WebUtils.getCurrentUser().getJwtToken()));
    Map scopeName = JsonUtils.readValue(result);
    String code = String.valueOf(scopeName.get("code"));
    if ("0".equals(code)) {
      return OperationResult.buildSuccessResult(scopeName.get("data"));
    }
    return OperationResult.buildFailureResult("获取信息失败");
  }

  @RequestMapping(value={"/initPopupList"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public OperationResult initPopupList(HttpServletRequest request) {
    this.LOGGER.debug("打开弹出选择列表页面开始");
    String corpId = WebUtils.getCurrentUser().getCorpId();

    String popuCode = request.getParameter("popuCode");

    String queryCondition = request.getParameter("queryCondition");
    if ((!ValidateUtils.validateParam(popuCode)) || (!ValidateUtils.validateChar(queryCondition, "%+="))) {
      this.LOGGER.debug("非法参数");
      return null;
    }

    Map param = new HashMap();
    param.put("corpId", corpId);
    param.put("popuCode", popuCode);
    param.put("queryCondition", queryCondition);

    String result = new Client().get(GlobalConstant.getApiUrl("flexbase", "/cmp/popcommpage/popuprandominfo/initPopupList"), param, null, null, new AccessToken(
      WebUtils.getCurrentUser().getJwtToken()));
    Map scopeName = JsonUtils.readValue(result);
    String code = String.valueOf(scopeName.get("code"));
    if ("0".equals(code)) {
      return OperationResult.buildSuccessResult(scopeName.get("data"));
    }
    return OperationResult.buildFailureResult("获取信息失败");
  }

  @RequestMapping(value={"/getSelectData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public String getSelectData(HttpServletRequest request)
  {
    String corpId = WebUtils.getCurrentUser().getCorpId();
    String popuCode = request.getParameter("popuCode");
    if (!ValidateUtils.validateId(popuCode)) {
      this.LOGGER.debug("非法参数");
      return null;
    }
    String sqlStr = "";
    String queryAlias = "t1";

    String returnValue = "";

    Map param = new HashMap();
    param.put("corpId", corpId);
    param.put("popuCode", popuCode);
    String result = new Client().get(GlobalConstant.getApiUrl("flexbase", "/cmp/popcommpage/popuprandominfo/getPoppageData"), param, null, null, new AccessToken(
      WebUtils.getCurrentUser().getJwtToken()));
    Map scopeName = JsonUtils.readValue(result);
    String code = String.valueOf(scopeName.get("code"));

    if ("0".equals(code)) {
      Map popupRandom = (Map)scopeName.get("data");
      sqlStr = (String)popupRandom.get("querySql");
      queryAlias = (String)popupRandom.get("queryAlias");
      returnValue = (String)popupRandom.get("queryReturn");
    }

    String hiddenField = "undefined".equals(request.getParameter("hiddenField")) ? "" : request.getParameter("hiddenField");
    String hidIds = "undefined".equals(request.getParameter("hidIds")) ? "" : request.getParameter("hidIds");
    String showField = "undefined".equals(request.getParameter("showField")) ? "" : request.getParameter("showField");
    String showInputValue = "undefined".equals(request.getParameter("showInputValue")) ? "" : request.getParameter("showInputValue");

    String jsonData = this.cmpPopPageSataticService.getTableSelectData(sqlStr, corpId, queryAlias, returnValue, hiddenField, hidIds, showField, showInputValue);
    return jsonData;
  }

  @RequestMapping(value={"/getPopupTableResult"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Page<Map> getPopupTableResult(HttpServletRequest request)
  {
    String corpId = WebUtils.getCurrentUser().getCorpId();
    String popuCode = request.getParameter("popuCode");
    if (!ValidateUtils.validateId(popuCode)) {
      throw new ValidationException("非法参数");
    }

    Map param = new HashMap();
    param.put("corpId", corpId);
    param.put("popuCode", popuCode);
    String result = new Client().get(GlobalConstant.getApiUrl("flexbase", "/cmp/popcommpage/popuprandominfo/getPoppageData"), param, null, null, new AccessToken(
      WebUtils.getCurrentUser().getJwtToken()));
    Map scopeName = JsonUtils.readValue(result);
    String code = String.valueOf(scopeName.get("code"));

    if ("0".equals(code)) {
      Map popupRandom = (Map)scopeName.get("data");
      String sqlStr = (String)popupRandom.get("querySql");
      String colum = (String)popupRandom.get("queryAlias");

      String stintType = "undefined".equals(request.getParameter("stintType")) ? "" : request.getParameter("stintType");
      String stintField = "undefined".equals(request.getParameter("stintField")) ? "" : request.getParameter("stintField");
      String stintData = "undefined".equals(request.getParameter("stintData")) ? "" : request.getParameter("stintData");
      String searchId = "undefined".equals(request.getParameter("searchId")) ? "" : request.getParameter("searchId");
      String searchField = "undefined".equals(request.getParameter("searchField")) ? "" : request.getParameter("searchField");

      Pageable pageable = PropertyFilter.buildPageableFromHttpRequest(request);
      List buildFilters = PropertyFilter.buildFiltersFromHttpRequest(request);
      Page page = this.cmpPopPageSataticService.getPopupTableResult(colum, sqlStr, corpId, stintType, stintField, stintData, searchId, searchField, pageable, buildFilters);

      return page;
    }
    return null;
  }

  @RequestMapping(value={"/getPopupTableTreeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public String getPopupTableTreeData(HttpServletRequest request)
  {
    String corpId = WebUtils.getCurrentUser().getCorpId();
    String popuCode = request.getParameter("popuCode");
    if (!ValidateUtils.validateParam(popuCode)) {
      throw new ValidationException("非法参数");
    }

    Map param = new HashMap();
    param.put("corpId", corpId);
    param.put("popuCode", popuCode);
    String result = new Client().get(GlobalConstant.getApiUrl("flexbase", "/cmp/popcommpage/popuprandominfo/getPoppageData"), param, null, null, new AccessToken(
      WebUtils.getCurrentUser().getJwtToken()));
    Map scopeName = JsonUtils.readValue(result);
    String code = String.valueOf(scopeName.get("code"));

    if ("0".equals(code)) {
      Map popupRandom = (Map)scopeName.get("data");
      String sqlStr = (String)popupRandom.get("treeSql");
      String queryReturn = (String)popupRandom.get("treeReturn");
      String treeAlias = (String)popupRandom.get("treeAlias");

      String searchColumn = request.getParameter("searchColumn");
      String popupName = request.getParameter("popupName");
      if ((!ValidateUtils.validateParam(searchColumn)) || (!ValidateUtils.validateParam(popupName))) {
        this.LOGGER.debug("非法参数");
        return null;
      }

      String jsonData = this.cmpPopPageSataticService.getPopupTableTree(sqlStr, corpId, treeAlias, searchColumn, popupName, queryReturn);
      return jsonData;
    }
    return null;
  }
}