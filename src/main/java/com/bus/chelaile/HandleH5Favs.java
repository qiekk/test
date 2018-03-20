package com.bus.chelaile;

/**
 * @author quekunkun
 *
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.ocsTest.cache.ICache;
import com.bus.chelaile.ocsTest.cache.OCSCacheUtil;
import com.bus.chelaile.util.Utilities;

import java.io.*;
import java.util.*;

public class HandleH5Favs {

	static final int RECORDSIZE = 7;
	static final String URLPOST = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=%s";
	static final String URLGET = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

	static ICache client1 = new OCSCacheUtil("d960fa96e23843f0.m.cnhzaliqshpub001.ocs.aliyuncs.com", "11211",
			"d960fa96e23843f0", "Chelaileocs2015");
	static ICache client2 = new OCSCacheUtil("9c0e27d0f09544c9.m.cnhzaliqshpub001.ocs.aliyuncs.com", "11211",
			"9c0e27d0f09544c9", "Yuanguang2014");
	static final Set<String> OPENIDSET = new HashSet<String>(); // all openids
	static final Set<String> lines = new HashSet<String>(); // all records
	static final Map<String, String> OPENID_UNIONID = new HashMap<String, String>(); // openId-->unionId#type
																						// //type:
																						// 1
																						// 公众号，
																						// 2
																						// 小程序
	static final Set<String> KEYS = new HashSet<String>(); // 去重用 ,
															// unionId+cityId+lineId+stopName+nextStopName

	static BufferedReader reader;
	static BufferedWriter writer;
	static String fileIn = "/data/quekunkun/favzhuanyi/favFile.txt";
	static String fileOut = "/data/quekunkun/favzhuanyi/favout.txt";
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
		handleFavFile();

		System.exit(1);
	}

	private static void handleFavFile() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "utf-8"));
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut), "utf-8"));
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
				writer.write(OPENID_UNIONID.get(sl[0]) + "#" + sl[0] + "#" + sl[1] + "#" + sl[2] + "#" + sl[3] + "#"
						+ sl[4] + "#" + sl[5] + "#" + sl[6]);
				handleN++;
				writer.newLine();
				writer.flush();
			} else {
				notHandleN++;
				System.out.println("no union id ,openId=" + sl[0]);
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
		// openidunion2.put("ozKKGuBw5F3Vs6XpUza9LEAH3bJc", "1231231231321213");
		for (String openId : OPENIDSET) {
			// System.out.println(client.get("WECHATSIGNATUREACCESSTOKEN"));
			// String url = String.format(URL,
			// client.get("WECHATSIGNATUREACCESSTOKEN"));

			int type = 1;
			String w1Token = (String) client1.get("WECHATSIGNATUREACCESSTOKEN");
			String url = String.format(URLGET, w1Token, openId);
			String response = Utilities.newHttpGet(url);
			JSONObject resJ = JSON.parseObject(response);
			System.out.println("response=" + response);
			String errorcode = resJ.getString("errcode");
			String unionId = resJ.getString("unionid");
			if (errorcode != null || unionId == null) {
				type = 2;
				String w2Token = (String) client2.get("WECHATSIGNATUREACCESSTOKEN");
				url = String.format(URLGET, w2Token, openId);
				response = Utilities.newHttpGet(url);
				resJ = JSON.parseObject(response);
				
				if(isDebug)
					System.out.println("response=" + response);
				
				errorcode = resJ.getString("errcode");
				unionId = resJ.getString("unionid");
				if (errorcode != null || unionId == null) {
					continue;
				}
			}

			OPENID_UNIONID.put(openId, unionId + "#" + type);
		}
	}

}