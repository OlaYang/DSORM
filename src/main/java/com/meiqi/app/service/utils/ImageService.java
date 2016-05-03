package com.meiqi.app.service.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Ad;
import com.meiqi.app.pojo.Category;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.GoodsGallery;

/**
 * 
 * @ClassName: GoodsImageService
 * @Description:
 * @author 杨永川
 * @date 2015年5月14日 下午2:24:40
 *
 */
public class ImageService {
    private static String       IMAGE_SERVICE_URL = "";
    private static final String HOST_NAME_TEST    = AppSysConfig.getValue("host_name_test");
    private static final String HOST_NAME_DEV     = AppSysConfig.getValue("host_name_dev");
    // bug:1440
    static {
        try {
            IMAGE_SERVICE_URL = "";
            // 获取服务器hostName
            String hostName = InetAddress.getLocalHost().getHostName();
            if (HOST_NAME_DEV.equals(hostName) || HOST_NAME_TEST.equals(hostName)) {
                IMAGE_SERVICE_URL = AppSysConfig.getValue("image_service_url_test");
            } else {
                IMAGE_SERVICE_URL = AppSysConfig.getValue(ContentUtils.IMAGE_SERVICE_URL);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }



    /**
     * 
     * @Title: setGoodsCover
     * @Description:设置图片的封面 cover 前缀
     * @param @param goodsList
     * @return void
     * @throws
     */
    public static void setGoodsCover(List<Goods> goodsList) {
        if (CollectionsUtils.isNull(goodsList)) {
            return;
        }
        for (Goods goods : goodsList) {
            setGoodsCover(goods);
        }

    }



    /**
     * 
     * @Title: setGoodsCover
     * @Description:设置图片的封面 cover 前缀
     * @param @param goodsGalleryDao
     * @param @param goods
     * @return void
     * @throws
     */
    public static void setGoodsCover(Goods goods) {
        if (null == goods) {
            return;
        }
        goods.setCover(getHaveImagePerfixUrl(goods.getCover()));
    }



    /**
     * 
     * @Title: setGoodsImages
     * @Description: 设置商品图片 前缀
     * @param @param goodsList
     * @return void
     * @throws
     */
    public static void setGoodsImages(List<GoodsGallery> images) {
        if (CollectionsUtils.isNull(images)) {
            return;
        }
        for (GoodsGallery goodsGallery : images) {
            goodsGallery.setImageURL(getHaveImagePerfixUrl(goodsGallery.getImageURL()));
        }
    }



    /**
     * 
     * @Title: setAdImageURL
     * @Description:设置广告图片url 前缀
     * @param @param adList 参数说明
     * @return void 返回类型
     * @throws
     */
    public static void setAdImageURL(List<Ad> adList) {
        if (CollectionsUtils.isNull(adList)) {
            return;
        }
        for (Ad ad : adList) {
            ad.setImageURL(getHaveImagePerfixUrl(ad.getImageURL()));
        }
    }



    /**
     * 
     * @Title: setCategoryImageURL
     * @Description:设置商品分类图片url 前缀
     * @param @param categoryList 参数说明
     * @return void 返回类型
     * @throws
     */
    public static void setCategoryImageURL(List<Category> categoryList) {
        if (CollectionsUtils.isNull(categoryList)) {
            return;
        }
        for (Category category : categoryList) {
            category.setImageURL(getHaveImagePerfixUrl(category.getImageURL()));
        }
    }



    /**
     * 
     * 设置url 前缀
     *
     * @param url
     * @return
     */
    public static String getHaveImagePerfixUrl(String url) {
        if (!StringUtils.isBlank(url) && !url.contains(IMAGE_SERVICE_URL) && !url.contains(ContentUtils.HTTP)) {
            url = IMAGE_SERVICE_URL + url;
        }
        return url;
    }

}
