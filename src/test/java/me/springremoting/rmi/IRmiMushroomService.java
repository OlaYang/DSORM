package me.springremoting.rmi;

public interface IRmiMushroomService {
	/**
	 * 根据传入参数从执行持久化操作
	 * @param content
	 * @return
	 */
	public String offer(String content);
	
	/**
	 * 根据传入参数获取一个全局事务
	 * @param content
	 * @return
	 */
	public String start(String content);
	
	/**
	 * 根据传入参数提交一个全局事务
	 * @param content
	 * @return
	 */
	public String commit(String content);
	
	/**
	 * 根据传入参数回滚一个全局事务
	 * @param content
	 * @return
	 */
	public String rollback(String content);
}
