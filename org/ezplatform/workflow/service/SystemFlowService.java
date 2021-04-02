package org.ezplatform.workflow.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.axis2.transport.http.RESTRequestEntity;
import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.core.service.BaseService;
import org.ezplatform.model.formmgr.runtime.dto.KeyValueDto;
import org.ezplatform.model.formmgr.runtime.dto.RuntimeDataDto;
import org.ezplatform.model.formmgr.runtime.dto.SubTblDataDto;
import org.ezplatform.util.StringUtils;
import org.ezplatform.workflow.dao.FlowBuesinessAttachDao;
import org.ezplatform.workflow.dao.FlowBuesinessFailDao;
import org.ezplatform.workflow.entity.FlowBuesinessAttach;
import org.ezplatform.workflow.entity.FlowBuesinessFail;
import org.ezplatform.workflow.entity.KingdeeApplicationData;
import org.ezplatform.workflow.entity.SelectEntity;
import org.ezplatform.workflow.web.client.HttpClientProperties;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import net.sf.jsqlparser.expression.operators.relational.JsonOperator;

@Service(value="systemFlowService")
@SuppressWarnings("all")
public class SystemFlowService extends BaseService<FlowBuesinessFail, String>{
	   protected Logger logger = LoggerFactory.getLogger(SystemFlowService.class);

		@Value("classpath:/flowConfig.json")
	    private Resource resource;
	    private static Map<String,Map<String,String>> flowMap = new LinkedHashMap<String, Map<String,String>>(); // 存取erp交互流程配置
	    private static Map<String,List<String []>> fieldMap =new LinkedHashMap<String, List<String []>>(); // 存取erp交互流程配置
	    private static Map<String,List<String []>> fieldChildMap =new LinkedHashMap<String, List<String []>>(); // 存取erp交互流程配置

	    
	    @Autowired
	    @Qualifier("flowBuesinessFailDao")
	    private FlowBuesinessFailDao flowBuesinessFailDao;
	    
	    @Autowired
	    private JdbcTemplate jdbcTemplate;
	    @Autowired
	    @Qualifier("flowBuesinessAttachDao")
	    private FlowBuesinessAttachDao flowBuesinessAttachDao;
	   
	    @Autowired
		private RestTemplate httpClientTemplate;
	    
	    @Autowired
	    private MaterialService materialService;
	    @Autowired
	    private AssetsService assetsService;
	    
	    @Autowired
	    private FinancialService financialService;
	    @Autowired 
	    private ShangjiService shangjiService;
		@Autowired
		private HttpClientProperties httpClientProperties;

		/*@PersistenceContext
		  private EntityManager entityManager;

		  protected EntityManager getEntityManager()
		  {
			  
		    return this.entityManager;
		  }*/
		private String saveUrl ;// 保存接口url
		private String loginUrl ;// 用户校验接口URL
		private String shUrl ;// 审核接口url
		private String fshUrl ;// 反审核接口url
		private String uploadurl;//附件上传接口地址
		private String deleteUrl;//删除接口
		private String path;//附件存储路径
		private String acctID ;
		private String username ;
		private String password ;
		private String lcid ;
		private String userToken;
		private String createId;
		private List<FlowBuesinessAttach> fileList;
		


	    public static Map<String, Map<String, String>> getFlowMap() {
			return flowMap;
		}

		public static void setFlowMap(Map<String, Map<String, String>> flowMap) {
			SystemFlowService.flowMap = flowMap;
		}

		public static Map<String, List<String[]>> getFieldMap() {
			return fieldMap;
		}

		public static void setFieldMap(Map<String, List<String[]>> fieldMap) {
			SystemFlowService.fieldMap = fieldMap;
		}
		

		public static Map<String, List<String[]>> getFieldChildMap() {
			return fieldChildMap;
		}

		public static void setFieldChildMap(Map<String, List<String[]>> fieldChildMap) {
			SystemFlowService.fieldChildMap = fieldChildMap;
		}

		@PostConstruct
	    public void init(){
	    	 getSystemFlowInfo();
	    	 //初始化金蝶参数
	    	 initKingConfig();
	    }
	    
	    /**
	     * 获取配置文件数据
	     * @return
	     */
	    private void getSystemFlowInfo() {
	    	  try {
	              File file = resource.getFile();
	              String jsonData = this.jsonRead(file);
	              JSONObject jsonObject = JSONObject.parseObject(jsonData);
	              JSONArray array = JSONArray.parseArray(jsonObject.get("processData").toString());
	             
	              for(int i=0,size=array.size();i<size;i++){
	            	  Map<String,String> tempMap=new HashMap<String,String>();
	                  JSONObject jsonObject2 = array.getJSONObject(i);
	                  String processCode=(String) jsonObject2.get("processCode");
	                  String tableName=(String) jsonObject2.get("tableName");
	                  String className=(String) jsonObject2.get("className");
	                  String classMethod=(String) jsonObject2.get("classMethod");
	                  String getDataMethod=jsonObject2.get("getDataMethod")==null?"":jsonObject2.get("getDataMethod").toString();
	                  String formid=(String) jsonObject2.get("formid");
	                  String dataTpye=(String) jsonObject2.get("dataTpye");
	                  tempMap.put("tableName", tableName);
	                  tempMap.put("className", className);
	                  tempMap.put("classMethod", classMethod);
	                  tempMap.put("processCode", processCode);
	                  tempMap.put("formid", formid);
	                  tempMap.put("dataTpye", dataTpye);
	                  tempMap.put("getDataMethod", getDataMethod);
	                  flowMap.put(processCode, tempMap);
	                  JSONArray arrayFields = JSONArray.parseArray(jsonObject2.get("tableFields").toString());
	                 
	                  //交互字段类型
	                  List<String []> tempMapFileds=new ArrayList<String []>();
	                  for(int j=0,sizej=arrayFields.size();j<sizej;j++){
	                	  JSONObject jsonObjectField = arrayFields.getJSONObject(j);
	                	  if(jsonObjectField.get("systemField")!=null) {
	                		  //主表字段 
	                		  String systemField=(String) jsonObjectField.get("systemField");
		                      String fieldType=(String) jsonObjectField.get("fieldType");
		                      String interFaceField=(String) jsonObjectField.get("interFaceField");
		                      String interFaceChild=(String) jsonObjectField.get("interFaceChild");
		                      String [] arrTemp=new String [] {systemField,fieldType,interFaceField,interFaceChild};
		                      tempMapFileds.add(arrTemp);
	                	  }else if(jsonObjectField.get("childTable")!=null) {
	                		 // 子表字段
	                		  //fieldChildMap	
                		  		String childTable=(String) jsonObjectField.get("childTable");
                		  		String interFaceField=(String) jsonObjectField.get("interFaceField");
                		  	    String fieldType=(String) jsonObjectField.get("fieldType");
	                			
                		  	 	String [] arrTemp=new String [] {childTable,fieldType,interFaceField,""};
                		  	 	tempMapFileds.add(arrTemp);
		                       
                		  	    JSONArray arraychildFields = JSONArray.parseArray(jsonObjectField.get("childTableField").toString());
                		  	    List<String []> tempchildFiledsMap=new ArrayList<String []>();
                		  	    for(int k=0,sizek=arraychildFields.size();k<sizek;k++) {
                		  	    	  JSONObject jsonObjectChildField = arraychildFields.getJSONObject(k); 
                		  	    	  String systemChildField=(String) jsonObjectChildField.get("systemField");
	       		                      String fieldChildType=(String) jsonObjectChildField.get("fieldType");
	       		                      String interFaceChildField=(String) jsonObjectChildField.get("interFaceField");
	       		                      String interFaceChildChild=(String) jsonObjectChildField.get("interFaceChild");
	       		                      String [] arrChildTemp=new String [] {systemChildField,fieldChildType,interFaceChildField,interFaceChildChild};
	       		                   tempchildFiledsMap.add(arrChildTemp);
         	                  	}
                		  	  fieldChildMap.put(childTable, tempchildFiledsMap);
	                		  
	                	  }
	                	 
	                  }
	                  fieldMap.put(processCode, tempMapFileds);
	              }
	              logger.info("flowMap==================>"+flowMap);
	              logger.info("fieldMap==================>"+fieldMap);
	          } catch (IOException e) {
	              e.printStackTrace();
	          }

		}
	    private String jsonRead(File file){
	        Scanner scanner = null;
	        StringBuilder buffer = new StringBuilder();
	        try {
	            scanner = new Scanner(file, "utf-8");
	            while (scanner.hasNextLine()) {
	                buffer.append(scanner.nextLine());
	            }
	        } catch (Exception e) {

	        } finally {
	            if (scanner != null) {
	                scanner.close();
	            }
	        }
	        return buffer.toString();
	    }

		public Map<String,String> getFlowInfo(String processInstanceId,String processDefinitionId) {
			Map<String,String> resultMap=new HashMap<String, String>();
		    StringBuffer sql = new StringBuffer();
		    Map paramMap = new HashMap();
		    sql.append("select t.business_key_,t.ext_processcode from ACT_HI_PROCINST t ");
		    sql.append("  WHERE rownum=1 and t.proc_inst_id_=:proc_inst_id_   ");
		    if(StringUtils.isNoneBlank(processDefinitionId)) {
		    	sql.append(" and t.proc_def_id_=:proc_def_id_ ");
		    	 paramMap.put("proc_def_id_", processDefinitionId);
		    }
		    paramMap.put("proc_inst_id_", processInstanceId);
		   
		    List list = super.findByListNativeQuery(sql.toString(), "", paramMap);
		    if ((list != null) && (!list.isEmpty())) {
		      String business_key_ = StringUtils.null2String(((Map)list.get(0)).get("business_key_"));
		      String ext_processcode = StringUtils.null2String(((Map)list.get(0)).get("ext_processcode"));
		      resultMap.put("business_key_", business_key_);
		      resultMap.put("ext_processcode", ext_processcode);
		    }
			return resultMap;
			
		}

		@Override
		protected JpaBaseDao<FlowBuesinessFail, String> getEntityDao() {
			// TODO Auto-generated method stub
			return this.flowBuesinessFailDao;
		}


