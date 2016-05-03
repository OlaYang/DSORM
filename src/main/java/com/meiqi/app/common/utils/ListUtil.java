package com.meiqi.app.common.utils;

import java.util.Collection;
import java.util.List;

public class ListUtil {

	public static <T> boolean isNullOrEmpty(Collection<T> list) {
		return list == null || list.size() <= 0;
	}

	public static <T> int size(List<T> list) {
		if (ListUtil.isNullOrEmpty(list)) {
			return 0;
		} else {
			return list.size();
		}
	}

	public static <T> boolean notEmpty(List<T> list) {
		if (null != list) {
			return !list.isEmpty();
		}
		return false;
	}

}
