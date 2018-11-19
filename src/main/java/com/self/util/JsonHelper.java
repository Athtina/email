package com.self.util;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class JsonHelper {

	private static ObjectMapper objectMapper = new ObjectMapper();

	private static ObjectMapper bigDecimalObjectMapper;

	static {
		// 忽略字符串中不能识别的属性
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// BigDecimal转换时调用toPlainString方法而非toString方法，避免使用科学计数法
		objectMapper.configure(Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		// 所有浮点数用BigDecimal接收
		bigDecimalObjectMapper = objectMapper.copy();
		bigDecimalObjectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper.copy();
	}

	public static <T> T parseToObject(InputStream is, Class<T> toClass) {
		try {
			return objectMapper.readValue(is, toClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static Object parseToObject(byte[] b) {
		return parseToObject(b, Object.class);
	}

	public static <T> T parseToObject(byte[] b, Class<T> valueType) {
		return parseToObject(b, 0, b.length, valueType);
	}

	public static <T> T parseToObject(byte[] b, int offset, int len, Class<T> valueType) {
		try {
			return objectMapper.readValue(b, offset, len, valueType);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static <T> T parseToObject(String json, Class<T> toClass) {
		try {
			return objectMapper.readValue(json, toClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 此方法可以用于复杂对象比如，List<Account>，其他方法返回的则是List<Map>
	 */
	public static <T> T parseToObject(String json, TypeReference<T> type) {
		try {
			return objectMapper.readValue(json, type);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static Map parseToMap(String json) {
		return parseToObject(json, Map.class);
	}

	public static Map<String, String> parseToMapStrStr(String json) {
		return parseToObject(json, new TypeReference<Map<String, String>>() {
		});
	}

	public static Map parseToMap(byte[] b) {
		if (b == null || b.length == 0) {
			return null;
		}
		return parseToObject(b, 0, b.length, Map.class);
	}

	public static Map parseToMap(InputStream is) {
		return parseToObject(is, Map.class);
	}

	public static String parseToJson(Object o) {
		if (o == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 使用BigDecimal接收所有浮点数，不会出现四舍五入和科学计数法
	 * 
	 * @param json
	 * @param type
	 * @return
	 */
	public static <T> T parseToObjectForBigDecimal(String json, TypeReference<T> type) {
		try {
			return bigDecimalObjectMapper.readValue(json, type);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 使用BigDecimal接收所有浮点数，不会出现四舍五入和科学计数法
	 * 
	 * @param json
	 * @param toClass
	 * @return
	 */
	public static <T> T parseToObjectForBigDecimal(String json, Class<T> toClass) {
		try {
			return bigDecimalObjectMapper.readValue(json, toClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 使用BigDecimal接收所有浮点数，不会出现四舍五入和科学计数法
	 * 
	 * @param json
	 * @return Map
	 */
	public static Map parseToMapForBigDecimal(String json) {
		return parseToObjectForBigDecimal(json, Map.class);
	}

}
