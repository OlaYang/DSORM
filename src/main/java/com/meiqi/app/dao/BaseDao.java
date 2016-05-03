package com.meiqi.app.dao;

import java.util.List;

public interface BaseDao {

    long addObejct(Object object);



    void updateObejct(Object object);



    void saveOrUpdateObejct(Object object);



    void deleteObejct(Object object);



    Object getObjectById(Class cls, long id);



    List<Object> getAllObject(Class cls, int firstResult, int maxResults);



    List<Object> getAllObject(Class cls);



    List<Object> getObjectByProperty(Class cls, String[] propertyName, Object[] propertyValue, int firstResult,
            int maxResults);



    void clearSiteCache();



    void flush();

}
