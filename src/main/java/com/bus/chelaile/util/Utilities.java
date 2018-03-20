package com.bus.chelaile.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Utilities {
	
//	private static Logger log = CLLog.getLogger(Utilities.class);
	
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5','6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}
	
	public static final String encode(String str){
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
    /* 
     * HTTP Get and Post Utilities
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

 
//    public static String initParams(String url, Map<String, String> params){
//        if (null == params || params.isEmpty()) {
//            return url;
//        }
//        StringBuilder sb = new StringBuilder(url);
//        if (url.indexOf("?") == -1) {
//            sb.append("?");
//        } else {
//            sb.append("&");
//        }
//        boolean first = true;
//        for (Entry<String, String> entry : params.entrySet()) {
//            if (first) {
//                first = false;
//            } else {
//                sb.append("&");
//            }
//            String key = entry.getKey();
//            String value = entry.getValue();
//            sb.append(key).append("=");
//            if (StringUtils.isNotEmpty(value)) {
//                try {
//                    sb.append(URLEncoder.encode(value, DEFAULT_CHARSET));
//                } catch (UnsupportedEncodingException e) {
// //               	log.error("Failed to send request", e);
//                }
//            }
//        }
//        return sb.toString();
//    }
    
    public static String httpGet(String url) {
		StringBuffer bufferRes = new StringBuffer();
		try {
			URL urlGet = new URL(url);
			HttpURLConnection http = (HttpURLConnection) urlGet
					.openConnection();
			http.setConnectTimeout(25000);
			http.setReadTimeout(25000);
			http.setRequestMethod("GET");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			http.connect();

			InputStream in = http.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
			String valueString = null;
			bufferRes = new StringBuffer();
			while ((valueString = read.readLine()) != null) {
				bufferRes.append(valueString);
			}
			in.close();
			if (http != null) {
				http.disconnect();
			}
		} catch (MalformedURLException e) {
//			log.error("Failed to send request", e);
		} catch (ProtocolException e) {
//			log.error("Failed to send request", e);
		} catch (UnsupportedEncodingException e) {
//			log.error("Failed to send request", e);
		} catch (IOException e) {
//			log.error("Failed to send request", e);;
		}
		if (null == bufferRes)
			return null;
		else
			return bufferRes.toString();
    }
    
	public static String newHttpGet(String url) {
    	HttpClient client = new HttpClient();
    	GetMethod getMethod = new GetMethod(url);
    	try {
			client.executeMethod(getMethod);
			String ret = getMethod.getResponseBodyAsString();
			ret = new String(ret.getBytes(getMethod.getRequestCharSet()), "UTF-8");
			return ret;
		} catch (HttpException e) {
//			log.error("Http Get request failed", e);
		} catch (IOException e) {
//			log.error("Http Get request failed", e);
		}
    	return null;
    }
    
    @SuppressWarnings("deprecation")
	public static String newHttpPost(String url, String data) {
    	HttpClient client = new HttpClient();
    	PostMethod postMethod = new PostMethod(url);
    	postMethod.setRequestBody(data);
    	
    	try {
			client.executeMethod(postMethod);
			return postMethod.getResponseBodyAsString();
		} catch (HttpException e) {
//			log.error("Failed to send request", e);
		} catch (IOException e) {
//			log.error("Failed to send request", e);
		}
    	
    	return null;
    }
    
	@SuppressWarnings("resource")
	public static JSONObject doPost(String url, String jsonStr) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		JSONObject response = null;
		try {
			StringEntity s = new StringEntity(jsonStr);
			s.setContentEncoding("UTF-8");
			s.setContentType("application/json");// 发送json数据需要设置contentType
			post.setEntity(s);
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// HttpEntity entity = res.getEntity();
				String result = EntityUtils.toString(res.getEntity());// 返回json格式：
				response = JSON.parseObject(result);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return response;
	}
	
    
    public static String httpPost(String url, String data) {
        StringBuffer bufferRes = null;
        try {
			URL urlGet = new URL(url);
			HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
			http.setConnectTimeout(25000);
			http.setReadTimeout(25000);
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			http.connect();
			
			OutputStream out = http.getOutputStream();
			out.write(data.getBytes("UTF-8"));
			out.flush();
			out.close();

			InputStream in = http.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
			String valueString = null;
			bufferRes = new StringBuffer();
			while ((valueString = read.readLine()) != null) {
				bufferRes.append(valueString);
			}
			in.close();
			if (http != null) {
				http.disconnect();
			}
		} catch (MalformedURLException e) {
//			log.error("Failed to send request", e);
		} catch (ProtocolException e) {
//			log.error("Failed to send request", e);
		} catch (UnsupportedEncodingException e) {
//			log.error("Failed to send request", e);
		} catch (IOException e) {
//			log.error("Failed to send request", e);
		}
		if (null == bufferRes)
			return null;
		else
			return bufferRes.toString();
    }
    public static void main(String[] args)
    {
    	String url = "http://api.chelaile.net.cn:7000/wow/user!login.action?qqnickname=? ?&qqiconurl=http%3A%2F%2Fq.qlogo.cn%2Fqqapp%2F100490037%2F3D0B50B72160783F6C2920416089468D%2F100&udid=f4_09_d8_1c_d8_77&clientId=3D0B50B72160783F6C2920416089468D&nw=WIFI&sign=%2BFBrWeQafldZ0ApjHfRukA%3D%3D&v=2.2.0&s=android&last_src=app_baidu_as&sv=4.4.2&vc=26&first_src=app_qq_sj";
    	System.out.print("1================");
    	//String result = httpGet(url);
    	try
    	{
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
		Date d2 = sdf.parse("24:00");
		String fstartTime = sdf.format(d2);
    	System.out.println(fstartTime);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
