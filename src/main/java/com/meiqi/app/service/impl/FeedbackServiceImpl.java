package com.meiqi.app.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.dao.FeedbackDao;
import com.meiqi.app.dao.UsersDao;
import com.meiqi.app.pojo.Feedback;
import com.meiqi.app.pojo.Users;
import com.meiqi.app.service.FeedbackService;

/**
 * 
 * @ClassName: FeedbackServiceImpl
 * @Description:用户反馈信息
 * @author 杨永川
 * @date 2015年5月26日 下午6:14:00
 *
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {
    private static final Logger LOG = Logger.getLogger(FeedbackServiceImpl.class);
    private Class<Feedback>     cls = Feedback.class;

    @Autowired
    private FeedbackDao         feedbackDao;
    @Autowired
    private UsersDao            usersDao;



    public UsersDao getUsersDao() {
        return usersDao;
    }



    public void setUsersDao(UsersDao usersDao) {
        this.usersDao = usersDao;
    }



    public FeedbackDao getFeedbackDao() {
        return feedbackDao;
    }



    public void setFeedbackDao(FeedbackDao feedbackDao) {
        this.feedbackDao = feedbackDao;
    }



    /**
     * 
     * @Title: addFeedback
     * @Description:添加用户反馈
     * @param @param feedback
     * @param @return
     * @throws
     */
    @Override
    public long addFeedback(Feedback feedback) {
        LOG.info("Function:addFeedback.Start.");
        long msgId = 0;
        Users users = (Users) usersDao.getObjectById(Users.class, feedback.getUserId());
        feedback.setUserName(users.getRealName());
        feedback.setUserEmail(users.getEmail());
        feedback.setMsgTime(DateUtils.getSecond());
        msgId = feedbackDao.addObejct(feedback);
        LOG.info("Function:addFeedback.End.");
        return msgId;
    }

}
