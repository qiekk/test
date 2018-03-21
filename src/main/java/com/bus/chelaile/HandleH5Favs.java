package com.bus.chelaile;

/**
 * @author quekunkun
 *
 */
import com.bus.chelaile.ocsTest.CacheUtil;
import com.bus.chelaile.thread.RequestWechatUnionIdThread;

import java.io.*;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleH5Favs {

	private static int THREAD_COUNT = 100;

	static final Logger logger = LoggerFactory.getLogger(HandleH5Favs.class);

	static final int RECORDSIZE = 7;

	static final Set<String> OPENIDSET = new HashSet<String>(); // all openids
	public static final Set<String> lines = new HashSet<String>(); // all records
	public static final Map<String, String> OPENID_UNIONID = new HashMap<String, String>(); // openId-->unionId#type
	// //type:
	// 1
	// 公众号，
	// 2
	// 小程序
	public static final Set<String> KEYS = new HashSet<String>(); // 去重用 ,
															// unionId+cityId+lineId+stopName+nextStopName

	public static final String POPKEY = "FAV_KEY";

	static BufferedReader reader;
	static BufferedWriter writer;
	static BufferedWriter writerNoUnion;
	static BufferedWriter writerCopy;
	static String fileIn = "/data/quekunkun/favzhuanyi/favFile.txt";
	static String fileCopy = "/data/quekunkun/favzhuanyi/copy.txt";
	public static boolean hasWrite = false;
//	String fileOut = "/data/quekunkun/favzhuanyi/favout.txt";
//	String fileNoUnion = "/data/quekunkun/favzhuanyi/nounion.txt";
	static boolean isDebug = false;

	public static void main(String[] args) throws Exception {
		CacheUtil.initClient();
		System.out.println("1111111111111");
		System.out.println(args.length);
		if (args.length >= 1)
			fileIn = args[0];
		if (args.length >= 2)
//			fileOut = args[1];
		if (args.length >= 3)
			isDebug = Boolean.parseBoolean(args[2]);
		if (args.length >= 4)
			THREAD_COUNT = Integer.parseInt(args[3]);

		ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(THREAD_COUNT);
//		System.out.println("fileIn=" + fileIn + ", fileOut=" + fileOut + ",  isDebug=" + isDebug);
		// PropertyConfigurator.configure(".\\src\\log4j.properties");
		// logger.info("日志测试！！！！！！！");
		handleFavFile(exec);

		System.exit(1);
	}

	private static void handleFavFile(ScheduledThreadPoolExecutor exec) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "utf-8"));
		writerCopy = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileCopy), "utf-8"));
		int totalN = 0;

		System.out.println(Runtime.getRuntime().maxMemory());

		// 读文件、缓存
		String buf = null;
		while ((buf = reader.readLine()) != null) {
			totalN++;
			String bufS[] = buf.split("#");
			lines.add(buf);
			OPENIDSET.add(bufS[0]);
			
			// copy file
//			if (bufS[6].equalsIgnoreCase("NULL")) {
//				bufS[6] = "0";
//			}
//			writerCopy.write(bufS[0] + "#" + "0#" + bufS[1] + "#" + bufS[2] + "#" + bufS[3] + "#" + bufS[4] + "#"
//					+ bufS[5] + "#" + bufS[6]);
//			writerCopy.newLine();
//			writerCopy.flush();
		}
		writerCopy.close();
		reader.close();
		// redis duoxiancheng
		for (String s : OPENIDSET) {
			CacheUtil.lPush(POPKEY, s);
		}
		OPENIDSET.clear();
		System.out.println("totalN=" + totalN);

		// 调微信接口，openId对应unionId
		getOpenIdUnionId(exec);
	}

	/**
	 * @param openidunion2
	 * @param openidset2
	 */
	private static void getOpenIdUnionId(ScheduledThreadPoolExecutor exec) {

		// 多线程
		int c = THREAD_COUNT;
//		final CountDownLatch latch = new CountDownLatch(c);
		for (int i = 0; i < c; i++) {
			exec.execute(new RequestWechatUnionIdThread(isDebug, i));
		}

//		try {
//			// 等待所有线程计算完毕
//			latch.await();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

}