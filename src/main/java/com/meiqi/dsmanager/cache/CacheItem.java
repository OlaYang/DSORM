package com.meiqi.dsmanager.cache;



import java.util.Date;

public class CacheItem {
	  private Date createOrUpdateTime = new Date();//创建缓存的时间
	  private long expireTime = 1;//缓存期满的时间
	  private boolean refreshed = false;
	  private Object entity;//缓存的实体
	  
	  public CacheItem(Object obj,long expires){
	    this.entity = obj;
	    this.expireTime = expires;
	  }
	  
	  public boolean isExpired(){
	    return (expireTime != -1 && new Date().getTime()-createOrUpdateTime.getTime() > expireTime);
	  }

	/**
	 * @return the refreshed
	 */
	public boolean isRefreshed() {
		return refreshed;
	}

	/**
	 * @param refreshed the refreshed to set
	 */
	public void setRefreshed(boolean refreshed) {
		this.refreshed = refreshed;
	}

	/**
	 * @return the entity
	 */
	public Object getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Object entity) {
		this.entity = entity;
	}

	/**
	 * @return the createOrUpdateTime
	 */
	public Date getCreateOrUpdateTime() {
		return createOrUpdateTime;
	}

	/**
	 * @param createOrUpdateTime the createOrUpdateTime to set
	 */
	public void setCreateOrUpdateTime(Date createOrUpdateTime) {
		this.createOrUpdateTime = createOrUpdateTime;
	}

	/**
	 * @return the expireTime
	 */
	public long getExpireTime() {
		return expireTime;
	}

	/**
	 * @param expireTime the expireTime to set
	 */
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
	  
	  
}
