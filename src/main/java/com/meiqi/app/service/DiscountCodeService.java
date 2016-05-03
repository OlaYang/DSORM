/**
 * 
 */
package com.meiqi.app.service;

import java.util.List;
import java.util.Map;

import com.meiqi.app.pojo.DiscountInfo;

/**
 * 
 * @Description:TODO
 * 
 * @author:luzicong
 * 
 * @time:2015年7月2日 下午7:10:47
 */
public interface DiscountCodeService {

    /**
     * 
     * @param userId
     * @param pageSize
     * @param pageIndex
     * @return
     */
    List<DiscountInfo> getDiscountInfoList(long userId, int pageIndex, int pageSize, String plat);



    int getDiscountInfoTotal(long userId);



    //boolean sendDiscountCode(long userId, String phone, Map<String, Object> param);



    /**
     * 
     * 检查该用户,该目标手机是否还可以发送折扣码
     *
     * @param userId
     * @param phone
     * @return
     */
    //boolean canSendDiscountCode(long userId, String phone);

}
