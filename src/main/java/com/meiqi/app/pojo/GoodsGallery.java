package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: GoodsGallery
 * @Description:商品相册(商品ID,图片,描述,缩略图,原图)
 * @author 杨永川
 * @date 2015年4月8日 上午10:28:01
 *
 */
public class GoodsGallery {
    private long   imageId;
    private long   goodsId = 0;
    private String imageName;
    private String imageURL;
    private String imgDesc;
    private String thumbUrl;
    private String imgOriginal;
    private int    sortOrder;



    public GoodsGallery() {
    }



    public GoodsGallery(long imageId, long goodsId, String imageName, String imageURL, String imgDesc, String thumbUrl,
            String imgOriginal, int sortOrder) {
        super();
        this.imageId = imageId;
        this.goodsId = goodsId;
        this.imageName = imageName;
        this.imageURL = imageURL;
        this.imgDesc = imgDesc;
        this.thumbUrl = thumbUrl;
        this.imgOriginal = imgOriginal;
        this.sortOrder = sortOrder;
    }



    public long getImageId() {
        return imageId;
    }



    public void setImageId(long imageId) {
        this.imageId = imageId;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }



    public String getImageName() {
        return imageName;
    }



    public void setImageName(String imageName) {
        this.imageName = imageName;
    }



    public String getImageURL() {
        return imageURL;
    }



    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }



    public String getImgDesc() {
        return imgDesc;
    }



    public void setImgDesc(String imgDesc) {
        this.imgDesc = imgDesc;
    }



    public String getThumbUrl() {
        return thumbUrl;
    }



    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }



    public String getImgOriginal() {
        return imgOriginal;
    }



    public void setImgOriginal(String imgOriginal) {
        this.imgOriginal = imgOriginal;
    }



    public int getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

}