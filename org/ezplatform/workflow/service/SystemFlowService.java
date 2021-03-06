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
	    private static Map<String,Map<String,String>> flowMap = new LinkedHashMap<String, Map<String,String>>(); // ??????erp??????????????????
	    private static Map<String,List<String []>> fieldMap =new LinkedHashMap<String, List<String []>>(); // ??????erp??????????????????
	    private static Map<String,List<String []>> fieldChildMap =new LinkedHashMap<String, List<String []>>(); // ??????erp??????????????????

	    
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
		private String saveUrl ;// ????????????url
		private String loginUrl ;// ??????????????????URL
		private String shUrl ;// ????????????url
		private String fshUrl ;// ???????????????url
		private String uploadurl;//????????????????????????
		private String deleteUrl;//????????????
		private String path;//??????????????????
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
	    	 //?????????????????????
	    	 initKingConfig();
	    }
	    
	    /**
	     * ????????????????????????
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
	                 
	                  //??????????????????
	                  List<String []> tempMapFileds=new ArrayList<String []>();
	                  for(int j=0,sizej=arrayFields.size();j<sizej;j++){
	                	  JSONObject jsonObjectField = arrayFields.getJSONObject(j);
	                	  if(jsonObjectField.get("systemField")!=null) {
	                		  //???????????? 
	                		  String systemField=(String) jsonObjectField.get("systemField");
		                      String fieldType=(String) jsonObjectField.get("fieldType");
		                      String interFaceField=(String) jsonObjectField.get("interFaceField");
		                      String interFaceChild=(String) jsonObjectField.get("interFaceChild");
		                      String [] arrTemp=new String [] {systemField,fieldType,interFaceField,interFaceChild};
		                      tempMapFileds.add(arrTemp);
	                	  }else if(jsonObjectField.get("childTable")!=null) {
	                		 // ????????????
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
		 * ?????????????????????
		 * @param sql
		 * @param queryMap 
		 * @param interFaceField 
		 * @param searchField 
		 * @param interFaceFieldChilds 
		 * @param queryMap
		 * @param searchFieldTypes  //????????????   0 ?????? 1 ?????? 2 ?????? 3 ??????  4 ???????????????
		 * @param paramsMap  //????????????
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
		    		//?????????????????????
		    		String fieldValue=getFiledValue(searchFieldTypeArr[i],  StringUtils.null2String(tempMap.get(searFiledArr[i])),paramsMap );
		    		if(StringUtils.isNotBlank(interFaceFieldChildArr[i])) {
		    			String interFacd=interFaceFieldArr[i];//?????????????????????????????????
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
		    			String interFacd=interFaceFieldArr[i];//?????????????????????????????????
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
		 * ??????????????????
		 * @param tableName
		 * @param childFieldList
		 * @param business_key_
		 * @param maintableName ????????????
		 * @return
		 */
		public Object getChildTableData(String tableName, List<String[]> childFieldList, String business_key_, String maintableName) {
			
			 List< Map<String,Object>> listTemp = new ArrayList< Map<String,Object>>();
			 String searchField="";//????????????????????????
			 String searchFieldTypes="";//????????????????????????
			 String interFaceField="";//???????????????????????????
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
 				//?????????????????????sql
 				String sql="select "+searchField+" from " +tableName+ " where fkid =:id ";
 				if("jdclqd".equals(tableName)) {
 					//????????????????????????????????????????????????
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
 			    			String interFacd=interFaceFieldArr[i];//?????????????????????????????????
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
	 * ????????????  ????????????????????????
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
	 * ????????????  ????????????????????????
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

			// ?????? processInstanceId ?????? ??????code ????????????????????????
			Map<String, String> flowInfoMap = getFlowInfo(processInstanceId, processDefinitionId);

			logger.info("flowInfoMap==============>" + flowInfoMap);
			// systemFlowService.dealFlow(flowInfoMap);
			// ????????????Map
			Map<String, Map<String, String>> flowMap = SystemFlowService.getFlowMap();
			if (flowInfoMap != null) {
				String ext_processcode = flowInfoMap.get("ext_processcode");
				String business_key_ = flowInfoMap.get("business_key_");
				FlowBuesinessFail fail = new FlowBuesinessFail();
				if (flowMap.get(ext_processcode) != null) {
					// ??????????????????????????????
					Map<String, String> tempMap = flowMap.get(ext_processcode);
					String tableName = tempMap.get("tableName");
					String className = tempMap.get("className");
					String classMethod = tempMap.get("classMethod");
					String formid = tempMap.get("formid");
					String dataTpye = tempMap.get("dataTpye");
					String processCode=tempMap.get("processCode");
					List<JSONObject> requestList=new ArrayList<JSONObject>();
					if("wlbmsq".equals(processCode)) {
						//????????????????????????
						// ?????? processInstanceId ?????? ??????code ????????????????????????
						requestList=materialService.getMaterialList(business_key_);
					}else if("gysxzbgsq".equals(processCode)){
						//???????????????/??????
						requestList=materialService.getSupplierList(business_key_);
						fileList=materialService.getFileList();
					}else if("rlxzlbx".equals(processCode)||"cglbx".equals(processCode)) {
						//????????????????????? ??????????????????
						requestList=financialService.getRLXZBXList(business_key_, tableName);
						fileList=financialService.getFileList();
					}else if("ygbx".equals(processCode)) {
						//????????????
						requestList=financialService.getAmountList(business_key_);
						fileList=financialService.getFileList();
					}else if("jksq".equals(processCode)) {
						//????????????
						requestList=financialService.getJKFlowList(business_key_);
						fileList=financialService.getFileList();
					}else if("ZCDBSQ".equals(processCode)||"ZCTKSQ".equals(processCode)){
						//?????????????????????????????????????????????
						//??????????????????
						if("ZCDBSQ".equals(processCode)){
							requestList=assetsService.getZCDBList(business_key_, tableName);
							fileList=assetsService.getFileList();
						}else{
							requestList=assetsService.getZCTKList(business_key_, tableName);
							fileList=assetsService.getFileList();
						}

					}else if("fylwzsq".equals(processCode)||"JDXQSQ".equals(processCode)){
						//????????????????????? ??? ????????????????????????
						requestList=assetsService.getFYWZSQList(business_key_,tableName);
						System.out.println("requestList======size()====>>>>"+requestList.size());
						fileList=assetsService.getFileList();
					}else if("XHTKSQ".equals(processCode)){
						//??????????????????
						requestList=assetsService.getXHTKQList(business_key_);
						fileList=assetsService.getFileList();
					}else {
						List<Object> Parameters = dealTableData(processInstanceId, processDefinitionId);
						Parameters.remove(0);
						JSONObject  jsonData=(JSONObject) Parameters.get(1);

						//??????????????????jsondata
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

						//?????????????????????????????????
						//jinxhCreate
						System.out.println(processCode);
						//??????????????????
						if("sjxmlx".equals(processCode)){

							jsonData = renderJsonDataSJLX(jsonData,business_key_,processCode);

						}

						//??????????????????
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
							//???????????????
							HttpHeaders headers = new HttpHeaders();
					        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
					        headers.setContentType(type);
					        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
					        
					      //??????????????????????????????????????????????????????
							if("fylwzsq".equals(processCode)||"jksq".equals(processCode) || "rlxzlbx".equals(processCode) || "cglbx".equals(processCode) || "ygbx".equals(processCode)) {
								String erpdjbh="";
								if("fylwzsq".equals(processCode)) {
									 erpdjbh=assetsService.getErpdjbh();
									 System.out.println("erpdjbh===========>>>>"+erpdjbh);
								}else {
									 erpdjbh=financialService.getErpdjbh();
								}
								if(org.apache.commons.lang3.StringUtils.isNotEmpty(erpdjbh)) {
									//??????????????????
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
								// ????????????????????? ?????????????????????????????????
								String sjformId = "KDXF_SJXM";
								Map<String, Object> sjapplicationDataMap = new HashMap<String, Object>();
								sjapplicationDataMap.put("formid", sjformId);
								JSONObject jsonData=requestList.get(0);
								//??????record??????????????????????????????
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
										//??????????????????????????????
										if("sjxmlx".equals(processCode)) {
											String sql="update "+tableName+" set erpfid=?, sjxmbm=?  where id=?";
											jdbcTemplate.update(sql,finterid,fbillno,business_key_);
										    
										  //???????????? ?????????????????????
										    List<Object> Parameters = dealTableData(processInstanceId, processDefinitionId);
											//????????????????????????
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
										System.out.println("????????????===================???");
										System.out.println(e.getMessage());
									}
									
								}
								
								
							}

					        if(requestList!=null&&requestList.size()>0) {
					        	for(JSONObject jsonData:requestList ) {
					        		Map<String, Object> applicationDataMap = new HashMap<String, Object>();
									//????????????
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
										//?????????????????? ????????? ????????????????????????????????????
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
										// ?????????????????????????????????????????????
										fail.setFlowBId("0");
										fail.setResponseJson(result.getBody().toString());
										//????????????
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
											//??????????????????
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
											//??????????????????
											if("khxz".equals(returnTableName)) {
												/*String sql="update "+returnTableName+" set khbm='"+fbillno+"'  where id=?1";
												Query query = this.entityManager.createNativeQuery(sql).setParameter(1, returnRecordId);
											    query.executeUpdate();*/
												String sql="update "+tableName+" set khbm=? where id=?";
												jdbcTemplate.update(sql,fbillno,business_key_);
											}
											
											//??????????????????????????????
											if("fylwzsq".equals(processCode)||"rlxzlbx".equals(processCode)||"cglbx".equals(processCode)||"ygbx".equals(processCode)||"jksq".equals(processCode)) {
												/*String sql="update "+tableName+" set erpdjbh='"+fbillno+"'  where id=?1";
												Query query = this.entityManager.createNativeQuery(sql).setParameter(1, business_key_);
											    query.executeUpdate();*/
												String sql="update "+tableName+" set erpdjbh=? where id=?";
												jdbcTemplate.update(sql,fbillno,business_key_);
											}
										} catch (Exception e) {
											// TODO: handle exception
											System.out.println("????????????===================???");
											System.out.println(e.getMessage());
										}
										
									}
									returnFlag=true;
					        	}
					        }
							
							logger.info("??????????????????==============>" + result.getBody().toString());
						}
						logger.info("??????????????????==============>" + (System.currentTimeMillis() - startTime));

					} catch (Exception e) {
						logger.error("?????????????????????????????????", e);
						e.printStackTrace();
						fail.setResponseJson(e.getMessage());
						save(fail);

					}
					if (StringUtils.isNotEmpty(className)) {
						// ????????????????????????
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

			logger.info("??????????????????????????????================???" + paramsMap + ",????????????:" + paramsMap);
			return returnFlag;
		}


	/**
		 * ????????????
		 * @param formid
		 * @param finterid
		 * @param fbillno
		 * @param processInstanceId
		 * @param fileList
		 * @throws UnsupportedEncodingException
		 */
		public  void uploadAttachFile(String formid,String finterid ,String fbillno ,String processInstanceId ,boolean isSave,List<FlowBuesinessAttach> fileList) {
			//????????????
			if(fileList!=null&&fileList.size()>0) {
				for(int i=0,size=fileList.size();i<size;i++) {
					FlowBuesinessAttach  attach=fileList.get(i);
					String fileName=attach.getFileName();
					String fileSaveName=attach.getFileSaveName();
					String extType=attach.getExtType();
					String cPath=attach.getFilePath();
					String content_type=attach.getContent_type();
					
					if(!isSave) {
						//???????????????????????????????????????????????????
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
						//??????
						if(isSave) {
							//????????????????????? 
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
		 * ????????????????????????
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
		// ????????????Map
		Map<String, Map<String, String>> flowMap = getFlowMap();
		Map<String, List<String[]>> fieldMap = getFieldMap();
		// ????????????Map
		Map<String, List<String[]>> fieldChildMap = getFieldChildMap();
		if (flowInfoMap != null) {
			String processCode = flowInfoMap.get("ext_processcode");
			String business_key_ = flowInfoMap.get("business_key_");

			List<JSONObject> requestList = null;
			if (flowMap.get(processCode) != null) {

				if ("rlxzlbx".equals(processCode) || "cglbx".equals(processCode)) {
					// ????????????????????? ??????????????????
					String tableName = "rlxzlbx";
					if ("cglbx".equals(processCode)) {
						tableName = "dgcglbx";
					}
					financialService.getRLXZBXList(business_key_, tableName);
				} else if ("ygbx".equals(processCode)) {
					// ????????????
					financialService.getAmountList(business_key_);
				} else if ("jksq".equals(processCode)) {
					// ????????????
					financialService.getJKFlowList(business_key_);
				}else if("fylwzsq".equals(processCode)) {
					//?????????????????????
					requestList = assetsService.getFYWZSQList(business_key_,"fylwzsqb");
				}else if("JDXQSQ".equals(processCode)) {
					//??????????????????
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
					// ??????????????????
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
						//???????????????
						HttpHeaders headers = new HttpHeaders();
				        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
				        headers.setContentType(type);
				        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
						// ??????????????????????????????
						if ("rlxzlbx".equals(processCode) || "cglbx".equals(processCode) || "ygbx".equals(processCode)
								|| "jksq".equals(processCode)||"fylwzsq".equals(processCode)) {
							String erpdjbh = "";
							
							if("fylwzsq".equals(processCode)) {
								erpdjbh = assetsService.getErpdjbh();
							}else {
								erpdjbh = financialService.getErpdjbh();
							}
							if (org.apache.commons.lang3.StringUtils.isNotEmpty(erpdjbh)) {
								// ??????????????????
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


								//????????????????????????????????????????????????????????????
								if("fylwzsq".equals(processCode) || "JDXQSQ".equals(processCode)){
									if(requestList!=null&&requestList.size()>0) {
										for(JSONObject jsonData:requestList ) {
											applicationDataMap = new HashMap<String, Object>();
											//????????????
											String returnTableName=jsonData.getString("tableName");
											String returnRecordId=jsonData.getString("recordId");
											String data= JSONObject.toJSONString(jsonData);

											applicationDataMap.put("formid", formid);
											applicationDataMap.put("data",jsonData);

											boolean IsAutoSubmitAndAudit = true;

											jsonData.put("IsAutoSubmitAndAudit", IsAutoSubmitAndAudit);
											jsonString = JSONObject.toJSONString(applicationDataMap);
											//????????????
											//???????????????????????????????????????
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
												// ?????????????????????????????????????????????
												fail.setFlowBId("0");
												fail.setResponseJson(result.getBody().toString());
												//????????????
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

								//??????
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
								// ?????????????????????????????????????????????
								fail.setResponseJson(result.getBody().toString());
								save(fail);
							}
						}
						returnFlag = true;
						logger.info("??????????????????==============>" + result.getBody().toString());
					}
					logger.info("??????????????????==============>" + (System.currentTimeMillis() - startTime));
				}

			} catch (Exception e) {
				logger.error("?????????????????????????????????", e);
				save(fail);
			}
		}

		return returnFlag;
	}

		/**
		 * ?????????????????????
		 * 
		 * @return
		 */
		public List<Object> dealTableData(String processInstanceId, String processDefinitionId) {
			List<Object> Parameters = new ArrayList<Object>();
			// ?????? processInstanceId ?????? ??????code ????????????????????????
			Map<String, String> flowInfoMap = getFlowInfo(processInstanceId, processDefinitionId);
			logger.info("flowInfoMap==============>" + flowInfoMap);
			// dealFlow(flowInfoMap);
			// ????????????Map
			Map<String, Map<String, String>> flowMap = getFlowMap();
			Map<String, List<String[]>> fieldMap = getFieldMap();
			// ????????????Map
			Map<String, List<String[]>> fieldChildMap = getFieldChildMap();
			if (flowInfoMap != null) {
				String ext_processcode = flowInfoMap.get("ext_processcode");
				String business_key_ = flowInfoMap.get("business_key_");
				if (flowMap.get(ext_processcode) != null) {
					// ??????????????????????????????
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
						// ?????????????????????
						List<String[]> fieldList = fieldMap.get(ext_processcode);
						String searchField = "";// ????????????????????????
						String searchFieldTypes = "";// ????????????????????????
						String interFaceField = "";// ???????????????????????????
						String interFaceFieldChilds = "";// ?????????????????????????????????
						String childTables = "";// ????????????
						String childTableInterFaceFields = "";// ?????????????????????
						if (fieldList != null && fieldList.size() > 0) {
							for (int i = 0, size = fieldList.size(); i < size; i++) {
								String[] obj = fieldList.get(i);
								if ("table".equals(obj[1])) {
									// ????????????
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
							// ?????????????????????sql
							String sql = "select " + searchField + " from " + tableName + " where id =:id ";
							if("cpxzbgsq".equals(tableName)) {
								//?????????????????????????????????????????????
								sql += " and cpxzbg='0' ";
							}else if("cghtspsqb".equals(tableName)) {
								//???????????????????????????erp ??????
								sql += " and erpzd='1' ";
							}else if("zclwzsqb".equals(tableName)) {
								//?????????????????????????????????
								sql += " and zcly!='1' ";
							}
							//jinxh 1163
							Map<String, String> queryMap = new HashMap<String, String>();
							Map<String,String> paramsMap=new HashMap<String,String>();
			 				paramsMap.put("maintableName", tableName);
			 				paramsMap.put("recordId", business_key_);
							queryMap.put("id", business_key_);
							// ???????????????????????????
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
		 * ?????????????????????
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
		 * ??????????????????????????????????????????
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
		 * ?????????????????????????????????
		 */
		//@Scheduled(fixedDelay = 60000L)
		@Scheduled(cron = "0 0 1 * * ? ")
		public void dealFailedKingdeeData() {
			
			// ???????????????????????????
			List<FlowBuesinessFail> list = flowBuesinessFailDao.findAll();
			List<FlowBuesinessFail> listsuccess = new ArrayList<FlowBuesinessFail>();
			if (list != null && list.size() > 0) {
				// ?????????ERP
				if (Login()) {
					String ids = "";// ?????????????????????id
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
							//??????processInstanceId??????????????????
							JSONArray SuccessEntitys=ResponseStatus.getJSONArray("SuccessEntitys");
							JSONObject resObj=SuccessEntitys.getJSONObject(0);
							String finterid=resObj.getString("Id");
							String fbillno=resObj.getString("Number");
							//??????????????????
							//??????????????????
							if("wlbmsqzb".equals(returnTableName)) {
								/*String sql="update "+returnTableName+" set wlbm='"+fbillno+"'  where id=?1";
								Query query = this.entityManager.createNativeQuery(sql).setParameter(1, returnRecordId);
							    query.executeUpdate();*/
								String sql="update "+returnTableName+" set wlbm=? where id=?";
								jdbcTemplate.update(sql,fbillno,returnRecordId);
							}
							//??????????????????
							if("khxz".equals(returnTableName)) {
								/*String sql="update "+returnTableName+" set khbm='"+fbillno+"'  where id=?1";
								Query query = this.entityManager.createNativeQuery(sql).setParameter(1, returnRecordId);
							    query.executeUpdate();*/
								String sql="update khxz set khbm=? where id=?";
								jdbcTemplate.update(sql,fbillno,returnRecordId);
							}
							//??????????????????????????????
							if("dgcglbx".equals(returnTableName)||"rlxzlbx".equals(returnTableName)||"ygbx".equals(returnTableName)||"jk".equals(returnTableName)) {
								
								String sql="update "+returnTableName+" set erpdjbh=? where id=?";
								jdbcTemplate.update(sql,fbillno,returnRecordId);
							}
							List<FlowBuesinessAttach> attachList=flowBuesinessAttachDao.getFlowBuesinessAttach(processInstanceId);
							uploadAttachFile(formid, finterid, fbillno, processInstanceId,true, attachList);
							listsuccess.add(po);
						}

					}

					//??????????????????????????? 
					List<FlowBuesinessAttach> attachList=flowBuesinessAttachDao.getFlowBuesinessAttach();
					uploadAttachFile("", "", "", null,false, attachList);
					if (listsuccess.size() > 0) {
						// ???????????????????????????
						flowBuesinessFailDao.deleteInBatch(listsuccess);
					}
				}
			}

		}
		
		/**
		 * ???????????????????????????id
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
		 * ???????????????????????????id
		 * @param id
		 * @param infoType
		 * @param paramsMap 
		 * @return
		 */
		public  Map<String,String>  getUserOrOrgFNumberById(String id, String infoType, Map<String, String> paramsMap) {
			 String sql="";
			 String tableName=paramsMap.get("maintableName");
			 //?????????????????????????????????
			 String sql2=" select table_name from user_tables where table_name =upper('"+tableName+"_scope') ";
			 List tempList=super.findByListNativeQuery(sql2, "");
			 String whereSql="";
			 if(tempList!=null&&tempList.size()>0) {
				 whereSql=" or id=(select scopevalue from "+tableName+"_scope  where fielddatavalue=:id)";
			 }
			
			 if("user".equals(infoType)) {
				 // ?????????????????????????????????id 
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
		 * ??????????????? ?????????????????? ?????????????????????
		 * @param fieldType 0 ?????? 1 ?????? 2 ?????? 3 ??????  4 ??????????????? 5 ERP??????????????????  6 ????????????
		 * @param fieldValue
		 * @param paramsMap
		 * @return
		 */
		public String  getFiledValue(String fieldType,String fieldValue,Map<String,String> paramsMap) {
			String returnValue=fieldValue;
				
				if("1".equals(fieldType)) {
					//????????????
					/*returnValue=UploadToWebSite(fieldValue, "", userToken, true, path);*/
					fieldValue="'"+fieldValue.replace("|", "','")+"'";
					fileList=getFileListByIds(fieldValue);
				}else if("2".equals(fieldType)) {
					//????????????
					Map<String,String> map=getUserOrOrgFNumberById(fieldValue, "user",paramsMap);
					returnValue=map.get("fNumber");
				}else if("3".equals(fieldType)) {
					//????????????
					Map<String,String> map=getUserOrOrgFNumberById(fieldValue, "org",paramsMap);
					returnValue=map.get("fNumber");
				}else if("5".equals(fieldType)) {
					//????????????
					Map<String,String> map=getUserOrOrgFNumberById(fieldValue, "org",paramsMap);
					returnValue=getERPuserOrgId(map.get("fNumber"));
				}else if("4".equals(fieldType)) {
					String tableName=paramsMap.get("tableName");
					String fieldName=paramsMap.get("fieldName");
					String recordId=paramsMap.get("recordId");
					Map tempMap=new HashMap();
					tempMap.put("recordId", recordId);
					//?????????????????????
					if("zclwzsqzb".equals(tableName)) {
						   // ??????????????????????????????????????????
							if("xqr".equals(fieldName)) {
								String sql="SELECT org_code   FROM sys_org where  id ="
										+ "(select org_id from sys_org_user a  left join zclwzsqb_scope b  "
										+ "on a.user_id=b.scopevalue left join zclwzsqb c on b.fielddatavalue=c.xqr"
										+ "  where c.id=:recordId)   ";
								List tempList= super.findByListNativeQuery(sql,"", tempMap);
								if(tempList!=null&&tempList.size()>0) {
									String	orgCode= StringUtils.null2String(((Map)tempList.get(0)).get("org_code"));
									// ????????????????????????????????????????????????
									returnValue=getERPuserOrgId(orgCode);
								}
							}
					}
				}else if("6".equals(fieldType)) {
					//????????????
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
					//???????????????????????????
					returnValue=getZRGInfo(fieldValue,paramsMap);
					
				}
			return returnValue;
		}
		
		
		
		
		/*
		 * ???????????????????????????
		 */
		public String getZRGInfo(String id , Map<String, String> paramsMap) {
			 String sql="";
			 String fNumber = "";
			 String tableName=paramsMap.get("maintableName");
			 //?????????????????????????????????
			 String sql2=" select table_name from user_tables where table_name =upper('"+tableName+"_scope') ";
			 List tempList=super.findByListNativeQuery(sql2, "");
			 String whereSql="";
			 if(tempList!=null&&tempList.size()>0) {
				 whereSql=" or id=(select scopevalue from "+tableName+"_scope  where fielddatavalue=:id)";
			 }
			
			 // ?????????????????????????????????id 
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
		 * orgCode ????????????
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
		 * ????????????
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
		 * ??????????????????
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
		 * ??????????????????
		 * @param fbilltype ??????formId
		 * @param finterid ??????id
		 * @param fbillno ????????????
		 * @param fileId
		 * @param fileName
		 * @param extType
		 * @param fileSize
		 * @return
		 */
		public String saveBillData(String fbilltype,String finterid,String fbillno,String fileId,String fileName,String extType,String fileSize) {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
			JSONObject jsonRoot=new JSONObject(true);
			  jsonRoot.put("Creator", username);//???????????????
			  jsonRoot.put("NeedUpDateFields", new JSONArray());//????????????????????????????????????????????????
			  
			  String formID = "BOS_Attachment";
			  JSONObject model=new JSONObject(true);
				//{"Upload2AttachmentResult":{"Message":"","Success":true,"FileId":"d823d57e528941adb36c6d2dd975e780","FileName":"12345.docx","FileSize":66473,"Url":""}}
	 
				model.put("FID", 0);//??????????????????????????????
				model.put("FBILLTYPE", fbilltype);//????????????????????????????????????????????????
				model.put("FINTERID", finterid);//?????????????????????????????????????????????????????????1???
				model.put("FBILLNO", fbillno);//?????????????????????????????????????????????????????????PRE001???
				model.put("FENTRYKEY", " ");//???????????????????????????????????????????????????????????????????????????
				model.put("FENTRYINTERID", -1);//??????????????????????????????-1????????????????????????????????????
				model.put("FFILEID", fileId);//??????????????????????????????????????????????????????
				model.put("FFILESTORAGE", "1");//???????????????1?????????????????????2????????????????????????3?????????????????????????????4?????????????????????????????
				model.put("FATTACHMENTNAME", fileName);//????????????
				model.put("FEXTNAME",extType);//??????????????????
				//Decimal fileSize = Convert.ToDecimal(uploadResult["FileSize"].ToString());
				DecimalFormat df = new DecimalFormat("#.00");
				model.put("FATTACHMENTSIZE", df.format(Long.valueOf(fileSize)/1024));//????????????????????????KB???
				model.put("FBILLSTATUS", "A");//???????????????????????????A?????????
				model.put("FALIASFILENAME",fileName);//?????????
				model.put("FIsAllowDownLoad", false);//?????????????????????false?????????????????????
				model.put("FCREATEMEN", JSONObject.parseObject("{\"FUSERID\":\""+createId+"\"}") );//????????????
				model.put("FCREATETIME", sdf.format(new Date()));//???????????????
				/*model.put("FMODIFYMEN", JSONObject.parseObject("{\"FUSERID\":\""+16394+"\"}") );//????????????
				model.put("FMODIFYTIME", "2020-02-02");//???????????????
*/				jsonRoot.put("Model", model);
				ResponseEntity<String> result = null;
				//???????????????
				HttpHeaders headers = new HttpHeaders();
		        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		        headers.setContentType(type);
		        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		        
        		Map<String, Object> applicationDataMap = new HashMap<String, Object>();
				//????????????
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
					// ?????????????????????????????????????????????
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
	     * ????????????
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
	     * ????????????????????????
	     * @param jsonRoot
	     * @return
	     */
	    public Map<String, String> queryZZKPCode(JSONObject jsonRoot) {
	    		Login();
	    		Map<String,String> resultMap=new HashMap<String,String>();
				ResponseEntity<String> result = null;
				//???????????????
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
	     * ?????????????????????
	     * @param formData
	     * @return
	     */
		public Map<String, String> updateFormData(String formData) {
			Map<String, String> returnMap = new HashMap<String, String>();
			Map<String, Object> mainTblDataMap = new HashMap<String, Object>();
			//F_PAEZ_MXID
			RuntimeDataDto runtimeDataDto = (RuntimeDataDto) JSONObject.parseObject(formData, RuntimeDataDto.class);
			List<KeyValueDto> mainTblDataList = runtimeDataDto.getMainTblData();// ????????????
			String mainTable=runtimeDataDto.getMainTblName();//?????????
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
			//????????????
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
			returnMap.put("msg", "???????????????");
			return returnMap;
		}
		
		
		/**
		 * ?????? ??????????????????
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
		 * ????????????????????? json
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
		 * ????????????????????? json
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
		 * ??????????????????????????????
		 * @return
		 */
	public JSONArray queryFormInfo(JSONObject queryJson) {
		JSONArray resultArr = null;
		if (Login()) {
			ResponseEntity<String> result = null;
			// ???????????????
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
	 * ??????????????????????????????????????????
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
	 * ???????????????????????????
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
