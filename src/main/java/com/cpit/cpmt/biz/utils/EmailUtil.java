package com.cpit.cpmt.biz.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.cpit.common.SpringContextHolder;

public class EmailUtil {
	private final static Logger logger = LoggerFactory.getLogger(EmailUtil.class);

	private static ExecutorService	excthreadPool = Executors.newFixedThreadPool(5);

	private static final String HOST_NAME;
	private static final String ACCOUNT;
	private static final String PWD;
	private static final String FROM;
	private static final String OFFICIAL;
	
	private static String FROM_NAME = "深圳新能源汽车充电设施安全监控平台";

	static {
		Environment environment = SpringContextHolder.getApplicationContext().getEnvironment();
		HOST_NAME = environment.getProperty("email.host.name");
		ACCOUNT = environment.getProperty("email.account");
		PWD = environment.getProperty("email.pwd");
		FROM = environment.getProperty("email.from");
		OFFICIAL = environment.getProperty("email.official");
		if("yes".equals(OFFICIAL)) {
			FROM_NAME = FROM_NAME+"-正式用";
		}else {
			FROM_NAME = FROM_NAME+"-测试用";
		}
	}
	
	public static void send(String subject, String content,String toWho) {
		//logger.info("sendEmail subject ="+subject);
		//logger.info("sendEmail content ="+content);
		//logger.info("sendEmail toWho ="+toWho);

		if(subject == null || subject.trim().length()==0
			|| content == null || content.trim().length()==0
			|| toWho == null || toWho.trim().length()==0
			) {
			logger.error("can not sendEmail, subject-content-toWho is empty");
			return;
		}
		List<String> toWhos = new ArrayList<String>();
		toWhos.add(toWho);
		sendMore(subject,content,toWhos);
	}
	
	public static void sendMore( String subject, String content,List<String> toWhos) {
		if(subject == null || subject.trim().length()==0
				|| content == null || content.trim().length()==0
				|| toWhos == null || toWhos.size()==0
				) {
			logger.error("222can not sendEmail, subject-content-toWho is empty");
			return;
		}
		try {
			logger.info("begin to send email to "+subject);
			HtmlEmail email = new HtmlEmail();
			email.setHostName(HOST_NAME);
			email.setSmtpPort(25);
			email.setAuthentication(ACCOUNT, PWD);
			email.setCharset("utf-8");
			
			if(toWhos.size() == 1) {
				email.addTo(toWhos.get(0));
			}else {
				String[] toArr = new String[toWhos.size()];
				toWhos.toArray(toArr);
				email.addTo(toArr);
			}
			email.addBcc(FROM);
			email.setFrom(FROM,FROM_NAME);
			email.setSubject(subject);
			email.setHtmlMsg(content);
			syncSend(email);
		} catch (Exception ex) {
			logger.error("sendEmail fail",ex);
		}
	}
	
	//==========================private method
	private static void syncSend(HtmlEmail email) {
		excthreadPool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					int timeout = ThreadLocalRandom.current().nextInt(10, 999);
					TimeUnit.MILLISECONDS.sleep(timeout);
					email.send();
				} catch (Exception ex) {
					logger.error("send email error",ex);
				}
			}

		});
	}

}
