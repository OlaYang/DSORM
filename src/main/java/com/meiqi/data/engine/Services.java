package com.meiqi.data.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.meiqi.data.dao.ITServiceDBDao;
import com.meiqi.data.dao.ITServiceDao;
import com.meiqi.data.dao.ITServiceMappingDao;
import com.meiqi.data.dao.ITServiceNextDBConfigDao;
import com.meiqi.data.dao.IWxApiInfoDao;
import com.meiqi.data.engine.functions.CHARSMARRY;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceDB;
import com.meiqi.data.entity.TServiceEnDecryptKey;
import com.meiqi.data.entity.TServiceMapping;
import com.meiqi.data.entity.TServiceNextDBConfig;
import com.meiqi.data.entity.WxApiInfo;
import com.meiqi.data.render.SqlUtil;
import com.meiqi.data.util.ConfigUtil;
import com.meiqi.data.util.LogUtil;
import com.meiqi.openservice.commons.util.CollectionsUtils;
import com.meiqi.util.MyApplicationContextUtil;
/**
 * User: Date: 13-6-21 Time: 上午10:25 服务提供者
 */
@Service("services")
public class Services implements ApplicationListener<ApplicationEvent> {
    private static final Log LOG=LogFactory.getLog("clearcache");
	static class Config {
		ConcurrentHashMap<String, TService> serviceConfigStr = new ConcurrentHashMap<String, TService>();
		ConcurrentHashMap<Integer, String> serviceid2Name = new ConcurrentHashMap<Integer, String>();
		ConcurrentHashMap<String, TServiceDB> dbConfig = new ConcurrentHashMap<String, TServiceDB>();
		HashMap<String, String> serviceMapping = new HashMap<String, String>();
		ConcurrentHashMap<String, CopyOnWriteArrayList<TServiceNextDBConfig>> nextDbConfig = new ConcurrentHashMap<String, CopyOnWriteArrayList<TServiceNextDBConfig>>();
		HashMap<String, TServiceEnDecryptKey> enDecryptConfig = new HashMap<String, TServiceEnDecryptKey>();
		ConcurrentHashMap<String, WxApiInfo> wxApiMapping = new ConcurrentHashMap<String, WxApiInfo>();
		boolean dataStarted=false;
	}

	@Autowired
	private  ITServiceDao tServiceDao;
	@Autowired
	private  ITServiceDBDao tServiceDBDao;
	@Autowired
	private  ITServiceMappingDao tServiceMappingDao;
	@Autowired
	private  ITServiceNextDBConfigDao tServiceNextDBConfigDao;

	//@Autowired
	//private  IWxApiInfoDao wxApiInfoDao;
	
	static final int RELOAD_CACHETIME_THRESHOLD = 1800; // 缓存预加载的阀值
	static final int RELOAD_LATENCY_THRESHOLD = 2000; // 缓存预加载的时延要求
	private static final int PRE_RELOAD_TIME_THRESHOLD = 5 * 60; // 缓存提前预加载的阀值
	private static final int RELOAD_THREAD_COUNT = 2;
	private static final int FIND_CHANGE_PERIOD_SECOND = 60;
	public static final String SERVICE_USER_INFO = "根据用户名查询信息";
	public static final String SERVICE_SENDMESSAGE_INFO = "DX_BUV1_SmsInfo";
	public static final String SERVICE_USER_LIST = "用户列表查询";
	private static  Thread loadConfigThread;
	private static  Thread findChangeThread;
	private static volatile boolean needReload = true;
	private static final ConcurrentLinkedQueue<String> configChanged = new ConcurrentLinkedQueue<String>();
	/**
	 * 获取用户的id
	 * */
	public static final String SERVICE_GET_USERID = "HMJ_BUV1_USERS";
	
