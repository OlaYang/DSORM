package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.pojo.Ad;

/**
 * 
 * @ClassName: AdService
 * @Description:
 * @author 杨永川
 * @date 2015年3月26日 下午4:52:15
 *
 */
public interface AdService {

    List<Ad> getHomeAd(String adPosition, int palt);

}