		/**
		 * 获取业务表数据
		 * @param sql
		 * @param queryMap 
		 * @param interFaceField 
		 * @param searchField 
		 * @param interFaceFieldChilds 
		 * @param queryMap
		 * @param searchFieldTypes  //字段类型   0 文本 1 附件 2 用户 3 组织  4 自定义查询
		 * @param paramsMap  //额外参数
		 * @return
		 */
		public Map<String, Object> getTableDataMap(String sql, String searchField, String interFaceField, String interFaceFieldChilds, Map queryMap, String searchFieldTypes, Map<String, String> paramsMap) {
			logger.info("sql====================>"+sql);
			Map<String,Object> resultMap=new LinkedHashMap<String, Object>();
		    List list = super.findByListNativeQuery(sql.toString(), "", queryMap);
		    if ((list != null) && (!list.isEmpty())) {
		    	String [] searFiledArr=searchField.split(",");
		    	String [] interFaceFieldArr=interFaceField.split(",");
		    	String [] interFaceFieldChildArr=interFaceFieldChilds.split(",");
		    	String [] searchFieldTypeArr=searchFieldTypes.split(",");
		    	Map tempMap=(Map) list.get(0);
		    	for(int i=0,size=interFaceFieldArr.length;i<size;i++) {
		    		paramsMap.put("fieldName", searFiledArr[i]);
		    		//处理接口字段值
		    		String fieldValue=getFiledValue(searchFieldTypeArr[i],  StringUtils.null2String(tempMap.get(searFiledArr[i])),paramsMap );
		    		if(StringUtils.isNotBlank(interFaceFieldChildArr[i])) {
		    			String interFacd=interFaceFieldArr[i];//处理多个字段对应一个值
		    			JSONObject jsonObject=null;
			    			if("JSONArray".equals(interFaceFieldChildArr[i])) {
			    					JSONArray jsonArray=new JSONArray();
			    				  if(fieldValue.indexOf(",")>0) {
			    					 
			    					  String [] arr=fieldValue.split(",");
			    					  for(String value:arr) {
			    						 JSONObject json=new JSONObject();
			    						 json.put("FNumber", value);
			    						 jsonArray.add(json);
			    					  }
			    				  }else {
			    					JSONObject json=new JSONObject();
		    						 json.put("FNumber", fieldValue);
		    						 jsonArray.add(json);
			    				  }
			    				  if(interFacd.indexOf("!@#")>-1) {
					    				String []interFacdArr=interFacd.split("!@#");
					    				for(String str:interFacdArr) {
					    					resultMap.put(str,jsonArray );
					    				}
					    			}else {
					    				resultMap.put(interFaceFieldArr[i],jsonArray );
					    			}
			    			}else {
			    				jsonObject=JSONObject.parseObject("{\""+interFaceFieldChildArr[i]+"\":\""+fieldValue+"\"}");
			    				if(interFacd.indexOf("!@#")>-1) {
				    				String []interFacdArr=interFacd.split("!@#");
				    				for(String str:interFacdArr) {
				    					resultMap.put(str,jsonObject );
				    				}
				    			}else {
				    				resultMap.put(interFaceFieldArr[i],jsonObject );
				    			}
			    			}
		    			
		    			
		    			//resultMap.put(interFaceFieldArr[i], "{\""+interFaceFieldChildArr[i]+"\":\""+ StringUtils.null2String(tempMap.get(searFiledArr[i]))+"\"}");
		    		}else {
		    			//resultMap.put(interFaceFieldArr[i],  StringUtils.null2String(tempMap.get(searFiledArr[i])));
		    			String interFacd=interFaceFieldArr[i];//处理多个字段对应一个值
		    			if(interFacd.indexOf("!@#")>-1) {
		    				String []interFacdArr=interFacd.split("!@#");
		    				for(String str:interFacdArr) {
		    					resultMap.put(str,fieldValue );
		    				}
		    			}else {
		    				resultMap.put(interFaceFieldArr[i],fieldValue );
		    			}
		    		}
				      
		    	}
		      
		    }
			return resultMap;
		}
		
		/**
		 * 
		 * 获取子表数据
		 * @param tableName
		 * @param childFieldList
		 * @param business_key_
		 * @param maintableName 主表名称
		 * @return
		 */
		public Object getChildTableData(String tableName, List<String[]> childFieldList, String business_key_, String maintableName) {
			
			 List< Map<String,Object>> listTemp = new ArrayList< Map<String,Object>>();
			 String searchField="";//主表数据查询字段
			 String searchFieldTypes="";//主表数据字段类型
			 String interFaceField="";//主表数据接口字段名
			 String interFaceChilds="";
			 if(childFieldList!=null&&childFieldList.size()>0) {
 				for(int i=0,size=childFieldList.size();i<size;i++) {
 					String [] obj=childFieldList.get(i);
 						searchField+=obj[0]+",";
    					searchFieldTypes+=obj[1]+",";
    					interFaceField+=obj[2]+",";
    					interFaceChilds+=obj[3]+",";
 				}
 				logger.info("searchField====================>"+searchField);
 				logger.info("interFaceField====================>"+interFaceField);
 				logger.info("interFaceChilds====================>"+interFaceChilds);
 				searchField=trimBothEndsChars(searchField,",");
 				searchFieldTypes=trimBothEndsChars(searchFieldTypes,",");
 				interFaceField=trimBothEndsChars(interFaceField,",");
 				interFaceChilds=trimBothEndsChars(interFaceChilds,",");
 				//业务表数据查询sql
 				String sql="select "+searchField+" from " +tableName+ " where fkid =:id ";
 				if("jdclqd".equals(tableName)) {
 					//交底材料第一条数据中主数据中获取
 					sql=" select '005' as clmc  ,xmhtsfyj as sfyjwc ,xmhtjsr as jsr ,xmhtbz as bz  from htjdsjb  where id=:id union all " +sql;
 				}else if("zclwzsqzb".equalsIgnoreCase(tableName)){
					sql += " and zcly !='1' ";
				}
 				
 				Map queryMap=new HashMap();
 				Map<String,String> paramsMap=new HashMap<String,String>();
 				paramsMap.put("tableName", tableName);
 				paramsMap.put("recordId", business_key_);
 				paramsMap.put("maintableName", maintableName);
 				
 				queryMap.put("id", business_key_);
 				logger.info("sql====================>"+sql);
 				logger.info("searchField====================>"+searchField);
 				logger.info("interFaceField====================>"+interFaceField);
 				logger.info("interFaceChilds====================>"+interFaceChilds);
 			    List list = super.findByListNativeQuery(sql.toString(), "", queryMap);
 			    if ((list != null) && (!list.isEmpty())) {
 			    	String [] searFiledArr=searchField.split(",");
 			    	String [] interFaceFieldArr=interFaceField.split(",");
 			    	String [] interFaceFieldChildArr=interFaceChilds.split(",");
 			    	String [] searchFieldTypeArr=searchFieldTypes.split(",");
 			    	
 			    	for(int k=0,sizeList=list.size();k<sizeList;k++) {
 			    		Map tempMap=(Map) list.get(k);
 			    		Map<String,Object> resultMap=new LinkedHashMap<String, Object>();
 			    		for(int i=0,size=interFaceFieldArr.length;i<size;i++) {
 			    			String interFacd=interFaceFieldArr[i];//处理多个字段对应一个值
 			    			paramsMap.put("fieldName", searFiledArr[i]);
 	 			    		if(StringUtils.isNotBlank(interFaceFieldChildArr[i])) {
 	 			    			String fieldValue=getFiledValue(searchFieldTypeArr[i],  StringUtils.null2String(tempMap.get(searFiledArr[i])), paramsMap);
 	 			    			JSONObject jsonObject=null;

 				    			if("JSONArray".equals(interFaceFieldChildArr[i])) {
 				    					JSONArray jsonArray=new JSONArray();
 				    				  if(fieldValue.indexOf(",")>0) {
 				    					 
 				    					  String [] arr=fieldValue.split(",");
 				    					  for(String value:arr) {
 				    						 JSONObject json=new JSONObject();
 				    						 json.put("FNumber", value);
 				    						 jsonArray.add(json);
 				    					  }
 				    				  }else {
 				    					JSONObject json=new JSONObject();
 			    						 json.put("FNumber", fieldValue);
 			    						 jsonArray.add(json);
 				    				  }
 				    				  if(interFacd.indexOf("!@#")>-1) {
 						    				String []interFacdArr=interFacd.split("!@#");
 						    				for(String str:interFacdArr) {
 						    					resultMap.put(str,jsonArray);
 						    				}
 						    			}else {
 						    				resultMap.put(interFaceFieldArr[i],jsonArray );
 						    			}
 				    			}else {
 				    				jsonObject=JSONObject.parseObject("{\""+interFaceFieldChildArr[i]+"\":\""+fieldValue+"\"}");
 				    				if(interFacd.indexOf("!@#")>-1) {
 					    				String []interFacdArr=interFacd.split("!@#");
 					    				for(String str:interFacdArr) {
 					    					resultMap.put(str,jsonObject );
 					    				}
 					    			}else {
 					    				resultMap.put(interFaceFieldArr[i],jsonObject );
 					    			}
 				    			}
 	 			    		}else {
 	 			    			//resultMap.put(interFaceFieldArr[i],  StringUtils.null2String(tempMap.get(searFiledArr[i])));
 	 			    			String fieldValue=getFiledValue(searchFieldTypeArr[i], StringUtils.null2String(tempMap.get(searFiledArr[i])), paramsMap);
 	 			    			if(interFacd.indexOf("!@#")>-1) {
 	 			    				String []interFacdArr=interFacd.split("!@#");
 	 			    				for(String str:interFacdArr) {
 	 			    					resultMap.put(str,fieldValue );
 	 			    				}
 	 			    			}else {
 	 			    				resultMap.put(interFaceFieldArr[i],fieldValue );
 	 			    			}
 	 			    		}
 	 					      
 	 			    	}
 			    		
						listTemp.add(resultMap);
 			    	}
 			    }
			 }
			//String childJson= JSONObject.toJSONString(listTemp);
			//logger.info("childJson==============>"+childJson);
			return listTemp;
		}

	/**
	 * 商机立项  复合子表金额信息
	 * @param originJson
	 * @param fkid
	 * @param processCode
	 * @return
	 */
	public  JSONObject renderJsonDataSJLX(JSONObject originJson,String fkid ,String processCode){
		JSONObject newJson = originJson;
		if(newJson!=null){
			String sql = " select yskmbm,sum(qnjyqnd) qnjyqnds,sum(dnfy) dnfys,sum(mnjyhn) mnjyhns,sum(ywcbfyxj) ywcbfyxjs from ysyfyzb where fkid='"+ fkid +"' group by yskmbm ";
			List list = jdbcTemplate.queryForList(sql);

			if(list.size()>0){
				JSONArray jArr = new JSONArray();

				for(int i=0;i<list.size();i++){
					Map<String,Object> map = (Map<String,Object>)list.get(i);
					if(map!=null){
						JSONObject jsonTemp = new JSONObject(true);

						String yskmbm = map.get("yskmbm")+"";
						String qnjyqnds = map.get("qnjyqnds")+"";
						String dnfys = map.get("dnfys")+"";
						String mnjyhns = map.get("mnjyhns")+"";
						String ywcbfyxjs = map.get("ywcbfyxjs")+"";


						jsonTemp.put("F_KDXF_FYLB1", JSONObject.parseObject("{\"FNUMBER\":\""+yskmbm+"\"}"));
						jsonTemp.put("F_KDXF_QNJYQ",qnjyqnds);
						jsonTemp.put("F_KDXF_DNFY",dnfys);
						jsonTemp.put("F_KDXF_MNJYH",mnjyhns);
						jsonTemp.put("F_KDXF_YWCBFY",ywcbfyxjs);

						jArr.add(jsonTemp);
					}

				}

				JSONObject jModel = JSONObject.parseObject(newJson.get("Model")+"");
				jModel.put("F_KDXF_SJLXEntity",jArr);
				newJson.put("Model",jModel);
			}
		}

		return newJson;
	}


