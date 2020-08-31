package com.cpit.cpmt.biz.impl.exchange.basic;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RefreshScope
public class AuthenMgmt {
	private final static Logger logger = LoggerFactory.getLogger(AuthenMgmt.class);

	@Value("${uploadfile.access.url}")
	private String uploadfileAccessUrl;

	// 证书
	// private final static String URL =
	// "http://120.241.28.5:16668/cpmt/security/client.jks";
	// 证书密码
	// private final static String PWD = "123456";
	public RestTemplate sslTemplate(String url, String pwd) throws Exception {
		// uploadfileAccessUrl="http://120.241.28.5:16668/cpmt/";
		url = uploadfileAccessUrl + url;
		logger.debug("url[{}],pwd[{}]", url, pwd);
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
				.loadTrustMaterial(new URL(url), pwd.toCharArray()).build();

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).setRetryHandler(retryHandler)
				.build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
}
