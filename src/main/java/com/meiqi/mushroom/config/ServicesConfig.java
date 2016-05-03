package com.meiqi.mushroom.config;

import com.meiqi.mushroom.dao.ITMushroomDBDao;
import com.meiqi.mushroom.dao.ITMushroomNodeDao;
import com.meiqi.mushroom.dao.ITMushroomServiceDao;
import com.meiqi.mushroom.dao.ITMushroomTableServiceDao;
import com.meiqi.mushroom.entity.TMushroomDB;
import com.meiqi.mushroom.entity.TMushroomNode;
import com.meiqi.mushroom.entity.TMushroomService;
import com.meiqi.mushroom.entity.TMushroomTable;
import com.meiqi.mushroom.entity.TMushroomTableService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * 初始化服务 User: Date: 13-10-10 Time: 上午9:57
 * mushroom配置缓存，以及初始化加载类
 */
@Service
public class ServicesConfig implements ApplicationListener<ApplicationEvent> {
	static class Config {
		Map<String, TMushroomService> MUSHROOM_SERVICE_MAP = new HashMap<String, TMushroomService>();
		Map<Integer, TMushroomNode> MUSHROOM_NODE_MAP = new HashMap<Integer, TMushroomNode>();
		Map<Integer, TMushroomDB> MUSHROOM_DB_MAP = new HashMap<Integer, TMushroomDB>();
		//mushroom table service配置缓存表
		//不加载该数据，每次set操作时，从数据库查询，放入该缓存。
		Map<Integer, List<TMushroomTableService>> MUSHROOM_TABLE_SERVICE_MAP = new HashMap<Integer, List<TMushroomTableService>>();
	}
	private static boolean mushroomStarted = false;

	//是否开启线程，加载配置
	private static boolean threadStarted=true;
	
	//加载配置间隔时间
	private static int threadTime=10*1000;
	@Autowired
	private ITMushroomServiceDao tMushroomServiceDao;
	@Autowired
	private ITMushroomNodeDao tMushroomNodeDao;
	@Autowired
	private ITMushroomDBDao tMushroomDBDao;
	@Autowired
	private ITMushroomTableServiceDao tMushroomTableServiceDao;
	private static Config config = new Config();

	//从MUSHROOM_TABLE_SERVICE_MAP取缓存
	public static List<TMushroomTableService> getMushroomTableService(int tid){
		return config.MUSHROOM_TABLE_SERVICE_MAP.get(tid);
	}
	//往MUSHROOM_TABLE_SERVICE_MAP put缓存
	public static void setMushroomTableService(int tid,List<TMushroomTableService> TMushroomTableServiceList){
		config.MUSHROOM_TABLE_SERVICE_MAP.put(tid, TMushroomTableServiceList);
	}
	
	public static TMushroomService getService(String name) {
		if (name == null) {
			return null;
		}

		return config.MUSHROOM_SERVICE_MAP.get(name);
	}

	
	public static TMushroomNode getDBNode(String name){
		
		TMushroomService tMushroomService=getService(name);
		if(null== tMushroomService){
			return null;
		}
		if(1>tMushroomService.getTables().size()){
			return null;
		}
		TMushroomNode tMushroomNode=getNode(tMushroomService.getTables().get(0).getNid());
		if(null==tMushroomNode){
			return null;
		}
		return tMushroomNode;
	}
	
	public static TMushroomNode getNode(Integer nid) {
		if (nid == null) {
			return null;
		}

		return config.MUSHROOM_NODE_MAP.get(nid);
	}

	public static TMushroomDB getDB(Integer did) {
		if (did == null) {
			return null;
		}

		return config.MUSHROOM_DB_MAP.get(did);
	}
	
	//匿名内部类线程，不断去执行更新加载数据服务配置
	class LoadThread implements Runnable{
		
		public void run() {
			try {
				while(true){
					//执行间隔时间
					Thread.sleep(threadTime);
					loadService();
					
				}
			} catch (Exception e) {
			}
			
		}
		
	}
	
	//加载mush配置服务
	private void loadService(){
		List<TMushroomService> services = tMushroomServiceDao.findAll();
		List<TMushroomNode> nodes = tMushroomNodeDao.findAll();
		List<TMushroomDB> dbs = tMushroomDBDao.findAll();
		final Config tmp = new Config();
		final Map<String, TMushroomService> map1 = tmp.MUSHROOM_SERVICE_MAP;
		final Map<Integer, TMushroomNode> map2 = tmp.MUSHROOM_NODE_MAP;
		final Map<Integer, TMushroomDB> map3 = tmp.MUSHROOM_DB_MAP;

		for (TMushroomService service : services) {
			map1.put(service.getName(), service);
		}

		for (TMushroomNode node : nodes) {
			map2.put(node.getNid(), node);
		}

		for (TMushroomDB db : dbs) {
			map3.put(db.getDid(), db);
		}
		config = tmp;
	}
	
	/**
	 * 根据传入的serviceName加载mushRoom服务
	 * @param serviceName
	 */
	public void loadServiceByServiceName(String serviceName){
		TMushroomService tMushroomService = tMushroomServiceDao.findByName(serviceName);
		List<TMushroomTable> tables = tMushroomService.getTables();
		for (int i = 0; i < tables.size(); i++) {
			Integer nid = tables.get(i).getNid();
			Integer did = tables.get(i).getDid();
			TMushroomNode tMushroomNode = tMushroomNodeDao.findById(nid);
			TMushroomDB tMushroomDB = tMushroomDBDao.findByDid(did);
			config.MUSHROOM_DB_MAP.put(did, tMushroomDB);
			config.MUSHROOM_NODE_MAP.put(nid, tMushroomNode);
		}
		config.MUSHROOM_SERVICE_MAP.put(serviceName, tMushroomService);
		
	}
	

	/**
	 * spring方法 tomcat启动完成时执行 
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		if (false == mushroomStarted) {
			loadService();
			mushroomStarted = true;
			System.out.println("mushroom初始配置读取成功......");
			//是否需要启动更新线程
			if(true == threadStarted){
				LoadThread lt=new LoadThread();
				new Thread(lt).start();
			}
		}
	}

}