	/**
	 * 商机变更  复合子表金额信息
	 * @param originJson
	 * @param fkid
	 * @param processCode
	 * @return
	 */
	public  JSONObject renderJsonDataSJBG(JSONObject originJson,String fkid ,String processCode){
		JSONObject newJson = originJson;
		if(newJson!=null){
			String sql = " select yskmbm,sum(dqys) dqyss,sum(kyys) kyyss,sum(bctz) bctzs,sum(tzhys) tzhyss from ysyfybgzb where fkid='"+ fkid +"' group by yskmbm ";
			List list = jdbcTemplate.queryForList(sql);

			if(list.size()>0){
				JSONArray jArr = new JSONArray();

				for(int i=0;i<list.size();i++){
					Map<String,Object> map = (Map<String,Object>)list.get(i);
					if(map!=null){
						JSONObject jsonTemp = new JSONObject(true);

						String yskmbm = map.get("yskmbm")+"";
						String dqyss = map.get("dqyss")+"";
						String kyyss = map.get("kyyss")+"";
						String bctzs = map.get("bctzs")+"";
						String tzhyss = map.get("tzhyss")+"";


						jsonTemp.put("F_KDXF_FYLB1", JSONObject.parseObject("{\"FNUMBER\":\""+yskmbm+"\"}"));
						jsonTemp.put("F_KDXF_DQYS",dqyss);
						jsonTemp.put("F_KDXF_KYYS",kyyss);
						jsonTemp.put("F_KDXF_BCTZ",bctzs);
						jsonTemp.put("F_KDXF_TZHYS",tzhyss);

						jArr.add(jsonTemp);
					}

				}

				JSONObject jModel = JSONObject.parseObject(newJson.get("Model")+"");
				jModel.put("F_KDXF_SJBGEntity",jArr);
				newJson.put("Model",jModel);
			}
		}

		return newJson;
	}


