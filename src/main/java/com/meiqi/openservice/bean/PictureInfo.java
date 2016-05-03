package com.meiqi.openservice.bean;

/**
 * 
 * @ClassName: PictureInfo
 * @Description: TODO(装修效果图、装修案例相关信息)
 * @author fangqi
 * @date 2015年9月7日 下午3:28:35
 *
 */
public class PictureInfo {
    private String val_id;          // 图片或者案例ID

    private String val_type;        // 0代表装修效果图；1代码装修案例

    private String mark_url;        // 图片或案例所在相对地址

    private int    exposure_num = 1; // 图片或者案例的曝光次数

    private int    click_num    = 0; // 图片或者案例的点击次数



    public PictureInfo() {
        super();
    }



    public PictureInfo(String val_id, String val_type, String mark_url) {
        super();
        this.val_id = val_id;
        this.val_type = val_type;
        this.mark_url = mark_url;
    }



    public PictureInfo(String val_id, String val_type, String mark_url, int exposure_num, int click_num) {
        super();
        this.val_id = val_id;
        this.val_type = val_type;
        this.mark_url = mark_url;
        this.exposure_num = exposure_num;
        this.click_num = click_num;
    }



    public String getVal_id() {
        return val_id;
    }



    public void setVal_id(String val_id) {
        this.val_id = val_id;
    }



    public String getVal_type() {
        return val_type;
    }



    public void setVal_type(String val_type) {
        this.val_type = val_type;
    }



    public String getMark_url() {
        return mark_url;
    }



    public void setMark_url(String mark_url) {
        this.mark_url = mark_url;
    }



    public int getExposure_num() {
        return exposure_num;
    }



    public void setExposure_num(int exposure_num) {
        this.exposure_num = exposure_num;
    }



    public int getClick_num() {
        return click_num;
    }



    public void setClick_num(int click_num) {
        this.click_num = click_num;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mark_url == null) ? 0 : mark_url.hashCode());
        result = prime * result + ((val_id == null) ? 0 : val_id.hashCode());
        result = prime * result + ((val_type == null) ? 0 : val_type.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PictureInfo other = (PictureInfo) obj;
        if (mark_url == null) {
            if (other.mark_url != null)
                return false;
        } else if (!mark_url.equals(other.mark_url))
            return false;
        if (val_id == null) {
            if (other.val_id != null)
                return false;
        } else if (!val_id.equals(other.val_id))
            return false;
        if (val_type == null) {
            if (other.val_type != null)
                return false;
        } else if (!val_type.equals(other.val_type))
            return false;
        return true;
    }

    
    
}
