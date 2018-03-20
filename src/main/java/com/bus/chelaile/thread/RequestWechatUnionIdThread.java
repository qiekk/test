/**
 * @author quekunkun
 *
 */
package com.bus.chelaile.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.HandleH5Favs;
import com.bus.chelaile.ocsTest.cache.ICache;
import com.bus.chelaile.ocsTest.cache.OCSCacheUtil;
import com.bus.chelaile.util.Utilities;

public class RequestWechatUnionIdThread implements Runnable {

	static final String URLPOST = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=%s";
	static final String URLGET = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
	static ICache client1 = new OCSCacheUtil("d960fa96e23843f0.m.cnhzaliqshpub001.ocs.aliyuncs.com", "11211",
			"d960fa96e23843f0", "Chelaileocs2015");
	static ICache client2 = new OCSCacheUtil("9c0e27d0f09544c9.m.cnhzaliqshpub001.ocs.aliyuncs.com", "11211",
			"9c0e27d0f09544c9", "Yuanguang2014");

	private String openId;
	private boolean Debug;

	public RequestWechatUnionIdThread(String openId, boolean Debug) {
		this.openId = openId;
		this.Debug = Debug;
	}

	public void run() {

		int type = 1;
		String w1Token = (String) client1.get("WECHATSIGNATUREACCESSTOKEN");
		String url = String.format(URLGET, w1Token, getOpenId());
		String response = Utilities.newHttpGet(url);
		JSONObject resJ = JSON.parseObject(response);
		System.out.println("response=" + response);
		String errorcode = resJ.getString("errcode");
		String unionId = resJ.getString("unionid");
		if (errorcode != null || unionId == null) {
			type = 2;
			String w2Token = (String) client2.get("WECHATSIGNATUREACCESSTOKEN");
			url = String.format(URLGET, w2Token, getOpenId());
			response = Utilities.newHttpGet(url);
			resJ = JSON.parseObject(response);

			if (isDebug())
				System.out.println("response=" + response);

			errorcode = resJ.getString("errcode");
			unionId = resJ.getString("unionid");
			if (errorcode != null || unionId == null) {
				return;
			}
		}

		//bu kaolv ziyuan chongtu 
		HandleH5Favs.OPENID_UNIONID.put(getOpenId(), unionId + "#" + type);
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public boolean isDebug() {
		return Debug;
	}

	public void setDebug(boolean debug) {
		Debug = debug;
	}

}