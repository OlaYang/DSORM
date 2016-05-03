package com.meiqi.data.render;

/**
 * User: 
 * Date: 14-1-4
 * Time: 下午5:14
 */
public class SqlSegment {
    final String str;
    final boolean isRequiredPara;

    public SqlSegment(String str, boolean requiredPara) {
        this.str = str;
        isRequiredPara = requiredPara;
    }
}
