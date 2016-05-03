package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.CartDao;
import com.meiqi.app.dao.GoodsDao;
import com.meiqi.app.dao.OrderGoodsDao;
import com.meiqi.app.dao.ProductsDao;
import com.meiqi.app.exception.AppException;
import com.meiqi.app.pojo.Cart;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.OrderGoods;
import com.meiqi.app.pojo.Products;
import com.meiqi.app.service.CartService;
import com.meiqi.app.service.GoodsService;
import com.meiqi.app.service.utils.ImageService;
import com.meiqi.app.service.utils.PriceCalculateService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.commons.util.DataUtil;

/**
 * 
 * @ClassName: CartServiceImpl
 * @Description:
 * @author sky2.0
 * @date 2015年4月21日 下午10:55:14
 *
 */
@Service
public class CartServiceImpl implements CartService {
    private static final Logger LOG                    = Logger.getLogger(CartServiceImpl.class);
    Class<Cart>                 cls                    = Cart.class;
    @Autowired
    private CartDao             cartDao;
    @Autowired
    private GoodsDao            goodsDao;
    @Autowired
    private ProductsDao         productsDao;
    @Autowired
    private OrderGoodsDao       orderGoodsDao;
    @Autowired
    private IDataAction dataAction;
    @Autowired
    private GoodsService goodsService;

    private static final String ERRMSG_UNDERSTOCK      = "您输入的购买数量超过库存";

    private static final String ERRMSG_GOODS_NOT_EXIST = "商品不存在";



    /**
     * 
     */
    public int getCartTotal(long userId) {
        LOG.info("Function:getCartTotal.Start.");
        int total = cartDao.getCartTotal(cls, userId);
        LOG.info("Function:getCartList.End.");
        return total;
    }



    /**
     * 
     * @Title: getCartList
     * @Description:获取用户的购物车
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public List<Cart> getCartList(long userId) {
        LOG.info("Function:getCartList.Start.");
        List<Cart> cartList = cartDao.getCartList(cls, userId);
        
        if (!CollectionsUtils.isNull(cartList)) {
            List<Cart> carts = new ArrayList<Cart>();
            Map<Long,Map<String,String>> tempMap=new HashMap<Long, Map<String,String>>();
            for (Cart cart : cartList) {
            	//查询套装信息
            	long suitId=cart.getSuitId();
                if(0<suitId){
                	Map<String,String> map=null;
                	if(tempMap.containsKey(suitId)){
                		map=tempMap.get(suitId);
                	}else{
                		map=queryFromDb(suitId);
                		if(null!=map){
                    		tempMap.put(suitId, map);
                		}
                	}
                	
                	if(null!=map){
                		cart.setSuitName(map.get("suit_name"));
                		cart.setSuitPrice(Double.parseDouble(map.get("suit_price")));
                		cart.setShopPrice(Double.parseDouble(map.get("shop_price")));
                		cart.setSuitNumber(cart.getGoodsAmount());
                	}
                }
            	
            	
                Goods goods = (Goods) goodsDao.getObjectById(Goods.class, cart.getGoods().getGoodsId());
                // 购物车里 该商品无效
                if (null == goods) {
                    continue;
                }
                
                // 获取商品规格
                String standardName = getGoodsStandardName(cart.getGoods().getGoodsId());
                if (StringUtils.isBlank(standardName)) {
                    standardName = ContentUtils.TWO_BLANK;
                }

                Goods cartGoods = cart.getGoods();
                PriceCalculateService.priceCalculate(goods);
                // Goods goods = cart.getGoods();
                cartGoods.setName(goods.getName());
                cartGoods.setStandardName(standardName);
                cartGoods.setPrice(goods.getPrice());
                // 支持 #2432
                cartGoods.setOriginalPrice(goods.getOriginalPrice());
                ImageService.setGoodsCover(cartGoods);
                carts.add(cart);
            }
            //将临时map清空，
            tempMap=null;
            cartList = carts;
        }

        LOG.info("Function:getCartList.End.");
        return cartList;
    }


    /*
	 * 调用规则引擎查询数据库
	 */
	private Map<String,String> queryFromDb(long suitId) {
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("IPAD_HSV1_GoodsSuit");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("suit_id", suitId);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = null;
		responseData = DataUtil
				.parse(resultData, RuleServiceResponseData.class);
		List<Map<String,String>> mapList=responseData.getRows();
		if(0<mapList.size()){
			return mapList.get(0);
		}else{
			return null;
		}
		
	}

