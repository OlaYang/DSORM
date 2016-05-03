package com.meiqi.app.pojo;

import java.util.List;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @ClassName: MallAdvertisement
 * @Description: 商城首页广告类
 * @author 杨永川
 * @date 2015年5月16日 上午11:59:44
 *
 */
public class MallAdvertisement {

    @ColumnKey(value = "headerAdvertisement")
    private List<Ad> headerAdvertisement;

    @ColumnKey(value = "mixAdvertisement")
    private List<Ad> mixAdvertisement;

    @ColumnKey(value = "bannerAdvertisement")
    private List<Ad> bannerAdvertisement;



    public MallAdvertisement() {
        super();
    }



    public MallAdvertisement(List<Ad> headerAdvertisement, List<Ad> mixAdvertisement, List<Ad> bannerAdvertisement) {
        super();
        this.headerAdvertisement = headerAdvertisement;
        this.mixAdvertisement = mixAdvertisement;
        this.bannerAdvertisement = bannerAdvertisement;
    }



    public List<Ad> getHeaderAdvertisement() {
        return headerAdvertisement;
    }



    public void setHeaderAdvertisement(List<Ad> headerAdvertisement) {
        this.headerAdvertisement = headerAdvertisement;
    }



    public List<Ad> getMixAdvertisement() {
        return mixAdvertisement;
    }



    public void setMixAdvertisement(List<Ad> mixAdvertisement) {
        this.mixAdvertisement = mixAdvertisement;
    }



    public List<Ad> getBannerAdvertisement() {
        return bannerAdvertisement;
    }



    public void setBannerAdvertisement(List<Ad> bannerAdvertisement) {
        this.bannerAdvertisement = bannerAdvertisement;
    }

}
