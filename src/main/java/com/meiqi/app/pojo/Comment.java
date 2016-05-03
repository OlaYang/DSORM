package com.meiqi.app.pojo;

import java.util.ArrayList;
import java.util.List;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @ClassName: Comment
 * @Description:评论(类型,类型ID,email,用户名,内容,评星,添加时间,IP,状态, 父类ID)
 * @author 杨永川
 * @date 2015年4月8日 下午1:42:02
 *
 */
public class Comment {
    @ColumnKey(value = "comment_id")
    private int         commentId;

    @ColumnKey(value = "comment_type")
    private int         commentType;

    @ColumnKey(value = "id_value")
    private long        idValue;

    @ColumnKey(value = "email")
    private String      email;

    @ColumnKey(value = "user_name")
    private String      userName;

    @ColumnKey(value = "content")
    private String      content;

    // 评论等级（0、1 差评 ，2、 3 中，4 、5 好）
    @ColumnKey(value = "comment_rank")
    private byte        commentRank;
    // 服务星级
    @ColumnKey(value = "service_rank")
    private byte        serviceRank;

    @ColumnKey(value = "add_time")
    private int         time;

    @ColumnKey(value = "ip_address")
    private String      ipAddress;

    @ColumnKey(value = "status")
    private byte        status;

    @ColumnKey(value = "parent_id")
    private int         parentId;

    @ColumnKey(value = "user_id")
    private int         userId;

    @ColumnKey(value = "order_id")
    private int         orderId;

    @ColumnKey(value = "address_info")
    private String      address;

    @ColumnKey(value = "goods_desc")
    private String      goodsDesc;

    @ColumnKey(value = "standardName")
    private String      standardName;

    private String      addTime;

    private List<Image> images    = new ArrayList<Image>();
    @ColumnKey(value = "goods_id")
    private long        goodsId;
    private int         type;
    private int         pageIndex = 0;
    private int         pageSize  = 0;



    public Comment() {
    }



    public Comment(int commentId, int commentType, long idValue, String email, String userName, String content,
            byte commentRank, byte serviceRank, int time, String ipAddress, byte status, int parentId, int userId,
            int orderId, String address, String goodsDesc, String standardName) {
        super();
        this.commentId = commentId;
        this.commentType = commentType;
        this.idValue = idValue;
        this.email = email;
        this.userName = userName;
        this.content = content;
        this.commentRank = commentRank;
        this.serviceRank = serviceRank;
        this.time = time;
        this.ipAddress = ipAddress;
        this.status = status;
        this.parentId = parentId;
        this.userId = userId;
        this.orderId = orderId;
        this.address = address;
        this.goodsDesc = goodsDesc;
        this.standardName = standardName;
    }



    public Comment(int commentId, int commentType, long idValue, String email, String userName, String content,
            byte commentRank, byte serviceRank, int time, String ipAddress, byte status, int parentId, int userId,
            int orderId, String address, String goodsDesc, String standardName, List<Image> images) {
        super();
        this.commentId = commentId;
        this.commentType = commentType;
        this.idValue = idValue;
        this.email = email;
        this.userName = userName;
        this.content = content;
        this.commentRank = commentRank;
        this.serviceRank = serviceRank;
        this.time = time;
        this.ipAddress = ipAddress;
        this.status = status;
        this.parentId = parentId;
        this.userId = userId;
        this.orderId = orderId;
        this.address = address;
        this.goodsDesc = goodsDesc;
        this.standardName = standardName;
        this.images = images;
    }



    public int getCommentId() {
        return commentId;
    }



    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }



    public int getCommentType() {
        return commentType;
    }



    public void setCommentType(int commentType) {
        this.commentType = commentType;
    }



    public long getIdValue() {
        return idValue;
    }



    public void setIdValue(long idValue) {
        this.idValue = idValue;
    }



    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
    }



    public String getUserName() {
        return userName;
    }



    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getContent() {
        return content;
    }



    public void setContent(String content) {
        this.content = content;
    }



    public byte getCommentRank() {
        return commentRank;
    }



    public void setCommentRank(byte commentRank) {
        this.commentRank = commentRank;
    }



    public byte getServiceRank() {
        return serviceRank;
    }



    public void setServiceRank(byte serviceRank) {
        this.serviceRank = serviceRank;
    }



    public int getTime() {
        return time;
    }



    public void setTime(int addTime) {
        this.time = addTime;
    }



    public String getIpAddress() {
        return ipAddress;
    }



    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }



    public byte getStatus() {
        return status;
    }



    public void setStatus(byte status) {
        this.status = status;
    }



    public int getParentId() {
        return parentId;
    }



    public void setParentId(int parentId) {
        this.parentId = parentId;
    }



    public int getUserId() {
        return userId;
    }



    public void setUserId(int userId) {
        this.userId = userId;
    }



    public int getOrderId() {
        return orderId;
    }



    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }



    public String getAddress() {
        return address;
    }



    public void setAddress(String address) {
        this.address = address;
    }



    public String getGoodsDesc() {
        return goodsDesc;
    }



    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }



    public String getStandardName() {
        return standardName;
    }



    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }



    public List<Image> getImages() {
        return images;
    }



    public void setImages(List<Image> images) {
        this.images = images;
    }



    public String getAddTime() {
        return addTime;
    }



    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }



    public int getType() {
        return type;
    }



    public void setType(int type) {
        this.type = type;
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

}