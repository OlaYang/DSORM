package com.meiqi.app.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Comment;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.CommentService;
import com.meiqi.app.service.EtagService;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: CommentAction
 * @Description: 评论
 * @author yangyongchuan
 * @date 2015年7月20日 下午1:57:33
 *
 */
@Service
public class CommentAction extends BaseAction {
    private static final Logger LOG                   = Logger.getLogger(CommentAction.class);
    private static final String COMMENT_PROPERTY_LIST = "goodsId,commentId,commentType,idValue,email,userName,content,commentRank,serviceRank,addTime,ipAddress,status,parentId,userId,orderId,address,goodsDesc,images,imageURL,standardName";
    @Autowired
    private EtagService         eTagService;
    @Autowired
    private CommentService      commentService;



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        String method = appRepInfo.getMethod();
        String param = appRepInfo.getParam();
        if (url.contains("goodsComments") && "get".equalsIgnoreCase(method)) {// 获取商品列表
            Comment comment = DataUtil.parse(param, Comment.class);
            String checkResult = checkComments(comment);
            if (!StringUtils.isBlank(checkResult)) {
                return checkResult;
            }
            String data = getGoodsComments(comment);
            boolean result = eTagService.toUpdatEtag1(request, response, "goodsComments/" + comment.getGoodsId(), data);
            if (result) {
                return null;
            }
            return data;
        }
        return null;
    }



    /**
     * 
     * @Title: getGoodsComments
     * @Description: 获取指定商品的评论列表，并分页
     * @param @param params
     * @param @param goodsId
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    public String getGoodsComments(Comment comment) {
        LOG.info("Function:getGoodsComments.Start.");
        List<Comment> commentList = commentService.getGoodsComments(comment);
        String resultData = null;
        if (null != commentList && commentList.size() > 0) {
            resultData = JsonUtils.listFormatToString(commentList,
                    StringUtils.getStringList(COMMENT_PROPERTY_LIST, ContentUtils.COMMA));
        }
        LOG.info("Function:getGoodsComments.Start.");
        return resultData;
    }



    /**
     * 
     * @Title: checkComments
     * @Description: 检测传入参数
     * @param @param Comment
     * @param @return 参数说明
     * @return
     * @throws
     */
    private String checkComments(Comment comment) {
        if (null == comment) {
            return JsonUtils.getErrorJson("传入参数错误!", null);
        }

        if (0 == comment.getType()) {
            return JsonUtils.getErrorJson("请传入评论类型!", null);
        }
        if (1 > comment.getGoodsId()) {
            return JsonUtils.getErrorJson("请传入商品id!", null);
        }
        return null;
    }

}
