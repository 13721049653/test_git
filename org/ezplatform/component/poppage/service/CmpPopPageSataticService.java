package org.ezplatform.component.poppage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.ezplatform.core.common.WebUser;
import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.core.pagination.PropertyFilter;
import org.ezplatform.core.service.BaseService;
import org.ezplatform.core.web.util.WebUtils;
import org.ezplatform.util.JsonUtils;
import org.ezplatform.util.RestClient;
import org.ezplatform.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("cmpPopPageSataticService")
@Transactional
public class CmpPopPageSataticService extends BaseService
{
  private static final String sysCorpId = "&SYS_CORP_ID&";
  private static final String sysUserId = "&SYS_USER_ID&";
  private static final String sysOrgId = "&SYS_ORG_ID&";
  private RestClient restClient = new RestClient(true);

  protected JpaBaseDao getEntityDao()
  {
    return null;
  }

  public Page<Map> getPopuppageResult(String queryAlias, String sql, String corpId, String stintType, String stintField, String stintData, Pageable pageable, List<PropertyFilter> buildFilters)
  {
    WebUser user = WebUtils.getCurrentUser();

    stintData = getIds(stintData);

    String alias = StringUtils.isEmpty(queryAlias) ? "" : new StringBuilder().append(queryAlias).append(".").toString();

    sql = sql.replaceAll("&SYS_CORP_ID&", corpId);
    sql = sql.replaceAll("&SYS_USER_ID&", user.getUserId());
    if (user.getOrgId() != null) {
      sql = sql.replaceAll("&SYS_ORG_ID&", user.getOrgId());
    }

    if ((StringUtils.isNotEmpty(stintField)) && (StringUtils.isNotEmpty(stintData)) && 
      (StringUtils.isNotEmpty(stintType)))
    {
      if ("1".equals(stintType))
        sql = new StringBuilder().append(sql).append(" and ").append(alias).append(stintField).append(" in ").append(stintData).toString();
      else if ("0".equals(stintType)) {
        sql = new StringBuilder().append(sql).append(" and ").append(alias).append(stintField).append(" not in ").append(stintData).toString();
      }
    }
    String strSql = PropertyFilter.getSqlByFilterAndPageable(queryAlias, sql.toString(), "", buildFilters, pageable);

    Page page = super.findByPageNativeSQL(pageable, strSql);
    return page;
  }

  public String getPopupTree(String sqlStr, String corpId, String queryAlias, String stintType, String stintField, String stintData, String treeShow, String popupName, String queryReturn)
  {
    stintData = getIds(stintData);
    String alias = StringUtils.isEmpty(queryAlias) ? "" : new StringBuilder().append(queryAlias).append(".").toString();
    sqlStr = sqlStr.replaceAll("&SYS_CORP_ID&", corpId);

    if ((StringUtils.isNotEmpty(popupName)) && (StringUtils.isNotEmpty(treeShow))) {
      sqlStr = new StringBuilder().append(sqlStr).append(" and ").append(alias).append(treeShow).append(" like '%").append(popupName).append("%'").toString();
    }

    if ((StringUtils.isNotEmpty(stintField)) && (StringUtils.isNotEmpty(stintData)) && (StringUtils.isNotEmpty(stintType))) {
      if ("1".equals(stintType))
        sqlStr = new StringBuilder().append(sqlStr).append(" and ").append(alias).append(stintField).append(" in ").append(stintData).toString();
      else if ("0".equals(stintType)) {
        sqlStr = new StringBuilder().append(sqlStr).append(" and ").append(alias).append(stintField).append(" not in ").append(stintData).toString();
      }

    }

    Query query = getEntityManager().createNativeQuery(sqlStr);
    List popupTree = query.getResultList();

    List strList = getTreeTable(queryReturn);

    int treePid = 1;
    for (int t = 0; t < strList.size(); t++) {
      String str = (String)strList.get(t);
      if (("pId".equals(str)) && (t + 1 < strList.size())) {
        treePid = Integer.parseInt((String)strList.get(t + 1));
      }
    }

    StringBuilder localStringBuffer = new StringBuilder("");
    localStringBuffer.append("[");

    for (int i = 0; i < popupTree.size(); i++) {
      localStringBuffer.append("{");
      if ((strList != null) && (strList.size() > 1)) {
        for (int j = 0; j < strList.size(); j += 2) {
          String num = (String)strList.get(j + 1);
          int no = Integer.parseInt(num);
          String result = ((Object[])(Object[])(Object[])popupTree.get(i))[no] == null ? "" : ((Object[])(Object[])(Object[])popupTree.get(i))[no].toString();
          localStringBuffer.append(new StringBuilder().append("\"").append((String)strList.get(j)).append("\":\"").append(result).append("\",").toString());
        }

        if (treePid < ((Object[])(Object[])popupTree.get(i)).length) {
          if ((((Object[])(Object[])popupTree.get(i))[treePid] != null) && 
            (StringUtils.isNotEmpty(((Object[])(Object[])popupTree
            .get(i))[
            treePid].toString())))
            localStringBuffer.append("\"open\":false");
          else {
            localStringBuffer.append("\"open\":true");
          }
        }

        localStringBuffer.append("},");
      }
    }

    String result = localStringBuffer.toString();
    if (result.endsWith(",")) {
      result = result.substring(0, result.length() - 1);
    }
    result = new StringBuilder().append(result).append("]").toString();

    return result;
  }

