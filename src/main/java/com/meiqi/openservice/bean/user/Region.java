package com.meiqi.openservice.bean.user;

import java.util.List;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @ClassName: Region
 * @Description:地区数据
 * @author 杨永川
 * @date 2015年4月8日 上午11:07:48
 *
 */
public class Region {
    @ColumnKey(value = "region_id")
    private long         regionId;
    @ColumnKey(value = "parent_id")
    private long         parentId   = 0;
    @ColumnKey(value = "region_name")
    private String       regionName;
    private int          regionType = 2;
    private short        agencyId   = 0;
    private byte         isHot      = 0;
    private short        sortOrder  = 50;
    // 首字母
    @ColumnKey(value = "head_char")
    private String       headChar;
    private List<Region> subRegionList;
    // 父region 成都 -> 父region 四川
    private Region       parentRegion;



    public Region() {
    }



    public Region(long regionId, long parentId, int regionType, String regionName, String headChar) {
        super();
        this.regionId = regionId;
        this.parentId = parentId;
        this.regionType = regionType;
        this.regionName = regionName;
        this.headChar = headChar;
    }



    public Region(long regionId, long parentId, String regionName, int regionType, short agencyId, byte isHot,
            short sortOrder, String headChar, List<Region> subRegionList) {
        super();
        this.regionId = regionId;
        this.parentId = parentId;
        this.regionName = regionName;
        this.regionType = regionType;
        this.agencyId = agencyId;
        this.isHot = isHot;
        this.sortOrder = sortOrder;
        this.headChar = headChar;
        this.subRegionList = subRegionList;
    }



    public Region(long regionId, long parentId, String regionName, int regionType, short agencyId, byte isHot,
            short sortOrder, String headChar) {
        super();
        this.regionId = regionId;
        this.parentId = parentId;
        this.regionName = regionName;
        this.regionType = regionType;
        this.agencyId = agencyId;
        this.isHot = isHot;
        this.sortOrder = sortOrder;
        this.headChar = headChar;
    }



    public long getRegionId() {
        return regionId;
    }



    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }



    public long getParentId() {
        return parentId;
    }



    public void setParentId(long parentId) {
        this.parentId = parentId;
    }



    public String getRegionName() {
        return regionName;
    }



    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }



    public int getRegionType() {
        return regionType;
    }



    public void setRegionType(int regionType) {
        this.regionType = regionType;
    }



    public short getAgencyId() {
        return agencyId;
    }



    public void setAgencyId(short agencyId) {
        this.agencyId = agencyId;
    }



    public byte getIsHot() {
        return isHot;
    }



    public void setIsHot(byte isHot) {
        this.isHot = isHot;
    }



    public short getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(short sortOrder) {
        this.sortOrder = sortOrder;
    }



    public String getHeadChar() {
        return headChar;
    }



    public void setHeadChar(String headChar) {
        this.headChar = headChar;
    }



    public List<Region> getSubRegionList() {
        return subRegionList;
    }



    public void setSubRegionList(List<Region> subRegionList) {
        this.subRegionList = subRegionList;
    }



    public Region getParentRegion() {
        return parentRegion;
    }



    public void setParentRegion(Region parentRegion) {
        this.parentRegion = parentRegion;
    }

}