		public boolean dealERPActivity(String processInstanceId,String processDefinitionId,boolean IsAutoSubmitAndAudit ) {
			System.out.println("jinxh============jinxh===============jinxh====infoXueyang");
			boolean returnFlag=false;
			fileList=new ArrayList<FlowBuesinessAttach>();
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("processDefinitionId", processDefinitionId);
			paramsMap.put("processInstanceId", processInstanceId);

			// 根据 processInstanceId 获取 流程code 及业务表数据主键
			Map<String, String> flowInfoMap = getFlowInfo(processInstanceId, processDefinitionId);

			logger.info("flowInfoMap==============>" + flowInfoMap);
			// systemFlowService.dealFlow(flowInfoMap);
			// 流程定义Map
			Map<String, Map<String, String>> flowMap = SystemFlowService.getFlowMap();
			if (flowInfoMap != null) {
				String ext_processcode = flowInfoMap.get("ext_processcode");
				String business_key_ = flowInfoMap.get("business_key_");
				FlowBuesinessFail fail = new FlowBuesinessFail();
				if (flowMap.get(ext_processcode) != null) {
					// 获取各个流程业务代办
					Map<String, String> tempMap = flowMap.get(ext_processcode);
					String tableName = tempMap.get("tableName");
					String className = tempMap.get("className");
					String classMethod = tempMap.get("classMethod");
					String formid = tempMap.get("formid");
					String dataTpye = tempMap.get("dataTpye");
					String processCode=tempMap.get("processCode");
					List<JSONObject> requestList=new ArrayList<JSONObject>();
					if("wlbmsq".equals(processCode)) {
						//物料编码申请流程
						// 根据 processInstanceId 获取 流程code 及业务表数据主键
						requestList=materialService.getMaterialList(business_key_);
					}else if("gysxzbgsq".equals(processCode)){
						//供应商新增/变更
						requestList=materialService.getSupplierList(business_key_);
						fileList=materialService.getFileList();
					}else if("rlxzlbx".equals(processCode)||"cglbx".equals(processCode)) {
						//人力行政类报销 及采购类报销
						requestList=financialService.getRLXZBXList(business_key_, tableName);
						fileList=financialService.getFileList();
					}else if("ygbx".equals(processCode)) {
						//员工报销
						requestList=financialService.getAmountList(business_key_);
						fileList=financialService.getFileList();
					}else if("jksq".equals(processCode)) {
						//借款申请
						requestList=financialService.getJKFlowList(business_key_);
						fileList=financialService.getFileList();
					}else if("ZCDBSQ".equals(processCode)||"ZCTKSQ".equals(processCode)){
						//根据业务表主键获取资产卡片编码
						//调拨退库分开
						if("ZCDBSQ".equals(processCode)){
							requestList=assetsService.getZCDBList(business_key_, tableName);
							fileList=assetsService.getFileList();
						}else{
							requestList=assetsService.getZCTKList(business_key_, tableName);
							fileList=assetsService.getFileList();
						}

					}else if("fylwzsq".equals(processCode)||"JDXQSQ".equals(processCode)){
						//费用类物资申请 和 接待需求申请流程
						requestList=assetsService.getFYWZSQList(business_key_,tableName);
						System.out.println("requestList======size()====>>>>"+requestList.size());
						fileList=assetsService.getFileList();
					}else if("XHTKSQ".equals(processCode)){
						//现货退库申请
						requestList=assetsService.getXHTKQList(business_key_);
						fileList=assetsService.getFileList();
					}else {
						List<Object> Parameters = dealTableData(processInstanceId, processDefinitionId);
						Parameters.remove(0);
						JSONObject  jsonData=(JSONObject) Parameters.get(1);

						//商机结项修改jsondata
						if("sjjxsplc".equalsIgnoreCase(processCode)){
							System.out.println("jsonData=1111=====>>>"+jsonData);
							String tempSj = "";
							String sqlGetUser = " select fnumber from xfsmzjk.t_kdxf_staff@toerp where fusername in (select login_name from sys_user where id in (select createdby from sjjxsjb ) and id = '"+business_key_+"') ";
							List listGetUser = jdbcTemplate.queryForList(sqlGetUser);
							if(listGetUser.size()>0){
								Map<String,Object> mapGetUser = (Map<String,Object>)listGetUser.get(0);
								tempSj = mapGetUser.get("fnumber")+"";
							}

							if(!"".equalsIgnoreCase(tempSj)){
								jsonData.put("F_KDXF_SQR","{\"FSTAFFNUMBER\": \""+tempSj+"\"}");
							}
						}

						//商机立项和结项合并子表
						//jinxhCreate
						System.out.println(processCode);
						//商机项目立项
						if("sjxmlx".equals(processCode)){

							jsonData = renderJsonDataSJLX(jsonData,business_key_,processCode);

						}

						//商机项目变更
						if("sjxmbg".equals(processCode)){

							jsonData = renderJsonDataSJBG(jsonData,business_key_,processCode);

						}

						requestList.add(jsonData);
					}

					String data ="";
					fail.setFormid(formid);
					fail.setProcessInstanceId(processInstanceId);
					
					try {
						long startTime = System.currentTimeMillis();
						if (Login()) {
							String url = saveUrl;
							if ("pushStatus".equals(dataTpye)) {
								fail.setKingdeeUrl(shUrl);
								url = shUrl;
							} else {
								fail.setKingdeeUrl(saveUrl);
								url = saveUrl;
							}
							ResponseEntity<String> result = null;
							//设置请求头
							HttpHeaders headers = new HttpHeaders();
					        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
					        headers.setContentType(type);
					        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
					        
					      //更新财务报销流程编码及费用类物资申请
							if("fylwzsq".equals(processCode)||"jksq".equals(processCode) || "rlxzlbx".equals(processCode) || "cglbx".equals(processCode) || "ygbx".equals(processCode)) {
								String erpdjbh="";
								if("fylwzsq".equals(processCode)) {
									 erpdjbh=assetsService.getErpdjbh();
									 System.out.println("erpdjbh===========>>>>"+erpdjbh);
								}else {
									 erpdjbh=financialService.getErpdjbh();
								}
								if(org.apache.commons.lang3.StringUtils.isNotEmpty(erpdjbh)) {
									//删除临时数据
									JSONArray arr=new JSONArray();
									arr.add(erpdjbh);
									JSONObject json=new JSONObject(true);
									json.put("Numbers", arr);
									Map<String, Object> applicationDataMap = new HashMap<String, Object>();
									applicationDataMap.put("formid", formid);
									applicationDataMap.put("data",json);
									String jsonString = JSONObject.toJSONString(applicationDataMap);
									fail.setData(jsonString);
									HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);
									result = httpClientTemplate.postForEntity(deleteUrl, formEntity, String.class);
									
								}
							}else if (requestList!=null&&requestList.size()>0&&"sjxmlx".equals(processCode)) {
								// 商机项目立项时 先推送商机项目信息表单
								String sjformId = "KDXF_SJXM";
								Map<String, Object> sjapplicationDataMap = new HashMap<String, Object>();
								sjapplicationDataMap.put("formid", sjformId);
								JSONObject jsonData=requestList.get(0);
								//根据record获取产品线及产品编码
								JSONArray cpxJsonArr=getCPXInfo(business_key_);
								
								//12
								JSONObject model=jsonData.getJSONObject("Model");
								model.put("F_KDXF_CPXEntity", cpxJsonArr);
								model=shangjiService.getShangjiLixiang(model);
								jsonData.put("Model", model);
								jsonData.put("IsAutoSubmitAndAudit", IsAutoSubmitAndAudit);
								
								sjapplicationDataMap.put("data", jsonData);
								
								String jsonStringsj = JSONObject.toJSONString(sjapplicationDataMap);
								HttpEntity<String> formEntity = new HttpEntity<String>(jsonStringsj, headers);

								result = httpClientTemplate.postForEntity(url, formEntity, String.class);
								JSONObject json = JSONObject.parseObject(result.getBody().toString());
								JSONObject ResponseStatus = json.getJSONObject("Result")
										.getJSONObject("ResponseStatus");
								String IsSuccess = ResponseStatus.getString("IsSuccess");
								if("true".equals(IsSuccess)) {
									JSONArray SuccessEntitys=ResponseStatus.getJSONArray("SuccessEntitys");
									JSONObject resObj=SuccessEntitys.getJSONObject(0);
									String finterid=resObj.getString("Id");
									String fbillno=resObj.getString("Number");
									
									try {
										//更新财务报销流程编码
										if("sjxmlx".equals(processCode)) {
											String sql="update "+tableName+" set erpfid=?, sjxmbm=?  where id=?";
											jdbcTemplate.update(sql,finterid,fbillno,business_key_);
										    
										  //重新获取 商机项目的数据
										    List<Object> Parameters = dealTableData(processInstanceId, processDefinitionId);
											//移除表单推送类型
											Parameters.remove(0);
											JSONObject  jsonData1=(JSONObject) Parameters.get(1);
											JSONObject model1=jsonData1.getJSONObject("Model");
											model1.put("F_KDXF_CPXEntity", cpxJsonArr);
											jsonData1.put("Model", model1);
											
											requestList=new  ArrayList<JSONObject>();
											requestList.add(jsonData1);
										}
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("报错了啊===================？");
										System.out.println(e.getMessage());
									}
									
								}
								
								
							}

					        if(requestList!=null&&requestList.size()>0) {
					        	for(JSONObject jsonData:requestList ) {
					        		Map<String, Object> applicationDataMap = new HashMap<String, Object>();
									//业务数据
					        		String returnTableName=jsonData.getString("tableName");
					        		String returnRecordId=jsonData.getString("recordId");
									data= JSONObject.toJSONString(jsonData);
									
									applicationDataMap.put("formid", formid);
									applicationDataMap.put("data",jsonData);

									if("fylwzsq".equals(processCode) || "JDXQSQ".equals(processCode)){
										IsAutoSubmitAndAudit = true;
									}

									jsonData.put("IsAutoSubmitAndAudit", IsAutoSubmitAndAudit);
									if("cghtsp".equals(processCode)) {
										//采购合同审批 审核前 调用保存接口回写合同编码
										KingdeeApplicationData	appData = new KingdeeApplicationData(new String [] {"F_KDXF_HTBH"},"true", "false", "true", "true",
												"true", JSONObject.parseObject(data, Feature.OrderedField),tableName,business_key_);
										JSONObject tempJson= (JSONObject) JSONObject.toJSON(appData);
										Map<String, Object> tempapplicationDataMap = new HashMap<String, Object>();
										tempapplicationDataMap.put("formid", formid);
										tempapplicationDataMap.put("data",tempJson);
										String jsonStringtemp = JSONObject.toJSONString(tempapplicationDataMap);
										HttpEntity<String> formEntity = new HttpEntity<String>(jsonStringtemp, headers);

										System.out.print("jsonStringtemp:=====>>jsonStringtemp=====>>>" + jsonStringtemp);
										result = httpClientTemplate.postForEntity(saveUrl, formEntity, String.class);
										System.out.println("result======>"+result);	
										
									}

									String jsonString = JSONObject.toJSONString(applicationDataMap);
									
									fail.setData(jsonString);

									System.out.println("jsonString==jsonString=>>>====>>>>"+jsonString);
									System.out.println("urlinfo====>>>>"+url);
							        HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);
									
									result = httpClientTemplate.postForEntity(url, formEntity, String.class);
									JSONObject json = JSONObject.parseObject(result.getBody().toString());
									JSONObject ResponseStatus= json.getJSONObject("Result").getJSONObject("ResponseStatus");
									String IsSuccess =ResponseStatus.getString("IsSuccess");
									
									if (!"true".equals(IsSuccess)) {
										// 接口调用失败保存记录及失败原因
										fail.setFlowBId("0");
										fail.setResponseJson(result.getBody().toString());
										//保存附件
										if(fileList!=null&&fileList.size()>0) {
											for(int i=0,size=fileList.size();i<size;i++) {
												FlowBuesinessAttach  attach=fileList.get(i);
												attach.setProcessInstanceId(processInstanceId);
												attach.setFormid(formid);
												String cPath =attach.getFilePath();
												attach.setFilePath(cPath);
												flowBuesinessAttachDao.save(attach);
											}
										}
										save(fail);
									}else {
										JSONArray SuccessEntitys=ResponseStatus.getJSONArray("SuccessEntitys");
										JSONObject resObj=SuccessEntitys.getJSONObject(0);
										String finterid=resObj.getString("Id");
										String fbillno=resObj.getString("Number");
										uploadAttachFile(formid, finterid, fbillno, processInstanceId,true, fileList);
										try {
											//更新物料编码
											//jinxh
											System.out.println("returnTableName===========>>>>"+returnTableName);
											System.out.println("fbillno==========>>>"+fbillno);
											System.out.println("business_key_========>>>"+business_key_);
											System.out.println("returnRecordId===========>>>"+returnRecordId);
											if("wlbmsqzb".equals(returnTableName)) {
												/*String sql="update "+returnTableName+" set wlbm='"+fbillno+"'  where id=?1";
												Query query = this.entityManager.createNativeQuery(sql).setParameter(1, returnRecordId);
											    query.executeUpdate();*/
												String sql="update "+returnTableName+" set wlbm=? where id=?";
												jdbcTemplate.update(sql,fbillno,returnRecordId);
												//jdbcTemplate.update(sql,fbillno,business_key_);
											}
											//更新客户编码
											if("khxz".equals(returnTableName)) {
												/*String sql="update "+returnTableName+" set khbm='"+fbillno+"'  where id=?1";
												Query query = this.entityManager.createNativeQuery(sql).setParameter(1, returnRecordId);
											    query.executeUpdate();*/
												String sql="update "+tableName+" set khbm=? where id=?";
												jdbcTemplate.update(sql,fbillno,business_key_);
											}
											
											//更新财务报销流程编码
											if("fylwzsq".equals(processCode)||"rlxzlbx".equals(processCode)||"cglbx".equals(processCode)||"ygbx".equals(processCode)||"jksq".equals(processCode)) {
												/*String sql="update "+tableName+" set erpdjbh='"+fbillno+"'  where id=?1";
												Query query = this.entityManager.createNativeQuery(sql).setParameter(1, business_key_);
											    query.executeUpdate();*/
												String sql="update "+tableName+" set erpdjbh=? where id=?";
												jdbcTemplate.update(sql,fbillno,business_key_);
											}
										} catch (Exception e) {
											// TODO: handle exception
											System.out.println("报错了啊===================？");
											System.out.println(e.getMessage());
										}
										
									}
									returnFlag=true;
					        	}
					        }
							
							logger.info("接口返回结果==============>" + result.getBody().toString());
						}
						logger.info("接口调用时间==============>" + (System.currentTimeMillis() - startTime));

					} catch (Exception e) {
						logger.error("调用业务系统的方法失败", e);
						e.printStackTrace();
						fail.setResponseJson(e.getMessage());
						save(fail);

					}
					if (StringUtils.isNotEmpty(className)) {
						// 处理流程特殊业务
						try {
							Class<?> clazz = Class.forName(className);
							Method method = clazz.getMethod(classMethod, new Class[] { DelegateTask.class, Map.class });
							method.invoke(clazz.newInstance(), new Object[] { processInstanceId, data });
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}

			logger.info("开始调用业务系统接口================》" + paramsMap + ",业务参数:" + paramsMap);
			return returnFlag;
		}


	/**
		 * 上传附件
		 * @param formid
		 * @param finterid
		 * @param fbillno
		 * @param processInstanceId
		 * @param fileList
		 * @throws UnsupportedEncodingException
		 */
		public  void uploadAttachFile(String formid,String finterid ,String fbillno ,String processInstanceId ,boolean isSave,List<FlowBuesinessAttach> fileList) {
			//处理附件
			if(fileList!=null&&fileList.size()>0) {
				for(int i=0,size=fileList.size();i<size;i++) {
					FlowBuesinessAttach  attach=fileList.get(i);
					String fileName=attach.getFileName();
					String fileSaveName=attach.getFileSaveName();
					String extType=attach.getExtType();
					String cPath=attach.getFilePath();
					String content_type=attach.getContent_type();
					
					if(!isSave) {
						//只处理附件上传失败，单据推送成功的
						formid=attach.getFormid();
						finterid=attach.getFinterid();
						fbillno=attach.getFbillno();
					}
					JSONObject fileJson=null;
					try {
						fileJson = UploadToWebSite(fileName,fileSaveName, "", userToken, true, cPath,content_type);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
					JSONObject upload2AttachmentResult=fileJson.getJSONObject("Upload2AttachmentResult");
					if(upload2AttachmentResult!=null&&upload2AttachmentResult.getBooleanValue("Success")) { 
						String fileId = upload2AttachmentResult.getString("FileId");
						String FileName=upload2AttachmentResult.getString("FileName");
						String FileSize=upload2AttachmentResult.getString("FileSize");
						saveBillData(formid, finterid, fbillno,fileId,FileName, extType, FileSize) ;
						//{"Upload2AttachmentResult":{"Message":"","Success":true,"FileId":"d823d57e528941adb36c6d2dd975e780","FileName":"12345.docx","FileSize":66473,"Url":""}}
					}else{
						//表示
						if(isSave) {
							//附件上传失败后 
							attach.setProcessInstanceId(processInstanceId);
							attach.setFilePath(cPath);
							attach.setFormid(formid);
							attach.setFinterid(finterid);
							attach.setFbillno(fbillno);
							attach.setResponseJson(fileJson.toString());
							flowBuesinessAttachDao.save(attach);
						}
						
					}
					
					
				}
			}
			
		}

		/**
		 * 处理金蝶退回业务
		 * 
		 * @param paramMap
		 * @return
		 */
	public boolean backKingdeeService(Map<String, String> paramMap) {
		boolean returnFlag = false;
		String processDefinitionId = paramMap.get("processDefinitionId");
		String processInstanceId = paramMap.get("processInstanceId");

		Map<String, String> flowInfoMap = getFlowInfo(processInstanceId, processDefinitionId);
		logger.info("flowInfoMap==============>" + flowInfoMap);
		// 流程定义Map
		Map<String, Map<String, String>> flowMap = getFlowMap();
		Map<String, List<String[]>> fieldMap = getFieldMap();
		// 获取子表Map
		Map<String, List<String[]>> fieldChildMap = getFieldChildMap();
		if (flowInfoMap != null) {
			String processCode = flowInfoMap.get("ext_processcode");
			String business_key_ = flowInfoMap.get("business_key_");

			List<JSONObject> requestList = null;
			if (flowMap.get(processCode) != null) {

				if ("rlxzlbx".equals(processCode) || "cglbx".equals(processCode)) {
					// 人力行政类报销 及采购类报销
					String tableName = "rlxzlbx";
					if ("cglbx".equals(processCode)) {
						tableName = "dgcglbx";
					}
					financialService.getRLXZBXList(business_key_, tableName);
				} else if ("ygbx".equals(processCode)) {
					// 员工报销
					financialService.getAmountList(business_key_);
				} else if ("jksq".equals(processCode)) {
					// 借款申请
					financialService.getJKFlowList(business_key_);
				}else if("fylwzsq".equals(processCode)) {
					//费用类物资申请
					requestList = assetsService.getFYWZSQList(business_key_,"fylwzsqb");
				}else if("JDXQSQ".equals(processCode)) {
					//接待需求申请
					requestList = assetsService.getFYWZSQList(business_key_,"jdxqsqb");
				}

			}

			FlowBuesinessFail fail = new FlowBuesinessFail();
			try {
				List<Object> Parameters = dealTableData(processInstanceId, processDefinitionId);

				if (Parameters != null && Parameters.size() > 0) {
					String dataTpye = (String) Parameters.get(0);
					String formid = (String) Parameters.get(1);
					long startTime = System.currentTimeMillis();
					String url = "";
					// 添加失败记录
					fail.setFormid(formid);
					fail.setProcessInstanceId(processInstanceId);

					if ("pushStatus".equals(dataTpye)) {
						fail.setKingdeeUrl(fshUrl);
						url = fshUrl;
					} else if ("rlxzlbx".equals(processCode) || "cglbx".equals(processCode)
							|| "ygbx".equals(processCode) || "jksq".equals(processCode)||"fylwzsq".equals(processCode)) {
						url = deleteUrl;
					} else {
						return returnFlag;
					}
					if (Login()) {
						ResponseEntity<String> result = null;
						Parameters.remove(0);
						//设置请求头
						HttpHeaders headers = new HttpHeaders();
				        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
				        headers.setContentType(type);
				        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
						// 更新财务报销流程编码
						if ("rlxzlbx".equals(processCode) || "cglbx".equals(processCode) || "ygbx".equals(processCode)
								|| "jksq".equals(processCode)||"fylwzsq".equals(processCode)) {
							String erpdjbh = "";
							
							if("fylwzsq".equals(processCode)) {
								erpdjbh = assetsService.getErpdjbh();
							}else {
								erpdjbh = financialService.getErpdjbh();
							}
							if (org.apache.commons.lang3.StringUtils.isNotEmpty(erpdjbh)) {
								// 删除临时数据
								JSONArray arr = new JSONArray();
								arr.add(erpdjbh);
								JSONObject json = new JSONObject(true);
								json.put("Numbers", arr);
								Map<String, Object> applicationDataMap = new HashMap<String, Object>();
								applicationDataMap.put("formid", formid);
								applicationDataMap.put("data", json);
								String jsonString = JSONObject.toJSONString(applicationDataMap);
								fail.setData(jsonString);

								System.out.println("jsonStringBack1111====>>>" + jsonString);

								HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);
								result = httpClientTemplate.postForEntity(deleteUrl, formEntity, String.class);


								//如果费用类报销，再次调用接口消减库存改变
								if("fylwzsq".equals(processCode) || "JDXQSQ".equals(processCode)){
									if(requestList!=null&&requestList.size()>0) {
										for(JSONObject jsonData:requestList ) {
											applicationDataMap = new HashMap<String, Object>();
											//业务数据
											String returnTableName=jsonData.getString("tableName");
											String returnRecordId=jsonData.getString("recordId");
											String data= JSONObject.toJSONString(jsonData);

											applicationDataMap.put("formid", formid);
											applicationDataMap.put("data",jsonData);

											boolean IsAutoSubmitAndAudit = true;

											jsonData.put("IsAutoSubmitAndAudit", IsAutoSubmitAndAudit);
											jsonString = JSONObject.toJSONString(applicationDataMap);
											//替换退回
											//临时处理，这里不是我处理的
											jsonString = jsonString.replace("GENERAL","RETURN");

											fail.setData(jsonString);

											System.out.println("jsonStringback==jsonStringback=>>>====>>>>"+jsonString);
											formEntity = new HttpEntity<String>(jsonString, headers);

											//url = shUrl;
											url = saveUrl;
											result = httpClientTemplate.postForEntity(url, formEntity, String.class);
											json = JSONObject.parseObject(result.getBody().toString());
											JSONObject ResponseStatus= json.getJSONObject("Result").getJSONObject("ResponseStatus");
											String IsSuccess =ResponseStatus.getString("IsSuccess");

											System.out.println("ResponseStatusBack======>>>"+ResponseStatus.toString());

											if (!"true".equals(IsSuccess)) {
												// 接口调用失败保存记录及失败原因
												fail.setFlowBId("0");
												fail.setResponseJson(result.getBody().toString());
												//保存附件
												if(fileList!=null&&fileList.size()>0) {
													for(int i=0,size=fileList.size();i<size;i++) {
														FlowBuesinessAttach  attach=fileList.get(i);
														attach.setProcessInstanceId(processInstanceId);
														attach.setFormid(formid);
														String cPath =attach.getFilePath();
														attach.setFilePath(cPath);
														flowBuesinessAttachDao.save(attach);
													}
												}
												save(fail);
											}else {
												JSONArray SuccessEntitys=ResponseStatus.getJSONArray("SuccessEntitys");
												JSONObject resObj=SuccessEntitys.getJSONObject(0);
												String finterid=resObj.getString("Id");
												String fbillno=resObj.getString("Number");
												uploadAttachFile(formid, finterid, fbillno, processInstanceId,true, fileList);

											}
											returnFlag=true;
										}
									}
								}

								//小件
							}
						} else {
							Map<String, Object> applicationDataMap = new HashMap<String, Object>();
							applicationDataMap.put("formid", Parameters.get(0));
							applicationDataMap.put("data", Parameters.get(1));
							String jsonString = JSONObject.toJSONString(applicationDataMap);


							fail.setData(jsonString);
							HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);
							result = httpClientTemplate.postForEntity(url, formEntity, String.class);
							JSONObject json = JSONObject.parseObject(result.getBody().toString());
							JSONObject ResponseStatus = json.getJSONObject("Result").getJSONObject("ResponseStatus");
							String IsSuccess = ResponseStatus.getString("IsSuccess");
							if (!"true".equals(IsSuccess)) {
								// 接口调用失败保存记录及失败原因
								fail.setResponseJson(result.getBody().toString());
								save(fail);
							}
						}
						returnFlag = true;
						logger.info("接口返回结果==============>" + result.getBody().toString());
					}
					logger.info("接口调用时间==============>" + (System.currentTimeMillis() - startTime));
				}

			} catch (Exception e) {
				logger.error("调用业务系统的方法失败", e);
				save(fail);
			}
		}

		return returnFlag;
	}

