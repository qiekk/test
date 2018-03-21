/**
 * @author quekunkun
 *
 */
package com.bus.chelaile.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.HandleH5Favs;
import com.bus.chelaile.ocsTest.CacheUtil;
import com.bus.chelaile.util.Utilities;

public class RequestWechatUnionIdThread implements Runnable {

	static final String URLPOST = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=%s";
	static final String URLGET = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

	private boolean debug;
//	private CountDownLatch cntLatch;
	private int i ;

	public RequestWechatUnionIdThread(boolean Debug, int i) {
		this.debug = Debug;
//		this.cntLatch = cntLatch;
		this.i = i;
	}

	public void run() {
		System.out.println("******************* thread num = " + i);
		int count = 0;

		try {
			while (true) {
				String openId = null;
				try {
					openId = CacheUtil.lpop(HandleH5Favs.POPKEY);
					if (openId == null)
						break;
				} catch (Exception e) {
					continue;
				}

				try {
					if (count % 1000 == 0)
						System.out.println("thread:" + i + ", handledcount:" + count);
					count++;
					int type = 1;
					String w1Token = (String) CacheUtil.get1("WECHATSIGNATUREACCESSTOKEN");
					String url = String.format(URLGET, w1Token, openId);
					String response = Utilities.newHttpGet(url);
					JSONObject resJ = JSON.parseObject(response);
					if (debug)
						System.out.println("response=" + response);

					String errorcode = resJ.getString("errcode");
					String unionId = resJ.getString("unionid");
					if (errorcode != null || unionId == null) {
						type = 2;
						String w2Token = (String) CacheUtil.get2("wechat#token#wx71d589ea01ce3321");
						// wechat#token#wx71d589ea01ce3321
						// WECHATSIGNATUREACCESSTOKEN
						url = String.format(URLGET, w2Token, openId);
						response = Utilities.newHttpGet(url);
						resJ = JSON.parseObject(response);

						if (isDebug())
							System.out.println("response=" + response);

						errorcode = resJ.getString("errcode");
						unionId = resJ.getString("unionid");
						if (errorcode != null || unionId == null) {
							continue;
						}
					}
					// bu kaolv ziyuan chongtu
					HandleH5Favs.OPENID_UNIONID.put(openId, unionId + "#" + type);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			System.out.println("############## thread num over ,i = " + i );
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

//	public CountDownLatch getCntLatch() {
//		return cntLatch;
//	}
//
//	public void setCntLatch(CountDownLatch cntLatch) {
//		this.cntLatch = cntLatch;
//	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

}