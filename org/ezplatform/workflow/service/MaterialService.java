package org.ezplatform.workflow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.core.service.BaseService;
import org.ezplatform.util.StringUtils;
import org.ezplatform.workflow.dao.FlowBuesinessFailDao;
import org.ezplatform.workflow.entity.FlowBuesinessAttach;
import org.ezplatform.workflow.entity.FlowBuesinessFail;
import org.ezplatform.workflow.entity.KingdeeApplicationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
@Service(value="materialService")
public class MaterialService  extends BaseService<FlowBuesinessFail, String>{
	@Autowired
    @Qualifier("flowBuesinessFailDao")
	private FlowBuesinessFailDao flowBuesinessFailDao;
	
	
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

	private String materialJsonModelString="{\r\n" + 
			"        \"FMATERIALID\": 0,\r\n" + 
			"        \"FCreateOrgId\": {\r\n" + 
			"            \"FNumber\": \"100\"\r\n" + 
			"        },\r\n" + 
			"        \"FUseOrgId\": {\r\n" + 
			"            \"FNumber\": \"100\"\r\n" + 
			"        },\r\n" + 
			"        \"FName\": \"\",\r\n" + 
			"        \"FMaterialGroup\": {\r\n" + 
			"            \"FNumber\": \"1111\"\r\n" + 
			"        },\r\n" + 
			"        \"FImgStorageType\": \"B\",\r\n" + 
			"        \"FIsSalseByNet\": false,\r\n" + 
			"        \"F_KDXF_YCLH\": \"22\",\r\n" + 
			"        \"F_KDXF_RZ\": false,\r\n" + 
			"        \"F_KDXF_ZLDJ\": \"A\",\r\n" + 
			"        \"F_KDXF_SFWG\": false,\r\n" + 
			"        \"FSubHeadEntity\": {\r\n" + 
			"            \"FIsControlSal\": false,\r\n" + 
			"            \"FIsAutoRemove\": false,\r\n" + 
			"            \"FIsMailVirtual\": false\r\n" + 
			"        },\r\n" + 
			"        \"SubHeadEntity\": {\r\n" + 
			"            \"FErpClsID\": \"1\",\r\n" + 
			"            \"FFeatureItem\": \"1\",\r\n" + 
			"            \"FCategoryID\": {\r\n" + 
			"                \"FNumber\": \"CHLB01_SYS\"\r\n" + 
			"            },\r\n" + 
			"            \"FTaxType\": {\r\n" + 
			"                \"FNumber\": \"WLDSFL01_SYS\"\r\n" + 
			"            },\r\n" + 
			"            \"FTaxRateId\": {\r\n" + 
			"                \"FNUMBER\": \"SL02_SYS\"\r\n" + 
			"            },\r\n" + 
			"            \"FBaseUnitId\": {\r\n" + 
			"                \"FNumber\": \"Pcs\"\r\n" + 
			"            },\r\n" + 
			"            \"FIsPurchase\": true,\r\n" + 
			"            \"FIsInventory\": true,\r\n" + 
			"            \"FIsSubContract\": false,\r\n" + 
			"            \"FIsSale\": true,\r\n" + 
			"            \"FIsProduce\": false,\r\n" + 
			"            \"FIsAsset\": false\r\n" + 
			"        },\r\n" + 
			"        \"SubHeadEntity1\": {\r\n" + 
			"            \"FStoreUnitID\": {\r\n" + 
			"                \"FNumber\": \"Pcs\"\r\n" + 
			"            },\r\n" + 
			"            \"FUnitConvertDir\": \"1\",\r\n" + 
			"            \"FIsLockStock\": true,\r\n" + 
			"            \"FIsCycleCounting\": false,\r\n" + 
			"            \"FCountCycle\": \"1\",\r\n" + 
			"            \"FCountDay\": 1,\r\n" + 
			"            \"FIsMustCounting\": false,\r\n" + 
			"            \"FIsBatchManage\": false,\r\n" + 
			"            \"FIsKFPeriod\": false,\r\n" + 
			"            \"FIsExpParToFlot\": false,\r\n" + 
			"            \"FCurrencyId\": {\r\n" + 
			"                \"FNumber\": \"PRE001\"\r\n" + 
			"            },\r\n" + 
			"            \"FIsEnableMinStock\": false,\r\n" + 
			"            \"FIsEnableMaxStock\": false,\r\n" + 
			"            \"FIsEnableSafeStock\": false,\r\n" + 
			"            \"FIsEnableReOrder\": false,\r\n" + 
			"            \"FIsSNManage\": false,\r\n" + 
			"            \"FIsSNPRDTracy\": false,\r\n" + 
			"            \"FSNManageType\": \"1\",\r\n" + 
			"            \"FSNGenerateTime\": \"1\"\r\n" + 
			"        },\r\n" + 
			"        \"SubHeadEntity3\": {\r\n" + 
			"            \"FPurchaseUnitId\": {\r\n" + 
			"                \"FNumber\": \"Pcs\"\r\n" + 
			"            },\r\n" + 
			"            \"FPurchasePriceUnitId\": {\r\n" + 
			"                \"FNumber\": \"Pcs\"\r\n" + 
			"            },\r\n" + 
			"            \"FPurchaseOrgId\": {\r\n" + 
			"                \"FNumber\": \"100\"\r\n" + 
			"            },\r\n" + 
			"            \"FIsQuota\": false,\r\n" + 
			"            \"FQuotaType\": \"1\",\r\n" + 
			"            \"FIsVmiBusiness\": false,\r\n" + 
			"            \"FEnableSL\": false,\r\n" + 
			"            \"FIsPR\": false,\r\n" + 
			"            \"FIsReturnMaterial\": true,\r\n" + 
			"            \"FIsSourceControl\": false,\r\n" + 
			"            \"FPOBillTypeId\": {\r\n" + 
			"                \"FNUMBER\": \"CGSQD01_SYS\"\r\n" + 
			"            },\r\n" + 
			"            \"FPrintCount\": 1\r\n" + 
			"        }\r\n" + 
			