	private static Config config = new Config();
	private static volatile int config_cache_second = ConfigUtil.getConfig_cache_second();
	private static final ExecutorService dealChangePool = new ThreadPoolExecutor(RELOAD_THREAD_COUNT
	            , RELOAD_THREAD_COUNT,
	            0L, TimeUnit.MILLISECONDS,
	            new LinkedBlockingQueue<Runnable>()
	            , new ThreadFactory() {
	        final AtomicInteger poolNumber = new AtomicInteger(1);

	        @Override
	        public Thread newThread(Runnable r) {
	            return new Thread(r, "reload_cache-" + poolNumber.getAndIncrement());
	        }
	 });

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		if(false==config.dataStarted){
    		final List<TService> pos = tServiceDao.getTServiceInfoByName(null);
    		final List<TServiceDB> dbpos = tServiceDBDao.getTServiceDBInfoById(null);
    
    		final ConcurrentHashMap<String, TService> serviceConfigStrNew = new ConcurrentHashMap<String, TService>();
    		final ConcurrentHashMap<Integer, String> serviceid2NameNew = new ConcurrentHashMap<Integer, String>();
    		final ConcurrentHashMap<String, TServiceDB> dbConfigNew = new ConcurrentHashMap<String, TServiceDB>();
    		final HashMap<String, String> serviceMappingNew = getServiceMapping();
    		final ConcurrentHashMap<String, CopyOnWriteArrayList<TServiceNextDBConfig>> nextDbConfigNew = getNextDbConfig();
    
    		for (TService service : pos) {
    			final TService old = getService(service.getName());
    
    			if (old != null && old.equals(service)) {
    				serviceConfigStrNew.put(service.getName(), old);
    				serviceid2NameNew.put(service.getServiceID(), old.getName());
    			} else {
    				serviceConfigStrNew.put(service.getName(), service);
    				serviceid2NameNew.put(service.getServiceID(), service.getName());
    			}
    		}
    
    		for (TServiceDB db : dbpos) {
    			final TServiceDB old = getDB(db.getDbID());
    
    			if (old != null && old.equals(db)) {
    				dbConfigNew.put(db.getDbID(), old);
    			} else {
    				dbConfigNew.put(db.getDbID(), db);
    			}
    		}
    
    		Config newC = new Config();
    		newC.dbConfig = dbConfigNew;
    		newC.serviceConfigStr = serviceConfigStrNew;
    		newC.serviceid2Name = serviceid2NameNew;
    		newC.serviceMapping = serviceMappingNew;
    		newC.nextDbConfig = nextDbConfigNew;
    		newC.wxApiMapping = getWxApiMapping();
    		config = newC;
    		config.dataStarted=true;
    		System.out.println("Data初始配置读取成功......");
    		
    		//初始化微信标签词库的规则到map
			CHARSMARRY.initData();
    		
		}
	}

	private  HashMap<String, String> getServiceMapping() {
		HashMap<String, String> ret = new HashMap<String, String>();
		try {
			final List<TServiceMapping> pos = tServiceMappingDao.findAllTServiceMappingInfo();
			for (TServiceMapping po : pos) {
				ret.put(po.getServiceInterface(), po.getServiceImplement());
			}
		} catch (Exception e) {
			LOG.error("fail get service mapping, " + String.valueOf(e));
		}
		return ret;
	}
	private  static ConcurrentHashMap<String, WxApiInfo> getWxApiMapping() {
		ConcurrentHashMap<String, WxApiInfo> ret = new ConcurrentHashMap<String, WxApiInfo>();
		try {
			IWxApiInfoDao dao=(IWxApiInfoDao)MyApplicationContextUtil.getBean("wxApiInfoDao");
			
			final List<WxApiInfo> pos = dao.getAllApi();
			for (WxApiInfo po : pos) {
				ret.put(po.getApiName(), po);
			}
		} catch (Exception e) {
			LOG.error("fail get wxapi mapping, " + e.getMessage());
		}
		return ret;
	}
	public static String id2Name(Integer serviceID) {
		if (serviceID == null) {
			return null;
		}

		return config.serviceid2Name.get(serviceID);
	}
	public static WxApiInfo getWxApiInfoByName(String apiName) {
		if (apiName == null) {
			return null;
		}

		WxApiInfo po = config.wxApiMapping.get(apiName);
		return po;
	}

	public static TService getService(String serviceName) {
		if (serviceName == null) {
			return null;
		}

		TService po = config.serviceConfigStr.get(serviceName);
		return po;
	}

	public static void setService(TService tService) {
		if (null != tService) {
		    String serviceName=tService.getName();
			config.serviceConfigStr.put(serviceName, tService);
			config.serviceid2Name.put(tService.getServiceID(), serviceName);
			if ("base".equals(tService.getType())) {
	             ConcurrentHashMap<String, D2Data>  map=Cache4BaseService.DATA_CACHE.get(serviceName);
	             if(MapUtils.isNotEmpty(map)){
	                   Iterator<Map.Entry<String, D2Data>> cacheLay2= map.entrySet().iterator();
	                   for (; cacheLay2.hasNext(); ) {
	                            Map.Entry<String, D2Data> entryLay2 = cacheLay2.next();
	                            final D2Data curD2Data = entryLay2.getValue();
	                            try {
	                               if(Cache4BaseService.DATA_CACHE.get(serviceName)!=null){
	                                   Cache4BaseService.DATA_CACHE.remove(serviceName);
	                               }
	                               SqlUtil.cleanSqlCache(tService);
	                               Cache4BaseService.getD2Data(tService, curD2Data.parameter);
	                               LOG.info("clear cache reload base serviceName="+serviceName+",param:"+ curD2Data.parameter);
	                           } catch (RengineException e) {
	                               LOG.error("clear cache reload base serviceName:"+serviceName+",error:"+e.getMessage());
	                           }
	                   }
	              }
			} else {
			    //LOG.info("clear cache  clear advance serviceName="+serviceName);
			    if(Cache4AdvanceService.DATA_CACHE.get(serviceName)!=null){
			        LOG.info("clear advance cache serviceName="+serviceName);
			        Cache4AdvanceService.DATA_CACHE.remove(serviceName);
			    }
			}
		}
	}

	public static void deleteService(TService tService) {
		if (null != tService) {
			config.serviceConfigStr.remove(tService.getName());
			config.serviceid2Name.remove(tService.getServiceID());
			if ("base".equals(tService.getType())) {
				Cache4BaseService.DATA_CACHE.remove(tService.getName());
				SqlUtil.cleanSqlCache(tService);
			} else {
				Cache4AdvanceService.DATA_CACHE.remove(tService.getName());
			}
		}
	}

	public static TServiceDB getDB(String dbID) {
		if (dbID == null) {
			return null;
		}

		return config.dbConfig.get(dbID);
	}

	public static String getServiceMapping(String name) {
		if (name == null) {
			return null;
		}

		return config.serviceMapping.get(name);
	}

	private  ConcurrentHashMap<String, CopyOnWriteArrayList<TServiceNextDBConfig>> getNextDbConfig() {
		final ConcurrentHashMap<String, CopyOnWriteArrayList<TServiceNextDBConfig>> nextDbConfigNew = new ConcurrentHashMap<String, CopyOnWriteArrayList<TServiceNextDBConfig>>();
		try {
			List<TServiceNextDBConfig> nextDBConfigs = tServiceNextDBConfigDao.findAllTServiceNextDBConfigInfo();
			for (TServiceNextDBConfig nextDBConfig : nextDBConfigs) {
				String serviceName = nextDBConfig.getServiceName();
				if (nextDbConfigNew.keySet().contains(serviceName)) {
					CopyOnWriteArrayList<TServiceNextDBConfig> oldNextDbConfigs = nextDbConfigNew.get(serviceName);
					oldNextDbConfigs.add(nextDBConfig);
					nextDbConfigNew.put(serviceName, oldNextDbConfigs);
				} else {
					CopyOnWriteArrayList<TServiceNextDBConfig> newNextDbConfigs = new CopyOnWriteArrayList<TServiceNextDBConfig>();
					newNextDbConfigs.add(nextDBConfig);
					nextDbConfigNew.put(serviceName, newNextDbConfigs);
				}

			}
		} catch (Exception e) {
			LOG.error("获取nextDbConfig失败");
		}
		return nextDbConfigNew;

	}

	public static String nextDbIdByConfig(String serviceName, Map<String, Object> param) {
		if (!config.nextDbConfig.keySet().contains(serviceName)) {
			return null;
		}
		try {
			String dbID = null;
			CopyOnWriteArrayList<TServiceNextDBConfig> nextDBConfigs = config.nextDbConfig.get(serviceName);
			for (TServiceNextDBConfig nextDBConfig : nextDBConfigs) {
				String dbNameByParam = (String) param.get(nextDBConfig.getDbNameKey());
				if (dbNameByParam.equals(nextDBConfig.getDbNameValue())) {
					dbID = nextDBConfig.getDbID();
					break;
				}
			}
			return dbID;
		} catch (Exception e) {
			LOG.error("获取nextDBID失败");
			return null;
		}

	}

	public static void reloadDbConfig(String dbID) {
		try {
		    ITServiceDBDao tServiceDBDao=(ITServiceDBDao)MyApplicationContextUtil.getBean("tServiceDBDao");
		    Map<String,String> param=new HashMap<String, String>();
		    param.put("dbID", dbID);
			List<TServiceDB> dbs = tServiceDBDao.getTServiceDBInfoById(param);
			TServiceDB db=null;
			if(!CollectionsUtils.isNull(dbs)){
			    db=dbs.get(0);
			    config.dbConfig.put(dbID, db);
			    
			    ArrayBlockingQueue<RengineConnection> connections = Cache4BaseService.CONN_POOL.get(dbID);
			    if (connections != null) {
                    int id = 1;
                    RengineConnection connection;
                    while ((connection = connections.poll()) != null) {
                        try {
                            connection.jdbcConn.close();
                            LogUtil.info("succ close connection, " + id + "@" + dbID);
                        } catch (Exception e) {
                            LogUtil.error("", e);
                            LogUtil.info("fail close connection, " + id + "@" + dbID);
                        }
                        id++;
                    }
                }
			}
		} catch (Throwable t) {
			LOG.error("load db_config error");
		}
	}
	/**
	 * 刷新微信API配置信息
	 */
	public static void reloadWxApiInfo() {
		try {
			config.wxApiMapping = getWxApiMapping();
		} catch (Throwable t) {
			LOG.error("load wx api error:"+t.getMessage());
		}
	}
	private static HashMap<String, TServiceEnDecryptKey> getEnDecryptConfig() {
		HashMap<String, TServiceEnDecryptKey> ret = new HashMap<String, TServiceEnDecryptKey>();
		return ret;
	}

	 
    private static void submitDealChange(Runnable runnable) {
    	        try {
    	            dealChangePool.submit(runnable);
    	        } catch (Exception e) {
    	            LOG.error("error submit deal change, " + String.valueOf(e));
    	        }
    	  }
    
    public void loadRuleConfig(){
        long time = 0;
            try {
                if (time >= config_cache_second || needReload) {
                    needReload = false;
                    time = 0;
                    configChanged.clear();
                    final List<TService> pos = tServiceDao.getTServiceInfoByName(null);
                    final List<TServiceDB> dbpos = tServiceDBDao.getTServiceDBInfoById(null);
                    
                    final ConcurrentHashMap<String, TService> serviceConfigStrNew = new ConcurrentHashMap<String, TService>();
                    final ConcurrentHashMap<Integer, String> serviceid2NameNew = new ConcurrentHashMap<Integer, String>();
                    final ConcurrentHashMap<String, TServiceDB> dbConfigNew = new ConcurrentHashMap<String, TServiceDB>();
                    final HashMap<String, String> serviceMappingNew = getServiceMapping();
                    final ConcurrentHashMap<String, CopyOnWriteArrayList<TServiceNextDBConfig>> nextDbConfigNew = getNextDbConfig();
                    
                    for (TService service : pos) {
                        final TService old = getService(service.getName());

                        if (old != null && old.equals(service)) {
                            serviceConfigStrNew.put(service.getName(), old);
                            serviceid2NameNew.put(service.getServiceID(), old.getName());
                        } else {
                            serviceConfigStrNew.put(service.getName(), service);
                            serviceid2NameNew.put(service.getServiceID(), service.getName());
                        }
                    }

                    for (TServiceDB dbPo : dbpos) {
                        final TServiceDB oldPo = getDB(dbPo.getDbID());

                        if (oldPo != null && oldPo.equals(dbPo)) {
                            dbConfigNew.put(dbPo.getDbID(), oldPo);
                        } else {
                            dbConfigNew.put(dbPo.getDbID(), dbPo);
                        }
                    }

                    Config newC = new Config();
                    newC.dbConfig = dbConfigNew;
                    newC.serviceConfigStr = serviceConfigStrNew;
                    newC.serviceid2Name = serviceid2NameNew;
                    newC.serviceMapping = serviceMappingNew;
                    newC.nextDbConfig = nextDbConfigNew;
                    newC.wxApiMapping = getWxApiMapping();
                    LOG.info("Reload serviceid2Name again!");
                    config = newC;
                } else {
                    String serviceName;
                    while ((serviceName = configChanged.poll()) != null) {
                        
                        TService po = (TService) tServiceDao.getTServiceInfoByName(serviceName);

                        if (po == null) { // 删除
                            TService oldPo = config.serviceConfigStr.remove(serviceName);
                            if (oldPo != null) {
                                config.serviceid2Name.remove(oldPo.getServiceID());
                            }
                        } else {
                            TService oldPo = getService(po.getName());
                            if (oldPo == null) { // 新增
                                config.serviceConfigStr.put(po.getName(), po);
                                config.serviceid2Name.put(po.getServiceID(), po.getName());
                            } else { // 修改
                                config.serviceConfigStr.put(po.getName(), po);
                                config.serviceid2Name.put(po.getServiceID(), po.getName());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("load config error, " +e.getMessage());
            }
    }
    
    public void loadRuleData(){
        final PriorityQueue<ReloadInfo> needReloadQ = new PriorityQueue<ReloadInfo>();
            try {
                Iterator<Map.Entry<String, ConcurrentHashMap<String, D2Data>>> cacheLay1
                        = Cache4BaseService.DATA_CACHE.entrySet().iterator();
                for (; cacheLay1.hasNext(); ) {
                    Map.Entry<String, ConcurrentHashMap<String, D2Data>> entryLay1 = cacheLay1.next();
                    String baseServiceName=entryLay1.getKey();
                    final TService po = getService(baseServiceName); 
                    if (po != null) {
                        final int cacheTime = po.getCacheTime() == null ? 0 : po.getCacheTime();
                        final long currentTime = System.currentTimeMillis() / 1000;
                        final long timestamp = po.getUpdateTime() == null ? 0L : po.getUpdateTime().getTime();

                        Iterator<Map.Entry<String, D2Data>> cacheLay2
                                = entryLay1.getValue().entrySet().iterator();

                        for (; cacheLay2.hasNext(); ) {
                            Map.Entry<String, D2Data> entryLay2 = cacheLay2.next();
                            final D2Data curD2Data = entryLay2.getValue();
                            final long timeElapsed = currentTime - curD2Data.createtime; // 已流失的时间
                            final long timeSurvied = cacheTime - timeElapsed; // 存活时间

                            if (curD2Data.timestamp != timestamp
                                    || timeElapsed > cacheTime) {
                                // 已不是最新版本，已过期
                                LOG.info("clear cache " +entryLay1.getKey());
                                cacheLay2.remove(); // 立即移除
                            } else if (timeSurvied <= PRE_RELOAD_TIME_THRESHOLD
                                    && cacheTime >= RELOAD_CACHETIME_THRESHOLD
                                    && curD2Data.latency <= RELOAD_LATENCY_THRESHOLD
                                    && !curD2Data.isInReload) {
                                // 提前五分钟开始Reload,
                                // 如果是缓存时间超过半小时，且reload时延不超过2s
                                // 如果在Reload中，则不重新Reload
                                curD2Data.isInReload = true;
                                needReloadQ.offer(new ReloadInfo(
                                        curD2Data.lastAcTime, po
                                        , curD2Data.parameter
                                        , curD2Data.latency
                                        , curD2Data.createtime));
                                LOG.info("reload data " +entryLay1.getKey());
                            }
                        }
                    } else { // 已删除，需要移除整个Lay1层的缓存
                        cacheLay1.remove();
                        LOG.info("clear cache " +entryLay1.getKey());
                    }
                }

                long sum = RELOAD_THREAD_COUNT * FIND_CHANGE_PERIOD_SECOND * 1000;
                ReloadInfo needReload;

                while ((needReload = needReloadQ.poll()) != null && sum > 0) {
                    final ReloadInfo willReload = needReload;
                    final TService po = willReload.po;

                    submitDealChange(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Cache4BaseService.getD2Data(po, willReload.param, willReload.createtime);
                            } catch (Exception e) {
                                LOG.error("load data error, " +e.getMessage());
                            }
                        }
                    });

                    sum -= willReload.latency;
                }
                needReloadQ.clear();
            } catch (Exception e) {
                LOG.error("load data error, " +e.getMessage());
            }
        }
}
