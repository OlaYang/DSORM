package com.meiqi.app.pojo;

import java.util.List;

/**
 * 
 * @ClassName: GoodsFilter
 * @Description:商品筛选条件类--条件大类GoodsFilter
 * @author 杨永川
 * @date 2015年4月23日 下午9:56:57
 *
 */
public class GoodsFilter {
    /**
     * 1=品牌 2=类别 3=价格 4 =商品类型 (包邮) 5= 属性类型
     */
    private int                   filterType;
    // if fiterType =5,fiterObejctId = attrId
    private long                  filterObjectId = 0;
    private String                filterTypeName;
    private List<GoodsFilterItem> filterItems;
    // 0=综合 1=销量 2=价格 升序 3 =价格降序
    private int                   sort;
    // 0=商品类别 1=关键字搜索
    private int                   type;
    private int                   pageIndex      = 0;
    private int                   pageSize       = 0;



    public GoodsFilter() {
    }



    public GoodsFilter(int filterType, long filterObjectId, String filterTypeName, List<GoodsFilterItem> filterItems,
            int sort, int type) {
        super();
        this.filterType = filterType;
        this.filterObjectId = filterObjectId;
        this.filterTypeName = filterTypeName;
        this.filterItems = filterItems;
        this.sort = sort;
        this.type = type;
    }



    public GoodsFilter(int filterType, long filterObjectId, String filterTypeName, List<GoodsFilterItem> filterItems) {
        super();
        this.filterType = filterType;
        this.filterObjectId = filterObjectId;
        this.filterTypeName = filterTypeName;
        this.filterItems = filterItems;
    }



    public int getFilterType() {
        return filterType;
    }



    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }



    public String getFilterTypeName() {
        return filterTypeName;
    }



    public void setFilterTypeName(String filterTypeName) {
        this.filterTypeName = filterTypeName;
    }



    public List<GoodsFilterItem> getFilterItems() {
        return filterItems;
    }



    public void setFilterItems(List<GoodsFilterItem> filterItems) {
        this.filterItems = filterItems;
    }



    public long getFilterObjectId() {
        return filterObjectId;
    }



    public void setFilterObjectId(long filterObjectId) {
        this.filterObjectId = filterObjectId;
    }



    public int getSort() {
        return sort;
    }



    public void setSort(int sort) {
        this.sort = sort;
    }



    public int getType() {
        return type;
    }



    public void setType(int type) {
        this.type = type;
    }



    public int getPageIndex() {
        return pageIndex;
    }



    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }



    public int getPageSize() {
        return pageSize;
    }



    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
