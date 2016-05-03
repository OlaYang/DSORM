package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.ProductsDao;
import com.meiqi.app.pojo.Products;
import com.meiqi.app.service.ProductsService;


@Service
public class ProductsServiceImpl implements ProductsService {
    private static final Logger LOG = Logger.getLogger(ProductsServiceImpl.class);
    Class<Products>             cls = Products.class;
    private ProductsDao         productsDao;



    public ProductsDao getProductsDao() {
        return productsDao;
    }



    public void setProductsDao(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }



    /**
     * 
     * @Title: getAllInventory
     * @Description:获取商品 库存总量
     * @param @param goodsId
     * @param @return
     * @throws
     */
    @Override
    public int getAllInventory(long goodsId) {
        LOG.info("Function:getAllInventory.Start.");
        int inventory = productsDao.getAllInventory(cls, goodsId);
        LOG.info("Function:getAllInventory.End.");
        return inventory;
    }





}
