package org.ezplatform.travel.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ezplatform.core.dao.jpa.JpaBaseDao;
import org.ezplatform.core.service.BaseService;
import org.ezplatform.model.formmgr.runtime.dto.KeyValueDto;
import org.ezplatform.model.formmgr.runtime.dto.RuntimeDataDto;
import org.ezplatform.model.formmgr.runtime.dto.SubTblDataDto;
import org.ezplatform.travel.dao.CityEntityDao;
import org.ezplatform.travel.dao.TravelBaoganDao;
import org.ezplatform.travel.dao.TravelCanyinDao;
import org.ezplatform.travel.dao.TravelZhusuDao;
import org.ezplatform.travel.entity.ButieEntity;
import org.ezplatform.travel.entity.CityEntity;
import org.ezplatform.travel.entity.TravelBaoganEntity;
import org.ezplatform.travel.entity.TravelCanyinEntity;
import org.ezplatform.travel.entity.TravelEntity;
import org.ezplatform.travel.entity.TravelZhusuEntity;
import org.ezplatform.travel.util.TravelUtil;
import org.ezplatform.workflow.service.FinancialService;
import org.ezplatform.workflow.service.SystemFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;

/**
 * 员工报销流程计算
 * @author WEI
 *
 */
@Service(value="travelService")
public class TravelService2 extends BaseService<CityEntity, String>{
	 protected Logger logger = LoggerFactory.getLogger(TravelService2.class);
	@Override
	protected JpaBaseDao<CityEntity, String> getEntityDao() {
		// TODO Auto-generated method stub
		return cityEntityDao;
	}

	@Autowired
	private SystemFlowService systemFlowService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private CityEntityDao cityEntityDao;
	
	@Autowired
	private TravelBaoganDao travelBaoganDao;
	
	@Autowired
	private TravelCanyinDao travelCanyinDao;
	@Autowired
	private TravelZhusuDao travelZhusuDao;
	
	private String projectBM ;
	private String projectkm ;
	private String jineqianzhui;//用于员工报销流程 区分财务金额与个人填写金额
	private Map<String, String> projectNameMap = new HashMap<String, String>();

	private boolean ysgkFlag = true;
		
		/**
		 * 根据出差地点计算补贴
		 * @param travelList 出差行程  String [出发地，到达地，出发时间 ，到达时间]
		 * @param paramsMap 额外需要的参数 bxrUserId 报销人id，bgType 包干类型 
		 * @return
		 * @throws ParseException 
		 */
		public List<TravelEntity> getSubsidyAmount(List<String []> travelList,Map<String,String> paramsMap) throws ParseException {
			 List<TravelEntity> alltravelList=new ArrayList<>();
			if(travelList!=null) {
				Map<Integer,List<TravelEntity>> travelMap=new LinkedHashMap<Integer,List<TravelEntity>>();
				List<TravelEntity> travelListEntity = new ArrayList<TravelEntity>();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				int j=1;
				String chufaCity="";//出发地
				String chufaTime="";//出发时间
				String chufaDday="";//出发年月日
				TravelEntity travelEntity = new TravelEntity();
				for(int i=0,size=travelList.size();i<size;i++) {
					//出发行程
					String[] arr=travelList.get(i);
					String fromPlace=arr[0];//出发地
					String destination=arr[1];//到达地
					String beginTime=arr[2];//出发时间
					String endTime=arr[3];//到达时间
					if(i==0){
						chufaCity=fromPlace;
						chufaTime=sdf.format(sdf1.parse(beginTime));
						paramsMap.put("chufaCity", chufaCity);
						paramsMap.put("chufaTime", chufaTime);
						
					}
					if(travelEntity.getCityName()==null) {
						//起点
						travelEntity.setSfqd(true);
						travelEntity.setStarTime(beginTime);
						travelEntity.setChufaCity(fromPlace);
						travelEntity.setCityName(destination);
					}else {
						//先判断达到地是否为行程最开始的出发地
						if(destination.equals(chufaCity)) {
							//设置到达时间
							travelEntity.setEndTime(endTime);
							if(chufaTime.equals(sdf.format(sdf1.parse(endTime)))) {
								travelEntity.setOneday(true);
							}else {
								travelEntity.setOneday(false);
							}
							travelEntity.setSfzd(true);
							travelListEntity.add(travelEntity);
							travelMap.put(j, travelListEntity);
							j++;
							travelListEntity=new ArrayList<TravelEntity>();
							travelEntity=new TravelEntity();
						}else {
							if(fromPlace.equals(travelEntity.getCityName())) {
								travelEntity.setEndTime(beginTime);
								if(chufaTime.equals(sdf.format(sdf1.parse(beginTime)))) {
									travelEntity.setOneday(true);
								}else {
									travelEntity.setOneday(false);
								}
								travelListEntity.add(travelEntity);
								//设置新的目的地
								travelEntity=new TravelEntity();
								travelEntity.setCityName(destination);
								travelEntity.setStarTime(beginTime);
								travelEntity.setChufaCity(fromPlace);
							}
						}
						
					}
				}
				
				//循环计算行程补贴
				for (Map.Entry<Integer, List<TravelEntity>> m : travelMap.entrySet()) {
			        System.out.println("key:" + m.getKey() + " value:" + m.getValue());
			        List<TravelEntity> tempTravelList=m.getValue();
			        List<TravelEntity> newtravelList=getTravelButie(tempTravelList,paramsMap);
			        //获取实际差旅补贴金额	
			        alltravelList.addAll(newtravelList);
			    }
				
			}
			
			return alltravelList;
		}
		
	
		
