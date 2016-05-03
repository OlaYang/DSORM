package com.meiqi.app.common.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @ClassName: MySessionFactory
 * @Description:
 * @author sky2.0
 * @date 2015年1月4日 下午3:04:54
 *
 */
@Service
public class MySessionFactory {
	@Autowired
    SessionFactory sessionFactory;



    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }



    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }



    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }



    public void colseSessionFactory() {
    	sessionFactory.close();
    }
}
