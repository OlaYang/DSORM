package com.meiqi.app.pojo;

import java.util.List;

import com.meilele.datalayer.common.data.builder.ColumnKey;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.StringUtils;

/**
 * 
 * @ClassName: Goods
 * @Description:商品
 * @author 杨永川
 * @date 2015年4月7日 下午5:51:01
 *
 */
public class Goods {
    @ColumnKey(value = "goods_id")
    private long                goodsId;
    private long                catId                 = 0;
    @ColumnKey(value = "goods_sn")
    private String              goodsSn;
    @ColumnKey(value = "goods_storage_type")
    private byte                goodsStorageType      = 0;
    @ColumnKey(value = "goods_name")
    private String              name;
    private String              goodsNameStyle;
    private int                 clickCount            = 0;
    private long                brandId               = 0;
    private String              providerName;
    @ColumnKey(value = "goods_number")
    private int                 amount                = 0;
    private double              goodsWeight           = 0.000;
    @ColumnKey(value = "shop_price")
    private double              originalPrice         = 0.00;
    // shopPrice
    @ColumnKey(value = "effect_price")
    private double              price                 = 0.00;
    @ColumnKey(value = "promote_price")
    private double              promotePrice          = 0.00;
    @ColumnKey(value = "promote_start_date")
    private int                 promoteStartDate      = 0;
    @ColumnKey(value = "promote_end_date")
    private int                 promoteEndDate        = 0;
    private long                warnNumber            = 1;
    private String              keywords;
    @ColumnKey(value = "goods_brief")
    private String              remark;
    private String              goodsDesc             = "";
    private String              descUrl               = "";
    private String              specification         = "";
    private String              packagingAndAfterSale = "";
    private String              goodsThumb;
    @ColumnKey(value = "goods_img")
    private String              cover;
    private List<GoodsGallery>  images;
    private String              originalImg;
    private byte                isReal                = 1;
    private String              extensionCode;
    private byte                isOnSale              = 1;
    private byte                isAloneSale           = 1;
    private byte                isShipping            = 0;
    private int                 integral              = 0;
    private int                 addTime               = 0;
    private int                 sortOrder             = 100;
    private byte                isDelete              = 0;
    private byte                isBest                = 0;
    private byte                isNew                 = 0;
    private byte                isHot                 = 0;
    private long                isPromote             = 0;
    private long                bonusTypeId           = 0;
    private int                 lastUpdate            = 0;
    @ColumnKey(value = "goods_type")
    private long                goodsType             = 0;
    private String              sellerNote;
    private int                 giveIntegral;
    private int                 rankIntegral;
    private Integer             suppliersId;
    private byte                isCheck               = 1;
    // 商品已销售数量
    private boolean             hasStore              = false;
    @ColumnKey(value = "discount")
    private double              discount;
    private byte                isNoFreight           = 0;
    private byte                isShow                = 0;
    // app json data
    @ColumnKey(value = "standardName")
    private String              standardName;
    @ColumnKey(value = "total_sold_count")
    private String              soldAmount            = "0";
    private int                 commentCount;
    private GoodsTransport      goodsTransport;
    private Store               storeAddress;

    // 临时属性 是否有效
    private boolean             valid;
    private boolean             favorite              = false;
    private long[]              goodsIds;
    private List standards;
    //private List<GoodsStandard> standards;
    private int                 selectedGoodsAmount;
    private boolean             selected;
    // 猜你喜欢
    private List<Goods>         recommands;
    @ColumnKey(value = "shop_id")
    private long                shopId                = 0;
    // 运输信息
    private String              transportService;

    private int                 pageIndex             = 0;
    private int                 pageSize              = 0;
    // 商品好评数
    private int                 commentGoodCount      = 0;
    //商品中评数
    private int 				commentMediumCount    =0;
    
	//商品差评数
    private int 				commentBadCount    =0;
    // 商品有图评论数
    private int                 commentImageCount     = 0;
    // 商品好评率
    private String              rating;
    @ColumnKey(value = "is_shop")
    // 是否 到店体验
    private boolean             isShop                = false;

    // 商家信息
    private Shop                shopInfo;
    
    @ColumnKey(value = "specificationTitle")
    private String specificationTitle;
    
    @ColumnKey(value = "child_name")
    private String childName;
    
    @ColumnKey(value = "arrival_time")
    private int arrivalTime;
    
    @ColumnKey(value = "fee_amout")
    private int feeAmout = 0;
    
