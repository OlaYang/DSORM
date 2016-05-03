package com.meiqi.app.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.pojo.Comment;
import com.meiqi.app.pojo.Image;
import com.meiqi.app.service.CommentService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger LOG = Logger.getLogger(CommentServiceImpl.class);
    @Autowired
    private IDataAction         dataAction;



    /**
     * 
     * @Title: getGoodsComments
     * @Description: TODO根据goodsID获取对应商品评论信息，并分页
     * @param @param param
     * @param @param goodsId
     * @param @return 参数说明
     * @return List<Comment> 返回类型
     * @throws
     */
    public List<Comment> getGoodsComments(Comment commentParam) {
        LOG.info("Function:getHostGoodsList.Start");

        Map<String, Object> paramMap = goodsCommentsParams(commentParam);

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("IOS_HSV1_XXY_GOODS_COMMENT");// 规则名称
        dsReqInfo.setParam(paramMap);
        dsReqInfo.setNeedAll("1");
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(responseBaseData.getRows());
        List<Comment> commentList = new LinkedList<Comment>();
        List<Map<String, Object>> mapListJson = (List) json;
        for (int i = 0; i < mapListJson.size(); i++) {
            Map<String, Object> obj = mapListJson.get(i);
            // map 转换 object
            try {
                Comment comment = (Comment) BeanBuilder.buildBean(Comment.class.newInstance(), obj);
                if (obj.containsKey("images")) {
                    List<Image> imageList = JSON.parseArray((String) obj.get("images"), Image.class);
                    comment.setImages(imageList);
                }
                long addTime = comment.getTime();
                if (addTime != 0) {
                    comment.setAddTime(DateUtils.timeToDate(addTime * 1000, DateUtils.sdf_simple));
                }
                commentList.add(comment);
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        LOG.info("Function:getHostGoodsList.End");
        return commentList;
    }



    /**
     * 
     * @Title: goodsCommentsParams
     * @Description: TODO装配分页条件
     * @param @param param
     * @param @return 参数说明
     * @return Map<String,Object> 返回类型
     * @throws
     */
    private Map<String, Object> goodsCommentsParams(Comment comment) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("goodsId", comment.getGoodsId());
        // 1=商品详情评论，2=全部评论，3=好评，4=图评
        int type = comment.getType();
        switch (type) {
        case 1:
            break;
        case 2:
            break;
        case 3:
        	//查好评
            map.put("type", 1);
            break;
        case 4:
        	//查图评
            map.put("img", 1);
            break;
        case 5:
        	//查中评
            map.put("type", 2);
            break;
        case 6:
        	//查差评
            map.put("type", 3);
            break;
        default:
            break;
        }
        int limitStart = comment.getPageIndex() * comment.getPageSize();
        int limitEnd = comment.getPageSize();
        map.put("limitStart", limitStart);
        map.put("limitEnd", limitEnd > 0 ? limitEnd : 10);
        return map;
    }
}