    private String getGoodsStandardName(long goodsId) {
        Goods goods = goodsService.getGoodsBaseInfo(goodsId, -1);
        return goods == null ? null : goods.getStandardName();
    }



    /**
     * 
     * @Title: assembleStandardName
     * @Description:拼装规格
     * @param @param goodsAttr
     * @param @return
     * @return String
     * @throws
     */
    public String assembleStandardName(String goodsAttr) {
        String standardName = ContentUtils.TWO_BLANK;
        if (!StringUtils.isBlank(goodsAttr)) {
            standardName = goodsAttr.replaceAll(ContentUtils.LINE_FEED, ContentUtils.TWO_BLANK);
        }
        return standardName;
    }



    /**
     * @throws AppException
     * 
     * @Title: reBuy
     * @Description:再次购买 重新购买 (加入购物车)
     * @param @param orderId
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public boolean addGoodsToCartFromReBuy(long orderId, long userId) throws AppException {
        LOG.info("Function:reBuy.Start.");
        boolean result = false;
        List<OrderGoods> orderGoodsList = orderGoodsDao.getGoodsByOrderId(orderId);
        if (!CollectionsUtils.isNull(orderGoodsList)) {
            LOG.info("再次购买 重新购买 (加入购物车) 失败,该订单数据异常，没有商品数据");
        }
        for (OrderGoods orderGoods : orderGoodsList) {
            addGoodsToCart(userId, orderGoods.getGoodsId(), orderGoods.getGoodsNumber());
        }
        result = true;
        LOG.info("Function:reBuy.End.");
        return result;
    }



    /**
     * @throws AppException
     * 
     * @Title: addGoodsToCart
     * @Description:
     * @param @param userId
     * @param @param goodsId
     * @param @param goodsAmount
     * @param @return
     * @throws
     */
    @Override
    public boolean addGoodsToCart(long userId, long goodsId, int goodsAmount) throws AppException {
        LOG.info("Function:addGoodsToCart.Start.");
        Cart cart = cartDao.getCartByUserIdAndGoodsId(cls, userId, goodsId);
        Goods goods = (Goods) goodsDao.getObjectById(Goods.class, goodsId);
        if (null == goods) {
            throw new AppException(ERRMSG_GOODS_NOT_EXIST);
        }

        if (null != cart) {
            LOG.info("已经添加了这个商品，所以将购买数量相加.");
            // 购买数量相加
            goodsAmount += cart.getGoodsAmount();
            // 设置为已选择
            cart.setSelected(true);
        } else {
            cart = new Cart();
        }

        // 检查库存
        if (goods.getAmount() < goodsAmount) {
            throw new AppException(ERRMSG_UNDERSTOCK);
        }

        cart.setUserId(userId);
        cart.setGoodsId(goodsId);
        cart.setGoodsSn(goods.getGoodsSn());
        cart.setGoodsSn(goods.getGoodsSn());
        cart.setGoodsName(goods.getName());
        cart.setMarketPrice(goods.getOriginalPrice());
        cart.setGoodsPrice(goods.getPrice());
        cart.setIsReal(goods.getIsReal());
        cart.setExtensionCode(goods.getExtensionCode());
        cart.setGoodsAmount(goodsAmount);
        // 父商品id，如果有的话 parent id 不为 0。该商品为父商品的配件
        // cart.setParentId(0);
        // setGoodsAttrId goodsAttr不能为空
        // 获取商品规格
        Products products = productsDao.getProducts(Products.class, goodsId);
        long productId = 0;
        String standardName = ContentUtils.TWO_BLANK;
        String goodsAttrId = ContentUtils.TWO_BLANK;
        if (null != products) {
            productId = products.getProductId();
            goodsAttrId = products.getGoodsAttr();
            standardName = products.getGoodsAttrValue();
        }
        cart.setProductId(productId);
        cart.setGoodsAttrId(goodsAttrId);
        cart.setGoodsAttr(standardName);

        cartDao.addObejct(cart);

        LOG.info("Function:addGoodsToCart.End.");
        return true;
    }