  public String getTableSelectData(String sqlStr, String corpId, String queryAlias, String returnValue, String hiddenField, String hidIds, String showField, String showInputValue)
  {
    String alias = StringUtils.isEmpty(queryAlias) ? "" : new StringBuilder().append(queryAlias).append(".").toString();
    if (StringUtils.isNotEmpty(sqlStr))
    {
      sqlStr = sqlStr.replaceAll("&SYS_CORP_ID&", corpId);

      if ((StringUtils.isNotEmpty(hiddenField)) && (StringUtils.isNotEmpty(hidIds))) {
        hidIds = getIds(hidIds);
        sqlStr = new StringBuilder().append(sqlStr).append(" and ").append(alias).append(hiddenField).append("  in ").append(hidIds).toString();
      }

      Query query = getEntityManager().createNativeQuery(sqlStr);
      List popupTree = query.getResultList();

      List strList = getTreeTable(returnValue);

      if (!returnValue.contains(showField)) {
        strList.add(showField);
      }

      String result = "";
      for (int i = 0; i < popupTree.size(); i++)
      {
        String oStr = "";

        for (int j = 0; j < strList.size(); j += 2) {
          String num = (String)strList.get(j + 1);
          int no = Integer.parseInt(num);
          String val = ((Object[])(Object[])popupTree.get(i))[no] == null ? "" : ((Object[])(Object[])popupTree.get(i))[no].toString();
          if ("".equals(oStr))
            oStr = new StringBuilder().append('"').append((String)strList.get(j)).append('"').append(':').append('"').append(val).append('"').toString();
          else {
            oStr = new StringBuilder().append(oStr).append(",").append('"').append((String)strList.get(j)).append('"').append(':').append('"').append(val).append('"').toString();
          }
        }
        oStr = new StringBuilder().append("{").append(oStr).append("}").toString();
        if ("".equals(result))
          result = oStr;
        else {
          result = new StringBuilder().append(result).append(",").append(oStr).toString();
        }
      }

      result = new StringBuilder().append("[").append(result).append("]").toString();
      return result;
    }

    return "";
  }

  public Page<Map> getPopupTableResult(String queryAlias, String sql, String corpId, String stintType, String stintField, String stintData, String searchId, String searchField, Pageable pageable, List<PropertyFilter> buildFilters)
  {
    stintData = getIds(stintData);
    String alias = StringUtils.isEmpty(queryAlias) ? "" : new StringBuilder().append(queryAlias).append(".").toString();
    sql = sql.replaceAll("&SYS_CORP_ID&", corpId);

    if ((StringUtils.isNotEmpty(stintField)) && (StringUtils.isNotEmpty(stintData)) && (StringUtils.isNotEmpty(stintType))) {
      if ("1".equals(stintType))
        sql = new StringBuilder().append(sql).append(" and ").append(alias).append(stintField).append(" in ").append(stintData).toString();
      else if ("0".equals(stintType)) {
        sql = new StringBuilder().append(sql).append(" and ").append(alias).append(stintField).append(" not in ").append(stintData).toString();
      }
    }

    if (StringUtils.isNotEmpty(searchId))
    {
      sql = new StringBuilder().append(sql).append(" and ").append(alias).append(searchField).append(" = '").append(searchId).append("'").toString();
    }
    String strSql = PropertyFilter.getSqlByFilterAndPageable(queryAlias, sql.toString(), "", buildFilters, pageable);
    Page page = super.findByPageNativeSQL(pageable, strSql);
    return page;
  }

