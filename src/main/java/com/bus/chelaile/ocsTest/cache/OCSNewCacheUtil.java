package com.bus.chelaile.ocsTest.cache;
//package com.bus.chelaile.common.cache;
//
//import net.spy.memcached.AddrUtil;
//import net.spy.memcached.ConnectionFactory;
//import net.spy.memcached.ConnectionFactoryBuilder;
//import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
//import net.spy.memcached.MemcachedClient;
//import net.spy.memcached.auth.AuthDescriptor;
//import net.spy.memcached.auth.PlainCallbackHandler;
//import net.spy.memcached.internal.OperationFuture;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.bus.chelaile.model.PropertiesName;
//import com.bus.chelaile.util.config.PropertiesUtils;
//
//
//
//
//
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicLong;
//
//public class OCSNewCacheUtil implements ICache{
//    final static String host = "9c0e27d0f09544c9.m.cnhzaliqshpub001.ocs.aliyuncs.com";//控制台上的“内网地址”
//    final static String port ="11211"; //默认端口 11211，不用改
//    final static String username = "9c0e27d0f09544c9";//控制台上的“访问账号”
//    final static String password = "Yuanguang2014";//邮件中提供的“密码”
//    private static final String PROP_OCS_HOST = "ocs.new.host";
//    private static final String PROP_OCS_PORT = "ocs.new.port";
//    private static final String PROP_OCS_USERNAME = "ocs.new.username";
//    private static final String PROP_OCS_PASSWORD = "ocs.new.password";
//
//    private static MemcachedClient client = null;
//
//    private static final int DEFAULT_EXPIRE = 60 * 60;
//
//    protected static final Logger logger = LoggerFactory.getLogger(OCSNewCacheUtil.class);
//
//    private static AtomicLong missCount = new AtomicLong(0);
//    private static AtomicLong hitCount = new AtomicLong(0);
//
//    static {
//        initClient();
//    }
//
//    private static void initClient() {
//        try {
//            AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"},
//                    new PlainCallbackHandler(readUsername(), readPassword()));
//
//            ConnectionFactory connFactory = new ConnectionFactoryBuilder().setProtocol(Protocol.BINARY).setAuthDescriptor(ad).build();
//
//            client = new MemcachedClient(connFactory,
//                    AddrUtil.getAddresses(readHost() + ":" + readPort()));
//            
//            logger.info("host:"+readHost() + ":" +readUsername());
//
//            if (client == null) {
//                logger.error("MemcachedClient client is NULL, 初始化不成功");
//            }
//        } catch (IOException ex) {
//            logger.error("初始化OCS MemcachedClient失败: " + ex.getMessage(), ex); 
//        }        
//    }
//    
//    public static MemcachedClient getClient() {
//        return client;
//    }
//    
//    /**
//     * @param key the Cache Key
//     * @param exp the expiration time of the records, should not exceeds 60 * 60 * 24 * 30(30 天), 单位: 秒
//     * @param obj 缓存的对象
//     */
//    public  void set(String key, int exp, Object obj) {
//        if (client == null) {
//            logger.error("[OCS_CLIENT_NULL] OCS Client is NULL: op=set");
//            return ;
//        }
//        
//        client.set(key, exp, obj);
//        if (isLogCacheDetail()) {
//            logger.info("[CACHE_SET] key={}, exp={}, obj={}", key, exp, obj);
//        }
//    }
//    
//    /**
//     * 设置缓存， 默认缓存时间是1小时。
//     * @param key 缓存的key
//     * @param obj 缓存的对象
//     */
//    public  void set(String key, Object obj) {
//        set(key, DEFAULT_EXPIRE, obj);
//    }
//    
//    public  Object get(String key) {
//        if (client == null) {
//            logger.error("[OCS_CLIENT_NULL] OCS Client is NULL: op=get");
//            return null;
//        }
//        
//        Object obj = null;
//        
//        try {
//            obj = client.get(key);
//        } catch(Exception ex) {
//            logger.error("[OCS_GET_EXCEPTION] key={}, errMsg={}", key, ex.getMessage());
//            try {
//                obj = client.get(key);
//                logger.info("OCS get retry SUCCESS, key={}, value={}", key, obj);
//            } catch(Exception ex2) {
//                logger.info("OCS get retry FAIL, key={}, errMsg={}", key, ex2.getMessage());
//            }
//        }
//        
//        if (logger.isInfoEnabled()) {
//            long currHit = -1;
//            long currMiss = -1;
//            
//            if (obj != null) {
//                if (isLogCacheDetail()) {
//                    logger.info("OCS Cache HIT: key={}, value={}", key, obj);
//                }
//                currHit = hitCount.incrementAndGet();
//                currMiss = missCount.get();
//            } else {
//                if (isLogCacheDetail()) {
//                    logger.info("OCS Cache MISS: key={}", key);
//                }
//                currMiss = missCount.incrementAndGet();
//                currHit = hitCount.get();
//            }
//            
//            long total = currHit + currMiss;
//            if ((total % 1000) == 0) {
//                logger.info(String.format("[OCS_SUMMARY] Total:%d, Miss:%d, MissRate:%.1f%%; Hit:%d, HitRate:%.1f%%", 
//                        total, currMiss, currMiss * 100.0 / total, currHit, currHit * 100.0 / total));
//            }
//        }
//        
//        return obj;
//    }
//    
//    public  OperationFuture<Boolean> delete(String key) {
//        if (client == null) {
//            logger.error("[OCS_CLIENT_NULL] OCS Client is NULL: op=delete");
//            return null;
//        }
//        
//        return client.delete(key);
//    }
//
//
//    public  Map<String, Object> getByList(List<String> list) {
//        if (client == null) {
//            logger.error("[OCS_CLIENT_NULL] OCS Client is NULL: op=getByList");
//            return null;
//        }
//        return client.getBulk(list);
//    }
//    
//    ////////////////////////////////////////
//    //   Private Methods
//    ////////////////////////////////////////
//    private static String readHost() {
//    	return PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_HOST);
//        //return PropertiesReaderWrapper.read(PROP_OCS_HOST, host);
//    }
//    
//    private static String readPort() {
//    	return PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_PORT);
//        //return PropertiesReaderWrapper.read(PROP_OCS_PORT, port);
//    }
//    
//    private static String readUsername() {
//    	return PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_USERNAME);
//        //return PropertiesReaderWrapper.read(PROP_OCS_USERNAME, username);
//    }
//    
//    private static String readPassword() {
//    	return PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_PASSWORD);
//        //return PropertiesReaderWrapper.read(PROP_OCS_PASSWORD, password);
//    }
//    
//    private static boolean isLogCacheDetail() {
//    	return Boolean.parseBoolean(PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), "log.ocs.cache.detail","false"));
//        //return PropertiesReaderWrapper.readBool("log.ocs.cache.detail", false);
//    }
//
//	@Override
//	public long IncValue(String key, int exp) {
//		return 0;
//	}
//
//	@Override
//	public boolean acquireLock() {
//		return false;
//	}
//
//	@Override
//	public boolean releaseLock() {
//		return false;
//	}
//
//	@Override
//	public void incrBy(String key, int incNumber, int exp) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public Set<String> getSet(String key) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}
