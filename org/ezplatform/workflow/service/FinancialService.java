package org.ezplatform.workflow.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

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

/**
 * 财务类报销流程
 * @author WEI
 *
 */
@Service(value="financialService")
public class FinancialService extends BaseService<FlowBuesinessFail, String>{
	@Autowired
	@Qualifier("flowBuesinessFailDao")
	private FlowBuesinessFailDao flowBuesinessFailDao;


	@Autowired
	private SystemFlowService systemFlowService;

	@Autowired
	private JdbcTemplate jdbcTemplate;


	@Override
	protected JpaBaseDao<FlowBuesinessFail, String> getEntityDao() {
		// TODO Auto-generated method stub
		return this.flowBuesinessFailDao;
	}

	//附件列表
	private List<FlowBuesinessAttach> fileList;

	public List<FlowBuesinessAttach> getFileList() {
		return fileList;
	}
	//erp单据编号
	private String erpdjbh;

	public String getErpdjbh() {
		return erpdjbh;
	}

	private String amountJsonString=" {\r\n" +
			"        \"FID\": 0,\r\n" +
			"        \"FBillTypeID\": {\r\n" +
			"            \"FNUMBER\": \"QTYFD02_SYS\"\r\n" +
			"        },\r\n" +
			"        \"FDATE\": \"2020-09-15 00:00:00\",\r\n" +
			"        \"FENDDATE_H\": \"2020-09-15 00:00:00\",\r\n" +
			"        \"FISINIT\": false,\r\n" +
			"        \"FCONTACTUNITTYPE\": \"BD_Empinfo\",\r\n" +
			"        \"FCONTACTUNIT\": {\r\n" +
			"            \"FNumber\": \"20060035\"\r\n" +
			"        },\r\n" +
			"        \"FCURRENCYID\": {\r\n" +
			"            \"FNumber\": \"PRE001\"\r\n" +
			"        },\r\n" +
			"        \"FTOTALAMOUNTFOR_H\": 215.0,\r\n" +
			"        \"FNOTSETTLEAMOUNTFOR\": 215.0,\r\n" +
			"        \"FDEPARTMENTID\": {\r\n" +
			"            \"FNumber\": \"BM000002\"\r\n" +
			"        },\r\n" +
			"        \"FSETTLEORGID\": {\r\n" +
			"            \"FNumber\": \"100\"\r\n" +
			"        },\r\n" +
			"        \"FPURCHASEORGID\": {\r\n" +
			"            \"FNumber\": \"100\"\r\n" +
			"        },\r\n" +
			"        \"FPAYORGID\": {\r\n" +
			"            \"FNumber\": \"100\"\r\n" +
			"        },\r\n" +
			"        \"FSettleTypeID\": {\r\n" +
			"            \"FNumber\": \"JSFS01_SYS\"\r\n" +
			"        },\r\n" +
			"        \"FNOTAXAMOUNT\": 200.0,\r\n" +
			"        \"FTAXAMOUNT\": 15.0,\r\n" +
			"        \"FACCNTTIMEJUDGETIME\": \"2020-09-15 00:00:00\",\r\n" +
			"        \"FCancelStatus\": \"A\",\r\n" +
			"        \"FBUSINESSTYPE\": \"T\",\r\n" +
			"        \"F_KDXF_ZT\": \"OA主题\",\r\n" +
			"        \"F_KDXF_LCBH\": \"OA流程编号\",\r\n" +
			"        \"F_KDXF_SQR\": {\r\n" +
			"            \"FSTAFFNUMBER\": \"20060035\"\r\n" +
			"        },\r\n" +
			"        \"F_KDXF_TWLB\": \"项目费用\",\r\n" +
			"        \"F_KDXF_SFFT1\": true,\r\n" +
			"        \"F_KDXF_DLBX1\": true,\r\n" +
			"        \"F_KDXF_CJKLC\": \"\",\r\n" +
			"        \"FEntity\": [\r\n" +
			"            {\r\n" +
			"                \"F_KDXF_XMLB\": \"KDXF_XMLX\",\r\n" +
			"                \"F_KDXF_XMMC1\": {\r\n" +
			"                    \"FNumber\": \"D2-20000001\"\r\n" +
			"                },\r\n" +
			"                \"FCOSTID\": {\r\n" +
			"                    \"FNumber\": \"FYXM10_SYS\"\r\n" +
			"                },\r\n" +
			"                \"FCOSTDEPARTMENTID\": {\r\n" +
			"                    \"FNumber\": \"BM000001\"\r\n" +
			"                },\r\n" +
			"                \"F_KDXF_FYLX\": {\r\n" +
			"                    \"FNUMBER\": \"007\"\r\n" +
			"                },\r\n" +
			"                \"FEntryTaxRate\": 7.0,\r\n" +
			"                \"FNOTAXAMOUNTFOR\": 100.0,\r\n" +
			"                \"FTAXAMOUNTFOR\": 7.0,\r\n" +
			"                \"FTOTALAMOUNTFOR\": 107.0,\r\n" +
			"                \"FNOTSETTLEAMOUNTFOR_D\": 107.0,\r\n" +
			"                \"FNOTAXAMOUNT_D\": 100.0,\r\n" +
			"                \"FTAXAMOUNT_D\": 7.0,\r\n" +
			"                \"F_KDXF_KSRQ\": \"2020-09-15 00:00:00\",\r\n" +
			"                \"F_KDXF_JSRQ\": \"2020-09-25 00:00:00\",\r\n" +
			"                \"FCREATEINVOICE\": false\r\n" +
			"            },\r\n" +
			"        ]\r\n" +
			"    }";

	String jkflowJsonString1=" {\r\n" +
			"        \"FID\": 0,\r\n" +
			"        \"FBillTypeID\": {\r\n" +
			"            \"FNUMBER\": \"FKDLX04_SYS\"\r\n" +
			"        },\r\n" +
			"        \"FDATE\": \"2020-09-22 00:00:00\",\r\n" +
			"        \"FCONTACTUNITTYPE\": \"BD_Empinfo\",\r\n" +
			"        \"FCONTACTUNIT\": {\r\n" +
			"            \"FNumber\": \"20110362\"\r\n" +
			"        },\r\n" +
			"        \"FRECTUNITTYPE\": \"BD_Empinfo\",\r\n" +
			"        \"FRECTUNIT\": {\r\n" +
			"            \"FNumber\": \"20110362\"\r\n" +
			"        },\r\n" +
			"        \"FDEPARTMENTID\": {\r\n" +
			"            \"FNumber\": \"BM000002\"\r\n" +
			"        },\r\n" +
			"        \"FISINIT\": false,\r\n" +
			"        \"FCURRENCYID\": {\r\n" +
			"            \"FNumber\": \"PRE001\"\r\n" +
			"        },\r\n" +
			"        \"FEXCHANGERATE\": 1.0,\r\n" +
			"        \"FSETTLERATE\": 1.0,\r\n" +
			"        \"FSETTLEORGID\": {\r\n" +
			"            \"FNumber\": \"100\"\r\n" +
			"        },\r\n" +
			"        \"FPURCHASEORGID\": {\r\n" +
			"            \"FNumber\": \"100\"\r\n" +
			"        },\r\n" +
			"        \"FDOCUMENTSTATUS\": \"Z\",\r\n" +
			"        \"FBUSINESSTYPE\": \"3\",\r\n" +
			"        \"F_KDXF_ZT\": \"OA主题\",\r\n" +
			"        \"F_KDXF_LCBH\": \"OA流程编号\",\r\n" +
			"        \"F_KDXF_SQR\": {\r\n" +
			"            \"FSTAFFNUMBER\": \"20060035\"\r\n" +
			"        },\r\n" +
			"        \"F_KDXF_SSBM\": {\r\n" +
			"            \"FNUMBER\": \"BM000001\"\r\n" +
			"        },\r\n" +
			"        \"F_KDXF_SFJK\": true,\r\n" +
			"        \"FCancelStatus\": \"A\",\r\n" +
			"        \"FPAYORGID\": {\r\n" +
			"            \"FNumber\": \"100\"\r\n" +
			"        },\r\n" +
			"        \"FISSAMEORG\": true,\r\n" +
			"        \"FIsCredit\": false,\r\n" +
			"        \"FSETTLECUR\": {\r\n" +
			"            \"FNUMBER\": \"PRE001\"\r\n" +
			"        },\r\n" +
			"        \"FIsWriteOff\": false,\r\n" +
			"        \"FREALPAY\": false,\r\n" +
			"        \"FREMARK\": \"备注\",\r\n" +
			"        \"FISCARRYRATE\": false,\r\n" +
			"        \"FSETTLEMAINBOOKID\": {\r\n" +
			"            \"FNUMBER\": \"PRE001\"\r\n" +
			"        },\r\n" +
			"        \"F_KDXF_XMLB1\": \"KDXF_XMLX\",\r\n" +
			"        \"F_KDXF_XMMC1\": {\r\n" +
			"            \"FNumber\": \"D2-20000001\"\r\n" +
			"        }\r\n" +
			"    }";