			"    }";
	
	
	private String supplierJsonString="{\r\n" + 
			"        \"FSupplierId\": 0,\r\n" + 
			"        \"FCreateOrgId\": {\r\n" + 
			"            \"FNumber\": \"100\"\r\n" + 
			"        },\r\n" + 
			"        \"FUseOrgId\": {\r\n" + 
			"            \"FNumber\": \"100\"\r\n" + 
			"        },\r\n" + 
			"        \"FName\": \"测试\",\r\n" + 
			"        \"F_KDXF_GRGY\": true,\r\n" + 
			"        \"F_KDXF_GYSX\": \"\",\r\n" + 
			"        \"F_KDXF_DZ\": \"122345\",\r\n" + 
			"        \"F_KDXF_YX\": \"122345@qq.com\",\r\n" + 
			"        \"F_KDXF_DH\": \"150555555\",\r\n" + 
			"        \"F_KDXF_CS1\": {\r\n" + 
			"            \"FNumber\": \"094\"\r\n" + 
			"        },\r\n" + 
			"        \"F_KDXF_Assistant11\": {\r\n" + 
			"            \"FNumber\": \"012\"\r\n" + 
			"        },\r\n" + 
			"        \"F_KDXF_Assistant1\": {\r\n" + 
			"            \"FNumber\": \"001\"\r\n" + 
			"        },\r\n" + 
			"        \"F_KDXF_ZT\": \"34\",\r\n" + 
			"        \"F_KDXF_LCBH\": \"12\",\r\n" + 
			"        \"FBaseInfo\": {\r\n" + 
			"            \"FRegisterFund\": 100000.0,\r\n" + 
			"            \"FSOCIALCRECODE\": \"122222\",\r\n" + 
			"            \"FSupplyClassify\": \"CG\"\r\n" + 
			"        },\r\n" + 
			"        \"FBusinessInfo\": {\r\n" + 
			"           \"FSettleTypeId\": {\r\n" + 
			"                \"FNumber\": \"JSFS01_SYS\"\r\n" + 
			"            },\r\n" + 
			"            \"FVmiBusiness\": false,\r\n" + 
			"            \"FEnableSL\": false"+
			"        },\r\n" + 
			"        \"FFinanceInfo\": {\r\n" + 
			"            \"FPayCurrencyId\": {\r\n" + 
			"                \"FNumber\": \"PRE001\"\r\n" + 
			"            },\r\n" + 
			"            \"FTaxType\": {\r\n" + 
			"                \"FNumber\": \"SFL02_SYS\"\r\n" + 
			"            },\r\n" + 
			"            \"FInvoiceType\": \"1\",\r\n" + 
			"            \"FTaxRateId\": {\r\n" + 
			"                \"FNUMBER\": \"SL02_SYS\"\r\n" + 
			"            }\r\n" + 
			"        },\r\n" + 
			"		\"FBankInfo\": [\r\n" + 
			"            {\r\n" + 
			"                \"FBankCode\": \"1123\",\r\n" + 
			"                \"FBankHolder\": \"23\",\r\n" + 
			"                \"FOpenBankName\": \"中国\",\r\n" + 
			"                \"FCNAPS\": \"11111\",\r\n" + 
			"                \"FBankCurrencyId\": {\r\n" + 
			"                    \"FNumber\": \"PRE001\"\r\n" + 
			"                },\r\n" + 
			"                \"FBankIsDefault\": false\r\n" + 
			"            }\r\n" + 
			"        ]\r\n" + 
			"    }";
	
	

