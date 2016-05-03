package com.meiqi.app.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.exception.AppException;
import com.meiqi.app.pojo.Cart;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.CartService;
import com.meiqi.app.service.EtagService;
import com.meiqi.app.service.RecommandsService;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: CartController
 * @Description:
 * @author 杨永川
 * @date 2015年4月17日 下午4:41:13
 *
 */
@Service
public class CartAction extends BaseAction {
    private static final Logger LOG               = Logger.getLogger(CartAction.class);
    private static final String CARTJSON_PROPERTY = "cartId,goods,amount,goodsId,cover,name,title,price,standardName,goodsAmount,selected,carts,recommands,originalPrice,suitId,suitName,shopPrice,suitPrice,suitNumber";
    @Autowired
    private CartService         cartService;
    @Autowired
    private RecommandsService   recommandsService;
    @Autowired
    private EtagService         eTagService;



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        String method = appRepInfo.getMethod();
        long userId = StringUtils.StringToLong(appRepInfo.getHeader().get("userId").toString());
        if ("cart".equals(url) && "get".equals(method)) {
            // 获取购物车
            String data = getCart(userId);
            String key = "cart/" + userId;
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;
        }
        if ("cartTotal".equals(url) && "get".equals(method)) {
            // 获取购物车 商品个数
            String data = getCartTotal(userId);
            return data;
        } else if ("cart".equals(url) && "put".equals(method)) {
            // 添加购物车
            Cart cart = DataUtil.parse(appRepInfo.getParam(), Cart.class);
            return addGoodsToCart(cart, userId);
        } else if ("cart".equals(url) && "patch".equals(method)) {
            // 添加购物车
            Cart cart = DataUtil.parse(appRepInfo.getParam(), Cart.class);
            return updateGoodsForCart(cart);
        } else if (url.equals("cart") && "delete".equals(method)) {
            String result = "";
            result = deleteGoodsForCart(appRepInfo.getParam(), userId);
            return result;
        } else if ("cart/selected".equals(url) && "patch".equals(method)) {
            // 选中购物车goods
            Cart cart = DataUtil.parse(appRepInfo.getParam(), Cart.class);
            return selectedGoodsForCart(cart);
        } else if (url.contains("cart/order") && "put".equals(method)) {
            long orderId = StringUtils.StringToLong(url.replaceAll("cart/order/", ""));
            return addGoodsToCartFromReBuy(orderId, userId);
        }

        return null;
    }



    /**
     * 
     * @Title: getCartTotal
     * @Description: 获取购物车 商品数量
     * @param @param userId
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    public String getCartTotal(long userId) {
        LOG.info("Function:getCartTotal.Start.");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int cartTotal = cartService.getCartTotal(userId);
        resultMap.put("total", cartTotal);
        LOG.info("Function:getCartTotal.End.");
        return JsonUtils.objectFormatToString(resultMap);
    }



    /**
     * 
     * @Title: getCart
     * @Description:获取用户的购物车
     * @param @return
     * @return String
     * @throws
     */
    public String getCart(long userId) {
        LOG.info("Function:getCart.Start.");
        String cartListJson = null;
        LOG.info("Find cart,userId=" + userId);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Cart> cartList = cartService.getCartList(userId);
        resultMap.put("carts", cartList);
        // 购物车为空 获取推荐商品(猜你喜欢)
        if (CollectionsUtils.isNull(cartList)) {
            List<Goods> recommandGoods = recommandsService.getRecommandsForCart();
            resultMap.put("recommands", recommandGoods);
        }
        cartListJson = JsonUtils.objectFormatToString(resultMap,
                StringUtils.getStringList(CARTJSON_PROPERTY, ContentUtils.COMMA));

        LOG.info("Function:getCart.End.");
        return cartListJson;
    }



    /**
     * 
     * @Title: addGoodsToCart
     * @Description:添加商品购物车
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String addGoodsToCart(Cart cart, long userId) {
        LOG.info("Function:addGoodsToCart.Start.");
        String addJson = JsonUtils.getErrorJson("添加失败", null);
        long goodsId = cart.getGoodsId();
        int goodsAmount = cart.getGoodsAmount();
        boolean result = false;
        try {
            result = cartService.addGoodsToCart(userId, goodsId, goodsAmount);
        } catch (AppException e) {
            addJson = JsonUtils.getErrorJson(e.getMessage(), null);
        }
        if (result) {
            addJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:addGoodsToCart.End.");
        return addJson;
    }



    /**
     * 
     * @Title: updateGoodsForCart
     * @Description:修改购物车商品
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String updateGoodsForCart(Cart cart) {
        LOG.info("Function:updateGoodsToCart.Start.");
        String updateJson = JsonUtils.getErrorJson("修改失败", null);
        long cartId = cart.getCartId();
        int goodsAmount = cart.getGoodsAmount();
        boolean result = false;
        try {
            result = cartService.updateGoodsForCart(cartId, goodsAmount);
        } catch (AppException e) {
            updateJson = JsonUtils.getErrorJson(e.getMessage(), null);
        }
        if (result) {
            updateJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:updateGoodsToCart.End.");
        return updateJson;
    }



    /**
     * 
     * @Title: deleteGoodsForCart
     * @Description:删除购物车商品
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String deleteGoodsForCart(String params, long userId) {
        LOG.info("Function:deteleGoodsForCart.Start.");
        String deleteJson = JsonUtils.getErrorJson("删除失败", null);
        Map<String, Object> map = DataUtil.parse(params);
        String cartIdStr = map.get("cartIds").toString();
        List<Long> cartIdList = JSON.parseArray(cartIdStr, Long.class);
        boolean result = false;
        if (cartIdList != null && cartIdList.size() > 0) {
            result = cartService.deleteGoodsForCart(cartIdList, userId);
        }
        if (result) {
            deleteJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:deteleGoodsForCart.End.");
        return deleteJson;
    }



    /**
     * 
     * @Title: selectedGoodsForCart
     * @Description:选中购物车goods
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String selectedGoodsForCart(Cart cart) {
        LOG.info("Function:selectedGoodsForCart.Start.");
        String selectedJson = JsonUtils.getErrorJson("error", null);
        if (null != cart && !CollectionsUtils.isNull(cart.getCartIds())) {
            boolean result = cartService.selectedGoodsForCart(cart);
            if (result) {
                selectedJson = JsonUtils.getSuccessJson(null);
            }
        }
        LOG.info("Function:selectedGoodsForCart.End.");
        return selectedJson;
    }



    /**
     * 
     * @Title: reBuy
     * @Description:再次购买 重新购买 (加入购物车)
     * @param @param orderId
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String addGoodsToCartFromReBuy(long orderId, long userId) {
        LOG.info("Function:reBuy.Start.");
        LOG.info("再次购买订单 id=" + orderId + ",加入购物车");
        String reBuyJson = JsonUtils.getErrorJson("", null);
        boolean result = false;
        try {
            result = cartService.addGoodsToCartFromReBuy(orderId, userId);
        } catch (AppException e) {
            reBuyJson = JsonUtils.getErrorJson(e.getMessage(), null);
        }
        if (result) {
            reBuyJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:reBuy.End.");
        return reBuyJson;

    }

}
