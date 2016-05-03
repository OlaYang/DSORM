package com.meiqi.app.pojo;

import com.meilele.datalayer.common.data.builder.ColumnKey;
import com.meiqi.app.service.utils.ImageService;

/**
 * 
 * @ClassName: Image
 * @Description:评论晒图
 * @author 方琦
 * @date 2015年3月31日 下午4:48:48
 *
 */
public class Image {
    private long   imageId;
    private String imageName;
    @ColumnKey(value = "comment_img")
    private String imageURL;    // 原图
    private String avatar;      // 头像
    private String image100_100; // 缩略图
    private String image700_700; // 满屏图
    private String remark;



    public Image() {
        super();
    }



    public Image(long imageId, String imageName, String imageURL, String avatar, String image100_100,
            String image700_700, String remark) {
        super();
        this.imageId = imageId;
        this.imageName = imageName;
        this.imageURL = imageURL;
        this.avatar = avatar;
        this.image100_100 = image100_100;
        this.image700_700 = image700_700;
        this.remark = remark;
    }



    public long getImageId() {
        return imageId;
    }



    public void setImageId(long imageId) {
        this.imageId = imageId;
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
        this.imageURL = ImageService.getHaveImagePerfixUrl(imageURL);
    }



    public String getAvatar() {
        return avatar;
    }



    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }



    public String getImage100_100() {
        return image100_100;
    }



    public void setImage100_100(String image100_100) {
        this.image100_100 = image100_100;
    }



    public String getImage700_700() {
        return image700_700;
    }



    public void setImage700_700(String image700_700) {
        this.image700_700 = image700_700;
    }



    public String getRemark() {
        return remark;
    }



    public void setRemark(String remark) {
        this.remark = remark;
    }

}