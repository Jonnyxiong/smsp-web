package com.ucpaas.smsp.util;


import com.jsmsframework.common.util.HttpUtil;
import com.jsmsframework.common.util.JsonUtil;
import com.ucpaas.sms.common.util.StringUtils;
import com.ucpaas.smsp.model.SMSResponse;
import com.ucpaas.smsp.util.web.SSLHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("deprecation")
public class HttpUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	/**
	 * Http 请求方法
	 *
	 * @param url
	 * @param content
	 * @return
	 */
	public static String httpPost(String url, String content, boolean needSSL) {
		return HttpUtils.httpPost(url, content, needSSL,ContentType.APPLICATION_JSON);
	}
	public static String httpFormPost(String url, String content, boolean needSSL) {
		return HttpUtils.httpPost(url, content, needSSL,ContentType.APPLICATION_FORM_URLENCODED);
	}
	public static String httpPost(String url, String content, boolean needSSL,ContentType contentType) {
		// 创建HttpPost
		String result = null;
		HttpClient httpClient = getHttpClient(needSSL, StringUtils.getHostFromURL(url));
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", contentType + ";charset=utf-8");
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			httpPost.setConfig(requestConfig);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(content.getBytes("utf-8")));
			requestBody.setContentLength(content.getBytes("utf-8").length);
			httpPost.setEntity(requestBody);
			// 执行客户端请求
			HttpEntity entity = httpClient.execute(httpPost).getEntity();

			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
				EntityUtils.consume(entity);
			}

		} catch (Throwable e) {
			logger.error("【HTTP请求失败】: url={}, content={}, \n error:{}", url, content, e);
		}

		return result;
	}

	/**
	 * @Description json post
	 * @param url
	 * @param content
	 * @param needSSL
	 * @return
	 * @author wangwei
	 * @date 2017年3月9日 下午5:53:18
	 */
	public static String doJsonPost(String url, String content, boolean needSSL) {
		// 创建HttpPost
		String result = null;
		HttpClient httpClient = getHttpClient(needSSL, StringUtils.getHostFromURL(url));
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON + ";charset=utf-8");
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			httpPost.setConfig(requestConfig);
			int timeout = 10; //10秒
			try{
				timeout = 30; //30秒
			}catch (Exception e) {
				timeout = 10; //10秒
			}
			httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout*1000);
			httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout*1000);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(content.getBytes("utf-8")));
			requestBody.setContentLength(content.getBytes("utf-8").length);
			httpPost.setEntity(requestBody);
			// 执行客户端请求
			HttpEntity entity = httpClient.execute(httpPost).getEntity();

			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
				EntityUtils.consume(entity);
			}

		} catch (Throwable e) {
			logger.error("【HTTP请求失败】: url={}, content={}, \n error:{}", url, content, e);
			return "error";
		}

		return result;
	}

	public static DefaultHttpClient getHttpClient(boolean sslClient, String host) {
		DefaultHttpClient httpclient = null;
		if (sslClient) {
			try {
				SSLHttpClient chc = new SSLHttpClient();
				InetAddress address = null;
				String ip;
				try {
					address = InetAddress.getByName(host);
					ip = address.getHostAddress().toString();
					httpclient = chc.registerSSL(ip, "TLS", 443, "https");
				} catch (UnknownHostException e) {
					logger.error("获取请求服务器地址失败：host = {} " + host, e);
				}
				HttpParams hParams = new BasicHttpParams();
				hParams.setParameter("https.protocols", "SSLv3,SSLv2Hello");
				httpclient.setParams(hParams);
			} catch (KeyManagementException e) {
				logger.error("ssl error:", e);
			} catch (NoSuchAlgorithmException e) {
				logger.error("ssl error:", e);
			}
		} else {
			httpclient = new DefaultHttpClient();
		}
		return httpclient;
	}

	public static String fakeSMSPHttpPost() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("a", "a");
		map.put("b", "b");
		map.put("c", "c");
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(map);
		int i = 1;
		SMSResponse resp = new SMSResponse(i, list);
		String json = JsonUtil.toJson(resp);

		return json;
	}


	/**
	 * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
	 *
	 * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
	 * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
	 *
	 * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
	 * 192.168.1.100
	 *
	 * 用户真实IP为： 192.168.1.110
	 *
	 * @param request
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}


	public static void main(String[] args) throws UnknownHostException {
		
		System.out.println(InetAddress.getByName("api.ucpaas.com").getHostAddress().toString());
	}
}
