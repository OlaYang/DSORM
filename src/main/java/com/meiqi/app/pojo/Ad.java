package com.meiqi.app.pojo;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @ClassName: Ad
 * @Description:广告表
 * @author 杨永川
 * @date 2015年4月7日 下午3:44:20
 *
 */
public class Ad {
    @ColumnKey(value = "adId")
    private long   adId;
    private int    positionId;
    private int    isShow;
    private long   city;
    private String adPic;
    private String desc1;
    private String desc2;
    @ColumnKey(value = "link")
    private String link;
    private int    sortOrder;
    private int    startTime;
    private int    endTime;
    private int    utime;

    // 广告名称
    @ColumnKey(value = "name")
    private String name;
    // app type
    @ColumnKey(value = "type")
    private byte   type = 0;
    // type ==0 or 2 objectId 是goodsId or catId
    @ColumnKey(value = "objectId")
    private long   objectId;
    // 广告图片路径
    @ColumnKey(value = "imageURL")
    private String imageURL;



    public Ad() {
    }



    public Ad(long adId, int positionId, int isShow, long city, String adPic, String desc1, String desc2, String link,
            int sortOrder, int startTime, int endTime, int utime, String name, byte type, long objectId, String imageURL) {
        super();
        this.adId = adId;
        this.positionId = positionId;
        this.isShow = isShow;
        this.city = city;
        this.adPic = adPic;
        this.desc1 = desc1;
        this.desc2 = desc2;
        this.link = link;
        this.sortOrder = sortOrder;
        this.startTime = startTime;
        this.endTime = endTime;
        this.utime = utime;
        this.name = name;
        this.type = type;
        this.objectId = objectId;
        this.imageURL = imageURL;
    }



    public long getAdId() {
        return adId;
    }



    public void setAdId(long adId) {
        this.adId = adId;
    }



    public int getPositionId() {
        return positionId;
    }



    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }



    public int getIsShow() {
        return isShow;
    }



    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }



    public long getCity() {
        return city;
    }



    public void setCity(long city) {
        this.city = city;
    }



    public String getAdPic() {
        return adPic;
    }



    public void setAdPic(String adPic) {
        this.adPic = adPic;
    }



    public String getDesc1() {
        return desc1;
    }



    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }



    public String getDesc2() {
        return desc2;
    }



    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }



    public String getLink() {
        return link;
    }



    public void setLink(String link) {
        this.link = link;
    }



    public int getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }



    public int getStartTime() {
        return startTime;
    }



    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }



    public int getEndTime() {
        return endTime;
    }



    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }



    public int getUtime() {
        return utime;
    }



    public void setUtime(int utime) {
        this.utime = utime;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public byte getType() {
        return type;
    }



    public void setType(byte type) {
        this.type = type;
    }



    public long getObjectId() {
        return objectId;
    }



    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }



    public String getImageURL() {
        return imageURL;
    }



    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}