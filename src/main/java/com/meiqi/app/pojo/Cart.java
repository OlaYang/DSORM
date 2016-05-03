package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: Cart
 * @Description: 购物车
 * @author sky2.0
 * @date 2015年4月21日 下午9:55:00
 *
 */
public class Cart {
    private long    cartId;
    private long    userId      = 0;
    private String  sessionId;
    private long    goodsId;
    private Goods   goods;
    private String  goodsSn;
    private long    productId   = 0;
    private String  goodsName;
    private double  marketPrice = 0.00;
    private double  goodsPrice  = 0.00;
    private int     goodsAmount = 0;
    private String  goodsAttr   = "";
    private byte    isReal      = 0;
    private String  extensionCode;
    private int     parentId    = 0;
    private byte    recType     = 0;
    private short   isGift      = 0;
    private byte    isShipping  = 0;
    private long    canHandsel  = 0;
    private String  goodsAttrId = "";
    private boolean selected    = true;
    // json 对象 临时属性
    private Long[]  cartIds;
    
    //套装信息
    private long suitId=0L;
    private String suitName;
    private double shopPrice;
    private double suitPrice;
    private int suitNumber;

    
    public Cart() {
    }



    public Cart(long cartId, Goods goods, String goodsName, double goodsPrice, int goodsAmount, String goodsAttr,
            boolean selected,long suitId) {
        super();
        this.cartId = cartId;
        this.goods = goods;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
        this.goodsAmount = goodsAmount;
        this.goodsAttr = goodsAttr;
        this.selected = selected;
        this.suitId = suitId;
    }



    public Cart(long cartId, long userId, String sessionId, long goodsId, Goods goods, String goodsSn, long productId,
            String goodsName, double marketPrice, double goodsPrice, int goodsAmount, String goodsAttr, byte isReal,
            String extensionCode, int parentId, byte recType, short isGift, byte isShipping, long canHandsel,
            String goodsAttrId, boolean selected) {
        super();
        this.cartId = cartId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.goodsId = goodsId;
        this.goods = goods;
        this.goodsSn = goodsSn;
        this.productId = productId;
        this.goodsName = goodsName;
        this.marketPrice = marketPrice;
        this.goodsPrice = goodsPrice;
        this.goodsAmount = goodsAmount;
        this.goodsAttr = goodsAttr;
        this.isReal = isReal;
        this.extensionCode = extensionCode;
        this.parentId = parentId;
        this.recType = recType;
        this.isGift = isGift;
        this.isShipping = isShipping;
        this.canHandsel = canHandsel;
        this.goodsAttrId = goodsAttrId;
        this.selected = selected;
    }



    public Cart(long cartId, long userId, String sessionId, long goodsId, Goods goods, String goodsSn, long productId,
            String goodsName, double marketPrice, double goodsPrice, int goodsAmount, String goodsAttr, byte isReal,
            String extensionCode, int parentId, byte recType, short isGift, byte isShipping, long canHandsel,
            String goodsAttrId, boolean selected, Long[] cartIds) {
        super();
        this.cartId = cartId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.goodsId = goodsId;
        this.goods = goods;
        this.goodsSn = goodsSn;
        this.productId = productId;
        this.goodsName = goodsName;
        this.marketPrice = marketPrice;
        this.goodsPrice = goodsPrice;
        this.goodsAmount = goodsAmount;
        this.goodsAttr = goodsAttr;
        this.isReal = isReal;
        this.extensionCode = extensionCode;
        this.parentId = parentId;
        this.recType = recType;
        this.isGift = isGift;
        this.isShipping = isShipping;
        this.canHandsel = canHandsel;
        this.goodsAttrId = goodsAttrId;
        this.selected = selected;
        this.cartIds = cartIds;
    }



    public long getUserId() {
        return userId;
    }



    public void setUserId(long userId) {
        this.userId = userId;
    }



    public String getSessionId() {
        return sessionId;
    }



    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }



    public Goods getGoods() {
        return goods;
    }



    public void setGoods(Goods goods) {
        this.goods = goods;
    }



    public String getGoodsSn() {
        return goodsSn;
    }



    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }



    public long getProductId() {
        return productId;
    }



    public void setProductId(long productId) {
        this.productId = productId;
    }



    public String getGoodsName() {
        return goodsName;
    }



    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }



    public double getMarketPrice() {
        return marketPrice;
    }



    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }



    public double getGoodsPrice() {
        return goodsPrice;
    }



    public void setGoodsPrice(double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }



    public long getCartId() {
        return cartId;
    }



    public void setCartId(long cartId) {
        this.cartId = cartId;
    }



    public int getGoodsAmount() {
        return goodsAmount;
    }



    public void setGoodsAmount(int goodsAmount) {
        this.goodsAmount = goodsAmount;
    }



    public String getGoodsAttr() {
        return goodsAttr;
    }



    public void setGoodsAttr(String goodsAttr) {
        this.goodsAttr = goodsAttr;
    }



    public byte getIsReal() {
        return isReal;
    }



    public void setIsReal(byte isReal) {
        this.isReal = isReal;
    }



    public String getExtensionCode() {
        return extensionCode;
    }



    public void setExtensionCode(String extensionCode) {
        this.extensionCode = extensionCode;
    }



    public int getParentId() {
        return parentId;
    }



    public void setParentId(int parentId) {
        this.parentId = parentId;
    }



    public byte getRecType() {
        return recType;
    }



    public void setRecType(byte recType) {
        this.recType = recType;
    }



    public short getIsGift() {
        return isGift;
    }



    public void setIsGift(short isGift) {
        this.isGift = isGift;
    }



    public byte getIsShipping() {
        return isShipping;
    }



    public void setIsShipping(byte isShipping) {
        this.isShipping = isShipping;
    }



    public long getCanHandsel() {
        return canHandsel;
    }



    public void setCanHandsel(long canHandsel) {
        this.canHandsel = canHandsel;
    }



    public String getGoodsAttrId() {
        return goodsAttrId;
    }



    public void setGoodsAttrId(String goodsAttrId) {
        this.goodsAttrId = goodsAttrId;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }



    public boolean isSelected() {
        return selected;
    }



    public void setSelected(boolean selected) {
        this.selected = selected;
    }



    public Long[] getCartIds() {
        return cartIds;
    }

    public void setCartIds(Long[] cartIds) {
        this.cartIds = cartIds;
    }



	public long getSuitId() {
		return suitId;
	}



	public void setSuitId(long suitId) {
		this.suitId = suitId;
	}



	public String getSuitName() {
		return suitName;
	}



	public void setSuitName(String suitName) {
		this.suitName = suitName;
	}



	public double getShopPrice() {
		return shopPrice;
	}



	public void setShopPrice(double shopPrice) {
		this.shopPrice = shopPrice;
	}



	public double getSuitPrice() {
		return suitPrice;
	}



	public void setSuitPrice(double suitPrice) {
		this.suitPrice = suitPrice;
	}



	public int getSuitNumber() {
		return suitNumber;
	}



	public void setSuitNumber(int suitNumber) {
		this.suitNumber = suitNumber;
	}
    
    

}