    /**
     * @throws AppException
     * 
     * @Title: updateGoodsToCart
     * @Description:修改购物车商品
     * @param @param cartId
     * @param @param goodsId
     * @param @param goodsAmount
     * @param @return
     * @throws
     */
    @Override
    public boolean updateGoodsForCart(long cartId, int goodsAmount) throws AppException {
        LOG.info("Function: updateGoodsToCart.Start.");
        boolean result = false;
        Cart cart = (Cart) cartDao.getObjectById(cls, cartId);
        if (null != cart) {
            cart.setGoodsAmount(goodsAmount);

            // 检查库存
            Goods goods = (Goods) goodsDao.getObjectById(Goods.class, cart.getGoodsId());
            if (null == goods || goods.getAmount() < goodsAmount) {
                LOG.info("超过库存数量，修改失败，Cart:cartId =" + cartId);
                throw new AppException(ERRMSG_UNDERSTOCK);
            }

            cartDao.updateObejct(cart);
            result = true;
        } else {
            LOG.info("该购物车不存在，修改失败，Cart:cartId =" + cartId);
            throw new AppException("该购物车不存在，修改失败");
        }
        LOG.info("Function: updateGoodsToCart.End.");
        return result;
    }



    /**
     * 
     * @Title: dateleGoodsForCart
     * @Description:删除购物车商品
     * @param @param cartId
     * @param @param goodsAmount
     * @param @return
     * @throws
     */
    @Override
    public boolean deleteGoodsForCart(long cartId) {
        LOG.info("Function:dateleGoodsForCart.Start.");
        boolean result = false;
        Cart cart = (Cart) cartDao.getObjectById(cls, cartId);
        if (null != cart) {
            cartDao.deleteObejct(cart);
            result = true;
        }
        LOG.info("Function:dateleGoodsForCart.End.");
        return result;
    }



    /**
     * 
     * @Title: dateleGoodsForCart
     * @Description:删除购物车商品
     * @param @param cartId
     * @param @param goodsAmount
     * @param @return
     * @throws
     */
    @Override
    public boolean deleteGoodsForCart(List<Long> cartIdList, long userId) {
        LOG.info("Function:deleteGoodsForCart.Start.");
        LOG.info("删除购物车商品,user id=" + userId);
        boolean result = false;
        if (!CollectionsUtils.isNull(cartIdList)) {
            cartDao.removeCarts(cartIdList, userId);
            result = true;
        }
        LOG.info("Function:deleteGoodsForCart.End.");
        return result;
    }



    /**
     * 
     * @Title: dateleAllGoodsForCart
     * @Description:清空购物车
     * @param @param cartId
     * @param @param goodsAmount
     * @param @return
     * @throws
     */
    @Override
    public boolean deleteAllGoodsForCart(long userId) {
        LOG.info("Function:dateleAllGoodsForCart.Start.");
        boolean result = false;
        cartDao.deleteAllGoodsForCart(cls, userId);
        result = true;
        LOG.info("Function:dateleAllGoodsForCart.End.");
        return result;
    }



    /**
     * 
     * @Title: selectedGoodsForCart
     * @Description:选中购物车goods 是否结算
     * @param @param cart
     * @param @return
     * @throws
     */
    @Override
    public boolean selectedGoodsForCart(Cart cart) {
        LOG.info("Function:selectedGoodsForCart.Start.");
        int result = cartDao.selectedGoodsForCart(cart);
        LOG.info("Function:selectedGoodsForCart.End.");
        return result > 0;
    }

}