	String jkflowJsonString="{\n" +
			"        \"FID\": 0,\n" +
			"        \"FBillTypeID\": {\n" +
			"            \"FNUMBER\": \"FKDLX04_SYS\"\n" +
			"        },\n" +
			"        \"FDATE\": \"2021-01-18 00:00:00\",\n" +
			"        \"FCONTACTUNITTYPE\": \"BD_Supplier\",\n" +
			"        \"FCONTACTUNIT\": {\n" +
			"            \"FNumber\": \"VEN00003\"\n" +
			"        },\n" +
			"        \"FRECTUNITTYPE\": \"BD_Supplier\",\n" +
			"        \"FRECTUNIT\": {\n" +
			"            \"FNumber\": \"VEN00003\"\n" +
			"        },\n" +
			"        \"FISINIT\": false,\n" +
			"        \"FCURRENCYID\": {\n" +
			"            \"FNumber\": \"PRE001\"\n" +
			"        },\n" +
			"        \"FEXCHANGERATE\": 1.0,\n" +
			"        \"FSETTLERATE\": 1.0,\n" +
			"        \"FSETTLEORGID\": {\n" +
			"            \"FNumber\": \"100\"\n" +
			"        },\n" +
			"        \"FPURCHASEORGID\": {\n" +
			"            \"FNumber\": \"100\"\n" +
			"        },\n" +
			"        \"FDOCUMENTSTATUS\": \"Z\",\n" +
			"        \"FBUSINESSTYPE\": \"3\",\n" +
			"        \"F_KDXF_SFJK\": false,\n" +
			"        \"FCancelStatus\": \"A\",\n" +
			"        \"FPAYORGID\": {\n" +
			"            \"FNumber\": \"100\"\n" +
			"        },\n" +
			"        \"FISSAMEORG\": true,\n" +
			"        \"FIsCredit\": false,\n" +
			"        \"FSETTLECUR\": {\n" +
			"            \"FNUMBER\": \"PRE001\"\n" +
			"        },\n" +
			"        \"FIsWriteOff\": false,\n" +
			"        \"FREALPAY\": false,\n" +
			"        \"FISCARRYRATE\": false,\n" +
			"        \"FSETTLEMAINBOOKID\": {\n" +
			"            \"FNUMBER\": \"PRE001\"\n" +
			"        },\n" +
			"        \"FMoreReceive\": false\n" +
			"    }";



	/**
	 * 借款流程travel
	 * @param recordId
	 * @return
	 */
	public List<JSONObject> getJKFlowList(String recordId){
		erpdjbh="";
		List<JSONObject> resultList=new ArrayList<JSONObject>();
		Map<String,String> paramsMap=new HashMap<String,String>();
		paramsMap.put("maintableName","jk");
		String sqlmain="select  a.wldwlx,a.wldwlxbm,a.wldwbm,a.title ,a.lcbh,a.createdby,a.sqrq,a.createdorg,b.scopevalue ,a.dljk ,a.ywlx,a.jklx,a.jkr,a.gsgs,a.xmbm,a.xmlb,a.xmmc,a.ssbm,a.bizhong,a.jsfsbm,a.bz,a.fj,a.erpdjbh,d.f_kdxf_xmjd  from jk a  left join jk_scope  b on a.bdljkr=b.fielddatavalue  "

				+ " left join (select  ROW_NUMBER() OVER(PARTITION BY c.fxmnumber ORDER BY c.f_kdxf_xmjd asc) rn , c.fxmnumber, c.F_KDXF_XMJD,c.F_KDXF_XMJDNAME,c.FBillStatus from   xfsmzjk.T_XM_XMJHCBTRJZ@toerp c where c.FBillStatus ='A' )  d on a.xmbm=d.fxmnumber and d.rn=1  "

				+ " where a.id =:recordId";
		//String sql4="select fylxbm,yskm,yskmbm,sqje,bz from  jksqzb  where  fkid=:recordId ";
		String sql4="select yskmbm,bz,sum(sqje) as sqje from  jksqzb  where  fkid=:recordId  group by yskmbm,bz ";
		Map queryMap=new HashMap();
		queryMap.put("recordId", recordId);
		List list=super.findByListNativeQuery(sqlmain, "", queryMap);
		//员工报销json串
		JSONObject jkJson=JSONObject.parseObject(jkflowJsonString,Feature.OrderedField);
		if(list!=null&&list.size()>0) {
			Map map=(Map) list.get(0);
			String wldwlx=StringUtils.null2String(map.get("wldwlx"));//往来单位类型
			String wldwlxbm=StringUtils.null2String(map.get("wldwlxbm"));//往来单位类型编码
			String wldwbm=StringUtils.null2String(map.get("wldwbm"));//往来单位编码
			String title=StringUtils.null2String(map.get("title"));//流程标题
			String bh=StringUtils.null2String(map.get("lcbh"));//流程编号
			String createdby=StringUtils.null2String(map.get("createdby"));//申请人
			String createdorg=StringUtils.null2String(map.get("createdorg"));//申请人组部门
			String dljk=StringUtils.null2String(map.get("dljk"));//代理借款
			String scopevalue=StringUtils.null2String(map.get("scopevalue"));//被代理人

			String sqrq=StringUtils.null2String(map.get("sqrq"));//申请日期
			String ywlx=StringUtils.null2String(map.get("ywlx"));//业务类型
			String jklx=StringUtils.null2String(map.get("jklx"));//借款类型
			String gsgs=StringUtils.null2String(map.get("gsgs"));//归属公司
			String xmbm=StringUtils.null2String(map.get("xmbm"));//项目编号
			String xmlb=StringUtils.null2String(map.get("xmlb"));//项目类别
			String f_kdxf_xmjd=StringUtils.null2String(map.get("f_kdxf_xmjd"));//项目阶段
			String xmmc=StringUtils.null2String(map.get("xmmc"));//项目名称
			String ssbm=StringUtils.null2String(map.get("ssbm"));//项目所属部门
			erpdjbh=StringUtils.null2String(map.get("erpdjbh"));//erp单据编号
			String jsfsbm=StringUtils.null2String(map.get("jsfsbm"));//结算方式
			String bizhong=StringUtils.null2String(map.get("bizhong"));//币种
			String bzmain=StringUtils.null2String(map.get("bz"));//

			//借款申请替换日期
			jkJson.put("FDATE",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			//jkJson.put("FDATE", sqrq);//业务日期
			jkJson.put("FREMARK", bzmain);//备注


			//获取申请人报销 FDEPARTMENTID
			Map<String, String> orgCodemap= systemFlowService.getUserOrOrgFNumberById(createdorg, "org",paramsMap);
			String orgCode=orgCodemap.get("fNumber");
			//获取申请部门
			jkJson.put("FDEPARTMENTID", JSONObject.parseObject("{\"FNumber\":\""+orgCode+"\"}"));
			if("true".equals(dljk)) {
				createdby=scopevalue;
			}
			Map<String, String> userAccountmap= systemFlowService.getUserOrOrgFNumberById(createdby, "user",paramsMap);
			String  FCreatorId=userAccountmap.get("fNumber");
			jkJson.put("F_KDXF_SQR", JSONObject.parseObject("{\"FSTAFFNUMBER\":\""+FCreatorId+"\"}"));//申请人


			jkJson.put("F_KDXF_JKLX",jklx);
			if("4".equals(jklx)) {
				//个人借支
				jkJson.put("FCONTACTUNITTYPE", "BD_Empinfo");//往来单位类型
				jkJson.put("FRECTUNITTYPE", "BD_Empinfo");//往来单位类型
				jkJson.put("FCONTACTUNIT", JSONObject.parseObject("{\"FNumber\":\""+FCreatorId+"\"}"));//往来单位
				jkJson.put("FRECTUNIT", JSONObject.parseObject("{\"FNumber\":\""+FCreatorId+"\"}"));//往来单位
			}else {
				jkJson.put("FCONTACTUNITTYPE", wldwlxbm);//往来单位类型
				jkJson.put("FCONTACTUNIT", JSONObject.parseObject("{\"FNumber\":\""+wldwbm+"\"}"));//往来单位
				jkJson.put("FRECTUNITTYPE", wldwlxbm);//往来单位类型
				jkJson.put("FRECTUNIT", JSONObject.parseObject("{\"FNumber\":\""+wldwbm+"\"}"));//往来单位

			}

			String fieldValue="";
			String fj=StringUtils.null2String(map.get("fj"));
			if(org.apache.commons.lang3.StringUtils.isNotEmpty(fj)) {
				fieldValue=fj;
			}
			if(org.apache.commons.lang3.StringUtils.isNotEmpty(fieldValue)) {
				fieldValue="'"+fieldValue.replace("|", "','")+"'";
				fileList=systemFlowService.getFileListByIds(fieldValue);
			}

			jkJson.put("FCURRENCYID", JSONObject.parseObject("{\"FNumber\":\""+bizhong+"\"}"));//币种
			jkJson.put("FSETTLECUR", JSONObject.parseObject("{\"FNumber\":\""+bizhong+"\"}"));//结算币别
			jkJson.put("FSETTLEMAINBOOKID", JSONObject.parseObject("{\"FNumber\":\""+bizhong+"\"}"));//结算币别

			Map<String, String> orgCodeMap= systemFlowService.getUserOrOrgFNumberById(gsgs, "org",paramsMap);
			orgCode=orgCodeMap.get("fNumber");
			//获取所属公司编码
			String ssgsf=systemFlowService.getERPuserOrgId(orgCode);
			jkJson.put("FSETTLEORGID", JSONObject.parseObject("{\"FNumber\":\""+ssgsf+"\"}"));//使用组织
			jkJson.put("FPAYORGID", JSONObject.parseObject("{\"FNumber\":\""+ssgsf+"\"}"));//付款组织
			jkJson.put("FPURCHASEORGID", JSONObject.parseObject("{\"FNumber\":\""+ssgsf+"\"}"));//付款组织
			jkJson.put("FBUSINESSTYPE","3");//业务类型 3
			/* //项目所属部门
			 Map<String, String> orgCodemap1= systemFlowService.getUserOrOrgFNumberById(ssbm, "org",paramsMap);
			 String F_KDXF_SSBM=orgCodemap1.get("fNumber");
			 jkJson.put("F_KDXF_SSBM", JSONObject.parseObject("{\"FNumber\":\""+F_KDXF_SSBM+"\"}"));*/


			jkJson.put("F_KDXF_XMLB1", xmlb);  // 项目类别编码
			jkJson.put("F_KDXF_XMMC1",JSONObject.parseObject("{\"FNumber\":\""+xmbm+"\"}"));  // 项目编码

			jkJson.put("F_KDXF_ZT", title);
			jkJson.put("F_KDXF_LCBH", bh);

			jkJson.put("F_KDXF_SFJK", true);//是否借款

			//获取通用子表

			//多余封装json  FEntity  明细
			JSONArray FEntity=new JSONArray();
			JSONObject jsonEntity=new JSONObject(true);
			JSONArray FPAYBILLENTRY = new JSONArray();

			List list1=super.findByListNativeQuery(sql4, "", queryMap);
			if(list1!=null&&list1.size()>0) {
				for(int i=0,size=list1.size();i<size;i++) {
					Map map1=(Map) list1.get(i);

					String yskmbm=StringUtils.null2String(map1.get("yskmbm"));//预算科目编码
					String sqje=StringUtils.null2String(map1.get("sqje"));//申请金额
					String bz=StringUtils.null2String(map1.get("bz"));//备注

					//设置实体数组

					if("KDXF_SJXM1".equals(xmlb)){

						//info data 明细
						jsonEntity.put("F_KDXF_XMLB",xmlb);
						jsonEntity.put("F_KDXF_XMMC1",JSONObject.parseObject("{\"FNumber\":\""+xmbm+"\"}"));
						jsonEntity.put("FCOSTID",JSONObject.parseObject("{\"FNumber\":\""+yskmbm+"\"}"));
						jsonEntity.put("FCOSTDEPARTMENTID", JSONObject.parseObject("{\"FNumber\":\""+orgCode+"\"}"));
						jsonEntity.put("FCREATEINVOICE",false);
						jsonEntity.put("FTOTALAMOUNTFOR",sqje);//总金额

						FEntity.add(jsonEntity);

						jkJson.put("FEntity", FEntity);
					}else{
						JSONObject json = new JSONObject(true);
						json.put("FSETTLETYPEID", JSONObject.parseObject("{\"FNumber\":\"" + jsfsbm + "\"}"));
						json.put("F_KDXF_XMJD", JSONObject.parseObject("{\"FNumber\":\"" + f_kdxf_xmjd + "\"}"));

						json.put("FPURPOSEID", JSONObject.parseObject("{\"FNumber\":\"SFKYT23_SYS\"}"));
						json.put("FACCOUNTID", JSONObject.parseObject("{\"FNumber\":\"551902862210501\"}"));
						//
						json.put("FPAYTOTALAMOUNTFOR", sqje);
						json.put("FCOMMENT", bz);


						FPAYBILLENTRY.add(json);

						jkJson.put("FPAYBILLENTRY", FPAYBILLENTRY);
					}
				}

			}

			System.out.println("jkJson==>>>jkJson============>>>>"+jkJson);
			KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
					"true", jkJson,"jk",recordId);
			resultList.add((JSONObject) JSONObject.toJSON(appData));
		}

		return resultList;
	}


