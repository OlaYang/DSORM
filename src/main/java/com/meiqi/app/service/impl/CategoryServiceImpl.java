package com.meiqi.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.dao.CategoryDao;
import com.meiqi.app.pojo.Category;
import com.meiqi.app.service.CategoryService;
import com.meiqi.app.service.utils.ImageService;

/**
 * 
 * @ClassName: CategoryServiceImpl
 * @Description:
 * @author 杨永川
 * @date 2015年3月27日 上午10:23:17
 *
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    public static final Logger LOG = Logger.getLogger(CategoryServiceImpl.class);
    Class                      cls = Category.class;
    @Autowired
    private CategoryDao        categoryDao;



    public CategoryDao getCategoryDao() {
        return categoryDao;
    }



    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }



    @Override
    public void addCategory(Category category) {
        categoryDao.addObejct(category);
    }



    @Override
    public void deleteCategory(Category category) {
        categoryDao.deleteObejct(category);
    }



    @Override
    public void updateCategory(Category category) {
        categoryDao.updateObejct(category);
    }



    @Override
    public Category getCategoryById(long id) {
        return (Category) categoryDao.getObjectById(cls, id);
    }



    @Override
    public List getAllCategory(int firstResult, int maxResults) {
        return categoryDao.getAllObject(cls, firstResult, maxResults);
    }



    @Override
    public List finAllCategoryByProperty(String[] propertyName, Object[] propertyVaule, int firstResult, int maxResults) {
        return categoryDao.getObjectByProperty(cls, propertyName, propertyVaule, firstResult, maxResults);
    }



    @Override
    public List getAllCategory() {
        LOG.info("Function:getAllCategory.Start.");
        List list = categoryDao.getAllObject(cls);
        LOG.info("Function:getAllCategory.End.");
        return list;
    }



    /**
     * 获取首页显示的category
     */
    @Override
    public List<Category> getAllHomeCategory() {
        LOG.info("Function:getAllHomeCategory.Start.");
        List<Category> list = categoryDao.getAllHomeCategory(cls);
        // 设置category 图片前缀
        ImageService.setCategoryImageURL(list);
        LOG.info("Function:getAllHomeCategory.End.");
        return list;
    }



    /**
     * 
     * @Title: getSubCategory
     * @Description:获取子分类
     * @param @param category_id
     * @param @return
     * @throws
     */
    @Override
    public Map<String, Object> getSubCategory(long catId) {
        LOG.info("Function:getSubCategory.Start.");
        List<Category> subCategoryList = categoryDao.getSubCategory(cls, catId);
        Map<String, Object> subCategoryMap = new HashMap<String, Object>();
        subCategoryMap.put(ContentUtils.SUBCATEGATES, subCategoryList);

        LOG.info("Function:getSubCategory.End.");
        return subCategoryMap;
    }



    /**
     * 
     * @Title: getRecommendCategory
     * @Description:获取推荐的分类
     * @param @return
     * @throws
     */
    @Override
    public List<Category> getRecommendCategory() {
        LOG.info("Function:getRecommendCategory.Start.");
        List<Category> recommendCategoryList = categoryDao.getRecommendCategory(cls);
        LOG.info("Function:getRecommendCategory.End.");
        return recommendCategoryList;
    }



    /**
     * 
     * @Title: getHotCategory
     * @Description:获取热门的分类
     * @param @return
     * @throws
     */
    @Override
    public List<Category> getHotCategory() {
        LOG.info("Function:getHotCategory.Start.");
        List<Category> hotCategoryList = categoryDao.getHotCategory(cls);
        LOG.info("Function:getHotCategory.End.");
        return hotCategoryList;
    }

}
