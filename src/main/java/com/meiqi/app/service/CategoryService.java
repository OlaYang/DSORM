package com.meiqi.app.service;

import java.util.List;
import java.util.Map;

import com.meiqi.app.pojo.Category;

public interface CategoryService {
    void addCategory(Category category);



    void deleteCategory(Category category);



    void updateCategory(Category category);



    Category getCategoryById(long id);



    List getAllCategory(int firstResult, int maxResults);



    List getAllCategory();



    List<Category> getAllHomeCategory();



    List finAllCategoryByProperty(String[] propertyName, Object[] propertyVaule, int firstResult, int maxResults);



    Map<String, Object> getSubCategory(long category_id);



    List<Category> getRecommendCategory();



    List<Category> getHotCategory();
}