	/**
	 * 人力行政类报销
	 * @param recordId
	 * @return
	 */
	public List<JSONObject> getRLXZBXList(String recordId,String tableName){
		erpdjbh="";
		List<JSONObject> resultList=new ArrayList<JSONObject>();

		String sqlmain="select  a.wldwlx,a.wldwlxbm,a.wldwbm,a.title ,a.bh,a.createdby,a.sqrq,a.createdorg,a.bxr,a.dlbx,b.scopevalue,a.ywlx,a.sfdxmbx,a.gsgs,a.xmbm,a.xmlb,a.xmmc,a.ssbm,a.ssbmbm,a.xmjl,a.fylb,a.fj,a.bz,a.jsfsbm,a.cwhdjehj,a.fxje,a.czje,a.cjklc,a.cjklx,a.erpdjbh,d.f_kdxf_xmjd from rlxzlbx a left join rlxzlbx_scope  b on a.dlbxr=b.fielddatavalue "
				+ "left join (select  ROW_NUMBER() OVER(PARTITION BY c.fxmnumber ORDER BY c.f_kdxf_xmjd asc) rn , c.fxmnumber, c.F_KDXF_XMJD,c.F_KDXF_XMJDNAME,c.FBillStatus from   xfsmzjk.T_XM_XMJHCBTRJZ@toerp c where c.FBillStatus ='A' )  d on a.xmbm=d.fxmnumber and d.rn=1 "
				+ "  where a.id =:recordId";
		String sql4="select xmlb,fylxbm,yskm,yskmbm,sl,xfyrq,scopevalue,fysm,xmbm,xmmc,cpx,ssbm,ssbmbm,ssbmfzr,fplx,d.f_kdxf_xmjd,sum(bxje) bxje ,sum(bhsje) bhsje ,sum(zzsjxse) zzsjxse,sum(hdbxje) hdbxje ,sum(hdbhsje) hdbhsje ,sum(hdzzsjxse) hdzzsjxse from  rlxzlbxtyzb left join rlxzlbx_scope on cyr=fielddatavalue "
				+ "left join (select  ROW_NUMBER() OVER(PARTITION BY c.fxmnumber ORDER BY c.f_kdxf_xmjd asc) rn , c.fxmnumber, c.F_KDXF_XMJD,c.F_KDXF_XMJDNAME,c.FBillStatus from   xfsmzjk.T_XM_XMJHCBTRJZ@toerp c where c.FBillStatus ='A' )  d on xmbm=d.fxmnumber and d.rn=1 "
				+ "  where fkid=:recordId and hdbxje>0 group by xmlb,fylxbm,yskm,yskmbm,xfyrq,scopevalue,fysm,sl,xmbm,xmmc,cpx,ssbm,ssbmbm,ssbmfzr,fplx,d.f_kdxf_xmjd";
		Map<String,String> paramsMap=new HashMap<String,String>();
		paramsMap.put("maintableName","rlxzlbx");
		if("dgcglbx".equals(tableName)) {
			sqlmain="select  a.wldwlx,a.wldwlxbm,a.wldwbm,a.title ,a.bh,a.createdby,a.sqrq,a.createdorg,a.bxr,a.dlbx,b.scopevalue,a.ywlx,a.sfdxmbx,a.gsgs,a.xmbm,a.xmlb,a.xmmc,a.xmjl,a.fylb,a.fj,a.bz,a.jsfsbm ,a.cwhdjehj,a.fxje,a.czje,a.cjklc,a.cjklx,a.erpdjbh,d.f_kdxf_xmjd,a.ssbmbm from dgcglbx a left join dgcglbx_scope  b on a.dlbxr=b.fielddatavalue "
					+ " left join (select  ROW_NUMBER() OVER(PARTITION BY c.fxmnumber ORDER BY c.f_kdxf_xmjd asc) rn , c.fxmnumber, c.F_KDXF_XMJD,c.F_KDXF_XMJDNAME,c.FBillStatus from   xfsmzjk.T_XM_XMJHCBTRJZ@toerp c where c.FBillStatus ='A' )  d on a.xmbm=d.fxmnumber and d.rn=1  "
					+ "  where a.id =:recordId";
			sql4="select xmlb,fylxbm,yskm,yskmbm,sl,xfyrq,scopevalue,fysm,xmbm,xmmc,cpx,ssbm,ssbmbm,ssbmfzr,d.f_kdxf_xmjd,sum(bxje) bxje ,sum(bhsje) bhsje ,sum(zzsjxse) zzsjxse,sum(hdbxje) hdbxje ,sum(hdbhsje) hdbhsje ,sum(hdzzsjxse) hdzzsjxse from  dgcglbxzb left join dgcglbx_scope on cyr=fielddatavalue  "
					+ " left join (select  ROW_NUMBER() OVER(PARTITION BY c.fxmnumber ORDER BY c.f_kdxf_xmjd asc) rn , c.fxmnumber, c.F_KDXF_XMJD,c.F_KDXF_XMJDNAME,c.FBillStatus from   xfsmzjk.T_XM_XMJHCBTRJZ@toerp c where c.FBillStatus ='A' )  d on xmbm=d.fxmnumber and d.rn=1 "
					+ " where fkid=:recordId and hdbxje>0 group by xmlb,fylxbm,yskm,yskmbm,xfyrq,scopevalue,fysm,sl,xmbm,xmmc,cpx,ssbm,ssbmbm,ssbmfzr,d.f_kdxf_xmjd";
			paramsMap.put("maintableName","dgcglbx");
		}


		Map queryMap=new HashMap();
		queryMap.put("recordId", recordId);
		List list=super.findByListNativeQuery(sqlmain, "", queryMap);

		JSONObject amount=JSONObject.parseObject(amountJsonString,Feature.OrderedField);
		if(list!=null&&list.size()>0) {
			Map map=(Map) list.get(0);
			String wldwlx=StringUtils.null2String(map.get("wldwlx"));//往来单位类型
			String wldwlxbm=StringUtils.null2String(map.get("wldwlxbm"));//往来单位类型编码
			String wldwbm=StringUtils.null2String(map.get("wldwbm"));//往来单位编码

			String title=StringUtils.null2String(map.get("title"));//流程标题
			String bh=StringUtils.null2String(map.get("bh"));//流程编号
			String createdby=StringUtils.null2String(map.get("createdby"));//申请人
			String createdorg=StringUtils.null2String(map.get("createdorg"));//申请人组部门
			String sqrq=StringUtils.null2String(map.get("sqrq"));//申请日期
			String bxr=StringUtils.null2String(map.get("bxr"));//报销人
			String dlbx=StringUtils.null2String(map.get("dlbx"));//代理报销
			String dlbxr=StringUtils.null2String(map.get("scopevalue"));//代理报销人
			String ywlx=StringUtils.null2String(map.get("ywlx"));//业务类型
			String sfdxmbx=StringUtils.null2String(map.get("sfdxmbx"));//是否多项目报销
			String gsgs=StringUtils.null2String(map.get("gsgs"));//归属公司
			String btlx=StringUtils.null2String(map.get("btlx"));//补贴类型
			String xmbm=StringUtils.null2String(map.get("xmbm"));//项目编号
			String xmlb=StringUtils.null2String(map.get("xmlb"));//项目类别
			String f_kdxf_xmjd=StringUtils.null2String(map.get("f_kdxf_xmjd"));//项目阶段
			String xmmc=StringUtils.null2String(map.get("xmmc"));//项目名称
			String xmjl=StringUtils.null2String(map.get("xmjl"));//项目经理
			String ssbmbm=StringUtils.null2String(map.get("ssbmbm"));//项目所属部门编码
			String fylb=StringUtils.null2String(map.get("fylb"));//费用类别
			String jsfsbm=StringUtils.null2String(map.get("jsfsbm"));//结算方式
			String bz=StringUtils.null2String(map.get("bz"));//币种
			String fxje=StringUtils.null2String(map.get("fxje"));//付现金额
			String czje=StringUtils.null2String(map.get("czje"));//冲账金额
			
			String cjklc=StringUtils.null2String(map.get("cjklc"));//冲借款流程
			String cjklx=StringUtils.null2String(map.get("cjklx"));//冲借款借款类型
			erpdjbh=StringUtils.null2String(map.get("erpdjbh"));//erp单据编号
			String fieldValue="";
			String fj=StringUtils.null2String(map.get("fj"));
			if(org.apache.commons.lang3.StringUtils.isNotEmpty(fj)) {
				fieldValue=fj;
			}
			if(org.apache.commons.lang3.StringUtils.isNotEmpty(fieldValue)) {
				fieldValue="'"+fieldValue.replace("|", "','")+"'";
				fileList=systemFlowService.getFileListByIds(fieldValue);
			}

			amount.put("FDATE",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			//amount.put("FDATE", sqrq);//业务日期
			amount.put("FENDDATE_H", sqrq);
			amount.put("FACCNTTIMEJUDGETIME", sqrq);//到期计算日期
			amount.put("F_KDXF_CJKLC", cjklc);//冲借款流程
			amount.put("F_KDXF_JKLX", cjklc);//冲借款流程

			amount.put("FCONTACTUNITTYPE", wldwlxbm);//往来单位类型
			String wldw="";
			String sqrbmid="";//申请人部门id
			if("true".equals(dlbx)||"1".equals(dlbx)) {
				wldw=dlbxr;
				//根据报销人用户id获取组织id
				sqrbmid=systemFlowService.getUserOrOrgIdByFNumber(dlbxr, "userorg").get("id");
			}else {
				wldw=createdby;
				sqrbmid=createdorg;
			}
			 /*Map<String, String> userAccountmap= systemFlowService.getUserOrOrgFNumberById(createdby, "user",paramsMap);
			 String  FCreatorId=userAccountmap.get("fNumber");*/

			Map<String, String> userAccountmap= systemFlowService.getUserOrOrgFNumberById(wldw, "user",paramsMap);
			String FCreatorId=userAccountmap.get("fNumber");
			amount.put("F_KDXF_SQR", JSONObject.parseObject("{\"FSTAFFNUMBER\":\""+FCreatorId+"\"}"));//申请人
			amount.put("FCONTACTUNIT", JSONObject.parseObject("{\"FNumber\":\""+wldwbm+"\"}"));//往来单位
			amount.put("FCURRENCYID", JSONObject.parseObject("{\"FNumber\":\""+bz+"\"}"));
			//获取申请人报销 FDEPARTMENTID
			Map<String, String> orgCodemap= systemFlowService.getUserOrOrgFNumberById(sqrbmid, "org",paramsMap);
			String orgCode=orgCodemap.get("fNumber");
			//获取申请部门
			// String FDEPARTMENTID= systemFlowService.getERPuserOrgId(orgCode);
			amount.put("FDEPARTMENTID", JSONObject.parseObject("{\"FNumber\":\""+orgCode+"\"}"));

			Map<String, String> orgCodeMap= systemFlowService.getUserOrOrgFNumberById(gsgs, "org",paramsMap);
			orgCode=orgCodeMap.get("fNumber");
			//获取所属公司编码
			String ssgsf=systemFlowService.getERPuserOrgId(orgCode);
			amount.put("FSETTLEORGID", JSONObject.parseObject("{\"FNumber\":\""+ssgsf+"\"}"));//使用组织
			amount.put("FPAYORGID", JSONObject.parseObject("{\"FNumber\":\""+ssgsf+"\"}"));//付款组织

			amount.remove("FPURCHASEORGID");
			amount.put("FSettleTypeID", JSONObject.parseObject("{\"FNumber\":\""+jsfsbm+"\"}"));  //结算方式
			amount.put("F_KDXF_SFFT1", sfdxmbx);  // 是否分摊
			amount.put("F_KDXF_DLBX1", dlbx);  // 代理报销
			amount.put("F_KDXF_ZT", title);
			amount.put("F_KDXF_LCBH", bh);
			
			amount.put("FNOTSETTLEAMOUNTFOR1", czje);//冲借款金额
			amount.put("FNOTSETTLEAMOUNTFOR", fxje);//付现金额

			//获取通用子表
			JSONArray FEntityArray=new JSONArray();
			System.out.println(sql4);
			List list1=super.findByListNativeQuery(sql4, "", queryMap);
			if(list1!=null&&list1.size()>0) {
				for(int i=0,size=list1.size();i<size;i++) {
					Map map1=(Map) list1.get(i);

					String fylxbm=StringUtils.null2String(map1.get("fylxbm"));//费用类型编码
					String yskmbm=StringUtils.null2String(map1.get("yskmbm"));//预算科目编码
					String xfyrq=StringUtils.null2String(map1.get("xfyrq"));//日期
					String cyr=StringUtils.null2String(map1.get("scopevalue"));//参与人
					String sl=StringUtils.null2String(map1.get("sl"));//税率
					String fplx=StringUtils.null2String(map1.get("fplx"));//发票类型
					String xmbm1=StringUtils.null2String(map1.get("xmbm"));//项目编码
					String xmlb1=StringUtils.null2String(map1.get("xmlb"));//项目类别
					String f_kdxf_xmjd1=StringUtils.null2String(map1.get("f_kdxf_xmjd"));//项目阶段
					String ssbmbm1=StringUtils.null2String(map1.get("ssbmbm"));//所属部门
					String bhsje=StringUtils.null2String(map1.get("bhsje"));//核定不含税金额
					String zzsjxse=StringUtils.null2String(map1.get("zzsjxse"));//核定进项税金额
					String bxje=StringUtils.null2String(map1.get("bxje"));//核定报销金额

					String hdbhsje=StringUtils.null2String(map1.get("hdbhsje"));//核定不含税金额
					String hdzzsjxse=StringUtils.null2String(map1.get("hdzzsjxse"));//核定进项税金额
					String hdbxje=StringUtils.null2String(map1.get("hdbxje"));//核定报销金额
					if(StringUtils.isNotEmpty(hdbxje)&&new BigDecimal(hdbxje).compareTo(new BigDecimal(0))==1) {
						bxje=hdbxje;
						zzsjxse=hdzzsjxse;
						bhsje=hdbhsje;
					}

					if("false".equals(sfdxmbx)) {
						xmbm1=xmbm;
						xmlb1=xmlb;
						ssbmbm1=ssbmbm;
						f_kdxf_xmjd1=f_kdxf_xmjd;
					}

					JSONObject json=new JSONObject(true);
					json.put("F_KDXF_XMLB", xmlb1);//项目类别
					json.put("F_KDXF_XMMC1",JSONObject.parseObject("{\"FNumber\":\""+xmbm1+"\"}"));//项目编码
					json.put("F_KDXF_XMJD",JSONObject.parseObject("{\"FNumber\":\""+f_kdxf_xmjd1+"\"}"));//项目阶段
					json.put("F_KDXF_FYLX", JSONObject.parseObject("{\"FNumber\":\""+fylxbm+"\"}"));//费用类型编码
					json.put("FCOSTID", JSONObject.parseObject("{\"FNumber\":\""+yskmbm+"\"}"));//费用项目编码
					Map<String, String> userAccountmap1= systemFlowService.getUserOrOrgFNumberById(cyr, "user",paramsMap);
					String  cyrfnum=userAccountmap1.get("fNumber");
					json.put("F_KDXF_CHRY", JSONObject.parseObject("{\"FSTAFFNUMBER\":\""+cyrfnum+"\"}"));//参会人

					//获取申请人报销 FDEPARTMENTID
					Map<String, String> orgCodemap1= systemFlowService.getUserOrOrgFNumberById(ssbmbm1, "org",paramsMap);
					String FCOSTDEPARTMENTID=orgCodemap1.get("fNumber");
					if("true".equals(sfdxmbx)) {
						FCOSTDEPARTMENTID = ssbmbm1 + "";
					}else{
						FCOSTDEPARTMENTID = ssbmbm + "";
					}
					//费用承担部门
					// String FCOSTDEPARTMENTID= systemFlowService.getERPuserOrgId(orgCode1);
					json.put("FCOSTDEPARTMENTID", JSONObject.parseObject("{\"FNumber\":\""+FCOSTDEPARTMENTID+"\"}"));//费用承担部门

					json.put("FEntryTaxRate", sl);
					json.put("FINVOICETYPE", fplx);
					json.put("FTOTALAMOUNTFOR", bxje);
					json.put("FNOTAXAMOUNTFOR", bhsje);
					json.put("FTAXAMOUNTFOR", zzsjxse);
					//设置实体数组
					System.out.println("jsonData ====>>>" + json);
					FEntityArray.add(json);
				}

				amount.put("FEntity", FEntityArray);
			}

			KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
					"true", amount,tableName,recordId);
			resultList.add((JSONObject) JSONObject.toJSON(appData));
		}

		return resultList;
	}