		/**
		 * 处理业务表数据
		 * 
		 * @return
		 */
		public List<Object> dealTableData(String processInstanceId, String processDefinitionId) {
			List<Object> Parameters = new ArrayList<Object>();
			// 根据 processInstanceId 获取 流程code 及业务表数据主键
			Map<String, String> flowInfoMap = getFlowInfo(processInstanceId, processDefinitionId);
			logger.info("flowInfoMap==============>" + flowInfoMap);
			// dealFlow(flowInfoMap);
			// 流程定义Map
			Map<String, Map<String, String>> flowMap = getFlowMap();
			Map<String, List<String[]>> fieldMap = getFieldMap();
			// 获取子表Map
			Map<String, List<String[]>> fieldChildMap = getFieldChildMap();
			if (flowInfoMap != null) {
				String ext_processcode = flowInfoMap.get("ext_processcode");
				String business_key_ = flowInfoMap.get("business_key_");
				if (flowMap.get(ext_processcode) != null) {
					// 获取各个流程业务代办
					Map<String, String> tempMap = flowMap.get(ext_processcode);
					String tableName = tempMap.get("tableName");
					String className = tempMap.get("className");
					String classMethod = tempMap.get("classMethod");
					String formid = tempMap.get("formid");
					String dataTpye = tempMap.get("dataTpye");
					String getDataMethod=tempMap.get("getDataMethod");
					Parameters.add(dataTpye);
					Parameters.add(formid);
					Map<String, Object> tableData = new LinkedHashMap<String, Object>();
					Map<String, Object> childtableData = new LinkedHashMap<String, Object>();
					if (StringUtils.isNotEmpty(tableName)) {
						// 获取业务表数据
						List<String[]> fieldList = fieldMap.get(ext_processcode);
						String searchField = "";// 主表数据查询字段
						String searchFieldTypes = "";// 主表数据字段类型
						String interFaceField = "";// 主表数据接口字段名
						String interFaceFieldChilds = "";// 主数据接口字段名子节点
						String childTables = "";// 子表名称
						String childTableInterFaceFields = "";// 子表接口字段名
						if (fieldList != null && fieldList.size() > 0) {
							for (int i = 0, size = fieldList.size(); i < size; i++) {
								String[] obj = fieldList.get(i);
								if ("table".equals(obj[1])) {
									// 子表数据
									childTables += obj[0] + ",";
									childTableInterFaceFields = obj[2] + ",";
									List<String[]> childFieldList = fieldChildMap.get(obj[0]);
									childtableData.put(obj[2],getChildTableData(obj[0], childFieldList, business_key_,tableName));

								} else {
									searchField += obj[0] + ",";
									searchFieldTypes += obj[1] + ",";
									interFaceField += obj[2] + ",";
									interFaceFieldChilds += obj[3] + ",";
								}

							}
							searchField = trimBothEndsChars(searchField, ",");
							searchFieldTypes = trimBothEndsChars(searchFieldTypes, ",");
							interFaceField = trimBothEndsChars(interFaceField, ",");
							interFaceFieldChilds = trimBothEndsChars(interFaceFieldChilds, ",");
							// 业务表数据查询sql
							String sql = "select " + searchField + " from " + tableName + " where id =:id ";
							if("cpxzbgsq".equals(tableName)) {
								//产品新增变更流程只推送新增数据
								sql += " and cpxzbg='0' ";
							}else if("cghtspsqb".equals(tableName)) {
								//采购合同审批只推送erp 制单
								sql += " and erpzd='1' ";
							}else if("zclwzsqb".equals(tableName)) {
								//资产类物资申请只推送采
								sql += " and zcly!='1' ";
							}
							//jinxh 1163
							Map<String, String> queryMap = new HashMap<String, String>();
							Map<String,String> paramsMap=new HashMap<String,String>();
			 				paramsMap.put("maintableName", tableName);
			 				paramsMap.put("recordId", business_key_);
							queryMap.put("id", business_key_);
							// 获取各个业务表数据
							Map<String, Object> tableDataTemp = getTableDataMap(sql, searchField,
									interFaceField, interFaceFieldChilds, queryMap,searchFieldTypes,paramsMap);
							tableData.putAll(tableDataTemp);
							tableData.putAll(childtableData);
							String dataJson = JSONObject.toJSONString(tableData,SerializerFeature.DisableCircularReferenceDetect);
							logger.info("dataJson==============>" + dataJson);
							logger.info("formid==============>" + formid);
							if ("pushStatus".equals(dataTpye)) {
								
								Parameters.add(JSONObject.parse(dataJson));
							} else {
								
								KingdeeApplicationData appData = new KingdeeApplicationData("true", "false", "true", "true",
										"true", JSONObject.parseObject(dataJson, Feature.OrderedField),tableName,business_key_);
								
								Parameters.add(JSONObject.toJSON(appData));
							}
						}
					}
				}
			}
			return Parameters;

		}

		/**
		 * 初始化金蝶参数
		 */
		private void initKingConfig() {

			saveUrl = httpClientProperties.getKingUrl()
					+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Save.common.kdsvc";
			loginUrl = httpClientProperties.getKingUrl()
					+ "Kingdee.BOS.WebApi.ServicesStub.AuthService.ValidateUser.common.kdsvc";
			shUrl = httpClientProperties.getKingUrl()
					+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Audit.common.kdsvc";
			fshUrl = httpClientProperties.getKingUrl()
					+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.UnAudit.common.kdsvc";
			uploadurl= httpClientProperties.getKingUrl()
					+ "FileUpLoadServices/FileService.svc/upload2attachment/";
			deleteUrl=httpClientProperties.getKingUrl()
					+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Delete.common.kdsvc";
			acctID = httpClientProperties.getAcctID();
			username = httpClientProperties.getUsername();
			password = httpClientProperties.getPassword();
			lcid = httpClientProperties.getLcid();
			path=httpClientProperties.getPath();
		}

