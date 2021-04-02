package org.ezplatform.workflow.service;

import java.text.SimpleDateFormat;
import java.util.*;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.core.service.BaseService;
import org.ezplatform.util.StringUtils;
import org.ezplatform.workflow.dao.FlowBuesinessFailDao;
import org.ezplatform.workflow.entity.FlowBuesinessAttach;
import org.ezplatform.workflow.entity.FlowBuesinessFail;
import org.ezplatform.workflow.entity.KingdeeApplicationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

@Service(value="assetsService")
public class AssetsService  extends BaseService<FlowBuesinessFail, String>{
	@Autowired
    @Qualifier("flowBuesinessFailDao")
	private FlowBuesinessFailDao flowBuesinessFailDao;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@Autowired
	private SystemFlowService systemFlowService;
	@Override
	protected JpaBaseDao<FlowBuesinessFail, String> getEntityDao() {
		// TODO Auto-generated method stub
		return this.flowBuesinessFailDao;
	}
	private List<FlowBuesinessAttach> fileList;
	
	public List<FlowBuesinessAttach> getFileList() {
		return fileList;
	}
	
	//erp单据编号
	private String erpdjbh;
	
	public String getErpdjbh() {
		return erpdjbh;
	}
	String zcdbJsonString="{\r\n" +
			"        \"FID\": 0,\r\n" + 
			"        \"FAssetOrgID\": {\r\n" + 
			"            \"FNumber\": \"100\"\r\n" + 
			"        },\r\n" + 
			"        \"FOwnerOrgID\": {\r\n" + 
			"            \"FNumber\": \"100\"\r\n" + 
			"        },\r\n" + 
			"        \"FAlterId\": {\r\n" + 
			"            \"FNumber\": \"ZCLB031\"\r\n" + 
			"        },\r\n" + 
			"        \"FPeriodEnddate\": \"2020-09-23 00:00:00\",\r\n" + 
			"        \"FCbSupplierID\": false,\r\n" + 
			"        \"FCbManufacturer\": false,\r\n" + 
			"        \"FCbRemark\": false,\r\n" + 
			"        \"FCbAssetStatusID\": false,\r\n" + 
			"        \"FAfterAlterId\": -1,\r\n" + 
			"        \"FCbLocality\": false,\r\n" + 
			"        \"FCbPositionID\": false,\r\n" + 
			"        \"FCbSpecification\": false,\r\n" + 
			"        \"FCBPREDICTWORKLOAD\": false,\r\n" + 
			"        \"FCbOrgVal\": false,\r\n" + 
			"        \"FCBWORKLOADUNITID\": false,\r\n" + 
			"        \"FCbAccumDevalue\": false,\r\n" + 
			"        \"FCBINPUTTAX\": false,\r\n" + 
			"        \"FCbResidualvalue\": false,\r\n" + 
			"        \"FCBORIGINALCOST\": false,\r\n" + 
			"        \"FCBEXPENSEVALUE\": false,\r\n" + 
			"        \"FCbDeprMethod\": false,\r\n" + 
			"        \"FCBEXPENSETAX\": false,\r\n" + 
			"        \"FCbLifePeriods\": false,\r\n" + 
			"        \"FCBTOTALWORKLOAD\": false,\r\n" + 
			"        \"FCbAccumDepr\": false,\r\n" + 
			"        \"FPreAllocateEntity\": [\r\n" + 
			"            {\r\n" + 
			"                \r\n" + 
			"                \"FPreAllocUseDeptID\": {\r\n" + 
			"                    \"FNumber\": \"BM000001\"\r\n" + 
			"                },\r\n" + 
			"                \r\n" + 
			"                \"FPreAllocUserID\": {\r\n" + 
			"                    \"FNumber\": \"FYXM08_SYS\"\r\n" + 
			"                }\r\n" + 
			"            }\r\n" + 
			"        ]\r\n" + 
			"    }";

