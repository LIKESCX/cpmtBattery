package com.cpit.cpmt.biz.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class JsonUtil {

	private static ObjectMapper mapper;

	/**
	 * 获取ObjectMapper实例
	 * 
	 * @param createNew
	 *            方式：true，新实例；false,存在的mapper实例
	 * @return
	 */
	public static synchronized ObjectMapper getMapperInstance(boolean createNew) {
		if (createNew) {
			ObjectMapper om = new ObjectMapper();
			om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
			om.setSerializationInclusion(Include.NON_NULL);
			om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return om;
		} else if (mapper == null) {
			mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return mapper;
	}

	/**
	 * 将java对象转换成json字符串
	 * 
	 * @param obj
	 *            准备转换的对象
	 * @param capital
	 *            输出的json中属性是否首字母大写，true是，false否           
	 * @return json字符串
	 * @throws Exception
	 */
	public static String beanToJson(Object obj,boolean... capital) throws Exception {
		try {
			if(obj == null)
				return "";
			ObjectMapper objectMapper = getMapperInstance(true);
			if(capital.length != 0 && capital[0]) {
				mkMapper4Capital(objectMapper);
			}			
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw e;
		}
	}
	
	

	
	/**
	 * 将json字符串转换成java对象
	 * 
	 * @param json
	 *            准备转换的json字符串
	 * @param cls
	 *            准备转换的类
	 * @param capital
	 *            json中属性是否首字母大写，true是，false否
	 * @return
	 * @throws Exception
	 */
	public static <T> T jsonToBean(String json, Class<T> cls, boolean... capital) throws Exception {
		try {
			if(json == null || json.isEmpty())
				return null;
			ObjectMapper objectMapper = getMapperInstance(true);
			if(capital.length != 0 && capital[0]) {
				mkMapper4Capital(objectMapper);
			}
			return objectMapper.readValue(json, cls);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 将Map List转为指定对象List
	 * @param mapList
	 * @param clz
	 * @param capital
	 *            json中属性是否首字母大写，true是，false否
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> mkList(List mapList,Class<T> clz, boolean... capital) throws Exception{
		if(mapList == null || mapList.isEmpty())
			return null;
		List<T> nl = new ArrayList<T>();
		for(Iterator ite = mapList.iterator();ite.hasNext();){
			Object obj = ite.next();
			String json = beanToJson(obj,capital);
			T t = (T)jsonToBean(json,clz,capital);
			nl.add(t);
		}
		return nl;
	}	
	
	
	
	private static void mkMapper4Capital(ObjectMapper mapper) {
		
		mapper.setPropertyNamingStrategy(new PropertyNamingStrategy() {
			private static final long serialVersionUID = 1L;
			// 反序列化时调用
			@Override
			public String nameForSetterMethod(MapperConfig<?> config,
					AnnotatedMethod method, String defaultName) {
				return method.getName().substring(3);
			}
			// 序列化时调用
			@Override
			public String nameForGetterMethod(MapperConfig<?> config,
					AnnotatedMethod method, String defaultName) {
				return method.getName().substring(3);
			}
		});
	}
	
}
