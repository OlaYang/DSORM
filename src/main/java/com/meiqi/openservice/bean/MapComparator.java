/**   
* @Title: MapComparator.java 
* @Package com.meiqi.openservice.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2016年3月2日 下午3:13:07 
* @version V1.0   
*/
package com.meiqi.openservice.bean;

import java.util.Comparator;
import java.util.Map;

import com.meiqi.openservice.commons.util.StringUtils;

/** 
 * @ClassName: MapComparator 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2016年3月2日 下午3:13:07 
 *  
 */
public  class MapComparator implements Comparator<Map<String, Object>> {
    private String s1;
    private String s2;
    public MapComparator(String s1,String s2){
        this.s1 = s1;
        this.s2 = s2;
    }
    
    @Override
    public int compare(Map<String, Object> o3, Map<String, Object> o4) {
        // TODO Auto-generated method stub
        Object o1 = o3.get(s1);
        Object o2 = o4.get(s2);
        if(null != o1 && StringUtils.isNotEmpty(o1.toString()) && null != o2 && StringUtils.isNotEmpty(o2.toString())){
            Double b1 = Double.valueOf(o1.toString());
            Double b2 = Double.valueOf(o2.toString());
            if("asc".equals(s2)){
                return b1 > b2?1:( b1 == b2?0:-1);
            }else if("desc".equals(s2)){
                return b2 > b1?1:( b1 == b2?0:-1);
            }
        }
        return 0;
    }

}