  public String getPopupTableTree(String sqlStr, String corpId, String treeAlias, String searchColumn, String popupName, String queryReturn)
  {
    String alias = StringUtils.isEmpty(treeAlias) ? "" : new StringBuilder().append(treeAlias).append(".").toString();
    sqlStr = sqlStr.replaceAll("&SYS_CORP_ID&", corpId);
    if ((StringUtils.isNotEmpty(searchColumn)) && (StringUtils.isNotEmpty(popupName))) {
      sqlStr = new StringBuilder().append(sqlStr).append(" and ").append(alias).append(searchColumn).append(" like '%").append(popupName).append("%' ").toString();
    }

    Query query = getEntityManager().createNativeQuery(sqlStr);
    List popupTree = query.getResultList();

    List strList = getTreeTable(queryReturn);

    int treePid = 1;
    for (int t = 0; t < strList.size(); t++) {
      String str = (String)strList.get(t);
      if (("pId".equals(str)) && (t + 1 < strList.size())) {
        treePid = Integer.parseInt((String)strList.get(t + 1));
      }
    }

    StringBuilder localStringBuffer = new StringBuilder("");
    localStringBuffer.append("[");

    for (int i = 0; i < popupTree.size(); i++) {
      localStringBuffer.append("{");
      if ((strList != null) && (strList.size() > 1)) {
        for (int j = 0; j < strList.size(); j += 2) {
          String num = (String)strList.get(j + 1);
          int no = Integer.parseInt(num);
          String result = ((Object[])(Object[])popupTree.get(i))[no] == null ? "" : ((Object[])(Object[])popupTree.get(i))[no].toString();
          localStringBuffer.append(new StringBuilder().append("\"").append((String)strList.get(j)).append("\":\"").append(result).append("\",").toString());
        }

        if (treePid < ((Object[])(Object[])popupTree.get(i)).length) {
          if ((((Object[])(Object[])popupTree.get(i))[treePid] != null) && 
            (StringUtils.isNotEmpty(((Object[])(Object[])popupTree
            .get(i))[
            treePid].toString())))
            localStringBuffer.append("\"open\":false");
          else {
            localStringBuffer.append("\"open\":true");
          }
        }

        localStringBuffer.append("},");
      }
    }

    String result = localStringBuffer.toString();
    if (result.endsWith(",")) {
      result = result.substring(0, result.length() - 1);
    }
    result = new StringBuilder().append(result).append("]").toString();
    return result;
  }

  private String getIds(String hidIds)
  {
    if (StringUtils.isNotEmpty(hidIds)) {
      String[] str = hidIds.split(",");

      String ids = "";
      for (int i = 0; i < str.length; i++) {
        if ("".equals(ids))
          ids = new StringBuilder().append("'").append(str[i]).append("'").toString();
        else {
          ids = new StringBuilder().append(ids).append(", '").append(str[i]).append("'").toString();
        }
      }

      ids = new StringBuilder().append("(").append(ids).append(")").toString();
      return ids;
    }
    return "";
  }

  private List<String> getTreeTable(String returnValue) {
    List resultList = new ArrayList();
    if (!StringUtils.isEmpty(returnValue)) {
      String[] strArray = returnValue.split(",");
      for (int i = 0; i < strArray.length; i++) {
        resultList.add(strArray[i]);
      }
    }
    return resultList;
  }

  public Object getPoppageInfo(String corpId, String popCode, String token)
  {
    Map paramMap = new HashMap();
    paramMap.put("corpId", corpId);
    paramMap.put("popCode", popCode);
    String json = this.restClient.get("/cmp/poppage/getPoppageInfo", token, paramMap);
    Map mapJson = JsonUtils.readValue(json);
    Object obj = mapJson.get("data");
    return obj;
  }
}