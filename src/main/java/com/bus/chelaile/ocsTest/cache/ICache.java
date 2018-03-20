package com.bus.chelaile.ocsTest.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.spy.memcached.internal.OperationFuture;

public interface ICache {
	 public void set(String key, int exp, Object obj);
	 
	 public Object get(String key);
	
	 public OperationFuture<Boolean> delete(String key);
	 
	 public Map<String, Object> getByList(List<String> list);
	 
	 
	 public long IncValue(String key, int exp);
	 
	 // redis 分布式锁获取
	 public boolean acquireLock();
	 
	 // redis 分布式锁释放
	 public boolean releaseLock();
	 
	 //
	 public void incrBy(String key, int incNumber, int exp);

	 // 从redis直接获取set
	 public Set<String> getSet(String key);
	 
	 
	 // redis 有序集合
	 public void setSortedSet(String key, double score,String value, int expire);
	 public Set<String> zrangeByScore(String key, double startScore, double endScore, int count);
	 public Set<String> zrevRangeByScore(String key, double endScore, double startScore, int count);

	public void lpush(String key, String value);

	public String lpop(String key);
}