		/**
		 * 获取行程补贴
		 * @param tempTravelList
		 * @param paramsMap
		 * @return
		 * @throws ParseException 
		 */
		private List<TravelEntity> getTravelButie(List<TravelEntity> travelList, Map<String, String> paramsMap) throws ParseException {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			String chufaCity=paramsMap.get("chufaCity");
			String chufaTime=paramsMap.get("chufaTime");
			Date chufaDate=sdf.parse(chufaTime);
			String userLevel=paramsMap.get("userLevel");
			String sfbg=paramsMap.get("sfbg");//是否包干
			List<TravelEntity> newtravelList=new ArrayList<TravelEntity>();
			//中转补贴
			Map<String,Integer> zhBtMap=new HashMap<String,Integer>();
			if(travelList!=null) {
				for(TravelEntity travelEntity:travelList) {
					String starTime=travelEntity.getStarTime();//行程开始时间
					String endTime =travelEntity.getEndTime();//行程结束时间
					String chufaCityTemp=travelEntity.getChufaCity();//行程开始地
					String cityName=travelEntity.getCityName();//行程到达地
					boolean sfzd=travelEntity.getSfzd();//是否为终点
					boolean sfqd=travelEntity.getSfqd();//是否为起点
					boolean onday=travelEntity.getOneday();//是否同一天到达
					Date startDate=sdf.parse(starTime);
					Date endDate=sdf.parse(endTime);
					String dayTime=sdf.format(startDate);
					int level=0;
					System.out.println("startDate=========>>>"+startDate);
					System.out.println("endDate=========>>>"+endDate);
					if(startDate.equals(endDate)) {
						if(startDate.equals(chufaDate)) {
							//如果行程是当天到达,则取补贴最高的城市
							 level = getCityLevelByName(cityName);
						}else {
							//表示中间出发去别地
							int tempLevel = getCityLevelByName(cityName);
							int tempLevel2 = getCityLevelByName(chufaCityTemp);
							tempLevel=tempLevel2<tempLevel?tempLevel2:tempLevel;
						}
						if(zhBtMap.get(dayTime)!=null) {
							if(level<zhBtMap.get(dayTime)) {
								zhBtMap.put(dayTime, level);
							}
						}else {
							zhBtMap.put(dayTime, level);
						}
						travelEntity.setDays(1);
						if(level==6) {
							travelEntity.setJkdqDays(1);
						}
						float money=getButieJineByCityLevel(level,1,userLevel,sfbg)+getButieJineByCityLevel(level,1,userLevel,sfbg);
						System.out.println(money+"======>>>money");
						money = money/2;
						//判断是否半天
						System.out.println("starTime=====>>>"+starTime);
						System.out.println("endTime=====>>>"+endTime);
						if( Integer.parseInt((starTime.split(" ")[1]).split(":")[0]+"")<=12 && Integer.parseInt((endTime.split(" ")[1]).split(":")[0]+"")<=12 ){
							money = money/2;
						}else if( Integer.parseInt((starTime.split(" ")[1]).split(":")[0]+"")>=12 && Integer.parseInt((endTime.split(" ")[1]).split(":")[0]+"")>=12 ){
							money = money/2;
						}

						travelEntity.setButiejine(money);
						newtravelList.add(travelEntity);
					}else {
							//判断中转地出发城市等级及到达城市等级
							int chufaLevel=getCityLevelByName(chufaCityTemp);//出发城市等级
							if(chufaCityTemp.equals(chufaCity)) {
								chufaLevel=100;//始发地 默认不参与补贴计算
							}
							int daodaLevel=getCityLevelByName(cityName);//到达城市等级
							if(daodaLevel<=chufaLevel) {
								//到达城市等级高于出发城市等级
								float days= TravelUtil.getAmountDays(starTime, endTime,sfqd,sfzd) ;//出发时间
								
								//设置住宿天数
								//float zhusuDays=TravelUtil.getZhusuDays(starTime, endTime);
								//travelEntity.setZhusudays(zhusuDays);
								
								if(!sfqd||!sfzd) {
									//获取补贴金额 中转默认加一天
									days++;
								}
								
								travelEntity.setDays(days);
								if(daodaLevel==6) {
									//设置艰苦地区天数
									travelEntity.setJkdqDays(days);
								}
								System.out.println("补贴天数======》"+days);
								float money=getButieJineByCityLevel(daodaLevel,days,userLevel,sfbg);
								travelEntity.setButiejine(money);
								
							}else {
								//到达城市等级低于于出发城市等级
								float days= TravelUtil.getAmountDays(starTime, endTime,sfqd,sfzd) ;//出发时间
								//设置住宿天数
								//float zhusuDays=TravelUtil.getZhusuDays(starTime, endTime);
								//travelEntity.setZhusudays(zhusuDays);
								//System.out.println("住宿天数======》"+zhusuDays);
								
								//获取补贴金额 中转默认加一天
								float money=getButieJineByCityLevel(daodaLevel,days,userLevel,sfbg)+getButieJineByCityLevel(chufaLevel,1,userLevel,sfbg);
								travelEntity.setDays(days+1);
								if(chufaLevel==6) {
									travelEntity.setJkdqDays(1);
								}
								travelEntity.setButiejine(money);
								
							}
							newtravelList.add(travelEntity);
					}
				}
			}
			System.out.println(newtravelList);
			
			return newtravelList;
		}



		/**
		 * 根据城市等级及用户等级获取餐饮补贴金额
		 * @param cityLevel
		 * @param days 
		 * @param userLevel 用户级别
		 * @param sfbg 是否包干
		 * @return
		 */
	private float getButieJineByCityLevel(int cityLevel, float days, String userLevel, String sfbg) {
		BigDecimal money = new BigDecimal(0);
		Map<Integer, String> butieMap = getButieMapByCityLevel(cityLevel, sfbg);
		System.out.println("daodaLevel============>" + cityLevel);
		money = new BigDecimal(days).multiply(new BigDecimal(butieMap.get(cityLevel)));
		System.out.println("days============>" + days);

		System.out.println("userLevel============>" + userLevel);
		String format = new BigDecimal(String.valueOf(money)).toString();
		float rr = Float.valueOf(format);
		return rr;

	}

		



		/**
		 *  获取城市最高等级城市
		 * @param cityList 
		 * @param chufaCity
		 * @return
		 */
	public String getCityLevelTop(List<String[]> cityList, String chufaCity, String chufaTime) {

		int level = 0;
		String cityNamereturn = "";
		if (cityList != null && cityList.size() > 0) {
			for (String[] arr : cityList) {
				String cityName = arr[0];
				String cityTime = arr[1];
				if (!chufaCity.equals(cityName)) {
					// 获取城市等级
					int tempLevel = getCityLevelByName(cityName);
					if (tempLevel < level) {
						level = tempLevel;
						cityNamereturn = cityName;
					}
				}
			}
		}

		return cityNamereturn;
	}
		
		/**
		 * 根据城市名称获取城市等级
		 * @param cityName
		 * @return
		 */
		private int getCityLevelByName(String cityName) {
			int level=0;
			CityEntity city=cityEntityDao.getCityEntityByCityName(cityName);
			
			String cityLevel=city.getCityLevel();
			if("北京市".equals(cityName)) {
				level=1;
			}else {
				if(StringUtils.isNoneEmpty(cityLevel)) {
					if(TravelUtil.TYLCITY.equals(cityLevel)) 
						level=2;
					else if(TravelUtil.YLCITY.equals(cityLevel))
						level=3;
					else if(TravelUtil.ELCITY.equals(cityLevel))
						level=4;
					else 
						level=5;
				}
			}
			if("是".equals(city.getSfjkdq())||"是".equals(city.getSfpydq())) {
				level=6;
			}
			return level;
		}


