package com.mttk.orche.util;

/**
 * 把对象或原始队形转换成需要的目标类型
 * 
 */
public class ConformUtil {
	// 试图转换object为boolean - 注意试图更多的认为是true
	// 往往是脚本执行结果
	public static Boolean toBoolean(Object object) {
		try {
			Boolean result = toType(object, Boolean.class);
			if (result == null) {
				return true;
			}
			return result;
		} catch (Exception e) {
			//
			return (object != null);
		}
	}

	/**
	 * 把对象只能地转换成目标的类型
	 * 
	 * @param targetType 目标类型 支持字符串,Long,Integer,Float,Double,Boolean
	 * @param object     目标对象
	 * @return 转换好的对象,或抛出异常代表无法转换
	 */
	public static <T> T toType(Object obj, Class<T> targetType) {
		if (obj == null) {
			return null;
		}
		if (targetType.isAssignableFrom(obj.getClass())) {
			return (T) obj;
		}
		// 用标准Java实现类型转换
		return convertToType(obj, targetType);
		// //如果对象为空不需要转换
		// if (obj==null) {
		// return null;
		// }
		// //如果对象能直接转换成目标类型,直接返回
		// if (targetType.isAssignableFrom(obj.getClass())) {
		// return (T)obj;
		// }
		// //目标是字符串
		// if (String.class.isAssignableFrom(targetType)) {
		// return (T)confirmString(obj);
		// }
		// if
		// (Integer.class.isAssignableFrom(targetType)||Integer.TYPE.isAssignableFrom(targetType))
		// {
		// return (T)confirmInteger(obj);
		// }
		// if
		// (Long.class.isAssignableFrom(targetType)||Long.TYPE.isAssignableFrom(targetType))
		// {
		// return (T)confirmLong(obj);
		// }
		// if
		// (Double.class.isAssignableFrom(targetType)||Double.TYPE.isAssignableFrom(targetType))
		// {
		// return (T)confirmDouble(obj);
		// }
		// if
		// (Float.class.isAssignableFrom(targetType)||Float.TYPE.isAssignableFrom(targetType))
		// {
		// return (T)confirmFloat(obj);
		// }
		// if
		// (Boolean.class.isAssignableFrom(targetType)||Boolean.TYPE.isAssignableFrom(targetType))
		// {
		// return (T)confirmBoolean(obj);
		// }
		// throw new RuntimeException("Unsported target type:"+targetType);
	}
	// 字符串
	// private static String confirmString(Object obj) {
	// return obj.toString();
	// }
	// private static Integer confirmInteger(Object obj) {
	// if (obj instanceof Integer) {
	// return (Integer)obj;
	// }else if (obj instanceof Long) {
	// return ((Long)obj).intValue();
	// }else if (obj instanceof String) {
	// return Integer.parseInt((String)obj);
	// }else {
	// throw new RuntimeException("Can not confirm to Integer of
	// type:"+obj.getClass());
	// }
	// }
	// private static Long confirmLong(Object obj) {
	// if (obj instanceof Long) {
	// return (Long)obj;
	// }else if (obj instanceof Integer) {
	// return ((Integer)obj).longValue();
	// }else if (obj instanceof String) {
	// return Long.parseLong((String)obj);
	// }else {
	// throw new RuntimeException("Can not confirm to Long of
	// type:"+obj.getClass());
	// }
	// }
	// private static Double confirmDouble(Object obj) {
	// if (obj instanceof Double) {
	// return (Double)obj;
	// }else if (obj instanceof Float) {
	// return ((Float)obj).doubleValue();
	// }else if (obj instanceof String) {
	// return Double.parseDouble((String)obj);
	// }else {
	// throw new RuntimeException("Can not confirm to Double of
	// type:"+obj.getClass());
	// }
	// }
	// private static Float confirmFloat(Object obj) {
	// if (obj instanceof Float) {
	// return (Float)obj;
	// }else if (obj instanceof Double) {
	// return ((Double)obj).floatValue();
	// }else if (obj instanceof String) {
	// return Float.parseFloat((String)obj);
	// }else {
	// throw new RuntimeException("Can not confirm to Float of
	// type:"+obj.getClass());
	// }
	// }
	// private static Boolean confirmBoolean(Object obj) {
	// if (obj instanceof Boolean) {
	// return (Boolean)obj;
	// }else if (obj instanceof String) {
	// return Boolean.parseBoolean((String)obj);
	// }else {
	// throw new RuntimeException("Can not confirm to Boolean of
	// type:"+obj.getClass());
	// }
	// }

