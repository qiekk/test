package com.bus.chelaile.ocsTest.cache;

import net.spy.memcached.internal.OperationFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisCacheImplUtil implements ICache {
	private static Logger log = LoggerFactory.getLogger(RedisCacheImplUtil.class);

	protected static final Logger logger = LoggerFactory.getLogger(RedisCacheImplUtil.class);

	private static String DEFAULT_REDIS_HOST = "127.0.0.1";
	private static int DEFAULT_REDIS_PORT = Integer.parseInt("6379");

	private static JedisPool pool = null;

    public RedisCacheImplUtil(String host,int port){
    	initPool(host, port);
    }

	private static void initPool(String host, int port) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(400);
		// config.setMaxActive(400);
		config.setMaxIdle(200);
		config.setMinIdle(20);

		// config.setMaxWaitMillis();
		// TODO 
//		config.setTestWhileIdle(true);
//		config.setTestOnBorrow(true);
//		config.setTestOnReturn(true);


		pool = new JedisPool(config, host, port);

		System.out.println("***** redis初始化， redis info , host=" + host + ", port=" + port);
		log.info("RedisCacheImplUtil init success,ip={},host={}", host, port);
	}

	private static JedisPool getPool() {
		if (pool == null) {
			initPool(DEFAULT_REDIS_HOST, DEFAULT_REDIS_PORT);
		}
		return pool;
	}

	public void set(String key, String value, int expire) {
		JedisPool pool = null;
		Jedis conn = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			conn.set(key, value);
			if (expire != -1)
				conn.expire(key, expire);
			log.debug("Redis-Set: Key=" + key + ",Value=" + value);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.set, key=%s value=%s, error message: " + e.getMessage(), key,
					value));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
	}

	public void setList(String key, String value, int expire) {
		JedisPool pool = null;
		Jedis conn = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			conn.lpush(key, value);
			if (expire != -1)
				conn.expire(key, expire);
			log.debug("Redis-Set: Key=" + key + ",Value=" + value);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.set, key=%s value=%s, error message: " + e.getMessage(), key,
					value));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
	}

	public void setSet(String key, String value, int expire) {
		JedisPool pool = null;
		Jedis conn = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			conn.sadd(key, value);
			if (expire != -1)
				conn.expire(key, expire);
			log.debug("Redis-Set: Key=" + key + ",Value=" + value);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.set, key=%s value=%s, error message: " + e.getMessage(), key,
					value));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
	}

	/**
	 * 缓存 有序集合
	 * 
	 * @param key
	 * @param score
	 * @param value
	 * @param expire
	 */
	public void setSortedSet(String key, double score, String value, int expire) {
		JedisPool pool = null;
		Jedis conn = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			conn.zadd(key, score, value);
			if (expire != -1)
				conn.expire(key, expire);
			log.debug("Redis-SortedSet: Key=" + key + ",Value=" + value);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.set, key=%s value=%s, error message: " + e.getMessage(), key,
					value));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
	}

	/**
	 * 按照score，从小到大获取值。 起始score为startScore，从0开始获取count个值
	 * 
	 * @return
	 */
	public Set<String> zrangeByScore(String key, double startScore, double endScore, int count) {
		JedisPool pool = null;
		Jedis conn = null;
		Set<String> value = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			value = conn.zrangeByScore(key, startScore, endScore, 0, count);
			log.debug("Redis-SortedGet: Key=" + key + ",Value=" + value);
		} catch (Exception e) {
			log.error(String.format(
					"Error occur in Redis.set, key=%s startScore=%s, endScore=%s, error message: " + e.getMessage(),
					key, startScore, endScore));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return value;
	}

	/**
	 * 按照score，从大到小获取值。 起始score为endScore，从0开始获取count个值
	 * 
	 * @return
	 */
	public Set<String> zrevRangeByScore(String key, double endScore, double startScore, int count) {
		JedisPool pool = null;
		Jedis conn = null;
		Set<String> value = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			value = conn.zrevrangeByScore(key, endScore, startScore, 0, count);
			log.debug("Redis-SortedGet: Key=" + key + ",Value=" + value);
		} catch (Exception e) {
			log.error(String.format(
					"Error occur in Redis.set, key=%s startScore=%s, endScore=%s, error message: " + e.getMessage(),
					key, startScore, endScore));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return value;
	}

	public void clearDB() {
		JedisPool pool = null;
		Jedis conn = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			conn.flushDB();
			log.debug("Redis-clearDB");
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.flushDB"));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
	}

	public Set allKeys(String patternstr) {
		JedisPool pool = null;
		Jedis conn = null;
		Set result = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			result = conn.keys(patternstr);
			log.debug("Redis-Keys: pattern=" + patternstr);
		} catch (Exception e) {
			log.error("Error occur in Redis.keys, pattern=: " + patternstr + " exception:" + e);
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return result;
	}

	public void set(String key, String value) {
		set(key, value, -1);
	}

	public void addList(String key, String value) {
		setList(key, value, -1);
	}

	public List<String> getMValue(String[] udidList) {
		JedisPool pool = null;
		Jedis conn = null;
		List<String> ret = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			ret = conn.mget(udidList);
			log.debug("Redis-Get: Key=" + udidList.length + ",Value=" + ret);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.set, key=%s, error message: " + e.getMessage(),
					udidList.length));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return ret;
	}

	public String get(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		String ret = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			ret = conn.get(key);
			log.debug("Redis-Get: Key=" + key + ",Value=" + ret);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.set, key=%s, error message: " + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}

		return ret;
	}

	public List<String> getList(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		List<String> ret = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			ret = conn.lrange(key, 0, -1);
			log.debug("Redis-Get: Key=" + key + ",Value=" + ret);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.set, key=%s, error message: " + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}

		return ret;
	}

	public Set<String> getSet(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		Set<String> ret = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			ret = conn.smembers(key);
			log.debug("Redis-Get: Key=" + key + ",Value=" + ret);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.set, key=%s, error message: " + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}

		return ret;
	}

	public void del(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			conn.del(key);
			log.debug("Redis-Del: Key=" + key);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.del, key=%s" + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
	}

	public long delFromSet(String key, String member) {
		JedisPool pool = null;
		Jedis conn = null;
		long i = -1;
		try {
			pool = getPool();
			conn = pool.getResource();
			i = conn.srem(key, member);
			log.debug("Redis-Del: Key=" + key);

		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.del, key=%s" + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return i;
	}

	public long delFromSortedSet(String key, String member) {
		JedisPool pool = null;
		Jedis conn = null;
		long i = -1;
		try {
			pool = getPool();
			conn = pool.getResource();
			i = conn.zrem(key, member);
			log.debug("Redis-Del: Key=" + key);

		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.del, key=%s" + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return i;
	}

	public boolean isInSet(String key, String member) {
		JedisPool pool = null;
		Jedis conn = null;
		boolean flag = false;
		try {
			pool = getPool();
			conn = pool.getResource();
			flag = conn.sismember(key, member);
			log.debug("Redis-Del: Key=" + key);

		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.del, key=%s" + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return flag;
	}

	public long getValueSize(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		long i = 0;
		try {
			pool = getPool();
			conn = pool.getResource();
			i = conn.scard(key);
			log.debug("Redis-Del: Key=" + key);

		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.del, key=%s" + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return i;
	}

	public long getSortedSetValueSize(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		long i = 0;
		try {
			pool = getPool();
			conn = pool.getResource();
			i = conn.zcard(key);
			log.debug("Redis-Del: Key=" + key);

		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.del, key=%s" + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return i;
	}

	/**
	 * 设置有效期，单位 秒
	 */
	public long IncValue(String key, int expire) {
		JedisPool pool = null;
		Jedis conn = null;
		long i = 0;
		try {
			pool = getPool();
			conn = pool.getResource();
			i = conn.incr(key);
			if (expire != -1)
				conn.expire(key, expire);
			log.debug("Redis-Del: Key=" + key);

		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.del, key=%s" + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return i;
	}

	public static long DecValue(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		long i = 0;
		try {
			pool = getPool();
			conn = pool.getResource();
			i = conn.decr(key);
			log.debug("Redis-Del: Key=" + key);

		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.del, key=%s" + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return i;
	}


	
	public Map<String, String> getHsetAll(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		Map<String, String> result = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			result = conn.hgetAll(key);

			log.debug("Redis-Hget: Key={}, value={}", key, result);
		} catch (Exception e) {
			log.error("Error occur in Redis.Hget, key={}, error message: {}", key, e.getMessage());
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return result;
	}

	public String getHashSetValue(String key, String field) {
		JedisPool pool = null;
		Jedis conn = null;
		String result = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			result = conn.hget(key, field);

			log.debug("Redis-Hget: Key={}, field={}, value={}", key, field, result);
		} catch (Exception e) {
			log.error("Error occur in Redis.Hget, key={}, error message: {}", key, e.getMessage());
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return result;
	}

	public Long setHashSetValue(String key, String field, String value) {
		JedisPool pool = null;
		Jedis conn = null;
		Long result = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			result = conn.hset(key, field, value);

			log.debug("Redis-Hset: Key={}, field={}, value={}", key, field, result);
		} catch (Exception e) {
			log.error("Error occur in Redis.Hset, key={}, field={}, value={}, error message: {}", key, field, result, e.getMessage());
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return result;
	}
	
	
	public Set<String> getHKeys(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		Set<String> result = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			result = conn.hkeys(key);

			log.debug("Redis-Hkeys: Key={}", key);
		} catch (Exception e) {
			log.error("Error occur in Redis.Hset, key={}, error message: {}", key, e.getMessage());
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return result;
	}
	
	public void addHashSetValue(String key, String field, int value) {
		JedisPool pool = null;
		Jedis conn = null;
		Long result = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			result = conn.hincrBy(key, field, value);
			
			log.debug("Redis-Hset: Key={}, field={}, value={}", key, field, result);
		} catch (Exception e) {
			log.error("Error occur in Redis.Hset, key={}, error message: {}", key, e.getMessage());
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return;
	}

	

	public void lpush(String key, String value) {
		JedisPool pool = null;
		Jedis conn = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			conn.lpush(key, value);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.lpush, key=%s,value=%s" + e.getMessage(), key, value));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
	}

	public String lpop(String key) {
		JedisPool pool = null;
		Jedis conn = null;
		String value = null;
		try {
			pool = getPool();
			conn = pool.getResource();
			value = conn.lpop(key);
		} catch (Exception e) {
			log.error(String.format("Error occur in Redis.lpop, key=%s" + e.getMessage(), key));
			if (pool != null && conn != null) {
				pool.returnResource(conn);
				pool = null;
				conn = null;
			}
		} finally {
			if (pool != null && conn != null)
				pool.returnResource(conn);
		}
		return value;
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

	public void set(String key, int exp, Object obj) {
		// TODO Auto-generated method stub
		
	}

	public OperationFuture<Boolean> delete(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getByList(List<String> list) {
		// TODO Auto-generated method stub
		return null;
	}
}
