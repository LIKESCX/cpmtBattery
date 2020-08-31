package com.cpit.cpmt.biz.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.main.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
public class TestEmailUtil {

	@Value("${platform.interface.access.url}")
	String piau;
	
	@Autowired
	private AccessManageMgmt mgmt;
	
	@Value("${secret.key.operator.id}")
	private String operatorKey;//运营商秘钥
	
	@Value("${secret.key.data}")
	private String dataKey;//数据加密秘钥==加密向量
	
	@Value("${secret.key.sign}")
	private String signKey;//签名秘钥
	
	@Value("${platform.operator.id}")
	private String platformId;//平台id
	
	@Value("${platform.interface.access.url}")
	private String accessUrl;//平台接口地址
	
	@Value("${https.platform.ca}")
	private String caCrt;//ca证书
	
	@Value("${https.platform.client}")
	private String clientCrt;//client证书
	
	@Value("${https.platform.client.key}")
	private String clientKey;//client密钥
	
	@Value("${ssl.client.keystore.url}")
	private String keyStoreFile;//keystore file
	
	@Value("${ssl.client.keystore.pwd}")
	private String keyStorePwd;//keystore pwd

	@Test
	public void sendMore() {
		List<String> toWhos = new ArrayList<String>();
		// tos.add("liyinghui@potevio.com");
		toWhos.add("cpithz_xuqiushuo@potevio.com");
		toWhos.add("zhoujinguang@potevio.com");
		EmailUtil.sendMore("通知", "<h1>hello world，你好</h1><br>平台接口访问地址：" + piau, toWhos);
		try {
			TimeUnit.SECONDS.sleep(60);
		} catch (InterruptedException e) {
		}
	}

	@Test
	public void send() {
		String toWho = "zhoujinguang@potevio.com";
		EmailUtil.send("通知2", "<h1>hello world，你好</h1><br>平台接口访问地址：" + piau, toWho);
		try {
			TimeUnit.SECONDS.sleep(60);
		} catch (InterruptedException e) {
		}
	}
	
	@Test
	public void testHtml() {
		StringBuffer sb = new StringBuffer();
		BufferedInputStream bis = null;
		try {
			URL url = this.getClass().getClassLoader().getResource("emailTemplate/table.html");
			String urlString = url.toString();
			String fileUrl = urlString.substring(urlString.indexOf("/"));
			System.out.println(fileUrl);
			File file = new File(fileUrl);
			FileInputStream fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			int len = 0;
			byte[] temp = new byte[1024];
			while ((len = bis.read(temp)) != -1) {
				sb.append(new String(temp, 0, len));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String newContent = new String();
		String content = sb.toString();
		newContent = content;
		newContent = newContent.replace("{clientKey}", clientKey);
		newContent = newContent.replace("{clientCrt}", clientCrt);
		newContent = newContent.replace("{caCrt}", caCrt);
		newContent = newContent.replace("{keystoreFile}", keyStoreFile);
		newContent = newContent.replace("{keyStorePwd}", keyStorePwd);
		newContent = newContent.replace("{operatorKey}", operatorKey);
		newContent = newContent.replace("{secretKey}", "12312312");
		newContent = newContent.replace("{signKey}", signKey);
		newContent = newContent.replace("{dataKey}", dataKey);
		newContent = newContent.replace("{platformId}", platformId);
		newContent = newContent.replace("{accessUrl}", accessUrl);
		System.out.println(newContent);
	}
	
	@Test
	public void testEmail() throws Exception{
		for(int i=0;i<1;i++)
			mgmt.sendEmail("zhoujinguang@potevio.com", "47BA2356F3A7E833","359191261");
		TimeUnit.SECONDS.sleep(10);
	}
}
