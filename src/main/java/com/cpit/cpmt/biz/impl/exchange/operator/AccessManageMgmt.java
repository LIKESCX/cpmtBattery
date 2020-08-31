package com.cpit.cpmt.biz.impl.exchange.operator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.AccessManageDao;
import com.cpit.cpmt.biz.impl.system.OAuth2Mgmt;
import com.cpit.cpmt.biz.utils.EmailUtil;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;

@Service
public class AccessManageMgmt {
	private final static Logger logger = LoggerFactory.getLogger(AccessManageMgmt.class);

	@Autowired
	private AccessManageDao accessManageDao;
	
	@Autowired
	private OperatorInfoMgmt operatorInfoMgmt;

	@Autowired
	private AccessParamMgmt accessParamMgmt;
	
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
	
	@Value("${email.official}")
	private String official;

	
	private static String emailTemplate = "";
	
	@Autowired
	private OAuth2Mgmt oauth2Mgmt;

	static{
		mkEmailTemplate();
	}
	

	@Cacheable(cacheNames="operator-access-manage-by-id",key="#root.caches[0].name+#operatorId",unless="#result == null")
	public AccessManage getAccessManageInfoById(String operatorId) {
		
		return accessManageDao.selectByPrimaryKey(operatorId);
	}
	
	@Transactional
	public void addAccessManage(AccessManage accessManage) throws Exception {
		Integer ifAccess = accessManage.getIfAccess();
		if(ifAccess==AccessManage.IFACCESS_ON) {
			String operatorID = accessManage.getOperatorID();
			OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(operatorID);
			if(null==operatorInfo.getSecretKey()) {
				String keyCreate = "";
				OperatorInfoExtend operator = new OperatorInfoExtend();
				keyCreate = KeyCreate(16);
				if(keyCreate.equals(operatorKey)) {
					keyCreate = KeyCreate(16);
				}
				operator.setSecretKey(keyCreate);
				operator.setOperatorID(operatorID);
				operatorInfoMgmt.updateOperatorInfo(operator);
		    	//往oauth_token表里添加记录
		        oauth2Mgmt.addOAuth2(operatorID, keyCreate);
		        //发邮件
		        sendEmail(operatorInfo.getContactEmail(),keyCreate,operatorID);
			}
		}
		accessManageDao.insertSelective(accessManage);
	}

	public Page<AccessManage> getAccessManageList(AccessManage accessManage) {
		return accessManageDao.getAccessManageList(accessManage);
	}

	@Transactional
	@Caching(evict={
	 	@CacheEvict(cacheNames="operator-access-manage-by-id",key="#root.caches[0].name+#accessManage.operatorID")
	})
	public void updateAccessManage(AccessManage accessManage) {
		Integer ifAccess = accessManage.getIfAccess();
		if(null!=ifAccess) {
			String operatorID = accessManage.getOperatorID();
			AccessManage manage = accessManageDao.selectByPrimaryKey(operatorID);
			Integer oldIfAccess = manage.getIfAccess();
			OperatorInfoExtend infoExtend = operatorInfoMgmt.getOperatorInfoById(operatorID);
			if(oldIfAccess==AccessManage.IFACCESS_OFF && ifAccess==AccessManage.IFACCESS_ON && null==infoExtend.getSecretKey()) {
				OperatorInfoExtend operator = new OperatorInfoExtend();
				String keyCreate = KeyCreate(16);
				if(keyCreate.equals(operatorKey)) {
					keyCreate = KeyCreate(16);
				}
				operator.setSecretKey(keyCreate);
				operator.setOperatorID(operatorID);
				operatorInfoMgmt.updateOperatorInfo(operator);
				//往oauth_token表里添加记录
		        oauth2Mgmt.addOAuth2(operatorID, keyCreate);
				//发邮件
				sendEmail(infoExtend.getContactEmail(),keyCreate,operatorID);
			}
		}
		accessManageDao.updateByPrimaryKeySelective(accessManage);
	}

	@CacheEvict(cacheNames="operator-access-manage-by-id",key="#root.caches[0].name+#operatorId")
	public void delAccessManage(String operatorId) {
		accessManageDao.deleteByPrimaryKey(operatorId);
	}
	
	public static String KeyCreate(int KeyLength) {
        String base = "ABCDEF0123456789";
	    Random random = new Random();
	    StringBuffer Keysb = new StringBuffer();
	    // 生成指定位数的随机秘钥字符串
	    for (int i = 0; i < KeyLength; i++) {
	       int number = random.nextInt(base.length());
	       Keysb.append(base.charAt(number));
	    }
	    return Keysb.toString();
	}
	
	public void sendEmail(String email, String secretKey,String operatorId) {
		OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(operatorId);
		if(operatorInfo == null) {
			logger.error("can not email AccessInfo because operator is null");
			return;
		}
		String subject = "秘钥信息-To"+operatorInfo.getOperatorName();
		String content = changeContent(secretKey,operatorInfo);
		EmailUtil.send(subject, content,email);
	}
	
	public String changeContent(String secretKey,OperatorInfoExtend operatorInfo) {
		String newContent = new String(emailTemplate);
		if("yes".equals(official)) {
			newContent = newContent.replace("{official}", "正式");
		}else {
			newContent = newContent.replace("{official}", "测试");
		}
		newContent = newContent.replace("{clientKey}", clientKey);
		newContent = newContent.replace("{clientCrt}", clientCrt);
		newContent = newContent.replace("{caCrt}", caCrt);
		newContent = newContent.replace("{keystoreFile}", keyStoreFile);
		newContent = newContent.replace("{keyStorePwd}", keyStorePwd);
		newContent = newContent.replace("{operatorKey}", operatorKey);
		newContent = newContent.replace("{signKey}", signKey);
		newContent = newContent.replace("{secretKey}", secretKey);
		newContent = newContent.replace("{dataKey}", dataKey);
		newContent = newContent.replace("{platformId}", platformId);
		newContent = newContent.replace("{accessUrl}", accessUrl);
		List<AccessParam> params = accessParamMgmt.getAccessParamInfoById(operatorInfo.getOperatorID());

		if(params != null && !params.isEmpty() && params.get(0).getVersionNum() != null){
			String version = params.get(0).getVersionNum();
			newContent = newContent.replace("[version]",  version);
		}
		newContent = newContent.replace("{operatorId}", operatorInfo.getOperatorID());
		newContent = newContent.replace("{operatorName}", operatorInfo.getOperatorName());
		
		return newContent;
	}
	
	//===============================private method
	private static void mkEmailTemplate() {
		if(emailTemplate.length() != 0)
			return;
		BufferedInputStream bis = null;
		try {
			StringBuffer sb = new StringBuffer();
			bis = new BufferedInputStream(AccessManageMgmt.class.getResourceAsStream("/emailTemplate/table.html"));
			int len = 0;
			byte[] temp = new byte[1024];
			while ((len = bis.read(temp)) != -1) {
				sb.append(new String(temp, 0, len,"utf-8"));
			}
			emailTemplate = sb.toString();
		} catch (Exception ex) {
			logger.error("make emailTemplate fail",ex);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
		}

	}

}
