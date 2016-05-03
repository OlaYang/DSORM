package com.meiqi.app.pojo;

import java.util.Set;

/**
 * 
 * @ClassName: EcsCategory
 * @Description:商品分类(名称,关键字,描述,父类ID,排序,是否有子类,模板文件,数量单位,是否导航显示)
 * @author 杨永川
 * @date 2015年4月7日 下午4:51:04
 *
 */
public class Category {
    private long          catId;
    private String        catName;
    private String        keywords;
    private String        catDesc;
    private long          parentId  = 0;
    private long          sortOrder = 50;
    private String        templateFile;
    private String        measureUnit;
    private long          showInNav = 0;
    private String        style;
    private long          isShow    = 1;
    private long          grade     = 0;
    private String        filterAttr;
    private byte          hot;
    private String        imageURL;
    private byte          homeShow;
    private byte          recommend;
    private byte          functionType;
    private Set<Category> subCategates;



    public Category() {
    }



    public Category(long catId, String catName, String keywords, String catDesc, Set<Category> subCategates) {
        super();
        this.catId = catId;
        this.catName = catName;
        this.keywords = keywords;
        this.catDesc = catDesc;
        this.subCategates = subCategates;
    }



    public Category(long catId, String catName, String keywords, String catDesc) {
        super();
        this.catId = catId;
        this.catName = catName;
        this.keywords = keywords;
        this.catDesc = catDesc;
    }



    public Category(long catId, String catName, String keywords, String catDesc, long parentId, long sortOrder,
            String templateFile, String measureUnit, long showInNav, String style, long isShow, long grade,
            String filterAttr, byte hot, String imageURL, byte homeShow, byte recommend, byte functionType,
            Set<Category> subCategates) {
        super();
        this.catId = catId;
        this.catName = catName;
        this.keywords = keywords;
        this.catDesc = catDesc;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
        this.templateFile = templateFile;
        this.measureUnit = measureUnit;
        this.showInNav = showInNav;
        this.style = style;
        this.isShow = isShow;
        this.grade = grade;
        this.filterAttr = filterAttr;
        this.hot = hot;
        this.imageURL = imageURL;
        this.homeShow = homeShow;
        this.recommend = recommend;
        this.functionType = functionType;
        this.subCategates = subCategates;
    }



    public long getCatId() {
        return catId;
    }



    public void setCatId(long catId) {
        this.catId = catId;
    }



    public String getCatName() {
        return catName;
    }



    public void setCatName(String catName) {
        this.catName = catName;
    }



    public String getKeywords() {
        return keywords;
    }



    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }



    public String getCatDesc() {
        return catDesc;
    }



    public void setCatDesc(String catDesc) {
        this.catDesc = catDesc;
    }



    public long getParentId() {
        return parentId;
    }



    public void setParentId(long parentId) {
        this.parentId = parentId;
    }



    public long getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(long sortOrder) {
        this.sortOrder = sortOrder;
    }



    public String getTemplateFile() {
        return templateFile;
    }



    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }



    public String getMeasureUnit() {
        return measureUnit;
    }



    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }



    public long getShowInNav() {
        return showInNav;
    }



    public void setShowInNav(long showInNav) {
        this.showInNav = showInNav;
    }



    public String getStyle() {
        return style;
    }



    public void setStyle(String style) {
        this.style = style;
    }



    public long getIsShow() {
        return isShow;
    }



    public void setIsShow(long isShow) {
        this.isShow = isShow;
    }



    public long getGrade() {
        return grade;
    }



    public void setGrade(long grade) {
        this.grade = grade;
    }



    public String getFilterAttr() {
        return filterAttr;
    }



    public void setFilterAttr(String filterAttr) {
        this.filterAttr = filterAttr;
    }



    public byte getHot() {
        return hot;
    }



    public void setHot(byte hot) {
        this.hot = hot;
    }



    public String getImageURL() {
        return imageURL;
    }



    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }



    public byte getHomeShow() {
        return homeShow;
    }



    public void setHomeShow(byte homeShow) {
        this.homeShow = homeShow;
    }



    public byte getRecommend() {
        return recommend;
    }



    public void setRecommend(byte recommend) {
        this.recommend = recommend;
    }



    public Set<Category> getSubCategates() {
        return subCategates;
    }



    public void setSubCategates(Set<Category> subCategates) {
        this.subCategates = subCategates;
    }



    public byte getFunctionType() {
        return functionType;
    }



    public void setFunctionType(byte functionType) {
        this.functionType = functionType;
    }

}