	/**
	 * 用标准Java实现类型转换
	 * 支持字符串,Long,Integer,Float,Double,Boolean等基本类型转换
	 */
	@SuppressWarnings("unchecked")
	private static <T> T convertToType(Object obj, Class<T> targetType) {
		// 目标是字符串
		if (String.class.isAssignableFrom(targetType)) {
			return (T) obj.toString();
		}

		// 目标是Integer
		if (Integer.class.isAssignableFrom(targetType) || Integer.TYPE.isAssignableFrom(targetType)) {
			return (T) convertToInteger(obj);
		}

		// 目标是Long
		if (Long.class.isAssignableFrom(targetType) || Long.TYPE.isAssignableFrom(targetType)) {
			return (T) convertToLong(obj);
		}

		// 目标是Double
		if (Double.class.isAssignableFrom(targetType) || Double.TYPE.isAssignableFrom(targetType)) {
			return (T) convertToDouble(obj);
		}

		// 目标是Float
		if (Float.class.isAssignableFrom(targetType) || Float.TYPE.isAssignableFrom(targetType)) {
			return (T) convertToFloat(obj);
		}

		// 目标是Boolean
		if (Boolean.class.isAssignableFrom(targetType) || Boolean.TYPE.isAssignableFrom(targetType)) {
			return (T) convertToBoolean(obj);
		}
		//
		throw new RuntimeException("不支持的目标类型: " + targetType);
	}

	private static Integer convertToInteger(Object obj) {
		if (obj instanceof Integer) {
			return (Integer) obj;
		} else if (obj instanceof Long) {
			return ((Long) obj).intValue();
		} else if (obj instanceof Double) {
			return ((Double) obj).intValue();
		} else if (obj instanceof Float) {
			return ((Float) obj).intValue();
		} else if (obj instanceof String) {
			return Integer.parseInt((String) obj);
		} else {
			throw new RuntimeException("无法转换为Integer类型: " + obj.getClass());
		}
	}

	private static Long convertToLong(Object obj) {
		if (obj instanceof Long) {
			return (Long) obj;
		} else if (obj instanceof Integer) {
			return ((Integer) obj).longValue();
		} else if (obj instanceof Double) {
			return ((Double) obj).longValue();
		} else if (obj instanceof Float) {
			return ((Float) obj).longValue();
		} else if (obj instanceof String) {
			return Long.parseLong((String) obj);
		} else {
			throw new RuntimeException("无法转换为Long类型: " + obj.getClass());
		}
	}

	private static Double convertToDouble(Object obj) {
		if (obj instanceof Double) {
			return (Double) obj;
		} else if (obj instanceof Float) {
			return ((Float) obj).doubleValue();
		} else if (obj instanceof Long) {
			return ((Long) obj).doubleValue();
		} else if (obj instanceof Integer) {
			return ((Integer) obj).doubleValue();
		} else if (obj instanceof String) {
			return Double.parseDouble((String) obj);
		} else {
			throw new RuntimeException("无法转换为Double类型: " + obj.getClass());
		}
	}

	private static Float convertToFloat(Object obj) {
		if (obj instanceof Float) {
			return (Float) obj;
		} else if (obj instanceof Double) {
			return ((Double) obj).floatValue();
		} else if (obj instanceof Long) {
			return ((Long) obj).floatValue();
		} else if (obj instanceof Integer) {
			return ((Integer) obj).floatValue();
		} else if (obj instanceof String) {
			return Float.parseFloat((String) obj);
		} else {
			throw new RuntimeException("无法转换为Float类型: " + obj.getClass());
		}
	}

	private static Boolean convertToBoolean(Object obj) {
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		} else if (obj instanceof String) {
			return Boolean.parseBoolean((String) obj);
		} else {
			throw new RuntimeException("无法转换为Boolean类型: " + obj.getClass());
		}
	}
}