	/**
	 *
	 * @param recordId
	 * @return
	 */
	public List<JSONObject> getAmountList(String recordId)
	{
		List resultList = new ArrayList();
		this.erpdjbh = "";

		String sqlmain = "select  a.title ,a.bh,a.createdby,a.sqrq,a.createdorg,a.bxr,a.dlbx,b.scopevalue,a.fylxbm,a.ywlx,a.sfdxmbx,a.gsgs,a.btlx,a.xmbm,a.xmlb,a.xmmc,a.xmjl,a.fylb,a.fj,a.fycbsm,a.jffsbm,a.bz,a.cwhdje,a.fxje,a.erpdjbh ,a.bgbt,a.fbgbt,a.zsbt,a.jkdqbt,a.hdbgbt,a.hdfbgbt,a.hdzsbt,a.hdjkdqbt, d.f_kdxf_xmjd from ygbx a left join ygbx_scope  b on a.dlbxr=b.fielddatavalue  left join (select  ROW_NUMBER() OVER(PARTITION BY c.fxmnumber ORDER BY c.f_kdxf_xmjd asc) rn , c.fxmnumber, c.F_KDXF_XMJD,c.F_KDXF_XMJDNAME,c.FBillStatus from   xfsmzjk.T_XM_XMJHCBTRJZ@toerp c where c.FBillStatus ='A' )  d on a.xmbm=d.fxmnumber and d.rn=1   where a.id =:recordId";

		String sql1 = "select fylx,fylxbm,yskm,yskmbm,sl,fplx,xfyrq,scopevalue,fysm,xmbm,xmlb,xmmc,cpx,ssbm,ssbmfzr,sum(bxje) bxje ,sum(bhsje) bhsje ,sum(zzsjxse) zzsjxse,sum(hdbhsje) hdbhsje,sum(hdzzsjxse) hdzzsjxse,sum(hdbxje) hdbxje , f_kdxf_xmjd from  ygbxtyzb left join ygbx_scope on cyr=fielddatavalue left join (select  ROW_NUMBER() OVER(PARTITION BY c.fxmnumber ORDER BY c.f_kdxf_xmjd asc) rn , c.fxmnumber, c.F_KDXF_XMJD,c.F_KDXF_XMJDNAME,c.FBillStatus from   xfsmzjk.T_XM_XMJHCBTRJZ@toerp c where c.FBillStatus ='A' )  d on xmbm=d.fxmnumber and d.rn=1  where fkid=:recordId and hdbxje>0 group by fylx,fylxbm,yskm,yskmbm,xfyrq,scopevalue,fysm,sl,fplx,xmbm,xmlb,xmmc,cpx,ssbm,ssbmfzr,f_kdxf_xmjd ";

		String sqlAll = "select * from (select yskm ,yskmbm,sl,fplx,sum(bxje) bxje ,sum(bhsje) bhsje ,sum(zzsjxse) zzsjxse,sum(hdbhsje) hdbhsje,sum(hdzzsjxse) hdzzsjxse,sum(hdbxje) hdbxje  from ( select yskm ,yskmbm,sl,fplx,bxje ,bhsje,zzsjxse,hdbhsje,hdzzsjxse,hdbxje from ccf where fkid=:recordId "
				+ " union all  select yskm ,yskmbm,sl,fplx,bxje ,bhsje,zzsjxse,hdbhsje,hdzzsjxse,hdbxje from zsf where fkid=:recordId "
				+ " union all  select yskm ,yskmbm,sl,fplx,bxje ,bhsje,zzsjxse,hdbhsje,hdzzsjxse,hdbxje from dcf where fkid=:recordId "
				+ " union all  select yskm ,yskmbm,sl,fplx,bxje ,bhsje,zzsjxse,hdbhsje,hdzzsjxse,hdbxje from clfqt where fkid=:recordId ) c   group by yskm ,yskmbm,sl,fplx ) d where d.hdbxje >0";


		/*String sqlAll = "select * from (select yskm ,yskmbm,sl,fplx,sum(bxje) bxje ,sum(bhsje) bhsje ,sum(zzsjxse) zzsjxse,sum(hdbhsje) hdbhsje,sum(hdzzsjxse) hdzzsjxse,sum(hdbxje) hdbxje  from ( select yskm ,yskmbm,sl,fplx,bxje ,bhsje,zzsjxse,hdbhsje,hdzzsjxse,hdbxje from ccf where fkid=:recordId "
				+ " union all  select yskm ,yskmbm,sl,fplx,bxje ,bhsje,zzsjxse,hdbhsje,hdzzsjxse,hdbxje from zsf where fkid=:recordId "
				+ " union all  select yskm ,yskmbm,sl,fplx,bxje ,bhsje,zzsjxse,hdbhsje,hdzzsjxse,hdbxje from dcf where fkid=:recordId "
				+ " union all  select yskm ,yskmbm,sl,fplx,bxje ,bhsje,zzsjxse,hdbhsje,hdzzsjxse,hdbxje from clfqt where fkid=:recordId ";*/
		//判断是否是差旅费
		/*String getFYLX_sql = "select fylx from ygbx where id = '"+recordId+"' ";
		List listgetFYLX = jdbcTemplate.queryForList(getFYLX_sql);
		if(listgetFYLX!=null && listgetFYLX.size()>0){
			Map<String,Object> mapFYLX = (Map<String,Object>)listgetFYLX.get(0);
			String fylxTemp = mapFYLX.get("fylx")+"";
			if("121".equalsIgnoreCase(fylxTemp)){
				sqlAll += sql1;
				sqlAll += ") c   group by yskm ,yskmbm,sl,fplx ) d where d.bxje >0";

				sql1 += " and 1<>1";
			}else{
				sqlAll += ") c   group by yskm ,yskmbm,sl,fplx ) d where d.bxje >0";
			}

		}*/

		String ccfSql = " select cxfs,cfcs,ddcs,xcfsj,xddsj from ccf where fkid=:recordId ";

		Map paramsMap = new HashMap();
		paramsMap.put("maintableName", "ygbx");
		Map queryMap = new HashMap();
		queryMap.put("recordId", recordId);
		List list = super.findByListNativeQuery(sqlmain, "", queryMap);

		JSONObject amount = JSONObject.parseObject(this.amountJsonString, new Feature[] { Feature.OrderedField });
		if ((list != null) && (list.size() > 0)) {
			Map map = (Map)list.get(0);
			String title = StringUtils.null2String(map.get("title"));
			String bh = StringUtils.null2String(map.get("bh"));
			String createdby = StringUtils.null2String(map.get("createdby"));
			String createdorg = StringUtils.null2String(map.get("createdorg"));
			String sqrq = StringUtils.null2String(map.get("sqrq"));
			String bxr = StringUtils.null2String(map.get("bxr"));
			String dlbx = StringUtils.null2String(map.get("dlbx"));
			String dlbxr = StringUtils.null2String(map.get("scopevalue"));
			String ywlx = StringUtils.null2String(map.get("ywlx"));
			String sfdxmbx = StringUtils.null2String(map.get("sfdxmbx"));
			String gsgs = StringUtils.null2String(map.get("gsgs"));
			String btlx = StringUtils.null2String(map.get("btlx"));
			String xmbm = StringUtils.null2String(map.get("xmbm"));
			String xmlb = StringUtils.null2String(map.get("xmlb"));
			String f_kdxf_xmjd = StringUtils.null2String(map.get("f_kdxf_xmjd"));
			String xmmc = StringUtils.null2String(map.get("xmmc"));
			String xmjl = StringUtils.null2String(map.get("xmjl"));
			String fylb = StringUtils.null2String(map.get("fylb"));
			String fylxbmm = StringUtils.null2String(map.get("fylxbm"));
			String jffsbm = StringUtils.null2String(map.get("jffsbm"));
			String bz = StringUtils.null2String(map.get("bz"));

			this.erpdjbh = StringUtils.null2String(map.get("erpdjbh"));
			String cwhdje = StringUtils.null2String(map.get("cwhdje"));
			String fxje = StringUtils.null2String(map.get("fxje"));

			String fieldValue = "";
			String fj = StringUtils.null2String(map.get("fj"));

			if ("".equals(fj)) {
				fj = "init";
			}
			String sqlQueryChildFJ = "select distinct b.tyfj as bfj,c.ccfj as cfj,d.zsfj as dfj,e.dcfj as efj ,f.fj as ffj from ygbx a inner join ygbxtyzb b on a.id = b.fkid inner join ccf c on a.id = c.fkid inner join zsf d on a.id = d.fkid inner join dcf e on a.id = e.fkid inner join clfqt f on a.id = f.fkid  where a.id= '" + recordId + "' ";
			System.out.println("sqlQueryChildFJ====>>>>" + sqlQueryChildFJ);
			List listChild = this.jdbcTemplate.queryForList(sqlQueryChildFJ);
			if (listChild != null) {
				for (int i = 0; i < listChild.size(); i++) {
					Map mapFJ = (Map)listChild.get(i);
					if (org.apache.commons.lang3.StringUtils.isNotEmpty(mapFJ.get("bfj") + "") && fj.indexOf(mapFJ.get("bfj") + "")==-1 )
						fj = fj + "|" + mapFJ.get("bfj") + "";
					else {
						fj = fj + "";
					}
					if (org.apache.commons.lang3.StringUtils.isNotEmpty(mapFJ.get("cfj") + "") && fj.indexOf(mapFJ.get("cfj") + "")==-1 )
						fj = fj + "|" + mapFJ.get("cfj") + "";
					else {
						fj = fj + "";
					}
					if (org.apache.commons.lang3.StringUtils.isNotEmpty(mapFJ.get("dfj") + "") && fj.indexOf(mapFJ.get("dfj") + "")==-1 )
						fj = fj + "|" + mapFJ.get("dfj") + "";
					else {
						fj = fj + "";
					}
					if (org.apache.commons.lang3.StringUtils.isNotEmpty(mapFJ.get("efj") + "") && fj.indexOf(mapFJ.get("efj") + "")==-1 )
						fj = fj + "|" + mapFJ.get("efj") + "";
					else {
						fj = fj + "";
					}

					if (org.apache.commons.lang3.StringUtils.isNotEmpty(mapFJ.get("ffj") + "") && fj.indexOf(mapFJ.get("ffj") + "")==-1 )
						fj = fj + "|" + mapFJ.get("ffj") + "";
					else {
						fj = fj + "";
					}
				}

			}

			System.out.println("fjfjfjfjfjfjfjfj===>>>>" + fj);
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(fj)) {
				fieldValue = fj;
			}
			String fycbsm = StringUtils.null2String(map.get("fycbsm"));
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(fycbsm)) {
				if (org.apache.commons.lang3.StringUtils.isNotEmpty(fieldValue))
					fieldValue = fieldValue + "|" + fycbsm;
				else {
					fieldValue = fieldValue + fycbsm;
				}
			}

