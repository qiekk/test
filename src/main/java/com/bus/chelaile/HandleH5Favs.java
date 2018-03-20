package com.bus.chelaile;

/**
 * @author quekunkun
 *
 */
import com.bus.chelaile.thread.RequestWechatUnionIdThread;

import java.io.*;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleH5Favs {

	private static final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(10);
	
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

	static BufferedReader reader;
	static BufferedWriter writer;
	static BufferedWriter writerNoUnion;
	static String fileIn = "/data/quekunkun/favzhuanyi/favFile.txt";
	static String fileOut = "/data/quekunkun/favzhuanyi/favout.txt";
	static String fileNoUnion = "/data/quekunkun/favzhuanyi/nounion.txt";
	static boolean isDebug = false;

	public static void main(String[] args) throws Exception {
		System.out.println("1111111111111");
		System.out.println(args.length);
		if(args.length >= 1)
			fileIn = args[0];
		if(args.length >= 2)
			fileOut = args[1];
		if(args.length >= 3)
			isDebug = Boolean.parseBoolean(args[2]);
			
		System.out.println("fileIn=" + fileIn + ", fileOut=" + fileOut + ",  isDebug=" + isDebug);
//		PropertyConfigurator.configure(".\\src\\log4j.properties");
//		logger.info("日志测试！！！！！！！");
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

		// 调微信接口，openId对应unionId
		getOpenIdUnionId();

		// 生成新串，写文件
		for (String line : lines) {
			String sl[] = line.split("#");
			if (OPENID_UNIONID.containsKey(sl[0])) {
				if (sl[6].equalsIgnoreCase("NULL")) {
					sl[6] = "0";
				}
				writer.write(OPENID_UNIONID.get(sl[0]) + "#" + sl[1] + "#" + sl[2] + "#" + sl[3] + "#"
						+ sl[4] + "#" + sl[5] + "#" + sl[6]);
				handleN++;
				writer.newLine();
				writer.flush();
			} else {
				notHandleN++;
//				writerNoUnion.write(sl[0] + "#" + "0" + "#" + sl[1] + "#" + sl[2] + "#" + sl[3] + "#"
//						+ sl[4] + "#" + sl[5] + "#" + sl[6]);
//				writerNoUnion.newLine();
//				writerNoUnion.flush();
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
		for (String openId : OPENIDSET) {

			RequestWechatUnionIdThread reqThread = new RequestWechatUnionIdThread(openId, isDebug);
			exec.execute(reqThread);
			
		}
	}

}