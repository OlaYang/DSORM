package com.meiqi.app.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Category;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.CategoryService;

/**
 * 
 * @ClassName: CategoryAction
 * @Description:
 * @author sky2.0
 * @date 2015年3月29日 下午11:04:53
 *
 */
@Service
public class CategoryAction extends BaseAction
{
    
    private static final Logger LOG = Logger.getLogger(CategoryAction.class);
    
    private static final String CATEGORY_LIST_PROPERTY = "catId,catName,keywords,catDesc,imageURL,functionType";
    
    private static final String JSONPATH = AppSysConfig.getValue("jsonPath");
    
    @Autowired
    private CategoryService categoryService;
    
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo)
    {
        String url = appRepInfo.getUrl();
        if ("category/all".equals(url))
        {
            // 获取所有category
            return getAllCategory();
        }
        else if ("category/home".equals(url))
        {
            // 获取 app首页 category
            return getHomeCategory();
        }
        else if (StringUtils.matchByRegex(url, "^category\\/\\d+$"))
        {
            // 根据category_id 获取category 废弃
            long catId = StringUtils.StringToLong(url.replaceAll("category/", ""));
            return getCategoryByCatId(catId);
        }
        else if (StringUtils.matchByRegex(url, "^category/subCategory\\/\\d+$"))
        {
            // 根据category_id 获取category
            long catId = StringUtils.StringToLong(url.replaceAll("category/subCategory/", ""));
            return getSubCategory(catId);
        }
        
        return null;
    }
    
    /**
     * 
     * @Title: getHomeCategory
     * @Description:获取所有顶层category
     * @param @return
     * @return String
     * @throws
     */
    public String getAllCategory()
    {
        LOG.info("Function:getAllCategory.Start.");
        String allCategoryJson = JsonUtils.readJsonFile(basePath + JSONPATH + "CategoryList.json");
        LOG.info("Function:getAllCategory.End.");
        return allCategoryJson;
    }
    
    /**
     * 
     * @Title: getHomeCategory
     * @Description:获取首页category
     * @param @return
     * @return String
     * @throws
     */
    public String getHomeCategory()
    {
        String homeCategoryJson = null;
        LOG.info("Function:getHomeCategory.Start.");
        List<Category> homeCategoryList = categoryService.getAllHomeCategory();
        homeCategoryJson =
            JsonUtils.listFormatToString(homeCategoryList,
                StringUtils.getStringList(CATEGORY_LIST_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getHomeCategory.End.");
        
        return homeCategoryJson;
    }
    
    /**
     * 
     * @Title: getCategoryByCategory_id
     * @Description:根据category_id 获取category
     * @param @param category_id
     * @param @param response
     * @return void
     * @throws
     */
    public String getCategoryByCatId(long catId)
    {
        LOG.info("Function:getCategoryByCategory_id.Start.");
        LOG.info("get category Id=" + catId);
        Category category = categoryService.getCategoryById(catId);
        String categoryJson =
            JsonUtils.objectFormatToString(category,
                StringUtils.getStringList(CATEGORY_LIST_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getCategoryByCategory_id.End.");
        return categoryJson;
    }
    
    /**
     * 
     * @Title: getSubCategpry
     * @Description:获取子分类
     * @param @param category_id
     * @param @param response
     * @return void
     * @throws
     */
    public String getSubCategory(long catId)
    {
        String subCategoryJson = null;
        LOG.info("Function:getSubCategory.Start.");
        LOG.info("get category,id=" + catId);
        String subcategoryListPath = basePath + JSONPATH + "SubcategoryList_" + catId + ".json";
        subCategoryJson = JsonUtils.readJsonFile(subcategoryListPath);
        LOG.info("Function:getSubCategory.Start.");
        return subCategoryJson;
    }
    
}
