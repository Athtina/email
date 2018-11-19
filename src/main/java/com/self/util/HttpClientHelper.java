package com.self.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

public class HttpClientHelper {

	private static final String APPLICATION_X_WWW_FORM_URLENCODED = ContentType.APPLICATION_FORM_URLENCODED
			.getMimeType();
	private static final String APPLICATION_JSON = ContentType.APPLICATION_JSON.getMimeType();

	private static final String CHARTSET = "UTF-8";

	private static final int CONNTIMEOUT = 60000;

	private static final int READTIMEOUT = 60000;

	private static final int MAX_RETRY = 3;

	private static HttpClientBuilder httpClientBuilder;

	private static CloseableHttpClient httpClient;

	static {
		SSLContext ctx = SSLContexts.createDefault();
		X509TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws java.security.cert.CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws java.security.cert.CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		try {
			ctx.init(null, new TrustManager[] { tm }, null);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);

		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", plainsf).register("https", sslsf).build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);

		cm.setMaxTotal(1000);

		cm.setDefaultMaxPerRoute(30);

		cm.setValidateAfterInactivity(2000);

		httpClientBuilder = HttpClients.custom().setConnectionManager(cm)
				.setDefaultRequestConfig(getCustomReqConfig(CONNTIMEOUT, READTIMEOUT));

		httpClient = httpClientBuilder.build();
	}

	public static CloseableHttpClient createHttpClient() {
		return httpClientBuilder.build();
	}

	public static <T> T postForObject(String url, String body, Class<T> toClass) throws IOException {
		String res = post(url, body, APPLICATION_X_WWW_FORM_URLENCODED, CHARTSET, CONNTIMEOUT, READTIMEOUT);
		return JsonHelper.parseToObject(res, toClass);
	}

	public static Map<?, ?> postForMap(String url, String body) throws IOException {
		String res = post(url, body, APPLICATION_X_WWW_FORM_URLENCODED, CHARTSET, CONNTIMEOUT, READTIMEOUT);
		return JsonHelper.parseToMap(res);
	}

	public static String post(String url, String body) throws IOException {
		return post(url, body, APPLICATION_X_WWW_FORM_URLENCODED, CHARTSET, CONNTIMEOUT, READTIMEOUT);
	}

	public static String post(String url, String body, String charset, Integer connTimeout, Integer readTimeout)
			throws IOException {
		return post(url, body, APPLICATION_X_WWW_FORM_URLENCODED, charset, connTimeout, readTimeout);
	}

	public static String postJsonData(String url, String body) throws IOException {
		return post(url, body, APPLICATION_JSON, CHARTSET, CONNTIMEOUT, READTIMEOUT);
	}

	public static <T> T postJsonData(String url, String body, Class<T> toClass) throws IOException {
		String res = post(url, body, APPLICATION_JSON, CHARTSET, CONNTIMEOUT, READTIMEOUT);
		return JsonHelper.parseToObject(res, toClass);
	}

	public static <T> T postFormData(String url, Map<String, String> params, Class<T> toClass) throws IOException {
		String res = postForm(url, params, null, CONNTIMEOUT, READTIMEOUT);
		return JsonHelper.parseToObject(res, toClass);
	}

	public static String postFormData(String url, Map<String, String> params) throws IOException {
		return postForm(url, params, null, CONNTIMEOUT, READTIMEOUT);
	}

	public static String postFormData(String url, Map<String, String> params, Integer connTimeout, Integer readTimeout)
			throws IOException {
		return postForm(url, params, null, connTimeout, readTimeout);
	}

	public static <T> T getForObject(String url, Class<T> toClass) throws UnsupportedOperationException, IOException {
		String res = get(url, CHARTSET, CONNTIMEOUT, READTIMEOUT);
		return JsonHelper.parseToObject(res, toClass);
	}

	public static String get(String url) throws IOException {
		return get(url, CHARTSET, CONNTIMEOUT, READTIMEOUT);
	}

	public static String get(String url, Map<String, String> map) throws IOException {
		StringBuffer sb = new StringBuffer(url);
		if (MapUtils.isNotEmpty(map)) {
			sb.append("?");
			for (Entry<String, String> set : map.entrySet()) {
				sb.append(set.getKey()).append("=").append(set.getValue()).append("&");
			}
		}
		return get(sb.toString());
	}

	public static String get(String url, String charset) throws IOException {
		return get(url, charset, CONNTIMEOUT, READTIMEOUT);
	}

	public static String post(String url, String body, String mimeType, String charset, Integer connTimeout,
			Integer readTimeout) throws IOException {
		HttpPost post = new HttpPost(url);
		try {
			if (StringUtils.isNotBlank(body)) {
				HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
				post.setEntity(entity);
			}
			RequestConfig customReqConfig = getCustomReqConfig(connTimeout, readTimeout);
			post.setConfig(customReqConfig);
			HttpResponse res = httpClient.execute(post);
			return EntityUtils.toString(res.getEntity(), charset);
		} finally {
			post.releaseConnection();
		}
	}

	public static String postForm(String url, Map<String, String> params, Map<String, String> headers,
			Integer connTimeout, Integer readTimeout) throws IOException {
		HttpPost post = new HttpPost(url);
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> formParams = new ArrayList<>();
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
				post.setEntity(entity);
			}
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			RequestConfig customReqConfig = getCustomReqConfig(connTimeout, readTimeout);
			post.setConfig(customReqConfig);
			HttpResponse res = httpClient.execute(post);
			return EntityUtils.toString(res.getEntity(), CHARTSET);
		} finally {
			post.releaseConnection();
		}
	}

	public static String get(String url, String charset, Integer connTimeout, Integer readTimeout)
			throws UnsupportedOperationException, IOException {
		HttpGet get = new HttpGet(url);
		try {
			RequestConfig customReqConfig = getCustomReqConfig(connTimeout, readTimeout);
			get.setConfig(customReqConfig);
			HttpResponse res = httpClient.execute(get);
			return EntityUtils.toString(res.getEntity(), charset);
		} finally {
			get.releaseConnection();
		}
	}

	private static RequestConfig getCustomReqConfig(Integer connTimeout, Integer readTimeout) {
		Builder customReqConf = RequestConfig.custom();
		if (connTimeout != null) {
			customReqConf.setConnectTimeout(connTimeout);
		}
		if (readTimeout != null) {
			customReqConf.setSocketTimeout(readTimeout);
		}
		return customReqConf.build();
	}
}
