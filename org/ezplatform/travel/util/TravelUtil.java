package org.ezplatform.travel.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TravelUtil {
	/**
	 * 差旅报销工具类
	 */
	public static final String TYLCITY="特一类地区(CITY_ESPECIAL)";
	public static final String YLCITY="一类城市(CITY_FIRST)";
	
	public static final String ELCITY="二类城市(CITY_SECOND)";
	public static final String SLCITY="三类城市(CITY_THIRD)";
	
	
	
	

	/**
	 * 根据出发时间及到达时间计算补贴天数  不含出发时间及到达时间
	 * @param beginTime 出发时间
	 * @param endTime 到达时间
	 * @param sfqd  是否起点
	 * @param sfzd 是否重点
	 * @return days
	 */
	public static float getAmountDays(String beginTime,String endTime, boolean sfqd, boolean sfzd) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		float days=0;
		try {
			Calendar beginCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			Date beginDate=sdf.parse(beginTime);
			Date endDate=sdf.parse(endTime);
			beginCal.setTime(beginDate);
			endCal.setTime(endDate);
			if(sfqd) {
				//开始时间小时
				int beginHour=beginCal.get(Calendar.HOUR_OF_DAY);
				
				if(beginHour<12) {
					days+=1;
				}else {
					days+=0.5;
				}
			}
			//间隔时间为出发后第二天 到  到达前一天
			beginCal.add(Calendar.DAY_OF_YEAR,1);
			beginCal.set(Calendar.HOUR_OF_DAY, 0);
			beginCal.set(Calendar.MINUTE, 0);
			beginCal.set(Calendar.MILLISECOND, 0);
			//到达时间小时
			if(sfzd) {
				int endHour=endCal.get(Calendar.HOUR_OF_DAY);
				if(endHour<12) {
					days+=0.5;
				}else {
					days+=1;
				}
				//endCal.add(Calendar.DAY_OF_YEAR,-1);
			}
			
			endCal.set(Calendar.HOUR_OF_DAY, 0);
			endCal.set(Calendar.MINUTE, 0);
			endCal.set(Calendar.MILLISECOND, 0);
			/*System.out.println("开始时间 小时"+beginHour);
			System.out.println("到达时间 小时"+endHour);*/
			
			long day = (endCal.getTimeInMillis()-beginCal.getTimeInMillis())/(24*60*60*1000);
			System.out.println("相隔天数"+day);
			days+=day;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return days;
	}

	/**
	 * 根据出发时间及到达时间计算住宿天数  
	 * @param beginTime 出发时间
	 * @param endTime 到达时间
	 * @return days
	 */
	public static float getZhusuDays(String beginTime,String endTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		float days=0;
		try {
			Calendar beginCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			Date beginDate=sdf.parse(beginTime);
			Date endDate=sdf.parse(endTime);
			beginCal.setTime(beginDate);
			endCal.setTime(endDate);
			
			//间隔时间为出发后第二天 到  到达前一天
			beginCal.add(Calendar.DAY_OF_YEAR,1);
			beginCal.set(Calendar.HOUR_OF_DAY, 0);
			beginCal.set(Calendar.MINUTE, 0);
			beginCal.set(Calendar.MILLISECOND, 0);
			
			
			endCal.set(Calendar.HOUR_OF_DAY, 0);
			endCal.set(Calendar.MINUTE, 0);
			endCal.set(Calendar.MILLISECOND, 0);
			/*System.out.println("开始时间 小时"+beginHour);
			System.out.println("到达时间 小时"+endHour);*/
			
			long day = (endCal.getTimeInMillis()-beginCal.getTimeInMillis())/(24*60*60*1000)+1;
			System.out.println("相隔天数"+day);
			days+=day;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return days;
	}

	
	/**
	 * 
	 * @param nowTime   当前时间	
	 * @param startTime	开始时间
	 * @param endTime   结束时间
	 * @return
	 * @author sunran   判断当前时间在时间区间内
	 */
	public  boolean isEffectiveDate(String nowT, String startT, String endT) {
		System.out.println("nowT======>>>" + nowT);
		System.out.println("startT======>>>" + startT);
		System.out.println("endT======>>>" + endT);


		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");

		Boolean flag=false;
		try {
			Date nowTime = null;
			if(nowT.indexOf(" ")>-1){
				nowTime = sdf.parse(nowT);
			}else{
				nowTime = sdf1.parse(nowT);
			}


			Date startTime=null;
			if(startT.indexOf(" ")>-1){
				startTime = sdf.parse(startT);
			}else{
				startTime = sdf1.parse(startT);
			}


			Date endTime=null;
			if(endT.indexOf(" ")>-1){
				endTime = sdf.parse(endT);
			}else{
				endTime = sdf1.parse(endT);
			}


			if (nowTime.getTime() == startTime.getTime()
	                || nowTime.getTime() == endTime.getTime()) {
				flag= true;
	        }

	        Calendar date = Calendar.getInstance();
	        date.setTime(nowTime);

	        Calendar begin = Calendar.getInstance();
	        begin.setTime(startTime);

	        Calendar end = Calendar.getInstance();
	        end.setTime(endTime);

	        if (date.after(begin) && date.before(end)) {
	        	flag=  true;
	        } else {
	        	flag=  false;
	        }
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
    }
	public static void main(String[] args) {
		//System.out.println( new TravelUtil().getAmountDays("2021-02-12 13:00:00", "2021-02-14 18:00:00",false,false));;
		System.out.println( new TravelUtil().getZhusuDays("2021-02-12 13:00:00", "2021-02-14 18:00:00"));;
	}
}
