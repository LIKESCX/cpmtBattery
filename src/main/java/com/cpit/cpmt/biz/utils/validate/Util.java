package com.cpit.cpmt.biz.utils.validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 公用工具类
 * @author zhoujinguang
 *
 */
public  class Util {
	private final static Logger logger = LoggerFactory.getLogger(Util.class);

	
	/**
	 * 根据sequenceDao.getId得到的ID，获取符合模块要求长度的字符串ID
	 * @param num sequenceDao.getId获取的id
	 * @param prefix 返回的字符串的前缀，比如区域的a，子站的s
	 * @param idLength 返回的字符串的总长度，包括prefix
	 * @return 
	 * 目前暂未考虑prefix+num的长度大于idLength的情况
	 */
	public static String getStringIdBySequence(String prefix,int num,int idLength){
		
		
		
		String idOld=prefix+num;
		if(idOld.trim().length()==idLength){
			return idOld;
		}else{
			String strId="";
			int numZero = idLength-idOld.trim().length();
			String zeroPrefix="";
			for(int i=0;i<numZero;i++){
				zeroPrefix+="0";
			}
			strId = prefix+zeroPrefix+num;
			return strId;			
		}
	}
	
	/**
	* 日期转换成字符串
	* @param date 
	* @return str
	*/
	public static String DateToStr(Date date) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String str = format.format(date);
	   return str;
	} 

	/**
	* 字符串转换成日期
	* @param str
	* @return date
	*/
	public static Date StrToDate(String str) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	   Date date = null;
	   try {
	    date = format.parse(str);
	   } catch (ParseException e) {
	    	logger.error("StrToDate error on "+str,e);
	   }
	   return date;
	}
	
	public static String getDateToStr(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR)  + "年" + (calendar.get(Calendar.MONTH)+1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
	} 

	public static String getWeek(Date d) {
		String week="";
		final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五","星期六" };
		//String s = "2006-01-12 16:30";
		//SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
		if(dayOfWeek<0)
			dayOfWeek=0;
		week = dayNames[dayOfWeek];
		//System.out.println(week);
		return week;
		
	}

	public static String ListToJsonString(Object obj){
		ObjectMapper mapper = new ObjectMapper();
	    try {
	    	return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error("error on transfer json", e);
			return "";
		}	    
	}
	
	
	public static String getYear_Month(){
		Calendar a=Calendar.getInstance();
		int tYear = a.get(Calendar.YEAR);//得到年
		int tMon = a.get(Calendar.MONTH)+1;//得到月	    
		return tYear+"-"+tMon;
	}
	
	 /** 
     * 获取某年第一天日期 
     * @param year 年份 
     * @return Date 
     */  
    public static Date getYearFirst(int year){  
        Calendar calendar = Calendar.getInstance();  
        calendar.clear();  
        calendar.set(Calendar.YEAR, year);  
        Date currYearFirst = calendar.getTime();  
        return currYearFirst;  
    } 
    /** 
     * 获取某年最后一天日期 
     * @param year 年份 
     * @return Date 
     */  
    public static Date getYearLast(int year){  
        Calendar calendar = Calendar.getInstance();  
        calendar.clear();  
        calendar.set(Calendar.YEAR, year);  
        calendar.roll(Calendar.DAY_OF_YEAR, -1);  
        Date currYearLast = calendar.getTime();  
        return currYearLast;  
    } 
	public static void main(String[] args) {
		/*System.out.println(Util.getStringIdBySequence("a",3, 7));
		Date date = new Date();;
		System.out.println("日期转字符串：" + Util.DateToStr(date));
		System.out.println("字符串转日期：" + Util.StrToDate(Util.DateToStr(date)));
		System.out.println(getWeek(date));
		System.out.println(getDateToStr(date));
		System.out.println(getYear_Month());*/
		
		System.out.println(getDateToStr(getYearFirst(2005)));
		System.out.println(getDateToStr(getYearLast(2005)));
		
		Calendar a=Calendar.getInstance();
		int tYear = a.get(Calendar.YEAR);//得到年
		int tMonth = a.get(Calendar.MONTH)+1;//得到月
		System.out.println("年："+tYear+"月："+tMonth+"");
	}
	
	 public static String getSeq(String prxfix,int id){  
	        String seq="";
		 	if(id<10){
	        	seq = prxfix + "000" + String.valueOf(id);
	        }else if(id>=10 && id <100){
	        	seq = prxfix + "00" + String.valueOf(id);
	        }else if(id>=100 && id <1000){
	        	seq = prxfix + "0" + String.valueOf(id);
	        }
	        return seq;  
	    } 
	 
	 public static String ReadFile(String Path) {
			BufferedReader reader = null;
			String laststr = "";
			try {
				FileInputStream fileInputStream = new FileInputStream(Path);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
				reader = new BufferedReader(inputStreamReader);
				String tempString = null;
				while ((tempString = reader.readLine()) != null) {
					laststr += tempString;
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return laststr;
		}
		
		 public static void WriteFile(String path,String str)
		    {
		        try
		        {
		        File file=new File(path);
		        if(!file.exists())
		            file.createNewFile();
		        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        FileOutputStream out=new FileOutputStream(file,false); //如果追加方式用true        
		        StringBuffer sb=new StringBuffer();
		        sb.append("-----------"+sdf.format(new Date())+"------------\n");
		        sb.append(str+"\n");
		        out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
		        out.close();
		        }
		        catch(IOException ex)
		        {
		            System.out.println(ex.getStackTrace());
		        }
		    }


	/**
	 * 手机校验
	 *
	 * @param phoneNumber
	 * @return
	 */
	public static boolean validatePhoneNumber(String phoneNumber) {
		final String phoneRegex = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6-7]|17[0135678]|18[0-9]|19[1,8,9])\\d{8}$";
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			return false;
		}
		return phoneNumber.matches(phoneRegex);
	}
}
 
