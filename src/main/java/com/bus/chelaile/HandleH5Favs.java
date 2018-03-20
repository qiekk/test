package com.bus.chelaile;

/**
 * @author quekunkun
 *
 */
import com.bus.chelaile.ocsTest.CacheUtil;
import com.bus.chelaile.ocsTest.cache.ICache;
import com.bus.chelaile.ocsTest.cache.RedisCacheImplUtil;
import com.bus.chelaile.thread.RequestWechatUnionIdThread;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleH5Favs {

	private static final int THREAD_COUNT = 1;
	private static final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(THREAD_COUNT);

	static final Logger logger = LoggerFactory.getLogger(HandleH5Favs.class);

	static final int RECORDSIZE = 7;

	static final Set<String> OPENIDSET = new HashSet<String>(); // all openids
	static final Set<String> lines = new HashSet<String>(); // all records
	public static final Map<String, String> OPENID_UNIONID = new HashMap<String, String>(); // openId-->unionId#type
	// //type:
	// 1
	// 公众号，
	// 2
	// 小程序
	static final Set<String> KEYS = new HashSet<String>(); // 去重用 ,
															// unionId+cityId+lineId+stopName+nextStopName

	public static final String POPKEY = "FAV_KEY";

	static BufferedReader reader;
	static BufferedWriter writer;
	static BufferedWriter writerNoUnion;
	static String fileIn = "/data/quekunkun/favzhuanyi/favFile.txt";
	static String fileOut = "/data/quekunkun/favzhuanyi/favout.txt";
	static String fileNoUnion = "/data/quekunkun/favzhuanyi/nounion.txt";
	static boolean isDebug = false;

	public static void main(String[] args) throws Exception {
		CacheUtil.initClient();
		System.out.println("1111111111111");
		System.out.println(args.length);
		if (args.length >= 1)
			fileIn = args[0];
		if (args.length >= 2)
			fileOut = args[1];
		if (args.length >= 3)
			isDebug = Boolean.parseBoolean(args[2]);

		System.out.println("fileIn=" + fileIn + ", fileOut=" + fileOut + ",  isDebug=" + isDebug);
		// PropertyConfigurator.configure(".\\src\\log4j.properties");
		// logger.info("日志测试！！！！！！！");
		handleFavFile();

		System.exit(1);
	}

	private static void handleFavFile() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "utf-8"));
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut), "utf-8"));
		writerNoUnion = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNoUnion), "utf-8"));
		int handleN = 0;
		int notHandleN = 0;
		int totalN = 0;

		System.out.println(Runtime.getRuntime().maxMemory());

		// 读文件、缓存
		String buf = null;
		while ((buf = reader.readLine()) != null) {
			totalN++;
			String bufS[] = buf.split("#");
			lines.add(buf);
			OPENIDSET.add(bufS[0]);
		}
		reader.close();
		// redis duoxiancheng
		for (String s : OPENIDSET) {
			CacheUtil.lPush(POPKEY, s);
		}
		System.out.println("totalN=" + totalN);

		// 调微信接口，openId对应unionId
		getOpenIdUnionId();

		// 生成新串，写文件
		for (String line : lines) {
			String sl[] = line.split("#");
			if (OPENID_UNIONID.containsKey(sl[0])) {
				if (sl[6].equalsIgnoreCase("NULL")) {
					sl[6] = "0";
				}
				writer.write(OPENID_UNIONID.get(sl[0]) + "#" + sl[1] + "#" + sl[2] + "#" + sl[3] + "#" + sl[4] + "#"
						+ sl[5] + "#" + sl[6]);
				handleN++;
				writer.newLine();
				writer.flush();
			} else {
				notHandleN++;
				writerNoUnion.write(sl[0] + "#" + "0" + "#" + sl[1] + "#" + sl[2] + "#" + sl[3] + "#" + sl[4] + "#"
						+ sl[5] + "#" + sl[6]);
				writerNoUnion.newLine();
				writerNoUnion.flush();
			}
		}
		writer.close();

		System.out.println("total=" + totalN);
		System.out.println("handle=" + handleN);
		System.out.println("notHandle=" + notHandleN);
	}

	/**
	 * @param openidunion2
	 * @param openidset2
	 */
	private static void getOpenIdUnionId() {

		// 多线程
		int c = THREAD_COUNT;
		final CountDownLatch latch = new CountDownLatch(c);
		for (int i = 0; i < c; i++) {
			exec.execute(new RequestWechatUnionIdThread(isDebug, latch, i));
		}

		try {
			// 等待所有线程计算完毕
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}