	public static void main(String[] args) {
		/*String [] arr1=new String[] {"合肥","武汉","2020-06-01 23:00:00","2020-06-02 02:00:00"};
		String [] arr2=new String[] {"武汉","北京","2020-06-02 08:00:00","2020-06-02 13:00:00"};
		String [] arr3=new String[] {"北京","上海","2020-06-05 11:00:00","2020-06-05 13:00:00"};
		String [] arr4=new String[] {"上海","合肥","2020-06-08 11:00:00","2020-06-08 13:00:00"};
		
		String [] arr5=new String[] {"合肥","武汉","2020-06-01 11:00:00","2020-06-01 13:00:00"};
		String [] arr6=new String[] {"武汉","北京","2020-06-01 15:00:00","2020-06-01 18:00:00"};
		String [] arr7=new String[] {"北京","上海","2020-06-05 11:00:00","2020-06-05 13:00:00"};
		String [] arr8=new String[] {"上海","合肥","2020-06-08 11:00:00","2020-06-08 13:00:00"};
		
		
		List<String []> travelList=new ArrayList<String []>();
		travelList.add(arr1);
		travelList.add(arr2);
		travelList.add(arr3);
		travelList.add(arr4);
		
		travelList.add(arr5);
		travelList.add(arr6);
		travelList.add(arr7);
		travelList.add(arr8);
		try {
			Map<String,String> map= new HashMap<String,String>();
			new TravelService2().getSubsidyAmount(travelList,map);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		TravelService2 s=new TravelService2();
		System.out.println(s.getNumber(""));
		;

	}


	/**
	 * 获取补贴金额
	 * @param formDataJson
	 * @param webUser
	 * @return
	 */
	public ButieEntity dealTravelData(String formData, String userId) {

		Map<String, Object> mainTblDataMap = new HashMap<String, Object>();// 表单主数据
		Map<String, String> extparamsMap = new HashMap<String, String>();
		Map<String, List<List<KeyValueDto>>> subTblDataMap = new HashMap<String, List<List<KeyValueDto>>>();
		Map<String, Object> tongyongDataMap = new HashMap<String, Object>();// 通用子表
		Map<String, Object> ccpDataMap = new HashMap<String, Object>();// 车船票
		Map<String, Object> zsfDataMap = new HashMap<String, Object>();// 住宿费
		Map<String, Object> dcfDataMap = new HashMap<String, Object>();// 打车费
		Map<String, Object> projectDataMap = new HashMap<String, Object>();// 项目拆分
		BigDecimal chailvbutie=new BigDecimal(0);//差旅补贴
		BigDecimal zhusubutie=new BigDecimal(0);//住宿补贴
		BigDecimal jiankudiqubutie=new BigDecimal(0);//住宿补贴
		BigDecimal jkdqDays=new BigDecimal(0);//艰苦地区天数
		String msg="";
		
		
		RuntimeDataDto runtimeDataDto = (RuntimeDataDto) JSONObject.parseObject(formData, RuntimeDataDto.class);
		// 获取表单数据
		String mainTblName = runtimeDataDto.getMainTblName();// 表名
		List<KeyValueDto> mainTblDataList = runtimeDataDto.getMainTblData();// 主表数据
		mainTblDataMap = getValueMap(mainTblDataList);
		
		String baoxiaoren = "";// 报销人
		String baoxiaorenLevel = "";
		String sfdxmbx = "";// 是否多项目报销
		String sfdlbx=mainTblDataMap.get("dlbx").toString(); //是否代理报销
		String fylx=mainTblDataMap.get("fylx").toString();//费用类型 121 是差旅费
		
		ButieEntity butie=new ButieEntity();
		//当报销类型为差旅费时计算补贴
		if ("121".equals(fylx)) {

			String userLevel = "1";// 1 普通员工 2 一级部门负责人及专业序列5级以上 3 分管emt
			if ("true".equals(sfdlbx)) {
				String tempUser = mainTblDataMap.get("dlbxr").toString();
				if (!"".equals(tempUser)) {
					JSONArray jsonArr = (JSONArray) JSONArray.parse(mainTblDataMap.get("dlbxr").toString());
					JSONObject josn = jsonArr.getJSONObject(0);
					userLevel = getUserLevel(josn.getString("scopeValue"), 2);
				}

			} else {
				userLevel = getUserLevel(userId, 2);
			}
			extparamsMap.put("userLevel", userLevel);// 用户级别
			String sfbg = mainTblDataMap.get("btlx").toString();
			extparamsMap.put("sfbg", sfbg);
			logger.error("mainTblDataMap================>" + mainTblDataMap);

			// 子表数据
			List<SubTblDataDto> subTblList = runtimeDataDto.getSubTbl();
			for (SubTblDataDto subDto : subTblList) {
				String subTableName = subDto.getSubTblName();
				List<List<KeyValueDto>> tempData = subDto.getSubTblData();
				subTblDataMap.put(subTableName, tempData);
			}
			List<String []> zhaodaifeiList=new ArrayList<String []>();//招待费
			// 通用子表数据
			List<List<KeyValueDto>> tongyongList = subTblDataMap.get("ygbxtyzb");
			if (tongyongList != null && tongyongList.size() > 0) {
				for (int i = 0, size = tongyongList.size(); i < size; i++) {
					Map<String, Object> tempMap = getValueMap(tongyongList.get(i));
					String yskmbm = tempMap.get("yskmbm") == null ? "" : tempMap.get("yskmbm").toString(); // 预算科目编码
					String xfyrq = tempMap.get("xfyrq") == null ? "" : tempMap.get("xfyrq").toString(); // 费用日期
					String bxje = tempMap.get("bxje") == null ? "" : tempMap.get("bxje").toString(); // 报销金额
					if (StringUtils.isEmpty(bxje)) {
						continue;
					}
					if("FYXM09_SYS".equals(yskmbm)) {
						String[] arr = new String[] { xfyrq, bxje};
						zhaodaifeiList.add(arr);
					}
				}
				
			}

			// 获取车船票行程
			List<String[]> travelList = new ArrayList<String[]>();
			List<TravelEntity> travelEntitlList = new ArrayList<TravelEntity>();
			List<List<KeyValueDto>> ccpList = subTblDataMap.get("ccf");
			if (ccpList != null && ccpList.size() > 0) {
				for (int i = 0, size = ccpList.size(); i < size; i++) {
					Map<String, Object> tempMap = getValueMap(ccpList.get(i));
					logger.error("tempMap================>" + tempMap);
					String cfcs = tempMap.get("cfcs") == null ? "" : tempMap.get("cfcs").toString(); // 出发城市
					String ddcs = tempMap.get("ddcs") == null ? "" : tempMap.get("ddcs").toString(); // 到达城市
					String xcfsj = tempMap.get("xcfsj") == null ? "" : tempMap.get("xcfsj").toString(); // 出发时间
					String xddsj = tempMap.get("xddsj") == null ? "" : tempMap.get("xddsj").toString(); // 到达时间
					if (StringUtils.isEmpty(cfcs)) {
						continue;
					}
					String[] arr = new String[] { cfcs, ddcs, xcfsj, xddsj };
					travelList.add(arr);
				}
				try {
					travelEntitlList = getSubsidyAmount(travelList, extparamsMap);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			TravelUtil util=new TravelUtil();
			//扣除通用子表招待费
			for (TravelEntity po : travelEntitlList) {
				jkdqDays = jkdqDays.add(new BigDecimal(po.getJkdqDays()));
				String startTime=po.getStarTime();
				String endTime=po.getEndTime();
				String cityName=po.getCityName();
				for(String [] arr:zhaodaifeiList) {
					String time=arr[0];
					if(util.isEffectiveDate(time, startTime, endTime)) {
						String jine=travelCanyinDao.getCanyinjine(cityName);
						chailvbutie = chailvbutie.subtract(new BigDecimal(jine));
					}
				}
				chailvbutie = chailvbutie.add(new BigDecimal(po.getButiejine()));
			}
			// 艰苦地区补贴
			jiankudiqubutie = jkdqDays.multiply(new BigDecimal(200));

			// 住宿费
			List<List<KeyValueDto>> zsfList = subTblDataMap.get("zsf");
			List<String[]> zhuSuList = new ArrayList<String[]>();
			if (zsfList != null && zsfList.size() > 0) {
				for (int i = 0, size = zsfList.size(); i < size; i++) {
					Map<String, Object> tempMap = getValueMap(zsfList.get(i));
					logger.error("tempMap================>" + tempMap);
					String cs = tempMap.get("cs") == null ? "" : tempMap.get("cs").toString(); // 城市
					String zsfs = tempMap.get("zsfs") == null ? "" : tempMap.get("zsfs").toString(); // 住宿方式
					String sjrzts = tempMap.get("sjrzts") == null ? "" : tempMap.get("sjrzts").toString(); // 实际入住天数
					String bxje = tempMap.get("bxje") == null ? "" : tempMap.get("bxje").toString(); // 报销金额
					if (StringUtils.isEmpty(cs)) {
						continue;
					}
					String[] arr = new String[] { cs, zsfs, sjrzts, bxje };
					zhuSuList.add(arr);
				}
				zhuSuList = getZhusuAmount(zhuSuList, extparamsMap);
				for (String[] arr : zhuSuList) {
					String cs = arr[0];
					String zsfs = arr[1];
					String sjrzts = arr[2];
					String bxje = arr[3];
					String sfcb = arr[4];// 是否超标
					String butiejine = arr[5];// 补贴金额
					if ("true".equals(sfcb)) {
						msg += "城市：" + cs + "，报销金额：" + bxje + "、";
					}
					zhusubutie = zhusubutie.add(new BigDecimal(butiejine).multiply(new BigDecimal(sjrzts)));
				}
				if (msg.endsWith("、")) {
					msg = msg.substring(0, msg.length() - 1);
				}
			}

			butie = new ButieEntity(chailvbutie, zhusubutie, jiankudiqubutie, msg, sfbg);
		}
		
		return butie;

	}
	
	


	
	/**
	 * 获取用户级别
	 * @param tempUser
	 * @param type  1 关联关系表 2 用户表主键
	 * @return
	 */
	private String getUserLevel(String userId, int type) {
		String userlevel="1";
		String sql="";
		if(type==1) {
			sql="select select useraccount, userlevel from ( c.fgemtnumber useraccount  ,3 as userlevel from sys_user a left join  ygbx_scope b on a.id=b.scopevalue  left join  XFSMZJK.T_KDXF_DEPARTMENT@toerp  c on a.login_name=c.fgemtnumber where nvl(c.fgemtnumber,'-1')!='-1' and b.fielddatavalue =:userId\r\n" + 
					"\r\n" + 
					"union  all\r\n" + 
					"select c.bmfzrgh useraccount,2 as userlevel from sys_user a left join  ygbx_scope b on a.id=b.scopevalue  left join  XFSMZJK.T_KDXF_DEPARTMENT@toerp  c on a.login_name=c.bmfzrgh where nvl(c.bmfzrgh,'-1')!='-1' and c.fpnumber in(select fnumber from  XFSMZJK.T_KDXF_DEPARTMENT@toerp where fpnumber is null ) and b.fielddatavalue=:userId\r\n" + 
					"\r\n" + 
					"union all select c.FUSERNAME useraccount ,2 as userlevel from sys_user a left join  ygbx_scope b on a.id=b.scopevalue  left join  XFSMZJK.T_KDXF_STAFF@toerp  c on a.login_name=c.FUSERNAME where fzjnumber like 'P%' and to_number(replace(fzjnumber,'P','')) between 5 and 98 and  b.fielddatavalue=:userId  "
					+ ") d order by userlevel desc ";
		}else {
			sql=" select useraccount, userlevel from ( select c.fgemtnumber useraccount  ,3 as userlevel from sys_user a  left join  XFSMZJK.T_KDXF_DEPARTMENT@toerp  c on a.login_name=c.fgemtnumber where nvl(c.fgemtnumber,'-1')!='-1' and a.id =:userId\r\n "
					+"union  all\r\n" + 
					"select c.bmfzrgh useraccount,2 as userlevel from sys_user a  left join  XFSMZJK.T_KDXF_DEPARTMENT@toerp  c on a.login_name=c.bmfzrgh where nvl(c.bmfzrgh,'-1')!='-1' and c.fpnumber in(select fnumber from  XFSMZJK.T_KDXF_DEPARTMENT@toerp where fpnumber is null ) and a.id=:userId\r\n" + 
					"\r\n" + 
					"union all select c.FUSERNAME useraccount ,2 as userlevel from sys_user a  left join  XFSMZJK.T_KDXF_STAFF@toerp  c on a.login_name=c.FUSERNAME where fzjnumber like 'P%' and to_number(replace(fzjnumber,'P','')) between 5 and 98 and  a.id=:userId "
					+ ") d order by userlevel desc ";
	
		}
		
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("userId", userId);
		List list=super.findByListNativeQuery(sql, "", paramMap);
		if(list!=null&&list.size()>0) {
			userlevel = org.ezplatform.util.StringUtils.null2String(((Map)list.get(0)).get("userlevel"));
		}
		return userlevel;
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
	 * 获取城市的餐饮补贴标准
	 * @param cityLevel
	 * @param sfbg
	 * @return
	 */
	private Map<Integer, String> getButieMapByCityLevel(int cityLevel, String sfbg) {
		Map<Integer, String> butieMap=new HashMap<Integer,String>();
		String cityFl="";
		switch(cityLevel){
		case 0:
			cityFl=TravelUtil.SLCITY;
			break;
		case 1:
			cityFl=TravelUtil.TYLCITY;
			break;
		case 2:
			cityFl=TravelUtil.TYLCITY;
			break;
		case 3:
			cityFl=TravelUtil.YLCITY;
			break;
		case 4:
			cityFl=TravelUtil.ELCITY;
			break;
		default:
			cityFl=TravelUtil.SLCITY;
			break;
		}
		if("1".equals(sfbg)) {
			//包干补贴
			TravelBaoganEntity baoganEntity=travelBaoganDao.getButieByCityLevel(cityFl);
			butieMap.put(cityLevel, baoganEntity.getBgbz());
		}else {
			TravelCanyinEntity baoganEntity=travelCanyinDao.getButieByCityLevel(cityFl);
			butieMap.put(cityLevel, String.valueOf(new BigDecimal(baoganEntity.getZaocanbz()).add(new BigDecimal(baoganEntity.getZhongcanbz())).add(new BigDecimal(baoganEntity.getWancanbz()))));
		}
		return butieMap;
	}
	
	/**
	 * 获取住宿补贴
	 * @param zhuSuList
	 * @param extparamsMap
	 * @return
	 */
	private List<String[]> getZhusuAmount(List<String[]> zhuSuList, Map<String, String> extparamsMap) {
		List<String[]> returnSuList = new ArrayList<String[]>();
		String userLevel = extparamsMap.get("userLevel");
		if (zhuSuList != null && zhuSuList.size() > 0) {
			for (int i = 0, size = zhuSuList.size(); i < size; i++) {
				String[] arr = zhuSuList.get(i);
				String cs = arr[0];
				String zsfs = arr[1];
				String sjrzts = arr[2];
				String bxje = arr[3];
				String sfcb = "false";// 是否超标
				String butiejine = "0";// 补贴金额
				// 根据城市及人员级别获取住宿标准
				BigDecimal zhusubiaozhun = new BigDecimal(getZhusuBiaozhunByUserLevelAndCity(cs, userLevel));
				BigDecimal dj = new BigDecimal(bxje).divide(new BigDecimal(sjrzts),2,BigDecimal.ROUND_HALF_UP);
				if (dj.compareTo(zhusubiaozhun) > -1) {
					sfcb = "true";
				} else {
					// 计算补贴金额
					BigDecimal chae = zhusubiaozhun.subtract(dj);
					if (chae.compareTo(new BigDecimal(100)) > -1) {
						butiejine = "50";
					} else {
						butiejine = chae.divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP).toString();
					}
				}
				String[] arr2 = new String[] { cs, zsfs, sjrzts, bxje, sfcb, butiejine };
				returnSuList.add(arr2);
			}
		}

		return returnSuList;
	}



	/**
	 * 获取住宿标准
	 * @param cityName 
	 * @param userLevel 1 普通员工 2 一级部门负责人及专业序列5级及以上员工  3 公司EMT成员
	 * @return
	 */
	private String getZhusuBiaozhunByUserLevelAndCity(String cityName, String userLevel) {
		String zhusubutie = "";
		CityEntity cityEntity = cityEntityDao.getCityEntityByCityName(cityName);
		String cityLevel = cityEntity.getCityLevel();
		if ("北京市".equals(cityName)) {
			cityLevel = cityName;
		}
		TravelZhusuEntity zhusuPo = travelZhusuDao.getTravelZhusuEntityByCityLevel(cityLevel);
		if ("1".equals(userLevel))
			zhusubutie = zhusuPo.getYbyg();
		else if ("2".equals(userLevel))
			zhusubutie = zhusuPo.getMiddleyg();
		else if ("3".equals(userLevel))
			zhusubutie = zhusuPo.getGsemtcy();

		return zhusubutie;
	}


	/**
	 * 预算管控处理
	 * @param formData
	 * @param webUser
	 * @param operate  start 发起环节  complete 财务办理环节
	 * @return
	 */
	public String dealYsgkData(String formData, String userId, String operate) {
		
		if("complete".equals(operate)) {
			jineqianzhui="hd";
		}else {
			jineqianzhui="";
		}
		Map<String, Object> mainTblDataMap = new HashMap<String, Object>();// 表单主数据
		Map<String, BigDecimal> projectMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> projectAllMap = new HashMap<String, BigDecimal>();
		Map<String, List<List<KeyValueDto>>> subTblDataMap = new HashMap<String, List<List<KeyValueDto>>>();
		
		RuntimeDataDto runtimeDataDto = (RuntimeDataDto) JSONObject.parseObject(formData, RuntimeDataDto.class);
		// 获取表单数据
		String mainTblName = runtimeDataDto.getMainTblName();// 表名
		List<KeyValueDto> mainTblDataList = runtimeDataDto.getMainTblData();// 主表数据
		mainTblDataMap = getValueMap(mainTblDataList);

		// 子表数据
		List<SubTblDataDto> subTblList = runtimeDataDto.getSubTbl();
		for (SubTblDataDto subDto : subTblList) {
			String subTableName = subDto.getSubTblName();
			List<List<KeyValueDto>> tempData = subDto.getSubTblData();
			subTblDataMap.put(subTableName, tempData);
		}

		String jieduanFlag = "";// 是否按阶段报销
		String sfdxmbx = mainTblDataMap.get("sfdxmbx")==null?"":mainTblDataMap.get("sfdxmbx").toString();// 是否多项目报销
		if ("ygbx".equals(mainTblName)) {
			// 员工报销

			// 获取车船票行程
			List<List<KeyValueDto>> ccpList = subTblDataMap.get("ccf");
			if (ccpList != null && ccpList.size() > 0) {
				projectMap = getprojectMapTwo(ccpList, projectMap,"","");
			}
			// 获取住宿费
			List<List<KeyValueDto>> zsfList = subTblDataMap.get("zsf");
			if (zsfList != null && zsfList.size() > 0) {
				projectMap = getprojectMapTwo(zsfList, projectMap,"","");
			}
			// 获取打车费
			List<List<KeyValueDto>> dcf = subTblDataMap.get("dcf");
			if (dcf != null && dcf.size() > 0) {
				projectMap = getprojectMapTwo(dcf, projectMap,"","");
			}
			// 获取补贴金额
			String bgbt = getNumber(mainTblDataMap.get(jineqianzhui+ "bgbt").toString());// 包干补贴
			String fbgbt = getNumber(mainTblDataMap.get(jineqianzhui+ "fbgbt").toString());// 非包干补贴
			String zsbt = getNumber(mainTblDataMap.get(jineqianzhui+ "zsbt").toString());// 住宿补贴
			String jkdqbt = getNumber(mainTblDataMap.get(jineqianzhui+ "jkdqbt").toString());// 艰苦地区补贴

			if("".equalsIgnoreCase(bgbt) ){
				bgbt = "0";
			}

			if("".equalsIgnoreCase(fbgbt) ){
				fbgbt = "0";
			}

			if("".equalsIgnoreCase(zsbt) ){
				zsbt = "0";
			}

			if("".equalsIgnoreCase(jkdqbt) ){
				jkdqbt = "0";
			}
			
			BigDecimal butieZongjie = new BigDecimal(bgbt).add(new BigDecimal(fbgbt)).add(new BigDecimal(zsbt))
					.add(new BigDecimal(jkdqbt));
			// 获取通用子表
			List<List<KeyValueDto>> tozbList = subTblDataMap.get("ygbxtyzb");

			if ("true".equals(sfdxmbx)) {
				if (tozbList != null && tozbList.size() > 0) {
					for (int i = 0, size = tozbList.size(); i < size; i++) {
						Map<String, Object> tempMap = getValueMap(tozbList.get(i));
						logger.error("tempMap================>" + tempMap);
						String xmbm = tempMap.get("xmbm") == null ? "" : tempMap.get("xmbm").toString(); // 项目编码
						
						String xmlb = tempMap.get("xmlb") == null ? "" : tempMap.get("xmlb").toString(); // 项目类别
						String yskmbm = tempMap.get("yskmbm") == null ? "" : tempMap.get("yskmbm").toString(); // 预算科目编码(费用类型编码)
						String xmmc = tempMap.get("xmmc") == null ? "" : tempMap.get("xmmc").toString(); // 项目名称
						String yskm = tempMap.get("yskm") == null ? "" : tempMap.get("yskm").toString(); 
						projectNameMap.put(xmbm+yskmbm, xmmc+"中"+yskm);
						String bxje = tempMap.get(jineqianzhui+ "bxje") == null ? "" : tempMap.get(jineqianzhui+ "bxje").toString(); // 报销金额
						String key = xmbm + "!@#" + xmlb + "!@#" + yskmbm;

						if (StringUtils.isEmpty(bxje)) {
							continue;
						}
						if (projectMap.get(key) != null) {
							BigDecimal value = projectMap.get(key);
							BigDecimal value2 = new BigDecimal(bxje).add(value);
							projectMap.put(key, value2);
						} else {
							BigDecimal value = new BigDecimal(bxje);
							projectMap.put(key, value);
						}
					}
				}

				// 获取项目信息
				List<List<KeyValueDto>> projectList = subTblDataMap.get("clfdxmftzb");
				if (projectList != null && projectList.size() > 0) {

					for (Map.Entry<String, BigDecimal> m : projectMap.entrySet()) {
						System.out.println("key:" + m.getKey() + " value:" + m.getValue());
						String key = m.getKey();
						if (key.indexOf("!@#") == -1) {
							BigDecimal value = m.getValue();
							for (int i = 0, size = projectList.size(); i < size; i++) {
								// 根据项目查询预算
								Map<String, Object> tempMap = getValueMap(projectList.get(i));
								String xmbm = tempMap.get("xmbm") == null ? "" : tempMap.get("xmbm").toString(); // 项目编码
								String xmlb = tempMap.get("xmlb") == null ? "" : tempMap.get("xmlb").toString(); // 项目类别
								String xmmc = tempMap.get("xmmc") == null ? "" : tempMap.get("xmmc").toString(); // 项目名称
								projectNameMap.put(xmbm+key, xmmc+"中差旅费");
								String clfftbl = tempMap.get("clfftbl") == null ? ""
										: tempMap.get("clfftbl").toString();// 项目拆分比例
								BigDecimal b = new BigDecimal(Float.valueOf(clfftbl)).divide(new BigDecimal(100.00), 6,
										BigDecimal.ROUND_HALF_UP);
								String tempKey = xmbm + "!@#" + xmlb + "!@#" + key;
								// 将补贴总金额加入差旅费中
								BigDecimal tempValue = value.add(butieZongjie);
								projectAllMap.put(tempKey, tempValue.multiply(b));
							}
						}else {
							projectAllMap.put(key, m.getValue());
							
						}

					}

				}

			} else {
				// 单项目报销
				String xmbm = mainTblDataMap.get("xmbm") == null ? "" : mainTblDataMap.get("xmbm").toString(); // 项目编码
				String xmlb = mainTblDataMap.get("xmlb") == null ? "" : mainTblDataMap.get("xmlb").toString(); // 项目类别
				String xmmc = mainTblDataMap.get("xmmc") == null ? "" : mainTblDataMap.get("xmmc").toString(); // 项目名称
				if (tozbList != null && tozbList.size() > 0) {
					projectMap = getprojectMapTwo(tozbList, projectMap,xmmc,xmbm);
				}
				
				
				for (Map.Entry<String, BigDecimal> m : projectMap.entrySet()) {
					System.out.println("key:" + m.getKey() + " value:" + m.getValue());
					String key = m.getKey();
					if(projectNameMap.get(xmbm+key)==null) {
						projectNameMap.put(xmbm+key, xmmc+"中差旅费");
					}
					
					BigDecimal value = m.getValue();
					String tempKey = xmbm + "!@#" + xmlb + "!@#" + key;
					// 将补贴总金额加入差旅费中
					BigDecimal tempValue = value.add(butieZongjie);
					//projectMap.put(tempKey, tempValue);
					//projectMap.remove(key);
					projectAllMap.put(tempKey, tempValue);
				}

			}
		} else if ("rlxzlbx".equals(mainTblName)) {
			// 人力行政类报销
			List<List<KeyValueDto>> rlxzlbxtyzbList = subTblDataMap.get("rlxzlbxtyzb");
			//获取借款信息
			String cjklc= mainTblDataMap.get("cjklc").toString();
			Map<String,BigDecimal> jiekuanMap=financialService.getJiekuanMap(cjklc);
			if ("true".equals(sfdxmbx)) {
				if (rlxzlbxtyzbList != null && rlxzlbxtyzbList.size() > 0) {
					for (int i = 0, size = rlxzlbxtyzbList.size(); i < size; i++) {
						Map<String, Object> tempMap = getValueMap(rlxzlbxtyzbList.get(i));
						logger.error("tempMap================>" + tempMap);
						String xmbm = tempMap.get("xmbm") == null ? "" : tempMap.get("xmbm").toString(); // 项目编码
						String xmlb = tempMap.get("xmlb") == null ? "" : tempMap.get("xmlb").toString(); // 项目类别
						String yskmbm = tempMap.get("yskmbm") == null ? "" : tempMap.get("yskmbm").toString(); // 预算科目编码(费用类型编码)
						String bxje = tempMap.get("bxje") == null ? "" : tempMap.get("bxje").toString(); // 报销金额
						String key = xmbm + "!@#" + xmlb + "!@#" + yskmbm;
						String xmmc = tempMap.get("xmmc") == null ? "" : tempMap.get("xmmc").toString(); // 项目名称
						String yskm = tempMap.get("yskm") == null ? "" : tempMap.get("yskm").toString(); 
						projectNameMap.put(xmbm+yskmbm, xmmc+"中"+yskm);
						if (StringUtils.isEmpty(bxje)) {
							continue;
						}
						
						if (projectMap.get(key) != null) {
							BigDecimal value = projectMap.get(key);
							BigDecimal value2 = new BigDecimal(bxje).add(value);
							projectMap.put(key, value2);
						} else {
							BigDecimal value = new BigDecimal(bxje);
							projectMap.put(key, value);
						}
					}
				}
				
				
				//扣除借款信息
				for (Map.Entry<String, BigDecimal> m : projectMap.entrySet()) {
					System.out.println("key:" + m.getKey() + " value:" + m.getValue());
					String key=m.getKey();
					BigDecimal value=m.getValue();
					String[] arr=key.split("!@#");
					//扣去借款金额
					if(jiekuanMap.get(arr[0] + "!@#"  + arr[2])!=null) {
						value=value.subtract(jiekuanMap.get(arr[0] + "!@#"  + arr[2]));
					}
					projectAllMap.put(key, value);
				}
				
				

			} else {
				
				String xmmc = mainTblDataMap.get("xmmc") == null ? "" : mainTblDataMap.get("xmmc").toString(); // 项目名称
				String xmbm = mainTblDataMap.get("xmbm") == null ? "" : mainTblDataMap.get("xmbm").toString(); // 项目编码
				String xmlb = mainTblDataMap.get("xmlb") == null ? "" : mainTblDataMap.get("xmlb").toString(); // 项目类别
				
				// 单项目报销
				if (rlxzlbxtyzbList != null && rlxzlbxtyzbList.size() > 0) {
					projectMap = getprojectMapTwo(rlxzlbxtyzbList, projectMap,xmmc,xmbm);
				}
				
				for (Map.Entry<String, BigDecimal> m : projectMap.entrySet()) {
					System.out.println("key:" + m.getKey() + " value:" + m.getValue());
					String key = m.getKey();
					BigDecimal value = m.getValue();
					String tempKey = xmbm + "!@#" + xmlb + "!@#" + key;
					//projectNameMap.put(xmbm+key, xmmc+"中");
					
					//扣除借款金额
					if(jiekuanMap.get(xmbm + "!@#"  + key)!=null) {
						value=value.subtract(jiekuanMap.get(xmbm + "!@#"  + key));
					}
					
					projectAllMap.put(tempKey, value);
					//projectMap.put(tempKey, value);
					//projectMap.remove(key);
				}

			}

		} else if ("dgcglbx".equals(mainTblName)) {
			// 采购类报销
			List<List<KeyValueDto>> rlxzlbxtyzbList = subTblDataMap.get("dgcglbxzb");
			//获取借款信息
			String cjklc= mainTblDataMap.get("cjklc").toString();
			Map<String,BigDecimal> jiekuanMap=financialService.getJiekuanMap(cjklc);
			if ("true".equals(sfdxmbx)) {
				if (rlxzlbxtyzbList != null && rlxzlbxtyzbList.size() > 0) {
					for (int i = 0, size = rlxzlbxtyzbList.size(); i < size; i++) {
						Map<String, Object> tempMap = getValueMap(rlxzlbxtyzbList.get(i));
						String xmbm = tempMap.get("xmbm") == null ? "" : tempMap.get("xmbm").toString(); // 项目编码
						String xmlb = tempMap.get("xmlb") == null ? "" : tempMap.get("xmlb").toString(); // 项目类别
						String yskmbm = tempMap.get("yskmbm") == null ? "" : tempMap.get("yskmbm").toString(); // 预算科目编码(费用类型编码)
						String bxje = tempMap.get("bxje") == null ? "" : tempMap.get("bxje").toString(); // 报销金额
						String key = xmbm + "!@#" + xmlb + "!@#" + yskmbm;
						String xmmc = tempMap.get("xmmc") == null ? "" : tempMap.get("xmmc").toString(); // 项目名称
						String yskm = tempMap.get("yskm") == null ? "" : tempMap.get("yskm").toString(); 
						projectNameMap.put(xmbm+yskmbm, xmmc+"中"+yskm);
						if (StringUtils.isEmpty(bxje)) {
							continue;
						}
						if (projectMap.get(key) != null) {
							BigDecimal value = projectMap.get(key);
							BigDecimal value2 = new BigDecimal(bxje).add(value);
							projectMap.put(key, value2);
						} else {
							BigDecimal value = new BigDecimal(bxje);
							projectMap.put(key, value);
						}
					}
					
					//扣除借款信息
					for (Map.Entry<String, BigDecimal> m : projectMap.entrySet()) {
						System.out.println("key:" + m.getKey() + " value:" + m.getValue());
						String key=m.getKey();
						BigDecimal value=m.getValue();
						String[] arr=key.split("!@#");
						//扣去借款金额
						if(jiekuanMap.get(arr[0] + "!@#"  + arr[2])!=null) {
							value=value.subtract(jiekuanMap.get(arr[0] + "!@#"  + arr[2]));
						}
						projectAllMap.put(key, value);
					}
				}

			} else {
				
				String xmbm = mainTblDataMap.get("xmbm") == null ? "" : mainTblDataMap.get("xmbm").toString(); // 项目编码
				String xmlb = mainTblDataMap.get("xmlb") == null ? "" : mainTblDataMap.get("xmlb").toString(); // 项目类别
				String xmmc = mainTblDataMap.get("xmmc") == null ? "" : mainTblDataMap.get("xmmc").toString(); // 项目名称
				// 单项目报销
				if (rlxzlbxtyzbList != null && rlxzlbxtyzbList.size() > 0) {
					projectMap = getprojectMapTwo(rlxzlbxtyzbList, projectMap, xmmc, xmbm);
				}
				
				for (Map.Entry<String, BigDecimal> m : projectMap.entrySet()) {
					System.out.println("key:" + m.getKey() + " value:" + m.getValue());
					String key = m.getKey();
					BigDecimal value = m.getValue();
					//projectNameMap.put(xmbm+key, xmmc+"中");
					String tempKey = xmbm + "!@#" + xmlb + "!@#" + key;
					//扣除借款金额
					if(jiekuanMap.get(xmbm + "!@#"  + key)!=null) {
						value=value.subtract(jiekuanMap.get(xmbm + "!@#"  + key));
					}
					projectAllMap.put(tempKey, value);
					//projectMap.remove(key);
				}

			}
		}else if ("jk".equals(mainTblName)) {
				// 借款流程
				List<List<KeyValueDto>> jksqzbbList = subTblDataMap.get("jksqzb");
				//获取借款信息
					String xmbm = mainTblDataMap.get("xmbm") == null ? "" : mainTblDataMap.get("xmbm").toString(); // 项目编码
					String xmlb = mainTblDataMap.get("xmlb") == null ? "" : mainTblDataMap.get("xmlb").toString(); // 项目类别
					String xmmc = mainTblDataMap.get("xmmc") == null ? "" : mainTblDataMap.get("xmmc").toString(); // 项目名称
					// 单项目报销
					if (jksqzbbList != null && jksqzbbList.size() > 0) {
						projectMap = getprojectMapJK(jksqzbbList, projectMap, xmmc, xmbm);
					}
					
					for (Map.Entry<String, BigDecimal> m : projectMap.entrySet()) {
						System.out.println("key:" + m.getKey() + " value:" + m.getValue());
						String key = m.getKey();
						BigDecimal value = m.getValue();
						String tempKey = xmbm + "!@#" + xmlb + "!@#" + key;
						projectAllMap.put(tempKey, value);
						
					

				}
		}
		
		    //根据项目，预算科目，是否按照阶段判断是否超预算
			String msg="";
			boolean flag=queryKingdeeFYYS(projectAllMap);
				if(!flag) {
					msg=projectNameMap.get(projectBM+projectkm);

				}
				
		return msg;
	}
	
	
	/**
	 * 处理数字为空
	 * @param string
	 * @return
	 */
	private String getNumber(String number) {
		if(!StringUtils.isNumeric(number)) {
			number="0";
		}
		return number;
	}
	

	public Map<String,BigDecimal> getprojectMap(List<List<KeyValueDto>> ccpList, Map<String, BigDecimal> projectMap){

		for (int i = 0, size = ccpList.size(); i < size; i++) {
			Map<String, Object> tempMap = getValueMap(ccpList.get(i));
			logger.error("tempMap================>"+tempMap);
			String xmbm = tempMap.get("xmbm") == null ? "" : tempMap.get("xmbm").toString(); // 项目编码
			String xmlb = tempMap.get("xmlb") == null ? "" : tempMap.get("xmlb").toString(); // 项目类别
			String yskmbm = tempMap.get("yskmbm") == null ? "" : tempMap.get("yskmbm").toString(); //预算科目编码(费用类型编码)
			String bxje= tempMap.get("bxje") == null ? "" : tempMap.get("bxje").toString(); //报销金额
			String key=xmbm+"!@#"+xmlb+"!@#"+yskmbm;
			
			if (StringUtils.isEmpty(bxje)) {
				continue;
			}
			if(projectMap.get(key)!=null) {
				BigDecimal value=projectMap.get(key);
				BigDecimal value2=new BigDecimal(bxje).add(value);
				projectMap.put(key, value2);
			}else {
				BigDecimal value=new BigDecimal(bxje);
				projectMap.put(key, value);
			}
		}
		return projectMap;
	}
	public Map<String,BigDecimal> getprojectMapTwo(List<List<KeyValueDto>> ccpList, Map<String, BigDecimal> projectMap, String xmmc,String xmbm){
		
		for (int i = 0, size = ccpList.size(); i < size; i++) {
			Map<String, Object> tempMap = getValueMap(ccpList.get(i));
			logger.error("tempMap================>"+tempMap);
			/*String xmbm = tempMap.get("xmbm") == null ? "" : tempMap.get("xmbm").toString(); // 项目编码
			String xmlb = tempMap.get("xmlb") == null ? "" : tempMap.get("xmlb").toString(); // 项目类别
*/			String yskmbm = tempMap.get("yskmbm") == null ? "" : tempMap.get("yskmbm").toString(); //预算科目编码(费用类型编码)
			String yskm = tempMap.get("yskm") == null ? "" : tempMap.get("yskm").toString(); //预算科目编码(费用类型编码)
			
			String bxje= tempMap.get(jineqianzhui+ "bxje") == null ? "" : tempMap.get(jineqianzhui+ "bxje").toString(); //报销金额
			String key=yskmbm;
			if(StringUtils.isNotEmpty(xmmc)) {
				projectNameMap.put(xmbm+yskmbm, xmmc+"中"+yskm);
			}
			if (StringUtils.isEmpty(bxje)) {
				continue;
			}
			if(projectMap.get(key)!=null) {
				BigDecimal value=projectMap.get(key);
				BigDecimal value2=new BigDecimal(bxje).add(value);
				projectMap.put(key, value2);
			}else {
				BigDecimal value=new BigDecimal(bxje);
				projectMap.put(key, value);
			}
		}
		return projectMap;
	}
	
	
	/**
	 * 借款流程子表处理
	 * @param ccpList
	 * @param projectMap
	 * @param xmmc
	 * @param xmbm
	 * @return
	 */
public Map<String,BigDecimal> getprojectMapJK(List<List<KeyValueDto>> ccpList, Map<String, BigDecimal> projectMap, String xmmc,String xmbm){
		
		for (int i = 0, size = ccpList.size(); i < size; i++) {
			Map<String, Object> tempMap = getValueMap(ccpList.get(i));
			logger.error("tempMap================>"+tempMap);
			/*String xmbm = tempMap.get("xmbm") == null ? "" : tempMap.get("xmbm").toString(); // 项目编码
			String xmlb = tempMap.get("xmlb") == null ? "" : tempMap.get("xmlb").toString(); // 项目类别
*/			String yskmbm = tempMap.get("yskmbm") == null ? "" : tempMap.get("yskmbm").toString(); //预算科目编码(费用类型编码)
			String yskm = tempMap.get("yskm") == null ? "" : tempMap.get("yskm").toString(); //预算科目编码(费用类型编码)
			
			String sqje= tempMap.get("sqje") == null ? "" : tempMap.get("sqje").toString(); //报销金额
			String key=yskmbm;
			if(StringUtils.isNotEmpty(xmmc)) {
				projectNameMap.put(xmbm+yskmbm, xmmc+"中"+yskm);
			}
			if (StringUtils.isEmpty(sqje)) {
				continue;
			}
			if(projectMap.get(key)!=null) {
				BigDecimal value=projectMap.get(key);
				BigDecimal value2=new BigDecimal(sqje).add(value);
				projectMap.put(key, value2);
			}else {
				BigDecimal value=new BigDecimal(sqje);
				projectMap.put(key, value);
			}
		}
		return projectMap;
	}

	/**
	 * 判断是否可以报销
	 * @param projectMap
	 * @return
	 */
	public boolean queryKingdeeFYYS( Map<String, BigDecimal> projectMap) {
			boolean flag=false;
			JSONArray allArr=new JSONArray();
			Map<String, Map<String, String>> projectInfoMap=new HashMap<String, Map<String, String>>();
			//商机项目不参与费用管控
			Map<String, Map<String, String>> projectSJInfoMap=new HashMap<String, Map<String, String>>();
			Map<String,String> sjInfoMap=new HashMap<String,String>();
			sjInfoMap.put("sfkz", "false");
			//商机项目
			Map<String,BigDecimal> sjxmMap=new HashMap<String,BigDecimal>();
			//项目立项
			Map<String,BigDecimal> xmlxMap=new HashMap<String,BigDecimal>();
			for (Map.Entry<String, BigDecimal> m : projectMap.entrySet()) {
				System.out.println("key:" + m.getKey() + " value:" + m.getValue());
				String key = m.getKey();
				BigDecimal value = m.getValue();
				if(key.indexOf("!@#KDXF_SJXM!@#")>-1) {
					sjxmMap.put(key,value);
				}else  {
					xmlxMap.put(key,value);
				}

			}

			if(sjxmMap.size()>0) {
				//调用商机项目预算
				List<String> FnumbersList=new ArrayList<String>();
				List<String> kmsList=new ArrayList<String>();
				for (Map.Entry<String, BigDecimal> m : sjxmMap.entrySet()) {
					System.out.println("key:" + m.getKey() + " value:" + m.getValue());
					String key = m.getKey();
					BigDecimal value = m.getValue();
					String [] keyArr=key.split("!@#");
					FnumbersList.add("'"+keyArr[0]+"'");
					kmsList.add("'"+keyArr[2]+"'");

					System.out.println(keyArr[0]+"===>>>keyArr[0]");
					projectSJInfoMap.put(keyArr[0], sjInfoMap);

				}


				if(FnumbersList!=null&&FnumbersList.size()>0) {
					String fnumbers = Joiner.on(",").join(FnumbersList);
					String kms = Joiner.on(",").join(kmsList);
					JSONObject queryJson=new JSONObject(true);
					queryJson.put("FormId","KDXF_SJXM");
					queryJson.put("FieldKeys", "FNumber,F_KDXF_KBXJE,F_KDXF_FYLB.fnumber ");
					queryJson.put("FilterString", "FNumber in ("+fnumbers+") and F_KDXF_FYLB.fnumber  in ("+kms+") ");
					queryJson.put("OrderString", "");
					queryJson.put("TopRowCount",0);
					queryJson.put("StartRow", 0);
					queryJson.put("Limit", 0);
					JSONArray zcbmMap = systemFlowService.queryFormInfo(queryJson);
					allArr.addAll(zcbmMap);
				}
			}

			if(xmlxMap.size()>0) {
				//调用项目立项预算
				List<String> FnumbersList=new ArrayList<String>();
				List<String> kmsList=new ArrayList<String>();
				List<String> FnumbersList1=new ArrayList<String>();
				List<String> kmsList1=new ArrayList<String>();
				for (Map.Entry<String, BigDecimal> m : xmlxMap.entrySet()) {
					System.out.println("key:" + m.getKey() + " value:" + m.getValue());
					String key = m.getKey();
					BigDecimal value = m.getValue();
					String [] keyArr=key.split("!@#");
					if(keyArr.length>2) {
						if("FYXM59_SYS".equals(keyArr[2])) {
							//其他科目
							FnumbersList1.add("'"+keyArr[0]+"'");
							kmsList1.add("'"+keyArr[2]+"'");
						}else {
							FnumbersList.add("'"+keyArr[0]+"'");
							kmsList.add("'"+keyArr[2]+"'");
						}
					}



				}

				List<String> FnumbersListAll=new ArrayList<String>();
				FnumbersListAll.addAll(FnumbersList1);
				FnumbersListAll.addAll(FnumbersList);
				//获取项目所处阶段及是否控制
				projectInfoMap = financialService.getProjectInfo(Joiner.on(",").join(FnumbersListAll));



				if(FnumbersList!=null&&FnumbersList.size()>0) {
					String fnumbers = Joiner.on(",").join(FnumbersList);
					String kms = Joiner.on(",").join(kmsList);
					//根据项目 预算科目 获取预算金额
					 JSONObject queryJson=new JSONObject(true);
					 queryJson.put("FormId","KDXF_XMLX");
					 queryJson.put("FieldKeys", "FNumber,F_KDXF_KBXJE,F_KDXF_KM3.fnumber,F_KDXF_XMJD3.fnumber");
					 queryJson.put("FilterString", "FNumber in ("+fnumbers+") and F_KDXF_KM3.fnumber in ("+kms+") ");
					 queryJson.put("OrderString", "");
					 queryJson.put("TopRowCount",0);
					 queryJson.put("StartRow", 0);
					 queryJson.put("Limit", 0);
					 JSONArray zcbmMap = systemFlowService.queryFormInfo(queryJson);
					 allArr.addAll(zcbmMap);

				}

				if(FnumbersList1!=null&&FnumbersList1.size()>0) {
					String fnumbers = Joiner.on(",").join(FnumbersList1);
					String kms = Joiner.on(",").join(kmsList1);
					JSONObject queryJson=new JSONObject(true);
					queryJson.put("FormId","KDXF_XMLX");
					queryJson.put("FieldKeys", "FNumber,F_KDXF_KBXJE2,F_KDXF_KM8.fnumber,F_KDXF_XMJD7.fnumber");
					queryJson.put("FilterString", "FNumber in ("+fnumbers+") and F_KDXF_KM8.fnumber in ("+kms+") ");
					queryJson.put("OrderString", "");
					queryJson.put("TopRowCount",0);
					queryJson.put("StartRow", 0);
					queryJson.put("Limit", 0);
					JSONArray zcbmMap = systemFlowService.queryFormInfo(queryJson);
					allArr.addAll(zcbmMap);
				}

			}

			//商机项目不按阶段管控
			System.out.println("projectSJInfoMap:====>>>>>" + projectSJInfoMap);
			System.out.println("allArr:--->>>" +allArr);
			if(projectSJInfoMap!=null&&projectSJInfoMap.size()>0) {
				projectInfoMap.putAll(projectSJInfoMap);
			}

			//处理项目预算费用
			System.out.println("allArr:--->>>" +allArr);
			//项目进入，商机跳过
			Map<String, BigDecimal> fyMap = null;
			Map<String, BigDecimal> fyMap1 = null;
			if(sjxmMap.size()>0){
				fyMap = getFyMap(allArr,false);//按阶段控制报销费用
				fyMap1 = getFyMap(allArr,false);//不按阶段控制报销费用
			}else{
				 fyMap = getFyMap(allArr,true);//按阶段控制报销费用
				fyMap1 = getFyMap(allArr,false);//不按阶段控制报销费用
			}

			System.out.println("allArr:--->>>" +allArr);
			//根据项目信息循环预算金额
			for (Map.Entry<String, BigDecimal> m : projectMap.entrySet()) {
				System.out.println("key:" + m.getKey() + " value:" + m.getValue());
				String key = m.getKey();
				BigDecimal bxje=m.getValue();
				String xmbm=key.split("!@#")[0];//项目编码
				String kmbm=key.split("!@#")[2];//科目编码
				//根据项目编码 判断该项目是否按阶段控制

				Map<String,String> map=projectInfoMap.get(xmbm);
				if(map==null) {
					continue;
				}
				String sfkz= org.ezplatform.util.StringUtils.null2String(map.get("sfkz"));//是否控制
				String xmjd= org.ezplatform.util.StringUtils.null2String(map.get("xmjd"));//项目阶段
				if("true".equals(sfkz)) {

					BigDecimal je=fyMap.get(xmbm+"!@#"+kmbm+"!@#"+xmjd);
					if(je==null) {
						je=new BigDecimal(0);
					}
					if(bxje.compareTo(je) == 1){
					    System.out.println("a大于b");
					    projectBM=xmbm;
					    projectkm=kmbm;
					    flag=false;
					}else {
						flag=true;
					}

				}else {


					BigDecimal je=fyMap1.get(xmbm+"!@#"+kmbm);

					System.out.println("xmbm=====>>>infoNumber:===>>>" + xmbm);
					System.out.println("kmbm=====>>>infoNumber:===>>>" + kmbm);
					System.out.println("je=====>>>infoNumber:===>>>" + je);
					if(je==null) {
						je=new BigDecimal(0);
					}
					if(bxje.compareTo(je) == 1){
					    System.out.println("a大于b");
					    projectBM=xmbm;
					    projectkm=kmbm;
					    flag=false;
					}else{
						flag=true;
					}
				}


				if(!flag) {
					break;
				}

			}

		return  flag;
	}


	/**
	 * 获取可报销金额
	 * @param allArr
	 * @param flag  是否按计划控制
	 * @return
	 */
	public Map<String, BigDecimal> getFyMap(JSONArray allArr, boolean flag)
	{
		Map resultMap = new HashMap();

		if ((allArr != null) && (allArr.size() > 0)) {
			int i = 0; for (int size = allArr.size(); i < size; i++) {
				JSONArray arr = allArr.getJSONArray(i);
				String projectNumber = arr.getString(0);
				String kbxje = arr.getString(1);
				String km = arr.getString(2);

				String key = "";
				if (flag) {
					String jd = arr.getString(3);
					key = projectNumber + "!@#" + km + "!@#" + jd;
				} else {
					key = projectNumber + "!@#" + km;
				}
				if (resultMap.get(key) != null) {
					BigDecimal value = (BigDecimal)resultMap.get(key);
					resultMap.put(key, value.add(new BigDecimal(kbxje)));
				} else {
					resultMap.put(key, new BigDecimal(kbxje));
				}
			}
		}

		return resultMap;
	}



	/**
	 * 根据人员id 获取信用等级
	 * @param userId
	 * @return
	 */
	public String dealXydjData(String userId) {
		String sql="SELECT  b.xydj from sys_user a left join xydjwh b on a.login_name=b.account where a.id=:userId and a.IS_DELETE=0";
		 Map<String,String> resultMap=new HashMap<String,String>();
		 Map<String,Object> queryMap=new HashMap<String,Object>();
		 queryMap.put("userId", userId);
		 String result="";
		 List list = super.findByListNativeQuery(sql, "", queryMap);
		 JSONArray arr=new JSONArray();
		 if ((list != null) && (!list.isEmpty())) {
			 Map map=(Map) list.get(0);
			 result=org.ezplatform.util.StringUtils.null2String(map.get("xydj"));
		 }
		return result;
	}
}