    @ColumnKey(value = "collect_number")
    private int collectNumber = 0;



    public Goods() {
    }



    /**
     * 图文详情
     * 
     * @param goodsId
     * @param goodsDesc
     * @param specification
     * @param packagingAndAfterSale
     */
    public Goods(long goodsId, String goodsDesc, String specification, String packagingAndAfterSale) {
        super();
        this.goodsId = goodsId;
        this.goodsDesc = goodsDesc;
        this.specification = specification;
        this.packagingAndAfterSale = packagingAndAfterSale;
    }



    /**
     * goods list
     * 
     * @param goodsId
     * @param name
     * @param price
     * @param originalPrice
     * @param promotePrice
     * @param title
     * @param cover
     * @param hasStore
     * @param brandId
     * @param goodsType
     */
    public Goods(long goodsId, String name, double price, double originalPrice, double promotePrice,
            int promoteStartDate, int promoteEndDate, String remark, String cover, long brandId, long goodsType) {
        super();
        this.goodsId = goodsId;
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.promotePrice = promotePrice;
        this.promoteStartDate = promoteStartDate;
        this.promoteEndDate = promoteEndDate;
        this.remark = remark;
        this.cover = cover;
        this.brandId = brandId;
        this.goodsType = goodsType;
        this.hasStore = false;
    }

    public Goods(long goodsId, long catId, String goodsSn, byte goodsStorageType, String name, String goodsNameStyle,
            int clickCount, long brandId, String providerName, int amount, double goodsWeight, double originalPrice,
            double price, double promotePrice, int promoteStartDate, int promoteEndDate, long warnNumber,
            String keywords, String remark, String goodsDesc, String descUrl, String specification,
            String packagingAndAfterSale, String goodsThumb, String cover, List<GoodsGallery> images,
            String originalImg, byte isReal, String extensionCode, byte isOnSale, byte isAloneSale, byte isShipping,
            int integral, int addTime, int sortOrder, byte isDelete, byte isBest, byte isNew, byte isHot,
            long isPromote, long bonusTypeId, int lastUpdate, long goodsType, String sellerNote, int giveIntegral,
            int rankIntegral, Integer suppliersId, byte isCheck, boolean hasStore, double discount, byte isNoFreight,
            byte isShow, String standardName, String soldAmount, int commentCount, GoodsTransport goodsTransport,
            Store storeAddress, boolean valid, boolean favorite, long[] goodsIds, List/*<GoodsStandard>*/ standards,
            int selectedGoodsAmount, boolean selected, List<Goods> recommands, long shopId, String transportService,
            int pageIndex, int pageSize, int commentGoodCount, int commentImageCount, String rating, boolean isShop,
            Shop shopInfo, String specificationTitle, String childName, int arrivalTime, int feeAmout, int collectNumber) {
        super();
        this.goodsId = goodsId;
        this.catId = catId;
        this.goodsSn = goodsSn;
        this.goodsStorageType = goodsStorageType;
        this.name = name;
        this.goodsNameStyle = goodsNameStyle;
        this.clickCount = clickCount;
        this.brandId = brandId;
        this.providerName = providerName;
        this.amount = amount;
        this.goodsWeight = goodsWeight;
        this.originalPrice = originalPrice;
        this.price = price;
        this.promotePrice = promotePrice;
        this.promoteStartDate = promoteStartDate;
        this.promoteEndDate = promoteEndDate;
        this.warnNumber = warnNumber;
        this.keywords = keywords;
        this.remark = remark;
        this.goodsDesc = goodsDesc;
        this.descUrl = descUrl;
        this.specification = specification;
        this.packagingAndAfterSale = packagingAndAfterSale;
        this.goodsThumb = goodsThumb;
        this.cover = cover;
        this.images = images;
        this.originalImg = originalImg;
        this.isReal = isReal;
        this.extensionCode = extensionCode;
        this.isOnSale = isOnSale;
        this.isAloneSale = isAloneSale;
        this.isShipping = isShipping;
        this.integral = integral;
        this.addTime = addTime;
        this.sortOrder = sortOrder;
        this.isDelete = isDelete;
        this.isBest = isBest;
        this.isNew = isNew;
        this.isHot = isHot;
        this.isPromote = isPromote;
        this.bonusTypeId = bonusTypeId;
        this.lastUpdate = lastUpdate;
        this.goodsType = goodsType;
        this.sellerNote = sellerNote;
        this.giveIntegral = giveIntegral;
        this.rankIntegral = rankIntegral;
        this.suppliersId = suppliersId;
        this.isCheck = isCheck;
        this.hasStore = hasStore;
        this.discount = discount;
        this.isNoFreight = isNoFreight;
        this.isShow = isShow;
        this.standardName = standardName;
        this.soldAmount = soldAmount;
        this.commentCount = commentCount;
        this.goodsTransport = goodsTransport;
        this.storeAddress = storeAddress;
        this.valid = valid;
        this.favorite = favorite;
        this.goodsIds = goodsIds;
        this.standards = standards;
        this.selectedGoodsAmount = selectedGoodsAmount;
        this.selected = selected;
        this.recommands = recommands;
        this.shopId = shopId;
        this.transportService = transportService;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.commentGoodCount = commentGoodCount;
        this.commentImageCount = commentImageCount;
        this.rating = rating;
        this.isShop = isShop;
        this.shopInfo = shopInfo;
        this.specificationTitle = specificationTitle;
        this.childName = childName;
        this.arrivalTime = arrivalTime;
        this.feeAmout = feeAmout;
        this.collectNumber = collectNumber;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public int getCommentMediumCount() {
		return commentMediumCount;
	}



	public void setCommentMediumCount(int commentMediumCount) {
		this.commentMediumCount = commentMediumCount;
	}



	public int getCommentBadCount() {
		return commentBadCount;
	}



	public void setCommentBadCount(int commentBadCount) {
		this.commentBadCount = commentBadCount;
	}




    public long getCatId() {
        return catId;
    }



    public String getSpecificationTitle() {
		return specificationTitle;
	}



	public void setSpecificationTitle(String specificationTitle) {
		this.specificationTitle = specificationTitle;
	}



	public void setCatId(long catId) {
        this.catId = catId;
    }



    public byte getGoodsStorageType() {
        return goodsStorageType;
    }



    public void setGoodsStorageType(byte goodsStorageType) {
        this.goodsStorageType = goodsStorageType;
    }



    public String getGoodsSn() {
        return goodsSn;
    }



    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }



    public String getGoodsNameStyle() {
        return goodsNameStyle;
    }



    public void setGoodsNameStyle(String goodsNameStyle) {
        this.goodsNameStyle = goodsNameStyle;
    }



    public int getClickCount() {
        return clickCount;
    }



    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }



    public long getBrandId() {
        return brandId;
    }



    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }



    public String getProviderName() {
        return providerName;
    }



    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }



    public double getGoodsWeight() {
        return goodsWeight;
    }



    public void setGoodsWeight(double goodsWeight) {
        this.goodsWeight = goodsWeight;
    }



    public double getPromotePrice() {
        return promotePrice;
    }



    public void setPromotePrice(double promotePrice) {
        this.promotePrice = promotePrice;
    }



    public int getPromoteStartDate() {
        return promoteStartDate;
    }



    public void setPromoteStartDate(int promoteStartDate) {
        this.promoteStartDate = promoteStartDate;
    }



    public int getPromoteEndDate() {
        return promoteEndDate;
    }



    public void setPromoteEndDate(int promoteEndDate) {
        this.promoteEndDate = promoteEndDate;
    }



    public long getWarnNumber() {
        return warnNumber;
    }



    public void setWarnNumber(long warnNumber) {
        this.warnNumber = warnNumber;
    }



    public String getKeywords() {
        return keywords;
    }



    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public double getPrice() {
        return price;
    }



    public void setPrice(double price) {
        this.price = price;
    }



    public String getRemark() {
        return remark;
    }



    public void setRemark(String remark) {
        this.remark = remark;
    }



    public String getGoodsDesc() {
        return goodsDesc;
    }



    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }



    public String getGoodsThumb() {
        return goodsThumb;
    }



    public void setGoodsThumb(String goodsThumb) {
        this.goodsThumb = goodsThumb;
    }



    public String getCover() {
        return cover;
    }



    public void setCover(String cover) {
        this.cover = cover;
    }



    public String getOriginalImg() {
        return originalImg;
    }



    public void setOriginalImg(String originalImg) {
        this.originalImg = originalImg;
    }



    public String getExtensionCode() {
        return extensionCode;
    }



    public void setExtensionCode(String extensionCode) {
        this.extensionCode = extensionCode;
    }



    public byte getIsOnSale() {
        return isOnSale;
    }



    public void setIsOnSale(byte isOnSale) {
        this.isOnSale = isOnSale;
        setValid();
    }



    public byte getIsAloneSale() {
        return isAloneSale;
    }



    public void setIsAloneSale(byte isAloneSale) {
        this.isAloneSale = isAloneSale;
    }



    public byte getIsShipping() {
        return isShipping;
    }



    public void setIsShipping(byte isShipping) {
        this.isShipping = isShipping;
    }



    public int getIntegral() {
        return integral;
    }



    public void setIntegral(int integral) {
        this.integral = integral;
    }



    public int getAddTime() {
        return addTime;
    }



    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }



    public int getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }



    public byte getIsDelete() {
        return isDelete;
    }



    public void setIsDelete(byte isDelete) {
        this.isDelete = isDelete;
        setValid();
    }



    public byte getIsBest() {
        return isBest;
    }



    public void setIsBest(byte isBest) {
        this.isBest = isBest;
    }



    public byte getIsNew() {
        return isNew;
    }



    public void setIsNew(byte isNew) {
        this.isNew = isNew;
    }



    public byte getIsHot() {
        return isHot;
    }



    public void setIsHot(byte isHot) {
        this.isHot = isHot;
    }



    public long getIsPromote() {
        return isPromote;
    }



    public void setIsPromote(long isPromote) {
        this.isPromote = isPromote;
    }



    public long getBonusTypeId() {
        return bonusTypeId;
    }



    public void setBonusTypeId(long bonusTypeId) {
        this.bonusTypeId = bonusTypeId;
    }



    public int getLastUpdate() {
        return lastUpdate;
    }



    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }



    public long getGoodsType() {
        return goodsType;
    }



    public void setGoodsType(long goodsType) {
        this.goodsType = goodsType;
    }



    public String getSellerNote() {
        return sellerNote;
    }



    public void setSellerNote(String sellerNote) {
        this.sellerNote = sellerNote;
    }



    public int getGiveIntegral() {
        return giveIntegral;
    }



    public void setGiveIntegral(int giveIntegral) {
        this.giveIntegral = giveIntegral;
    }



    public int getRankIntegral() {
        return rankIntegral;
    }



    public void setRankIntegral(int rankIntegral) {
        this.rankIntegral = rankIntegral;
    }



    public Integer getSuppliersId() {
        return suppliersId;
    }



    public void setSuppliersId(Integer suppliersId) {
        this.suppliersId = suppliersId;
    }



    public byte getIsCheck() {
        return isCheck;
    }



    public void setIsCheck(byte isCheck) {
        this.isCheck = isCheck;
    }



    public byte getIsReal() {
        return isReal;
    }



    public void setIsReal(byte isReal) {
        this.isReal = isReal;
    }



    public boolean isHasStore() {
        return hasStore;
    }



    public void setHasStore(boolean hasStore) {
        this.hasStore = hasStore;
    }



    public List<GoodsGallery> getImages() {
        return images;
    }



    public void setImages(List<GoodsGallery> images) {
        this.images = images;
    }



    public double getDiscount() {
        return discount;
    }



    public void setDiscount(double discount) {
        this.discount = discount;
    }



    public String getStandardName() {
        return standardName;
    }



    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }



    public int getAmount() {
        return amount;
    }



    public void setAmount(int amount) {
        this.amount = amount;
    }



    public String getSoldAmount() {
        return soldAmount;
    }



    /**
     * 
     * @Title: setSoldAmount
     * @Description:设置已经销售数量
     * @param @param soldAmount
     * @return void
     * @throws
     */
    public void setSoldAmount(String soldAmount) {
        if (StringUtils.isNumeric(soldAmount)) {
            int amount = Integer.parseInt(soldAmount);
            if (amount > 10000) {
                double soldAmountDouble = Math.round((amount / 10000.00) * 10);
                soldAmountDouble = (double) (soldAmountDouble / 10.0);
                this.soldAmount = soldAmountDouble + ContentUtils.CHINESE_CHARACTERS_WAN;
            } else {
                this.soldAmount = soldAmount;
            }
        } else if (StringUtils.isBlank(soldAmount) || "null".equalsIgnoreCase(soldAmount)) {
            this.soldAmount = "0";
        } else {
            this.soldAmount = soldAmount;
        }
    }



    public int getCommentCount() {
        return commentCount;
    }



    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }



    public double getOriginalPrice() {
        return originalPrice;
    }



    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }



    public GoodsTransport getGoodsTransport() {
        return goodsTransport;
    }



    public void setGoodsTransport(GoodsTransport goodsTransport) {
        this.goodsTransport = goodsTransport;
    }



    public Store getStoreAddress() {
        return storeAddress;
    }



    public void setStoreAddress(Store storeAddress) {
        this.storeAddress = storeAddress;
    }



    public String getSpecification() {
        return specification;
    }



    public void setSpecification(String specification) {
        this.specification = specification;
    }



    public String getPackagingAndAfterSale() {
        return packagingAndAfterSale;
    }



    public void setPackagingAndAfterSale(String packagingAndAfterSale) {
        this.packagingAndAfterSale = packagingAndAfterSale;
    }



    public byte getIsNoFreight() {
        return isNoFreight;
    }



    public void setIsNoFreight(byte isNoFreight) {
        this.isNoFreight = isNoFreight;
    }



    public boolean isValid() {
        return valid;
    }



    public void setValid(boolean valid) {
        this.valid = valid;
    }



    public void setValid() {
        this.valid = (byte) 1 == this.isOnSale && (byte) 0 == this.isDelete;
    }



    public boolean isFavorite() {
        return favorite;
    }



    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }



    public long[] getGoodsIds() {
        return goodsIds;
    }



    public void setGoodsIds(long[] goodsIds) {
        this.goodsIds = goodsIds;
    }



    public byte getIsShow() {
        return isShow;
    }



    public void setIsShow(byte isShow) {
        this.isShow = isShow;
    }



    public List/*<GoodsStandard>*/ getStandards() {
        return standards;
    }



    public void setStandards(List/*<GoodsStandard>*/ standards) {
        this.standards = standards;
    }
    
    
    /*public List<OrderdGoodsStandard> getOrderdStandards() {
        return orderdStandards;
    }



    public void setOrderdStandards(List<OrderdGoodsStandard> orderdStandards) {
        this.orderdStandards = orderdStandards;
    }*/



    public int getSelectedGoodsAmount() {
        return selectedGoodsAmount;
    }



    public void setSelectedGoodsAmount(int selectedGoodsAmount) {
        this.selectedGoodsAmount = selectedGoodsAmount;
    }



    public boolean isSelected() {
        return selected;
    }



    public void setSelected(boolean selected) {
        this.selected = selected;
    }



    public List<Goods> getRecommands() {
        return recommands;
    }



    public void setRecommands(List<Goods> recommands) {
        this.recommands = recommands;
    }



    public String getDescUrl() {
        return descUrl;
    }



    public void setDescUrl(String descUrl) {
        this.descUrl = descUrl;
    }



    @Override
    public int hashCode() {
        return 0;
    }



    public String getTransportService() {
        return transportService;
    }



    public void setTransportService(String transportService) {
        this.transportService = transportService;
    }



    public long getShopId() {
        return shopId;
    }



    public void setShopId(long shopId) {
        this.shopId = shopId;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Goods other = (Goods) obj;
        if (goodsId != other.goodsId)
            return false;
        return true;
    }



    public int getPageIndex() {
        return pageIndex;
    }



    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }



    public int getPageSize() {
        return pageSize;
    }



    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }



    public int getCommentGoodCount() {
        return commentGoodCount;
    }



    public void setCommentGoodCount(int commentGoodCount) {
        this.commentGoodCount = commentGoodCount;
    }



    public int getCommentImageCount() {
        return commentImageCount;
    }



    public void setCommentImageCount(int commentImageCount) {
        this.commentImageCount = commentImageCount;
    }



    public String getRating() {
        return rating;
    }



    public void setRating(String rating) {
        this.rating = rating;
    }



    public boolean isIsShop() {
        return isShop;
    }



    public void setShop(boolean isShop) {
        this.isShop = isShop;
    }



    public boolean isShop() {
        return isShop;
    }



    public Shop getShopInfo() {
        return shopInfo;
    }



    public void setShopInfo(Shop shopInfo) {
        this.shopInfo = shopInfo;
    }
    
    public String getChildName() {
        return childName;
    }



    public void setChildName(String childName) {
        this.childName = childName;
    }



    public int getArrivalTime() {
        return arrivalTime;
    }



    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }



    public int getFeeAmout() {
        return feeAmout;
    }



    public void setFeeAmout(int feeAmout) {
        this.feeAmout = feeAmout;
    }



    public int getCollectNumber() {
        return collectNumber;
    }



    public void setCollectNumber(int collectNumber) {
        this.collectNumber = collectNumber;
    }
    

}