			if (org.apache.commons.lang3.StringUtils.isNotEmpty(fieldValue)) {
				fieldValue = "'" + fieldValue.replace("|", "','") + "'";

				System.out.println("fieldValuefieldValuefieldValue====>>>" + fieldValue);
				this.fileList = this.systemFlowService.getFileListByIds(fieldValue);
			}

			amount.put("FDATE",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			//amount.put("FDATE", sqrq);
			amount.put("FENDDATE_H", sqrq);
			amount.put("FACCNTTIMEJUDGETIME", sqrq);

			amount.put("FCONTACTUNITTYPE", "BD_Empinfo");
			String wldw = "";
			String sqrbmid = "";
			if (("true".equals(dlbx)) || ("1".equals(dlbx)))
				wldw = dlbxr;
			else {
				wldw = createdby;
			}
			Map userAccountmap = this.systemFlowService.getUserOrOrgFNumberById(createdby, "user", paramsMap);
			String FCreatorId = (String)userAccountmap.get("fNumber");
			amount.put("F_KDXF_SQR", JSONObject.parseObject("{\"FSTAFFNUMBER\":\"" + FCreatorId + "\"}"));

			userAccountmap = this.systemFlowService.getUserOrOrgFNumberById(wldw, "user", paramsMap);
			FCreatorId = (String)userAccountmap.get("fNumber");
			amount.put("FCONTACTUNIT", JSONObject.parseObject("{\"FNumber\":\"" + FCreatorId + "\"}"));
			amount.put("FCURRENCYID", JSONObject.parseObject("{\"FNumber\":\"" + bz + "\"}"));
			amount.put("FTOTALAMOUNTFOR_H", cwhdje);
			amount.put("FNOTSETTLEAMOUNTFOR", fxje);

			Map orgCodemap = this.systemFlowService.getUserOrOrgFNumberById(createdorg, "org", paramsMap);
			String orgCode = (String)orgCodemap.get("fNumber");

			amount.put("FDEPARTMENTID", JSONObject.parseObject("{\"FNumber\":\"" + orgCode + "\"}"));

			Map orgCodeMap = this.systemFlowService.getUserOrOrgFNumberById(gsgs, "org", paramsMap);
			orgCode = (String)orgCodeMap.get("fNumber");

			String ssgsf = this.systemFlowService.getERPuserOrgId(orgCode);
			amount.put("FSETTLEORGID", JSONObject.parseObject("{\"FNumber\":\"" + ssgsf + "\"}"));
			amount.put("FPAYORGID", JSONObject.parseObject("{\"FNumber\":\"" + ssgsf + "\"}"));

			amount.remove("FPURCHASEORGID");
			amount.put("FSettleTypeID", JSONObject.parseObject("{\"FNumber\":\"" + jffsbm + "\"}"));
			amount.put("F_KDXF_SFFT1", sfdxmbx);
			amount.put("F_KDXF_DLBX1", dlbx);
			amount.put("F_KDXF_ZT", title);
			amount.put("F_KDXF_LCBH", bh);

			String bgbt = StringUtils.null2String(map.get("bgbt"));
			String fbgbt = StringUtils.null2String(map.get("fbgbt"));
			String zsbt = StringUtils.null2String(map.get("zsbt"));
			String jkdqbt = StringUtils.null2String(map.get("jkdqbt"));

			String hdbgbt = StringUtils.null2String(map.get("hdbgbt"));
			String hdfbgbt = StringUtils.null2String(map.get("hdfbgbt"));
			String hdzsbt = StringUtils.null2String(map.get("hdzsbt"));
			String hdjkdqbt = StringUtils.null2String(map.get("hdjkdqbt"));

			if ("".equals(bgbt)) {
				bgbt = "0";
			}

			if ("".equals(fbgbt)) {
				fbgbt = "0";
			}

			if ("".equals(zsbt)) {
				zsbt = "0";
			}

			if ("".equals(jkdqbt)) {
				jkdqbt = "0";
			}

			if ("".equals(hdbgbt)) {
				hdbgbt = "0";
			}

			if ("".equals(hdfbgbt)) {
				hdfbgbt = "0";
			}

			if ("".equals(hdzsbt)) {
				hdzsbt = "0";
			}

			if ("".equals(hdjkdqbt)) {
				hdjkdqbt = "0";
			}

			BigDecimal hdbutieZongjie = new BigDecimal(hdbgbt).add(new BigDecimal(hdfbgbt)).add(new BigDecimal(hdzsbt)).add(new BigDecimal(hdjkdqbt));
			BigDecimal butieZongjie = new BigDecimal(bgbt).add(new BigDecimal(fbgbt)).add(new BigDecimal(zsbt)).add(new BigDecimal(jkdqbt));
			if (hdbutieZongjie.compareTo(new BigDecimal(0)) == 1 || true) {
				butieZongjie = hdbutieZongjie;
			}

			JSONArray FEntityArray = new JSONArray();
			List list1 = super.findByListNativeQuery(sql1, "", queryMap);
			if ((list1 != null) && (list1.size() > 0)) {
				int i = 0; for (int size = list1.size(); i < size; i++) {
					Map map1 = (Map)list1.get(i);

					String fylxbm = StringUtils.null2String(map1.get("fylxbm"));
					String yskmbm = StringUtils.null2String(map1.get("yskmbm"));
					String xfyrq = StringUtils.null2String(map1.get("xfyrq"));
					String cyr = StringUtils.null2String(map1.get("scopevalue"));
					String sl = StringUtils.null2String(map1.get("sl"));
					String fplx = StringUtils.null2String(map1.get("fplx"));
					String xmbm1 = StringUtils.null2String(map1.get("xmbm"));
					String xmlb1 = StringUtils.null2String(map1.get("xmlb"));
					String f_kdxf_xmjd1 = StringUtils.null2String(map1.get("f_kdxf_xmjd"));

					String hdbhsje = StringUtils.null2String(map1.get("hdbhsje"));
					String hdzzsjxse = StringUtils.null2String(map1.get("hdzzsjxse"));
					String hdbxje = StringUtils.null2String(map1.get("hdbxje"));

					String bhsje = StringUtils.null2String(map1.get("bhsje"));
					String zzsjxse = StringUtils.null2String(map1.get("zzsjxse"));
					String bxje = StringUtils.null2String(map1.get("bxje"));
					if ((StringUtils.isNotEmpty(hdbxje)) && (new BigDecimal(hdbxje).compareTo(new BigDecimal(0)) < 1)) {
						hdbxje = bxje;
						hdzzsjxse = zzsjxse;
						hdbhsje = bhsje;
					}

					if ((hdbxje == null) || ("".equals(hdbxje))) {
						hdbxje = "0";
					}

					if (new BigDecimal(hdbxje).compareTo(new BigDecimal(0)) >= 1)
					{
						if (!StringUtils.isEmpty(yskmbm))
						{
							if ("false".equals(sfdxmbx)) {
								xmbm1 = xmbm;
								xmlb1 = xmlb;
								f_kdxf_xmjd1 = f_kdxf_xmjd;
							}

							JSONObject json = new JSONObject(true);
							json.put("F_KDXF_XMLB", xmlb1);
							json.put("F_KDXF_XMMC1", JSONObject.parseObject("{\"FNumber\":\"" + xmbm1 + "\"}"));
							json.put("F_KDXF_XMJD", JSONObject.parseObject("{\"FNumber\":\"" + f_kdxf_xmjd1 + "\"}"));

							json.put("FCOSTID", JSONObject.parseObject("{\"FNumber\":\"" + yskmbm + "\"}"));
							json.put("FEntryTaxRate", sl);
							json.put("FINVOICETYPE", fplx);
							json.put("FTOTALAMOUNTFOR", hdbxje);
							json.put("FNOTAXAMOUNTFOR", "0".equals(getNumber(hdbhsje))?getNumber(hdbxje):getNumber(hdbhsje));
							json.put("FTAXAMOUNTFOR", hdzzsjxse);

							FEntityArray.add(json);
						}
					}
				}
			}
			String sqlProject = " select  xmlb,xmbm,clfftbl,d.f_kdxf_xmjd  from clfdxmftzb   left join (select  ROW_NUMBER() OVER(PARTITION BY c.fxmnumber ORDER BY c.f_kdxf_xmjd asc) rn , c.fxmnumber, c.F_KDXF_XMJD,c.F_KDXF_XMJDNAME,c.FBillStatus from   xfsmzjk.T_XM_XMJHCBTRJZ@toerp c where c.FBillStatus ='A' )  d on xmbm=d.fxmnumber and d.rn=1  where  fkid=:recordId ";

			List projectList = super.findByListNativeQuery(sqlProject, "", queryMap);

			List list2 = super.findByListNativeQuery(sqlAll, "", queryMap);
			System.out.println("sqlAllnenennenne========>>>>"+sqlAll);
			if ((list2 != null) && (list2.size() > 0)) {
				int i = 0; for (int size = list2.size(); i < size; i++) {
					Map map1 = (Map)list2.get(i);
					System.out.println("map1========>>>>"+map1);

					String yskmbm = StringUtils.null2String(map1.get("yskmbm"));
					String sl = StringUtils.null2String(map1.get("sl"));
					String fplx = StringUtils.null2String(map1.get("fplx"));
					String hdbhsje = StringUtils.null2String(map1.get("hdbhsje"));
					String hdzzsjxse = StringUtils.null2String(map1.get("hdzzsjxse"));
					String hdbxje = StringUtils.null2String(map1.get("hdbxje"));

					String bhsje = StringUtils.null2String(map1.get("bhsje"));
					String zzsjxse = StringUtils.null2String(map1.get("zzsjxse"));
					String bxje = StringUtils.null2String(map1.get("bxje"));
					if ((StringUtils.isNotEmpty(hdbxje)) && (new BigDecimal(hdbxje).compareTo(new BigDecimal(0)) < 1)) {
						hdbxje = bxje;
						hdzzsjxse = zzsjxse;
						hdbhsje = bhsje;
					}

					if (new BigDecimal(hdbxje).compareTo(new BigDecimal(0)) >= 1)
					{
						if ("false".equals(sfdxmbx)) {
							JSONObject json = new JSONObject(true);
							json.put("F_KDXF_XMLB", xmlb);
							json.put("F_KDXF_XMMC1", JSONObject.parseObject("{\"FNumber\":\"" + xmbm + "\"}"));
							json.put("F_KDXF_XMJD", JSONObject.parseObject("{\"FNumber\":\"" + f_kdxf_xmjd + "\"}"));

							json.put("FCOSTID", JSONObject.parseObject("{\"FNumber\":\"" + yskmbm + "\"}"));
							json.put("FEntryTaxRate", sl);
							json.put("FINVOICETYPE", fplx);
							json.put("FTOTALAMOUNTFOR", hdbxje);
							json.put("FNOTAXAMOUNTFOR", "0".equals(getNumber(hdbhsje))?getNumber(hdbxje):getNumber(hdbhsje));
							json.put("FTAXAMOUNTFOR", hdzzsjxse);

							FEntityArray.add(json);
							if (i == 0 && butieZongjie.compareTo(new BigDecimal(0))==1) {
								json = new JSONObject(true);
								json.put("F_KDXF_XMLB", xmlb);
								json.put("F_KDXF_XMMC1", JSONObject.parseObject("{\"FNumber\":\"" + xmbm + "\"}"));
								json.put("F_KDXF_XMJD", JSONObject.parseObject("{\"FNumber\":\"" + f_kdxf_xmjd + "\"}"));
								json.put("FCOSTID", JSONObject.parseObject("{\"FNumber\":\"FYXM10_SYS\"}"));
								json.put("FEntryTaxRate", "0");
								json.put("FINVOICETYPE", "0");
								json.put("FTOTALAMOUNTFOR", butieZongjie);
								json.put("FNOTAXAMOUNTFOR", butieZongjie);
								json.put("FTAXAMOUNTFOR", "0");

								FEntityArray.add(json);
							}
						}
						else if ((projectList != null) && (projectList.size() > 0))
						{
							int j = 0; for (int sizeJ = projectList.size(); j < sizeJ; j++) {
							Map mapxm = (Map)projectList.get(j);
							String xmbm1 = StringUtils.null2String(mapxm.get("xmbm"));
							String xmlb1 = StringUtils.null2String(mapxm.get("xmlb"));
							String clfftbl = StringUtils.null2String(mapxm.get("clfftbl"));
							String f_kdxf_xmjd1 = StringUtils.null2String(mapxm.get("f_kdxf_xmjd"));

							BigDecimal a = new BigDecimal(getNumber(hdbxje));
							BigDecimal a1 = new BigDecimal("0".equals(getNumber(hdbhsje))?getNumber(hdbxje):getNumber(hdbhsje));
							BigDecimal a2 = new BigDecimal(getNumber(hdzzsjxse));
							BigDecimal b = new BigDecimal(Float.valueOf(clfftbl).floatValue()).divide(new BigDecimal(100.0D), 6, 4);
							JSONObject json = new JSONObject(true);
							json.put("F_KDXF_XMLB", xmlb1);
							json.put("F_KDXF_XMMC1", JSONObject.parseObject("{\"FNumber\":\"" + xmbm1 + "\"}"));
							json.put("F_KDXF_XMJD", JSONObject.parseObject("{\"FNumber\":\"" + f_kdxf_xmjd1 + "\"}"));

							json.put("FCOSTID", JSONObject.parseObject("{\"FNumber\":\"" + yskmbm + "\"}"));
							json.put("FEntryTaxRate", sl);
							json.put("FINVOICETYPE", fplx);
							json.put("FTOTALAMOUNTFOR", a.multiply(b));
							json.put("FNOTAXAMOUNTFOR", a1.multiply(b));
							json.put("FTAXAMOUNTFOR", a2.multiply(b));

							FEntityArray.add(json);

							if (i == 0 && (butieZongjie.multiply(b)).compareTo(new BigDecimal(0))==1) {
								json = new JSONObject(true);
								json.put("F_KDXF_XMLB", xmlb1);
								json.put("F_KDXF_XMMC1", JSONObject.parseObject("{\"FNumber\":\"" + xmbm1 + "\"}"));
								json.put("F_KDXF_XMJD", JSONObject.parseObject("{\"FNumber\":\"" + f_kdxf_xmjd1 + "\"}"));

								json.put("FCOSTID", JSONObject.parseObject("{\"FNumber\":\"FYXM10_SYS\"}"));
								json.put("FEntryTaxRate", "0");
								json.put("FINVOICETYPE", "0");
								json.put("FTOTALAMOUNTFOR", butieZongjie.multiply(b));
								json.put("FNOTAXAMOUNTFOR", butieZongjie.multiply(b));
								json.put("FTAXAMOUNTFOR", "0");

								FEntityArray.add(json);
							}
						}
						}

					}

				}

			}

			List ccfList = super.findByListNativeQuery(ccfSql, "", queryMap);
			if ((ccfList != null) && (ccfList.size() > 0)) {
				JSONArray F_KDXF_XCEntityArr = new JSONArray();
				int i = 0; for (int size = ccfList.size(); i < size; i++) {
					Map map1 = (Map)ccfList.get(i);
					JSONObject F_KDXF_XCEntity = new JSONObject(true);

					String cxfs = StringUtils.null2String(map1.get("cxfs"));
					String cfcs = StringUtils.null2String(map1.get("cfcs"));
					String ddcs = StringUtils.null2String(map1.get("ddcs"));
					String xcfsj = StringUtils.null2String(map1.get("xcfsj"));
					String xddsj = StringUtils.null2String(map1.get("xddsj"));
					F_KDXF_XCEntity.put("F_KDXF_CXFS", cxfs);
					F_KDXF_XCEntity.put("F_KDXF_CFCS", cfcs);
					F_KDXF_XCEntity.put("F_KDXF_DDCS", ddcs);
					F_KDXF_XCEntity.put("F_KDXF_CFSJ", xcfsj);
					F_KDXF_XCEntity.put("F_KDXF_DDSJ", xddsj);

					F_KDXF_XCEntityArr.add(F_KDXF_XCEntity);
				}
				amount.put("F_KDXF_XCEntity", F_KDXF_XCEntityArr);
			}


			System.out.println("FEntityArray111=====>>>" + FEntityArray);
			//对于差旅费 多项目 是否有招待费
			String sqlQuery = "select fylx,sfdxmbx,sfyzdf from ygbx where id = '"+recordId+"' ";
			List listQuery = jdbcTemplate.queryForList(sqlQuery);
			if(listQuery.size()>0){
				Map<String,Object> mapQuery = (Map<String,Object>)listQuery.get(0);
				String fylxTemp = mapQuery.get("fylx")+"";
				String sfdxmbxTemp = mapQuery.get("sfdxmbx")+"";
				String sfyzdfTemp = mapQuery.get("sfyzdf")+"";
				if("121".equals(fylxTemp) && "true".equals(sfdxmbxTemp) && "true".equals(sfyzdfTemp) ){
					//获取基础数据
					JSONObject jsonTemp = (JSONObject) FEntityArray.get(0);

					//获取总金额
					String moneyTemp="";
					String getTotalMoneySql = " select sum(hdbhsje) allheji from ygbxtyzb where fkid = '" + recordId + "' ";
					List listMoney = jdbcTemplate.queryForList(getTotalMoneySql);
					if(listMoney.size()>0){

						Map<String,Object> mapMoney = (Map<String,Object>)listMoney.get(0);
						moneyTemp = mapMoney.get("allheji")+"";
					}

					//String moneyTemp = jsonTemp.get("FTOTALAMOUNTFOR")+"";
					if("".equals(moneyTemp)){
						moneyTemp = "0";
					}
					//jsonTemp.get("FNOTAXAMOUNTFOR");

					//重新封装(此处sql取第一条)
					StringBuffer sqlgetClfftbuffer = new StringBuffer();
					sqlgetClfftbuffer.append(" select * from (select a.xmmc, a.xmbm, a.clfftbl, a.xmlb, b.F_KDXF_XMJD, ");
					sqlgetClfftbuffer.append(" ROW_NUMBER() OVER(PARTITION BY b.fxmnumber ORDER BY b.f_kdxf_xmjd asc) rn ");
					sqlgetClfftbuffer.append(" from clfdxmftzb a ");
					sqlgetClfftbuffer.append(" left join xfsmzjk.T_XM_XMJHCBTRJZ@toerp b ");
					sqlgetClfftbuffer.append(" on a.xmbm = b.fxmnumber ");
					sqlgetClfftbuffer.append(" where a.fkid = '"+ recordId +"' ");
					sqlgetClfftbuffer.append(" and (b.FBillStatus = 'A' or b.F_KDXF_XMJD is null)) where rn=1 ");

					String sqlgetClfft = sqlgetClfftbuffer.toString() + "";
					//String sqlgetClfft = "select a.xmmc,a.xmbm,a.clfftbl,a.xmlb,b.F_KDXF_XMJD from clfdxmftzb a left join xfsmzjk.T_XM_XMJHCBTRJZ@toerp b on a.xmbm = b.fxmnumber where a.fkid='"+ recordId +"' and  (b.FBillStatus = 'A' or b.F_KDXF_XMJD is null) ";
					List listQueryDetail = jdbcTemplate.queryForList(sqlgetClfft);
					if(listQueryDetail.size()>0){
						for( int i=0;i<listQueryDetail.size();i++){
							JSONObject jsonNew = JSONObject.parseObject(jsonTemp.toJSONString(),Feature.OrderedField);

							Map<String,Object> mapTemp = (Map<String,Object>)listQueryDetail.get(i);
							String tempXmbm = mapTemp.get("xmbm")+"";
							System.out.println("tempXmbm==>>>" + tempXmbm);
							String tempClfftbl = mapTemp.get("clfftbl")+"";
							String tempXmlb = mapTemp.get("xmlb")+"";
							String tempF_KDXF_XMJD = mapTemp.get("F_KDXF_XMJD")+"";

							if("".equals(tempClfftbl)){
								tempClfftbl = "0";
							}

							Double infoMoney = Double.parseDouble(tempClfftbl)/100 * Double.parseDouble(moneyTemp) ;

							if("".equals(tempF_KDXF_XMJD) || "null".equals(tempF_KDXF_XMJD)){
								tempF_KDXF_XMJD = "";
							}

							jsonNew.put("F_KDXF_XMLB",tempXmlb + "");
							jsonNew.put("F_KDXF_XMMC1", JSONObject.parseObject("{\"FNumber\":\"" + tempXmbm + "\"}"));
							jsonNew.put("F_KDXF_XMJD", JSONObject.parseObject("{\"FNumber\":\"" + tempF_KDXF_XMJD + "\"}"));
							jsonNew.put("FTOTALAMOUNTFOR",new BigDecimal(infoMoney).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
							jsonNew.put("FNOTAXAMOUNTFOR",new BigDecimal(infoMoney).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

							//addinfo
							FEntityArray.add(jsonNew);
						}

					}

					//删除多余数据
					System.out.println("FEntityArray=======>>up_update=======>>>>" + FEntityArray);
					String sqlGetTYsql = " select * from ygbxtyzb where fkid = '" + recordId + "' ";
					List listTyNumList = jdbcTemplate.queryForList(sqlGetTYsql);
					for(int i=0;i<listTyNumList.size();i++){
						FEntityArray.remove(0);
					}
					System.out.println("FEntityArray=======>>down_update=======>>>>" + FEntityArray);
					/*for(int i=0;i<FEntityArray.size();i++){
						JSONObject jsonChai = (JSONObject)FEntityArray.get(i);

						JSONObject jsonChai2  = (JSONObject)jsonChai.get("F_KDXF_XMMC1");
						if( "".equals(jsonChai2.get("FNumber"))){

							FEntityArray.remove(i);
							i=0;
						}
					}*/
				}
			}

			System.out.println("FEntityArray222=====>>>" + FEntityArray);

			amount.put("FEntity", FEntityArray);
			KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true", "true", amount, "ygbx", recordId);

			resultList.add((JSONObject)JSONObject.toJSON(appData));
		}

		return resultList;
	}




	/**
	 * 从中间库获取 获取项目阶段
	 * @param projectNumbers
	 * @return
	 */
	public Map<String ,Map<String,String>> getProjectInfo(String projectNumbers){
		Map<String ,Map<String,String>> projectMap=new HashMap<String ,Map<String,String>>();
		String sql=" select a.fnumber,a.f_kdxf_kzfy as sfkz, c.fxmnumber,c.rn,c.fid,c.f_kdxf_xmjd as xmjd,c.f_kdxf_xmjdname,c.fbillstatus from  xfsmzjk.T_KDXF_ITEM@toerp a left join  (" +
				" " +
				" select  ROW_NUMBER() OVER(PARTITION BY b.fxmnumber ORDER BY b.f_kdxf_xmjd asc) rn , b.fxmnumber, b.fid,b.F_KDXF_XMJD,b.F_KDXF_XMJDNAME,b.FBillStatus from   xfsmzjk.T_XM_XMJHCBTRJZ@toerp b where b.FBillStatus ='A' ) c on  a.fnumber=c.fxmnumber  where c.rn=1 and a.fnumber in("+projectNumbers+")";
		System.out.println("projectNumbers======>>>"+projectNumbers);
		System.out.println("sql=======>>>123123123====>>>"+sql);
		Map queryMap=new HashMap();
		List list2=super.findByListNativeQuery(sql, "", queryMap);
		if(list2!=null&&list2.size()>0) {
			for(int i=0,size=list2.size();i<size;i++) {
				Map<String,String> map1=(Map<String,String>) list2.get(i);
				String xmbm1=StringUtils.null2String(map1.get("fnumber"));//项目编码
				projectMap.put(xmbm1, map1);
			}
		}

		return projectMap;

	}


	/**
	 * 获取借款信息
	 * @param cjklc
	 * @return
	 */
	public Map<String, BigDecimal> getJiekuanMap(String cjklc) {
		String sql=" select a.xmbm,b.yskmbm,sum(sqje) as sqje  from jk a  left join jksqzb b on a.id=b.fkid where a.lcbh=:cjklc group by a.xmbm,b.yskmbm ";
		Map<String, BigDecimal> resultMap=new HashMap<String, BigDecimal>();
		Map queryMap=new HashMap();
		queryMap.put("cjklc", cjklc);
		List list=super.findByListNativeQuery(sql, "", queryMap);
		if(list!=null&&list.size()>0) {
			for(int i=0,size=list.size();i<size;i++) {
				Map map=(Map) list.get(i);
				String xmbm=StringUtils.null2String(map.get("xmbm"));//项目编码
				String yskmbm=StringUtils.null2String(map.get("yskmbm"));//预算科目编码
				String sqje=StringUtils.null2String(map.get("sqje"));//申请金额
				if(StringUtils.isNotEmpty(sqje)) {
					resultMap.put(xmbm+"!@#"+yskmbm, new BigDecimal(sqje));
				}
			}
		}
		return resultMap;
	}


	public   String getNumber(String number) {
		if( "".equalsIgnoreCase(number) || !isNumeric(number)) {
			number="0";
		}
		return number;
	}


	public  boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		if(str.indexOf(".")>0){//判断是否有小数点
			if(str.indexOf(".")==str.lastIndexOf(".") && str.split("\\.").length==2){ //判断是否只有一个小数点
				return pattern.matcher(str.replace(".","")).matches();
			}else {
				return false;
			}
		}else {
			return pattern.matcher(str).matches();
		}
	}

	public static void main(String[] args){
		System.out.println("1"+	new FinancialService().getNumber(null));
		System.out.println("2"+ StringUtils.null2String(""));
		System.out.println("3"+ StringUtils.null2String("null"));
		System.out.println("4"+ StringUtils.null2String(null));
	}




}
