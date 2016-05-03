package com.meiqi.liduoo.fastweixin.util;

/**
 * 类工具
 * @author L.cm
 *
 */
public abstract class ClassKit {
	
	/**
	 * 确定class是否可以被加载
	 * @param className
	 * @param classLoader
	 * @return
	 */
	public static boolean isPresent(String className, ClassLoader classLoader) {
		try {
			Class.forName(className, true, classLoader);
			return true;
		}
		catch (Throwable ex) {
			// Class or one of its dependencies is not present...
			return false;
		}
	}
	
}
