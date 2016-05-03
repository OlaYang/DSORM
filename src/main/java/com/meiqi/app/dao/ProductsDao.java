package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Products;

public interface ProductsDao extends BaseDao {
    int getAllInventory(Class<Products> cls, long goodsId);



    Products getProductsByAttr(Class<Products> cls, List<Long> goodsId, String goodsAttr);



    Products getProducts(Class<Products> cls, long goodsId);



    int getInventoryByAttr(Class<Products> cls, long goodsId);



    List<Products> getAllProducts(Class<Products> cls, long goodsId);



    List<Products> getProductsByGoodsIdList(Class<Products> cls, List<Long> goodsIdList);



    Products getProductsByGoodAttrValue(Class<Products> cls, long groupId, String selGoodsAttIdList);



    Products geProductsIdBySelGoodsAttr(Class<Products> cls, long groupId, String selGoodsAttributes);



    List<Products> getProductsByGroupId(Class<Products> cls, long groupId);
}
