package com.bus.chelaile.ocsTest;

import com.bus.chelaile.ocsTest.cache.ICache;
import com.bus.chelaile.ocsTest.cache.OCSCacheUtil;
import com.bus.chelaile.ocsTest.cache.RedisCacheImplUtil;

public class CacheUtil {
	private static ICache client1;
	private static ICache client2;
	private static ICache redisClient;

	private static boolean isInitSuccess = false;

	public static void initClient() {

		if (isInitSuccess) {
			System.out.println("已经 initCache 成功");
			return;
		}
		// gongzhonghao  ocs online
		client1 = new OCSCacheUtil("d960fa96e23843f0.m.cnhzaliqshpub001.ocs.aliyuncs.com", "11211", "d960fa96e23843f0",
				"Chelaileocs2015");
		
		// 
		client2 = new OCSCacheUtil("9c0e27d0f09544c9.m.cnhzaliqshpub001.ocs.aliyuncs.com", "11211", "9c0e27d0f09544c9",
				"Yuanguang2014");
		redisClient = new RedisCacheImplUtil("127.0.0.1", 6379);

		System.out.println("cache init 成功 **********************");
		isInitSuccess = true;
	}

	public static Object get1(String key) {
		return client1.get(key);
	}

	public static Object get2(String key) {
		return client2.get(key);
	}

	public static void lPush(String key, String value) {
		redisClient.lpush(key, value);
	}

	public static String lpop(String key) {
		return redisClient.lpop(key);
	}

	
	public static void main(String[] args) throws InterruptedException {

		client1 = new OCSCacheUtil("121.40.210.76", "7078", "9c0e27d0f09544c9", "Yuanguang2014");
		client1.set("qkk_test1", 60, "1111");
		System.out.println(client1.get("qkk_test1"));
		System.out.println(client1.get("qkk_test2"));

		System.out.println(client1.get("WECHATSIGNATUREACCESSTOKEN"));

		System.exit(1);
	}
}