	/**
	 * 获取物料编码申请信息
	 * @param recordId
	 * @return
	 */
	public List<JSONObject> getMaterialList(String recordId){
		 List<JSONObject> resultList=new ArrayList<JSONObject>();
		 Map<String,String> paramsMap=new HashMap<String,String>();
		 paramsMap.put("maintableName","wlbmsqb");
		 String sql="select  b.dlbm, b.zlbm,b.xlbm,b.zileibm ,b.id,a.title ,a.bh,a.createdby,a.sqrq,a.createdorg,a.bmlx, case when nvl(b.zileibm,'-1')!='-1' then b.zileibm else b.xlbm  end as wlfz ,b.zldj,b.wlbm,b.wlmc,b.wlms,b.yclh,b.wlsx,b.wllx,b.wldw,b.wldwbm,b.zxbzs,b.qyphgl,b.sfwgtyj,b.badrj,b.isyfwz ,b.wlcms  "
		 		+ "from wlbmsqb a   left join wlbmsqzb b on a.id=b.fkid where a.id=:recordId " ;
		 Map queryMap=new HashMap();
		 queryMap.put("recordId", recordId);
		 List list=super.findByListNativeQuery(sql, "", queryMap);
		 String FCreateOrgId="";//创建组织
		 String FCreatorId="";//创建人
		 if(list!=null&&list.size()>0) {
			 for(int i=0,size=list.size();i<size;i++) {
				 JSONObject materialJson=JSONObject.parseObject(materialJsonModelString,Feature.OrderedField);
				 Map map=(Map) list.get(i);
				 if("".equals(FCreateOrgId)) {
					 String createdorg=StringUtils.null2String(map.get("createdorg"));
					 Map<String, String> orgCodemap= systemFlowService.getUserOrOrgFNumberById(createdorg, "org",paramsMap);
					 String orgCode=orgCodemap.get("fNumber");
					 //获取所属公司编码
					 FCreateOrgId= systemFlowService.getERPuserOrgId(orgCode);
					 materialJson.put("FCreateOrgId", JSONObject.parseObject("{\"FNumber\":\""+FCreateOrgId+"\"}"));
					 materialJson.put("FUseOrgId", JSONObject.parseObject("{\"FNumber\":\""+FCreateOrgId+"\"}"));
				 }
				 String bid=StringUtils.null2String(map.get("id"));
				 //创建人
				 if("".equals(FCreatorId)) {
					 String createdby=StringUtils.null2String(map.get("createdby"));
					 Map<String, String> userAccountmap= systemFlowService.getUserOrOrgFNumberById(createdby, "user",paramsMap);
					 FCreatorId=userAccountmap.get("fNumber");
					 materialJson.put("FCreatorId", JSONObject.parseObject("{\"FNumber\":\""+FCreatorId+"\"}"));
				 }
				 
				 //标题
				 String title=StringUtils.null2String(map.get("title"));
				 materialJson.put("F_KDXF_ZT", title);
				//编码类型
				 String bmlx=StringUtils.null2String(map.get("bmlx"));
				 materialJson.put("F_KDXF_BMLX", bmlx);
				   
				 //编号
				 String bh=StringUtils.null2String(map.get("bh"));
				 materialJson.put("F_KDXF_LCBH", bh);
				 //申请日期
				 String sqrq=StringUtils.null2String(map.get("sqrq"));
				 materialJson.put("FCreateDate", sqrq);
				 //物料分组
				 String dlbm = StringUtils.null2String(map.get("dlbm"));
				 String zlbm = StringUtils.null2String(map.get("zlbm"));
				 String xlbm = StringUtils.null2String(map.get("xlbm"));
				 String zileibm = StringUtils.null2String(map.get("zileibm"));

				 String wlfz="";
				 //重置物料分组
				 if(!"".equals(zileibm)){
					 wlfz=zileibm+"";
				 }else if(!"".equals(xlbm)){
					 wlfz=xlbm+"";
				 }else if(!"".equals(zlbm)){
					 wlfz=zlbm+"";
				 }else if(!"".equals(dlbm)){
					 wlfz=dlbm+"";
				 }else{
					 wlfz=StringUtils.null2String(map.get("wlfz"));
				 }

				 materialJson.put("FMaterialGroup", JSONObject.parseObject("{\"FNumber\":\""+wlfz+"\"}"));
				 //质量等级
				 String zldj=StringUtils.null2String(map.get("zldj"));
				 materialJson.put("F_KDXF_ZLDJ", zldj);
				 //物料编码
				 String wlbm=StringUtils.null2String(map.get("wlbm"));
				// materialJson.put("F_KDXF_ZLDJ", zldj);
				 //物料名称
				 String wlmc=StringUtils.null2String(map.get("wlmc"));
				 materialJson.put("FName", wlmc);
				 //物料描述  ====erp 规格型号
				 String wlms=StringUtils.null2String(map.get("wlms"));
				 materialJson.put("FSpecification", wlms);
				 //原厂料号
				 String yclh=StringUtils.null2String(map.get("yclh"));
				 materialJson.put("F_KDXF_YCLH", yclh);
				 //物料属性
				 String wlsx=StringUtils.null2String(map.get("wlsx"));
				 JSONObject SubHeadEntity=materialJson.getJSONObject("SubHeadEntity");
				 SubHeadEntity.put("FErpClsID", wlsx);
				 //物料类型
				 String wllx=StringUtils.null2String(map.get("wllx"));
				// SubHeadEntity.put("FCategoryID", JSONObject.parseObject("{\"FNumber\":\""+wllx+"\"}"));

				 //物料单位
				 String wldw=StringUtils.null2String(map.get("wldwbm"));
				 SubHeadEntity.put("FBaseUnitId", JSONObject.parseObject("{\"FNumber\":\""+wldw+"\"}"));

				 //jinxh 物料长描述
				 String wlcms=StringUtils.null2String(map.get("wlcms"));
				 materialJson.put("FDescription", wlcms);
				 
				 //启用批号管理
				 String qyphgl=StringUtils.null2String(map.get("qyphgl"));
				 JSONObject SubHeadEntity1=materialJson.getJSONObject("SubHeadEntity1");
				 SubHeadEntity1.put("FStoreUnitID", JSONObject.parseObject("{\"FNumber\":\""+wldw+"\"}"));
				 SubHeadEntity1.put("FIsBatchManage", qyphgl);
				  
				 //是否外购通用件
				 String sfwgtyj=StringUtils.null2String(map.get("sfwgtyj"));
				 materialJson.put("F_KDXF_SFWG", sfwgtyj);
				//软著
				 String badrj=StringUtils.null2String(map.get("badrj"));
				 materialJson.put("F_KDXF_RZ", badrj);
				 //最小包装数
				 String zxbzs=StringUtils.null2String(map.get("zxbzs"));
				 JSONObject SubHeadEntity3=materialJson.getJSONObject("SubHeadEntity3");
				 SubHeadEntity3.put("FMinPackCount", zxbzs);
				 SubHeadEntity3.put("FPurchaseUnitId", JSONObject.parseObject("{\"FNumber\":\""+wldw+"\"}"));
				 SubHeadEntity3.put("FPurchasePriceUnitId", JSONObject.parseObject("{\"FNumber\":\""+wldw+"\"}"));
				 SubHeadEntity3.put("FPurchaseOrgId", JSONObject.parseObject("{\"FNumber\":\""+FCreateOrgId+"\"}"));
				 materialJson.put("SubHeadEntity", SubHeadEntity);
				 materialJson.put("SubHeadEntity1", SubHeadEntity1);
				 materialJson.put("SubHeadEntity3", SubHeadEntity3);

				 //增加是否研发物资
				 String isyfwz=StringUtils.null2String(map.get("isyfwz"));
				 materialJson.put("F_KDXF_SFYFWZ", isyfwz);

				KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
							"true", materialJson,"wlbmsqzb",bid);
				 resultList.add((JSONObject) JSONObject.toJSON(appData));
				 
			 }
		 }
		return resultList;
	}
	
	
	/**
	 * 获取供应商资料
	 * @param recordId
	 * @return
	 */
	public List<JSONObject> getSupplierList(String recordId){
		 List<JSONObject> resultList=new ArrayList<JSONObject>();
			
		 Map<String,String> paramsMap=new HashMap<String,String>();
		 paramsMap.put("maintableName","gysxzbgsqb");
		 String sql="select  a.title ,a.bh,a.createdby,a.sqrq,a.createdorg, gsgs,sfgrgys ,gysmc,tyshxydm,gjbm,sfbm,csbm,dzjdmph,dh,yx,khyh,lhh,bizhong,yhzh,jsfs,zczb,qyxz,dykh,xgfj,tgdcphfwnr \r\n" + 
		 		"from gysxzbgsqb a   where gysxzbg='新增' and a.id=:recordId " ;
		 Map queryMap=new HashMap();
		 queryMap.put("recordId", recordId);
		 List list=super.findByListNativeQuery(sql, "", queryMap);
		 String FCreateOrgId="";//创建组织
		 String FCreatorId="";//创建人
		 if(list!=null&&list.size()>0) {
			 for(int i=0,size=list.size();i<size;i++) {
				 JSONObject supplier=JSONObject.parseObject(supplierJsonString,Feature.OrderedField);
				 Map map=(Map) list.get(i);
				 if("".equals(FCreateOrgId)) {
					 String createdorg=StringUtils.null2String(map.get("createdorg"));
					 Map<String, String> orgCodemap= systemFlowService.getUserOrOrgFNumberById(createdorg, "org",paramsMap);
					 String orgCode=orgCodemap.get("fNumber");
					 //获取所属公司编码
					 FCreateOrgId= systemFlowService.getERPuserOrgId(orgCode);
					 supplier.put("FCreateOrgId", JSONObject.parseObject("{\"FNumber\":\""+FCreateOrgId+"\"}"));
					
				 }
				 //创建人
				 if("".equals(FCreatorId)) {
					 String createdby=StringUtils.null2String(map.get("createdby"));
					 Map<String, String> userAccountmap= systemFlowService.getUserOrOrgFNumberById(createdby, "user",paramsMap);
					 FCreatorId=userAccountmap.get("fNumber");
					 supplier.put("FCreatorId", JSONObject.parseObject("{\"FNumber\":\""+FCreatorId+"\"}"));
				 }
				 //标题
				 String title=StringUtils.null2String(map.get("title"));
				 supplier.put("F_KDXF_ZT", title);
				 //编号
				 String bh=StringUtils.null2String(map.get("bh"));
				 supplier.put("F_KDXF_LCBH", bh);
				 //申请日期
				 String sqrq=StringUtils.null2String(map.get("sqrq"));
				 supplier.put("FCreateDate", sqrq);
				 //所属公司
				 String gsgs=StringUtils.null2String(map.get("gsgs"));
				 Map<String, String> orgCodeMap= systemFlowService.getUserOrOrgFNumberById(gsgs, "org",paramsMap);
				 String orgCode=orgCodeMap.get("fNumber");
				 //获取所属公司编码
				 String ssgsf=systemFlowService.getERPuserOrgId(orgCode);
				 supplier.put("FUseOrgId", JSONObject.parseObject("{\"FNumber\":\""+ssgsf+"\"}"));
				 //是否个人供应商
				 String sfgrgys=StringUtils.null2String(map.get("sfgrgys"));
				 supplier.put("F_KDXF_GRGY", sfgrgys);
				 //供应商全称
				 String gysmc=StringUtils.null2String(map.get("gysmc"));
				 supplier.put("FName", gysmc);
				
				 // 基本信息 FBaseInfo 
				 Map<String,Object> FBaseInfo =new HashMap<String,Object>();
				 //统一社会信用代码
				 String tyshxydm=StringUtils.null2String(map.get("tyshxydm"));
				 String zczb=StringUtils.null2String(map.get("zczb"));
					 
				 FBaseInfo.put("FSOCIALCRECODE", tyshxydm);
				 FBaseInfo.put("FRegisterFund", zczb);
				 FBaseInfo.put("FSupplyClassify", "CG");
				 supplier.put("FBaseInfo", JSONObject.toJSON(FBaseInfo));
				 
				 //国家
				 String gj=StringUtils.null2String(map.get("gjbm"));
				 supplier.put("F_KDXF_Assistant1",  JSONObject.parseObject("{\"FNumber\":\""+gj+"\"}"));
				 //省份
				 String sf=StringUtils.null2String(map.get("sfbm"));
				 supplier.put("F_KDXF_Assistant11",  JSONObject.parseObject("{\"FNumber\":\""+sf+"\"}"));
				 //城市
				 String cs=StringUtils.null2String(map.get("csbm"));
				 supplier.put("F_KDXF_CS1",  JSONObject.parseObject("{\"FNumber\":\""+cs+"\"}"));
				 //地址
				 String dzjdmph=StringUtils.null2String(map.get("dzjdmph"));
				 supplier.put("F_KDXF_DZ", dzjdmph);
				 //电话
				 String dh=StringUtils.null2String(map.get("dh"));
				 supplier.put("F_KDXF_DH", dh);
				 //邮箱
				 String yx=StringUtils.null2String(map.get("yx"));
				 supplier.put("F_KDXF_YX", yx);
				 //供应商性质
				 String qyxz=StringUtils.null2String(map.get("qyxz"));
				 supplier.put("F_KDXF_GYSX", qyxz);
				 // 提供的产品或服务内容
				 String tgdcphfwnr=StringUtils.null2String(map.get("tgdcphfwnr"));
				 supplier.put("F_KDXF_TD", tgdcphfwnr);
				 // 银行信息 FBankInfo 
				 Map<String,Object> FBankInfo =new HashMap<String,Object>();
				 String FOpenBankName=StringUtils.null2String(map.get("khyh"));
				 String FCNAPS =StringUtils.null2String(map.get("lhh"));//联行号yx
				 String FBankCode=StringUtils.null2String(map.get("yhzh"));
				 String FBankCurrencyId=StringUtils.null2String(map.get("bizhong"));
				 
				 
				 String fieldValue="";
				 String fj=StringUtils.null2String(map.get("xgfj"));
				 if(org.apache.commons.lang3.StringUtils.isNotEmpty(fj)) {
					 fieldValue=fj;
				 }
				 if(org.apache.commons.lang3.StringUtils.isNotEmpty(fieldValue)) {
					 fieldValue="'"+fieldValue.replace("|", "','")+"'";
					 fileList=systemFlowService.getFileListByIds(fieldValue);
				 }
				
				 FBankInfo.put("FBankCode", FBankCode);
				 FBankInfo.put("FOpenBankName", FOpenBankName);
				 FBankInfo.put("FCNAPS", FCNAPS);
				 FBankInfo.put("FBankCurrencyId", JSONObject.parseObject("{\"FNumber\":\""+FBankCurrencyId+"\"}"));
				 FBankInfo.put("FBankIsDefault", "false");
				 JSONArray jsonArr=new JSONArray();
				 jsonArr.add(JSONObject.toJSON(FBankInfo));
				 supplier.put("FBankInfo", jsonArr);
				 //商务信息
				 Map<String,Object> FBusinessInfo =new HashMap<String,Object>(); 
				 String jsfs=StringUtils.null2String(map.get("jsfs"));//结算方式
				 FBusinessInfo.put("FSettleTypeId", JSONObject.parseObject("{\"FNumber\":\""+jsfs+"\"}"));
				 FBusinessInfo.put("FVmiBusiness", "false");
				 FBusinessInfo.put("FEnableSL", "false");
				 supplier.put("FBusinessInfo", JSONObject.toJSON(FBusinessInfo));
				KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
							"true", supplier);
				 resultList.add((JSONObject) JSONObject.toJSON(appData));
				 
			 }
		 }
		return resultList;
	}
	
	
	
}
