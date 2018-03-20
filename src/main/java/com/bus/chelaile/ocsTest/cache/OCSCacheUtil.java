package com.bus.chelaile.ocsTest.cache;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.OperationFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.bus.chelaile.ocsTest.cache.ICache;

public class OCSCacheUtil implements ICache{
//    final static String host = "9c0e27d0f09544c9.m.cnhzaliqshpub001.ocs.aliyuncs.com";//控制台上的“内网地址”
//    final static String port ="11211"; //默认端口 11211，不用改
//    final static String username = "9c0e27d0f09544c9";//控制台上的“访问账号”
//    final static String password = "Yuanguang2014";//邮件中提供的“密码”
//    private static final String PROP_OCS_HOST = "ocs.host";
//    private static final String PROP_OCS_PORT = "ocs.port";
//    private static final String PROP_OCS_USERNAME = "ocs.username";
//    private static final String PROP_OCS_PASSWORD = "ocs.password";

    private MemcachedClient client = null;

    private static final int DEFAULT_EXPIRE = 60 * 60;

    protected static final Logger logger = LoggerFactory.getLogger(OCSCacheUtil.class);

    private static AtomicLong missCount = new AtomicLong(0);
    private static AtomicLong hitCount = new AtomicLong(0);

    public OCSCacheUtil(String PROP_OCS_HOST,String PROP_OCS_PORT,String PROP_OCS_USERNAME,String PROP_OCS_PASSWORD){
    	initClient(PROP_OCS_HOST, PROP_OCS_PORT, PROP_OCS_USERNAME, PROP_OCS_PASSWORD);
    }

    private void initClient(String PROP_OCS_HOST,String PROP_OCS_PORT,String PROP_OCS_USERNAME,String PROP_OCS_PASSWORD ) {
        try {
            AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"},
                    new PlainCallbackHandler(PROP_OCS_USERNAME, PROP_OCS_PASSWORD));

            ConnectionFactory connFactory = new ConnectionFactoryBuilder().setProtocol(Protocol.BINARY).setAuthDescriptor(ad).build();

            logger.info("准备初始化OCS：PROP_OCS_HOST={},PROP_OCS_PORT={},PROP_OCS_USERNAME={},PROP_OCS_PASSWORD={}",PROP_OCS_HOST,PROP_OCS_PORT,PROP_OCS_USERNAME,PROP_OCS_PASSWORD);
            client = new MemcachedClient(connFactory,
                    AddrUtil.getAddresses(PROP_OCS_HOST + ":" + PROP_OCS_PORT));

            if (client == null) {
                logger.error("MemcachedClient client is NULL, 初始化不成功");
            }else{
            	logger.info("初始化OCS成功：PROP_OCS_HOST={},PROP_OCS_PORT={},PROP_OCS_USERNAME={},PROP_OCS_PASSWORD={}",PROP_OCS_HOST,PROP_OCS_PORT,PROP_OCS_USERNAME,PROP_OCS_PASSWORD);
            }
        } catch (IOException ex) {
            logger.error("初始化OCS MemcachedClient失败: " + ex.getMessage(), ex); 
        }        
    }
    
//    public static MemcachedClient getClient() {
//        return client;
//    }
    
    /**
     * @param key the Cache Key
     * @param exp the expiration time of the records, should not exceeds 60 * 60 * 24 * 30(30 天), 单位: 秒
     * @param obj 缓存的对象
     */
    public  void set(String key, int exp, Object obj) {
        if (client == null) {
            logger.error("[OCS_CLIENT_NULL] OCS Client is NULL: op=set");
            return ;
        }
        
        client.set(key, exp, obj);
    }
    
    /**
     * 设置缓存， 默认缓存时间是1小时。
     * @param key 缓存的key
     * @param obj 缓存的对象
     */
    public  void set(String key, Object obj) {
        set(key, DEFAULT_EXPIRE, obj);
    }
    
    public  Object get(String key) {
        if (client == null) {
            logger.error("[OCS_CLIENT_NULL] OCS Client is NULL: op=get");
            return null;
        }
        
        Object obj = null;
        
        try {
            obj = client.get(key);
        } catch(Exception ex) {
            logger.error("[OCS_GET_EXCEPTION] key={}, errMsg={}", key, ex.getMessage());
            try {
                obj = client.get(key);
                logger.info("OCS get retry SUCCESS, key={}, value={}", key, obj);
            } catch(Exception ex2) {
                logger.info("OCS get retry FAIL, key={}, errMsg={}", key, ex2.getMessage());
            }
        }
        
        if (logger.isInfoEnabled()) {
            long currHit = -1;
            long currMiss = -1;
            
            if (obj != null) {
//                if (isLogCacheDetail()) {
//                    logger.info("OCS Cache HIT: key={}, value={}", key, obj);
//                }
                currHit = hitCount.incrementAndGet();
                currMiss = missCount.get();
            } else {
//                if (isLogCacheDetail()) {
//                    logger.info("OCS Cache MISS: key={}", key);
//                }
                currMiss = missCount.incrementAndGet();
                currHit = hitCount.get();
            }
            
            long total = currHit + currMiss;
            if ((total % 1000) == 0) {
                logger.info(String.format("[OCS_SUMMARY] Total:%d, Miss:%d, MissRate:%.1f%%; Hit:%d, HitRate:%.1f%%", 
                        total, currMiss, currMiss * 100.0 / total, currHit, currHit * 100.0 / total));
            }
        }
        
        return obj;
    }
    
    public  OperationFuture<Boolean> delete(String key) {
        if (client == null) {
            logger.error("[OCS_CLIENT_NULL] OCS Client is NULL: op=delete");
            return null;
        }
        
        return client.delete(key);
    }


    public  Map<String, Object> getByList(List<String> list) {
        if (client == null) {
            logger.error("[OCS_CLIENT_NULL] OCS Client is NULL: op=getByList");
            return null;
        }
        return client.getBulk(list);
    }

	public long IncValue(String key, int exp) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean acquireLock() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean releaseLock() {
		// TODO Auto-generated method stub
		return false;
	}

	public void incrBy(String key, int incNumber, int exp) {
		// TODO Auto-generated method stub
		
	}

	public Set<String> getSet(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSortedSet(String key, double score, String value, int expire) {
		// TODO Auto-generated method stub
		
	}

	public Set<String> zrangeByScore(String key, double startScore, double endScore, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> zrevRangeByScore(String key, double endScore, double startScore, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	public void lpush(String key, String value) {
		// TODO Auto-generated method stub
		
	}

	public String lpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}
    
    ////////////////////////////////////////
    //   Private Methods
    ////////////////////////////////////////
//    private static String readHost() {
//    	return PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_HOST);
//        //return PropertiesReaderWrapper.read(PROP_OCS_HOST, host);
//    }
//    
//    private static String readPort() {
//    	return PropertiesUtils.getValue(PropertiesName.CACHE.getValue(), PROP_OCS_PORT);
//       // return PropertiesReaderWrapper.read(PROP_OCS_PORT, port);
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
}
