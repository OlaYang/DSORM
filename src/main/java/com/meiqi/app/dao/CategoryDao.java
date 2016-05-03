package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Category;

/**
 * 
 * @ClassName: CategoryDao
 * @Description:
 * @author sky2.0
 * @date 2015年1月17日 下午4:16:48
 *
 */
public interface CategoryDao extends BaseDao {
    // 获取首页显示的category
    List<Category> getAllHomeCategory(Class<Category> cls);



    // 获取子分类
    List<Category> getSubCategory(Class<Category> cls, long catId);



    // 获取推荐的category
    List<Category> getRecommendCategory(Class<Category> cls);



    // 获取热门category
    List<Category> getHotCategory(Class<Category> cls);
}