		private boolean Login() {
			boolean bResult = false;
			Map<String, Object> applicationDataMap = new HashMap<String, Object>();
			applicationDataMap.put("acctID", acctID);
			applicationDataMap.put("username", username);
			applicationDataMap.put("password", password);
			applicationDataMap.put("lcid", lcid);

			ResponseEntity<String> result = httpClientTemplate.postForEntity(loginUrl, applicationDataMap, String.class);
			String resJson = result.getBody().toString();
			JSONObject json = JSONObject.parseObject(resJson);

			int Loginresulttype = json.getIntValue("LoginResultType");
			String KDSVCSessionId = json.get("KDSVCSessionId") == null ? "" : json.get("KDSVCSessionId").toString();
			if (Loginresulttype == 1) {
				userToken= json.getJSONObject("Context").getString("UserToken");
				createId=json.getJSONObject("Context").getString("UserId");
				bResult = true;
			}
			return bResult;
		}

		/**
		 * 去除字符串首尾两端指定的字符
		 */
		public String trimBothEndsChars(String srcStr, String splitter) {
			/*
			 * String regex = "^" + splitter + "*|" + splitter + "*$"; return
			 * srcStr.replaceAll(regex, "");
			 */
			if (srcStr.endsWith(splitter)) {
				srcStr = srcStr.substring(0, srcStr.length() - 1);
			}
			return srcStr;
		}