	String zctkJsonString = "{\n" +
			"        \"FID\": 0,\n" +
			"        \"FAssetOrgID\": {\n" +
			"            \"FNumber\": \"100\"\n" +
			"        },\n" +
			"        \"FOwnerOrgID\": {\n" +
			"            \"FNumber\": \"100\"\n" +
			"        },\n" +
			"        \"FAlterId\": {\n" +
			"            \"FNumber\": \"ZCLB0119\"\n" +
			"        },\n" +
			"        \"FPeriodEnddate\": \"2021-01-18 00:00:00\",\n" +
			"        \"FCbSupplierID\": false,\n" +
			"        \"FCbManufacturer\": false,\n" +
			"        \"FCbRemark\": false,\n" +
			"        \"FCbAssetStatusID\": false,\n" +
			"        \"FAfterAlterId\": -1,\n" +
			"        \"FCbLocality\": false,\n" +
			"        \"FCbPositionID\": false,\n" +
			"        \"FCbSpecification\": false,\n" +
			"        \"FCBPREDICTWORKLOAD\": false,\n" +
			"        \"FCbOrgVal\": false,\n" +
			"        \"FCBWORKLOADUNITID\": false,\n" +
			"        \"FCbAccumDevalue\": false,\n" +
			"        \"FCBINPUTTAX\": false,\n" +
			"        \"FCbResidualvalue\": false,\n" +
			"        \"FCBORIGINALCOST\": false,\n" +
			"        \"FCBEXPENSEVALUE\": false,\n" +
			"        \"FCbDeprMethod\": false,\n" +
			"        \"FCBEXPENSETAX\": false,\n" +
			"        \"FCbLifePeriods\": false,\n" +
			"        \"FCBTOTALWORKLOAD\": false,\n" +
			"        \"FCbAccumDepr\": false,\n" +
			"        \"F_KDXF_SSBM1\": {\n" +
			"            \"FNUMBER\": \"BM000001\"\n" +
			"        },\n" +
			"        \"F_KDXF_SSBM\": {\n" +
			"            \"FNUMBER\": \"BM000001\"\n" +
			"        },\n" +
			"        \"F_KDXF_XMLB1\": \"KDXF_XMLX\",\n" +
			"        \"F_KDXF_XMLB\": \"KDXF_XMLX\"\n" +
			"    }";
	
	
	String fywzsqJsonString="{\r\n" + 
			"        \"FBillTypeID\": {\r\n" + 
			"            \"FNUMBER\": \"QTCKD01_SYS\"\r\n" + 
			"        },\r\n" + 
			"        \"FStockOrgId\": {\r\n" + 
			"            \"FNumber\": \"100\"\r\n" + 
			"        },\r\n" + 
			"        \"FPickOrgId\": {\r\n" + 
			"            \"FNumber\": \"100\"\r\n" + 
			"        },\r\n" + 
			"        \"FStockDirect\": \"GENERAL\",\r\n" + 
			"        \"FDate\": \"2020-11-25 00:00:00\",\r\n" + 
			"        \"FOwnerTypeIdHead\": \"BD_OwnerOrg\",\r\n" + 
			"        \"FOwnerIdHead\": {\r\n" + 
			"            \"FNumber\": \"100\"\r\n" + 
			"        },\r\n" + 
			"        \"FNote\": \"123\",\r\n" + 
			"        \"FBaseCurrId\": {\r\n" + 
			"            \"FNumber\": \"PRE001\"\r\n" + 
			"        },\r\n" + 
			"        \"F_KDXF_SSBM\": {\r\n" + 
			"            \"FNUMBER\": \"D000000281\"\r\n" + 
			"        },\r\n" + 
			"        \"F_KDXF_XMJL\": {\r\n" + 
			"            \"FSTAFFNUMBER\": \"20130617\"\r\n" + 
			"        },\r\n" + 
			"        \"F_KDXF_XMLB1\": \"KDXF_XMLX\",\r\n" + 
			"        \"F_KDXF_XMMC\": {\r\n" + 
			"            \"FNumber\": \"D1-20000006\"\r\n" + 
			"        },\r\n" + 
			"        \"F_KDXF_SFXY\": false,\r\n" + 
			"        \"FEntity\": [\r\n" + 
			"            {\r\n" + 
			"                \"FMaterialId\": {\r\n" + 
			"                    \"FNumber\": \"JA1100010002\"\r\n" + 
			"                },\r\n" + 
			"                \"FUnitID\": {\r\n" + 
			"                    \"FNumber\": \"ge\"\r\n" + 
			"                },\r\n" + 
			"                \"FBaseUnitId\": {\r\n" + 
			"                    \"FNumber\": \"ge\"\r\n" + 
			"                },\r\n" + 
			"                \"FOwnerTypeId\": \"BD_OwnerOrg\",\r\n" + 
			"                \"FOwnerId\": {\r\n" + 
			"                    \"FNumber\": \"100\"\r\n" + 
			"                },\r\n" + 
			"                \"FKeeperTypeId\": \"BD_KeeperOrg\",\r\n" + 
			"                \"FDistribution\": false,\r\n" + 
			"                \"FKeeperId\": {\r\n" + 
			"                    \"FNumber\": \"100\"\r\n" + 
			"                }\r\n" + 
			"            }\r\n" + 
			"        ]\r\n" + 
			"    }";
	/**
	 * 资产调拨
	 * @param recordId
	 * @return
	 */
	public List<JSONObject> getZCDBList(String recordId ,String tableName){
		List<JSONObject> resultList=new ArrayList<JSONObject>();
		Map<String,String> paramsMap=new HashMap<String,String>();
		paramsMap.put("maintableName",tableName+"");
		//paramsMap.put("maintableName","zcdbsqb");
		 
		String sqlmain="\r\n" + 
				"select  a.bt ,a.lcbh,a.createdby,a.sqrq,a.createdorg,a.sqly,b.scopevalue,a.xmmc,a.xmbm,a.xmlb,a.gsgs,a.fj ,f.f_kdxf_ssbm, "+ 
				"c.gdzcbm,d.zcbm,e.f_kdxf_cpx,e.f_kdxf_fpbl,e.f_kdxf_kssj,e.f_kdxf_sybm,e.f_kdxf_syr,e.f_kdxf_jssj,e.f_kdxf_fyxm \r\n" + 
				" from zcdbsqb a  left join zcdbsqb_scope  b on a.zcdrr=b.fielddatavalue\r\n"+ 
				" left join zcdbmx c on a.id=c.fkid "+ 
				" left join xfsmzjk.t_kdxf_card@toerp d on c.gdzcbm=d.fnumber \r\n" + 
				" left join xfsmzjk.t_kdxf_allocation@toerp e on d.fnumber = e.f_kdxf_kpbm"+
				"  left join (select FNUMBER,F_KDXF_SSBM from xfsmzjk.T_KDXF_ITEM_SJ@toerp union all  select FNUMBER,FSSBMNUMBER from xfsmzjk.T_KDXF_ITEM@toerp  ) f   on a.xmbm=f.FNUMBER where a.id =:recordId";
		
		String sql5="select  gdzcbm from zcdbmx where   fkid=:recordId  and nvl(gdzcbm,'-1')!='-1' group by  gdzcbm ";
		/*String sql4="  select  c.gdzcbm,a.fnumber,\r\n" + 
				"       a.fname          ,\r\n" + 
				"       a.zcbm           ,\r\n" + 
				"       b.f_kdxf_cpx ,\r\n" + 
				"       b.f_kdxf_fpbl        ,\r\n" + 
				"       b.f_kdxf_kssj      ,\r\n" + 
				"       b.f_kdxf_sybm,\r\n" + 
				"       b.f_kdxf_syr from zcdbmx c  left join  xfsmzjk.t_kdxf_card@toerp a on c.gdzcbm=a.fnumber\r\n" + 
				"  		left join xfsmzjk.t_kdxf_allocation@toerp b\r\n" + 
				"    on a.fnumber = b.f_kdxf_kpbm where   nvl(gdzcbm,'-1')!='-1'";*/

		 Map queryMap=new HashMap();
		 queryMap.put("recordId", recordId);
		 List list=super.findByListNativeQuery(sqlmain, "", queryMap);
		 List tempList=super.findByListNativeQuery(sql5, "", queryMap);
		 List<String> zcbmlist=new ArrayList<String>();
		 String kpbmIds="";//资产卡片编码串
		 if(tempList!=null&&tempList.size()>0) {
			 //资产卡片编码不为空 
			 for(int i=0,size=tempList.size();i<size;i++) {
				 Map map=(Map) tempList.get(i);
				 String gdzcbm=StringUtils.null2String(map.get("gdzcbm"));
				 kpbmIds="$"+gdzcbm+"$"+kpbmIds;
				 zcbmlist.add(gdzcbm);
			 }
		 }
		 /*//根据资产卡片获取资产编码 
		 Map<String,String> zcbmMap=new HashMap<String,String>();
		 if(!"".equals(kpbmIds)) {
			 kpbmIds="FNumber in("+kpbmIds.replace("$$", "','").replace("$", "'")+")";
			 JSONObject queryJson=new JSONObject(true);
			 queryJson.put("FormId", "FA_CARD");
			 queryJson.put("FieldKeys", "FNumber,FAllocAssetNO");
			 queryJson.put("FilterString", kpbmIds);
			 queryJson.put("OrderString", "");
			 queryJson.put("TopRowCount",0);
			 queryJson.put("StartRow", 0);
			 queryJson.put("Limit", 0);
			 zcbmMap = systemFlowService.queryZZKPCode(queryJson);
			 
		 }*/
		
		 if(list!=null&&list.size()>0) {
			 String FCreateOrgId="";//创建组织
			 Map map=(Map) list.get(0);
			 
			 String scopevalue=StringUtils.null2String(map.get("scopevalue"));
			 //根据使用人获取使用人部门
			 String orgFumber= getOrgFnumberByUserId(scopevalue);
			 if("".equals(FCreateOrgId)) {
				 String gsgs=StringUtils.null2String(map.get("gsgs"));
				 Map<String, String> orgCodemap= systemFlowService.getUserOrOrgFNumberById(gsgs, "org",paramsMap);
				 String orgCode=orgCodemap.get("fNumber");
				 //获取所属公司编码
				 FCreateOrgId= systemFlowService.getERPuserOrgId(orgCode);
			 }
			 
			 Map<String, String> userAccountmap= systemFlowService.getUserOrOrgFNumberById(scopevalue, "user",paramsMap);
			 String  FAllocUserID=userAccountmap.get("fNumber");//使用人

			 //记录编码
			 String reccordPm = "";
			 for(int j=0,sizej=zcbmlist.size();j<sizej;j++) {
				 JSONObject zcdbJson=JSONObject.parseObject(zcdbJsonString,Feature.OrderedField);
				 //资产组织 货主组织
				 zcdbJson.put("FAssetOrgID", JSONObject.parseObject("{\"FNumber\":\""+FCreateOrgId+"\"}"));
				 zcdbJson.put("FOwnerOrgID", JSONObject.parseObject("{\"FNumber\":\""+FCreateOrgId+"\"}"));
				 //标题
				 String title=StringUtils.null2String(map.get("bt"));
				 zcdbJson.put("F_KDXF_ZT", title);
				 //编号
				 String bh=StringUtils.null2String(map.get("lcbh"));
				 zcdbJson.put("F_KDXF_LCBH", bh);
				 //申请日期
				 //String sqrq=StringUtils.null2String(map.get("sqrq"));
				 String sqrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date())+" 00:00:00";
				 zcdbJson.put("FPeriodEnddate", sqrq);
				 //变更后所属部门
				 String f_kdxf_ssbm=StringUtils.null2String(map.get("f_kdxf_ssbm"));
				 zcdbJson.put("F_KDXF_SSBM1", JSONObject.parseObject("{\"FNumber\":\""+f_kdxf_ssbm+"\"}"));
				 //项目类别
				 String xmlb=StringUtils.null2String(map.get("xmlb"));
				 zcdbJson.put("F_KDXF_XMLB1", xmlb);
				 String fieldValue="";
				 String fj=StringUtils.null2String(map.get("fj"));
				 if(org.apache.commons.lang3.StringUtils.isNotEmpty(fj)) {
					 fieldValue=fj;
				 }
				 if(org.apache.commons.lang3.StringUtils.isNotEmpty(fieldValue)) {
					 fieldValue="'"+fieldValue.replace("|", "','")+"'";
					 fileList=systemFlowService.getFileListByIds(fieldValue);
				 }
				 
				 //变更后项目名称
				 String xmbm=StringUtils.null2String(map.get("xmbm"));
				 zcdbJson.put("F_KDXF_XMMC2", JSONObject.parseObject("{\"FNumber\":\""+xmbm+"\"}"));
				 //变更原因
				 String sqly=StringUtils.null2String(map.get("sqly"));
				 zcdbJson.put("FAlterReason", sqly);
				 String kpbm=zcbmlist.get(j);
				 zcdbJson.put("FAlterId", JSONObject.parseObject("{\"FNumber\":\""+kpbm+"\"}"));

				 JSONArray FPreAllocateEntityArr=new JSONArray();
				 JSONArray FAllocateEntityArr=new JSONArray();

				 for(int i=0,size=list.size();i<size;i++) {
				 	//此处添加逻辑过滤
					 //kpbm
					 String curPm = ((Map) list.get(i)).get("zcbm")+"";
					 String GetBmSql = "select fnumber from xfsmzjk.t_kdxf_card@toerp where zcbm='"+curPm+"' ";
					 List listBmget = jdbcTemplate.queryForList(GetBmSql);
					 if(listBmget.size()>0){
					 	Map<String,Object> mapTempContinue = (Map<String,Object> ) listBmget.get(0);
						 String tempfnumber = mapTempContinue.get("fnumber")+"";
						 if(!kpbm.equals(tempfnumber)){
							continue;
						 }
					 }

					 JSONObject FAllocateEntity =new JSONObject(true);//变更后 
					 JSONObject FPreAllocateEntity  =new JSONObject(true);//变更前
					 Map maptemp=(Map) list.get(i);
					 //资产编码
					 String zcbm= StringUtils.null2String(maptemp.get("zcbm"));
					 //添加过滤
					 //如果是资产退库，重置编码
					 if(!"zcdbsqb".equalsIgnoreCase(tableName)){
						 if("".equalsIgnoreCase(zcbm)){
							 String sqlGetBm = "select zcbm from xfsmzjk.T_KDXF_CARD@toerp where fnumber='"+kpbm+"'";
							 List listBM = jdbcTemplate.queryForList(sqlGetBm);
							 if(listBM.size()>0){
							 	Map<String,Object> mapBM = (Map<String,Object> )listBM.get(0);
								 zcbm = mapBM.get("zcbm")+"";
							 }
						 }
					 }
					 FAllocateEntity.put("FAllocAssetNO",zcbm);
					 FPreAllocateEntity.put("FPreAllocAssetNO",zcbm);
					 //产品线
					 String fnumber=StringUtils.null2String(maptemp.get("f_kdxf_cpx"));
					 FAllocateEntity.put("F_KDXF_CPX1",JSONObject.parseObject("{\"FNumber\":\""+fnumber+"\"}") );
					
					 //费用比列
					 String fybl=StringUtils.null2String(maptemp.get("f_kdxf_fpbl"));

					 FAllocateEntity.put("FAllocRatio", fybl);
					 FPreAllocateEntity.put("FPreAllocRatio",fybl);

					 if(!"zcdbsqb".equalsIgnoreCase(tableName)){
						 FAllocateEntity.put("FAllocRatio", 100.0);
					 }
					 
					 String kssj=StringUtils.null2String(maptemp.get("f_kdxf_kssj"));//开始时间
					 FAllocateEntity.put("FAllocBeginDate", kssj);
					 FPreAllocateEntity.put("FPreAllocBeginDate",kssj); 
					 
					 String jssj=StringUtils.null2String(maptemp.get("f_kdxf_jssj"));//结束时间
					 FAllocateEntity.put("FAllocEndDate", jssj);
					 FPreAllocateEntity.put("FPreAllocEndDate",jssj); 
					 
					 String fyxm=StringUtils.null2String(maptemp.get("f_kdxf_fyxm"));//费用项目
					 //FYXM08_SYS
					 if(!"zcdbsqb".equalsIgnoreCase(tableName)){
						 if("".equalsIgnoreCase(fyxm)){
							 fyxm = "FYXM08_SYS";
						 }
					 }
					 FAllocateEntity.put("FAllocCostItemID", JSONObject.parseObject("{\"FNumber\":\""+fyxm+"\"}"));//费用项目
					 FPreAllocateEntity.put("FPreAllocCostItemID", JSONObject.parseObject("{\"FNumber\":\""+fyxm+"\"}"));//费用项目
					 
					 String PFAllocUserID=StringUtils.null2String(maptemp.get("f_kdxf_syr"));//变更前使用人
					 if("zcdbsqb".equalsIgnoreCase(tableName)){
						//转移人员
						 String sql1 = "select f_kdxf_ygrgxxbm as fNumber from xfsmzjk.T_KDXF_STAFF@toerp where fnumber ='"+FAllocUserID+"' ";
						 List list1 = jdbcTemplate.queryForList(sql1);
						 if(list1.size()>0){
							 Map<String,Object> map1 = (Map<String,Object>)list1.get(0);
							 FAllocUserID = map1.get("fNumber")+"";
						 }
					 }
					 if("zcdbsqb".equalsIgnoreCase(tableName)){
						 String sql2 = "select f_kdxf_ygrgxxbm as fNumber from  xfsmzjk.T_KDXF_STAFF@toerp where fnumber='"+PFAllocUserID+"' ";
						 List list2 = jdbcTemplate.queryForList(sql2);
						 if(list2.size()>0){
							 Map<String,Object> map2 = (Map<String,Object>)list2.get(0);
							 PFAllocUserID = map2.get("fNumber")+"";
						 }
					 }

					 FAllocateEntity.put("FAllocUserID", JSONObject.parseObject("{\"FSTAFFNUMBER\":\""+FAllocUserID+"\"}"));//使用人
					 FPreAllocateEntity.put("FPreAllocUserID", JSONObject.parseObject("{\"FSTAFFNUMBER\":\""+PFAllocUserID+"\"}"));//使用人 
					 
					 String PorgFumber=StringUtils.null2String(maptemp.get("f_kdxf_sybm"));//变更前使用部门  
					 FAllocateEntity.put("FAllocUseDeptID", JSONObject.parseObject("{\"FNumber\":\""+orgFumber+"\"}"));//使用部门  
					 FPreAllocateEntity.put("FPreAllocUseDeptID", JSONObject.parseObject("{\"FNumber\":\""+PorgFumber+"\"}"));//使用部门  
					 FAllocateEntityArr.add(FAllocateEntity);
					 FPreAllocateEntityArr.add(FPreAllocateEntity);
				 }
				 zcdbJson.put("FAllocateEntity", FAllocateEntityArr );
				 zcdbJson.put("FPreAllocateEntity", FPreAllocateEntityArr );
				 KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
							"true", zcdbJson);
				 resultList.add((JSONObject) JSONObject.toJSON(appData));
			 }
		 }
		
		return resultList;
	}


	/**
	 * 资产退库
	 * @param recordId
	 * @return
	 */
	public List<JSONObject> getZCTKList(String recordId ,String tableName){
		List<JSONObject> resultList=new ArrayList<JSONObject>();
		Map<String,String> paramsMap=new HashMap<String,String>();
		paramsMap.put("maintableName",tableName+"");

		String sqlmain="" +
					"select  a.bt ,a.lcbh,a.createdby,a.sqrq,a.createdorg,a.tkyy sqly,b.scopevalue,a.xmbm,a.xmlb,a.gsgs,a.fj ,f.f_kdxf_ssbm, \r\n" +
					" c.gdzcbm,d.zcbm,e.f_kdxf_cpx,e.f_kdxf_fpbl,e.f_kdxf_kssj,e.f_kdxf_sybm,e.f_kdxf_syr,e.f_kdxf_jssj,e.f_kdxf_fyxm \r\n" +
					" from zctksqb a  left join zctksqb_scope  b on a.ZCBGYXM=b.fielddatavalue " +
					" left join zctkmxzb c on a.id=c.fkid "+
					" left join xfsmzjk.t_kdxf_card@toerp d on c.gdzcbm=d.fnumber  " +
					" left join xfsmzjk.t_kdxf_allocation@toerp e on d.fnumber = e.f_kdxf_kpbm"+
					"  left join (select FNUMBER,F_KDXF_SSBM from xfsmzjk.T_KDXF_ITEM_SJ@toerp union all  select FNUMBER,FSSBMNUMBER from xfsmzjk.T_KDXF_ITEM@toerp  ) f   on a.xmbm=f.FNUMBER where a.id =:recordId";

		String sql5="select  gdzcbm from zctkmxzb where   fkid=:recordId  and nvl(gdzcbm,'-1')!='-1' group by  gdzcbm ";

		Map queryMap=new HashMap();
		queryMap.put("recordId", recordId);
		List list=super.findByListNativeQuery(sqlmain, "", queryMap);
		List tempList=super.findByListNativeQuery(sql5, "", queryMap);
		List<String> zcbmlist=new ArrayList<String>();
		String kpbmIds="";//资产卡片编码串
		if(tempList!=null&&tempList.size()>0) {
			//资产卡片编码不为空
			for(int i=0,size=tempList.size();i<size;i++) {
				Map map=(Map) tempList.get(i);
				String gdzcbm=StringUtils.null2String(map.get("gdzcbm"));
				kpbmIds="$"+gdzcbm+"$"+kpbmIds;
				zcbmlist.add(gdzcbm);
			}
		}

		if(list!=null&&list.size()>0) {
			String FCreateOrgId="";//创建组织
			Map map=(Map) list.get(0);

			String scopevalue=StringUtils.null2String(map.get("scopevalue"));
			//根据使用人获取使用人部门
			String orgFumber= getOrgFnumberByUserId(scopevalue);
			if("".equals(FCreateOrgId)) {
				String gsgs=StringUtils.null2String(map.get("gsgs"));
				Map<String, String> orgCodemap= systemFlowService.getUserOrOrgFNumberById(gsgs, "org",paramsMap);
				String orgCode=orgCodemap.get("fNumber");
				//获取所属公司编码
				FCreateOrgId= systemFlowService.getERPuserOrgId(orgCode);
			}

			Map<String, String> userAccountmap= systemFlowService.getUserOrOrgFNumberById(scopevalue, "user",paramsMap);

			String tempScopeValue = scopevalue;

			String sqlGetUser = " select a.f_kdxf_ygrgxxbm as fNumber from xfsmzjk.T_KDXF_STAFF@toerp a inner join sys_user b on a.fusername = b.login_name where b.id = '"+ tempScopeValue +"'  ";
			List getUserList = jdbcTemplate.queryForList(sqlGetUser);
			if(getUserList.size()>0){
				Map<String,String> mapUser = (Map<String,String>)getUserList.get(0);
				userAccountmap = (Map<String,String>)getUserList.get(0);
			}
			String  FAllocUserID=userAccountmap.get("fNumber");//使用人
			//使用前部门
			for(int j=0,sizej=zcbmlist.size();j<sizej;j++) {
				JSONObject zcdbJson=JSONObject.parseObject(zctkJsonString,Feature.OrderedField);
				//资产组织 货主组织
				zcdbJson.put("FAssetOrgID", JSONObject.parseObject("{\"FNumber\":\""+ FCreateOrgId +"\"}"));
				zcdbJson.put("FOwnerOrgID", JSONObject.parseObject("{\"FNumber\":\""+ FCreateOrgId +"\"}"));
				//标题
				String title=StringUtils.null2String(map.get("bt"));
				zcdbJson.put("F_KDXF_ZT", title);
				//编号
				String bh=StringUtils.null2String(map.get("lcbh"));
				zcdbJson.put("F_KDXF_LCBH", bh);
				//申请日期
				//String sqrq=StringUtils.null2String(map.get("sqrq"));
				String sqrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date())+" 00:00:00";
				zcdbJson.put("FPeriodEnddate", sqrq);
				//变更后所属部门
				String f_kdxf_ssbm=StringUtils.null2String(map.get("f_kdxf_ssbm"));
				//BM000001
				if("".equals(f_kdxf_ssbm)){
					f_kdxf_ssbm = "BM000001";
				}
				//zcdbJson.put("F_KDXF_SSBM1", JSONObject.parseObject("{\"FNumber\":\""+f_kdxf_ssbm+"\"}"));
				//zcdbJson.put("F_KDXF_SSBM", JSONObject.parseObject("{\"FNumber\":\""+f_kdxf_ssbm+"\"}"));
				//项目类别
				String xmlb=StringUtils.null2String(map.get("xmlb"));
				zcdbJson.put("F_KDXF_XMLB1", xmlb.toUpperCase());
				zcdbJson.put("F_KDXF_XMLB", xmlb.toUpperCase());
				String fieldValue="";
				String fj=StringUtils.null2String(map.get("fj"));
				if(org.apache.commons.lang3.StringUtils.isNotEmpty(fj)) {
					fieldValue=fj;
				}
				if(org.apache.commons.lang3.StringUtils.isNotEmpty(fieldValue)) {
					fieldValue="'"+fieldValue.replace("|", "','")+"'";
					fileList=systemFlowService.getFileListByIds(fieldValue);
				}

				//变更后项目名称
				String xmbm=StringUtils.null2String(map.get("xmbm"));
				//充值默认
				if(!"".equals(xmbm)){
					zcdbJson.put("F_KDXF_XMMC2", JSONObject.parseObject("{\"FNumber\":\""+xmbm+"\"}"));
					//xmbm = "D2-20000001";
				}else{

					String kpbmTemp=zcbmlist.get(j);
					String getXmbmSql = "select t.fnumber,m.f_kdxf_xmbm,m.f_kdxf_xmmc,m.f_kdxf_sybm,m.f_kdxf_sybmmc from xfsmzjk.t_kdxf_card@toerp t join xfsmzjk.t_kdxf_allocation@toerp m on t.fnumber=m.f_kdxf_kpbm where t.fnumber = '"+kpbmTemp+"' ";
					List listgEXMBM = jdbcTemplate.queryForList(getXmbmSql);
					if(listgEXMBM.size()>0){
						Map<String,String> mapXMBM = (Map<String,String>)listgEXMBM.get(0);
						if(mapXMBM!=null){
							xmbm = mapXMBM.get("f_kdxf_xmbm")+"";
							f_kdxf_ssbm = mapXMBM.get("f_kdxf_sybm")+"";
						}
					}

					zcdbJson.put("F_KDXF_XMMC2", JSONObject.parseObject("{\"FNumber\":\""+xmbm+"\"}"));
				}

				zcdbJson.put("F_KDXF_SSBM1", JSONObject.parseObject("{\"FNumber\":\""+f_kdxf_ssbm+"\"}"));
				zcdbJson.put("F_KDXF_SSBM", JSONObject.parseObject("{\"FNumber\":\""+f_kdxf_ssbm+"\"}"));


				//变更原因
				String sqly=StringUtils.null2String(map.get("sqly"));
				zcdbJson.put("FAlterReason", sqly);
				String kpbm=zcbmlist.get(j);
				zcdbJson.put("FAlterId", JSONObject.parseObject("{\"FNumber\":\""+kpbm+"\"}"));

				JSONArray FPreAllocateEntityArr=new JSONArray();
				JSONArray FAllocateEntityArr=new JSONArray();
				for(int i=0,size=list.size();i<size;i++) {
					JSONObject FAllocateEntity =new JSONObject(true);//变更后
					JSONObject FPreAllocateEntity  =new JSONObject(true);//变更前
					Map maptemp=(Map) list.get(i);
					//资产编码
					String zcbm= StringUtils.null2String(maptemp.get("zcbm"));

					//如果是资产退库，重置编码
					if("".equalsIgnoreCase(zcbm)){
						String sqlGetBm = "select zcbm from xfsmzjk.T_KDXF_CARD@toerp where fnumber='"+kpbm+"'";
						List listBM = jdbcTemplate.queryForList(sqlGetBm);
						if(listBM.size()>0){
							Map<String,Object> mapBM = (Map<String,Object> )listBM.get(0);
							zcbm = mapBM.get("zcbm")+"";
						}
					}

					FAllocateEntity.put("FAllocAssetNO",zcbm);
					FPreAllocateEntity.put("FPreAllocAssetNO",zcbm);
					//产品线
					String fnumber=StringUtils.null2String(maptemp.get("f_kdxf_cpx"));
					FAllocateEntity.put("F_KDXF_CPX1",JSONObject.parseObject("{\"FNumber\":\""+fnumber+"\"}") );

					//费用比列
					String fybl=StringUtils.null2String(maptemp.get("f_kdxf_fpbl"));

					FAllocateEntity.put("FAllocRatio", fybl);
					FPreAllocateEntity.put("FPreAllocRatio",fybl);

					FAllocateEntity.put("FAllocRatio", 100.0);

					String kssj=StringUtils.null2String(maptemp.get("f_kdxf_kssj"));//开始时间
					FAllocateEntity.put("FAllocBeginDate", kssj);
					FPreAllocateEntity.put("FPreAllocBeginDate",kssj);

					String jssj=StringUtils.null2String(maptemp.get("f_kdxf_jssj"));//结束时间
					FAllocateEntity.put("FAllocEndDate", jssj);
					FPreAllocateEntity.put("FPreAllocEndDate",jssj);

					String fyxm=StringUtils.null2String(maptemp.get("f_kdxf_fyxm"));//费用项目
					//FYXM08_SYS
					if(!"zcdbsqb".equalsIgnoreCase(tableName)){
						if("".equalsIgnoreCase(fyxm)){
							fyxm = "FYXM08_SYS";
						}
					}
					FAllocateEntity.put("FAllocCostItemID", JSONObject.parseObject("{\"FNumber\":\""+fyxm+"\"}"));//费用项目
					FPreAllocateEntity.put("FPreAllocCostItemID", JSONObject.parseObject("{\"FNumber\":\""+fyxm+"\"}"));//费用项目

					String PFAllocUserID=StringUtils.null2String(maptemp.get("f_kdxf_syr"));//变更前使用人

					FAllocateEntity.put("FAllocUserID", JSONObject.parseObject("{\"FSTAFFNUMBER\":\""+"201700743_11171794_1"+"\"}"));//使用人
					FPreAllocateEntity.put("FPreAllocUserID", JSONObject.parseObject("{\"FSTAFFNUMBER\":\""+FAllocUserID+"\"}"));//使用人

					String PorgFumber=StringUtils.null2String(maptemp.get("f_kdxf_sybm"));//变更前使用部门
					FAllocateEntity.put("FAllocUseDeptID", JSONObject.parseObject("{\"FNumber\":\""+"D000003100"+"\"}"));//使用部门
					FPreAllocateEntity.put("FPreAllocUseDeptID", JSONObject.parseObject("{\"FNumber\":\""+orgFumber+"\"}"));//使用部门
					FAllocateEntityArr.add(FAllocateEntity);
					FPreAllocateEntityArr.add(FPreAllocateEntity);

					//linshi
					zcdbJson.put("F_KDXF_SSBM1", JSONObject.parseObject("{\"FNumber\":\""+orgFumber+"\"}"));
					zcdbJson.put("F_KDXF_SSBM", JSONObject.parseObject("{\"FNumber\":\""+orgFumber+"\"}"));
				}

				JSONArray FFinEntryArr=new JSONArray();
				zcdbJson.put("FAllocateEntity", FAllocateEntityArr );
				zcdbJson.put("FPreAllocateEntity", FPreAllocateEntityArr );
				/*JSONObject FFinEntryEntity  =new JSONObject(true);//变更前
				FFinEntryEntity.put("FPreAcctPolicyID", JSONObject.parseObject("{\"FNumber\":\""+"KJZC01_SYS"+"\"}"));
				FFinEntryEntity.put("FPreDeprMethod", "1");
				FFinEntryEntity.put("FCurrencyID", JSONObject.parseObject("{\"FNumber\":\""+"PRE001"+"\"}"));
				FFinEntryArr.add(FFinEntryEntity);*/
				//zcdbJson.put("FFinEntry",FFinEntryArr);

				//处理项目

				KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
						"true", zcdbJson);
				resultList.add((JSONObject) JSONObject.toJSON(appData));
			}
		}

		return resultList;
	}
	
	/**
	 * 费用物资申请
	 */
	public List<JSONObject> getFYWZSQList(String recordId,String tableName ){
		List<JSONObject> resultList=new ArrayList<JSONObject>();
		Map<String,String> paramsMap=new HashMap<String,String>();
		 paramsMap.put("maintableName","fylwzsqb");
		 
		String sqlmain="select a.title ,a.erpdjbh,a.bh,a.createdby,a.xmlx,a.sqrq,a.createdorg,a.xmmc,a.xmbm,a.gsgs,a.xgfj,a.sqly,b.scopevalue \r\n" + 
				" from fylwzsqb a left join fylwzsqb_scope b  on a.apcangguanyuan=b.fielddatavalue  where wzdl='3' and  a.id=:recordId";
		
		String sql5="select  wzbm,wzmc,ggxh,jldwbm,sl,bz from  fylwzsqxhwzzb where fkid=:recordId ";
		//接待需求申请
		if("jdxqsqb".equals(tableName)) {
			sqlmain="select a.title,a.erpdjbh,a.bh,a.createdby,a.xmlx,a.sqrq,a.createdorg,a.fygsbm xmmc,a.xmbm,a.gsgs,a.bz,b.scopevalue \r\n" + 
					" from jdxqsqb a left join jdxqsqb_scope b  on a.xqr=b.fielddatavalue  where sfsywzsq='true' and  a.id=:recordId";
			
			sql5="select  wzbm,wzmc,'' ggxh,jldwbm,sl,bz from  wzsqzb where fkid=:recordId ";
		}
		 Map queryMap=new HashMap();
		 queryMap.put("recordId", recordId);
		 List list=super.findByListNativeQuery(sqlmain, "", queryMap);
		 List tempList=super.findByListNativeQuery(sql5, "", queryMap);
		
		 if(list!=null&&list.size()>0) {
			 	 String FCreateOrgId="";//创建组织
				 Map map=(Map) list.get(0);
				 erpdjbh=StringUtils.null2String(map.get("erpdjbh"));//erp单据号
				 JSONObject zcdbJson=JSONObject.parseObject(fywzsqJsonString,Feature.OrderedField);
				 String gsgs=StringUtils.null2String(map.get("gsgs"));
				 Map<String, String> orgCodemap= systemFlowService.getUserOrOrgFNumberById(gsgs, "org",paramsMap);
				 String orgCode=orgCodemap.get("fNumber");
				 //获取所属公司编码
				 FCreateOrgId= systemFlowService.getERPuserOrgId(orgCode);
				 //库存组织
				 zcdbJson.put("FStockOrgId", JSONObject.parseObject("{\"FNumber\":\""+FCreateOrgId+"\"}"));
				 //标题
				 String title=StringUtils.null2String(map.get("title"));
				 zcdbJson.put("F_KDXF_ZT", title);
				 //编号
				 String bh=StringUtils.null2String(map.get("bh"));
				 zcdbJson.put("F_KDXF_LCBH", bh);
				 //申请日期
				 String sqrq=StringUtils.null2String(map.get("sqrq"));
				 //zcdbJson.put("FDate", sqrq);
			     zcdbJson.put("FDate",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
				 //单据类型  默认值
				 zcdbJson.put("FBillTypeID", JSONObject.parseObject("{\"FNumber\":\"QTCKD10_SYS\"}"));
				 //库存方向 FStockDirect 默认 普通
				 zcdbJson.put("FStockDirect", "GENERAL");
				
				 //项目所属部门 
				 zcdbJson.put("F_KDXF_SSBM", JSONObject.parseObject("{\"FNumber\":\""+orgCode+"\"}"));
				 
					 
				 //FOwnerTypeIdHead  货主类型 默认值 BD_OwnerOrg
				 zcdbJson.put("FOwnerTypeIdHead", "BD_OwnerOrg");
				 //申请人
				 String createdby=StringUtils.null2String(map.get("createdby"));
				 String userFumber = systemFlowService.getZRGInfo(createdby, paramsMap);
				 //申请人组织
				 String createdorg=StringUtils.null2String(map.get("createdorg"));
				 Map<String, String> orgTempmap= systemFlowService.getUserOrOrgFNumberById(createdorg, "org",paramsMap);
				 String orgFumber=orgTempmap.get("fNumber");
				
				 
				 //仓管员
				 String scopevalue=StringUtils.null2String(map.get("scopevalue"));
				 Map<String, String> userAccountmap= systemFlowService.getUserOrOrgFNumberById(scopevalue, "user",paramsMap);
				 String  FAllocUserID=userAccountmap.get("fNumber");
				 if("fylwzsqb".equals(tableName)) {
					 //研发类物资申请
					 zcdbJson.put("FSTOCKERID", JSONObject.parseObject("{\"FSTAFFNUMBER\":\""+FAllocUserID+"\"}"));//使用人
					//领料人
					 zcdbJson.put("FPickerId", JSONObject.parseObject("{\"FStaffNumber\":\""+userFumber+"\"}"));
					//领料人部门
					 zcdbJson.put("FDeptId", JSONObject.parseObject("{\"FNumber\":\""+orgFumber+"\"}"));
				 }else {
					 //业务招待流程
					 //需求人
					 String xqr=scopevalue;
					 if(org.apache.commons.lang3.StringUtils.isNotEmpty(xqr)) {
						 String xqrId = systemFlowService.getZRGInfo(xqr, paramsMap);
						 //领料人
						 zcdbJson.put("FPickerId", JSONObject.parseObject("{\"FStaffNumber\":\""+xqrId+"\"}"));
						 //根据需求人id 获取组织code
						 Map<String, String> orgMap=systemFlowService.getUserOrOrgIdByFNumber(xqr, "userorg");
						 orgTempmap= systemFlowService.getUserOrOrgFNumberById(orgMap.get("id"), "org",paramsMap);
						 orgFumber=orgTempmap.get("fNumber");
						 //领料人部门
						 zcdbJson.put("FDeptId", JSONObject.parseObject("{\"FNumber\":\""+orgFumber+"\"}"));
					 }else {
						//领料人
						 zcdbJson.put("FPickerId", JSONObject.parseObject("{\"FStaffNumber\":\""+userFumber+"\"}"));
						//领料人部门
						 zcdbJson.put("FDeptId", JSONObject.parseObject("{\"FNumber\":\""+orgFumber+"\"}"));
					 }
				 }
				
				 
				 //申请理由
				 String sqly=StringUtils.null2String(map.get("sqly"));
				 zcdbJson.put("FNOTE", sqly);
				 //项目类型 
				 String xmlx=StringUtils.null2String(map.get("xmlx"));
				 zcdbJson.put("F_KDXF_XMLB1", xmlx);
				 //项目编码 F_KDXF_XMMC
				 String xmbm=StringUtils.null2String(map.get("xmbm"));
				 zcdbJson.put("F_KDXF_XMMC", JSONObject.parseObject("{\"FNumber\":\""+xmbm+"\"}"));
				
				 
				 String fieldValue="";
				 String fj=StringUtils.null2String(map.get("xgfj"));
				 if(org.apache.commons.lang3.StringUtils.isNotEmpty(fj)) {
					 fieldValue=fj;
				 }
				 if(org.apache.commons.lang3.StringUtils.isNotEmpty(fieldValue)) {
					 fieldValue="'"+fieldValue.replace("|", "','")+"'";
					 fileList=systemFlowService.getFileListByIds(fieldValue);
				 }
				 JSONArray FEntityArr =new JSONArray();
				 for(int i=0,size=tempList.size();i<size;i++) {
					 JSONObject FEntity  =new JSONObject(true);//变更前
					 Map maptemp=(Map) tempList.get(i);
					// wzbm,wzmc,ggxh,jldw,sl
					 //物料名称
					 String wzmc=StringUtils.null2String(maptemp.get("wzbm"));
					 FEntity.put("F_KDXF_MXMC", wzmc);
					 //物料编码
					 String wzbm= StringUtils.null2String(maptemp.get("wzbm"));
					 FEntity.put("FMATERIALID",JSONObject.parseObject("{\"FNumber\":\""+wzbm+"\"}"));
					 //单位
					 String jldw=StringUtils.null2String(maptemp.get("jldwbm"));
					 FEntity.put("FUnitID",JSONObject.parseObject("{\"FNumber\":\""+jldw+"\"}") );
					 //发货仓库
					 String shck="99.999";
					 FEntity.put("FSTOCKID", JSONObject.parseObject("{\"FNumber\":\""+shck+"\"}"));
					 //实收数量
					 String sl=StringUtils.null2String(maptemp.get("sl"));
					 FEntity.put("FQty", sl);
					 //以下默认值 
					  
					 String bz=StringUtils.null2String(maptemp.get("bz"));
					 FEntity.put("FEntryNote", bz);
					      
					 FEntity.put("FStockStatusId", JSONObject.parseObject("{\"FNumber\":\"KCZT001\"}"));
					 FEntity.put("FOWNERTYPEID", "BD_OwnerOrg");
					// FEntity.put("FSTOCKSTATUSID", JSONObject.parseObject("{\"FNumber\":\"KCZT01_SYS\"}"));
					 FEntity.put("FOWNERTYPEID", "BD_OwnerOrg");
					 FEntity.put("FOWNERID", JSONObject.parseObject("{\"FNumber\":\"100\"}"));
					 FEntity.put("FKEEPERTYPEID", "BD_KeeperOrg");	
					 FEntity.put("FKEEPERID", JSONObject.parseObject("{\"FNumber\":\"100\"}"));	
						
					 FEntityArr.add(FEntity);
				 }
				 zcdbJson.put("FEntity", FEntityArr);
				 KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
							"true", zcdbJson,"fylwzsqb",recordId);
				 resultList.add((JSONObject) JSONObject.toJSON(appData));
		 }
		
		return resultList;
	}
	
	
	
	/**
	 * 现货退库申请
	 */
	public List<JSONObject> getXHTKQList(String recordId ){
		List<JSONObject> resultList=new ArrayList<JSONObject>();
		Map<String,String> paramsMap=new HashMap<String,String>();
		 paramsMap.put("maintableName","xhtksqb");
		 
		String sqlmain=" select a.bt ,a.lcbh,a.createdby,a.xmlb,a.sqrq,a.createdorg,a.xmmc,a.xmbm,a.gsgs,a.fj xgfj,a.thly sqly,b.scopevalue \r\n" + 
				" from xhtksqb a left join xhtksqb_scope b  on a.xqr=b.fielddatavalue  where  a.id=:recordId ";
		
		String sql5="select  wzbm,wzmc,ggxh,jldwbm,thsl sl from  xhtkmxzb where fkid=:recordId ";
		
		 Map queryMap=new HashMap();
		 queryMap.put("recordId", recordId);
		 List list=super.findByListNativeQuery(sqlmain, "", queryMap);
		 List tempList=super.findByListNativeQuery(sql5, "", queryMap);
		
		 if(list!=null&&list.size()>0) {
			 	 String FCreateOrgId="";//创建组织
				 Map map=(Map) list.get(0);
				 JSONObject zcdbJson=JSONObject.parseObject(fywzsqJsonString,Feature.OrderedField);
				 String gsgs=StringUtils.null2String(map.get("gsgs"));
				 Map<String, String> orgCodemap= systemFlowService.getUserOrOrgFNumberById(gsgs, "org",paramsMap);
				 String orgCode=orgCodemap.get("fNumber");
				 //获取所属公司编码
				 FCreateOrgId= systemFlowService.getERPuserOrgId(orgCode);
				 //库存组织
				 zcdbJson.put("FStockOrgId", JSONObject.parseObject("{\"FNumber\":\""+FCreateOrgId+"\"}"));
				 //标题
				 String title=StringUtils.null2String(map.get("bt"));
				 zcdbJson.put("F_KDXF_ZT", title);
				 //编号
				 String bh=StringUtils.null2String(map.get("lcbh"));
				 zcdbJson.put("F_KDXF_LCBH", bh);
				 //申请日期
				 String sqrq=StringUtils.null2String(map.get("sqrq"));
			     zcdbJson.put("FDate",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
				 //zcdbJson.put("FDate", sqrq);
				//发货仓库
				 String fhck=StringUtils.null2String(map.get("fhck"));
				 fhck="99.999";
				 //单据类型  默认值
				 zcdbJson.put("FBillTypeID", JSONObject.parseObject("{\"FNumber\":\"QTCKD10_SYS\"}"));
				 //库存方向 FStockDirect 默认 退货 RETURN  
				 zcdbJson.put("FStockDirect", "RETURN");
				 //部门 
				 zcdbJson.put("F_KDXF_SSBM", JSONObject.parseObject("{\"FNumber\":\""+orgCode+"\"}"));
				 zcdbJson.put("FDeptId", JSONObject.parseObject("{\"FNumber\":\""+orgCode+"\"}"));
				 //申请人
				 String createdby=StringUtils.null2String(map.get("createdby"));
				 String userFumber = systemFlowService.getZRGInfo(createdby, paramsMap);
				//领料人
				 zcdbJson.put("FPickerId", JSONObject.parseObject("{\"FStaffNumber\":\""+userFumber+"\"}"));
				 //FOwnerTypeIdHead  货主类型 默认值 BD_OwnerOrg
				 zcdbJson.put("FOwnerTypeIdHead", "BD_OwnerOrg");
				 //仓管员
				 String scopevalue=StringUtils.null2String(map.get("scopevalue"));
				 Map<String, String> userAccountmap= systemFlowService.getUserOrOrgFNumberById(scopevalue, "user",paramsMap);
				 String  FAllocUserID=userAccountmap.get("fNumber");
				 zcdbJson.put("FSTOCKERID", JSONObject.parseObject("{\"FSTAFFNUMBER\":\""+FAllocUserID+"\"}"));//使用人 
				 //申请理由
				 String sqly=StringUtils.null2String(map.get("sqly"));
				 zcdbJson.put("FNOTE", sqly);
				 //项目类型 
				 String xmlb=StringUtils.null2String(map.get("xmlb"));
				 zcdbJson.put("F_KDXF_XMLB1", xmlb);
				 //项目编码 F_KDXF_XMMC
				 String xmbm=StringUtils.null2String(map.get("xmbm"));
				 zcdbJson.put("F_KDXF_XMMC", JSONObject.parseObject("{\"FNumber\":\""+xmbm+"\"}"));
				
				 
				 String fieldValue="";
				 String fj=StringUtils.null2String(map.get("xgfj"));
				 if(org.apache.commons.lang3.StringUtils.isNotEmpty(fj)) {
					 fieldValue=fj;
				 }
				 if(org.apache.commons.lang3.StringUtils.isNotEmpty(fieldValue)) {
					 fieldValue="'"+fieldValue.replace("|", "','")+"'";
					 fileList=systemFlowService.getFileListByIds(fieldValue);
				 }
				 JSONArray FEntityArr =new JSONArray();
				 for(int i=0,size=tempList.size();i<size;i++) {
					 JSONObject FEntity  =new JSONObject(true);//变更前
					 Map maptemp=(Map) tempList.get(i);
					// wzbm,wzmc,ggxh,jldw,sl
					 //物料编码
					 String wzbm= StringUtils.null2String(maptemp.get("wzbm"));
					 FEntity.put("FMATERIALID",JSONObject.parseObject("{\"FNumber\":\""+wzbm+"\"}"));
					 //单位
					 String jldw=StringUtils.null2String(maptemp.get("jldwbm"));
					 FEntity.put("FUnitID",JSONObject.parseObject("{\"FNumber\":\""+jldw+"\"}") );
					 //发货仓库
					 FEntity.put("FSTOCKID", JSONObject.parseObject("{\"FNumber\":\""+fhck+"\"}"));
					 //实收数量
					 String sl=StringUtils.null2String(maptemp.get("sl"));
					 FEntity.put("FQty ", sl);
					 FEntity.put("FBaseQty ", sl);
					  
					 //以下默认值 
					 FEntity.put("FStockStatusId", JSONObject.parseObject("{\"FNumber\":\"KCZT001\"}"));
					 FEntity.put("FOWNERTYPEID", "BD_OwnerOrg");
					 FEntity.put("FOWNERID", JSONObject.parseObject("{\"FNumber\":\"100\"}"));
					 FEntity.put("FKEEPERTYPEID", "BD_KeeperOrg");	
					 FEntity.put("FKEEPERID", JSONObject.parseObject("{\"FNumber\":\"100\"}"));	
						
					 FEntityArr.add(FEntity);
				 }
				 zcdbJson.put("FEntity", FEntityArr);
				 KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
							"true", zcdbJson,"xhtksqb",recordId);
				 resultList.add((JSONObject) JSONObject.toJSON(appData));
		 }
		
		return resultList;
	}
	/**
	 * 获取人员所在部门
	 * @param
	 * @return scopevalue
	 */
	private String getOrgFnumberByUserId(String userId) {
		Map paramsMap=new HashMap();
		String fNumber="";
		 paramsMap.put("userId",userId);
		String sql="select org_code  as fNumber from  sys_org a left join sys_org_user b on a.id=b.org_id  where  b.user_id=:userId";
		 List list = super.findByListNativeQuery(sql, "", paramsMap);
		 if ((list != null) && (!list.isEmpty())) {
		       fNumber = StringUtils.null2String(((Map)list.get(0)).get("fNumber"));
		    }
		 return fNumber;
	}
	
}