		/**
		 * 定时处理发送失败的任务
		 */
		//@Scheduled(fixedDelay = 60000L)
		@Scheduled(cron = "0 0 1 * * ? ")
		public void dealFailedKingdeeData() {
			
			// 获取失败的任务数据
			List<FlowBuesinessFail> list = flowBuesinessFailDao.findAll();
			List<FlowBuesinessFail> listsuccess = new ArrayList<FlowBuesinessFail>();
			if (list != null && list.size() > 0) {
				// 先登录ERP
				if (Login()) {
					String ids = "";// 推送成功的记录id
					for (FlowBuesinessFail po : list) {
						String jsonString = po.getData();
						String url = po.getKingdeeUrl();
						HttpHeaders headers = new HttpHeaders();
				        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
				        headers.setContentType(type);
				        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
				        HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);
				        String processInstanceId =po.getProcessInstanceId();
				        String formid=po.getFormid();
				        JSONObject jsonO=JSONObject.parseObject(jsonString);
				        String returnTableName=jsonO.getJSONObject("data").getString("tableName");
		        		String returnRecordId=jsonO.getJSONObject("data").getString("recordId");
						ResponseEntity<String> result = httpClientTemplate.postForEntity(url, formEntity, String.class);
						JSONObject json = JSONObject.parseObject(result.getBody().toString());
						JSONObject ResponseStatus= json.getJSONObject("Result").getJSONObject("ResponseStatus");
						String IsSuccess =ResponseStatus.getString("IsSuccess");
						if ("true".equals(IsSuccess)) {
							//根据processInstanceId获取流程附件
							JSONArray SuccessEntitys=ResponseStatus.getJSONArray("SuccessEntitys");
							JSONObject resObj=SuccessEntitys.getJSONObject(0);
							String finterid=resObj.getString("Id");
							String fbillno=resObj.getString("Number");
							//更新物料编码
							//更新物料编码
							if("wlbmsqzb".equals(returnTableName)) {
								/*String sql="update "+returnTableName+" set wlbm='"+fbillno+"'  where id=?1";
								Query query = this.entityManager.createNativeQuery(sql).setParameter(1, returnRecordId);
							    query.executeUpdate();*/
								String sql="update "+returnTableName+" set wlbm=? where id=?";
								jdbcTemplate.update(sql,fbillno,returnRecordId);
							}
							//更新客户编码
							if("khxz".equals(returnTableName)) {
								/*String sql="update "+returnTableName+" set khbm='"+fbillno+"'  where id=?1";
								Query query = this.entityManager.createNativeQuery(sql).setParameter(1, returnRecordId);
							    query.executeUpdate();*/
								String sql="update khxz set khbm=? where id=?";
								jdbcTemplate.update(sql,fbillno,returnRecordId);
							}
							//更新财务报销流程编码
							if("dgcglbx".equals(returnTableName)||"rlxzlbx".equals(returnTableName)||"ygbx".equals(returnTableName)||"jk".equals(returnTableName)) {
								
								String sql="update "+returnTableName+" set erpdjbh=? where id=?";
								jdbcTemplate.update(sql,fbillno,returnRecordId);
							}
							List<FlowBuesinessAttach> attachList=flowBuesinessAttachDao.getFlowBuesinessAttach(processInstanceId);
							uploadAttachFile(formid, finterid, fbillno, processInstanceId,true, attachList);
							listsuccess.add(po);
						}

					}

					//处理附件上传失败的 
					List<FlowBuesinessAttach> attachList=flowBuesinessAttachDao.getFlowBuesinessAttach();
					uploadAttachFile("", "", "", null,false, attachList);
					if (listsuccess.size() > 0) {
						// 删除推送成功的记录
						flowBuesinessFailDao.deleteInBatch(listsuccess);
					}
				}
			}

		}
		
		/**
		 * 获取用户或者组织的id
		 * @param fNumber
		 * @param infoType
		 * @return
		 */
		public  Map<String,String>  getUserOrOrgIdByFNumber(String fNumber, String infoType) {
			 String sql="";
			 if("user".equals(infoType)) {
				 sql="SELECT id FROM sys_user WHERE login_name=:fNumber AND IS_DELETE=0";
			 }else if("org".equals(infoType)){
				 sql="SELECT id FROM sys_org WHERE org_code =:fNumber AND IS_DELETE=0";
			 }else if("station".equals(infoType)) {
				 sql="select id from sys_station where station_code =:fNumber AND IS_DELETE=0";
			 }else if("duty".equals(infoType)) {
				 sql="select id from sys_duty where duty_code =:fNumber AND IS_DELETE=0";
				 
			 }else if("userorg".equals(infoType)) {
				 sql="select org_id id from sys_org_user where user_id=:fNumber ";
			 }
			 Map<String,Object> queryMap=new HashMap<String,Object>();
			 Map<String,String> resultMap=new HashMap<String,String>();
			 queryMap.put("fNumber", fNumber);
			 List list = super.findByListNativeQuery(sql, "", queryMap);
			 if ((list != null) && (!list.isEmpty())) {
			      String id = StringUtils.null2String(((Map)list.get(0)).get("id"));
			      resultMap.put("id", id);
			    }
			return resultMap;
		}
		
		
		/**
		 * 获取用户或者组织的id
		 * @param id
		 * @param infoType
		 * @param paramsMap 
		 * @return
		 */
		public  Map<String,String>  getUserOrOrgFNumberById(String id, String infoType, Map<String, String> paramsMap) {
			 String sql="";
			 String tableName=paramsMap.get("maintableName");
			 //判断中间关联表是否存在
			 String sql2=" select table_name from user_tables where table_name =upper('"+tableName+"_scope') ";
			 List tempList=super.findByListNativeQuery(sql2, "");
			 String whereSql="";
			 if(tempList!=null&&tempList.size()>0) {
				 whereSql=" or id=(select scopevalue from "+tableName+"_scope  where fielddatavalue=:id)";
			 }
			
			 if("user".equals(infoType)) {
				 // 从关联关系表中获取用户id 
				 sql="select fnumber as fNumber from xfsmzjk.T_KDXF_STAFF@toerp where fusername ="
				 		+ " (SELECT login_name as fNumber FROM sys_user WHERE (id=:id  "+whereSql+") "
				 		+ " AND IS_DELETE=0)";
			 }else if("org".equals(infoType)){
				 sql="SELECT org_code  as fNumber FROM sys_org WHERE ( id =:id  or ( org_name=:id and org_level=2 ) "+whereSql+" ) AND IS_DELETE=0 ";
			 }
			 Map<String,Object> queryMap=new HashMap<String,Object>();
			 Map<String,String> resultMap=new HashMap<String,String>();
			 queryMap.put("id", id);
			 System.out.println("sql===sqlQuery"+sql);
			 System.out.println("id===idQuery"+id);
			 List list = super.findByListNativeQuery(sql, "", queryMap);
			 if ((list != null) && (!list.isEmpty())) {
			      String fNumber = StringUtils.null2String(((Map)list.get(0)).get("fNumber"));
			      resultMap.put("fNumber", fNumber);
			    }
			return resultMap;
		}
	    
			    
		/**
		 * 根据字段值 获取组织用户 或者自定义信息
		 * @param fieldType 0 文本 1 附件 2 用户 3 组织  4 自定义查询 5 ERP所属公司编码  6 元转万元
		 * @param fieldValue
		 * @param paramsMap
		 * @return
		 */
		public String  getFiledValue(String fieldType,String fieldValue,Map<String,String> paramsMap) {
			String returnValue=fieldValue;
				
				if("1".equals(fieldType)) {
					//附件上传
					/*returnValue=UploadToWebSite(fieldValue, "", userToken, true, path);*/
					fieldValue="'"+fieldValue.replace("|", "','")+"'";
					fileList=getFileListByIds(fieldValue);
				}else if("2".equals(fieldType)) {
					//用户账号
					Map<String,String> map=getUserOrOrgFNumberById(fieldValue, "user",paramsMap);
					returnValue=map.get("fNumber");
				}else if("3".equals(fieldType)) {
					//组织编码
					Map<String,String> map=getUserOrOrgFNumberById(fieldValue, "org",paramsMap);
					returnValue=map.get("fNumber");
				}else if("5".equals(fieldType)) {
					//所属公司
					Map<String,String> map=getUserOrOrgFNumberById(fieldValue, "org",paramsMap);
					returnValue=getERPuserOrgId(map.get("fNumber"));
				}else if("4".equals(fieldType)) {
					String tableName=paramsMap.get("tableName");
					String fieldName=paramsMap.get("fieldName");
					String recordId=paramsMap.get("recordId");
					Map tempMap=new HashMap();
					tempMap.put("recordId", recordId);
					//资产类物资申请
					if("zclwzsqzb".equals(tableName)) {
						   // 子表采购组织和需求组织默认值
							if("xqr".equals(fieldName)) {
								String sql="SELECT org_code   FROM sys_org where  id ="
										+ "(select org_id from sys_org_user a  left join zclwzsqb_scope b  "
										+ "on a.user_id=b.scopevalue left join zclwzsqb c on b.fielddatavalue=c.xqr"
										+ "  where c.id=:recordId)   ";
								List tempList= super.findByListNativeQuery(sql,"", tempMap);
								if(tempList!=null&&tempList.size()>0) {
									String	orgCode= StringUtils.null2String(((Map)tempList.get(0)).get("org_code"));
									// 根据组织编码去中间库查询所属组织
									returnValue=getERPuserOrgId(orgCode);
								}
							}
					}
				}else if("6".equals(fieldType)) {
					//万元转元
					if(!"".equals(fieldValue)){
						try{
							Double doubleValue = Double.parseDouble(fieldValue);
							returnValue =getMoney(fieldValue);
						}catch(Exception e){
							//e.printStackTrace();
						}

					}
					/*if(org.apache.commons.lang3.StringUtils.isNumeric(fieldValue)) {
						returnValue =getMoney(fieldValue);
					}*/
					
				}else if("7".equals(fieldType)) {
					JSONArray arr=new JSONArray();
					arr.add(fieldValue);
					returnValue=arr.toJSONString();
				}else if("8".equals(fieldType)) {
					//获取员工主任刚信息
					returnValue=getZRGInfo(fieldValue,paramsMap);
					
				}
			return returnValue;
		}
		
		
		
		
		/*
		 * 获取员工主任刚信息
		 */
		public String getZRGInfo(String id , Map<String, String> paramsMap) {
			 String sql="";
			 String fNumber = "";
			 String tableName=paramsMap.get("maintableName");
			 //判断中间关联表是否存在
			 String sql2=" select table_name from user_tables where table_name =upper('"+tableName+"_scope') ";
			 List tempList=super.findByListNativeQuery(sql2, "");
			 String whereSql="";
			 if(tempList!=null&&tempList.size()>0) {
				 whereSql=" or id=(select scopevalue from "+tableName+"_scope  where fielddatavalue=:id)";
			 }
			
			 // 从关联关系表中获取用户id 
			 sql="select f_kdxf_ygrgxxbm as fNumber from xfsmzjk.T_KDXF_STAFF@toerp where fusername ="
			 		+ " (SELECT login_name as fNumber FROM sys_user WHERE (id=:id  "+whereSql+") "
			 		+ " AND IS_DELETE=0)";
			 Map<String,Object> queryMap=new HashMap<String,Object>();
			 Map<String,String> resultMap=new HashMap<String,String>();
			 queryMap.put("id", id);
			 List list = super.findByListNativeQuery(sql, "", queryMap);
			 if ((list != null) && (!list.isEmpty())) {
			      fNumber = StringUtils.null2String(((Map)list.get(0)).get("fNumber"));
			    }
			return fNumber;
		}

		/**
		 * orgCode 组织编码
		 * @return
		 */
		public String getERPuserOrgId(String orgCode) {
			String returnValue="";
			String sql="select fuseorgid from xfsmzjk.t_kdxf_department@toerp where fnumber=:orgCode ";
			Map tempMap=new HashMap();
			tempMap.put("orgCode", orgCode);
			List tempList= super.findByListNativeQuery(sql,"", tempMap);
			if(tempList!=null&&tempList.size()>0) {
				returnValue= StringUtils.null2String(((Map)tempList.get(0)).get("fuseorgid"));
			}
			return returnValue;
		}
		/**
		 * 万元转元
		 * @param wy
		 * @return
		 */
		public  String getMoney(String wy) {
			BigDecimal bigDecimal1 = new BigDecimal(wy);
			BigDecimal bigDecimal2 = new BigDecimal(10000);
			double val =  bigDecimal1.multiply(bigDecimal2).doubleValue();
			BigDecimal bigDecimal = new BigDecimal(val);
			//System.out.println(bigDecimal.toString());
			String feesum = bigDecimal.toString();
			return feesum;
		}
		
		
		/**
		 * 分批上传附件
		 * @param fileName
		 * @param fileId
		 * @param token
		 * @param last
		 * @param content
		 * @param filePath 
		 * @param content_type 
		 * @return
		 * @throws UnsupportedEncodingException  
		 */
		public JSONObject UploadToWebSite(String fileName,String fileSaveName, String fileId, String token, boolean last,  String filePath, String content_type)  {
			
			/*try {
				String name=URLEncoder.encode(fileName,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			String Tuploadurl=uploadurl+"?fileName="+fileName+"&fileId="+fileId+"&token="+token+"&last="+last;
			HttpHeaders headers = new HttpHeaders();
			byte[] buffer=null;
			try {
				buffer = toByteArray(filePath+File.separator+fileSaveName);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// MediaType type = MediaType.parseMediaType("application/octet-stream");
	         headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	         headers.setContentLength(buffer.length);
	         HttpEntity<byte[]> httpEntity = new HttpEntity<byte[]>(buffer, headers);
	         String result= httpClientTemplate.postForObject(Tuploadurl, httpEntity, String.class);
	         JSONObject json=JSONObject.parseObject(result);
	         /*if(json.getJSONObject("Upload2AttachmentResult")!=null&&json.getJSONObject("Upload2AttachmentResult").getBooleanValue("Success"))
	         returnFileId= json.getJSONObject("Upload2AttachmentResult").getString("FileId");*/
			return json;
			
		}
		/**
		 * 保存单据附件
		 * @param fbilltype 单据formId
		 * @param finterid 单据id
		 * @param fbillno 单据编号
		 * @param fileId
		 * @param fileName
		 * @param extType
		 * @param fileSize
		 * @return
		 */
		public String saveBillData(String fbilltype,String finterid,String fbillno,String fileId,String fileName,String extType,String fileSize) {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
			JSONObject jsonRoot=new JSONObject(true);
			  jsonRoot.put("Creator", username);//创建用户。
			  jsonRoot.put("NeedUpDateFields", new JSONArray());//需要更新的字段：为空则表示全部。
			  
			  String formID = "BOS_Attachment";
			  JSONObject model=new JSONObject(true);
				//{"Upload2AttachmentResult":{"Message":"","Success":true,"FileId":"d823d57e528941adb36c6d2dd975e780","FileName":"12345.docx","FileSize":66473,"Url":""}}
	 
				model.put("FID", 0);//内码：为零代表新增。
				model.put("FBILLTYPE", fbilltype);//业务单据唯一标识：此处关联币别。
				model.put("FINTERID", finterid);//业务单据内码：此处上传至人民币，内码为1。
				model.put("FBILLNO", fbillno);//业务单据编码：此处上传至人民币，编码为PRE001。
				model.put("FENTRYKEY", " ");//关联实体标识：单据头为空格，单据体则填单据体标识。
				model.put("FENTRYINTERID", -1);//单据体内码：单据头为-1，单据体则填单据体内码。
				model.put("FFILEID", fileId);//文件编码：上面上传成功拿到文件编码。
				model.put("FFILESTORAGE", "1");//存储类型：1为文件服务器、2为亚马逊云存储、3为金蝶·个人云存储、4为金蝶·企业云存储。
				model.put("FATTACHMENTNAME", fileName);//附件名。
				model.put("FEXTNAME",extType);//文件后缀名。
				//Decimal fileSize = Convert.ToDecimal(uploadResult["FileSize"].ToString());
				DecimalFormat df = new DecimalFormat("#.00");
				model.put("FATTACHMENTSIZE", df.format(Long.valueOf(fileSize)/1024));//附件大小，单位为KB。
				model.put("FBILLSTATUS", "A");//单据状态：给默认值A即可。
				model.put("FALIASFILENAME",fileName);//别名。
				model.put("FIsAllowDownLoad", false);//是否禁止下载：false代表允许下载。
				model.put("FCREATEMEN", JSONObject.parseObject("{\"FUSERID\":\""+createId+"\"}") );//创建人。
				model.put("FCREATETIME", sdf.format(new Date()));//创建时间。
				/*model.put("FMODIFYMEN", JSONObject.parseObject("{\"FUSERID\":\""+16394+"\"}") );//修改人。
				model.put("FMODIFYTIME", "2020-02-02");//修改时间。
*/				jsonRoot.put("Model", model);
				ResponseEntity<String> result = null;
				//设置请求头
				HttpHeaders headers = new HttpHeaders();
		        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		        headers.setContentType(type);
		        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		        
        		Map<String, Object> applicationDataMap = new HashMap<String, Object>();
				//业务数据
				applicationDataMap.put("formid", formID);
				applicationDataMap.put("data",jsonRoot);
				String jsonString = JSONObject.toJSONString(applicationDataMap);
				
		        HttpEntity<String> formEntity = new HttpEntity<String>(jsonString, headers);
				
		        result = httpClientTemplate.postForEntity(saveUrl, formEntity, String.class);
				JSONObject json = JSONObject.parseObject(result.getBody().toString());
				JSONObject ResponseStatus= json.getJSONObject("Result").getJSONObject("ResponseStatus");
				String IsSuccess =ResponseStatus.getString("IsSuccess");
				if (!"true".equals(IsSuccess)) {
					FlowBuesinessFail fail = new FlowBuesinessFail();
					fail.setFormid(formID);
					fail.setKingdeeUrl(saveUrl);
					fail.setData(jsonString);
					fail.setFlowBId("1");
					// 接口调用失败保存记录及失败原因
					fail.setResponseJson(result.getBody().toString());
					save(fail);
				}
				return null;
		}
		
		/**
	     * the traditional io way
	     *
	     * @param filename
	     * @return
	     * @throws IOException
	     */
	    public  byte[] toByteArray(String filename) throws IOException {
	 
	        File f = new File(filename);
	        if (!f.exists()) {
	            throw new FileNotFoundException(filename);
	        }
	 
	        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
	        BufferedInputStream in = null;
	        try {
	            in = new BufferedInputStream(new FileInputStream(f));
	            int buf_size = 1024;
	            byte[] buffer = new byte[buf_size];
	            int len = 0;
	            while (-1 != (len = in.read(buffer, 0, buf_size))) {
	                bos.write(buffer, 0, len);
	            }
	            return bos.toByteArray();
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw e;
	        } finally {
	            try {
	                in.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            bos.close();
	        }
	    }
	    /**
	     * 获取附件
	     * @param fieldValue
	     * @return
	     */
	    public List<FlowBuesinessAttach> getFileListByIds(String fieldValue) {
	    	List<FlowBuesinessAttach> list=new ArrayList<FlowBuesinessAttach>();
	    	String sql="select t.content_type,t.file_displayname,t.file_ext,t.file_name,t.relative_path from sys_attachment t  WHERE  ID in ("+fieldValue+")";
	    	Map tempMap=new HashMap();
	    	List tempList= super.findByListNativeQuery(sql,"", tempMap);
			if(tempList!=null&&tempList.size()>0) {
				
				for(int i=0,size=tempList.size();i<size;i++) {
					FlowBuesinessAttach attach=new FlowBuesinessAttach();
					String content_type = StringUtils.null2String(((Map)tempList.get(i)).get("content_type"));
					String file_displayname = StringUtils.null2String(((Map)tempList.get(i)).get("file_displayname"));
					String file_ext = StringUtils.null2String(((Map)tempList.get(i)).get("file_ext"));
					String file_name = StringUtils.null2String(((Map)tempList.get(i)).get("file_name"));
					String relative_path = StringUtils.null2String(((Map)tempList.get(i)).get("relative_path"));
					
					attach.setFileName(file_displayname);
					attach.setContent_type(content_type);
					attach.setExtType(file_ext);
					attach.setFileSaveName(file_name);
					attach.setFilePath(path+relative_path);
					list.add(attach);
				}
			}
	    	return list;
		}
	    
	    /**
	     * 查询资产卡片数据
	     * @param jsonRoot
	     * @return
	     */
	    public Map<String, String> queryZZKPCode(JSONObject jsonRoot) {
	    		Login();
	    		Map<String,String> resultMap=new HashMap<String,String>();
				ResponseEntity<String> result = null;
				//设置请求头
				HttpHeaders headers = new HttpHeaders();
		        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		        headers.setContentType(type);
		        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		        JSONObject jso=new JSONObject();
	    		jso.put("data", jsonRoot);
		        HttpEntity<String> formEntity = new HttpEntity<String>(jso.toString(), headers);
		        result = httpClientTemplate.postForEntity(httpClientProperties.getKingUrl()+"Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.ExecuteBillQuery.common.kdsvc", formEntity, String.class);
				if(result.getStatusCodeValue()==200) {
					 JSONArray jsonArray = JSONArray.parseArray(result.getBody().toString());
					 if(jsonArray!=null&&jsonArray.size()>0) {
						 for(int i=0,size=jsonArray.size();i<size;i++) {
							 JSONArray arr=jsonArray.getJSONArray(i);
							 String key=arr.getString(0);
							 String value=arr.getString(1);
							 resultMap.put(key, value);
						 }
					 }
						
				}
		       
				return resultMap;
		}

	    
	    /**
	     * 更新数据表数据
	     * @param formData
	     * @return
	     */
		public Map<String, String> updateFormData(String formData) {
			Map<String, String> returnMap = new HashMap<String, String>();
			Map<String, Object> mainTblDataMap = new HashMap<String, Object>();
			//F_PAEZ_MXID
			RuntimeDataDto runtimeDataDto = (RuntimeDataDto) JSONObject.parseObject(formData, RuntimeDataDto.class);
			List<KeyValueDto> mainTblDataList = runtimeDataDto.getMainTblData();// 主表数据
			String mainTable=runtimeDataDto.getMainTblName();//主表名
			if(org.apache.commons.lang3.StringUtils.isNotEmpty(mainTable)) {
				mainTblDataMap = getValueMap(mainTblDataList);
				String sql=" update " +mainTable +" set  ";
				String whereSql="";
				  for (Map.Entry<String, Object> m : mainTblDataMap.entrySet()) {
					  	if("bh".equals(m.getKey())) {
					  		whereSql="where  bh='"+m.getValue()+"'";
					  	}else {
					  		sql+=  m.getKey()+" ='"+m.getValue()+"' ,"	;
					  	}
				       /* System.out.println("key:" + m.getKey() + " value:" + m.getValue());*/
				    }
				  if(sql.endsWith(",")) {
					  sql=sql.substring(0,sql.length()-1);
				  }
				  sql +=whereSql;
			     /* Query query = this.entityManager.createNativeQuery(sql);
			      query.executeUpdate();*/
				jdbcTemplate.update(sql);
			}
			//更新子表
			List<SubTblDataDto> subTblList = runtimeDataDto.getSubTbl();
			for (SubTblDataDto subDto : subTblList) {
				String subTableName = subDto.getSubTblName();
				if(org.apache.commons.lang3.StringUtils.isNotEmpty(subTableName)) {
					List<List<KeyValueDto>> tempData = subDto.getSubTblData();
					if (tempData != null && tempData.size() > 0) {
						String [] sqlArr =new String [tempData.size()];
						for (int i = 0, size = tempData.size(); i < size; i++) {
							String sql="update " +subTableName +" set  ";
							String whereSql="";
							Map<String, Object> tempMap = getValueMap(tempData.get(i));
							for (Map.Entry<String, Object> m : tempMap.entrySet()) {
							  	if("f_paez_mxid".equals(m.getKey())) {
							  		whereSql="where  id='"+m.getValue()+"'";
							  	}else {
							  		sql+=  m.getKey()+" ='"+m.getValue()+"' ,"	;
							  	}
						    }
							 if(sql.endsWith(",")) {
								  sql=sql.substring(0,sql.length()-1);
							  }
							 sql +=whereSql;
							 sqlArr[i]=sql;
							
						}
						jdbcTemplate.batchUpdate(sqlArr);
					}
				      
				}
				
			}
			returnMap.put("msg", "操作成功！");
			return returnMap;
		}
		
		
		/**
		 * 获取 前台表单数据
		 * @param list
		 * @return
		 */
		public Map<String,Object> getValueMap(List<KeyValueDto> list){
			  Map<String,Object> map =new HashMap<String,Object>();
			  for(KeyValueDto dto:list) {
				  map.put(dto.getKey(), dto.getValue());
			  }
			return map;
		}

		/**
		 * 获取选人选组织 json
		 * @param fNumber
		 * @param infoType
		 * @return
		 */
		public Map<String, String> getUserOrOrgIdJSON(String fNumber, String infoType) {


			 String sql="";
			 if("user".equals(infoType)) {
				 sql="SELECT id FROM sys_user WHERE login_name=:fNumber AND IS_DELETE=0";
			 }else if("org".equals(infoType)){
				 sql="SELECT id FROM sys_org WHERE org_code =:fNumber AND IS_DELETE=0";
			 }else if("station".equals(infoType)) {
				 sql="select id from sys_station where station_code =:fNumber AND IS_DELETE=0";
			 }else if("duty".equals(infoType)) {
				 sql="select id from sys_duty where duty_code =:fNumber AND IS_DELETE=0";
				 
			 }
			 Map<String,Object> queryMap=new HashMap<String,Object>();
			 Map<String,String> resultMap=new HashMap<String,String>();
			 queryMap.put("fNumber", fNumber);
			 List list = super.findByListNativeQuery(sql, "", queryMap);
			 if ((list != null) && (!list.isEmpty())) {
			      String id = StringUtils.null2String(((Map)list.get(0)).get("id"));
			      resultMap.put("id", id);
			    }
			return resultMap;
		
		}
		
		
		/**
		 * 获取选人选组织 json
		 * @param fNumber
		 * @param infoType
		 * @return
		 */
		public JSONArray getUserOrOrgIdJSON_NEW(String fNumber, String infoType) {


			 String sql="";
			 if("user".equals(infoType)) {
				 sql="SELECT  a.id as scopevalue  ,a.user_name as scopename ,c.org_name_path from sys_user a  left join  sys_org_user b on a.id=b.user_id left join sys_org c on b.org_id=c.id WHERE a.login_name=:fNumber AND a.IS_DELETE=0";
			 }else if("org".equals(infoType)){
				 sql="SELECT  c.id as scopevalue,c.org_name as scopename ,c.org_name_path,c.org_type FROM sys_org c  WHERE c.org_code =:fNumber AND c.IS_DELETE=0";
			 }else if("usercode".equals(infoType)) {
				 sql="SELECT  a.id as scopevalue  ,a.user_name as scopename ,c.org_name_path,c.org_code from sys_user a  left join  sys_org_user b on a.id=b.user_id left join sys_org c on b.org_id=c.id WHERE a.login_name=:fNumber AND a.IS_DELETE=0";
			 }
			 Map<String,Object> queryMap=new HashMap<String,Object>();
			 Map<String,String> resultMap=new HashMap<String,String>();
			 queryMap.put("fNumber", fNumber);
			 List list = super.findByListNativeQuery(sql, "", queryMap);
			 JSONArray arr=new JSONArray();
			 if ((list != null) && (!list.isEmpty())) {
				  Map map=(Map) list.get(0);
			      String scopevalue = StringUtils.null2String(map.get("scopevalue"));
			      String scopename = StringUtils.null2String(map.get("scopename"));
			      String org_name_path = StringUtils.null2String(map.get("org_name_path"));
			      
			      JSONObject json=new JSONObject(true);
			      json.put("scopeName", scopename);
			      json.put("scopeType", infoType);
			      json.put("scopeValue", scopevalue);
			      json.put("title", scopename+"("+org_name_path+")");
			      if("org".equals(infoType)) {
			    	  json.put("orgType", StringUtils.null2String(map.get("org_type")));
			      }else if("usercode".equals(infoType)) {
			    	  json.put("orgCode", StringUtils.null2String(map.get("org_code")));
			      }
			      arr.add(json);
			    }
			return arr;
		
		}
		/**
		 * 员工报销预算管控查询
		 * @return
		 */
	public JSONArray queryFormInfo(JSONObject queryJson) {
		JSONArray resultArr = null;
		if (Login()) {
			ResponseEntity<String> result = null;
			// 设置请求头
			HttpHeaders headers = new HttpHeaders();
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			JSONObject jso = new JSONObject();
			jso.put("data", queryJson);
			HttpEntity<String> formEntity = new HttpEntity<String>(jso.toString(), headers);
			result = httpClientTemplate.postForEntity(
					httpClientProperties.getKingUrl()
							+ "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.ExecuteBillQuery.common.kdsvc",
					formEntity, String.class);
			if (result.getStatusCodeValue() == 200) {
				resultArr = JSONArray.parseArray(result.getBody().toString());
/*
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0, size = jsonArray.size(); i < size; i++) {
						JSONArray arr = jsonArray.getJSONArray(i);
						String key = arr.getString(0);
						resultMap.put(key, arr);
					}
				}*/

			}

		}
		return resultArr;
	}

	/**
	 * 获取商机立项项目中产品线明细
	 * @param business_key_
	 * @return
	 */
	public JSONArray getCPXInfo(String recordId) {
		String sql = "select cpxbm,cpbm from sjlxsjb  where id=:recordId";
		JSONArray jsonArr = new JSONArray();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("recordId", recordId);
		List list = super.findByListNativeQuery(sql, "", queryMap);
		JSONArray arr = new JSONArray();
		if ((list != null) && (!list.isEmpty())) {
			Map map = (Map) list.get(0);
			String cpxbm = StringUtils.null2String(map.get("cpxbm"));
			String cpbm = StringUtils.null2String(map.get("cpbm"));
			if (StringUtils.isNotEmpty(cpxbm)) {
				JSONObject json = new JSONObject(true);
				json.put("F_KDXF_CPX1", JSONObject.parseObject("{\"FNumber\":\"" + cpxbm + "\"}"));
				json.put("F_KDXF_CPX", JSONObject.parseObject("{\"FNumber\":\"" + cpxbm + "\"}"));
				json.put("F_KDXF_SRBL", 100);
				json.put("F_KDXF_FYBL", 100);
				json.put("F_KDXF_FYBL", 100);
				JSONArray F_KDXF_CPEntity = new JSONArray();
				if (StringUtils.isNotEmpty(cpbm)) {
					String[] cpArr = cpbm.split(",");
					for (String cp : cpArr) {
						JSONObject jsoncp = new JSONObject(true);
						jsoncp.put("F_KDXF_CP", JSONObject.parseObject("{\"FNumber\":\"" + cp + "\"}"));
						F_KDXF_CPEntity.add(jsoncp);
					}

				}
				json.put("F_KDXF_CPEntity", F_KDXF_CPEntity);
				jsonArr.add(json);
			}

		}
		return jsonArr;

	}

	/**
	 * 获取列表字段查询项
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	public String querystintField(String tableName, String fieldName) {

		 String sql="SELECT fieldName,seachname from tckfz where formfieldname=:formfieldname and tablename=:tablename ";
		 Map<String,Object> queryMap=new HashMap<String,Object>();
		 Map<String,String> resultMap=new HashMap<String,String>();
		 String seachname="";
		 queryMap.put("tablename", tableName);
		 queryMap.put("formfieldname", fieldName);
		 List list = super.findByListNativeQuery(sql, "", queryMap);
		 if ((list != null) && (!list.isEmpty())) {
			  Map map=(Map) list.get(0);
		      seachname = StringUtils.null2String(map.get("seachname"));
		      seachname+=";"+ StringUtils.null2String(map.get("fieldName"));
		    }
		return  seachname;
	
	
	}
		
}
